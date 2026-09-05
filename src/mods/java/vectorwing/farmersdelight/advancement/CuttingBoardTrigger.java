package vectorwing.farmersdelight.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import vectorwing.farmersdelight.FarmersDelight;

import java.util.Map;
import java.util.Set;

public class CuttingBoardTrigger implements ICriterionTrigger<CuttingBoardTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation(FarmersDelight.MODID, "use_cutting_board");
	private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

	public ResourceLocation getId() {
		return ID;
	}

	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<Instance> listener) {
		Listeners boardListeners = this.listeners.get(playerAdvancementsIn);
		if (boardListeners == null) {
			boardListeners = new Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, boardListeners);
		}

		boardListeners.add(listener);
	}

	public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<Instance> listener) {
		Listeners boardListeners = this.listeners.get(playerAdvancementsIn);
		if (boardListeners != null) {
			boardListeners.remove(listener);
			if (boardListeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}

	}

	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new Instance();
	}

	public void trigger(ServerPlayerEntity player) {
		Listeners boardListeners = this.listeners.get(player.getAdvancements());
		if (boardListeners != null) {
			boardListeners.trigger();
		}

	}

	public static class Instance extends CriterionInstance {
		public Instance() {
			super(CuttingBoardTrigger.ID);
		}
	}

	static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<ICriterionTrigger.Listener<Instance>> listeners = Sets.newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(ICriterionTrigger.Listener<Instance> listener) {
			this.listeners.remove(listener);
		}

		public void trigger() {
			for (ICriterionTrigger.Listener<Instance> listener : Lists.newArrayList(this.listeners)) {
				listener.grantCriterion(this.playerAdvancements);
			}

		}
	}
}
