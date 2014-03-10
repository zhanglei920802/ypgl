package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class SalerSimple extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1253688748074246705L;
	protected int saler_id;
	protected String saler_name;

	public int getSaler_id() {
		return saler_id;
	}

	public void setSaler_id(int saler_id) {
		this.saler_id = saler_id;
	}

	public String getSaler_name() {
		return saler_name;
	}

	public void setSaler_name(String saler_name) {
		this.saler_name = saler_name;
	}

	@Override
	public String toString() {
		return "SalerSimple [saler_id=" + saler_id + ", saler_name="
				+ saler_name + "]";
	}

	public static List<SalerSimple> GetCountermen(byte[] response) throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<SalerSimple> datas = null;
		SalerSimple tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<SalerSimple>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new SalerSimple();
				tmp_obj = array.getJSONObject(i);

				tmp.saler_id = tmp_obj.getInt("SalerID");
				tmp.saler_name = tmp_obj.getString("SalerName");

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
