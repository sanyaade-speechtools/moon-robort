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
	
}
