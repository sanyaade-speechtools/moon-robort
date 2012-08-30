package com.robort.game.gobang.model;

public class Piece {
	public Side side;
	public int x;
	public int y;
	
	public Piece(Side side, int x, int y) {
		super();
		this.side = side;
		this.x = x;
		this.y = y;
	}
	
	public Piece reverseSide() {
		return new Piece(Side.reverse(side), x, y);
	}

	@Override
	public String toString() {
		return "Piece [side=" + side + ", x=" + x + ", y=" + y + "]";
	}
	
}
