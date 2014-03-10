package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class CustomerSimple extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5162432238576193626L;
	protected int Customer_id;
	protected String Customer_Name;

	public int getCustomer_id() {
		return Customer_id;
	}

	public void setCustomer_id(int customer_id) {
		Customer_id = customer_id;
	}

	public String getCustomer_Name() {
		return Customer_Name;
	}

	public void setCustomer_Name(String customer_Name) {
		Customer_Name = customer_Name;
	}

	@Override
	public String toString() {
		return "CustomerSimple [Customer_id=" + Customer_id
				+ ", Customer_Name=" + Customer_Name + "]";
	}

	public static List<CustomerSimple> parseCustomSimpleList(byte[] response) throws AppException {
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<CustomerSimple> datas = null;
		CustomerSimple tmp = null;
		JSONObject tmp_obj = null;

		try {
			datas = new ArrayList<CustomerSimple>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			
//			Log.d(tag, msg)
			System.out.println("CustomerSimple.parseCustomSimpleList(){json data["+array+"]}");
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new CustomerSimple();
				tmp_obj = array.getJSONObject(i);

				tmp.Customer_id = tmp_obj.getInt("CustomerID");
				tmp.Customer_Name = tmp_obj.getString("CustomerName");

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
