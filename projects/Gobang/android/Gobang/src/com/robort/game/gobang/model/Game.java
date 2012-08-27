package com.robort.game.gobang.model;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.robort.game.gobang.BoardView;
import com.robort.game.gobang.R;
import com.robort.game.gobang.player.Man;
import com.robort.game.gobang.player.Player;
import com.robort.game.gobang.player.Robot;
import com.robort.game.gobang.util.Logger;

public class Game {
	public BoardView boardView;
	public Board board;
	public Mode mode;
	public Player players[];
	public Player curPlayer;
	
	MediaPlayer media_player;
	private Handler handler;
	
	private Game() {
		board = Board.getInstance();
		players = new Player[Constants.rules.PLAYERS];
		setHandler();
	}

	private static Game game = new Game();
	public static Game getInstance() {
		return game;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
		if (mode == Mode.HUMAN_TO_HUMAN) {
			players[0] = new Man(Side.Black);
			players[1] = new Man(Side.White);
		}
		else if (mode == Mode.HUMAN_TO_BOT) {
			players[0] = new Man(Side.Black);
			players[1] = new Robot(Side.White);
		}
		else if (mode == Mode.BOT_TO_HUMAN) {
			players[0] = new Robot(Side.Black);
			players[1] = new Man(Side.White);
		}
		else if (mode == Mode.BOT_TO_BOT) {
			players[0] = new Robot(Side.Black);
			players[1] = new Robot(Side.White);
		}
	}
	
	public void setBoardView(BoardView boardView) {
		this.boardView = boardView;
	}
	
	public void start() {
		board.reset();
		curPlayer = players[0];
		curPlayer.turn();
	}
	
	public Player getOpponent(Player player) {
		if (player == players[0])
			return players[1];
		else
			return players[0];
	}
	
	public boolean position(Piece piece) {
		if (board.position(piece)) {
			// Force the view to redraw
			boardView.invalidate();
			// Perform the sound of putting piece
			playSound();
			// Check whether the game is finished
			if (board.isFinished())
				Toast.makeText(boardView.getContext(), "Finished: Winner is " + board.getWinner(), Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}
	
	public void regret() {
		board.regret();
		// Force the view to redraw
		boardView.invalidate();
	}
	
	public void reset() {
		board.reset();
		boardView.invalidate();
	}
	
	private void playSound() {
		if (media_player == null)
			media_player = MediaPlayer.create(boardView.getContext(), R.raw.position);
//		else
//			media_player.stop();
		try{
//			media_player.prepare();
			media_player.start();
		}
		catch(Exception e) {
			Logger.e("Playback failed.");
		}
	}
	
	private void setHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Action.DONE) {
					curPlayer = getOpponent(curPlayer);
//					Toast.makeText(game.boardView.getContext(), curPlayer.side + "'s turn", Toast.LENGTH_SHORT).show();
					if (!board.isFinished())
						curPlayer.turn();
				}
				super.handleMessage(msg);
			}
		};
	}

	public Handler getHandler() {
		return handler;
	}
}
