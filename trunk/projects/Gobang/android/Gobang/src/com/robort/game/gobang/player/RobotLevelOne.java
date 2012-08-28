package com.robort.game.gobang.player;

import com.robort.game.gobang.model.Board;
import com.robort.game.gobang.model.Piece;
import com.robort.game.gobang.model.Side;

/**
 * ���ò�������˼�룬���������Сԭ�������������Alpha-Beta��֦�����Ż�
 * @author Robort
 *
 */
public class RobotLevelOne extends Robot {

	public RobotLevelOne(Side side) {
		super(side);
	}
	
	// ����һ�����ӵ����ȼ���
	public int[][] priorityTable = new int [Board.SIZE][Board.SIZE];
	
	public void think() {
		// ����ÿ�����ӿ����ԣ���������ȼ���
		for (int i=0; i<Board.SIZE; i++) {
			for (int j=0; j<Board.SIZE; j++) {
				priorityTable[i][j] = evaluate(new Piece(board.curSide, i, j));
			}
		}
		
		// ����ѡ��Ϊ���ȼ���ߵ���
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
