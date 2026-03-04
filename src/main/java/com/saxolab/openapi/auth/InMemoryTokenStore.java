package com.saxolab.openapi.auth;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryTokenStore implements SaxoOAuthTokenStore {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;

    @Override
    public void storeTokens(String accessToken, String refreshToken, Instant expiresAt) {
        lock.writeLock().lock();
        try {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresAt = expiresAt;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String getAccessToken() {
        lock.readLock().lock();
        try {
            return accessToken;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String getRefreshToken() {
        lock.readLock().lock();
        try {
            return refreshToken;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Instant getExpiresAt() {
        lock.readLock().lock();
        try {
            return expiresAt;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean hasTokens() {
        lock.readLock().lock();
        try {
            return accessToken != null && refreshToken != null;
        } finally {
            lock.readLock().unlock();
        }
    }
}
