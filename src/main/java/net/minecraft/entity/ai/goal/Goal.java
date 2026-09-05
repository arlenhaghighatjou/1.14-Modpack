package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class Goal {
    private final EnumSet<Goal.Flag> flags = EnumSet.noneOf(Goal.Flag.class);

    public abstract boolean shouldExecute();

    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    public boolean isPreemptible() {
        return true;
    }

    public void startExecuting() {
    }

    public void resetTask() {
    }

    public void tick() {
    }

    public void setMutexFlags(EnumSet<Goal.Flag> p_220684_1_) {
        this.flags.clear();
        this.flags.addAll(p_220684_1_);
    }

    public EnumSet<Goal.Flag> getMutexFlags() {
        return this.flags;
    }

    public static enum Flag {
        MOVE,
        LOOK,
        JUMP,
        TARGET;
    }
}
