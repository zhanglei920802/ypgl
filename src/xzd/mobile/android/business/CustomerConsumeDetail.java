package xzd.mobile.android.business;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;
import xzd.mobile.android.model.Base;

public class CustomerConsumeDetail extends Base {
	public String customername;
	public String medicinename;

	public String time;

	public String consumetype;

	public String num;

	public String getCustomername() {
		return customername;
	}

	public void setCustomername(String customername) {
		this.customername = customername;
	}

	public String getMedicinename() {
		return medicinename;
	}

	public void setMedicinename(String medicinename) {
		this.medicinename = medicinename;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getConsumetype() {
		return consumetype;
	}

	public void setConsumetype(String consumetype) {
		this.consumetype = consumetype;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "CustomerConsumeDetail [customername=" + customername
				+ ", medicinename=" + medicinename + ", time=" + time
				+ ", consumetype=" + consumetype + ", num=" + num + "]";
	}

	public static List<CustomerConsumeDetail> GetCustomerConsumeDetail(
			byte[] response) throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<CustomerConsumeDetail> datas = null;
		CustomerConsumeDetail tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<CustomerConsumeDetail>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new CustomerConsumeDetail();
				tmp_obj = array.getJSONObject(i);
				tmp.customername = tmp_obj.getString("CustomerName");
				tmp.medicinename = tmp_obj.getString("MedicineName");
				tmp.time = tmp_obj.getString("Time");
				tmp.consumetype = tmp_obj.getString("ConsumeType");
				tmp.num = tmp_obj.getString("NUM");

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
