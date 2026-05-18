package com.believe.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class RedisDistributedLock {

    private final StringRedisTemplate redisTemplate;
    private final ThreadLocal<Map<String, String>> lockTokens = ThreadLocal.withInitial(HashMap::new);

    private static final String UNLOCK_LUA =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>(UNLOCK_LUA, Long.class);
    }

    public RedisDistributedLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryLock(String key, Duration timeout, Duration ttl) {
        String requestId = UUID.randomUUID().toString();
        long deadline = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < deadline) {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(key, requestId, ttl);
            if (Boolean.TRUE.equals(success)) {
                lockTokens.get().put(key, requestId);
                return true;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    public void unlock(String key) {
        String requestId = lockTokens.get().remove(key);
        if (requestId == null) {
            return;
        }
        redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(key), requestId);
        if (lockTokens.get().isEmpty()) {
            lockTokens.remove();
        }
    }

    public <T> T execute(String key, Duration timeout, Duration ttl, Supplier<T> action) {
        if (!tryLock(key, timeout, ttl)) {
            throw new RuntimeException("获取分布式锁失败: " + key);
        }
        try {
            return action.get();
        } finally {
            unlock(key);
        }
    }
}
