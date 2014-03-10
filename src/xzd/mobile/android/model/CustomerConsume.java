package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class CustomerConsume extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1060080215885401716L;
	private String begin_date;
	private String end_date;
	private int consume_count;
	private String customer_name;
	private String medicine_name;

	public String getBegin_date() {
		return begin_date;
	}

	public void setBegin_date(String begin_date) {
		this.begin_date = begin_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public int getConsume_count() {
		return consume_count;
	}

	public void setConsume_count(int consume_count) {
		this.consume_count = consume_count;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getMedicine_name() {
		return medicine_name;
	}

	public void setMedicine_name(String medicine_name) {
		this.medicine_name = medicine_name;
	}

	@Override
	public String toString() {
		return "CustomerConsume [begin_date=" + begin_date + ", end_date="
				+ end_date + ", consume_count=" + consume_count
				+ ", customer_name=" + customer_name + ", medicine_name="
				+ medicine_name + "]";
	}

	public static List<CustomerConsume> GetCustomerConsume(byte[] response)
			throws AppException {

		if (null == response) {
			return null;

		}

		JSONArray array = null;
		List<CustomerConsume> datas = null;
		CustomerConsume tmp = null;
		JSONObject tmp_obj = null;

		try {
			datas = new ArrayList<CustomerConsume>();
			try {
				array = new JSONObject(new String(response)).getJSONArray("d");
				if (null == array) {

					throw AppException.http(new Exception());
				}
			} catch (Exception e) {
				// TODO: handle exception

				throw AppException.http(e);
			}

			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp_obj = array.getJSONObject(i);
				tmp = new CustomerConsume();
				tmp.begin_date = tmp_obj.getString("BeginDate");
				tmp.end_date = tmp_obj.getString("EndDate");
				tmp.consume_count = tmp_obj.getInt("ConsumeCount");
				tmp.customer_name = tmp_obj.getString("CustomerName");
				tmp.medicine_name = tmp_obj.getString("MedicineName");

				datas.add(tmp);
				tmp = null;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			tmp_obj = null;
			tmp = null;
			array = null;

		}
		return datas;
	}

}
