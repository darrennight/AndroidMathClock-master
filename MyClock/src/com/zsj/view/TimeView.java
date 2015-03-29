package com.zsj.view;

import java.util.Calendar;

import com.example.myclock.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class TimeView extends TextView {

	private TextView tvTime;
	public TimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimeView(Context context) {
		super(context);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvTime.setText("");
		
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			refreshTime();
			if (getVisibility() == View.VISIBLE) {
				handler.sendEmptyMessageDelayed(0, 1000);
			}
		};
	};

	private void refreshTime() {
		Calendar calendar = Calendar.getInstance();
		tvTime.setText(String.format("%d:%d:%d",
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)));
	}
}
