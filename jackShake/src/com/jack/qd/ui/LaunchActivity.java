package com.jack.qd.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.jack.qd.R;
import com.jack.qd.ui.shake.ShakeService;
import com.jack.qd.utils.ServiceUtil;

public class LaunchActivity extends Activity implements OnCheckedChangeListener {
	private static final String COM_SERVICE_SHAKE_NAME = "com.demo.SERVICE_SHAKE";
	final String TAG = getClass().getSimpleName();
	private CheckBox mCb_Service;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCb_Service = (CheckBox) this.findViewById(R.id.cb_service_switch);
		mCb_Service.setOnCheckedChangeListener(this);
		
		
	}
	
	@Override
	protected void onResume() {
		//检查服务是否已经启动
//		mCb_Service.setChecked(ServiceUtil.isServiceStarted(this, COM_SERVICE_SHAKE_NAME));
		super.onResume();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cb_service_switch:
			Log.i(TAG, isChecked+"");
			if(isChecked){
				//start
				startService(new Intent(COM_SERVICE_SHAKE_NAME)); 
			}else{
				//stop
				stopService(new Intent(COM_SERVICE_SHAKE_NAME));
			}
			break;

		default:
			break;
		}
		
	}
	
}
