package com.example.firetrail;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Tracks which players are currently in the Dark Spawn form on each side. */
public final class DarkFormState {
    private static final Set<UUID> ACTIVE = ConcurrentHashMap.newKeySet();

    public static void set(UUID playerId, boolean active) {
        if (active) ACTIVE.add(playerId);
        else ACTIVE.remove(playerId);
    }

    public static boolean isActive(UUID playerId) {
        return ACTIVE.contains(playerId);
    }

    private DarkFormState() {}
}
