package com.drehverschluss.perilscope.client.hud;

import java.util.List;

/**
 * All HUD elements of the mod.
 */
public final class HudElements {
    public static final AreaDifficultyElement AREA_DIFFICULTY = new AreaDifficultyElement();
    public static final DungeonDifficultyElement DUNGEON_DIFFICULTY = new DungeonDifficultyElement();
    public static final List<HudElement> ALL = List.of(AREA_DIFFICULTY, DUNGEON_DIFFICULTY);

    private HudElements() {
    }
}
