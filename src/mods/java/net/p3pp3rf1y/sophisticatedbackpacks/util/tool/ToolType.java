package net.p3pp3rf1y.sophisticatedbackpacks.util.tool;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A kind of tool a block can be broken with. Vanilla has no such concept, so the tool swapper
 * carries its own set and the registry adds to it from the mod's data.
 */
public class ToolType {
	private static final Map<String, ToolType> VALUES = new HashMap<>();

	public static final ToolType AXE = get("axe");
	public static final ToolType PICKAXE = get("pickaxe");
	public static final ToolType SHOVEL = get("shovel");
	public static final ToolType HOE = get("hoe");
	public static final ToolType SHEARS = get("shears");
	public static final ToolType SWORD = get("sword");

	private final String name;

	private ToolType(String name) {
		this.name = name;
	}

	public static ToolType get(String name) {
		return VALUES.computeIfAbsent(name.toLowerCase(Locale.ROOT), ToolType::new);
	}

	public static java.util.Set<ToolType> of(net.minecraft.item.ItemStack stack) {
		net.minecraft.item.Item item = stack.getItem();
		if (item instanceof net.minecraft.item.AxeItem) {
			return java.util.Collections.singleton(AXE);
		}
		if (item instanceof net.minecraft.item.PickaxeItem) {
			return java.util.Collections.singleton(PICKAXE);
		}
		if (item instanceof net.minecraft.item.ShovelItem) {
			return java.util.Collections.singleton(SHOVEL);
		}
		if (item instanceof net.minecraft.item.HoeItem) {
			return java.util.Collections.singleton(HOE);
		}
		if (item instanceof net.minecraft.item.ShearsItem) {
			return java.util.Collections.singleton(SHEARS);
		}
		if (item instanceof net.minecraft.item.SwordItem) {
			return java.util.Collections.singleton(SWORD);
		}
		return java.util.Collections.emptySet();
	}

	public static boolean isEffectiveOn(ToolType type, net.minecraft.block.BlockState state) {
		net.minecraft.block.material.Material material = state.getMaterial();
		if (type == PICKAXE) {
			return material == net.minecraft.block.material.Material.ROCK || material == net.minecraft.block.material.Material.IRON || material == net.minecraft.block.material.Material.ANVIL;
		}
		if (type == AXE) {
			return material == net.minecraft.block.material.Material.WOOD || material == net.minecraft.block.material.Material.PLANTS || material == net.minecraft.block.material.Material.GOURD;
		}
		if (type == SHOVEL) {
			return material == net.minecraft.block.material.Material.EARTH || material == net.minecraft.block.material.Material.SAND || material == net.minecraft.block.material.Material.CLAY || material == net.minecraft.block.material.Material.SNOW || material == net.minecraft.block.material.Material.SNOW_BLOCK;
		}
		if (type == HOE) {
			return material == net.minecraft.block.material.Material.LEAVES || material == net.minecraft.block.material.Material.PLANTS;
		}
		if (type == SHEARS) {
			return material == net.minecraft.block.material.Material.LEAVES || material == net.minecraft.block.material.Material.WEB || material == net.minecraft.block.material.Material.WOOL;
		}
		return false;
	}

	public static Map<String, ToolType> getValues() {
		return VALUES;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
