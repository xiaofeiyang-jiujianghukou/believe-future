package com.believe.common.spi.extension.id;

import com.believe.common.spi.IdGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LeafSegmentIdGenerator implements IdGenerator {

    private final DataSource dataSource;
    private final String bizTag;
    private final int step;

    private final AtomicLong currentId;
    private final AtomicLong maxId;
    private final ReentrantLock loadLock = new ReentrantLock();

    public LeafSegmentIdGenerator(DataSource dataSource, String bizTag, int step) {
        this.dataSource = dataSource;
        this.bizTag = bizTag;
        this.step = step;
        this.currentId = new AtomicLong(0);
        this.maxId = new AtomicLong(0);
        initTable();
        loadSegment();
    }

    private void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS leaf_alloc (
                    biz_tag VARCHAR(128) NOT NULL DEFAULT '',
                    max_id BIGINT NOT NULL DEFAULT 1,
                    step INT NOT NULL DEFAULT 1000,
                    description VARCHAR(256) DEFAULT '',
                    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    PRIMARY KEY (biz_tag)
                )
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            log.warn("Leaf alloc table init skipped: {}", e.getMessage());
        }
        String insertSql = "INSERT IGNORE INTO leaf_alloc (biz_tag, max_id, step, description) VALUES (?, 1, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, bizTag);
            ps.setInt(2, step);
            ps.setString(3, "auto created");
            ps.execute();
        } catch (SQLException e) {
            log.debug("Leaf alloc tag already exists: {}", bizTag);
        }
    }

    private void loadSegment() {
        loadLock.lock();
        try {
            String updateSql = "UPDATE leaf_alloc SET max_id = max_id + step WHERE biz_tag = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, bizTag);
                ps.executeUpdate();
            }
            String selectSql = "SELECT max_id FROM leaf_alloc WHERE biz_tag = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, bizTag);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        long newMaxId = rs.getLong("max_id");
                        maxId.set(newMaxId);
                        currentId.set(newMaxId - step);
                        log.debug("Segment loaded for {}: [{}, {}]", bizTag, currentId.get(), maxId.get());
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to load segment for {}: {}", bizTag, e.getMessage(), e);
            throw new RuntimeException("Failed to load leaf segment", e);
        } finally {
            loadLock.unlock();
        }
    }

    @Override
    public long nextId() {
        long id = currentId.incrementAndGet();
        if (id >= maxId.get()) {
            loadSegment();
            id = currentId.incrementAndGet();
        }
        return id;
    }

    @Override
    public String nextIdStr() {
        return String.valueOf(nextId());
    }
}
