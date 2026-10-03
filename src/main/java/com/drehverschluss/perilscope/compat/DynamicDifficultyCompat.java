package com.drehverschluss.perilscope.compat;

import com.drehverschluss.perilscope.Perilscope;
import com.drehverschluss.perilscope.core.AreaDifficultySource;
import dev.muon.dynamic_difficulty.api.LevelingAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

/**
 * Reads the area difficulty from Dynamic Difficulty through its public {@link LevelingAPI}.
 * <p>
 * This class references Dynamic Difficulty classes and must only be instantiated if the mod
 * {@code dynamic_difficulty} is loaded. Any failure disables the query for the rest of the session.
 */
public final class DynamicDifficultyCompat implements AreaDifficultySource {
    private boolean enabled = true;

    @Override
    @Nullable
    public Reading read(ServerLevel level, BlockPos pos) {
        if (!enabled) {
            return null;
        }
        try {
            int areaLevel = LevelingAPI.getLevelAt(level, pos);
            var structure = LevelingAPI.getStructureBonus(level, pos);
            var biome = LevelingAPI.getBiomeBonus(level, pos);
            int structureBonus = structure != null && structure.hasStructure() ? structure.totalBonus() : 0;
            int biomeBonus = biome != null ? biome.totalBonus() : 0;
            return new Reading(areaLevel, structureBonus, biomeBonus);
        } catch (Throwable t) {
            enabled = false;
            Perilscope.LOGGER.error("Querying Dynamic Difficulty failed, the area difficulty is disabled for this session", t);
            return null;
        }
    }
}
