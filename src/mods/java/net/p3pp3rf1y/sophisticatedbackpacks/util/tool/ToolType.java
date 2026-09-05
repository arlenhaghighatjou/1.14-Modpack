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
