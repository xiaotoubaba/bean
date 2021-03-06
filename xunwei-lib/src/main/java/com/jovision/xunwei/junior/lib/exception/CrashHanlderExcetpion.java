package com.jovision.xunwei.junior.lib.exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrashHanlderExcetpion implements UncaughtExceptionHandler {
	
	public static final String TAG = "CrashHandler";
	
	private UncaughtExceptionHandler mDefaultHandler;
	private static CrashHanlderExcetpion INSTANCE = new CrashHanlderExcetpion();
	private Context mContext;
	private Map<String, String> infos = new HashMap<String, String>();

	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);

	private CrashHanlderExcetpion() {
	}

	public static CrashHanlderExcetpion getInstance() {
		return INSTANCE;
	}


	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}


	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "抱歉，出现异常,即将退出", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		dumpHprof(ex);
		collectDeviceInfo(mContext);
		saveCrashInfo2File(ex);
		return true;
	}
	
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				//String model = Hutils.getMODEL();
				
				//infos.put("model", model);
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	private String saveCrashInfo2File(Throwable ex) {
		
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = time + "-" + timestamp + ".txt";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jovision/xunwei/error/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}

    private void dumpHprof(Throwable ex) {
        if (null == ex)
            return;

        Throwable thr = ex;
        while (null != thr) {
            String cls = thr.getClass().getName();
            if (null != cls && cls.contains("OutOfMemoryError")) {
                File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/jovision/xunwei/error/");
                if (!fileDir.exists())
                    fileDir.mkdirs();

                File hprofFile = null;
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

                String time = simpleDateFormat.format(date);
                hprofFile = new File(fileDir.getAbsolutePath(), "hprof-" + time + ".hprof");

                try {
                    Debug.dumpHprofData(hprofFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                } 
                break;
            }
            thr = thr.getCause();
        }
    }

}