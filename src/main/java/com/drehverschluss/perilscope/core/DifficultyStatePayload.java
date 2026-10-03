package com.drehverschluss.perilscope.core;

import com.drehverschluss.perilscope.Perilscope;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Server to client packet carrying the current {@link DifficultyState} of the receiving player.
 */
public record DifficultyStatePayload(DifficultyState state) implements CustomPacketPayload {
    public static final Type<DifficultyStatePayload> TYPE = new Type<>(Perilscope.id("difficulty_state"));

    public static final StreamCodec<ByteBuf, DifficultyState> STATE_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DifficultyState::areaLevel,
            ByteBufCodecs.VAR_INT, DifficultyState::structureBonus,
            ByteBufCodecs.VAR_INT, DifficultyState::biomeBonus,
            ByteBufCodecs.BOOL, DifficultyState::inDungeon,
            ByteBufCodecs.VAR_INT, DifficultyState::dungeonLevel,
            ByteBufCodecs.stringUtf8(DifficultyState.MAX_TYPE_KEY_LENGTH), DifficultyState::dungeonTypeKey,
            DifficultyState::new);

    public static final StreamCodec<ByteBuf, DifficultyStatePayload> STREAM_CODEC =
            STATE_CODEC.map(DifficultyStatePayload::new, DifficultyStatePayload::state);

    @Override
    public Type<DifficultyStatePayload> type() {
        return TYPE;
    }
}
