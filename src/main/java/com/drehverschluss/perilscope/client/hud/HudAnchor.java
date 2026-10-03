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
    CENTER(Align.CENTER, Align.CENTER),
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

    /**
     * @return the offset that places the left edge of the element at {@code x}
     */
    public int offsetX(int x, int screenWidth, int elementWidth) {
        return x - horizontal.base(screenWidth, elementWidth);
    }

    /**
     * @return the offset that places the top edge of the element at {@code y}
     */
    public int offsetY(int y, int screenHeight, int elementHeight) {
        return y - vertical.base(screenHeight, elementHeight);
    }

    /**
     * Picks the anchor of the screen cell (3x3 grid) that contains the given point.
     */
    public static HudAnchor nearest(int pointX, int pointY, int screenWidth, int screenHeight) {
        Align horizontal = Align.fromPosition(pointX, screenWidth);
        Align vertical = Align.fromPosition(pointY, screenHeight);
        for (HudAnchor anchor : values()) {
            if (anchor.horizontal == horizontal && anchor.vertical == vertical) {
                return anchor;
            }
        }
        throw new IllegalStateException("No anchor for " + horizontal + "/" + vertical);
    }

    private enum Align {
        START, CENTER, END;

        static Align fromPosition(int position, int screenSize) {
            if (position * 3 < screenSize) {
                return START;
            }
            return position * 3 >= screenSize * 2 ? END : CENTER;
        }

        int base(int screenSize, int elementSize) {
            return switch (this) {
                case START -> 0;
                case CENTER -> (screenSize - elementSize) / 2;
                case END -> screenSize - elementSize;
            };
        }
    }
}
