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
 * ̰�������Ҿֲ����ţ����ǵĲ���Ϊ2
 * ��ʵ�ϣ��ܶ��˵������߼��Ͳ��Ծ�����ˣ����õ����ۺ���Ҳ����
 * ���ԣ����ۺ���׼ȷ������棩������������̰���㷨�ĳɹ�������
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
		// 1. �Ӽ�������
		// 2. �Ӷ��ֿ��ǣ�����ӶԶ���������ҲӦ��������ֹ
		int posVal = this.evaluateBySide(piece);
		int negVal = this.evaluateBySide(piece.reverseSide());
		if (posVal + negVal > 0) {
			Logger.i(piece + " { pos : " + posVal + " }");
			Logger.i(piece + " { neg : " + negVal + " }");
		}
		return posVal + negVal;
	}
	
	/**
	 * Ŀ�꣺һ����Ϊ�����ģ�������ֵ����
	 * �Ľ������εĽ�һ��ϸ�֣���ֵ��ϸ�֣���ַ�չ�����صȵȡ�
	 * @param piece
	 * @return
	 */
	protected int evaluateBySide(Piece piece) {
		// �ǿ��Ӳ�����
		if (!this.board.isEmpty(piece.x, piece.y))
			return - LEVEL_1;
		
		// ���Ը��֣��������
		if (this.side.isBlack() && board.isBanned(piece))
			return - LEVEL_1;
		
		// ����ʤ�֣�����
		if (this.board.isFive(piece))
			return LEVEL_1;
		
		
		// ����ʣ�ಿ�֣�����������
		int value = DEAFULT_PRIORITY;
		
		// �����ĸ����򣬷ֱ��������
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
			
			// ���ȿ��ǣ�������ʤ�֣���˫���ģ��˴���Ȼֻ�а���᷵��true)
			if (this.side.isWhite() && shapesInDirs[direction].hasDoubleRushFour()) {
				value += LEVEL_1;
			}
		}
		
		// �����ĸ�����ͳ�￼�� / �ȼ���ֵ�Ľ�һ��ϸ��
		
		// 1  ��ʤ��
		
		// ����������ϵ�����
		Directions dirLiveFour =  whereIsShape(shapesInDirs, Constants.shapes.LIVE_FOUR);
		Directions dirRushFour = whereIsShape(shapesInDirs, Constants.shapes.RUSH_FOUR);
		Directions dirLiveThree =  whereIsShape(shapesInDirs, Constants.shapes.LIVE_THREE);
		Directions dirLiveTwo =  whereIsShape(shapesInDirs, Constants.shapes.LIVE_TWO);
		
		// ���磬���ں��壬������ʤ����ֻ��һ������������һ���������ģ����߻��ģ�+L2
		if (this.side.isBlack()) {
			if (dirLiveFour.exists())	// ����
				value += this.LEVEL_2;
			else if (isThreeFourWin(piece))	// ����
				value += this.LEVEL_2;
		}
		
		// ���ڰ��壬����������(˫����)��˫�������Լ�������ʤ�֣�+L2
		if (this.side.isWhite()) {
			if(dirLiveFour.exists()) {				// ����
				value += this.LEVEL_2;
			} else if (! dirLiveThree.equals(dirRushFour)) { // ����
				value += this.LEVEL_2;
			} else if (dirLiveThree.count() > 1) {  // ˫����
				value += this.LEVEL_2;
			}
		}
		
		// 2 ������
		// 2.1 �л����Ȼ���ʱ��+L3
		if (dirLiveThree.exists())
			value += this.LEVEL_3;
		// 2.2 �л���Ȼ���ʱ��+L4 * �������
		if (dirLiveTwo.exists()) {
			int count = 0;
			for (Integer i : dirLiveTwo.dirsList) {
				count += patternsInDirs[i].empty_num;
			}
			value +=  (this.LEVEL_4 * dirLiveTwo.count() - count * this.LEVEL_7);
		}
		// 2.3 �л���Ȼ���ʱ��+L5
		
		return value;
	}
	
	/**
	 * �Ƿ�Ϊ����ʤ�����壩
	 * @param piece
	 * @return
	 */
	protected boolean isThreeFourWin(Piece piece) {
		if (Settings.getInstance().isBanned() && piece.side.isBlack()) {
			int[] count_pieces = new int[Constants.rules.DIRECTIONS];
			
			// ���¼���ÿ�������ϵ�����������
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
			
			// �ų�����ȡʤ�����
			// �����������壩������
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				if (count_pieces[i] == Constants.rules.FOUR || count_pieces[i] == Constants.info.THREE_AND_FOUR)
					count_four ++;
				if (count_pieces[i] == Constants.rules.THREE)
					count_three_only ++;
			}
			// �ж��Ƿ�������ʤ
			if (count_three_only == 1 && count_four == 1)
				return true;
		}
		return false;
	}
	
	
	/**
	 * �ҳ���Ӧ���ͣ����ڵķ���
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
