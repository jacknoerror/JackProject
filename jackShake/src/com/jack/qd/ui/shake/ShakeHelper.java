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
		if (sensorManager != null) {// ע�������
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			// ��һ��������Listener���ڶ������������ô��������ͣ�����������ֵ��ȡ��������Ϣ��Ƶ��
		}
		return this;
	}

	protected void unRegister() {
		if (sensorManager != null) {// ȡ��������
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	/**
	 * ������Ӧ����
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// ��������Ϣ�ı�ʱִ�и÷���
			float[] values = event.values;
			float x = values[0]; // x�᷽����������ٶȣ�����Ϊ��
			float y = values[1]; // y�᷽����������ٶȣ���ǰΪ��
			float z = values[2]; // z�᷽����������ٶȣ�����Ϊ��
//			Log.i(TAG, "x�᷽����������ٶ�" + x + "��y�᷽����������ٶ�" + y + "��z�᷽����������ٶ�" + z);
			// һ����������������������ٶȴﵽ40�ʹﵽ��ҡ���ֻ���״̬��
			int medumValue = 19;// ���� i9250��ô�ζ����ᳬ��20��û�취��ֻ����19��
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
