package com.example.demonspell;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DemonFormState {
    private static final Set<UUID> ACTIVE = ConcurrentHashMap.newKeySet();

    public static void set(UUID playerId, boolean active) {
        if (active) ACTIVE.add(playerId);
        else ACTIVE.remove(playerId);
    }

    public static boolean isActive(UUID playerId) {
        return ACTIVE.contains(playerId);
    }

    private DemonFormState() {}
}
