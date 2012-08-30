package com.robort.game.gobang.model;

public class Settings {
	private Settings(){}
	private static Settings settings = new Settings();
	public static Settings getInstance() {
		return settings;
	}
	
	boolean banned;		// ÊÇ·ñÓĞ½ûÊÖ
	int bot_level;
	int size;
	int time;
	
	public void setBanned(boolean banned) {
		this.banned = banned;
	}
	
	public boolean isBanned() {
		return this.banned;
	}
}
