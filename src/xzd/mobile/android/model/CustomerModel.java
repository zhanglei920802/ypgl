package xzd.mobile.android.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

public class CustomerModel extends CustomerSimple {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3685391583500243079L;
	protected String Customer_LinkMan;
	protected String Customer_Tel;
	protected String Saler_Name;
	protected String Customer_Area;
	protected String Customer_Address;
	protected String Customer_Type;
	private String CustomerGroupName;
	public String getCustomer_LinkMan() {
		return Customer_LinkMan;
	}

	public String getCustomerGroupName() {
		return CustomerGroupName;
	}

	public void setCustomerGroupName(String customerGroupName) {
		CustomerGroupName = customerGroupName;
	}

	public void setCustomer_LinkMan(String customer_LinkMan) {
		Customer_LinkMan = customer_LinkMan;
	}

	public String getCustomer_Tel() {
		return Customer_Tel;
	}

	public void setCustomer_Tel(String customer_Tel) {
		Customer_Tel = customer_Tel;
	}

	public String getSaler_Name() {
		return Saler_Name;
	}

	public void setSaler_Name(String saler_Name) {
		Saler_Name = saler_Name;
	}

	public String getCustomer_Area() {
		return Customer_Area;
	}

	public void setCustomer_Area(String customer_Area) {
		Customer_Area = customer_Area;
	}

	public String getCustomer_Address() {
		return Customer_Address;
	}

	public void setCustomer_Address(String customer_Address) {
		Customer_Address = customer_Address;
	}

	public String getCustomer_Type() {
		return Customer_Type;
	}

	public void setCustomer_Type(String customer_Type) {
		Customer_Type = customer_Type;
	}

	 

	@Override
	public String toString() {
		return "CustomerModel [Customer_LinkMan=" + Customer_LinkMan
				+ ", Customer_Tel=" + Customer_Tel + ", Saler_Name="
				+ Saler_Name + ", Customer_Area=" + Customer_Area
				+ ", Customer_Address=" + Customer_Address + ", Customer_Type="
				+ Customer_Type + ", CustomerGroupName=" + CustomerGroupName
				+ "]";
	}

	public static List<CustomerModel> GetCustomerList(byte[] response) throws AppException {
		if (null == response) {
			return null;

		}

		JSONArray array = null;
		List<CustomerModel> datas = null;
		CustomerModel tmp = null;
		JSONObject tmp_obj = null;

		try {
			datas = new ArrayList<CustomerModel>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp_obj = array.getJSONObject(i);
				tmp = new CustomerModel();

				tmp.Customer_id = tmp_obj.getInt("CustomerID");
				tmp.Customer_Name = tmp_obj.getString("CustomerName");
				tmp.Customer_LinkMan = tmp_obj.getString("CustomerLinkMan");
				tmp.Customer_Tel = tmp_obj.getString("CustomerTel");
				tmp.Saler_Name = tmp_obj.getString("SalerName");
				tmp.Customer_Area = tmp_obj.getString("CustomerArea");
				tmp.Customer_Address = tmp_obj.getString("CustomerAddress");
				tmp.CustomerGroupName = tmp_obj.getString("CustomerGroupName");
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

	public static List<CustomerModel> GetCustomerListByCounter(byte[] response) throws AppException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (null == response) {
			return null;

		}
		JSONArray array = null;
		List<CustomerModel> datas = null;
		CustomerModel tmp = null;
		JSONObject tmp_obj = null;
		try {
			datas = new ArrayList<CustomerModel>();
			array = new JSONObject(new String(response)).getJSONArray("d");
			if (null == array) {
				throw AppException.http(new Exception());
			}
			// Begin
			for (int i = 0; i < array.length(); i++) {
				tmp = new CustomerModel();
				tmp_obj = array.getJSONObject(i);
				tmp.Customer_id = tmp_obj.getInt("CustomerID");
				tmp.Customer_Name = tmp_obj.getString("CustomerName");
				tmp.Customer_LinkMan = tmp_obj.getString("CustomerLinkMan");
				tmp.Customer_Tel = tmp_obj.getString("CustomerTel");
				tmp.Saler_Name = tmp_obj.getString("SalerName");
				tmp.Customer_Area =tmp_obj.getString("CustomerArea");
				tmp.Customer_Address = tmp_obj.getString("CustomerAddress");
				tmp.Customer_Type = tmp_obj.getString("CustomerType");
				tmp.CustomerGroupName = tmp_obj.getString("CustomerGroupName");
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
