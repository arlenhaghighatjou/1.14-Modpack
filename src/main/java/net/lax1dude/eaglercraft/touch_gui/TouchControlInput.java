package net.lax1dude.eaglercraft.touch_gui;

public class TouchControlInput {
	public int x;
	public int y;
	public final EnumTouchControl control;
	public TouchControlInput(int x, int y, EnumTouchControl control) {
		this.x = x;
		this.y = y;
		this.control = control;
	}
}