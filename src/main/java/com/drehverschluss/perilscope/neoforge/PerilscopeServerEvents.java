package com.drehverschluss.perilscope.neoforge;

import com.drehverschluss.perilscope.Perilscope;
import com.drehverschluss.perilscope.compat.DungeonDifficultyCompat;
import com.drehverschluss.perilscope.compat.DynamicDifficultyCompat;
import com.drehverschluss.perilscope.core.AreaDifficultySource;
import com.drehverschluss.perilscope.core.DifficultyTracker;
import com.drehverschluss.perilscope.core.DungeonDifficultySource;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Server side game events driving the {@link DifficultyTracker}.
 */
public final class PerilscopeServerEvents {
    private static final String DYNAMIC_DIFFICULTY_MOD_ID = "dynamic_difficulty";
    private static final String DUNGEON_DIFFICULTY_MOD_ID = "dungeon_difficulty";

    @Nullable
    private static DifficultyTracker tracker;

    private PerilscopeServerEvents() {
    }

    static void register(IEventBus gameEventBus) {
        gameEventBus.addListener(PerilscopeServerEvents::onServerStarting);
        gameEventBus.addListener(PerilscopeServerEvents::onServerStopped);
        gameEventBus.addListener(PerilscopeServerEvents::onServerTick);
        gameEventBus.addListener(PerilscopeServerEvents::onPlayerLoggedIn);
        gameEventBus.addListener(PerilscopeServerEvents::onPlayerLoggedOut);
        gameEventBus.addListener(PerilscopeServerEvents::onPlayerChangedDimension);
        gameEventBus.addListener(PerilscopeServerEvents::onPlayerRespawn);
    }

    private static void onServerStarting(ServerStartingEvent event) {
        // A fresh tracker per server session also re-enables integrations disabled after an error
        tracker = new DifficultyTracker(createAreaSource(), createDungeonSource(), PerilscopeNetworking::sendToPlayer);
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        tracker = null;
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        if (tracker != null) {
            tracker.onServerTick(event.getServer());
        }
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (tracker != null && event.getEntity() instanceof ServerPlayer player) {
            tracker.forceUpdate(player);
        }
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (tracker != null && event.getEntity() instanceof ServerPlayer player) {
            tracker.onLogout(player);
        }
    }

    private static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (tracker != null && event.getEntity() instanceof ServerPlayer player) {
            tracker.forceUpdate(player);
        }
    }

    private static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (tracker != null && event.getEntity() instanceof ServerPlayer player) {
            tracker.forceUpdate(player);
        }
    }

    @Nullable
    private static AreaDifficultySource createAreaSource() {
        // The compat class is only loaded if the mod is present
        if (!ModList.get().isLoaded(DYNAMIC_DIFFICULTY_MOD_ID)) {
            Perilscope.LOGGER.info("Dynamic Difficulty not installed, area difficulty is unavailable");
            return null;
        }
        return new DynamicDifficultyCompat();
    }

    @Nullable
    private static DungeonDifficultySource createDungeonSource() {
        if (!ModList.get().isLoaded(DUNGEON_DIFFICULTY_MOD_ID)) {
            Perilscope.LOGGER.info("Dungeon Difficulty not installed, dungeon difficulty is unavailable");
            return null;
        }
        return new DungeonDifficultyCompat();
    }
}
