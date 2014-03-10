package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class SalerVisitSimpleInfo extends SalerSimple {
	/**
	 * 
	 */
	private static final long serialVersionUID = -107068790606722195L;

	public String LastestVisitDate = ""; // 业务员最近拜访时间

	public int HasComment = 0;

	public String getLastestVisitDate() {
		return LastestVisitDate;
	}

	public void setLastestVisitDate(String lastestVisitDate) {
		LastestVisitDate = lastestVisitDate;
	}

	public int getHasComment() {
		return HasComment;
	}

	public void setHasComment(int hasComment) {
		HasComment = hasComment;
	}

	public static List<SalerVisitSimpleInfo> GetCountermenPage(byte[] response)
			throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<SalerVisitSimpleInfo> datas = null;
		SalerVisitSimpleInfo tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<SalerVisitSimpleInfo>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new SalerVisitSimpleInfo();
				tmp_obj = array.getJSONObject(i);

				tmp.saler_id = tmp_obj.getInt("SalerID");
				tmp.saler_name = tmp_obj.getString("SalerName");
				tmp.LastestVisitDate = tmp_obj.getString("LastestVisitDate");
				tmp.HasComment =tmp_obj.getInt("HasComment");
				
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
