package com.drehverschluss.perilscope.neoforge.client;

import com.drehverschluss.perilscope.client.hud.AreaDifficultyElement;
import com.drehverschluss.perilscope.client.hud.DungeonDifficultyElement;
import com.drehverschluss.perilscope.client.hud.HudAnchor;
import com.drehverschluss.perilscope.client.hud.HudElement;
import com.drehverschluss.perilscope.client.hud.HudElements;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * Client config (perilscope-client.toml) with the layout of every HUD element.
 */
public final class PerilscopeClientConfig {
    private static final int MAX_OFFSET = 10000;
    private static final int MAX_COLOR_LEVEL = 10000;

    public static final ModConfigSpec SPEC;
    public static final ElementConfig AREA_DIFFICULTY;
    public static final ModConfigSpec.BooleanValue AREA_SHOW_BONUSES;
    public static final ElementConfig DUNGEON_DIFFICULTY;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> DUNGEON_SAFE_TYPES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("\"Area Difficulty\" HUD element (Dynamic Difficulty)").push(AreaDifficultyElement.ID);
        AREA_DIFFICULTY = new ElementConfig(builder, AreaDifficultyElement.DEFAULT_ANCHOR,
                AreaDifficultyElement.DEFAULT_OFFSET_X, AreaDifficultyElement.DEFAULT_OFFSET_Y,
                AreaDifficultyElement.DEFAULT_COLOR_MAX_LEVEL);
        AREA_SHOW_BONUSES = builder
                .comment("Show the structure and biome bonus next to the area level")
                .define("showBonuses", true);
        builder.pop();

        builder.comment("\"Dungeon Difficulty\" HUD element (Dungeon Difficulty), only shown inside a difficulty zone")
                .push(DungeonDifficultyElement.ID);
        DUNGEON_DIFFICULTY = new ElementConfig(builder, DungeonDifficultyElement.DEFAULT_ANCHOR,
                DungeonDifficultyElement.DEFAULT_OFFSET_X, DungeonDifficultyElement.DEFAULT_OFFSET_Y,
                DungeonDifficultyElement.DEFAULT_COLOR_MAX_LEVEL);
        DUNGEON_SAFE_TYPES = builder
                .comment("Zone types without danger (settlements, ruins ...), shown in green without a level.",
                        "Use the last part of the type translation key, e.g. \"settlement\" for \"difficulty.type.settlement\"")
                .defineListAllowEmpty("safeTypes", List.of("settlement", "ruins"), () -> "settlement",
                        value -> value instanceof String);
        builder.pop();

        SPEC = builder.build();
    }

    private PerilscopeClientConfig() {
    }

    /**
     * Copies the config values into the HUD elements.
     */
    static void apply() {
        AREA_DIFFICULTY.applyTo(HudElements.AREA_DIFFICULTY);
        HudElements.AREA_DIFFICULTY.setShowBonuses(AREA_SHOW_BONUSES.get());
        DUNGEON_DIFFICULTY.applyTo(HudElements.DUNGEON_DIFFICULTY);
        HudElements.DUNGEON_DIFFICULTY.setSafeTypes(DUNGEON_SAFE_TYPES.get());
    }

    /**
     * Writes the current layout of the HUD elements to the config file (used by the layout editor).
     */
    public static void save() {
        AREA_DIFFICULTY.readFrom(HudElements.AREA_DIFFICULTY);
        DUNGEON_DIFFICULTY.readFrom(HudElements.DUNGEON_DIFFICULTY);
        SPEC.save();
    }

    /**
     * Layout values shared by all HUD elements.
     */
    public static final class ElementConfig {
        public final ModConfigSpec.EnumValue<HudAnchor> anchor;
        public final ModConfigSpec.IntValue offsetX;
        public final ModConfigSpec.IntValue offsetY;
        public final ModConfigSpec.DoubleValue scale;
        public final ModConfigSpec.BooleanValue visible;
        public final ModConfigSpec.BooleanValue colorByDifficulty;
        public final ModConfigSpec.IntValue colorMaxLevel;

        ElementConfig(ModConfigSpec.Builder builder, HudAnchor defaultAnchor, int defaultOffsetX, int defaultOffsetY,
                      int defaultColorMaxLevel) {
            anchor = builder
                    .comment("Screen anchor the element is positioned relative to")
                    .defineEnum("anchor", defaultAnchor);
            offsetX = builder
                    .comment("Horizontal offset from the anchor in GUI pixels (positive = right)")
                    .defineInRange("offsetX", defaultOffsetX, -MAX_OFFSET, MAX_OFFSET);
            offsetY = builder
                    .comment("Vertical offset from the anchor in GUI pixels (positive = down)")
                    .defineInRange("offsetY", defaultOffsetY, -MAX_OFFSET, MAX_OFFSET);
            scale = builder
                    .comment("Scale of the element")
                    .defineInRange("scale", 1.0, HudElement.MIN_SCALE, HudElement.MAX_SCALE);
            visible = builder
                    .comment("Whether the element is shown")
                    .define("visible", true);
            colorByDifficulty = builder
                    .comment("Color the text by difficulty level (green = easy, red = hard)")
                    .define("colorByDifficulty", true);
            colorMaxLevel = builder
                    .comment("Level that gets the hardest color, higher levels keep it")
                    .defineInRange("colorMaxLevel", defaultColorMaxLevel, 2, MAX_COLOR_LEVEL);
        }

        void readFrom(HudElement element) {
            anchor.set(element.getAnchor());
            offsetX.set(element.getOffsetX());
            offsetY.set(element.getOffsetY());
            scale.set(Math.round(element.getScale() * 100.0) / 100.0);
            visible.set(element.isVisible());
        }

        void applyTo(HudElement element) {
            element.setAnchor(anchor.get());
            element.setOffsetX(offsetX.get());
            element.setOffsetY(offsetY.get());
            element.setScale(scale.get().floatValue());
            element.setVisible(visible.get());
            element.setColorByDifficulty(colorByDifficulty.get());
            element.setColorMaxLevel(colorMaxLevel.get());
        }
    }
}
