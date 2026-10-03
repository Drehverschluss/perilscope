package com.drehverschluss.perilscope.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Periodically computes the {@link DifficultyState} of every player and sends it to the client
 * whenever it changed. One instance lives for one server session.
 */
public final class DifficultyTracker {
    public static final int UPDATE_INTERVAL_TICKS = 10;

    @Nullable
    private final AreaDifficultySource areaSource;
    @Nullable
    private final DungeonDifficultySource dungeonSource;
    private final BiConsumer<ServerPlayer, DifficultyState> sender;
    private final Map<UUID, DifficultyState> lastSent = new HashMap<>();
    private int tickCounter;

    /**
     * @param areaSource    source for the area difficulty, {@code null} if Dynamic Difficulty is not installed
     * @param dungeonSource source for the dungeon difficulty, {@code null} if Dungeon Difficulty is not installed
     * @param sender        sends a state to a player
     */
    public DifficultyTracker(@Nullable AreaDifficultySource areaSource,
                             @Nullable DungeonDifficultySource dungeonSource,
                             BiConsumer<ServerPlayer, DifficultyState> sender) {
        this.areaSource = areaSource;
        this.dungeonSource = dungeonSource;
        this.sender = sender;
    }

    /**
     * Called once per server tick (after the tick). Updates all players every {@link #UPDATE_INTERVAL_TICKS} ticks.
     */
    public void onServerTick(MinecraftServer server) {
        if (++tickCounter < UPDATE_INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            update(player, false);
        }
    }

    /**
     * Sends the current state unconditionally (login, dimension change).
     */
    public void forceUpdate(ServerPlayer player) {
        update(player, true);
    }

    public void onLogout(ServerPlayer player) {
        lastSent.remove(player.getUUID());
    }

    private void update(ServerPlayer player, boolean force) {
        DifficultyState state = compute(player.serverLevel(), player.blockPosition());
        DifficultyState previous = lastSent.put(player.getUUID(), state);
        if (force || !state.equals(previous)) {
            sender.accept(player, state);
        }
    }

    private DifficultyState compute(ServerLevel level, BlockPos pos) {
        int areaLevel = DifficultyState.UNAVAILABLE;
        int structureBonus = 0;
        int biomeBonus = 0;
        if (areaSource != null) {
            AreaDifficultySource.Reading area = areaSource.read(level, pos);
            if (area != null) {
                areaLevel = area.level();
                structureBonus = area.structureBonus();
                biomeBonus = area.biomeBonus();
            }
        }

        boolean inDungeon = false;
        int dungeonLevel = 0;
        String dungeonTypeKey = "";
        if (dungeonSource != null) {
            DungeonDifficultySource.Reading dungeon = dungeonSource.read(level, pos);
            if (dungeon != null) {
                inDungeon = true;
                dungeonLevel = dungeon.level();
                dungeonTypeKey = dungeon.typeTranslationKey();
            }
        }

        return new DifficultyState(areaLevel, structureBonus, biomeBonus, inDungeon, dungeonLevel, dungeonTypeKey);
    }
}
