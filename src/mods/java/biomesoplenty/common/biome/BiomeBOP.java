/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package biomesoplenty.common.biome;

import biomesoplenty.api.enums.BOPClimates;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BiomeBOP extends Biome
{
	public enum Type {
		BEACH, COLD, CONIFEROUS, DEAD, DENSE, DRY, END, FOREST, HILLS, HOT, JUNGLE, LUSH, MAGICAL, MOUNTAIN, NETHER, OCEAN, PLAINS, RARE, RIVER, SANDY, SAVANNA, SNOWY, SPARSE, SPOOKY, SWAMP, WASTELAND, WATER, WET
	}

	private long traits;

	public void addTypes(Type... types) {
		for (int i = 0; i < types.length; ++i) {
			traits |= 1L << types[i].ordinal();
		}
	}

	public static boolean hasType(Biome biome, Type type) {
		if (biome instanceof BiomeBOP) {
			return (((BiomeBOP) biome).traits & 1L << type.ordinal()) != 0;
		}
		Biome.Category category = biome.getCategory();
		switch (type) {
		case FOREST:
			return category == Biome.Category.FOREST || category == Biome.Category.TAIGA;
		case CONIFEROUS:
			return category == Biome.Category.TAIGA;
		case JUNGLE:
			return category == Biome.Category.JUNGLE;
		case RIVER:
			return category == Biome.Category.RIVER;
		case OCEAN:
			return category == Biome.Category.OCEAN;
		case LUSH:
			return category == Biome.Category.JUNGLE || category == Biome.Category.SWAMP;
		default:
			return false;
		}
	}

    protected Map<BOPClimates, Integer> weightMap = new HashMap<BOPClimates, Integer>();
	public boolean canSpawnInBiome;
	public int beachBiomeId = Registry.BIOME.getId(Biomes.BEACH);
	public int riverBiomeId = Registry.BIOME.getId(Biomes.RIVER);

    public BiomeBOP(Builder builder)
    {
        super(builder);
        this.canSpawnInBiome = true;
    }

    public void addWeight(BOPClimates climate, int weight)
    {
        this.weightMap.put(climate, weight);
    }

    public void setBeachBiome(Optional<Biome> biome)
    {
        if (biome.isPresent())
            this.beachBiomeId = Registry.BIOME.getId(biome.get());
        else
            this.beachBiomeId = -1;
    }

    public void setBeachBiome(Biome biome)
    {
        if (biome != null)
            this.beachBiomeId = Registry.BIOME.getId(biome);
        else
            this.beachBiomeId = -1;
    }

    public void setRiverBiome(Optional<Biome> biome)
    {
        if (biome.isPresent())
            this.riverBiomeId = Registry.BIOME.getId(biome.get());
        else
            this.riverBiomeId = -1;
    }

    public void setRiverBiome(Biome biome)
    {
        if (biome != null)
            this.riverBiomeId = Registry.BIOME.getId(biome);
        else
            this.riverBiomeId = -1;
    }

    public Map<BOPClimates, Integer> getWeightMap()
    {
        return this.weightMap;
    }
}
