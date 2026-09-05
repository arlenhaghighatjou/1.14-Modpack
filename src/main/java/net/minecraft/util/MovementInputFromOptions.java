package net.minecraft.util;

import net.lax1dude.eaglercraft.touch_gui.EnumTouchControl;
import net.lax1dude.eaglercraft.touch_gui.TouchControls;
import net.minecraft.client.GameSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void tick(boolean slow, boolean noDampening) {
        this.forwardKeyDown = this.gameSettings.keyBindForward.isKeyDown() || TouchControls.isPressed(EnumTouchControl.DPAD_UP)
                || TouchControls.isPressed(EnumTouchControl.DPAD_UP_LEFT)
                || TouchControls.isPressed(EnumTouchControl.DPAD_UP_RIGHT);
        this.backKeyDown = this.gameSettings.keyBindBack.isKeyDown() || TouchControls.isPressed(EnumTouchControl.DPAD_DOWN);
        this.leftKeyDown = this.gameSettings.keyBindLeft.isKeyDown() || TouchControls.isPressed(EnumTouchControl.DPAD_LEFT)
                || TouchControls.isPressed(EnumTouchControl.DPAD_UP_LEFT);
        this.rightKeyDown = this.gameSettings.keyBindRight.isKeyDown() || TouchControls.isPressed(EnumTouchControl.DPAD_RIGHT)
                || TouchControls.isPressed(EnumTouchControl.DPAD_UP_RIGHT);
        this.moveForward = this.forwardKeyDown == this.backKeyDown ? 0.0F : (float) (this.forwardKeyDown ? 1 : -1);
        this.moveStrafe = this.leftKeyDown == this.rightKeyDown ? 0.0F : (float) (this.leftKeyDown ? 1 : -1);
        this.jump = this.gameSettings.keyBindJump.isKeyDown() || TouchControls.isPressed(EnumTouchControl.JUMP)
                || TouchControls.isPressed(EnumTouchControl.FLY_UP);
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown() || TouchControls.getSneakToggled()
                || TouchControls.isPressed(EnumTouchControl.FLY_DOWN);
        if (!noDampening && (this.sneak || slow)) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }

    }
}
