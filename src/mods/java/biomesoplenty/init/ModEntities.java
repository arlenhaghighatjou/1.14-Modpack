/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package biomesoplenty.init;

import biomesoplenty.api.entity.BOPEntities;
import biomesoplenty.common.entity.item.BoatEntityBOP;
import biomesoplenty.common.entity.item.BoatRendererBOP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModEntities
{
    public static void registerEntities()
    {
        BOPEntities.boat_bop = createEntity(BoatEntityBOP::new, EntityClassification.MISC, "boat_bop", 1.375F, 0.5625F);
    }

    public static <T extends Entity> EntityType<T> createEntity(EntityType.IFactory<T> factory, EntityClassification classification, String name, float width, float height)
    {
        ResourceLocation id = new ResourceLocation("biomesoplenty", name);
        return Registry.register(Registry.ENTITY_TYPE, id, EntityType.Builder.create(factory, classification).size(width, height).build(id.toString()));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRendering()
    {
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        renderManager.register(BoatEntityBOP.class, new BoatRendererBOP(renderManager));
    }
}
