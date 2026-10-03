package com.drehverschluss.perilscope.neoforge.client;

import com.drehverschluss.perilscope.client.hud.AreaDifficultyElement;
import com.drehverschluss.perilscope.client.hud.DungeonDifficultyElement;
import com.drehverschluss.perilscope.client.hud.HudAnchor;
import com.drehverschluss.perilscope.client.hud.HudElement;
import com.drehverschluss.perilscope.client.hud.HudElements;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Client config (perilscope-client.toml) with the layout of every HUD element.
 */
public final class PerilscopeClientConfig {
    private static final int MAX_OFFSET = 10000;

    public static final ModConfigSpec SPEC;
    public static final ElementConfig AREA_DIFFICULTY;
    public static final ModConfigSpec.BooleanValue AREA_SHOW_BONUSES;
    public static final ElementConfig DUNGEON_DIFFICULTY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("\"Area Difficulty\" HUD element (Dynamic Difficulty)").push(AreaDifficultyElement.ID);
        AREA_DIFFICULTY = new ElementConfig(builder, AreaDifficultyElement.DEFAULT_ANCHOR,
                AreaDifficultyElement.DEFAULT_OFFSET_X, AreaDifficultyElement.DEFAULT_OFFSET_Y);
        AREA_SHOW_BONUSES = builder
                .comment("Show the structure and biome bonus next to the area level")
                .define("showBonuses", true);
        builder.pop();

        builder.comment("\"Dungeon Difficulty\" HUD element (Dungeon Difficulty), only shown inside a difficulty zone")
                .push(DungeonDifficultyElement.ID);
        DUNGEON_DIFFICULTY = new ElementConfig(builder, DungeonDifficultyElement.DEFAULT_ANCHOR,
                DungeonDifficultyElement.DEFAULT_OFFSET_X, DungeonDifficultyElement.DEFAULT_OFFSET_Y);
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

        ElementConfig(ModConfigSpec.Builder builder, HudAnchor defaultAnchor, int defaultOffsetX, int defaultOffsetY) {
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
        }

        void applyTo(HudElement element) {
            element.setAnchor(anchor.get());
            element.setOffsetX(offsetX.get());
            element.setOffsetY(offsetY.get());
            element.setScale(scale.get().floatValue());
            element.setVisible(visible.get());
        }
    }
}
