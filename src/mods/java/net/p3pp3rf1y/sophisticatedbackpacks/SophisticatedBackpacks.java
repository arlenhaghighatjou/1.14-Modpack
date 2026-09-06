package net.p3pp3rf1y.sophisticatedbackpacks;

import net.minecraft.item.ItemGroup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p3pp3rf1y.sophisticatedbackpacks.client.ClientProxy;
import net.p3pp3rf1y.sophisticatedbackpacks.command.SBPCommand;
import net.p3pp3rf1y.sophisticatedbackpacks.common.CommonProxy;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModLoot;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.RecipeHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class SophisticatedBackpacks {
	public static final String MOD_ID = "sophisticatedbackpacks";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final CommonProxy PROXY = new ClientProxy();
	public static final ItemGroup ITEM_GROUP = new SBItemGroup();

	private SophisticatedBackpacks() {}

	public static void registerContent() {
		PROXY.registerHandlers();
		ModLoot.init();
		PacketHandler.init();
		ModItems.registerDispenseBehavior();
		SBPCommand.registerArgumentTypes();
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerClient() {
		ModItems.registerClient();
		PROXY.registerClientHandlers();
	}

	public static void serverStarted(MinecraftServer server) {
		ServerWorld world = server.getWorld(DimensionType.OVERWORLD);
		if (world != null) {
			RecipeHelper.setWorld(world);
		}
	}

	public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
		SBPCommand.register(dispatcher);
	}
}
