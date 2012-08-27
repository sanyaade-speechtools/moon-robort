package com.robort.game.gobang.player;

import android.os.Message;

import com.robort.game.gobang.model.Action;
import com.robort.game.gobang.model.Board;
import com.robort.game.gobang.model.Game;
import com.robort.game.gobang.model.Profile;
import com.robort.game.gobang.model.Side;
import com.robort.game.gobang.util.Logger;

/**
 * ���ֶ������������ֺͻ������ֵĸ���
 * @author Thinkpad
 */

public abstract class Player {
	Side side;		// ִ����ɫ
	Board board;	// ����Ե�����
	Profile profile;	// ������Ϣ
	Game game;		// ��Ϸ��Ϣ
	
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
	
	// �ֵ���Player����
	public abstract void turn();
	
	public void done() {
		Message msg =  new Message();
		msg.what = Action.DONE;
		game.getHandler().sendMessage(msg);
	}
}
