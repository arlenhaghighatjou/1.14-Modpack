package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.profiler.IProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final PrioritizedGoal DUMMY = new PrioritizedGoal(Integer.MAX_VALUE, new Goal() {
      public boolean shouldExecute() {
         return false;
      }
   }) {
      public boolean isRunning() {
         return false;
      }
   };
   private final Map<Goal.Flag, PrioritizedGoal> flagGoals = new EnumMap<>(Goal.Flag.class);
   private final Set<PrioritizedGoal> goals = Sets.newLinkedHashSet();
   private final IProfiler profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
   private int tickRate = 3;

   public GoalSelector(IProfiler p_i50327_1_) {
      this.profiler = p_i50327_1_;
   }

   public void addGoal(int priority, Goal task) {
      this.goals.add(new PrioritizedGoal(priority, task));
   }

   public void removeGoal(Goal task) {
      Iterator<PrioritizedGoal> iterator = this.goals.iterator();
      while (iterator.hasNext()) {
         PrioritizedGoal goal = iterator.next();
         if (goal.getGoal() == task) {
            goal.resetTask();
            iterator.remove();
         }
      }
   }

   public void tick() {
      this.profiler.startSection("goalCleanup");
      for (PrioritizedGoal goal : this.goals) {
         if (goal.isRunning() && (this.hasDisabledFlag(goal) || !goal.shouldContinueExecuting())) {
            goal.resetTask();
         }
      }
      Iterator<Map.Entry<Goal.Flag, PrioritizedGoal>> iterator = this.flagGoals.entrySet().iterator();
      while (iterator.hasNext()) {
         if (!iterator.next().getValue().isRunning()) {
            iterator.remove();
         }
      }
      this.profiler.endSection();
      this.profiler.startSection("goalUpdate");
      for (PrioritizedGoal goal : this.goals) {
         if (goal.isRunning() || this.hasDisabledFlag(goal) || !this.areFlagsAvailable(goal) || !goal.shouldExecute()) {
            continue;
         }
         for (Goal.Flag flag : goal.getMutexFlags()) {
            PrioritizedGoal current = this.flagGoals.getOrDefault(flag, DUMMY);
            current.resetTask();
            this.flagGoals.put(flag, goal);
         }
         goal.startExecuting();
      }
      this.profiler.endSection();
      this.profiler.startSection("goalTick");
      for (PrioritizedGoal goal : this.goals) {
         if (goal.isRunning()) {
            goal.tick();
         }
      }
      this.profiler.endSection();
   }

   private boolean hasDisabledFlag(PrioritizedGoal goal) {
      for (Goal.Flag flag : goal.getMutexFlags()) {
         if (this.disabledFlags.contains(flag)) {
            return true;
         }
      }
      return false;
   }

   private boolean areFlagsAvailable(PrioritizedGoal goal) {
      for (Goal.Flag flag : goal.getMutexFlags()) {
         if (!this.flagGoals.getOrDefault(flag, DUMMY).isPreemptedBy(goal)) {
            return false;
         }
      }
      return true;
   }

   public Stream<PrioritizedGoal> getRunningGoals() {
      return this.goals.stream().filter(PrioritizedGoal::isRunning);
   }

   public void disableFlag(Goal.Flag p_220880_1_) {
      this.disabledFlags.add(p_220880_1_);
   }

   public void enableFlag(Goal.Flag p_220886_1_) {
      this.disabledFlags.remove(p_220886_1_);
   }

   public void setFlag(Goal.Flag p_220878_1_, boolean p_220878_2_) {
      if (p_220878_2_) {
         this.enableFlag(p_220878_1_);
      } else {
         this.disableFlag(p_220878_1_);
      }

   }
}
