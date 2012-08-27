package com.robort.game.gobang;

import com.robort.game.gobang.model.Constants;
import com.robort.game.gobang.model.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class HomeActivity extends Activity implements View.OnClickListener{
	Button ai_button;
	Button human_button;
	Button replay_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.home);
		
		// Initialize GUI
		ai_button = (Button) findViewById(R.id.home_btn_ai);
		ai_button.setOnClickListener(this);
		
		human_button = (Button) findViewById(R.id.home_btn_human);
		human_button.setOnClickListener(this);
		
		replay_button = (Button) findViewById(R.id.home_btn_replay);
		replay_button.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View view) {
		Settings.getInstance().setBanned(true);
		Intent intent =  new Intent(this, PlayActivity.class);
		if (view == human_button) {
			intent.putExtra(Constants.extras.MODE, 0);
			startActivity(intent);
		}
		else if (view == ai_button) {
			intent.putExtra(Constants.extras.MODE, 1);
			startActivity(intent);
		}
	}
	
}
