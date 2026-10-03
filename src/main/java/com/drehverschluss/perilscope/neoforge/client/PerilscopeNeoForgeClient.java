package com.drehverschluss.perilscope.neoforge.client;

import com.drehverschluss.perilscope.Perilscope;
import com.drehverschluss.perilscope.client.ClientDifficultyState;
import com.drehverschluss.perilscope.client.hud.PerilscopeHudLayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

/**
 * NeoForge client entry point. Not loaded on dedicated servers.
 */
@Mod(value = Perilscope.MOD_ID, dist = Dist.CLIENT)
public final class PerilscopeNeoForgeClient {
    public PerilscopeNeoForgeClient(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, PerilscopeClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(PerilscopeNeoForgeClient::onRegisterGuiLayers);
        modEventBus.addListener(PerilscopeNeoForgeClient::onConfigLoading);
        modEventBus.addListener(PerilscopeNeoForgeClient::onConfigReloading);
        NeoForge.EVENT_BUS.addListener(PerilscopeNeoForgeClient::onLoggingOut);
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Perilscope.id("hud"), new PerilscopeHudLayer());
    }

    private static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == PerilscopeClientConfig.SPEC) {
            PerilscopeClientConfig.apply();
        }
    }

    private static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == PerilscopeClientConfig.SPEC) {
            PerilscopeClientConfig.apply();
        }
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientDifficultyState.reset();
    }
}
