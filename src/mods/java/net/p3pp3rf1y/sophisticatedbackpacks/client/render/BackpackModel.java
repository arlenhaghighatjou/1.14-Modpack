package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.Collections;
import java.util.Map;

public class BackpackModel extends EntityModel<LivingEntity> {
	private final Map<Item, RendererModel> clipsBody;
	private final Map<Item, RendererModel> clipsLeftPouches;
	private final Map<Item, RendererModel> clipsRightPouches;
	private final Map<Item, RendererModel> clipsFrontPouch;
	private final Map<Item, RendererModel> clipsBattery;

	public final RendererModel cloth;
	private final RendererModel rightPouchesBorder;
	private final RendererModel leftPouchesBorder;
	private final RendererModel frontPouchBorder;
	private final RendererModel frontPouch;
	private final RendererModel rightPouches;
	private final RendererModel leftPouches;
	public final RendererModel border;
	private final RendererModel fabricFront;
	private final RendererModel fabricRight;
	private final RendererModel fabricLeft;
	public final RendererModel fabric;
	private final RendererModel battery;
	private final RendererModel batteryBorder;
	private final RendererModel leftTank;
	private final RendererModel leftTankBorder;
	private final RendererModel rightTank;
	private final RendererModel rightTankBorder;

	public BackpackModel() {
		textureWidth = 64;
		textureHeight = 64;

		cloth = new RendererModel(this);
		cloth.setRotationPoint(0.0F, 24.0F, 0.0F);
		cloth.setTextureOffset(0, 0).addBox(-3.5F, -13.25F, -3.25F, 7, 4, 6, 0.0F, false);
		cloth.setTextureOffset(0, 10).addBox(-5.0F, -13.0F, -3.0F, 10, 13, 6, 0.0F, false);

		rightPouchesBorder = new RendererModel(this);
		rightPouchesBorder.setRotationPoint(0.0F, 24.0F, 0.0F);
		rightPouchesBorder.setTextureOffset(44, 0).addBox(5.0F, -2.0F, -2.5F, 2, 1, 5, 0.0F, true);

		leftPouchesBorder = new RendererModel(this);
		leftPouchesBorder.setRotationPoint(0.0F, 24.0F, 0.0F);
		leftPouchesBorder.setTextureOffset(44, 0).addBox(-7.0F, -2.0F, -2.5F, 2, 1, 5, 0.0F, false);

		frontPouchBorder = new RendererModel(this);
		frontPouchBorder.setRotationPoint(0.0F, 24.0F, 0.0F);
		frontPouchBorder.setTextureOffset(44, 0).addBox(-4.0F, -2.0F, -5.0F, 8, 1, 2, 0.0F, false);

		frontPouch = new RendererModel(this);
		frontPouch.setRotationPoint(0.0F, 24.0F, 0.0F);
		frontPouch.setTextureOffset(25, 0).addBox(-4.0F, -1.0F, -5.0F, 8, 1, 2, 0.0F, false);
		frontPouch.setTextureOffset(13, 2).addBox(-4.0F, -4.0F, -5.0F, 8, 2, 2, 0.0F, false);
		frontPouch.setTextureOffset(13, 0).addBox(-4.0F, -6.0F, -5.0F, 8, 1, 2, 0.0F, false);

		rightPouches = new RendererModel(this);
		rightPouches.setRotationPoint(0.0F, 24.0F, 0.0F);
		rightPouches.setTextureOffset(32, 5).addBox(5.0F, -1.0F, -2.5F, 2, 1, 5, 0.0F, false);
		rightPouches.setTextureOffset(32, 13).addBox(5.0F, -4.0F, -2.5F, 2, 2, 5, 0.0F, false);
		rightPouches.setTextureOffset(32, 11).addBox(5.0F, -6.0F, -2.5F, 2, 1, 5, 0.0F, false);
		rightPouches.setTextureOffset(32, 22).addBox(5.0F, -9.0F, -2.5F, 1, 2, 5, 0.0F, false);
		rightPouches.setTextureOffset(32, 20).addBox(5.0F, -11.0F, -2.5F, 1, 1, 5, 0.0F, false);

		leftPouches = new RendererModel(this);
		leftPouches.setRotationPoint(0.0F, 24.0F, 0.0F);
		leftPouches.setTextureOffset(32, 5).addBox(-7.0F, -1.0F, -2.5F, 2, 1, 5, 0.0F, true);
		leftPouches.setTextureOffset(32, 13).addBox(-7.0F, -4.0F, -2.5F, 2, 2, 5, 0.0F, true);
		leftPouches.setTextureOffset(32, 11).addBox(-7.0F, -6.0F, -2.5F, 2, 1, 5, 0.0F, true);
		leftPouches.setTextureOffset(32, 22).addBox(-6.0F, -9.0F, -2.5F, 1, 2, 5, 0.0F, true);
		leftPouches.setTextureOffset(32, 20).addBox(-6.0F, -11.0F, -2.5F, 1, 1, 5, 0.0F, true);

		border = new RendererModel(this);
		border.setRotationPoint(0.0F, 24.0F, 0.0F);
		border.setTextureOffset(44, 7).addBox(-3.5F, -9.25F, -3.25F, 7, 1, 1, 0.0F, false);
		border.setTextureOffset(50, 20).addBox(3.5F, -13.25F, -3.25F, 1, 5, 6, 0.0F, false);
		border.setTextureOffset(50, 9).addBox(-4.5F, -13.25F, -3.25F, 1, 5, 6, 0.0F, false);

		fabricFront = new RendererModel(this);
		fabricFront.setRotationPoint(-3.25F, 16.0F, -6.0F);
		fabricFront.setTextureOffset(0, 55).addBox(-0.75F, 3.0F, 1.0F, 8, 1, 2, 0.0F, true);

		fabricRight = new RendererModel(this);
		fabricRight.setRotationPoint(-3.25F, 16.0F, -6.0F);
		fabricRight.setTextureOffset(32, 49).addBox(8.25F, -2.0F, 3.5F, 1, 1, 5, 0.0F, true);
		fabricRight.setTextureOffset(8, 45).addBox(8.25F, 3.0F, 3.5F, 2, 1, 5, 0.0F, true);

		fabricLeft = new RendererModel(this);
		fabricLeft.setRotationPoint(-3.25F, 16.0F, -6.0F);
		fabricLeft.setTextureOffset(32, 49).addBox(-2.75F, -2.0F, 3.5F, 1, 1, 5, 0.0F, false);
		fabricLeft.setTextureOffset(8, 45).addBox(-3.75F, 3.0F, 3.5F, 2, 1, 5, 0.0F, false);

		fabric = new RendererModel(this);
		fabric.setRotationPoint(-3.25F, 16.0F, -6.0F);
		fabric.setTextureOffset(54, 0).addBox(1.25F, -4.75F, 5.75F, 1, 1, 1, 0.0F, false);
		fabric.setTextureOffset(58, 0).addBox(4.25F, -4.75F, 5.75F, 1, 1, 1, 0.0F, false);
		fabric.setTextureOffset(44, 0).addBox(1.25F, -5.75F, 5.75F, 4, 1, 1, 0.0F, true);
		fabric.setTextureOffset(16, 44).addBox(0.0F, -5.5F, 2.5F, 1, 4, 7, 0.0F, false);
		fabric.setTextureOffset(0, 44).addBox(5.5F, -5.5F, 2.5F, 1, 4, 7, 0.0F, false);

		batteryBorder = new RendererModel(this);
		batteryBorder.setRotationPoint(0.0F, 24.0F, 0.0F);
		batteryBorder.setTextureOffset(28, 38).addBox(-4.25F, -5.25F, -6.25F, 1, 1, 4, 0.0F, false);
		batteryBorder.setTextureOffset(28, 43).addBox(-3.5F, -5.25F, -6.25F, 7, 1, 1, 0.0F, false);
		batteryBorder.setTextureOffset(33, 38).addBox(-4.25F, -1.25F, -6.25F, 1, 1, 4, 0.0F, false);
		batteryBorder.setTextureOffset(33, 38).addBox(3.25F, -5.25F, -6.25F, 1, 1, 4, 0.0F, false);
		batteryBorder.setTextureOffset(27, 45).addBox(-3.5F, -1.25F, -6.25F, 7, 1, 1, 0.0F, false);
		batteryBorder.setTextureOffset(39, 37).addBox(3.25F, -1.25F, -6.25F, 1, 1, 4, 0.0F, false);

		battery = new RendererModel(this);
		battery.setRotationPoint(0.0F, 24.0F, 0.0F);
		battery.setTextureOffset(28, 29).addBox(-4.0F, -6.0F, -6.0F, 8, 6, 3, 0.0F, false);
		battery.setTextureOffset(28, 53).addBox(-2.0F, -6.25F, -4.5F, 1, 1, 1, 0.0F, false);
		battery.setTextureOffset(28, 53).addBox(-0.75F, -6.25F, -4.5F, 1, 1, 1, 0.0F, false);
		battery.setTextureOffset(28, 53).addBox(-2.0F, -8.0F, -3.25F, 1, 1, 1, 0.0F, false);
		battery.setTextureOffset(28, 53).addBox(-0.75F, -8.0F, -3.25F, 1, 1, 1, 0.0F, false);
		battery.setTextureOffset(0, 58).addBox(-2.0F, -7.4F, -4.5F, 1, 2, 1, -0.2F, false);
		battery.setTextureOffset(6, 58).addBox(-0.75F, -7.4F, -4.5F, 1, 2, 1, -0.2F, false);
		battery.setTextureOffset(0, 61).addBox(-2.0F, -8.0F, -4.5F, 1, 1, 2, -0.2F, false);
		battery.setTextureOffset(6, 61).addBox(-0.75F, -8.0F, -4.5F, 1, 1, 2, -0.2F, false);

		leftTankBorder = new RendererModel(this);
		leftTankBorder.setRotationPoint(0.0F, 24.0F, 0.0F);
		leftTankBorder.setTextureOffset(50, 43).addBox(-8.0F, -9.5F, -2.0F, 3, 1, 4, 0.0F, false);

		leftTank = new RendererModel(this);
		leftTank.setRotationPoint(0.0F, 24.0F, 0.0F);
		leftTank.setTextureOffset(54, 27).addBox(-5.5F, -7.5F, -2.0F, 1, 6, 4, 0.0F, false);
		leftTank.setTextureOffset(50, 37).addBox(-8.0F, -1.5F, -2.0F, 3, 1, 4, 0.0F, false);
		leftTank.setTextureOffset(50, 42).addBox(-8.0F, -8.5F, -2.0F, 3, 1, 4, 0.0F, false);
		leftTank.setTextureOffset(50, 37).addBox(-8.0F, -10.5F, -2.0F, 3, 1, 4, 0.0F, false);
		leftTank.setTextureOffset(52, 48).addBox(-7.5F, -11.5F, -1.5F, 3, 1, 3, 0.0F, false);

		rightTankBorder = new RendererModel(this);
		rightTankBorder.setRotationPoint(0.0F, 24.0F, 0.0F);
		rightTankBorder.setTextureOffset(50, 43).addBox(5.0F, -9.5F, -2.0F, 3, 1, 4, 0.0F, true);

		rightTank = new RendererModel(this);
		rightTank.setRotationPoint(0.0F, 24.0F, 0.0F);
		rightTank.setTextureOffset(54, 27).addBox(4.5F, -7.5F, -2.0F, 1, 6, 4, 0.0F, true);
		rightTank.setTextureOffset(50, 37).addBox(5.0F, -1.5F, -2.0F, 3, 1, 4, 0.0F, true);
		rightTank.setTextureOffset(50, 42).addBox(5.0F, -8.5F, -2.0F, 3, 1, 4, 0.0F, true);
		rightTank.setTextureOffset(50, 37).addBox(5.0F, -10.5F, -2.0F, 3, 1, 4, 0.0F, true);
		rightTank.setTextureOffset(52, 48).addBox(4.5F, -11.5F, -1.5F, 3, 1, 3, 0.0F, true);

		clipsBody = ImmutableMap.of(
				ModItems.BACKPACK, getBodyClipsRenderer(29),
				ModItems.IRON_BACKPACK, getBodyClipsRenderer(32),
				ModItems.GOLD_BACKPACK, getBodyClipsRenderer(35),
				ModItems.DIAMOND_BACKPACK, getBodyClipsRenderer(38),
				ModItems.NETHERITE_BACKPACK, getBodyClipsRenderer(41)
		);
		clipsLeftPouches = ImmutableMap.of(
				ModItems.BACKPACK, getLeftPouchesClipsRenderer(29),
				ModItems.IRON_BACKPACK, getLeftPouchesClipsRenderer(32),
				ModItems.GOLD_BACKPACK, getLeftPouchesClipsRenderer(35),
				ModItems.DIAMOND_BACKPACK, getLeftPouchesClipsRenderer(38),
				ModItems.NETHERITE_BACKPACK, getLeftPouchesClipsRenderer(41)
		);
		clipsRightPouches = ImmutableMap.of(
				ModItems.BACKPACK, getRightPouchesClipsRenderer(29),
				ModItems.IRON_BACKPACK, getRightPouchesClipsRenderer(32),
				ModItems.GOLD_BACKPACK, getRightPouchesClipsRenderer(35),
				ModItems.DIAMOND_BACKPACK, getRightPouchesClipsRenderer(38),
				ModItems.NETHERITE_BACKPACK, getRightPouchesClipsRenderer(41)
		);
		clipsFrontPouch = ImmutableMap.of(
				ModItems.BACKPACK, getFrontPouchClipsRenderer(29),
				ModItems.IRON_BACKPACK, getFrontPouchClipsRenderer(32),
				ModItems.GOLD_BACKPACK, getFrontPouchClipsRenderer(35),
				ModItems.DIAMOND_BACKPACK, getFrontPouchClipsRenderer(38),
				ModItems.NETHERITE_BACKPACK, getFrontPouchClipsRenderer(41)
		);

		clipsBattery = ImmutableMap.of(
				ModItems.BACKPACK, getBatteryClipsRenderer(30),
				ModItems.IRON_BACKPACK, getBatteryClipsRenderer(33),
				ModItems.GOLD_BACKPACK, getBatteryClipsRenderer(36),
				ModItems.DIAMOND_BACKPACK, getBatteryClipsRenderer(39),
				ModItems.NETHERITE_BACKPACK, getBatteryClipsRenderer(42)
		);
	}

	public void render(float scale, int clothColor, int borderColor, Item backpackItem, boolean showLeftTank, boolean showRightTank, boolean showBattery) {
		float borderRed = (borderColor >> 16 & 255) / 255.0F;
		float borderGreen = (borderColor >> 8 & 255) / 255.0F;
		float borderBlue = (borderColor & 255) / 255.0F;
		float clothRed = (clothColor >> 16 & 255) / 255.0F;
		float clothGreen = (clothColor >> 8 & 255) / 255.0F;
		float clothBlue = (clothColor & 255) / 255.0F;

		if (showLeftTank) {
			leftTank.render(scale);
			GlStateManager.color4f(borderRed, borderGreen, borderBlue, 1F);
		leftTankBorder.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		} else {
			fabricLeft.render(scale);
			clipsLeftPouches.get(backpackItem).render(scale);
			GlStateManager.color4f(clothRed, clothGreen, clothBlue, 1F);
		leftPouches.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
			GlStateManager.color4f(borderRed, borderGreen, borderBlue, 1F);
		leftPouchesBorder.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		}

		if (showRightTank) {
			rightTank.render(scale);
			GlStateManager.color4f(borderRed, borderGreen, borderBlue, 1F);
		rightTankBorder.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		} else {
			fabricRight.render(scale);
			clipsRightPouches.get(backpackItem).render(scale);
			GlStateManager.color4f(clothRed, clothGreen, clothBlue, 1F);
		rightPouches.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
			GlStateManager.color4f(borderRed, borderGreen, borderBlue, 1F);
		rightPouchesBorder.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		}

		if (showBattery) {
			battery.render(scale);
			GlStateManager.color4f(borderRed, borderGreen, borderBlue, 1F);
		batteryBorder.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
			clipsBattery.get(backpackItem).render(scale);
		} else {
			fabricFront.render(scale);
			clipsFrontPouch.get(backpackItem).render(scale);
			GlStateManager.color4f(clothRed, clothGreen, clothBlue, 1F);
		frontPouch.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
			GlStateManager.color4f(borderRed, borderGreen, borderBlue, 1F);
		frontPouchBorder.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		}

		fabric.render(scale);
		clipsBody.get(backpackItem).render(scale);

		GlStateManager.color4f(clothRed, clothGreen, clothBlue, 1F);
		cloth.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);

		GlStateManager.color4f(borderRed, borderGreen, borderBlue, 1F);
		border.render(scale);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
	}

	private RendererModel getBodyClipsRenderer(int yTextureOffset) {
		RendererModel temp = new RendererModel(this);
		temp.setRotationPoint(0.0F, 24.0F, 0.0F);
		temp.setTextureOffset(22, yTextureOffset).addBox(-3.25F, -9.5F, -3.5F, 1, 2, 1, 0.0F, false);
		temp.setTextureOffset(25, yTextureOffset).addBox(2.25F, -9.5F, -3.5F, 1, 2, 1, 0.0F, false);
		return temp;
	}

	private RendererModel getLeftPouchesClipsRenderer(int yTextureOffset) {
		RendererModel temp = new RendererModel(this);
		temp.setRotationPoint(0.0F, 24.0F, 0.0F);
		temp.setTextureOffset(18, yTextureOffset).addBox(-6.25F, -10.0F, -0.5F, 1, 2, 1, 0.0F, false);
		temp.setTextureOffset(6, yTextureOffset).addBox(-7.25F, -5.0F, -0.5F, 1, 2, 1, 0.0F, false);
		return temp;
	}

	private RendererModel getRightPouchesClipsRenderer(int yTextureOffset) {
		RendererModel temp = new RendererModel(this);
		temp.setRotationPoint(0.0F, 24.0F, 0.0F);
		temp.setTextureOffset(6, yTextureOffset).addBox(6.25F, -5.0F, -0.5F, 1, 2, 1, 0.0F, true);
		temp.setTextureOffset(18, yTextureOffset).addBox(5.25F, -10.0F, -0.5F, 1, 2, 1, 0.0F, true);
		return temp;
	}

	private RendererModel getFrontPouchClipsRenderer(int yTextureOffset) {
		RendererModel temp = new RendererModel(this);
		temp.setRotationPoint(0.0F, 24.0F, 0.0F);
		temp.setTextureOffset(0, yTextureOffset).addBox(2.0F, -5.0F, -5.25F, 1, 2, 1, 0.0F, false);
		temp.setTextureOffset(3, yTextureOffset).addBox(-3.0F, -5.0F, -5.25F, 1, 2, 1, 0.0F, false);
		return temp;
	}

	private RendererModel getBatteryClipsRenderer(int yTextureOffset) {
		RendererModel temp = new RendererModel(this);
		temp.setRotationPoint(0.0F, 24.0F, 0.0F);
		temp.setTextureOffset(24, yTextureOffset).addBox(1.0F, -5.25F, -6.15F, 1, 1, 1, 0.2F, false);
		temp.setTextureOffset(21, yTextureOffset).addBox(1.0F, -1.25F, -6.15F, 1, 1, 1, 0.2F, false);
		return temp;
	}

	@Override
	public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		//noop
	}
}