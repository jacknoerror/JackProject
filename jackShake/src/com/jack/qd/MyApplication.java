/**
 * 
 */
package com.jack.qd;

import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;

import com.jack.qd.data.Const;


/**
 * @author taotao
 *
 */
public class MyApplication extends Application {
	
	
	private static MyApplication yApp;
	
	private void initScreenData(){
		DisplayMetrics dm = getResources().getDisplayMetrics();
		Const.SCREEN_WIDTH = dm.widthPixels;
		Const.SCREEN_HEIGHT= dm.heightPixels;
		Log.i(getString(R.string.app_name),  String.format("(%s)(%s)(%s)",dm.densityDpi+"","<=dpi---desi=>",dm.density+""));
	}
	@Override
	public void onCreate() {
		super.onCreate();
		yApp = this;
		initScreenData();
//		ImageLoaderHelper.initImageLoader(this);//0610
	}

	public static MyApplication app() {
		return yApp;
	}
}
