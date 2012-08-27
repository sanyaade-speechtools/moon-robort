package com.robort.game.gobang.player;

import android.os.Message;

import com.robort.game.gobang.model.Action;
import com.robort.game.gobang.model.Board;
import com.robort.game.gobang.model.Game;
import com.robort.game.gobang.model.Profile;
import com.robort.game.gobang.model.Side;
import com.robort.game.gobang.util.Logger;

/**
 * 棋手对象，是人类棋手和机器棋手的父类
 * @author Thinkpad
 */

public abstract class Player {
	Side side;		// 执子颜色
	Board board;	// 所面对的棋盘
	Profile profile;	// 棋手信息
	Game game;		// 游戏信息
	
	public Player(Side side) {
		this.side = side;
		this.game = Game.getInstance();
		this.board = game.board;
	}
	
	protected void sleep(int time) {
		try {
			Logger.i("sleep " + time + " milliseconds");
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// 轮到该Player下棋
	public abstract void turn();
	
	public void done() {
		Message msg =  new Message();
		msg.what = Action.DONE;
		game.getHandler().sendMessage(msg);
	}
}
