package xzd.mobile.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import xzd.mobile.android.business.LoginHelper;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.User;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppContext extends Application {

	public static final int PAGE_SIZE = 15;
	// Helper
	public AppManager appmanager = null;
	public static final boolean DEBUG = true;
	public static User user = null;
	public int today_orders_count = 0;
	public String area_code = "";

	public static UIHelper uihelper = null;
	public static StringUtils stringutils = null;
	public static LoginHelper loginhelper = null;
	
	@Override
	public void onCreate() {
		super.onCreate();

		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler());
		appmanager = AppManager.getInstance();

	}

	public  static boolean is_login() {
		return user == null ? false : true;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 保存已经序列化的对象
	 * 
	 * @param serializableObj
	 * @param file
	 * @return
	 */
	public boolean saveObject(Serializable serializableObj, String file,
			int mode) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = openFileOutput(file, mode);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(serializableObj);
			oos.flush();
			return true;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();

			} catch (IOException e) {

				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {

				e.printStackTrace();
			}

			oos = null;
			fos = null;
		}

	}

	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();

	}
}
