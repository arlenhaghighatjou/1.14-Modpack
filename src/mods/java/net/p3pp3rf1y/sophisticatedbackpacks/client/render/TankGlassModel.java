package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

public class TankGlassModel extends EntityModel<LivingEntity> {
	public final RendererModel leftTankGlass;
	public final RendererModel rightTankGlass;

	public TankGlassModel() {
		textureWidth = 32;
		textureHeight = 32;

		leftTankGlass = new RendererModel(this);
		leftTankGlass.setRotationPoint(0.0F, 24.0F, 0.0F);
		leftTankGlass.setTextureOffset(18, 5).addBox(-15F, 3.5F, -2.5F, 4, 10, 0, 0.0F, false);
		leftTankGlass.setTextureOffset(0, 0).addBox(-15F, 3.5F, -2.5F, 0, 10, 5, 0.0F, false);
		leftTankGlass.setTextureOffset(10, 5).addBox(-15F, 3.5F, 2.5F, 4, 10, 0, 0.0F, false);

		rightTankGlass = new RendererModel(this);
		rightTankGlass.setRotationPoint(0.0F, 24.0F, 0.0F);
		rightTankGlass.setTextureOffset(18, 5).addBox(11F, 3.5F, -2.5F, 4, 10, 0, 0.0F, true);
		rightTankGlass.setTextureOffset(0, 0).addBox(15F, 3.5F, -2.5F, 0, 10, 5, 0.0F, true);
		rightTankGlass.setTextureOffset(10, 5).addBox(11F, 3.5F, 2.5F, 4, 10, 0, 0.0F, true);
	}

	@Override
	public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		//noop
	}

	@Override
	public void render(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		//noop
	}

	public void render(float scale, boolean showLeftTank, boolean showRightTank) {
		if (showLeftTank) {
			leftTankGlass.render(scale);
		}
		if (showRightTank) {
			rightTankGlass.render(scale);
		}
	}
}
