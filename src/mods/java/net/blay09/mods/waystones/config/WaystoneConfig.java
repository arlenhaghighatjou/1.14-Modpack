package net.blay09.mods.waystones.config;

public class WaystoneConfig {

    public static final WaystoneCommonConfig COMMON = new WaystoneCommonConfig();
    public static final WaystoneServerConfig SERVER = new WaystoneServerConfig();
    public static final WaystoneClientConfig CLIENT = new WaystoneClientConfig();

    public static InventoryButtonMode getInventoryButtonMode() {
        return new InventoryButtonMode(SERVER.inventoryButton);
    }
}
