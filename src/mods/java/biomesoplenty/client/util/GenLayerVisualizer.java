/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package biomesoplenty.client.util;

import biomesoplenty.common.world.BOPLayerUtil;
import biomesoplenty.common.world.BOPWorldSettings;
import biomesoplenty.common.world.layer.traits.IBOPContextExtended;
import biomesoplenty.common.world.layer.traits.LazyAreaLayerContextBOP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import java.util.function.LongFunction;

public class GenLayerVisualizer extends Screen
{
    private static final int CANVAS_WIDTH = 100;
    private static final int CANVAS_HEIGHT = 100;
    private static final int START_X = 5000;
    private static final int START_Z = 10000;

    private DynamicTexture texture;
    private ResourceLocation textureLocation;

    public GenLayerVisualizer()
    {
        super(new StringTextComponent("Gen Layer Visualizer"));
    }

    public static void run()
    {
        Minecraft.getInstance().displayGuiScreen(new GenLayerVisualizer());
    }

    @Override
    protected void init()
    {
        if (this.texture != null)
        {
            return;
        }

        LongFunction<IBOPContextExtended<LazyArea>> contextFactory = (seedModifier) -> {
            return new LazyAreaLayerContextBOP(1, 0, seedModifier);
        };

        IAreaFactory<LazyArea> landAreaFactory = BOPLayerUtil.createInitialLandAndSeaFactory(contextFactory);
        IAreaFactory<LazyArea> climateFactory = BOPLayerUtil.createClimateFactory(contextFactory, new BOPWorldSettings());
        LazyArea area = BOPLayerUtil.createBiomeFactory(landAreaFactory, climateFactory, contextFactory).make();

        this.texture = new DynamicTexture(CANVAS_WIDTH, CANVAS_HEIGHT, false);

        for (int z = 0; z < CANVAS_HEIGHT; ++z)
        {
            for (int x = 0; x < CANVAS_WIDTH; ++x)
            {
                int colour = BiomeMapColours.getBiomeMapColour(area.getValue(START_X + x, START_Z + z));
                this.texture.getTextureData().setPixelRGBA(x, z, 0xFF000000 | (colour >> 16) & 0xFF | colour & 0xFF00 | (colour << 16) & 0xFF0000);
            }
        }

        this.texture.updateDynamicTexture();
        this.textureLocation = this.mc.getTextureManager().getDynamicTextureLocation("biomesoplenty_gen_layers", this.texture);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        this.mc.getTextureManager().bindTexture(this.textureLocation);
        int size = Math.min(this.width, this.height);
        blit((this.width - size) / 2, (this.height - size) / 2, 0, 0.0F, 0.0F, size, size, CANVAS_HEIGHT, CANVAS_WIDTH);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose()
    {
        this.mc.getTextureManager().deleteTexture(this.textureLocation);
        this.texture.close();
        super.onClose();
    }
}
