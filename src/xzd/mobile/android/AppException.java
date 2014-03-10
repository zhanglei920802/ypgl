package xzd.mobile.android;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.http.HttpException;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

public class AppException extends Exception implements UncaughtExceptionHandler {
	public static final String TAG = "AppException";

	private static final long serialVersionUID = -5529262667754665366L;

	private final static boolean Debug = true;

	public final static byte TYPE_NETWORK = 0x01;
	public final static byte TYPE_SOCKET = 0x02;
	public final static byte TYPE_HTTP_CODE = 0x03;
	public final static byte TYPE_HTTP_ERROR = 0x04;
	public final static byte TYPE_XML = 0x05;
	public final static byte TYPE_IO = 0x06;
	public final static byte TYPE_RUN = 0x07;

	private byte mtype;
	private int mcode;
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	public AppException() {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	public AppException(Exception exp, int code, byte type) {
		super(exp);
		this.mcode = code;
		this.mtype = type;
		if (Debug) {
			this.saveLog(exp);
		}
	}

	private void saveLog(Exception exp) {
		String errorlog = "errorlog.txt";
		String savePath = "";
		String logFilePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {

			String storageState = Environment.getExternalStorageState();
			if (storageState.equals(Environment.MEDIA_MOUNTED)) {
				savePath = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/Android/data/cn.cdut.app/Log/";
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				logFilePath = savePath + errorlog;
			}

			if (logFilePath == "") {
				return;
			}
			File logFile = new File(logFilePath);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			fw = new FileWriter(logFile, true);
			pw = new PrintWriter(fw);
			pw.println("--------------------" + (new Date().toLocaleString())
					+ "---------------------");
			exp.printStackTrace(pw);
			pw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}

	}

	public static AppException http(int code) {
		return new AppException(null, code, TYPE_HTTP_CODE);
	}

	public static AppException http(Exception e) {
		return new AppException(e, 0, TYPE_HTTP_ERROR);
	}

	public static AppException IO(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(e, 0, TYPE_NETWORK);
		} else if (e instanceof IOException) {
			return new AppException(e, 0, TYPE_IO);
		}
		return run(e);
	}

	public static AppException run(Exception e) {
		return new AppException(e, 0, TYPE_RUN);
	}

	public static AppException network(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(e, 0, TYPE_NETWORK);
		} else if (e instanceof HttpException) {
			return http(e);
		} else if (e instanceof SocketException) {
			return socket(e);
		}
		return http(e);
	}

	public static AppException socket(Exception e) {
		return new AppException(e, 0, TYPE_SOCKET);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}

	}

	private boolean handleException(Throwable ex) {

		if (ex == null) {
			return false;
		}

		final Context context = AppManager.getInstance().currentActivity();

		if (context == null) {
			return false;
		}

		final String crashReport = getCrashReport(context, ex);

		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				UIHelper.sendAppCrashReport(context, crashReport);
				Looper.loop();
			}

		}.start();
		return true;
	}

	private String getCrashReport(Context context, Throwable ex) {
		PackageInfo pinfo = ((AppContext) context.getApplicationContext())
				.getPackageInfo();

		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "("
				+ pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE
				+ "(" + android.os.Build.MODEL + ")\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		return exceptionStr.toString();
	}

	public static AppException getAppExceptionHandler() {
		return new AppException();
	}

	public int getCode() {
		return this.mcode;
	}

	public int getType() {
		return this.mtype;
	}

	public void makeToast(Context ctx) {
		switch (this.getType()) {
		case TYPE_HTTP_CODE:
			String err = ctx.getString(R.string.http_status_code_error,
					this.getCode());
			Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_HTTP_ERROR:
			Toast.makeText(ctx, R.string.http_exception_error,
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_SOCKET:
			Toast.makeText(ctx, R.string.socket_exception_error,
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_NETWORK:
			Toast.makeText(ctx, R.string.network_not_connected,
					Toast.LENGTH_SHORT).show();
			break;
		case TYPE_XML:
			Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT)
					.show();
			break;
		case TYPE_IO:
			Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT)
					.show();
			break;
		case TYPE_RUN:
			Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}
}
