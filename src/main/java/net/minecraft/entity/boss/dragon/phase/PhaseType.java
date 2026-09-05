package net.minecraft.entity.boss.dragon.phase;

import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class PhaseType<T extends IPhase> {
   private static PhaseType<?>[] phases = new PhaseType[0];
   public static final PhaseType<HoldingPatternPhase> HOLDING_PATTERN = create(HoldingPatternPhase::new, "HoldingPattern");
   public static final PhaseType<StrafePlayerPhase> STRAFE_PLAYER = create(StrafePlayerPhase::new, "StrafePlayer");
   public static final PhaseType<LandingApproachPhase> LANDING_APPROACH = create(LandingApproachPhase::new, "LandingApproach");
   public static final PhaseType<LandingPhase> LANDING = create(LandingPhase::new, "Landing");
   public static final PhaseType<TakeoffPhase> TAKEOFF = create(TakeoffPhase::new, "Takeoff");
   public static final PhaseType<FlamingSittingPhase> SITTING_FLAMING = create(FlamingSittingPhase::new, "SittingFlaming");
   public static final PhaseType<ScanningSittingPhase> SITTING_SCANNING = create(ScanningSittingPhase::new, "SittingScanning");
   public static final PhaseType<AttackingSittingPhase> SITTING_ATTACKING = create(AttackingSittingPhase::new, "SittingAttacking");
   public static final PhaseType<ChargingPlayerPhase> CHARGING_PLAYER = create(ChargingPlayerPhase::new, "ChargingPlayer");
   public static final PhaseType<DyingPhase> DYING = create(DyingPhase::new, "Dying");
   public static final PhaseType<HoverPhase> HOVER = create(HoverPhase::new, "Hover");
   private final Function<EnderDragonEntity, IPhase> factory;
   private final int id;
   private final String name;

   private PhaseType(int idIn, Function<EnderDragonEntity, IPhase> factoryIn, String nameIn) {
      this.id = idIn;
      this.factory = factoryIn;
      this.name = nameIn;
   }

   public IPhase createPhase(EnderDragonEntity dragon) {
      return this.factory.apply(dragon);
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static PhaseType<?> getById(int idIn) {
      return idIn >= 0 && idIn < phases.length ? phases[idIn] : HOLDING_PATTERN;
   }

   public static int getTotalPhases() {
      return phases.length;
   }

   private static <T extends IPhase> PhaseType<T> create(Function<EnderDragonEntity, IPhase> factoryIn, String nameIn) {
      PhaseType<T> phasetype = new PhaseType<>(phases.length, factoryIn, nameIn);
      phases = Arrays.copyOf(phases, phases.length + 1);
      phases[phasetype.getId()] = phasetype;
      return phasetype;
   }
}
