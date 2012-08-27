package com.robort.game.gobang.model;

public enum Side {
	Empty, Black, White;
	
	public static Side reverse(Side side) {
		if (side == Side.Black)
			return Side.White;
		if (side == Side.White)
			return Side.Black;
		return Side.Empty;
	}
}
