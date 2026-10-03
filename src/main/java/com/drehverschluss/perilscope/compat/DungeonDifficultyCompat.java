package com.drehverschluss.perilscope.compat;

import com.drehverschluss.perilscope.Perilscope;
import com.drehverschluss.perilscope.core.DungeonDifficultySource;
import net.dungeon_difficulty.logic.Difficulty;
import net.dungeon_difficulty.logic.PatternMatching;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

/**
 * Reads the difficulty zone from Dungeon Difficulty.
 * <p>
 * Dungeon Difficulty has no public API, so this uses its internal classes
 * ({@link PatternMatching}, {@link Difficulty}) which may change with any update.
 * This class must only be instantiated if the mod {@code dungeon_difficulty} is loaded.
 * Any failure disables the query for the rest of the session.
 */
public final class DungeonDifficultyCompat implements DungeonDifficultySource {
    private boolean enabled = true;

    @Override
    @Nullable
    public Reading read(ServerLevel level, BlockPos pos) {
        if (!enabled) {
            return null;
        }
        try {
            Difficulty difficulty = PatternMatching.getDifficulty(PatternMatching.LocationData.create(level, pos), level);
            if (difficulty == null || !difficulty.isValid()) {
                return null;
            }
            return new Reading(difficulty.level(), difficulty.typeTranslationKey());
        } catch (Throwable t) {
            enabled = false;
            Perilscope.LOGGER.error("Querying Dungeon Difficulty failed, the dungeon difficulty is disabled for this session", t);
            return null;
        }
    }
}
