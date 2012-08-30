package com.robort.game.gobang.player;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import com.robort.game.gobang.model.Board;
import com.robort.game.gobang.model.Constants;
import com.robort.game.gobang.model.Game;
import com.robort.game.gobang.model.Pattern;
import com.robort.game.gobang.model.Piece;
import com.robort.game.gobang.model.Settings;
import com.robort.game.gobang.model.Side;
import com.robort.game.gobang.util.Logger;

/**
 * 贪婪，查找局部最优，考虑的步数为2
 * 事实上，很多人的弈棋逻辑和策略就是如此，采用的评价函数也类似
 * 所以，评价函数准确与否（明辨），决定了这种贪心算法的成功可能性
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
		// 1. 从己方考虑
		// 2. 从对手考虑，如该子对对手有利，也应当落子阻止
		int posVal = this.evaluateBySide(piece);
		int negVal = this.evaluateBySide(piece.reverseSide());
		if (posVal + negVal > 0) {
			Logger.i(piece + " { pos : " + posVal + " }");
			Logger.i(piece + " { neg : " + negVal + " }");
		}
		return posVal + negVal;
	}
	
	/**
	 * 目标：一个较为完美的，单步估值函数
	 * 改进：棋形的进一步细分，估值的细分，棋局发展，攻守等等。
	 * @param piece
	 * @return
	 */
	protected int evaluateBySide(Piece piece) {
		// 非空子不能下
		if (!this.board.isEmpty(piece.x, piece.y))
			return - LEVEL_1;
		
		// 绝对负手：黑棋禁手
		if (this.side.isBlack() && board.isBanned(piece))
			return - LEVEL_1;
		
		// 绝对胜手：活五
		if (this.board.isFive(piece))
			return LEVEL_1;
		
		
		// 对于剩余部分，计算其评分
		int value = DEAFULT_PRIORITY;
		
		// 对于四个方向，分别计算棋形
		Pattern.ShapesInDirection[] shapesInDirs =  new Pattern.ShapesInDirection[Constants.rules.DIRECTIONS];
		Pattern[] patternsInDirs = new Pattern[Constants.rules.DIRECTIONS];
		for (int direction = 0; direction < Constants.rules.DIRECTIONS; direction++) {
			shapesInDirs[direction] 
			            = this.board.getShapesInDirection(piece, direction);
			try {
				patternsInDirs[direction] = this.board.getPattern(piece, direction);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 首先考虑，单方向胜手，即双冲四（此处显然只有白棋会返回true)
			if (this.side.isWhite() && shapesInDirs[direction].hasDoubleRushFour()) {
				value += LEVEL_1;
			}
		}
		
		// 对于四个方向，统筹考虑 / 等级分值的进一步细分
		
		// 1  致胜手
		
		// 抽象各方向上的棋形
		Directions dirLiveFour =  whereIsShape(shapesInDirs, Constants.shapes.LIVE_FOUR);
		Directions dirRushFour = whereIsShape(shapesInDirs, Constants.shapes.RUSH_FOUR);
		Directions dirLiveThree =  whereIsShape(shapesInDirs, Constants.shapes.LIVE_THREE);
		Directions dirLiveTwo =  whereIsShape(shapesInDirs, Constants.shapes.LIVE_TWO);
		
		// 比如，对于黑棋，有四三胜，即只有一个方向有三，一个方向有四，或者活四，+L2
		if (this.side.isBlack()) {
			if (dirLiveFour.exists())	// 活四
				value += this.LEVEL_2;
			else if (isThreeFourWin(piece))	// 四三
				value += this.LEVEL_2;
		}
		
		// 对于白棋，四三，活四(双活四)，双活三，以及其他等胜手，+L2
		if (this.side.isWhite()) {
			if(dirLiveFour.exists()) {				// 活四
				value += this.LEVEL_2;
			} else if (! dirLiveThree.equals(dirRushFour)) { // 四三
				value += this.LEVEL_2;
			} else if (dirLiveThree.count() > 1) {  // 双活三
				value += this.LEVEL_2;
			}
		}
		
		// 2 过渡手
		// 2.1 有活三等机会时，+L3
		if (dirLiveThree.exists())
			value += this.LEVEL_3;
		// 2.2 有活二等机会时，+L4 * 活二个数
		if (dirLiveTwo.exists()) {
			int count = 0;
			for (Integer i : dirLiveTwo.dirsList) {
				count += patternsInDirs[i].empty_num;
			}
			value +=  (this.LEVEL_4 * dirLiveTwo.count() - count * this.LEVEL_7);
		}
		// 2.3 有活二等机会时，+L5
		
		return value;
	}
	
	/**
	 * 是否为四三胜（黑棋）
	 * @param piece
	 * @return
	 */
	protected boolean isThreeFourWin(Piece piece) {
		if (Settings.getInstance().isBanned() && piece.side.isBlack()) {
			int[] count_pieces = new int[Constants.rules.DIRECTIONS];
			
			// 重新计算每个方向上的连续棋子数
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				try {
					count_pieces[i] = this.board.getThreeFour(piece, i);
				} catch (Exception e) {
					Logger.e("Get continuous pieces failed!!!");
					e.printStackTrace();
				}
			}
			
			int count_four = 0;
			int count_three_only = 0;
			
			// 排除四三取胜的情况
			// 计算活三、活（冲）四数量
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				if (count_pieces[i] == Constants.rules.FOUR || count_pieces[i] == Constants.info.THREE_AND_FOUR)
					count_four ++;
				if (count_pieces[i] == Constants.rules.THREE)
					count_three_only ++;
			}
			// 判断是否是四三胜
			if (count_three_only == 1 && count_four == 1)
				return true;
		}
		return false;
	}
	
	
	/**
	 * 找出相应类型，所在的方向
	 */
	protected Directions whereIsShape(Pattern.ShapesInDirection[] shapesInDirs, Constants.shapes shape) {
		List<Integer> dirsList = new ArrayList<Integer>();
		for (int i=0; i<Constants.rules.DIRECTIONS; i++) {
			for (Constants.shapes shp : shapesInDirs[i].getShapes()) {
				if (shp == shape) {
					dirsList.add(i);
					break;
				}
			}
		}
		return new Directions(dirsList);
	}
	
	class Directions {
		List<Integer> dirsList;
		
		public Directions(List<Integer> directions) {
			super();
			this.dirsList = directions;
		}

		boolean exists() {
			return dirsList.size() > 0;
		}
		
		int count() {
			return dirsList.size();
		}
		
		boolean intersect(Directions anotherDirs) {
			for (int i=0, j=0; i<dirsList.size() && j<anotherDirs.dirsList.size(); ) {
				if (dirsList.get(i) < anotherDirs.dirsList.get(j))
					i++;
				else if (dirsList.get(i) > anotherDirs.dirsList.get(j))
					j++;
				else
					return true;
			}
			return false;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Directions) {
				Directions anotherDirs = (Directions) o;
				if (dirsList.size() != anotherDirs.dirsList.size())
					return false;
				for (int i=0; i<dirsList.size(); i++) {
					if (dirsList.get(i) != anotherDirs.dirsList.get(i))
						return false;
				}
				return true;
			}
			else {
				return super.equals(o);
			}
		}
	}
}
