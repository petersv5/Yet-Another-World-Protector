package de.z0rdak.yawp.core.flag;

import de.z0rdak.yawp.api.Flag;
import net.minecraft.nbt.CompoundTag;

/**
 * A simple boolean state flag.
 */
public class BooleanFlag extends AbstractFlag {

    public BooleanFlag(CompoundTag nbt) {
        super(nbt);
        this.deserializeNBT(nbt);
    }

    public BooleanFlag(Flag flag, FlagState state, boolean override) {
        super(flag.name(), flag.flagInfo().type(), override, state);
    }

    public BooleanFlag(Flag flag, FlagState state, FlagMessage msg, boolean override) {
        this(flag, state, override);
        this.msg = msg;
    }

    public BooleanFlag(Flag flag) {
        super(flag.name(), flag.flagInfo().type(), false, FlagState.DENIED);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
    }
}
