package xzd.mobile.android.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

/**
 * 时间工具包
 * 
 * @date 2013年5月20日21:37:11
 * 
 * @author ZhangLei
 * 
 */
public class DateUtil {
	private static DateUtil dateutil = null;
	public static final String TAG = "DateUtil";

	public static DateUtil getInstance() {
		if (null == dateutil) {
			dateutil = new DateUtil();
		}
		return dateutil;
	}

	public String getDateCN() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}

	// 日期比较。如果给定日期在七天之内返回true,否则返回false
	public boolean chkDate(String Date, int days) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = null;
		if (Date.contains("/")) {
			sdf = new SimpleDateFormat("yyyy/MM/dd");
		} else if (Date.contains("-")) {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}

		try {

			if (getDate(Date, days).after(new Date(System.currentTimeMillis()))) {// 给定时间加7在当前时间之后
				Log.d(TAG, "给定时间加7在当前时间之后");
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "转换成功");
		}
		return false;
	}

	/**
	 * 
	 * 
	 * @param dateString
	 *            日期对象 ，格式如 1-31-1900
	 * @param lazyDays
	 *            倒推的天数
	 * @return 指定日期倒推指定天数后的日期对象
	 * @throws ParseException
	 */
	public Date getDate(String dateString, int afterdays) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date inputDate = dateFormat.parse(dateString);

		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);

		int inputDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		cal.set(Calendar.DAY_OF_YEAR, inputDayOfYear + afterdays);

		return cal.getTime();
	}

}
