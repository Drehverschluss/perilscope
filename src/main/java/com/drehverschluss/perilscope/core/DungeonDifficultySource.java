package com.drehverschluss.perilscope.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

/**
 * Server side source of the dungeon difficulty zone (Dungeon Difficulty).
 */
public interface DungeonDifficultySource {
    /**
     * @return the difficulty zone at the given position, or {@code null} if the position is not in a zone
     */
    @Nullable
    Reading read(ServerLevel level, BlockPos pos);

    record Reading(int level, String typeTranslationKey) {
    }
}
