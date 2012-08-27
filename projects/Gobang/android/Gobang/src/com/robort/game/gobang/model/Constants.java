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
		FIVE/*五连*/, 
		LIVE_FOUR/*活四*/, RUSH_FOUR/*冲四*/, DEAD_FOUR/*死四*/, // DOUBLE_RUSH_FOUR/*双冲四*/,
		LIVE_THREE/*活三*/, ASLEEP_THREE/*眠三*/, DEAD_THREE/*死三*/, // DOUBLE_ASLEEP_THREE/*双眠三*/,
		LIVE_TWO/*活二*/, ASLEEP_TWO/*眠二*/, DEAD_TWO/*死二*/,
		NOT_DEFINED/*未定义*/
	}
	
	public class direction {
		public final static int WEST = 0;	/*东西*/
		public final static int NORTH = 1;	/*南北*/
		public final static int NORTH_WEST = 2;	/*西北-东南*/
		public final static int SOUTH_WEST = 3;	/*西南-东北*/
	}
	
	public class extras {
		public final static String MODE = "com.robort.gobang.mode";	/*模式*/
	}
}
