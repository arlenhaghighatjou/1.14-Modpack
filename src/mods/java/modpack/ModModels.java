package modpack;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackDynamicModel;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Model hooks for the pack mods that assemble their models in code rather than shipping one json per combination.
 */
@OnlyIn(Dist.CLIENT)
public class ModModels {
	private ModModels() {}

	public static void registerModels(BiConsumer<ResourceLocation, IUnbakedModel> out) {
		BackpackDynamicModel.registerModels(out);
	}

	public static void onBaked(ModelBakery bakery, Map<ResourceLocation, IBakedModel> registry) {
		BackpackDynamicModel.bakeInto(bakery, registry);
	}
}
