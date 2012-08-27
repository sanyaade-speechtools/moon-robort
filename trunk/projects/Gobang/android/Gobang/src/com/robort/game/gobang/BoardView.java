package com.robort.game.gobang;

import com.robort.game.gobang.model.Board;
import com.robort.game.gobang.model.Game;
import com.robort.game.gobang.model.Piece;
import com.robort.game.gobang.model.Side;
import com.robort.game.gobang.model.Status;
import com.robort.game.gobang.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class BoardView extends View {	
	Bitmap BLACK_PIECE;
	Bitmap WHITE_PIECE;
	
	final static int PADDING = 10;
	final float BORDER = 3.0f;
	final int STAR_GRID = 3;
	
	float gap;
	float start_edge;
	float end_edge;
	float center;
	float piece_radius;
	
	Game game;
	Board board;
	
	{
//		Bitmap BLACK_PIECE = BitmapFactory.decodeResource(getResources(), id)	
		
	}
	
	
	public BoardView(Context context) {
		super(context);
	}
	
	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void refreshBoard() {
		final float H_GAP = (float) (getWidth()-2*PADDING) / (Board.SIZE - 1);
		final float V_GAP = (float) (getHeight()-2*PADDING) / (Board.SIZE - 1);
		gap = Math.min(H_GAP, V_GAP);
		start_edge = PADDING;
		end_edge = PADDING + gap * (Board.SIZE - 1);
		center = (start_edge + end_edge) / 2;
		piece_radius = (gap - 5)/2;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		refreshBoard();
		drawStatus(canvas);
		drawBoard(canvas);
		drawPieces(canvas);
		drawCurrentPosition(canvas);
		drawBannedPosition(canvas);
	}
	
	private void drawStatus(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	protected void drawBoard(Canvas canvas) {		
		// Draw lines
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(2);
		
		// Draw Lines
		for (int i = 0; i < Board.SIZE; i++) {
			canvas.drawLine(start_edge, start_edge + i * gap, end_edge, start_edge + i * gap, paint);
			canvas.drawLine(start_edge + i * gap, start_edge, start_edge + i * gap, end_edge, paint);
		}
		
		// Draw Border
		canvas.drawLine(start_edge-BORDER, start_edge-BORDER, start_edge-BORDER, end_edge+BORDER, paint);
		canvas.drawLine(start_edge-BORDER, end_edge+BORDER, end_edge+BORDER, end_edge+BORDER, paint);
		canvas.drawLine(start_edge-BORDER, start_edge-BORDER, end_edge+BORDER, start_edge-BORDER, paint);
		canvas.drawLine(end_edge+BORDER, start_edge-BORDER, end_edge+BORDER, end_edge+BORDER, paint);
		
		// Draw Stars
		canvas.drawCircle(center, center, 5, paint);
		canvas.drawCircle(start_edge + STAR_GRID * gap, start_edge + STAR_GRID * gap, 5, paint);
		canvas.drawCircle(start_edge + STAR_GRID * gap, end_edge - STAR_GRID * gap, 5, paint);
		canvas.drawCircle(end_edge - STAR_GRID * gap, start_edge + STAR_GRID * gap, 5, paint);
		canvas.drawCircle(end_edge - STAR_GRID * gap, end_edge - STAR_GRID * gap, 5, paint);
	}
	
	protected void drawPieces(Canvas canvas) {
		if (board != null && board.pieces.size() > 0) {
			for (Piece piece: board.pieces)
				drawPiece(canvas, piece);
		}
	}
	
	protected void drawPiece(Canvas canvas, Piece piece) {
		Paint paint = new Paint();
		if (piece.side == Side.Black)
			paint.setColor(Color.BLACK);
		else if (piece.side == Side.White)
			paint.setColor(Color.WHITE);
		else
			return ;
		canvas.drawCircle(start_edge + gap * piece.x, start_edge + gap * piece.y, piece_radius, paint);
	}
	
	protected void drawCurrentPosition(Canvas canvas) {
		if (board != null && board.pieces.size() > 0) { 
			Piece piece = board.pieces.get(board.pieces.size()-1);
			Paint paint = new Paint();
			paint.setColor(Color.GREEN);
			paint.setStrokeWidth(2);
			canvas.drawLine(start_edge + gap * piece.x - piece_radius/2, start_edge + gap * piece.y, 
					start_edge + gap * piece.x + piece_radius/2, start_edge + gap * piece.y, paint);
			canvas.drawLine(start_edge + gap * piece.x, start_edge + gap * piece.y - piece_radius/2, 
					start_edge + gap * piece.x, start_edge + gap * piece.y + piece_radius/2, paint);
		}
	}
	protected void drawBannedPosition(Canvas canvas) {
		if (board != null && board.curSide == Side.Black) {
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStrokeWidth(2);
			for (int i=0; i<Board.SIZE; i++) {
				for (int j=0; j<Board.SIZE; j++) {
					if (board.isEmpty(i, j) && board.isBanned(new Piece(Side.Black, i, j))) {
						canvas.drawLine(start_edge + gap * i - piece_radius/2, start_edge + gap * j - piece_radius/2, 
								start_edge + gap * i + piece_radius/2, start_edge + gap * j + piece_radius/2, paint);
						canvas.drawLine(start_edge + gap * i - piece_radius/2, start_edge + gap * j + piece_radius/2, 
								start_edge + gap * i + piece_radius/2, start_edge + gap * j - piece_radius/2, paint);
					}
				}
			}
		}
	}
	
	public void setGame(Game game) {
		this.game = game;
		this.board = game.board;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (board.getStatus() == Status.FINISHED)
			return false;
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = getPositionFromCoord(event.getX());
			int y = getPositionFromCoord(event.getY());
			game.position(new Piece(board.curSide, x, y));
			// Force the view to redraw
			this.invalidate();
			// Notify the current player is done
			game.curPlayer.done();
			break;
		}
		
		return true;
	}
	
	private int getPositionFromCoord(float coord) {
		return Math.round((coord - start_edge) / gap);
	}
}
