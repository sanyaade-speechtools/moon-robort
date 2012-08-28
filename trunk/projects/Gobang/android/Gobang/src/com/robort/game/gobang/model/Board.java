package com.robort.game.gobang.model;

import java.util.ArrayList;
import java.util.List;

import com.robort.game.gobang.util.Logger;

public class Board {
	private final static int MAX_SIZE = 19;	// ���������Ĭ��Ϊ19
	public static int SIZE = 15;	// ���̳���
	private int MIN_POS;	// ��С����λ��
	private int MAX_POS;	// �������λ��
	
	public List<Piece> pieces;	// ����˳��
	public Side piecesStatus[][] = new Side[MAX_SIZE][MAX_SIZE];	// �����ϵ�����
	
	public Side curSide;	// ��ǰ���巽
	private Side winner;	// ����ʱ���洢ʤ����
	private Status status;	// ��ֵ�״̬
	
	private Board() {
		MIN_POS = 0;
		MAX_POS = SIZE-1;
		
		pieces = new ArrayList<Piece>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				piecesStatus[i][j] = Side.Empty;
			}
		}
		curSide = Side.Black;
		status = Status.NOT_STARTED;
	}
	// Board is Singleton. There's only one Board in one Game
	private static Board board =  new Board();
	public static Board getInstance() {
		return board;
	}
	
	public void reset() {
		pieces.clear();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				piecesStatus[i][j] = Side.Empty;
			}
		}
		curSide = Side.Black;
		status = Status.PLAYING;
	}
	
	/**
	 * ����ʱ������ʤ����һ��
	 * @return
	 */
	public Side getWinner() {
		if (status == Status.FINISHED)
			return winner;
		return null;
	}
	
	private void setWinner(Side winner) {
		this.winner = winner;
		this.status = Status.FINISHED;
	}
	
	/**
	 * ���ص�ǰ���̵�״̬
	 * @return
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * ����
	 * @param piece
	 */
	public synchronized boolean position(Piece piece) {
		if (status == Status.FINISHED)
			return false;
		
		if (piece == null || isOutOfBound(piece.x, piece.y))
			return false;
		
		if (piecesStatus[piece.x][piece.y] == Side.Empty) {	
			pieces.add(piece);
			piecesStatus[piece.x][piece.y] = piece.side;		
			curSide = Side.reverse(piece.side);
			return true;
		}
		
		return false;
	}
	
	/**
	 * ���壬��ȥ�ղ��µ��ǲ���
	 */
	public void regret() {
		if (status == Status.FINISHED)
			return ;
		
		if(pieces.size() > 0) {
			Piece piece = pieces.remove(pieces.size()-1);
			piecesStatus[piece.x][piece.y] = Side.Empty;
			curSide = piece.side;
		}
	}
	
	/**
	 *  �н��֣�����ʤ�������ж��Ƿ��������ָ��ʤ����
	 * @return
	 */
	public synchronized boolean isFinished() {
		if (status == Status.FINISHED)
			return true;
		
		if (pieces.size() > 0)
			return isFinished(pieces.get(pieces.size()-1));
		else
			return false;
	}
	
	/**
	 *  �н��֣�����ʤ�������ж��Ƿ��������ָ��ʤ����
	 * @param piece
	 * @return
	 */
	private boolean isFinished(Piece piece) {
		if (status == Status.FINISHED)
			return true;
		
		// ���ȣ��ж�ÿ�������ϵ�������/���ӵĸ����������Ƿ�����ӣ��ж�ʤ��
		if (isFive(piece)) {
			setWinner(piece.side);
			return true;
		}

		// ����Ƿ����˽���
		if (isBanned(piece)) {
			setWinner(Side.reverse(piece.side));
			return true;			
		}
		
		return false;	
	}
	
	public boolean isOutOfBound (int x, int y) {
		return (x < 0 || y < 0 || x >= SIZE || y >= SIZE);
	}
	
	public boolean isEmpty(int x, int y) {
		if (isOutOfBound(x, y))	return true;
		return piecesStatus[x][y] == Side.Empty;
	}
	
	/**
	 * �ж����¸���ʱ���Ƿ�Ϊ����
	 * ����ĳЩ���ӵĽ��ֹ��򣬴˴�û�п������ڡ����Ժ�İ汾�����
	 * @param piece
	 * @return
	 */
	public boolean isBanned(Piece piece) {
		// ���ںڷ����ж��Ƿ��н���
		if (Settings.getInstance().banned && piece.side == Side.Black) {
			int[] count_pieces = new int[Constants.rules.DIRECTIONS];
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				try {
					count_pieces[i] = getContinuousPieces(piece, i);
				} catch (Exception e) {
					Logger.e("Get continuous pieces failed!!!");
					e.printStackTrace();
				}

				// �糤�����֣����峬��5����
				if (Settings.getInstance().banned && piece.side == Side.Black && count_pieces[i] > 5) {
					return true;
				}
			}
			
			// ���¼���ÿ�������ϵ�����������
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				try {
					count_pieces[i] = getThreeFour(piece, i);
				} catch (Exception e) {
					Logger.e("Get continuous pieces failed!!!");
					e.printStackTrace();
				}
				// ���������һ�����γɵ�˫���ģ��и�
				if (count_pieces[i] == Constants.rules.ILLEGAL) {
					return true;
				}
			}
			
			int count_four = 0, count_three = 0;
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
				return false;
			
			// ������������Ľ���
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				if (count_pieces[i] == Constants.rules.THREE || count_pieces[i] == Constants.info.THREE_AND_FOUR)
					count_three ++;
			} 
			
			// ������������/���Ľ���/����������/���������֣��кڸ�
			if (count_three > 1 || count_four > 1) {
				return true;
			}
		}
		
		return false;	
	}
	
	/**
	 * �ж������Ƿ������
	 * @param piece
	 * @return
	 */
	public boolean isFive(Piece piece) {
		int[] count_pieces = new int[Constants.rules.DIRECTIONS];
		for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
			try {
				count_pieces[i] = getContinuousPieces(piece, i);
			} catch (Exception e) {
				Logger.e("Get continuous pieces failed!!!");
			}
			
			// ���ӳ����ӣ���ʤ	
			if (count_pieces[i] == 5) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * �õ����Ӻ��ڸ÷����Ͽ��ܵõ�����״
	 * @param piece
	 * @param direction
	 * @return
	 */
	public Pattern.ShapesInLine getShape(Piece piece, int direction) {
		try {
			return getPattern(piece, direction).getShapes();
		} catch (Exception e) {
			Logger.e("Error when getting pattern!!");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ��ĳ�����ӳ������õ�ĳ��������������ͬ��ɫ�����ӵĸ���
	 * @param piece
	 * @param direction
	 * @return
	 * @throws Exception
	 */
	private int getContinuousPieces(Piece piece, int direction) throws Exception {
		int increment_x, increment_y;
		switch(direction) {
		case Constants.direction.WEST:
			increment_x = 1;
			increment_y = 0;
			break;
		case Constants.direction.NORTH:
			increment_x = 0;
			increment_y = 1;
			break;
		case Constants.direction.NORTH_WEST:
			increment_x = 1;
			increment_y = 1;
			break;
		case Constants.direction.SOUTH_WEST:
			increment_x = 1;
			increment_y = -1;
			break;
		default:
			throw new Exception("Undefined direction!!");
		}
		
		int min_x = Math.max(MIN_POS, piece.x - (Constants.rules.FIVE - 1));
		int max_x = Math.min(MAX_POS, piece.x + (Constants.rules.FIVE - 1));
		int min_y = Math.max(MIN_POS, piece.y - (Constants.rules.FIVE - 1));
		int max_y = Math.min(MAX_POS, piece.y + (Constants.rules.FIVE - 1));
		
		int match_num = 1;
		
		// ���㷴����
		for (int x = piece.x - increment_x, y = piece.y - increment_y; 
			x >= min_x && y >= min_y && x <= max_x && y <= max_y; 
			x -= increment_x, y -= increment_y) 
		{
			if (piecesStatus[x][y] == piece.side)
				match_num ++;
			else
				break;
		}
		
		// ����������
		for (int x = piece.x + increment_x, y = piece.y + increment_y; 
			x >= min_x && y >= min_y && x <= max_x && y <= max_y; 
			x += increment_x, y += increment_y) 
		{
			if (piecesStatus[x][y] == piece.side)
				match_num ++;
			else
				break;
		}
		
		return match_num;
	}
	
	/**
	 * ����÷����ϣ���Χ�����Σ�����������ֻ����[-4, 4]��Χ�ڵ�����
	 * @param piece
	 * @param direction
	 * @param pieces_line
	 * @throws Exception
	 */
	private Pattern getPattern(Piece piece, int direction) throws Exception{
		Pattern pattern = new Pattern(piece, direction);
		
		int increment_x, increment_y;
		switch(direction) {
		case Constants.direction.WEST:
			increment_x = 1;
			increment_y = 0;
			break;
		case Constants.direction.NORTH:
			increment_x = 0;
			increment_y = 1;
			break;
		case Constants.direction.NORTH_WEST:
			increment_x = 1;
			increment_y = 1;
			break;
		case Constants.direction.SOUTH_WEST:
			increment_x = 1;
			increment_y = -1;
			break;
		default:
			throw new Exception("Undefined direction!!");
		}
		
		int min_x = Math.max(MIN_POS, piece.x - (Constants.rules.FIVE - 1));
		int max_x = Math.min(MAX_POS, piece.x + (Constants.rules.FIVE - 1));
		int min_y = Math.max(MIN_POS, piece.y - (Constants.rules.FIVE - 1));
		int max_y = Math.min(MAX_POS, piece.y + (Constants.rules.FIVE - 1));
		
		Side [] pieces_neg = new Side[Constants.rules.FIVE - 1];
		Side [] pieces_pos = new Side[Constants.rules.FIVE - 1];
		
		// ������Ҫ�µ���
		pattern.pieces_line[Constants.rules.FIVE - 1] = piece.side;
		
		// �õ������������
		for (int x = piece.x - increment_x, y = piece.y - increment_y, i = 0; 
			x >= min_x && y >= min_y && x <= max_x && y <= max_y; 
			x -= increment_x, y -= increment_y, i++) 
		{
			pattern.pieces_line[Constants.rules.FIVE-2-i] = pieces_neg[i] = piecesStatus[x][y];
		}		
		// �õ������������
		for (int x = piece.x + increment_x, y = piece.y + increment_y, i = 0; 
		x >= min_x && y >= min_y && x <= max_x && y <= max_y; 
			x += increment_x, y += increment_y, i++) 
		{
			pattern.pieces_line[Constants.rules.FIVE+i] = pieces_pos[i] = piecesStatus[x][y];
		}
		
		int match_num = 1;		
		
		// ������߽硢�Ƿ�����������
		int empty_num_neg = 0;
		int first_empty_neg = pieces_neg.length;
		int edge_neg = pieces_neg.length;
		for (int i=0; i<pieces_neg.length; i++) {
			if (pieces_neg[i] == piece.side) {
				match_num ++ ;
			}
			else if (pieces_neg[i] == Side.reverse(piece.side)) {
				edge_neg = i;
				break;
			}
			else {
				// �õ���һ����λ
				if (i < first_empty_neg)	first_empty_neg = i;
				// ����ո�����
				boolean hasOuter = false;
				for (int j=i+1; j<pieces_neg.length; j++)
					if (pieces_neg[j] == piece.side) {
						hasOuter = true;
						break;
					}
				if (hasOuter) {
					empty_num_neg ++ ;	
				}
				else {
					edge_neg = i;
					break;
				}
			}
		}
		
		// �����ұ߽硢�Ƿ�����������
		int empty_num_pos = 0;
		int first_empty_pos = pieces_pos.length;
		int edge_pos = pieces_pos.length;
		for (int i=0; i<pieces_pos.length; i++) {
			if (pieces_pos[i] == piece.side) {
				match_num ++ ;
			}
			else if (pieces_pos[i] == Side.reverse(piece.side)) {
				edge_pos = i;
				break;
			}
			else {
				// �õ���һ����λ
				if (i < first_empty_pos)	first_empty_pos = i;
				// ����ո�����
				boolean hasOuter = false;
				for (int j=i+1; j<pieces_pos.length; j++)
					if (pieces_pos[j] == piece.side) {
						hasOuter = true;
						break;
					}
				if (hasOuter) {
					empty_num_pos ++ ;	
				}
				else {
					edge_pos = i;
					break;
				}
			}
		}
		int empty_num = empty_num_neg + empty_num_pos;
		
		pattern.match_num = match_num;
		pattern.empty_num = empty_num;
		pattern.edge_neg = Constants.rules.FIVE-1-edge_neg;
		pattern.edge_pos = Constants.rules.FIVE-1+edge_pos;
		pattern.first_empty_neg = Constants.rules.FIVE-1-first_empty_neg;
		pattern.first_empty_pos = Constants.rules.FIVE-1+first_empty_pos;
		
		return pattern;
	}
	
	/**
	 * ����÷������Ƿ�������������ġ�����
	 * @param piece
	 * @param direction
	 * @return
	 * @throws Exception
	 */
	private int getThreeFour(Piece piece, int direction) throws Exception {		
		// Get neighboring pieces
		Pattern.ShapesInLine shapes = getPattern(piece, direction).getShapes();
		
		// ������߶��г��ģ���Ϊ���Ľ��֣��ⵣ�󣩣����� ILLEGAL
		if (shapes.hasDoubleRushFour()) {
			return Constants.rules.ILLEGAL;
		}
		
		// ������������ģ��������������ĺ�����������		
		boolean isFour, isThree;
		isFour = shapes.hasLiveOrRunFour();
		isThree = shapes.hasLiveThree();
		
		// ������Ķ���
		if (isFour && isThree)
			return Constants.info.THREE_AND_FOUR;
		// ����л���
		else if (isFour)
			return Constants.rules.FOUR;
		// ����л���
		else if (isThree)
			return Constants.rules.THREE;
					
		return 0;
	}
}
