package com.drehverschluss.perilscope.client;

import com.drehverschluss.perilscope.core.DifficultyState;

/**
 * Last {@link DifficultyState} received from the server. Only used on the client.
 * <p>
 * Intentionally free of client-only imports, so the payload handler that writes into it can be
 * registered from common code without breaking dedicated servers.
 */
public final class ClientDifficultyState {
    private static volatile DifficultyState current = DifficultyState.EMPTY;

    private ClientDifficultyState() {
    }

    public static DifficultyState get() {
        return current;
    }

    public static void set(DifficultyState state) {
        current = state;
    }

    public static void reset() {
        current = DifficultyState.EMPTY;
    }
}
