package com.drehverschluss.perilscope.client.hud;

import net.minecraft.util.Mth;

/**
 * Maps a difficulty level to a text color, from mild (first color) to hardest (last color).
 */
public final class DifficultyColors {
    /** Color for places without any danger (e.g. settlements), not part of any palette. */
    public static final int SAFE = 0x55FF55;

    private DifficultyColors() {
    }

    public enum Palette {
        /** Area level: green, yellow, orange, red, magenta. Low levels are harmless. */
        AREA(0x55FF55, 0xFFFF55, 0xFFAA00, 0xFF5555, 0xE04BFF),
        /** Dungeon zones: already the lowest level is dangerous, so no green: yellow, orange, red, magenta. */
        DUNGEON(0xFFFF55, 0xFFAA00, 0xFF5555, 0xE04BFF);

        private final int[] stops;

        Palette(int... stops) {
            this.stops = stops;
        }
    }

    /**
     * @param level    difficulty level, the lowest level (1) gets the first color
     * @param maxLevel level that gets the last color, higher levels keep it
     * @return RGB color
     */
    public static int forLevel(Palette palette, int level, int maxLevel) {
        int[] stops = palette.stops;
        if (maxLevel <= 1) {
            return stops[stops.length - 1];
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
