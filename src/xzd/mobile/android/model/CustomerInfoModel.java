package xzd.mobile.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;

/// <summary>
/// 终端客户信息
/// </summary>
public class CustomerInfoModel extends CustomerModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6089816584756763322L;
	private String Customer_intr;
	private int Customer_LinkManSex;
	private String Customer_FAX;
	private String Customer_Licence;
	private String Customer_CA;
	private String CustomerAreaCode = null;
	private int CustomerGroupID = 0;
	private String CustomerMedicineName = null;
	private String CustomerMedicineID =null;
	
	public int getCustomerGroupID() {
		return CustomerGroupID;
	}

	public void setCustomerGroupID(int customerGroupID) {
		CustomerGroupID = customerGroupID;
	}

	public String getCustomerMedicineName() {
		return CustomerMedicineName;
	}

	public void setCustomerMedicineName(String customerMedicineName) {
		CustomerMedicineName = customerMedicineName;
	}

	public String getCustomerMedicineID() {
		return CustomerMedicineID;
	}

	public void setCustomerMedicineID(String customerMedicineID) {
		CustomerMedicineID = customerMedicineID;
	}

	public void UpdateContent(CustomerInfoModel data) {
		Customer_intr = data.Customer_intr;
		Customer_LinkManSex = data.Customer_LinkManSex;
		Customer_FAX = data.Customer_FAX;
		Customer_Licence = data.Customer_Licence;
		Customer_CA = data.Customer_CA;
		CustomerAreaCode = data.CustomerAreaCode;
		CustomerMedicineID = data.CustomerMedicineID;
		CustomerGroupID = data.CustomerGroupID;
		CustomerMedicineName = data.CustomerMedicineName;
	}

	public CustomerInfoModel(CustomerModel data) {
		Customer_id = data.Customer_id;
		Customer_Name = data.Customer_Name;
		Customer_LinkMan = data.Customer_LinkMan;
		Customer_Tel = data.Customer_Tel;
		Saler_Name = data.Saler_Name;
		Customer_Area = data.Customer_Area;
		Customer_Address = data.Customer_Address;
		Customer_Type = data.Customer_Type;

	}

	public CustomerInfoModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	@Override
	public String toString() {
		return "CustomerInfoModel [Customer_intr=" + Customer_intr
				+ ", Customer_LinkManSex=" + Customer_LinkManSex
				+ ", Customer_FAX=" + Customer_FAX + ", Customer_Licence="
				+ Customer_Licence + ", Customer_CA=" + Customer_CA
				+ ", CustomerAreaCode=" + CustomerAreaCode
				+ ", CustomerGroupID=" + CustomerGroupID
				+ ", CustomerMedicineName=" + CustomerMedicineName
				+ ", CustomerMedicineID=" + CustomerMedicineID + "]";
	}

	public String getCustomer_intr() {
		return Customer_intr;
	}

	public void setCustomer_intr(String customer_intr) {
		Customer_intr = customer_intr;
	}

	public int getCustomer_LinkManSex() {
		return Customer_LinkManSex;
	}

	public void setCustomer_LinkManSex(int customer_LinkManSex) {
		Customer_LinkManSex = customer_LinkManSex;
	}

	public String getCustomer_FAX() {
		return Customer_FAX;
	}

	public String getCustomerAreaCode() {
		return CustomerAreaCode;
	}

	public void setCustomerAreaCode(String customerAreaCode) {
		CustomerAreaCode = customerAreaCode;
	}

	public void setCustomer_FAX(String customer_FAX) {
		Customer_FAX = customer_FAX;
	}

	public String getCustomer_Licence() {
		return Customer_Licence;
	}

	public void setCustomer_Licence(String customer_Licence) {
		Customer_Licence = customer_Licence;
	}

	public String getCustomer_CA() {
		return Customer_CA;
	}

	public void setCustomer_CA(String customer_CA) {
		Customer_CA = customer_CA;
	}

	public static CustomerInfoModel GetCustomerInfo(byte[] responseData)
			throws AppException {

		if (null == responseData) {
			return null;

		}

		JSONObject obj = null;
		CustomerInfoModel data = null;

		try {
			obj = new JSONObject(new String(responseData)).getJSONObject("d");
			if (null == obj) {
				throw AppException.http(new Exception());
			}
			data = new CustomerInfoModel();

			data.Customer_id = obj.getInt("CustomerID");
			data.Customer_Name = obj.getString("CustomerName");
			data.Customer_LinkMan = obj.getString("CustomerLinkMan");
			data.Customer_Tel = obj.getString("CustomerTel");
			data.Saler_Name = obj.getString("SalerName");
			data.Customer_Area = obj.getString("CustomerArea");
			data.Customer_Address = obj.getString("CustomerAddress");

			data.Customer_Type = obj.getString("CustomerType");
			data.Customer_intr = obj.getString("Customerintr");
			data.Customer_LinkManSex = obj.getInt("CustomerLinkManSex");
			data.Customer_FAX = obj.getString("CustomerFAX");
			data.Customer_Licence = obj.getString("CustomerLicence");
			data.Customer_CA = obj.getString("CustomerCA");
			data.CustomerAreaCode = obj.getString("CustomerAreaCode");
			data.CustomerGroupID = obj.getInt("CustomerGroupID");
			data.CustomerMedicineName= obj.getString("CustomerMedicineName");
			data.CustomerMedicineID = obj.getString("CustomerMedicineID");
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.IO(e);
		}

		return data;

	}
}
