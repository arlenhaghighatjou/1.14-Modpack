package net.lax1dude.eaglercraft.profile;

import net.minecraft.util.ResourceLocation;

public enum DefaultCapes {

    NO_CAPE(0, "No Cape", null),
    VANILLA(1, "Vanilla", new ResourceLocation("eagler:capes/vanilla.png")),
    TIKTOK(2, "TikTok", new ResourceLocation("eagler:capes/tiktok.png")),
    CHERRY(3, "Cherry Blossom", new ResourceLocation("eagler:capes/cherry.png")),
    MOONRARE(4, "Moonrare", new ResourceLocation("eagler:capes/moonrare.png")),
    MOJANGSTAFF(5, "Mojang Staff", new ResourceLocation("eagler:capes/mojangstaff.png")),
    ANNIVERSARY(6, "Anniversary", new ResourceLocation("eagler:capes/anivessary.png")),
    ENDERMAN(7, "Enderman", new ResourceLocation("eagler:capes/enderman.png")),
    MINECON13(8, "Minecon 2013", new ResourceLocation("eagler:capes/minecon2013.png")),
    JAVA(9, "Java Edition Free", new ResourceLocation("eagler:capes/javaeditionfree.png")),
    GOLEM(10, "Iron Golem", new ResourceLocation("eagler:capes/irongolem.png")),
    ANNIVERSARY15(11, "15th Anniversary", new ResourceLocation("eagler:capes/15thanivessary.png")),
    GRASS(12, "Common Grass", new ResourceLocation("eagler:capes/commongrass.png"));


    public static final DefaultCapes[] defaultCapesMap = new DefaultCapes[13];

    static {
        DefaultCapes[] capes = values();
        for (int i = 0; i < capes.length; ++i) {
            defaultCapesMap[capes[i].id] = capes[i];
        }
    }

    public final int id;
    public final String name;
    public final ResourceLocation location;

    private DefaultCapes(int id, String name, ResourceLocation location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public static DefaultCapes getCapeFromId(int id) {
        DefaultCapes e = null;
        if (id >= 0 && id < defaultCapesMap.length) {
            e = defaultCapesMap[id];
        }
        if (e != null) {
            return e;
        } else {
            return NO_CAPE;
        }
    }

}
