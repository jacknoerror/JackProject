package com.jack.qd.ui.shake;

import com.jack.qd.data.Const;
import com.jack.qd.utils.JackUtils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class ShakeHelper  {
	static private ShakeHelper helper;
	public static ShakeHelper getInstance(){
		if(null==helper) helper = new ShakeHelper();
		return helper;
	}
	private ShakeHelper(){};
	
	
	private Context mContext;
	private SensorManager sensorManager;
	private Vibrator vibrator;
	
	private OnShakedListener mShakeHelperListener;

	private static final String TAG = "ShakeHelper";
	/** Called when the activity is first created. */
	public ShakeHelper init(Context context) {
		if(null!=context)mContext = context;
		if(null==mContext) return this;
		sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		return this;
	}

	protected ShakeHelper register() {
		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}
		return this;
	}

	protected void unRegister() {
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	/**
	 * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
//			Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				if(null!=vibrator)vibrator.vibrate(200);
				/*Message msg = new Message();
				msg.what = Const.WHAT_SENSOR_SHAKE;
				if(null!=handler)handler.sendMessage(msg);*/
				if(null!=mShakeHelperListener) mShakeHelperListener.onShaked(ShakeHelper.this);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	
	public final void setShakeHelperListener(OnShakedListener mShakeHelperListener) {
		this.mShakeHelperListener = mShakeHelperListener;
	}


	public interface OnShakedListener{
		public void onShaked(ShakeHelper helper);
	}
	
}
