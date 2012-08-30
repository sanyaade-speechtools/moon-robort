package com.robort.game.gobang.model;

/**
 * ���ڷ������β��洢�������
 * @author Thinkpad
 */
public class Pattern {
	public Piece piece;
	public int direction;
	public Side[] pieces_line;
	public int match_num;
	public int empty_num;
	public int edge_neg;
	public int edge_pos;
	public int first_empty_neg;
	public int first_empty_pos;
	
	private ShapesInDirection shapes;
	private Constants.shapes left_shape;
	private Constants.shapes right_shape;
	private Constants.shapes mid_cont_shape;
	
	public Pattern(Piece piece, int direction) {
		this.pieces_line = new Side[Board.SIZE*2-1];
		this.piece = piece;
		this.direction = direction;
		
		shapes = null;
		left_shape = right_shape = mid_cont_shape = null;
	}
	
	public Pattern.ShapesInDirection getShapes() {
		if (shapes == null) {
			if (this.empty_num == 0)
				shapes = new ShapesInDirection(1, getMiddleContShape());
			else if (this.empty_num == 1)
				shapes = new ShapesInDirection(2, getLeftJumpShape(), getRightJumpShape());
			else
				shapes = new ShapesInDirection(3, getLeftJumpShape(), getMiddleContShape(), getRightJumpShape());
		}
		return shapes;	
	}
	
	protected Constants.shapes getLeftJumpShape() {
		if (left_shape == null) {
			left_shape = getBasicShape(edge_neg, first_empty_pos);
		}
		return left_shape;
	}
	
	protected Constants.shapes getRightJumpShape() {
		if (right_shape == null) {
			right_shape = getBasicShape(first_empty_neg, edge_pos);
		}
		return right_shape;
	}
	
	protected Constants.shapes getMiddleContShape() {
		if (mid_cont_shape == null) {
			mid_cont_shape = getBasicShape(first_empty_neg, first_empty_pos);
		}
		return mid_cont_shape;
	}
	
	/**
	 * �������ڣ��õ����εĹؼ��Բ������磬ͬɫ�����Ӹ������ո�������������Ŀ
	 * @param start
	 * @param end
	 * @return
	 */
	protected ShapeParams getShapeParams(int start, int end) {
		ShapeParams shapeParams = new ShapeParams();
		// ��ͬ��ɫ���������м�����������߱���������
		for (int i=start; i<=end; i++) {
			if (pieces_line[i] == Side.reverse(piece.side))
				break;
			else if (pieces_line[i] == piece.side) {
				shapeParams.match_count ++;
			}
			else {
				shapeParams.empty_count ++;
			}
		}
		if (start > 0 && pieces_line[start-1] == Side.reverse(piece.side))
			shapeParams.block_count ++ ;
		if (end < pieces_line.length-1 && pieces_line[end+1] == Side.reverse(piece.side))
			shapeParams.block_count ++ ;
		return shapeParams;
	}
	
	/**
	 * �õ���Χ�ڵĻ������Σ��������λᱻ���ڷ����÷����ϵ���������
	 * @param start
	 * @param end
	 * @return
	 */
	protected Constants.shapes getBasicShape(int start, int end) {
		ShapeParams params = getShapeParams(start, end);
		
		if (isLiveFour(params))
			return Constants.shapes.LIVE_FOUR;
		else if (isRushFour(params))
			return Constants.shapes.RUSH_FOUR;
		else if (isLiveThree(params))
			return Constants.shapes.LIVE_THREE;
		else if (isAsleepThree(params))
			return Constants.shapes.ASLEEP_THREE;
		else if (isLiveTwo(params))
			return Constants.shapes.LIVE_TWO;
		else if (isAsleepTwo(params))
			return Constants.shapes.ASLEEP_TWO;
		else if (isDeadFour(params))
			return Constants.shapes.DEAD_FOUR;
		else if (isDeadThree(params))
			return Constants.shapes.DEAD_THREE;
		else if (isDeadTwo(params))
			return Constants.shapes.DEAD_TWO;
		else if (isFive(params))
			return Constants.shapes.FIVE;
		else if (isLongCont(params))
			return Constants.shapes.LONG_CONT;
		return Constants.shapes.NOT_DEFINED;
	}
	

	
	/**
	 * �ж������Ƿ���壬����������
	 * @param params
	 * @return
	 */
	protected boolean isFive(ShapeParams params) {
		if (params.match_count == Constants.rules.FIVE
				&& params.empty_count == 0)
			return true;
		return false;
	}
	
	/**
	 * �ж��Ƿ�ɳ����������������ӳ������
	 * @param params
	 * @return
	 */
	protected boolean isLongCont(ShapeParams params) {
		if (params.match_count > Constants.rules.FIVE
				&& params.empty_count == 0)
			return true;
		return false;
	}
	
	/**
	 * �ж������Ƿ�ɻ���
	 * @param piece
	 * @return
	 */
	protected boolean isLiveFour(ShapeParams params) {
		if (params.match_count == Constants.rules.FOUR
			&& params.empty_count == 0
			&& params.block_count == 0)
			return true;
		return false;
	}
	
	/**
	 * �ж������Ƿ�ɳ���
	 * ������/������
	 * @param piece
	 * @return
	 */
	protected boolean isRushFour(ShapeParams params) {
		if (params.match_count == Constants.rules.FOUR 
				&& (params.empty_count == 0 && params.block_count == 1 	// ������
						|| params.empty_count == 1))	// ������
			return true;
		return false;
	}
	
	
	/**
	 * �ж������Ƿ������
	 * @param piece
	 * @return
	 */
	protected boolean isDeadFour(ShapeParams params) {
		if (params.match_count == Constants.rules.FOUR 
				&& (params.empty_count == 0 && params.block_count == 2))
			return true;
		return false;
	}
	
	
	/**
	 * �ж��Ƿ�ɻ���
	 * ������/������
	 * @param piece
	 * @return
	 */
	protected boolean isLiveThree(ShapeParams params) {
		if (params.match_count == Constants.rules.THREE
				&& params.empty_count < 2 && params.block_count == 0)
			return true;
		return false;
	}
	
	/**
	 * �ж��Ƿ������
	 * ������/������/������/������/˫����
	 * ��������˫����δ����
	 * @param piece
	 * @return
	 */
	protected boolean isAsleepThree(ShapeParams params) {
		if (params.match_count == Constants.rules.THREE
				&& (params.empty_count < 2 && params.block_count == 1	// ��������������
					|| params.empty_count == 2 && params.block_count < 2))	// ������
			return true;
		return false;
	}
	
	/**
	 * �ж������Ƿ������
	 * @param piece
	 * @return
	 */
	protected boolean isDeadThree(ShapeParams params) {
		if (params.match_count == Constants.rules.THREE
				&& (params.empty_count < 2 && params.block_count == 2))
			return true;
		return false;
	}
	
	/**
	 * �ж��Ƿ�ɻ��
	 * �����/�����/�������
	 * @param piece
	 * @return
	 */
	protected boolean isLiveTwo(ShapeParams params) {
		if (params.match_count == Constants.rules.TWO
				&& params.empty_count < 3 && params.block_count == 0)	// �����/�����/�������
			return true;
		return false;
	}
	
	/**
	 * �ж��Ƿ���߶�
	 * ���߶�/���߶�/�����߶�/���߶�/���߶�
	 * ���߶��˴�Ϊ����
	 * @param piece
	 * @return
	 */
	protected boolean isAsleepTwo(ShapeParams params) {
		if (params.match_count == Constants.rules.TWO) {
			if (params.empty_count < 3 && params.block_count == 1	// �����/�����/�������
				|| params.empty_count == 3		// ���߶�
			)
				return true;
		}	
		return false;
	}
	
	/**
	 * �ж������Ƿ������
	 * @param piece
	 * @return
	 */
	protected boolean isDeadTwo(ShapeParams params) {
		if (params.match_count == Constants.rules.TWO
				&& (params.empty_count < 3 && params.block_count == 2))
			return true;
		return false;
	}
	
	class ShapeParams {
		public int match_count;		// ��״�У�ͬɫ������
		public int empty_count;		// ��״�У��ڲ��ո������
		public int block_count;		// ��״���ˣ����Է����������ĸ�����x | [0,1,2]
		
		public ShapeParams() {
			match_count = empty_count = block_count = 0;
		}
	}
	
	public class ShapesInDirection{
		Constants.shapes [] shapes;
		int count;
		
		public ShapesInDirection(int count, Constants.shapes... shapes) {
			this.count = count;
			this.shapes = shapes;
		}
		
		public int getShapesCount() {
			return count;
		}
		
		public Constants.shapes[] getShapes() {
			return shapes;
		}
		
		/**
		 * �Ƿ���˫����
		 * @return
		 */
		public boolean hasDoubleRushFour() {
			if (count > 2 && shapes[0] == Constants.shapes.RUSH_FOUR
				&& shapes[2] == Constants.shapes.RUSH_FOUR)
				return true;
			return false;
		}
		
		/**
		 * �Ƿ��г��Ļ����
		 * @return
		 */
		public boolean hasLiveOrRunFour() {
			for (Constants.shapes shape : shapes) {
				if (shape == Constants.shapes.LIVE_FOUR || shape == Constants.shapes.RUSH_FOUR)
					return true;
			}
			return false;
		}
		
		public boolean hasLiveThree() {
			for (Constants.shapes shape : shapes) {
				if (shape == Constants.shapes.LIVE_THREE)
					return true;
			}
			return false;
		}
	}
}
