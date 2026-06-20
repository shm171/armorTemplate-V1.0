package com.codex.armortemplatejson.consumption;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public record ConsumptionState(ResourceLocation type, long remaining, long capacity, long consumeRate) {
    private static final Codec<Long> REMAINING_CODEC = Codec.LONG.flatXmap(ConsumptionState::validateRemaining, DataResult::success);
    private static final Codec<Long> CAPACITY_CODEC = Codec.LONG.flatXmap(ConsumptionState::validateCapacity, DataResult::success);
    private static final Codec<Long> CONSUME_RATE_CODEC = Codec.LONG.flatXmap(ConsumptionState::validateConsumeRate, DataResult::success);

    private static final Codec<ConsumptionState> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(ConsumptionState::type),
            REMAINING_CODEC.fieldOf("remaining").forGetter(ConsumptionState::remaining),
            CAPACITY_CODEC.optionalFieldOf("capacity", Long.MAX_VALUE).forGetter(ConsumptionState::capacity),
            CONSUME_RATE_CODEC.optionalFieldOf("consume_rate", 0L).forGetter(ConsumptionState::consumeRate)
    ).apply(instance, ConsumptionState::new));

    public static final Codec<ConsumptionState> CODEC =
            BASE_CODEC.flatXmap(ConsumptionState::validateState, DataResult::success);

    public ConsumptionState {
        Objects.requireNonNull(type, "type");
        if (remaining < 0) {
            throw new IllegalArgumentException("remaining must be greater than or equal to 0");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be greater than or equal to 0");
        }
        if (consumeRate < 0) {
            throw new IllegalArgumentException("consume_rate must be greater than or equal to 0");
        }
        if (remaining > capacity) {
            throw new IllegalArgumentException("remaining must be less than or equal to capacity");
        }
    }

    public ConsumptionState withRemaining(long newRemaining) {
        return new ConsumptionState(type, newRemaining, capacity, consumeRate);
    }

    public ConsumptionState withType(ResourceLocation newType) {
        return new ConsumptionState(newType, remaining, capacity, consumeRate);
    }

    public ConsumptionState withCapacity(long newCapacity) {
        return new ConsumptionState(type, Math.min(remaining, newCapacity), newCapacity, consumeRate);
    }

    public ConsumptionState withConsumeRate(long newConsumeRate) {
        return new ConsumptionState(type, remaining, capacity, newConsumeRate);
    }

    private static DataResult<Long> validateRemaining(long remaining) {
        return remaining >= 0
                ? DataResult.success(remaining)
                : DataResult.error(() -> "remaining must be greater than or equal to 0");
    }

    private static DataResult<Long> validateCapacity(long capacity) {
        return capacity >= 0
                ? DataResult.success(capacity)
                : DataResult.error(() -> "capacity must be greater than or equal to 0");
    }

    private static DataResult<Long> validateConsumeRate(long consumeRate) {
        return consumeRate >= 0
                ? DataResult.success(consumeRate)
                : DataResult.error(() -> "consume_rate must be greater than or equal to 0");
    }

    private static DataResult<ConsumptionState> validateState(ConsumptionState state) {
        return state.remaining <= state.capacity
                ? DataResult.success(state)
                : DataResult.error(() -> "remaining must be less than or equal to capacity");
    }
}
