package com.drehverschluss.perilscope.client.hud;

import com.drehverschluss.perilscope.client.ClientDifficultyState;
import com.drehverschluss.perilscope.core.DifficultyState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

/**
 * GUI layer rendering all {@link HudElements}.
 */
public final class PerilscopeHudLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui || minecraft.getDebugOverlay().showDebugScreen()) {
            return;
        }
        DifficultyState state = ClientDifficultyState.get();
        for (HudElement element : HudElements.ALL) {
            element.render(graphics, minecraft.font, state);
        }
    }
}
