package com.drehverschluss.perilscope.neoforge;

import com.drehverschluss.perilscope.Perilscope;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

/**
 * NeoForge entry point, loaded on both sides. Client-only setup lives in
 * {@link com.drehverschluss.perilscope.neoforge.client.PerilscopeNeoForgeClient}.
 */
@Mod(Perilscope.MOD_ID)
public final class PerilscopeNeoForge {
    public PerilscopeNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(PerilscopeNetworking::registerPayloads);
        PerilscopeServerEvents.register(NeoForge.EVENT_BUS);
    }
}
