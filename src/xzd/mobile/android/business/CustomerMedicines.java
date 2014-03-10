package xzd.mobile.android.business;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import xzd.mobile.android.AppException;
import xzd.mobile.android.model.Base;

public class CustomerMedicines extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5809652813663351363L;
	private String medicineid = null;
	private String medicinename = null;
	private double medicineprice = 0f;
	private String count = "";

	public String getCount() {
		return count;
	}

	public boolean isZero() {
		if (TextUtils.isEmpty(count) || "0".equals(count)) {
			return true;
		}
		return false;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getMedicineid() {
		return medicineid;
	}

	public void setMedicineid(String medicineid) {
		this.medicineid = medicineid;
	}

	public String getMedicinename() {
		return medicinename;
	}

	public void setMedicinename(String medicinename) {
		this.medicinename = medicinename;
	}

	public double getMedicineprice() {
		return medicineprice;
	}

	public void setMedicineprice(double medicineprice) {
		this.medicineprice = medicineprice;
	}

	@Override
	public String toString() {
		return "CustomerMedicines [medicineid=" + medicineid
				+ ", medicinename=" + medicinename + ", medicineprice="
				+ medicineprice + "]";
	}

	public static List<CustomerMedicines> GetCustomerMedcinesByCustomerID(
			byte[] response) throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<CustomerMedicines> datas = null;
		CustomerMedicines tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<CustomerMedicines>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new CustomerMedicines();
				tmp_obj = array.getJSONObject(i);
				tmp.medicineid = tmp_obj.getString("MedicineID");
				tmp.medicinename = tmp_obj.getString("MedicineName");
				tmp.medicineprice = tmp_obj.getDouble("MedicinePrice");

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

	public CustomerMedicines(String medicineid, String medicinename,
			double medicineprice, String count) {
		super();
		this.medicineid = medicineid;
		this.medicinename = medicinename;
		this.medicineprice = medicineprice;
		this.count = count;
	}

	public CustomerMedicines() {
		super();
		// TODO Auto-generated constructor stub
	}

}
