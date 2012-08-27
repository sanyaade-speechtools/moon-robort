package com.robort.game.gobang.model;

public enum Mode {
	HUMAN_TO_HUMAN, HUMAN_TO_BOT, BOT_TO_HUMAN, BOT_TO_BOT;
	
	public static Mode fromInt(int i) {
		switch(i) {
		case 0:	
			return HUMAN_TO_HUMAN;
		case 1:
			return HUMAN_TO_BOT;
		case 2:
			return BOT_TO_HUMAN;
		case 3:
			return BOT_TO_BOT;
		}
		return HUMAN_TO_HUMAN;
	}
}
