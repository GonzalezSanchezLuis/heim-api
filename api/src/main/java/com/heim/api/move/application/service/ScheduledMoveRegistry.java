package com.heim.api.move.application.service;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScheduledMoveRegistry {

    private final Set<Long> pendingMoves = ConcurrentHashMap.newKeySet();

    public void register(Long moveId) {
        pendingMoves.add(moveId);
    }

    public void unregister(Long moveId) {
        pendingMoves.remove(moveId);
    }

    public boolean hasPending() {
        return !pendingMoves.isEmpty();
    }
}
