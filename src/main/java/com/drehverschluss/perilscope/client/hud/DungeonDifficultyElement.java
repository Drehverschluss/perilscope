package com.drehverschluss.perilscope.client.hud;

import com.drehverschluss.perilscope.core.DifficultyState;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

/**
 * Shows type and level of the Dungeon Difficulty zone. Only rendered while the player is inside a zone.
 */
public final class DungeonDifficultyElement extends HudElement {
    public static final String ID = "dungeon_difficulty";
    public static final HudAnchor DEFAULT_ANCHOR = HudAnchor.TOP_LEFT;
    public static final int DEFAULT_OFFSET_X = 4;
    /** Directly below the default position of the area element (4 + 15 px height + 2 px gap). */
    public static final int DEFAULT_OFFSET_Y = 21;

    public DungeonDifficultyElement() {
        super(ID, DEFAULT_ANCHOR, DEFAULT_OFFSET_X, DEFAULT_OFFSET_Y);
    }

    @Override
    protected List<Component> getLines(DifficultyState state) {
        if (!state.inDungeon()) {
            return List.of();
        }
        return List.of(Component.translatable("hud.perilscope.dungeon.zone", typeComponent(state)));
    }

    private static Component typeComponent(DifficultyState state) {
        String key = state.dungeonTypeKey();
        if (key.isEmpty()) {
            return Component.translatable("hud.perilscope.dungeon.unknown_type", state.dungeonLevel());
        }
        // Dungeon Difficulty's own translations take the level as argument ("Dungeon %s").
        // The fallback is used if the client has no translation for the key.
        return Component.translatableWithFallback(key, fallbackName(key) + " %s", state.dungeonLevel());
    }

    /**
     * "difficulty.type.boss_dungeon" -> "Boss Dungeon"
     */
    private static String fallbackName(String key) {
        String name = key.substring(key.lastIndexOf('.') + 1);
        StringBuilder builder = new StringBuilder();
        for (String word : name.split("_")) {
            if (word.isEmpty()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
        }
        return builder.toString().replace("%", "%%");
    }
}
