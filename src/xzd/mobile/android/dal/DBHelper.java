package xzd.mobile.android.dal;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作类 2013年5月31日11:08:41
 * 
 * @author ZhangLei
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;// 数据库版本
	private static final String DICTIONARY_TABLE_NAME = "user.db";// 数据库表名
	private static DBHelper dbhelper = null;

	private DBHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);

	}

	private DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);

	}

	public static DBHelper getInstance(Context context) {

		if (null == dbhelper) {
			dbhelper = new DBHelper(context, DICTIONARY_TABLE_NAME, null,
					DATABASE_VERSION);
		}

		return dbhelper;
	}

	/***
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		if (null != db) {

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
