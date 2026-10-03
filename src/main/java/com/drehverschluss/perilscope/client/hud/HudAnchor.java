package com.drehverschluss.perilscope.client.hud;

/**
 * Screen anchor of a HUD element. The final position is anchor point + offset, so the element
 * keeps its relative position for every window size and GUI scale.
 */
public enum HudAnchor {
    TOP_LEFT(Align.START, Align.START),
    TOP_CENTER(Align.CENTER, Align.START),
    TOP_RIGHT(Align.END, Align.START),
    CENTER_LEFT(Align.START, Align.CENTER),
    CENTER_RIGHT(Align.END, Align.CENTER),
    BOTTOM_LEFT(Align.START, Align.END),
    BOTTOM_CENTER(Align.CENTER, Align.END),
    BOTTOM_RIGHT(Align.END, Align.END);

    private final Align horizontal;
    private final Align vertical;

    HudAnchor(Align horizontal, Align vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    /**
     * @param screenWidth  GUI width in GUI pixels
     * @param elementWidth element width in GUI pixels (already scaled)
     * @param offsetX      offset in GUI pixels, positive values move right
     * @return x position of the left edge of the element
     */
    public int resolveX(int screenWidth, int elementWidth, int offsetX) {
        return horizontal.base(screenWidth, elementWidth) + offsetX;
    }

    /**
     * @param screenHeight  GUI height in GUI pixels
     * @param elementHeight element height in GUI pixels (already scaled)
     * @param offsetY       offset in GUI pixels, positive values move down
     * @return y position of the top edge of the element
     */
    public int resolveY(int screenHeight, int elementHeight, int offsetY) {
        return vertical.base(screenHeight, elementHeight) + offsetY;
    }

    private enum Align {
        START, CENTER, END;

        int base(int screenSize, int elementSize) {
            return switch (this) {
                case START -> 0;
                case CENTER -> (screenSize - elementSize) / 2;
                case END -> screenSize - elementSize;
            };
        }
    }
}
