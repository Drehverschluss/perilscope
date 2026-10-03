package com.drehverschluss.perilscope.client.hud;

import net.minecraft.util.Mth;

/**
 * Maps a difficulty level to colors, from mild (first color) to hardest (last color).
 * The text uses bright colors because dark colors are hard to read on the dark HUD background,
 * the frame uses the same colors except that the hardest level gets a dark red.
 */
public final class DifficultyColors {
    /** Color for places without any danger (e.g. settlements), not part of any palette. */
    public static final int SAFE = 0x55FF55;

    private static final int HARDEST_TEXT = 0xFF3030;
    private static final int HARDEST_FRAME = 0xAA0000;

    private DifficultyColors() {
    }

    public enum Palette {
        /** Area level: green, yellow, orange, red. Low levels are harmless. */
        AREA(0x55FF55, 0xFFFF55, 0xFFAA00, 0xFF5555),
        /** Dungeon zones: already the lowest level is dangerous, so no green. */
        DUNGEON(0xFFFF55, 0xFFAA00, 0xFF5555);

        private final int[] stops;

        Palette(int... mildStops) {
            // The hardest color is appended per use (text or frame)
            this.stops = mildStops;
        }
    }

    /**
     * @param level    difficulty level, the lowest level (1) gets the first color
     * @param maxLevel level that gets the last color, higher levels keep it
     * @return RGB text color
     */
    public static int textForLevel(Palette palette, int level, int maxLevel) {
        return forLevel(palette, level, maxLevel, HARDEST_TEXT);
    }

    /**
     * @return RGB frame color, same as the text color but dark red for the hardest level
     */
    public static int frameForLevel(Palette palette, int level, int maxLevel) {
        return forLevel(palette, level, maxLevel, HARDEST_FRAME);
    }

    private static int forLevel(Palette palette, int level, int maxLevel, int hardest) {
        int[] mild = palette.stops;
        int[] stops = new int[mild.length + 1];
        System.arraycopy(mild, 0, stops, 0, mild.length);
        stops[mild.length] = hardest;
        if (maxLevel <= 1) {
            return hardest;
        }
        float position = Mth.clamp((level - 1) / (float) (maxLevel - 1), 0.0F, 1.0F) * (stops.length - 1);
        int index = Math.min((int) position, stops.length - 2);
        float fraction = position - index;
        return lerp(stops[index], stops[index + 1], fraction);
    }

    private static int lerp(int from, int to, float fraction) {
        int red = Mth.lerpInt(fraction, from >> 16 & 0xFF, to >> 16 & 0xFF);
        int green = Mth.lerpInt(fraction, from >> 8 & 0xFF, to >> 8 & 0xFF);
        int blue = Mth.lerpInt(fraction, from & 0xFF, to & 0xFF);
        return red << 16 | green << 8 | blue;
    }
}
