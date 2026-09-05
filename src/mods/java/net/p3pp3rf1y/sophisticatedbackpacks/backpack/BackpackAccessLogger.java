package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.minecraft.util.registry.Registry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public class BackpackAccessLogger {
	private static final int REFRESH_INTERVAL_SECONDS = 30;

	private BackpackAccessLogger() {}

	private static long lastCacheRefresh = 0;
	private static final Map<String, Set<AccessLogRecord>> playerLogCache = new HashMap<>();

	public static void logPlayerAccess(PlayerEntity player, Item backpackItem, EaglercraftUUID backpackUuid, String backpackName, int clothColor, int trimColor, int columnsTaken) {
		if (player.level.isRemote) {
			return;
		}
		//noinspection ConstantConditions - at this point the registry name of item exists for sure otherwise the player wouldn't be able to open the backpack
		BackpackStorage.get().putAccessLog(new AccessLogRecord(Registry.ITEM.getKey(backpackItem), backpackUuid, player.getDisplayName().getString(), backpackName, clothColor, trimColor, Util.millisecondsSinceEpoch(), columnsTaken));
	}

	public static Set<String> getPlayerNames() {
		initPlayerBackpackCache();
		return playerLogCache.keySet();
	}

	public static Collection<AccessLogRecord> getBackpackLogsForPlayer(String playerName) {
		initPlayerBackpackCache();
		return playerLogCache.getOrDefault(playerName, new HashSet<>());
	}

	private static void initPlayerBackpackCache() {
		if (lastCacheRefresh + (REFRESH_INTERVAL_SECONDS * 1000) >= Util.millisecondsSinceEpoch()) {
			return;
		}
		lastCacheRefresh = Util.millisecondsSinceEpoch();

		playerLogCache.clear();
		BackpackStorage.get().getAccessLogs().values().forEach(alr -> playerLogCache.computeIfAbsent(alr.getPlayerName(), name -> new HashSet<>()).add(alr));
	}

	public static Collection<AccessLogRecord> getAllBackpackLogs() {
		return BackpackStorage.get().getAccessLogs().values();
	}

	public static Set<EaglercraftUUID> getBackpackUuids() {
		return BackpackStorage.get().getAccessLogs().keySet();
	}

	public static Optional<AccessLogRecord> getBackpackLog(EaglercraftUUID backpackUuid) {
		return Optional.ofNullable(BackpackStorage.get().getAccessLogs().get(backpackUuid));
	}
}
