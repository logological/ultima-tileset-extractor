/**
 * 
 */
package com.nothingisreal.ultima;

import java.awt.Color;

/**
 * @author psy
 *
 */
public enum C64Colour {
	BLACK, WHITE, RED, CYAN, PURPLE, GREEN, BLUE, YELLOW, ORANGE, BROWN, LIGHTRED, DARKGREY, GREY, LIGHTGREEN, LIGHTBLUE, LIGHTGREY;

	private final int rgb[] = { 0xff000000, 0xfffdfefc, 0xffbe1a24, 0xff30e6c6, 0xffb41ae2, 0xff1fd21e, 0xff211bae,
			0xffdff60a, 0xffb84104, 0xff6a3304, 0xfffe4a57, 0xff424540, 0xff70746f, 0xff59fe59, 0xff5f53fe,
			0xffa4a7a2 };

	public int getRGB() {
		return rgb[this.ordinal()];
	}

	public Color getColor() {
		return new Color(rgb[this.ordinal()]);
	}
}
