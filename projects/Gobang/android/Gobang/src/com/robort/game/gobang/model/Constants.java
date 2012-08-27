package com.robort.game.gobang.model;

public class Constants {
	public class rules {
		public final static int ILLEGAL = -1;
		
		public final static int FIVE = 5;
		public final static int FOUR = 4;
		public final static int THREE = 3;
		public final static int TWO = 2;
		
		public final static int DIRECTIONS = 4;
		public final static int PLAYERS = 2;
	}
	
	public class info {
		public final static int THREE_AND_FOUR = 7;
	}
	
	public enum shapes {
		// For the shapes, refer to here: http://www.rifchina.com/qtym/jianjie2.html
		FIVE/*����*/, 
		LIVE_FOUR/*����*/, RUSH_FOUR/*����*/, DEAD_FOUR/*����*/, // DOUBLE_RUSH_FOUR/*˫����*/,
		LIVE_THREE/*����*/, ASLEEP_THREE/*����*/, DEAD_THREE/*����*/, // DOUBLE_ASLEEP_THREE/*˫����*/,
		LIVE_TWO/*���*/, ASLEEP_TWO/*�߶�*/, DEAD_TWO/*����*/,
		NOT_DEFINED/*δ����*/
	}
	
	public class direction {
		public final static int WEST = 0;	/*����*/
		public final static int NORTH = 1;	/*�ϱ�*/
		public final static int NORTH_WEST = 2;	/*����-����*/
		public final static int SOUTH_WEST = 3;	/*����-����*/
	}
	
	public class extras {
		public final static String MODE = "com.robort.gobang.mode";	/*ģʽ*/
	}
}
