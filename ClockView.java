package com.zsj.view;

import com.example.myclock.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("HandlerLeak")
public class ClockView extends View {
	/**
	 * 钟盘图片资源
	 */
	private Drawable clockDrawable;
	/**
	 * 钟盘中心点图片资源
	 */
	private Drawable centerDrawable;
	/**
	 * 时针图片资源
	 */
	private Drawable hourDrawable;
	/**
	 * 分针图片资源
	 */
	private Drawable minuteDrawable;
	/**
	 * 秒针图片资源
	 */
	/**
	 * 画笔
	 */
	private Paint paint;
	private Drawable seconddDrawable;

	/**
	 * 是否发生变化
	 */
	private boolean isChange;

	/**
	 * 时间类，用来获取系统的时间
	 */
	private Time time;
	private Thread clockThread;
	public ClockView(Context context) {
		this(context, null);
	}

	public ClockView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 初始化图片资源和画笔
	 */
	public ClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyClockStyleable, defStyle, 0);
		//获取自定义属性
		clockDrawable = ta.getDrawable(R.styleable.MyClockStyleable_clock);
		centerDrawable = ta.getDrawable(R.styleable.MyClockStyleable_center_clock);
		hourDrawable = ta.getDrawable(R.styleable.MyClockStyleable_hour);
		minuteDrawable = ta.getDrawable(R.styleable.MyClockStyleable_minute);
		seconddDrawable = ta.getDrawable(R.styleable.MyClockStyleable_second);
		//释放资源，清注意TypedArray对象是一个shared资源，必须被在使用后进行回收。
		ta.recycle();

		paint = new Paint();
		paint.setColor(Color.parseColor("#000000"));
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setFakeBoldText(true);
		paint.setAntiAlias(true);
		
		time = new Time();
		clockThread = new Thread() {
			public void run() {
				while(isChange){
					postInvalidate();
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		};
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//设置当前的时间
		time.setToNow();
		//计算自定义view 的中心位置
		int viewCenterX = (getRight() - getLeft()) / 2;
		int viewCenterY = (getBottom() - getTop()) / 2;
		final Drawable dial = clockDrawable;
		//获取clockDrawable 的高度和宽度(即图片的高度和宽度)
		int h = dial.getIntrinsicHeight();
		int w = dial.getIntrinsicWidth();
		if ((getRight() - getLeft()) < w || (getBottom() - getTop()) < h) {
			float scale = Math.min((float) (getRight() - getLeft()) / w,
					(float) (getBottom() - getTop()) / h);
			canvas.save();
			canvas.scale(scale, scale, viewCenterX, viewCenterY);
		}
		if (isChange) {
			//设置要
			dial.setBounds(viewCenterX - (w / 2), viewCenterY
					- (h / 2), viewCenterX + (w / 2),
					viewCenterY + (h / 2));
		}
		dial.draw(canvas);
		canvas.save();
		
		if (isChange) {
			paint.setTextSize(40f);
			int textWidth = (int) paint.measureText("12");
			canvas.drawText("12", viewCenterX - (textWidth / 2), (float) (viewCenterY - (h / 3.5)), paint);
			int textWidth1 = (int) paint.measureText("6");
			canvas.drawText("6", viewCenterX - (textWidth1 / 2), (float) (viewCenterY + (h / 2.7)), paint);
			canvas.drawText("9", (float) (viewCenterX - (w / 2.7)), viewCenterY + 10, paint);
			canvas.drawText("3", (float) (viewCenterX + (w / 3)), viewCenterY + 10, paint);
		}
		canvas.save();
		//用canvas 画时针
		canvas.rotate(time.hour / 12.0f * 360.0f, viewCenterX, viewCenterY);
		Drawable mHour = hourDrawable;
		h = mHour.getIntrinsicHeight();
		w= mHour.getIntrinsicWidth();
		if (isChange) {
			mHour.setBounds(viewCenterX - (w / 2), viewCenterY
					- h + 20, viewCenterX + (w / 2),
					viewCenterY + 10);
		}
		mHour.draw(canvas);
		canvas.restore();
		canvas.save();
		//用canvas 话分针
		canvas.rotate(time.minute / 60.0f * 360.0f, viewCenterX, viewCenterY);
		Drawable mMinute = minuteDrawable;
		if (isChange) {
			w = mMinute.getIntrinsicWidth();
			h = mMinute.getIntrinsicHeight();
			mMinute.setBounds(viewCenterX - (w / 2), viewCenterY - h, viewCenterX + (w / 2), viewCenterY + 10);
		}
		mMinute.draw(canvas);
		canvas.restore();
		canvas.save();
		//用canvas 画中心点
		Drawable mCenter = centerDrawable;
		if (isChange) {
			w = mCenter.getIntrinsicWidth();
			h = mCenter.getIntrinsicHeight();
			mCenter.setBounds(viewCenterX - (w / 2), viewCenterY
					- (h / 2), viewCenterX + (w / 2),
					viewCenterY + (h / 2));
		}
		mCenter.draw(canvas);
		canvas.save();
		//用 canvas 画秒针
		canvas.rotate(time.second / 60.0f * 360.0f, viewCenterX, viewCenterY);
		Drawable mSecond = seconddDrawable;
		if (isChange) {
			w = mSecond.getIntrinsicWidth();
			h = mSecond.getIntrinsicHeight();
			mSecond.setBounds(viewCenterX - (w / 2), viewCenterY
					- h + 50, viewCenterX + (w / 2),
					viewCenterY + 50);
		}
		mSecond.draw(canvas);
		canvas.restore();
		canvas.save();
		//Log.e("TAG", "this is onDraw method");
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		isChange = true;
		clockThread.start();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isChange = false;
	}
}
