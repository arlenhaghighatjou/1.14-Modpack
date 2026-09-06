package net.p3pp3rf1y.sophisticatedbackpacks.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.tileentity.TileEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackTileEntity;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

public class CopyBackpackDataFunction extends LootFunction {
	protected CopyBackpackDataFunction(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	protected ItemStack doApply(ItemStack stack, LootContext context) {
		TileEntity te = context.get(LootParameters.BLOCK_ENTITY);
		if (te instanceof BackpackTileEntity) {
			return ((BackpackTileEntity) te).getBackpackWrapper().getBackpack();
		}

		return stack;
	}

	public static CopyBackpackDataFunction.Builder builder() {
		return new CopyBackpackDataFunction.Builder();
	}

	public static class Serializer extends LootFunction.Serializer<CopyBackpackDataFunction> {
		public Serializer() {
			super(new ResourceLocation(SophisticatedBackpacks.MOD_ID, "copy_backpack_data"), CopyBackpackDataFunction.class);
		}

		@Override
		public CopyBackpackDataFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
			return new CopyBackpackDataFunction(conditionsIn);
		}
	}

	public static class Builder extends LootFunction.Builder<CopyBackpackDataFunction.Builder> {
		@Override
		protected CopyBackpackDataFunction.Builder getThis() {
			return this;
		}

		@Override
		public ILootFunction build() {
			return new CopyBackpackDataFunction(getConditions());
		}
	}
}
