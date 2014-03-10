package xzd.mobile.android.business;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.basic.ApiClient;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.model.Medicine;
import xzd.mobile.android.model.MedicineList;
import xzd.mobile.android.model.MedicineOrder;
import xzd.mobile.android.model.MedicineOrderDetail;
import xzd.mobile.android.model.SalerOrderList;
import xzd.mobile.android.model.SalerSimple;

/**
 * 二〇一三年七月七日 00:22:02
 * 
 * @author ZhangLei
 * 
 */
public class OrderHelper {

	private static OrderHelper orderhelper = null;

	// private AppContext appcontext = null;

	public static OrderHelper getInstance() {
		if (null == orderhelper) {
			orderhelper = new OrderHelper();
		}

		return orderhelper;
	}

	public static List<CustomerSimple> GetSimCustomersBySalerID(int salerid)
			throws Exception {
		return ApiClient.GetSimCustomersBySalerID(salerid);
	}

	public MedicineList GetMedicineOrders(int pageIndex, int pageSize,
			String userid, boolean isrefresh, AppContext appcontext)
			throws AppException {
		MedicineList datas = null;

		String key = "order_" + pageIndex + "_" + pageSize + "";
		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new MedicineList();
				datas.setMedicine_list(ApiClient.getInstance()
						.GetMedicineOrders(pageIndex, pageSize, userid));

				if (datas != null && datas.getMedicine_list().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (MedicineList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (MedicineList) appcontext.readObject(key);

		}

		return datas;
	}

	public MedicineList QueryMedicineOrder(int pageIndex, int pageSize, int id,
			int type, AppContext appcontext, boolean isrefresh)
			throws AppException {
		MedicineList datas = null;

		String key = " Query_" + pageIndex + "_" + pageSize + "_" + id + "_"
				+ type;
		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new MedicineList();
				datas.setMedicine_list(ApiClient.getInstance()
						.QueryMedicineOrder(pageIndex, pageSize, id, type));

				if (datas != null && datas.getMedicine_list().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (MedicineList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (MedicineList) appcontext.readObject(key);

		}

		return datas;
	}

	public List<Medicine> getMedicineList(int userid) throws Exception {
		return ApiClient.getInstance().GetMedicinesBySalerID(userid);
	}

	public List<Medicine> GetSimMedicinesBySalerID(int salerid)
			throws Exception {
		return ApiClient.getInstance().GetSimMedicinesBySalerID(salerid);
	}

	public List<CustomerSimple> getCustomerSimpleList(int userid)
			throws Exception {
		return ApiClient.getInstance().GetSimpleCustomersBySalerID(userid);
	}

	public static boolean SaveMedicineOrders(int userid, int customerid,
			String medicineids, String medicinenums, double totalprice,
			String remark, String location) throws AppException {
		return ApiClient.SaveMedicineOrders(userid, customerid, medicineids,
				medicinenums, totalprice, remark, location);
	}

	public static List<CustomerSimple> GetSimCustomerListByCounter(
			int pageIndex, int pageSize, int userid) throws Exception {
		return ApiClient.GetSimCustomerListByCounter(pageIndex, pageSize,
				userid);
	}

	public void SaveEditInfo() {

	}

	public static List<CustomerSimple> QuerySimCustomerListByCounter(
			int userid, String name) throws Exception {
		return ApiClient.QuerySimCustomerListByCounter(userid, name);
	}

	public void GetEditInfo() {

	}

	/**
	 * 获取应用程序首选项
	 * 
	 * @param context
	 * @return
	 */
	public SharedPreferences getSharedPrefrence(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 根据订单号，获取订单详细信息
	 * 
	 * @param appContext
	 * @param user_id
	 * @param orderid
	 * @param isReresh
	 * @return
	 * @throws AppException
	 */
	public MedicineOrderDetail GetMedicineOrderDetail(int orderid)
			throws AppException {
		return ApiClient.getInstance().GetMedicineOrderDetail(orderid);
		// MedicineOrderDetail customerInfoModel = null;
		// String cacheKey = "medicineorderdetail_" + user_id + "_" + orderid;
		// if (appContext.isNetworkConnected()
		// && (!appContext.isExistDataCache(cacheKey) || isReresh)) {
		// try {
		// customerInfoModel = ApiClient.getInstance()
		// .GetMedicineOrderDetail(orderid);
		// if (customerInfoModel != null) {
		// appContext.saveObject(customerInfoModel, cacheKey,
		// Context.MODE_PRIVATE);
		// }
		// } catch (AppException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// customerInfoModel = (MedicineOrderDetail) appContext
		// .readObject(cacheKey);
		// if (null == customerInfoModel) {
		// throw e;
		// }
		// }
		// } else {
		// customerInfoModel = (MedicineOrderDetail) appContext
		// .readObject(cacheKey);
		//
		// }
		//
		// return customerInfoModel;
	}

	/**
	 * 判断是否为销售人员
	 * 
	 * @param sale_id
	 * @return
	 * @throws AppException
	 */
	public int getCounterMan(int sale_id) throws AppException {
		return ApiClient.getInstance().IsCounterMan(sale_id);
	}

	public List<SalerSimple> GetCountermen(int userid) throws AppException {
		return ApiClient.getInstance().GetCountermen(userid);
	}

	public static List<SalerSimple> QueryCountermen(int userid, String name)
			throws AppException {
		return ApiClient.QueryCountermen(userid, name);
	}

	public boolean UpdateMedicineOrders(int orderid, int customerid,
			String medicineids, String medicinenums, double totalprice,
			String remark, String location) throws AppException {
		return ApiClient.UpdateMedicineOrders(orderid, customerid, medicineids,
				medicinenums, totalprice, remark, location);
	}

	public int GetTodayOrdersCount(int userid) throws AppException {
		return ApiClient.getInstance().GetTodayOrdersCount(userid);
	}

	public MedicineOrder GetMedicineOrderInfo(int orderid) throws AppException {
		return ApiClient.getInstance().GetMedicineOrderInfo(orderid);
	}

	public static List<CustomerMedicines> GetCustomerMedcinesByCustomerID(
			int custid) throws AppException {
		return ApiClient.GetCustomerMedcinesByCustomerID(custid);

	}

	public static SalerOrderList GetSalerAndOrderCount(int pageIndex,
			int pageSize, int userid, boolean isrefresh, AppContext appcontext)
			throws AppException {
		SalerOrderList datas = null;

		String key = " GetSalerAndOrderCount_" + pageIndex + "_" + pageSize
				+ "userid_" + userid;

		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new SalerOrderList();
				datas.setDatas(ApiClient.GetSalerAndOrderCount(pageIndex,
						pageSize, userid));

				if (datas != null && datas.getDatas().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (SalerOrderList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (SalerOrderList) appcontext.readObject(key);

		}

		return datas;

	}

	public static SalerOrderList QuerySalerAndOrderCount(int userid,
			String name, boolean isrefresh, AppContext appcontext)
			throws AppException {

		SalerOrderList datas = null;

		String key = " QuerySalerAndOrderCount_userid" + userid + "_name"
				+ name;

		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new SalerOrderList();
				datas.setDatas(ApiClient.QuerySalerAndOrderCount(userid, name));

				if (datas != null && datas.getDatas().size() > 0) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (SalerOrderList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (SalerOrderList) appcontext.readObject(key);

		}

		return datas;
	}

	public static int SaveOrderReview(int orderid, int salerlevel,
			int reviewresult, String location) throws AppException {
		return ApiClient.SaveOrderReview(orderid, salerlevel, reviewresult,
				location);
	}
}
