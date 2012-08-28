package com.robort.game.gobang.model;

import java.util.ArrayList;
import java.util.List;

import com.robort.game.gobang.util.Logger;

public class Board {
	private final static int MAX_SIZE = 19;	// 棋盘最大宽度默认为19
	public static int SIZE = 15;	// 棋盘长宽
	private int MIN_POS;	// 最小落子位置
	private int MAX_POS;	// 最大落子位置
	
	public List<Piece> pieces;	// 落子顺序
	public Side piecesStatus[][] = new Side[MAX_SIZE][MAX_SIZE];	// 棋盘上的棋子
	
	public Side curSide;	// 当前下棋方
	private Side winner;	// 结束时，存储胜利方
	private Status status;	// 棋局的状态
	
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
	 * 结束时，返回胜利的一方
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
	 * 返回当前棋盘的状态
	 * @return
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * 落子
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
	 * 悔棋，消去刚才下的那步棋
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
	 *  有禁手，根据胜负条件判断是否结束，并指定胜利方
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
	 *  有禁手，根据胜负条件判断是否结束，并指定胜利方
	 * @param piece
	 * @return
	 */
	private boolean isFinished(Piece piece) {
		if (status == Status.FINISHED)
			return true;
		
		// 首先，判断每个方向上的连续黑/白子的个数，根据是否成五子，判断胜负
		if (isFive(piece)) {
			setWinner(piece.side);
			return true;
		}

		// 检查是否下了禁手
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
	 * 判断落下该子时，是否为禁手
	 * 对于某些复杂的禁手规则，此处没有考虑在内。在以后的版本中添加
	 * @param piece
	 * @return
	 */
	public boolean isBanned(Piece piece) {
		// 对于黑方，判断是否有禁手
		if (Settings.getInstance().banned && piece.side == Side.Black) {
			int[] count_pieces = new int[Constants.rules.DIRECTIONS];
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				try {
					count_pieces[i] = getContinuousPieces(piece, i);
				} catch (Exception e) {
					Logger.e("Get continuous pieces failed!!!");
					e.printStackTrace();
				}

				// 如长连禁手（黑棋超过5个）
				if (Settings.getInstance().banned && piece.side == Side.Black && count_pieces[i] > 5) {
					return true;
				}
			}
			
			// 重新计算每个方向上的连续棋子数
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				try {
					count_pieces[i] = getThreeFour(piece, i);
				} catch (Exception e) {
					Logger.e("Get continuous pieces failed!!!");
					e.printStackTrace();
				}
				// 对于特殊的一行中形成的双活四，判负
				if (count_pieces[i] == Constants.rules.ILLEGAL) {
					return true;
				}
			}
			
			int count_four = 0, count_three = 0;
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
				return false;
			
			// 计算包含活三的禁手
			for (int i = 0; i < Constants.rules.DIRECTIONS; i++) {
				if (count_pieces[i] == Constants.rules.THREE || count_pieces[i] == Constants.info.THREE_AND_FOUR)
					count_three ++;
			} 
			
			// 如有三三禁手/四四禁手/四三三禁手/四四三禁手，判黑负
			if (count_three > 1 || count_four > 1) {
				return true;
			}
		}
		
		return false;	
	}
	
	/**
	 * 判断落子是否成五连
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
			
			// 落子成五子，则胜	
			if (count_pieces[i] == 5) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 得到落子后在该方向上可能得到的形状
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
	 * 从某个落子出发，得到某个方向，连续的相同颜色的落子的个数
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
		
		// 先算反方向
		for (int x = piece.x - increment_x, y = piece.y - increment_y; 
			x >= min_x && y >= min_y && x <= max_x && y <= max_y; 
			x -= increment_x, y -= increment_y) 
		{
			if (piecesStatus[x][y] == piece.side)
				match_num ++;
			else
				break;
		}
		
		// 再算正方向
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
	 * 计算该方向上，周围的棋形；对于五子棋只计算[-4, 4]范围内的棋子
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
		
		// 设置正要下的子
		pattern.pieces_line[Constants.rules.FIVE - 1] = piece.side;
		
		// 得到反方向的棋形
		for (int x = piece.x - increment_x, y = piece.y - increment_y, i = 0; 
			x >= min_x && y >= min_y && x <= max_x && y <= max_y; 
			x -= increment_x, y -= increment_y, i++) 
		{
			pattern.pieces_line[Constants.rules.FIVE-2-i] = pieces_neg[i] = piecesStatus[x][y];
		}		
		// 得到正方向的棋形
		for (int x = piece.x + increment_x, y = piece.y + increment_y, i = 0; 
		x >= min_x && y >= min_y && x <= max_x && y <= max_y; 
			x += increment_x, y += increment_y, i++) 
		{
			pattern.pieces_line[Constants.rules.FIVE+i] = pieces_pos[i] = piecesStatus[x][y];
		}
		
		int match_num = 1;		
		
		// 计算左边界、是否被阻拦、空数
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
				// 得到第一个空位
				if (i < first_empty_neg)	first_empty_neg = i;
				// 计算空格数量
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
		
		// 计算右边界、是否被阻拦、空数
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
				// 得到第一个空位
				if (i < first_empty_pos)	first_empty_pos = i;
				// 计算空格数量
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
	 * 计算该方向上是否包含活三、活四、冲四
	 * @param piece
	 * @param direction
	 * @return
	 * @throws Exception
	 */
	private int getThreeFour(Piece piece, int direction) throws Exception {		
		// Get neighboring pieces
		Pattern.ShapesInLine shapes = getPattern(piece, direction).getShapes();
		
		// 如果两边都有冲四，则为四四禁手（扁担阵），返回 ILLEGAL
		if (shapes.hasDoubleRushFour()) {
			return Constants.rules.ILLEGAL;
		}
		
		// 计算活三、活四：包括连三、连四和跳三、跳四		
		boolean isFour, isThree;
		isFour = shapes.hasLiveOrRunFour();
		isThree = shapes.hasLiveThree();
		
		// 如果三四都有
		if (isFour && isThree)
			return Constants.info.THREE_AND_FOUR;
		// 如果有活四
		else if (isFour)
			return Constants.rules.FOUR;
		// 如果有活三
		else if (isThree)
			return Constants.rules.THREE;
					
		return 0;
	}
}
