package com.drehverschluss.perilscope.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

/**
 * Server side source of the area difficulty (Dynamic Difficulty).
 */
public interface AreaDifficultySource {
    /**
     * @return the area difficulty at the given position, or {@code null} if it is not available
     */
    @Nullable
    Reading read(ServerLevel level, BlockPos pos);

    record Reading(int level, int structureBonus, int biomeBonus) {
    }
}
