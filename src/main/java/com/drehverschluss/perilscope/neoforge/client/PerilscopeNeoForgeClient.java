package com.drehverschluss.perilscope.neoforge.client;

import com.drehverschluss.perilscope.Perilscope;
import com.drehverschluss.perilscope.client.ClientDifficultyState;
import com.drehverschluss.perilscope.client.hud.PerilscopeHudLayer;
import com.drehverschluss.perilscope.client.screen.HudEditScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

/**
 * NeoForge client entry point. Not loaded on dedicated servers.
 */
@Mod(value = Perilscope.MOD_ID, dist = Dist.CLIENT)
public final class PerilscopeNeoForgeClient {
    private static final KeyMapping EDIT_HUD_KEY = new KeyMapping("key.perilscope.edit_hud",
            InputConstants.UNKNOWN.getValue(), "key.categories.perilscope");

    public PerilscopeNeoForgeClient(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, PerilscopeClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, PerilscopeConfigMenuScreen::new);

        modEventBus.addListener(PerilscopeNeoForgeClient::onRegisterGuiLayers);
        modEventBus.addListener(PerilscopeNeoForgeClient::onRegisterKeyMappings);
        modEventBus.addListener(PerilscopeNeoForgeClient::onConfigLoading);
        modEventBus.addListener(PerilscopeNeoForgeClient::onConfigReloading);
        NeoForge.EVENT_BUS.addListener(PerilscopeNeoForgeClient::onLoggingOut);
        NeoForge.EVENT_BUS.addListener(PerilscopeNeoForgeClient::onClientTick);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(EDIT_HUD_KEY);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        while (EDIT_HUD_KEY.consumeClick()) {
            if (minecraft.screen == null) {
                minecraft.setScreen(new HudEditScreen(null, PerilscopeClientConfig::save));
            }
        }
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
