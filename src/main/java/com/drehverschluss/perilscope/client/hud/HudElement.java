package com.drehverschluss.perilscope.client.hud;

import com.drehverschluss.perilscope.core.DifficultyState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

/**
 * A freely positionable HUD element: anchor + offset (GUI pixels) + scale.
 * Phase 1 renders plain text lines on a semi-transparent background.
 */
public abstract class HudElement {
    public static final float MIN_SCALE = 0.25F;
    public static final float MAX_SCALE = 4.0F;

    private static final int PADDING = 3;
    private static final int LINE_SPACING = 1;
    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private final String id;
    private HudAnchor anchor;
    private int offsetX;
    private int offsetY;
    private float scale = 1.0F;
    private boolean visible = true;

    protected HudElement(String id, HudAnchor anchor, int offsetX, int offsetY) {
        this.id = id;
        this.anchor = anchor;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * @return the text lines to show for the given state, empty to render nothing
     */
    protected abstract List<Component> getLines(DifficultyState state);

    public void render(GuiGraphics graphics, Font font, DifficultyState state) {
        if (!visible) {
            return;
        }
        List<Component> lines = getLines(state);
        if (lines.isEmpty()) {
            return;
        }

        int textWidth = 0;
        for (Component line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }
        int lineHeight = font.lineHeight + LINE_SPACING;
        int width = textWidth + PADDING * 2;
        int height = lines.size() * lineHeight - LINE_SPACING + PADDING * 2;

        int scaledWidth = Mth.ceil(width * scale);
        int scaledHeight = Mth.ceil(height * scale);
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        // Keep the element on screen even if the window got smaller than the configured offset
        int x = Mth.clamp(anchor.resolveX(screenWidth, scaledWidth, offsetX), 0, Math.max(0, screenWidth - scaledWidth));
        int y = Mth.clamp(anchor.resolveY(screenHeight, scaledHeight, offsetY), 0, Math.max(0, screenHeight - scaledHeight));

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.fill(0, 0, width, height, BACKGROUND_COLOR);
        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(font, lines.get(i), PADDING, PADDING + i * lineHeight, TEXT_COLOR, true);
        }
        graphics.pose().popPose();
    }

    public String getId() {
        return id;
    }

    public HudAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(HudAnchor anchor) {
        this.anchor = anchor;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = Mth.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
