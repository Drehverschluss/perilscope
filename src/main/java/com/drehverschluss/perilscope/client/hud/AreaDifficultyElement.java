package com.drehverschluss.perilscope.client.hud;

import com.drehverschluss.perilscope.core.DifficultyState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

/**
 * Shows the Dynamic Difficulty area level at the player position, optionally with structure and biome bonus.
 */
public final class AreaDifficultyElement extends HudElement {
    public static final String ID = "area_difficulty";
    public static final HudAnchor DEFAULT_ANCHOR = HudAnchor.TOP_LEFT;
    public static final int DEFAULT_OFFSET_X = 4;
    public static final int DEFAULT_OFFSET_Y = 4;
    /** Dynamic Difficulty's default level cap. */
    public static final int DEFAULT_COLOR_MAX_LEVEL = 80;

    private boolean showBonuses = true;

    public AreaDifficultyElement() {
        super(ID, DEFAULT_ANCHOR, DEFAULT_OFFSET_X, DEFAULT_OFFSET_Y, DifficultyColors.Palette.AREA, DEFAULT_COLOR_MAX_LEVEL);
    }

    @Override
    protected List<Component> getLines(DifficultyState state) {
        if (!state.hasAreaLevel()) {
            return List.of();
        }
        return List.of(buildLine(state.areaLevel(), state.structureBonus(), state.biomeBonus()));
    }

    @Override
    protected List<Component> getPreviewLines() {
        return List.of(buildLine(12, 3, -1));
    }

    private MutableComponent buildLine(int areaLevel, int structureBonus, int biomeBonus) {
        // Only the level is colored, the bonuses keep the default text color
        MutableComponent line = Component.empty().append(colored(Component.translatable("hud.perilscope.area.level", areaLevel), areaLevel));
        if (showBonuses) {
            if (structureBonus != 0) {
                line.append(" ").append(Component.translatable("hud.perilscope.area.structure_bonus", signed(structureBonus)));
            }
            if (biomeBonus != 0) {
                line.append(" ").append(Component.translatable("hud.perilscope.area.biome_bonus", signed(biomeBonus)));
            }
        }
        return line;
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : Integer.toString(value);
    }

    public boolean isShowBonuses() {
        return showBonuses;
    }

    public void setShowBonuses(boolean showBonuses) {
        this.showBonuses = showBonuses;
    }
}
