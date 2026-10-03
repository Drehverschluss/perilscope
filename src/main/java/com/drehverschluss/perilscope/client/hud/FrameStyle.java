package com.drehverschluss.perilscope.client.hud;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Frame drawn around a HUD element, inspired by vanilla GUI elements. All frames are 1 to 2 pixels thick
 * (the text padding is 3 pixels) and are tinted with the given color.
 */
public enum FrameStyle {
    /** No frame. */
    NONE,
    /** Flat 1 px outline. */
    SIMPLE,
    /** Like a vanilla tooltip: rounded corners, the color fades from top to bottom. */
    TOOLTIP,
    /** Like a vanilla button or panel: dark outline with a light top left and a dark bottom right edge. */
    BEVEL,
    /** Like an advancement frame: dark outer line, colored inner line and light corner studs. */
    ORNATE,
    /** Pixel art panel: dark steel frame with the text in an inset field. Opaque, needs more space around the text. */
    PANEL;

    /**
     * @return pixels added around the text on every side, on top of the regular padding of the element
     */
    public int extraPadding() {
        return this == PANEL ? 4 : 0;
    }

    /**
     * Draws the frame inside the rectangle {@code (0, 0, width, height)}.
     *
     * @param rgb   frame color
     * @param alpha 0 - 255
     */
    public void draw(GuiGraphics graphics, int width, int height, int rgb, int alpha) {
        switch (this) {
            case NONE -> {
            }
            case SIMPLE -> graphics.renderOutline(0, 0, width, height, argb(rgb, alpha));
            case TOOLTIP -> drawTooltip(graphics, width, height, rgb, alpha);
            case BEVEL -> drawBevel(graphics, width, height, rgb, alpha);
            case ORNATE -> drawOrnate(graphics, width, height, rgb, alpha);
            case PANEL -> drawPanel(graphics, width, height, rgb, alpha);
        }
    }

    private static void drawTooltip(GuiGraphics graphics, int width, int height, int rgb, int alpha) {
        int top = argb(rgb, alpha);
        int bottom = argb(shade(rgb, 0.45F), alpha);
        graphics.fillGradient(0, 1, 1, height - 1, top, bottom);
        graphics.fillGradient(width - 1, 1, width, height - 1, top, bottom);
        graphics.fill(1, 0, width - 1, 1, top);
        graphics.fill(1, height - 1, width - 1, height, bottom);
    }

    private static void drawBevel(GuiGraphics graphics, int width, int height, int rgb, int alpha) {
        int outline = argb(0x101010, alpha);
        int light = argb(mixWithWhite(rgb, 0.4F), alpha);
        int dark = argb(shade(rgb, 0.5F), alpha);
        graphics.renderOutline(0, 0, width, height, outline);
        graphics.fill(1, 1, width - 1, 2, light);
        graphics.fill(1, 2, 2, height - 1, light);
        graphics.fill(2, height - 2, width - 1, height - 1, dark);
        graphics.fill(width - 2, 2, width - 1, height - 2, dark);
    }

    private static void drawOrnate(GuiGraphics graphics, int width, int height, int rgb, int alpha) {
        graphics.renderOutline(0, 0, width, height, argb(shade(rgb, 0.4F), alpha));
        graphics.renderOutline(1, 1, width - 2, height - 2, argb(rgb, alpha));
        int stud = argb(mixWithWhite(rgb, 0.6F), alpha);
        graphics.fill(1, 1, 2, 2, stud);
        graphics.fill(width - 2, 1, width - 1, 2, stud);
        graphics.fill(1, height - 2, 2, height - 1, stud);
        graphics.fill(width - 2, height - 2, width - 1, height - 1, stud);
    }

    private static void drawPanel(GuiGraphics graphics, int width, int height, int rgb, int alpha) {
        int outline = argb(0x0F1520, alpha);
        int highlight = argb(mix(0x56737B, rgb, 0.3F), alpha);
        int body = argb(0x27363E, alpha);
        int wellDark = argb(0x131A21, alpha);
        int wellLight = argb(mix(0x3E4F58, rgb, 0.2F), alpha);
        int wellFill = argb(0x1F2B34, alpha);
        // Outline with cut corners, then the frame body and the light top edge
        graphics.fill(1, 0, width - 1, height, outline);
        graphics.fill(0, 1, width, height - 1, outline);
        graphics.fill(1, 1, width - 1, height - 1, body);
        graphics.fill(2, 1, width - 2, 2, highlight);
        // Inset field: dark top left edge, light bottom right edge
        graphics.fill(3, 3, width - 3, height - 3, wellLight);
        graphics.fill(3, 3, width - 4, height - 4, wellDark);
        graphics.fill(4, 4, width - 4, height - 4, wellFill);
    }

    private static int mix(int from, int to, float amount) {
        int red = (int) ((from >> 16 & 0xFF) * (1 - amount) + (to >> 16 & 0xFF) * amount);
        int green = (int) ((from >> 8 & 0xFF) * (1 - amount) + (to >> 8 & 0xFF) * amount);
        int blue = (int) ((from & 0xFF) * (1 - amount) + (to & 0xFF) * amount);
        return red << 16 | green << 8 | blue;
    }

    private static int argb(int rgb, int alpha) {
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    private static int shade(int rgb, float factor) {
        int red = (int) ((rgb >> 16 & 0xFF) * factor);
        int green = (int) ((rgb >> 8 & 0xFF) * factor);
        int blue = (int) ((rgb & 0xFF) * factor);
        return red << 16 | green << 8 | blue;
    }

    private static int mixWithWhite(int rgb, float amount) {
        int red = (rgb >> 16 & 0xFF) + (int) ((255 - (rgb >> 16 & 0xFF)) * amount);
        int green = (rgb >> 8 & 0xFF) + (int) ((255 - (rgb >> 8 & 0xFF)) * amount);
        int blue = (rgb & 0xFF) + (int) ((255 - (rgb & 0xFF)) * amount);
        return red << 16 | green << 8 | blue;
    }
}
