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
	
	public Side reverse() {
		switch (this) {
		case Empty:
			return Empty;
		case Black:
			return White;
		case White:
			return Black;
		}
		return Empty;
	}
	
	public boolean isWhite() {
		return this.compareTo(Side.White) == 0;
	}
	
	public boolean isBlack() {
		return this.compareTo(Side.Black) == 0;
	}
	
	public boolean isEmpty() {
		return this.compareTo(Side.Empty) == 0;
	}
	
}

