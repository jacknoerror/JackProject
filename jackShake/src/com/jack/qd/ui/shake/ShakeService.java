package com.jack.qd.ui.shake;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jack.qd.MyApplication;
import com.jack.qd.R;
import com.jack.qd.data.MyEvent;
import com.jack.qd.utils.JackUtils;

import de.greenrobot.event.EventBus;

public class ShakeService extends Service {
	private NotificationManager mNM;
	private boolean looking;
	private String keyword; 

    /** 
     * Class for clients to access.    Because we know this service always 
     * runs in the same process as its clients, we don't need to deal with 
     * IPC. 
     */ 
    public class LocalBinder extends Binder { 
    		public ShakeService getService() { 
                    return ShakeService.this; 
            } 
    } 
     
    @Override 
    public void onCreate() { 
            mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE); 

            // Display a notification about us starting.    We put an icon in the status bar. 
            showNotification(); 
            
            EventBus.getDefault().register(this);
            
            ShakeHelper.getInstance().init(MyApplication.app()).register().setShakeHelperListener(new ShakeHelper.OnShakedListener() {
				
				@Override
				public void onShaked(ShakeHelper helper) {
					MyEvent myEvent = new MyEvent("go_lookup");
					myEvent.obj = JackUtils.getClipboardText(MyApplication.app());
					EventBus.getDefault().post(myEvent);
				}
			});
    } 
    
    public void onEventBackgroundThread(MyEvent e){
    	if(null==e )return;
    	String msg = e.getEventMsg();
    	String nowKeyword = (String) e.obj;
    	if(msg.equals("go_lookup")){
    		if(TextUtils.isEmpty(nowKeyword)||nowKeyword.equals(keyword)) return;
    		//jsoup connect
			try {
				keyword = nowKeyword;
				Document doc = Jsoup.connect("http://dict.baidu.com/s?f=8&wd="+nowKeyword).get();
				/*
				 * <div class="en-query-simple-means" id="en-query-simple-means">
					<span class="arrow"></span>
					<ul class="tablist" id="en-simple-means-list">
						<li rel="en-simple-means" class="active">简明释义</li> 		</ul>
					<div class="tab-content">
						<div class="tab en-simple-means dict-en-simplemeans-english" id="en-simple-means" style="display: block;"> <div><p><strong>abbr.</strong><span>acceptable periodic inspection 合意周期检查；armor-piercing incendiary tracer 追踪式穿甲燃烧弹</span></p></div><p><span>易混淆的单词：<a href="/s?wd=aPI">aPI</a><a href="/s?wd=Api">Api</a><a href="/s?wd=API">API</a></span></p>  <div class="source">以下结果由 <img src="/static/img/jinshan.png"><a href="http://cp.iciba.com/" target="_blank">金山词霸</a> 提供</div></div> 		</div>
				</div>
				 */
	            Element hrefs = doc.body().getElementById("container")
	            		.getElementById("wraper")
	            		.getElementById("en-query-simple-means")
	            		.getElementsByClass("tab-content").get(0)
	            		; 
//	            String resultMsg = hrefs;
	            String resultMsg = hrefs.text();
	    		//to show
	            Log.i("NOW", resultMsg);
	    		MyEvent myEvent = new MyEvent("go_showup");
	    		myEvent.obj = resultMsg;
				EventBus.getDefault().post(myEvent);
			} catch (IOException e1) {
				keyword = null;
				e1.printStackTrace();
			}  
    	}
    }
    public void onEventMainThread(MyEvent e) {
    	if(null==e )return;
    	String msg = e.getEventMsg();
    	if(msg.equals("go_showup")){
    		try{
    			String lookupResult = (String)e.obj ;
    			showLookupNotification(lookupResult);
    		}catch(Exception ex){
    		}
    	}
    }
    

    @Override 
    public int onStartCommand(Intent intent, int flags, int startId) { 
            Log.i("LocalService", "Received start id " + startId + ": " + intent); 
            // We want this service to continue running until it is explicitly 
            // stopped, so return sticky. 
            return START_STICKY; 
    } 

    @Override 
    public void onDestroy() { 
            // Cancel the persistent notification. 
            mNM.cancel(R.string.local_service_started); 

            // Tell the user we stopped. 
            Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show(); 
            
            EventBus.getDefault().unregister(this);
            ShakeHelper.getInstance().unRegister();
    } 

    @Override 
    public IBinder onBind(Intent intent) { 
         return mBinder;
    } 

    // This is the object that receives interactions from clients.    See 
    // RemoteService for a more complete example. 
    private final IBinder mBinder = new LocalBinder(); 

    /** 
     * Show a notification while this service is running. 
     */ 
    private void showNotification() { 
            // In this sample, we'll use the same text for the ticker and the expanded notification 
            CharSequence text = getText(R.string.local_service_started); 

            // Set the icon, scrolling text and timestamp 
            Notification notification = new Notification(R.drawable.ic_launcher, text, 
                            System.currentTimeMillis()); 

            // The PendingIntent to launch our activity if the user selects this notification 
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, 
                            new Intent(this, ShakeHelper.class), 0); //TODO 

            // Set the info for the views that show in the notification panel. 
            notification.setLatestEventInfo(this, getText(R.string.local_service_label), 
                                         text, contentIntent); 

            // Send the notification. 
            // We use a layout id because it is a unique number.    We use it later to cancel. 
            mNM.notify(R.string.local_service_started, notification); 
    } 
    
    @SuppressLint("NewApi")
	private void showLookupNotification(String result){
    	//handle string
    	if(TextUtils.isEmpty(result)) return;//TODO 查询失败
    	
    	//notify
//    	Notification notification = new Notification(R.drawable.ic_launcher, text, 
//                System.currentTimeMillis()); 
    	RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification_dict);
    	rv.setTextViewText(R.id.tv_notification_dict, result);
		Notification n = new Notification.Builder(MyApplication.app())
    		.setSmallIcon(R.drawable.ic_launcher)
//    		.setContent(rv)
    		.setContentInfo(result)
    		.setTicker(result)
//    		.setDeleteIntent(delIntent )
//    		.setContentIntent(intent)
//    		.notify()
    		.build()
    		;
		
    	mNM.notify(0x01102,n);
    }

    /*Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Const.WHAT_SENSOR_SHAKE:
				Toast.makeText(MyApplication.app(), JackUtils.getClipboardText(MyApplication.app()),Toast.LENGTH_SHORT).show();
//				Log.i(TAG, JackUtils.getClipboardText(mContext));
				break;
			}
		}

	};*/
    
}
