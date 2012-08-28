package com.robort.game.gobang.player;

import com.robort.game.gobang.model.Board;
import com.robort.game.gobang.model.Piece;
import com.robort.game.gobang.model.Side;

/**
 * 采用博弈树的思想，利用最大最小原理进行搜索，用Alpha-Beta剪枝进行优化
 * @author Robort
 *
 */
public class RobotLevelOne extends Robot {

	public RobotLevelOne(Side side) {
		super(side);
	}
	
	// 对下一步落子的优先级表
	public int[][] priorityTable = new int [Board.SIZE][Board.SIZE];
	
	public void think() {
		// 遍历每个落子可能性，计算出优先级表
		for (int i=0; i<Board.SIZE; i++) {
			for (int j=0; j<Board.SIZE; j++) {
				priorityTable[i][j] = evaluate(new Piece(board.curSide, i, j));
			}
		}
		
		// 落子选择为优先级最高的子
		int max_priority = Integer.MIN_VALUE;
		for (int i=0; i<Board.SIZE; i++) {
			for (int j=0; j<Board.SIZE; j++) {
				if (priorityTable[i][j] > max_priority) {
					max_priority = priorityTable[i][j];
					this.decision_x = i;
					this.decision_y  = j;
				}
			}
		}
	}
	
	protected final int DEAFULT_PRIORITY = 0;
	protected final int LEVEL_1 = 10000000;
	protected final int LEVEL_2 = 1000000;
	protected final int LEVEL_3 = 100000;
	protected final int LEVEL_4 = 10000;
	protected final int LEVEL_5 = 1000;
	protected final int LEVEL_6 = 100;
	protected final int LEVEL_7 = 10;
	protected final int LEVEL_8 = 1;
	
	protected int evaluate(Piece piece) {
		int value = DEAFULT_PRIORITY;

		return 0;
	}
	
}
