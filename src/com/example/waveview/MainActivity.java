package com.example.waveview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button startButton;

	private Button stopButton;

	private WaveView myView;

	private WaveView myViewTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startButton = (Button) findViewById(R.id.start);
		stopButton = (Button) findViewById(R.id.stop);
		myView = (WaveView) findViewById(R.id.myView);
		myView.setColor(Color.parseColor("#00CCFF"));
		myViewTest = (WaveView) findViewById(R.id.test);
		myViewTest.setColor(Color.parseColor("#00CCFF"));
		myViewTest.setInterpolator(new AccelerateInterpolator());
		startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				myView.start();
				myViewTest.start();
			}
		});
		
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				myView.stop();
				myViewTest.stop();
			}
		});

	}
}
