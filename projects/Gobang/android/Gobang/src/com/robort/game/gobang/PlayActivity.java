package com.robort.game.gobang;

import com.robort.game.gobang.model.Constants;
import com.robort.game.gobang.model.Game;
import com.robort.game.gobang.model.Mode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlayActivity extends Activity implements View.OnClickListener{
	Game game;
	
	BoardView boardView;
	Button regretBtn;
	Button resetBtn;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);
        
        Intent intent = getIntent();
        int mode = intent.getIntExtra(Constants.extras.MODE, 0);
        
        // Set Game Mode according to Intent Extra
        game = Game.getInstance();
        game.setMode(Mode.fromInt(mode));
        
        boardView = (BoardView) findViewById(R.id.board);
        boardView.setGame(game);
        game.setBoardView(boardView);
        
        regretBtn =  (Button) findViewById(R.id.play_btn_regret);
        regretBtn.setOnClickListener(this);
        
        resetBtn = (Button) findViewById(R.id.play_btn_reset);
        resetBtn.setOnClickListener(this);
        
        game.start();
    }

	@Override
	protected void onStart() {
		super.onStart();
//		game.start();
		
	}

	@Override
	public void onClick(View v) {
		if (v == regretBtn) {
			game.regret();
		}
		else if (v == resetBtn) {
			game.reset();
		}
	}
}