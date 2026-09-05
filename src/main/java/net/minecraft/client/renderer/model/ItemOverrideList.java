package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ItemOverrideList {
    public static final ItemOverrideList EMPTY = new ItemOverrideList();
    private final List<ItemOverride> overrides = Lists.newArrayList();
    private final List<IBakedModel> overrideBakedModels;

    private ItemOverrideList() {
        this.overrideBakedModels = Collections.emptyList();
    }

    public ItemOverrideList(ModelBakery p_i50984_1_, BlockModel p_i50984_2_, Function<ResourceLocation, IUnbakedModel> p_i50984_3_, List<ItemOverride> p_i50984_4_) {
        this.overrideBakedModels = p_i50984_4_.stream().map((p_217649_3_) -> {
            IUnbakedModel iunbakedmodel = p_i50984_3_.apply(p_217649_3_.getLocation());
            return Objects.equals(iunbakedmodel, p_i50984_2_) ? null : p_i50984_1_.func_217845_a(p_217649_3_.getLocation(), ModelRotation.X0_Y0);
        }).collect(Collectors.toList());
        Collections.reverse(this.overrideBakedModels);

        for (int i = p_i50984_4_.size() - 1; i >= 0; --i) {
            this.overrides.add(p_i50984_4_.get(i));
        }

    }

    public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, World worldIn, LivingEntity entityIn) {
        if (!this.overrides.isEmpty()) {
            for (int i = 0; i < this.overrides.size(); ++i) {
                ItemOverride itemoverride = this.overrides.get(i);
                if (itemoverride.matchesItemStack(stack, worldIn, entityIn)) {
                    IBakedModel ibakedmodel = this.overrideBakedModels.get(i);
                    if (ibakedmodel == null) {
                        return model;
                    }

                    return ibakedmodel;
                }
            }
        }

        return model;
    }
}
