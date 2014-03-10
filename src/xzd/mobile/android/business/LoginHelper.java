package xzd.mobile.android.business;

import xzd.mobile.android.basic.ApiClient;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.User;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 登录业务逻辑层
 * 
 * @author ZhangLei
 * 
 */
public class LoginHelper {

	private static LoginHelper loginhelper = null;

	// private AppContext appcontext = null;

	public static LoginHelper getInstance() {
		if (null == loginhelper) {
			loginhelper = new LoginHelper();
		}

		return loginhelper;
	}

	// 判断是否是存在用户信息
	public User getUser(Context context) {
		User user = new User();
		SharedPreferences spf = getSharedPrefrence(context);
		user.setM_mgr_logname(spf.getString(Config.SPF_USERNAME, ""));// 登录名称
		user.setM_id(spf.getInt(Config.SPF_M_ID, 0));
		user.setM_mgr_pwd(spf.getString(Config.SPF_PWD, ""));// 密码
		user.setM_mgr_name(spf.getString(Config.SPF_NAME, ""));// 用户名
		user.setM_role_id(spf.getInt(Config.SPF_ROLEID, 0));
		user.setM_team_id(spf.getInt(Config.SPF_TEAMID, 0));
		user.setSale_id(spf.getInt(Config.SPF_SALEID, -1));
		user.setRememberMe(spf.getBoolean(Config.SPF_REMEMBER_ME, false));
		user.setCounterMain_ID(spf.getInt(Config.COUNTER_MAN_ID, -1));

		return user;
	}

	public void saveUserInfo(Context context, User user) {
		Editor editor = getSharedPrefrence(context).edit();

		editor.putString(Config.SPF_USERNAME, user.getM_mgr_logname());
		editor.putString(Config.SPF_PWD, user.getM_mgr_pwd());
		editor.putString(Config.SPF_NAME, user.getM_mgr_name());
		editor.putInt(Config.SPF_M_ID, user.getM_id());
		editor.putInt(Config.SPF_ROLEID, user.getM_role_id());
		editor.putInt(Config.SPF_TEAMID, user.getM_team_id());
		editor.putInt(Config.SPF_SALEID, user.getSale_id());
		editor.putBoolean(Config.SPF_REMEMBER_ME, user.isRememberMe());
		editor.putInt(Config.COUNTER_MAN_ID, user.getCounterMain_ID());

		editor.commit();
	}

	/**
	 * 获取应用程序首选项
	 * 
	 * @param context
	 * @return
	 */
	public SharedPreferences getSharedPrefrence(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 处理用户登录
	 * 
	 * @throws Exception
	 */
	public User doLogin(final String username, final String pwd,
			final int isEncryp) throws Exception {
		return ApiClient.getInstance().doLogin(username, pwd, isEncryp);
	}

}
