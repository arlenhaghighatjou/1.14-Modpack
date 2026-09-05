package net.blay09.mods.waystones.config;

public class WaystoneServerConfig {
    public int blocksPerXPLevel = 500;
    public double maximumXpCost = 3;

    public double waystoneXpCostMultiplier = 1;

    public String inventoryButton = "NONE";
    public double inventoryButtonXpCostMultiplier = 1;
    public int inventoryButtonCooldown = 300;

    public double globalWaystoneXpCostMultiplier = 1;
    public double globalWaystoneCooldownMultiplier = 1;
    public boolean globalWaystoneRequiresCreative = true;

    public double warpStoneXpCostMultiplier = 1;
    public int warpStoneCooldown = 300;
    public int warpStoneUseTime = 32;

    public int scrollUseTime = 32;

    public DimensionalWarp dimensionalWarp = DimensionalWarp.ALLOW;
    public int dimensionalWarpXpCost = 3;

    public boolean restrictToCreative = false;
    public boolean restrictRenameToOwner = false;
    public boolean generatedWaystonesUnbreakable = false;
}
