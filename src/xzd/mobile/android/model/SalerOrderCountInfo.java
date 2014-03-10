package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class SalerOrderCountInfo extends SalerSimple {
	private int OrderCount = 0;

	public int getOrderCount() {
		return OrderCount;
	}

	public void setOrderCount(int orderCount) {
		OrderCount = orderCount;
	}

	public static List<SalerOrderCountInfo> GetSalerAndOrderCount(
			byte[] response) throws AppException {
		// TODO Auto-generated method stub
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<SalerOrderCountInfo> datas = null;
		SalerOrderCountInfo tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<SalerOrderCountInfo>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new SalerOrderCountInfo();
				tmp_obj = array.getJSONObject(i);
				tmp.saler_id = tmp_obj.getInt("SalerID");
				tmp.saler_name = tmp_obj.getString("SalerName");
				tmp.OrderCount = tmp_obj.getInt("OrderCount");
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
