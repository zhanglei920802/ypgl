package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class AreaInfo extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7181681575179640680L;
	private String Area_Code;
	private String Area_Name;

	public String getArea_Code() {
		return Area_Code;
	}

	public void setArea_Code(String area_Code) {
		Area_Code = area_Code;
	}

	public String getArea_Name() {
		return Area_Name;
	}

	public void setArea_Name(String area_Name) {
		Area_Name = area_Name;
	}

	@Override
	public String toString() {
		return "AreaInfo [Area_Code=" + Area_Code + ", Area_Name=" + Area_Name
				+ "]";
	}

	public static List<AreaInfo> GetAreaList(byte[] response)
			throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<AreaInfo> datas = null;
		AreaInfo tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<AreaInfo>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new AreaInfo();
				tmp_obj = array.getJSONObject(i);

				tmp.Area_Code = tmp_obj.getString("AreaCode");
				tmp.Area_Name = tmp_obj.getString("AreaName");

				datas.add(tmp);
				tmp = null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			datas = null;
			throw AppException.http(e);
		} finally {
			tmp_obj = null;
			tmp = null;
			array = null;

		}

		return datas;

	}
}
