package com.jack.qd.utils;

import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class ServiceUtil  {

	private static ActivityManager mActivityManager;



	// ͨ��Service���������ж��Ƿ�����ĳ������
	public static boolean isServiceStarted(
//			List<ActivityManager.RunningServiceInfo> mServiceList,
			Context context,
			String className) {
		if(null==context) return false;
		if(null==mActivityManager)mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(30);
		
		for (int i = 0; i < mServiceList.size(); i++)

		{

			if (className.equals(mServiceList.get(i).service.getClassName()))

			{
				return true;
			}

		}

		return false;

	}

	// ��ȡ���������ķ��������
	private String getServiceClassName(
			List<ActivityManager.RunningServiceInfo> mServiceList) {
		String res = "";

		for (int i = 0; i < mServiceList.size(); i++) {

			res += mServiceList.get(i).service.getClassName() + " \n";

		}

		return res;

	}
}