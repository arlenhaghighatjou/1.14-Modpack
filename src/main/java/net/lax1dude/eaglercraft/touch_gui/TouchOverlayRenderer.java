/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.lax1dude.eaglercraft.touch_gui;

import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.*;

import java.util.Set;

import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.google.common.collect.Sets;

import net.lax1dude.eaglercraft.PointerInputAbstraction;
import net.lax1dude.eaglercraft.Touch;
import net.lax1dude.eaglercraft.opengl.GameOverlayFramebuffer;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TouchOverlayRenderer {

	public static final ResourceLocation spriteSheet = new ResourceLocation("eagler:gui/touch_gui.png");

	static final int[] _fuck = new int[2];

	private GameOverlayFramebuffer overlayFramebuffer;
	private final Minecraft mc;
	private boolean invalid = false;
	private boolean invalidDeep = false;
	private int currentWidth = -1;
	private int currentHeight = -1;

	public TouchOverlayRenderer(Minecraft mc) {
		this.mc = mc;
		this.overlayFramebuffer = new GameOverlayFramebuffer(false);
		EnumTouchControl.currentLayout = null;
		EnumTouchControl.setLayoutState(this, EnumTouchLayoutState.IN_GUI);
	}

	public void invalidate() {
		invalid = true;
	}

	public void invalidateDeep() {
		invalid = true;
		invalidDeep = true;
	}

	public void render(int w, int h, MainWindow scaledResolution) {
		if(PointerInputAbstraction.isTouchMode() || this.mc.gameSettings.touchscreen || Touch.touchPointCount() > 0) {
			render0(w, h, scaledResolution);
		}
	}

	private void render0(int w, int h, MainWindow scaledResolution) {
		EnumTouchControl.setLayoutState(this, hashLayoutState());
		int sw = scaledResolution.getScaledWidth();
		int sh = scaledResolution.getScaledHeight();
		// Set up orthographic projection for GUI rendering
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(5889); // GL_PROJECTION
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, (double)sw, (double)sh, 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888); // GL_MODELVIEW
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(false);
		// Render controls directly to screen (no separate framebuffer)
		Tessellator tes = Tessellator.getInstance();
		if(tes.getWorldRenderer().isDrawing) tes.draw();
		Set<EnumTouchControl> controlsList = Sets.newHashSet(EnumTouchControl._VALUES);
		for (ObjectCursor<TouchControlInput> input : TouchControls.touchControls.values()) {
			controlsList.remove(input.value.control);
		}
		for (EnumTouchControl control : controlsList) {
			if(control.visible) {
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				control.getRender().call(control, 0, 0, false, mc, scaledResolution);
			}
		}
		for (ObjectCursor<TouchControlInput> input_ : TouchControls.touchControls.values()) {
			TouchControlInput input = input_.value;
			EnumTouchControl control = input.control;
			if(control.visible) {
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				control.getRender().call(control, input.x, input.y, true, mc, scaledResolution);
			}
		}
		// Keyboard zone
		if(EnumTouchControl.KEYBOARD.visible) {
			int[] pos = EnumTouchControl.KEYBOARD.getLocation(scaledResolution, _fuck);
			int scale = (int) scaledResolution.getGuiScaleFactor();
			int size = EnumTouchControl.KEYBOARD.size * scale;
			Touch.touchSetOpenKeyboardZone(pos[0] * scale,
					(scaledResolution.getScaledHeight() - pos[1] - 1) * scale - size, size, size);
		}else {
			Touch.touchSetOpenKeyboardZone(0, 0, 0, 0);
		}
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		// Restore matrices
		GlStateManager.matrixMode(5888); // GL_MODELVIEW
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5889); // GL_PROJECTION
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888); // GL_MODELVIEW
		GlStateManager.popMatrix();
	}

	private EnumTouchLayoutState hashLayoutState() {
		if(mc.currentScreen != null) {
			return mc.currentScreen.showCopyPasteButtons() ? EnumTouchLayoutState.IN_GUI_TYPING
					: (mc.currentScreen.canCloseGui() ? EnumTouchLayoutState.IN_GUI
							: EnumTouchLayoutState.IN_GUI_NO_BACK);
		}
		ClientPlayerEntity player = mc.player;
		if(player != null) {
			if(player.abilities.isFlying) {
				 return showDiagButtons() ? EnumTouchLayoutState.IN_GAME_WALK_FLYING : EnumTouchLayoutState.IN_GAME_FLYING;
			}else {
				if(player.abilities.allowFlying) {
					return showDiagButtons() ? EnumTouchLayoutState.IN_GAME_WALK_CAN_FLY : EnumTouchLayoutState.IN_GAME_CAN_FLY;
				}else {
					return showDiagButtons() ? EnumTouchLayoutState.IN_GAME_WALK : EnumTouchLayoutState.IN_GAME;
				}
			}
		}else {
			return showDiagButtons() ? EnumTouchLayoutState.IN_GAME_WALK : EnumTouchLayoutState.IN_GAME;
		}
	}

	private boolean showDiagButtons() {
		return TouchControls.isPressed(EnumTouchControl.DPAD_UP)
				|| TouchControls.isPressed(EnumTouchControl.DPAD_UP_LEFT)
				|| TouchControls.isPressed(EnumTouchControl.DPAD_UP_RIGHT);
	}

	public static void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV, int scaleFac) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, VertexFormat.POSITION_TEX);
		worldrenderer.pos((double) (xCoord + 0.0F), (double) (yCoord + (float) maxV * scaleFac), 0.0)
				.tex((double) ((float) (minU + 0) * f), (double) ((float) (minV + maxV) * f1)).endVertex();
		worldrenderer.pos((double) (xCoord + (float) maxU * scaleFac), (double) (yCoord + (float) maxV * scaleFac), 0.0)
				.tex((double) ((float) (minU + maxU) * f), (double) ((float) (minV + maxV) * f1)).endVertex();
		worldrenderer.pos((double) (xCoord + (float) maxU * scaleFac), (double) (yCoord + 0.0F), 0.0)
				.tex((double) ((float) (minU + maxU) * f), (double) ((float) (minV + 0) * f1)).endVertex();
		worldrenderer.pos((double) (xCoord + 0.0F), (double) (yCoord + 0.0F), 0.0)
				.tex((double) ((float) (minU + 0) * f), (double) ((float) (minV + 0) * f1)).endVertex();
		tessellator.draw();
	}

}
