package com.believe.common.spi.extension.id;

import com.believe.common.spi.IdGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnowflakeIdGenerator implements IdGenerator {

    private final long workerId;
    private final long datacenterId;
    private final long epoch;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        this(workerId, datacenterId, 1700000000000L);
    }

    public SnowflakeIdGenerator(long workerId, long datacenterId, long epoch) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("workerId must be 0.." + maxWorkerId);
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId must be 0.." + maxDatacenterId);
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.epoch = epoch;
        log.info("SnowflakeIdGenerator initialized: workerId={}, datacenterId={}", workerId, datacenterId);
    }

    @Override
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards, refusing to generate id for " + (lastTimestamp - timestamp) + "ms");
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - epoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    @Override
    public String nextIdStr() {
        return String.valueOf(nextId());
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
