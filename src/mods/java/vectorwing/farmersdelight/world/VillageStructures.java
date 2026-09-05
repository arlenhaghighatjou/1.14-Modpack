package vectorwing.farmersdelight.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.*;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VillageStructures {

	public static void init() {
		PlainsVillagePools.init();
		SnowyVillagePools.init();
		SavannaVillagePools.init();
		DesertVillagePools.init();
		TaigaVillagePools.init();

		Map<String, Integer> biomeChances = (new ImmutableMap.Builder<String, Integer>())
				.put("plains", 5)
				.put("snowy", 2)
				.put("savanna", 2)
				.put("desert", 2)
				.put("taiga", 3)
				.build();

		for (Map.Entry<String, Integer> biome : biomeChances.entrySet()) {
			addToPool(new ResourceLocation("village/"+biome.getKey()+"/houses"),	new ResourceLocation(FarmersDelight.MODID, "village/houses/"+biome.getKey()+"_compost_pile"), biome.getValue());
		}
	}

	private static void addToPool(ResourceLocation pool, ResourceLocation toAdd, int weight) {
		JigsawPattern old = JigsawManager.field_214891_a.get(pool);
		List<Pair<JigsawPiece, Integer>> newPieces = new ArrayList<>();
		for(JigsawPiece piece : old.func_214943_b(MathUtils.RAND))
		{
			newPieces.add(Pair.of(piece, 1));
		}
		newPieces.add(Pair.of(new SingleJigsawPiece(toAdd.toString(), ImmutableList.of()), weight));
		JigsawManager.field_214891_a.register(new JigsawPattern(pool, old.func_214947_b(), newPieces, JigsawPattern.PlacementBehaviour.RIGID));
	}
}
