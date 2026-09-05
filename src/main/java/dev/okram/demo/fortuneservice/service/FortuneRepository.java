package dev.okram.demo.fortuneservice.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Repository
public class FortuneRepository {
    private static final String UPSERT = """
            INSERT INTO fortunes (content_hash, message, times_seen, first_seen_at, last_seen_at)
            VALUES (?, ?, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT (content_hash) DO UPDATE SET
                times_seen = fortunes.times_seen + 1,
                last_seen_at = CURRENT_TIMESTAMP
            RETURNING times_seen
            """;

    private final JdbcTemplate jdbcTemplate;

    public FortuneRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long recordOccurrence(String message) {
        String hash = sha256(message);
        Long timesSeen = jdbcTemplate.queryForObject(
                UPSERT,
                Long.class,
                hash,
                message);
        if (timesSeen == null) {
            throw new IllegalStateException("Database did not return the fortune occurrence count");
        }
        return timesSeen;
    }

    private String sha256(String message) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                hash.append(String.format("%02x", value));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
