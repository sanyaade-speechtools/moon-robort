package com.robort.game.gobang.player;

import com.robort.game.gobang.model.Board;
import com.robort.game.gobang.model.Piece;
import com.robort.game.gobang.model.Side;
import com.robort.game.gobang.util.Logger;

public class Robot extends Player implements IRobot{

	public Robot(Side side) {
		super(side);
	}
	
	protected int decision_x;
	protected int decision_y;
	
	protected Piece lastPiece;
	
	public void see() {
		lastPiece = board.pieces.get(board.pieces.size()-1);
	}
	
	/**
	 * Think is to find set the value of decision_x and decision_y;
	 */
	public void think() {
		sleep(500);
		
		int x, y;
		do {
			x = (int) (Math.random() * Board.SIZE / 2) + Board.SIZE / 4;
			y = (int) (Math.random() * Board.SIZE / 2) + Board.SIZE / 4;
		}
		while (!board.isEmpty(x, y));
		
		decision_x = x;	decision_y = y;
	}
	
	public void action() {
		game.position(new Piece(side, decision_x, decision_y));
	}
	
	@Override
	public void turn() {
		see();
		think();
		action();
		done();
	}
}
