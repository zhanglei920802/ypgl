package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class CustGroup extends Base {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7872283137163436388L;

	public CustGroup() {
		// TODO Auto-generated constructor stub
	}

	private int GroupID = 0;
	private String GroupName = null;
	private int CustomerNum = 0;

	public int getGroupID() {
		return GroupID;
	}

	public void setGroupID(int groupID) {
		GroupID = groupID;
	}

	public String getGroupName() {
		return GroupName;
	}

	public void setGroupName(String groupName) {
		GroupName = groupName;
	}

	public int getCustomerNum() {
		return CustomerNum;
	}

	public void setCustomerNum(int customerNum) {
		CustomerNum = customerNum;
	}

	@Override
	public String toString() {
		return "CustGroup [GroupID=" + GroupID + ", GroupName=" + GroupName
				+ ", CustomerNum=" + CustomerNum + "]";
	}

	public static List<CustGroup> GetCustGroupBySalerID(byte[] response)
			throws AppException {
		// TODO Auto-generated method stub
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<CustGroup> datas = null;
		CustGroup tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<CustGroup>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new CustGroup();
				tmp_obj = array.getJSONObject(i);
				tmp.GroupID = tmp_obj.getInt("GroupID");
				tmp.GroupName = tmp_obj.getString("GroupName");
				tmp.CustomerNum = tmp_obj.getInt("CustomerNum");

				//除去未分组
				if ("未分组".equals(tmp.getGroupName())) {
					continue;
				}
				datas.add(tmp);
				tmp = null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			datas = null;
			throw AppException.http(new Exception());
		} finally {
			tmp_obj = null;
			tmp = null;
			array = null;

		}

		return datas;
	}

}
