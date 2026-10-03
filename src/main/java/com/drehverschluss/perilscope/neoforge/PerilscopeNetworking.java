package com.drehverschluss.perilscope.neoforge;

import com.drehverschluss.perilscope.client.ClientDifficultyState;
import com.drehverschluss.perilscope.core.DifficultyState;
import com.drehverschluss.perilscope.core.DifficultyStatePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Payload registration and sending.
 */
public final class PerilscopeNetworking {
    private static final String PROTOCOL_VERSION = "1";

    private PerilscopeNetworking() {
    }

    static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        // The handler only runs on the client. ClientDifficultyState has no client-only imports,
        // so referencing it here is safe on dedicated servers. Handlers run on the main thread by default.
        registrar.playToClient(DifficultyStatePayload.TYPE, DifficultyStatePayload.STREAM_CODEC,
                (payload, context) -> ClientDifficultyState.set(payload.state()));
    }

    static void sendToPlayer(ServerPlayer player, DifficultyState state) {
        PacketDistributor.sendToPlayer(player, new DifficultyStatePayload(state));
    }
}
