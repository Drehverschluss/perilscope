package com.drehverschluss.perilscope.core;

/**
 * Difficulty information for a single player position, computed on the server and synced to the client.
 *
 * @param areaLevel      area level from Dynamic Difficulty, {@link #UNAVAILABLE} if not available
 * @param structureBonus structure bonus from Dynamic Difficulty (0 if none)
 * @param biomeBonus     biome bonus from Dynamic Difficulty (0 if none)
 * @param inDungeon      whether the player stands in a Dungeon Difficulty zone
 * @param dungeonLevel   level of the Dungeon Difficulty zone (only meaningful if {@code inDungeon})
 * @param dungeonTypeKey translation key of the Dungeon Difficulty type (empty if not in a zone)
 */
public record DifficultyState(int areaLevel, int structureBonus, int biomeBonus,
                              boolean inDungeon, int dungeonLevel, String dungeonTypeKey) {
    public static final int UNAVAILABLE = -1;
    public static final int MAX_TYPE_KEY_LENGTH = 256;
    public static final DifficultyState EMPTY = new DifficultyState(UNAVAILABLE, 0, 0, false, 0, "");

    public DifficultyState {
        if (dungeonTypeKey == null || dungeonTypeKey.length() > MAX_TYPE_KEY_LENGTH) {
            dungeonTypeKey = "";
        }
    }

    public boolean hasAreaLevel() {
        return areaLevel != UNAVAILABLE;
    }
}
