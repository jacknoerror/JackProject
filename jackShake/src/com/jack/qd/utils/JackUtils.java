package com.jack.qd.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jack.qd.MyApplication;
import com.jack.qd.R;


/**
 * @author taotao
 *
 */
public class JackUtils {
	public final static String file_path = "/sdcard/gim/";
	public final static String log_path = "/sdcard/gim/log";


	public final static String srcRegEx = "(?<=src=\")(.*?)(?=\")";

	public static void showToast(Context context, String text) {
		if (null != context&&!TextUtils.isEmpty(text))
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();// 0413 判断空
	}

	public static ProgressDialog showProgressDialog(Context context, String text) {
		return ProgressDialog.show(context, "", text);
	}

	final static String HTTP = "http";

	public static String makeItUrl(String str) {
		if (!str.startsWith(HTTP)) {
			if (str.contains(HTTP)) {
				str = str.substring(str.indexOf(HTTP)).replace("\"", "")
						.replace("\\", "").replace("]", "");// 1202 0304
			} else {
				str = "";
				Log.e("jackUtil", "url illegal:" + str);
			}
		}
		return str;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 从Assets中读取图片
	 */
	public static Bitmap getbmFromAssetsFile(Resources res, String fileName) {
		if (res == null)
			return null;
		Bitmap bm = null;
		AssetManager am = res.getAssets();
		try {
			InputStream is = am.open(fileName);
			bm = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bm;

	}

	public static String getDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * write string to /data
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public static boolean writeToSomeWhere(Context context, String str) {
		return writeToFile(context, "fromSomewhere.xml", str);
	}

	public static boolean writeToFile(Context context, String filename,
			String content) {
		try {
			OutputStream out = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			OutputStreamWriter outw = new OutputStreamWriter(out);

			try {
				outw.write(content);
				outw.close();
				out.close();
				return true;
			} catch (IOException e) {
				return false;
			}
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	public static String readFromFile(Context context, String filename) {
		String result = "";

		try {
			InputStream in = context.openFileInput(filename);
			InputStreamReader inr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inr);
			while (br.read() != -1) {
				result += br.readLine();
			}

		} catch (FileNotFoundException e) {
			System.out.println("no such file as:" + filename);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * add SD-pre automately
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		if (!path.startsWith(getSDPre()))
			path = getSDPre() + path;
		return deleteFile(new File(path));
	}

	// 将SD卡文件删除
	public static boolean deleteFile(File file) {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (file.exists()) {
				if (file.isFile()) {
					return file.delete();// 1210
				}
				// 如果它是一个目录
				else if (file.isDirectory()) {
					// 声明目录下所有的文件 files[];
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
						deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
					}
				}
				return file.delete();
			} else {// 已经不存在
				return true; // 1210 taotao
			}
		}
		return false;
	}

	public static AlertDialog showDialog(Context context, String hintContent,
			DialogInterface.OnClickListener positiveListener) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(hintContent);

		builder.setTitle("提示");

		if (null != positiveListener)
			builder.setPositiveButton("确认", positiveListener);// 0408

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		return builder.show();
	}

	/**
	 * 
	 * @param path
	 *            "/qfc/01/we.jpg"
	 * @return
	 */
	public static Bitmap getBitmapFromSDCard(String filePath) {
		/**
		 * 图片文件路径 打印Environment.getExternalStorageDirectory()得到："/mnt/sdcard"，
		 * 即找到了sd卡的根目录
		 */
		if (filePath == null || !filePath.startsWith("/mnt/sdcard"))
			filePath = Environment.getExternalStorageDirectory() + filePath;
		File mfile = new File(filePath);
		if (mfile.exists()) {// 若该文件存在
			Bitmap bm = BitmapFactory.decodeFile(filePath);
			Log.i("SD", "exists");
			return bm;
		}
		return null;
	}

	public static void textpaint_bold(TextView tv) {
		TextPaint tp = tv.getPaint();
		tp.setFakeBoldText(true);// 加粗
	}

	public static void textpaint_underline(TextView tv) {
		TextPaint tp = tv.getPaint();
		tp.setUnderlineText(true);
	}

	public static String getSDPre() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	final static long durationMillis = 800, delayMillis = 200;

	public static void slideview(final View view, final float p1, final float p2) {// 1231
		if (null == view)
			return;

		TranslateAnimation animation = new TranslateAnimation(0, 0, p1, p2);
		// 添加了这行代码的作用时，view移动的时候 会有弹性效果
		animation.setInterpolator(new DecelerateInterpolator());
		animation.setDuration(durationMillis);
		animation.setStartOffset(delayMillis);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				int left = view.getLeft();
				int top = view.getTop() + (int) (p2 - p1);
				int width = view.getWidth();
				int height = view.getHeight();
				view.clearAnimation();
				view.layout(left, top, left + width, top + height);
			}
		});
		view.startAnimation(animation);
	}

	public static String getTimeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * MD5
	 * 
	 * @param s
	 * @return
	 */
	public final static String getMD5(String s) {
		return getMD5(s.getBytes());
	}

	public final static String getMD5(byte[] bytes) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = bytes;
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void bold(TextView tv) {
		TextPaint tp = tv.getPaint();
		tp.setFakeBoldText(true);// 加粗
	}

	public static String encryptionPassword(String password) {
		// 0A53F97440A7C3
		String result = "";
		int data[] = { 0x39, 0x60, 0xCA, 0x47, 0x73, 0x94, 0xA2, 0x45 };
		int j = 0;

		byte[] chats = null;
		try {
			chats = password.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < chats.length; i++) {
			String s = Integer.toHexString(chats[i] ^ data[j]);
			if (s.length() == 1) {
				s = "0" + s;
			}
			result += s;

			j = (j + 1) % 8;
		}

		return result;
	}

	public static boolean isBlank(String s) {
		boolean result = true;

		if (s != null) {
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) != ' ') {
					result = false;
					break;
				}
			}
		}

		return result;
	}

	public static final String parseDateTime1(String timeStr) {
		StringBuffer sb = new StringBuffer();
		sb.append(timeStr.substring(0, 4)).append("-")
				.append(timeStr.substring(4, 6)).append("-")
				.append(timeStr.substring(6, 8)).append(" ")
				.append(timeStr.substring(8, 10)).append(":")
				.append(timeStr.substring(10, 12)).append(":")
				.append(timeStr.substring(12, 14));

		return sb.toString();

	}

	public static String getChatTime(long time) {
		String result = "";
		if (time == 0) {

		} else {

			Date date = new Date(time);
			Date newDate = new Date(System.currentTimeMillis());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = formatter.format(date);
			String newDateString = formatter.format(newDate);
			if (dateString.equals(newDateString)) {
				SimpleDateFormat timeFormatter = new SimpleDateFormat(
						"HH:mm:ss");
				result = timeFormatter.format(time);
			} else {
				result = dateString;
			}
		}

		return result;

	}


	/**
	 * 返回替换后的表情信息
	 * 
	 * @param mess
	 * @return
	 */
	public static String repaceMess(String mess) {

		mess = mess
				.replaceAll("\\[(.*?)\\]",
						"<img src=\"{\\$SYS_IMAGE_PATH\\$}$1\\.gif\">")
				.replaceAll("\\<div(.*?)\\>", "").replace("<div>", "")
				.replace("</div>", "").replaceAll("<p>", "")
				.replace("</p>", "");
		mess = mess.replaceAll("\\<div(.*?)\\>", "");

		// if (mess.indexOf("$SYS_IMAGE_PATH") > 0) {
		//
		// } else {
		// mess = html(mess);
		// }

		return mess;
	}

	/**
	 * 图片缩放
	 * 
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Drawable imageScale(Bitmap bitmap, int dst_w, int dst_h) {

		int src_w = bitmap.getWidth();
		int src_h = bitmap.getHeight();

		if (src_w <= dst_w && src_h <= dst_h) {
			dst_w = src_w;
			dst_h = src_h;
		}

		float scale_w = ((float) dst_w) / src_w;
		float scale_h = ((float) dst_h) / src_h;
		Matrix matrix = new Matrix();
		matrix.postScale(scale_w, scale_h);
		Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
				true);
		return new BitmapDrawable(dstbmp);
	}




	/**
	 * 存储拍照图片到SD卡
	 */
	public static void saveFullImage(String filepath, String fileName) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(filepath + fileName);
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			// 缩放图片的尺寸
			int zoomWidth = 600;
			float scale = (float) zoomWidth / bitmapWidth;
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			// 产生缩放后的Bitmap对象
			Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmapWidth, bitmapHeight, matrix, false);
			// save file
			File myCaptureFile = new File(filepath + "small" + fileName);
			FileOutputStream out = new FileOutputStream(myCaptureFile);
			if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
				out.flush();
				out.close();
			}
			if (!bitmap.isRecycled()) {
				bitmap.recycle();// 记得释放资源，否则会内存溢出
			}
			if (!resizeBitmap.isRecycled()) {
				resizeBitmap.recycle();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 设置用户离线图片
	 * 
	 * @param bmp
	 *            原始图
	 * @return 返回离线图
	 */
	public static Bitmap setUsOfFlineBmp(Bitmap bmp) {
		Bitmap grayImg = null;
		try {
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			grayImg = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(grayImg);
			Paint paint = new Paint();
			ColorMatrix colorMatrix = new ColorMatrix();
			colorMatrix.setSaturation(0);
			paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
			canvas.drawBitmap(bmp, 0, 0, paint);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();

		}

		return grayImg;
	}

	/**
	 * 
	 * @param bmp1
	 *            原始图
	 * @param bmp2
	 *            状态图标
	 * @return 合成后的图
	 */
	public static Bitmap setSynthBmp(Bitmap bmp1, Bitmap bmp2) {
		Bitmap grayImg = null;
		try {
			int width = bmp1.getWidth();
			int height = bmp1.getHeight();
			grayImg = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(grayImg);
			Paint paint = new Paint();
			canvas.drawBitmap(bmp1, 0, 0, paint);
			canvas.drawBitmap(bmp2, width - bmp2.getWidth(),
					height - bmp2.getHeight(), paint);// 插入图标
			canvas.save(Canvas.ALL_SAVE_FLAG);

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return grayImg;

	}

	public static boolean detect(Activity act) {// ??
		ConnectivityManager manager = (ConnectivityManager) act
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}
		return true;
	}

	public static String html(String content) {
		String html = content;
		if (content.indexOf("$SYS_IMAGE_PATH") > 0) {
		} else {
			html = html.replace("<", "&lt;");
			html = html.replaceAll(">", "&gt;");
		}
		return html;
	}

	/**
	 * 获取当前的屏幕显示的窗口名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getDisplayClassName(Context context) {
		return ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1)
				.get(0).topActivity.getClassName();
	}

	/*
	 * System.getProperties().put( "proxySet", "true" );
	 * System.getProperties().put( "proxyHost", YftValues.PROXY_HOST );
	 * System.getProperties().put( "proxyPort", "80" );
	 */

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(),
				InputMethodManager.SHOW_FORCED);
	}

	public static void call(String phoneNum, Context context) throws Exception { 
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phoneNum));
		context.startActivity(intent);
	}

	/**
	 * @throws NameNotFoundException
	 * 
	 */
	public static int getApiLvl() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 对网络连接状态进行判断
	 * 
	 * @return true, 可用； false， 不可用
	 */
	public static boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) MyApplication
				.app().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}
	
	
	/**
	 * @param context 
	 * @return
	 */
	public static NetworkInfo getNetworkInfo(Context context){
		ConnectivityManager connManager = (ConnectivityManager) MyApplication
				.app().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		return info;
	}
	
	/**
	 * @param context
	 * @return 网络连接是否可用
	 */
	public static boolean isNetworkAvailable(Context context){
		NetworkInfo info = getNetworkInfo(context);
		return null!=info&&info.isAvailable();
	}
	
	/**
	 * @param context
	 * @return 是否wifi
	 */
	public static boolean isNetworkWIFI(Context context){
		NetworkInfo info = getNetworkInfo(context);
		return isNetworkAvailable(context)&&info.getType()== ConnectivityManager.TYPE_WIFI;
	}
	
	/**
	 * @param context
	 * @return 是否3g、2g网络
	 */
	public static boolean isNetworkPhone(Context context){
		NetworkInfo info = getNetworkInfo(context);
		return isNetworkAvailable(context)&&info.getType()== ConnectivityManager.TYPE_MOBILE;
	}
	
	
    /**
     * 获取图片所在文件夹名称
     * @param path
     * @return
     */
    public static String getDir(String path)
    {
        String subString = path.substring(0, path.lastIndexOf('/'));
        return subString.substring(subString.lastIndexOf('/') + 1, subString.length());
    }
    
    public static String getClipboardText(Context context){//TODO test
    	if(null==context) return "";
    	 if (getApiLvl() > 11) {
    	     android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    	     return c.getPrimaryClip().getItemAt(0).getText().toString();
    	    } else {
    	     android.text.ClipboardManager c = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    	     return c.getText().toString();
    	    }
    }
    
}
