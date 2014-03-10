package xzd.mobile.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class User extends Base {
	private static final String TAG = "USER";

	/**
	 * 
	 */
	private static final long serialVersionUID = 5720855380872602723L;
	private int M_id;
	private String M_mgr_logname;
	private String M_mgr_pwd;
	private String M_mgr_name;
	private int M_role_id;
	private int M_team_id;
	private int sale_id;
	private int CounterMain_ID = -1;
	private String msgpush_id;
	private int saler_level;
	// 用户配置信息

	private boolean haveUpdate = false;
	private boolean isRememberMe = false;

	public boolean isHaveUpdate() {
		return haveUpdate;
	}

	public void setHaveUpdate(boolean haveUpdate) {
		this.haveUpdate = haveUpdate;
	}

	public String getMsgpush_id() {
		return msgpush_id;
	}

	public void setMsgpush_id(String msgpush_id) {
		this.msgpush_id = msgpush_id;
	}

	public int getSaler_level() {
		return saler_level;
	}

	public void setSaler_level(int saler_level) {
		this.saler_level = saler_level;
	}

	public int getCounterMain_ID() {
		return CounterMain_ID;
	}

	public void setCounterMain_ID(int counterMain_ID) {
		CounterMain_ID = counterMain_ID;
	}

	public boolean isRememberMe() {
		return isRememberMe;
	}

	public void setRememberMe(boolean isRememberMe) {
		this.isRememberMe = isRememberMe;
	}

	public int getSale_id() {
		return sale_id;
	}

	public void setSale_id(int sale_id) {
		this.sale_id = sale_id;
	}

	public int getM_id() {
		return M_id;
	}

	public void setM_id(int m_id) {
		M_id = m_id;
	}

	public String getM_mgr_logname() {
		return M_mgr_logname;
	}

	public void setM_mgr_logname(String m_mgr_logname) {
		M_mgr_logname = m_mgr_logname;
	}

	public String getM_mgr_pwd() {
		return M_mgr_pwd;
	}

	public void setM_mgr_pwd(String m_mgr_pwd) {
		M_mgr_pwd = m_mgr_pwd;
	}

	public String getM_mgr_name() {
		return M_mgr_name;
	}

	public void setM_mgr_name(String m_mgr_name) {
		M_mgr_name = m_mgr_name;
	}

	public int getM_role_id() {
		return M_role_id;
	}

	public void setM_role_id(int m_role_id) {
		M_role_id = m_role_id;
	}

	public int getM_team_id() {
		return M_team_id;
	}

	public void setM_team_id(int m_team_id) {
		M_team_id = m_team_id;
	}

	public void LoginUpdate(User data) {

		M_id = data.getM_id();
		M_mgr_name = data.getM_mgr_name();
		M_mgr_logname = data.getM_mgr_logname();
		M_role_id = data.getM_role_id();
		M_team_id = data.getM_team_id();
		sale_id = data.getSale_id();
		msgpush_id = data.getMsgpush_id();
		saler_level = data.getSaler_level();
	}

	

	@Override
	public String toString() {
		return "User [M_id=" + M_id + ", M_mgr_logname=" + M_mgr_logname
				+ ", M_mgr_pwd=" + M_mgr_pwd + ", M_mgr_name=" + M_mgr_name
				+ ", M_role_id=" + M_role_id + ", M_team_id=" + M_team_id
				+ ", sale_id=" + sale_id + ", CounterMain_ID=" + CounterMain_ID
				+ ", msgpush_id=" + msgpush_id + ", saler_level=" + saler_level
				+ ", haveUpdate=" + haveUpdate + ", isRememberMe="
				+ isRememberMe + "]";
	}

	/**
	 * 解析用户模型
	 * 
	 * @param responseData
	 * @return
	 * 
	 *         解析成功。返回用户模型 失败,返回null
	 */
	public static User PasersJson(byte[] responseData) {
		if (null == responseData) {
			return null;
		}

		JSONObject obj = null;
		User user = null;

		/*
		 * {"Name":"超级管理员","ID":1,"__type":"Mobile.Model.User","RoleID":0,"Pswd":
		 * "e10adc3949ba59abbe56e057f20f883e","LogName":"admin","TeamID":1}
		 */
		try {
			obj = new JSONObject(new String(responseData)).getJSONObject("d");
			user = new User();
			user.M_id = obj.getInt("ID");
			user.M_mgr_logname = obj.getString("LogName");
			user.M_mgr_pwd = obj.getString("Pswd");
			user.M_mgr_name = obj.getString("Name");
			user.M_role_id = obj.getInt("RoleID");
			user.M_team_id = obj.getInt("TeamID");
			user.sale_id = obj.getInt("SaleID");
			user.msgpush_id = obj.getString("MsgPushID");
			user.saler_level = obj.getInt("SalerLevel");
			Log.d(TAG, "用户数据解析成功:" + user.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "用户数据解析失败");
			user = null;
		} finally {
			obj = null;
		}
		return user;
	}
}
