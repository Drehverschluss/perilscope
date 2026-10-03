package com.drehverschluss.perilscope.client.hud;

import com.drehverschluss.perilscope.core.DifficultyState;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Shows type and level of the Dungeon Difficulty zone. Only rendered while the player is inside a zone.
 */
public final class DungeonDifficultyElement extends HudElement {
    public static final String ID = "dungeon_difficulty";
    public static final HudAnchor DEFAULT_ANCHOR = HudAnchor.TOP_LEFT;
    public static final int DEFAULT_OFFSET_X = 4;
    /** Directly below the default position of the area element (4 + 15 px height + 2 px gap). */
    public static final int DEFAULT_OFFSET_Y = 21;
    /** Highest level of Dungeon Difficulty's default configuration. */
    public static final int DEFAULT_COLOR_MAX_LEVEL = 6;

    private Set<String> safeTypes = Set.of();

    public DungeonDifficultyElement() {
        super(ID, DEFAULT_ANCHOR, DEFAULT_OFFSET_X, DEFAULT_OFFSET_Y, DifficultyColors.Palette.DUNGEON, DEFAULT_COLOR_MAX_LEVEL);
    }

    @Override
    protected List<Component> getLines(DifficultyState state) {
        if (!state.inDungeon()) {
            return List.of();
        }
        if (isSafe(state.dungeonTypeKey())) {
            return List.of(coloredSafe(typeComponent(state, false).copy()));
        }
        return List.of(colored(typeComponent(state, true).copy(), state.dungeonLevel()));
    }

    @Override
    protected List<Component> getPreviewLines() {
        return List.of(colored(Component.translatable("hud.perilscope.dungeon.unknown_type", 5), 5));
    }

    /**
     * @param types type names (the last part of the translation key, e.g. "settlement") of zones without danger
     */
    public void setSafeTypes(Collection<? extends String> types) {
        Set<String> normalized = new HashSet<>();
        for (String type : types) {
            normalized.add(type.trim().toLowerCase(Locale.ROOT));
        }
        safeTypes = normalized;
    }

    private boolean isSafe(String typeKey) {
        return !typeKey.isEmpty() && safeTypes.contains(typeName(typeKey).toLowerCase(Locale.ROOT));
    }

    private static Component typeComponent(DifficultyState state, boolean withLevel) {
        String key = state.dungeonTypeKey();
        if (key.isEmpty()) {
            return Component.translatable("hud.perilscope.dungeon.unknown_type", state.dungeonLevel());
        }
        // Dungeon Difficulty's own translations take the level as argument ("Dungeon %s").
        // The fallback is used if the client has no translation for the key.
        String fallback = withLevel ? fallbackName(key) + " %s" : fallbackName(key);
        return Component.translatableWithFallback(key, fallback, state.dungeonLevel());
    }

    private static String typeName(String key) {
        return key.substring(key.lastIndexOf('.') + 1);
    }
    /**
     * "difficulty.type.boss_dungeon" -> "Boss Dungeon"
     */
    private static String fallbackName(String key) {
        String name = typeName(key);
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
