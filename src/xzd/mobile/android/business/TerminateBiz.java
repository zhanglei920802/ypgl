package xzd.mobile.android.business;

import java.util.List;

import android.content.Context;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.basic.ApiClient;
import xzd.mobile.android.model.AreaInfo;
import xzd.mobile.android.model.CustGroup;
import xzd.mobile.android.model.CustomList;
import xzd.mobile.android.model.CustomerConsume;
import xzd.mobile.android.model.CustomerInfoModel;
import xzd.mobile.android.model.CustomerModel;
import xzd.mobile.android.model.SalerVisitSimpleInfoList;
import xzd.mobile.android.model.StoChangeList;
import xzd.mobile.android.model.VisistRecordList;
import xzd.mobile.android.model.VisitRecordInfo;

public class TerminateBiz {
	private static TerminateBiz terminateBiz = null;

	private TerminateBiz() {
		// TODO Auto-generated constructor stub
	}

	public static TerminateBiz getInstance() {
		if (null == terminateBiz) {
			terminateBiz = new TerminateBiz();
		}
		return terminateBiz;
	}

	public static int SaveVisitReview(int visitid, int managerid,
			String managername, String answer, String location)
			throws AppException {
		return ApiClient.SaveVisitReview(visitid, managerid, managername,
				answer, location);
	}

	public CustomList GetCustomerList(AppContext appcontext, int pageIndex,
			int pageSize, int userid, boolean isrefresh, String name,
			int groupID, boolean b) throws AppException {
		CustomList datas = null;
		String key = null;
		if (!b) {
			key = "terminate_" + pageIndex + "_" + pageSize + "_" + userid;
		} else {
			key = "terminate_" + pageIndex + "_" + pageSize + "_" + userid
					+ "_" + name + "_" + groupID;
		}

		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new CustomList();
				if (!b) {
					datas.setCuLists(ApiClient.getInstance().GetCustomerList(
							pageIndex, pageSize, userid));
				} else {
					datas.setCuLists(GetCustomerListByCounter(pageIndex,
							pageSize, userid, name, groupID));

				}

				if (datas != null && datas.getCuLists().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}

			} catch (AppException e) {
				datas = (CustomList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (CustomList) appcontext.readObject(key);

		}

		return datas;

	}

	public CustomList GetCustomerListByManager(AppContext appcontext,
			int pageIndex, int pageSize, int userid, boolean isrefresh,
			String name, String areacode) throws AppException {
		CustomList datas = null;
		String key = null;

		key = "GetCustomerListByManager_" + pageIndex + "_" + pageSize + "_"
				+ userid + "_" + name + "_" + areacode;

		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new CustomList();

				datas.setCuLists(ApiClient.GetCustomerListByManager(pageIndex,
						pageSize, userid, name, areacode));

				if (datas != null && datas.getCuLists().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}

			} catch (AppException e) {
				datas = (CustomList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (CustomList) appcontext.readObject(key);

		}

		return datas;

	}

	public static StoChangeList GetStoChanges(AppContext appcontext,
			int pageIndex, int pageSize, int visit_id, boolean isrefresh)
			throws AppException {
		StoChangeList datas = null;

		String key = "getstochanges_" + pageIndex + "_" + pageSize + "_"
				+ visit_id;
		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new StoChangeList();
				datas.setDatas(ApiClient.getInstance().GetStoChanges(pageIndex,
						pageSize, visit_id));

				if (datas != null && datas.getDatas().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (StoChangeList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (StoChangeList) appcontext.readObject(key);

		}

		return datas;

	}

	public SalerVisitSimpleInfoList QueryCountermenAndVisitDate(int userid,
			String name, AppContext appcontext, boolean isrefresh)
			throws AppException {
		SalerVisitSimpleInfoList datas = null;

		String key = "QueryCountermenAndVisitDate" + userid + "_" + name;
		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new SalerVisitSimpleInfoList();
				datas.setmDatas(ApiClient.getInstance()
						.QueryCountermenAndVisitDate(userid, name));

				if (datas != null && datas.getmDatas().size() > 0) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (SalerVisitSimpleInfoList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (SalerVisitSimpleInfoList) appcontext.readObject(key);

		}

		return datas;

	}

	public static SalerVisitSimpleInfoList GetCountermenPage(
			AppContext appcontext, int pageIndex, int pageSize, int userid,
			boolean isrefresh) throws AppException {
		SalerVisitSimpleInfoList datas = null;

		String key = "GetCountermenPage" + pageIndex + "_" + pageSize + "_"
				+ userid;
		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new SalerVisitSimpleInfoList();
				datas.setmDatas(ApiClient.getInstance().GetCountermenPage(
						pageIndex, pageSize, userid));

				if (datas != null && datas.getmDatas().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (SalerVisitSimpleInfoList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (SalerVisitSimpleInfoList) appcontext.readObject(key);

		}

		return datas;

	}

	public static VisistRecordList GetVisitRecordsBySaler(
			AppContext appcontext, int pageIndex, int pageSize, int salerid,
			String customername, boolean isrefresh) throws AppException {
		VisistRecordList datas = null;

		String key = "GetVisitRecordsBySaler" + pageIndex + "_" + pageSize
				+ "_" + salerid + "_" + customername;
		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new VisistRecordList();
				ApiClient.getInstance();
				datas.setDatas(ApiClient.GetVisitRecordsBySaler(pageIndex,
						pageSize, salerid, customername));

				if (datas != null && datas.getDatas().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (VisistRecordList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (VisistRecordList) appcontext.readObject(key);

		}

		return datas;

	}

	@SuppressWarnings("static-access")
	public VisistRecordList GetVisitRecords(AppContext appcontext,
			int pageIndex, int pageSize, int userid, boolean isrefresh,
			int cusID, boolean isSeachMode) throws AppException {
		VisistRecordList datas = null;

		String key = null;
		if (!isSeachMode) {// 判定用户当前是否为搜索模式
			key = "getvisitrecords_" + pageIndex + "_" + pageSize + "_"
					+ userid;
		} else {
			key = "terminate_" + pageIndex + "_" + pageSize + "_" + userid
					+ "_" + cusID;
		}
		if (appcontext.isNetworkConnected()
				&& (!appcontext.isReadDataCache(key) || isrefresh)) {

			try {
				datas = new VisistRecordList();
				if (!isSeachMode) {
					datas.setDatas((ApiClient.getInstance().GetVisitRecords(
							pageIndex, pageSize, userid)));

				} else {
					datas.setDatas((ApiClient.getInstance().QueryVisitRecords(
							pageIndex, AppContext.PAGE_SIZE, userid, cusID)));

				}

				if (datas != null && datas.getDatas().size() > 0
						&& pageIndex == 1) {

					datas.setCacheKey(key);
					appcontext.saveObject(datas, key, Context.MODE_PRIVATE);

				}
			} catch (AppException e) {
				datas = (VisistRecordList) appcontext.readObject(key);
				if (datas == null) {
					throw e;
				}
			}

		} else {

			datas = (VisistRecordList) appcontext.readObject(key);

		}

		return datas;

	}

	public List<CustomerConsume> GetCustomerConsume(int saleid, int customerid,
			int medicineid) throws AppException {
		return ApiClient.getInstance().GetCustomerConsume(saleid, customerid,
				medicineid);
	}

	public CustomerInfoModel GetCustomerInfo(AppContext appContext, int cus_id,
			int user_id, boolean isReresh) throws AppException {
		CustomerInfoModel customerInfoModel = null;
		String cacheKey = "custominfo_" + cus_id + "_" + user_id;
		if (appContext.isNetworkConnected()
				&& (!appContext.isExistDataCache(cacheKey) || isReresh)) {
			try {
				customerInfoModel = ApiClient.getInstance().GetCustomerInfo(
						cus_id);
				if (customerInfoModel != null) {
					appContext.saveObject(customerInfoModel, cacheKey,
							Context.MODE_PRIVATE);
				}
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customerInfoModel = (CustomerInfoModel) appContext
						.readObject(cacheKey);
				if (null == customerInfoModel) {
					throw e;
				}
			}
		} else {
			customerInfoModel = (CustomerInfoModel) appContext
					.readObject(cacheKey);

		}

		return customerInfoModel;
	}

	public int SaveVisitRecordInfo(int userid, int customerid,
			String visitaddress, String remark, String location,
			String medicineids, String medicinenums) throws AppException {
		return ApiClient.getInstance().SaveVisitRecordInfo(userid, customerid,
				visitaddress, remark, location, medicineids, medicinenums);
	}

	public int SaveStoChangeInfo(int userid, int visitid, int customerid,
			int medicineid, int num, String location) throws AppException {
		return ApiClient.getInstance().SaveStoChangeInfo(userid, visitid,
				customerid, medicineid, num, location);
	}

	public List<AreaInfo> GetAreaList(String level, String code)
			throws AppException {
		return ApiClient.getInstance().GetAreaList(level, code);
	}

	public boolean SaveCustomerInfo(int userid, String areacode,
			String cusname, String linkman, String tel, String fax,
			String cusintr, String address, int groupID, String medicineid)
			throws AppException {
		return ApiClient.getInstance().SaveCustomerInfo(userid, areacode,
				cusname, linkman, tel, fax, cusintr, address, groupID,
				medicineid);

	}

	public boolean UpdateCustomerInfo(int cusid, String areacode,
			String cusname, String linkman, String tel, String fax,
			String cusintr, String address, int groupID, String medicineid)
			throws AppException {
		return ApiClient.getInstance().UpdateCustomerInfo(cusid, areacode,
				cusname, linkman, tel, fax, cusintr, address, groupID,
				medicineid);
	}

	public VisitRecordInfo GetVisitRecordInfo(AppContext appContext,
			int visitid, int user_id, boolean isReresh) throws AppException {
		VisitRecordInfo visitRecordInfo = null;
		String cacheKey = "GetVisitRecordInfo_" + visitid + "_" + user_id;
		if (appContext.isNetworkConnected()
				&& (!appContext.isExistDataCache(cacheKey) || isReresh)) {
			try {
				visitRecordInfo = ApiClient.getInstance().GetVisitRecordInfo(
						visitid);
				if (visitRecordInfo != null) {
					appContext.saveObject(visitRecordInfo, cacheKey,
							Context.MODE_PRIVATE);
				}
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				visitRecordInfo = (VisitRecordInfo) appContext
						.readObject(cacheKey);
				if (null == visitRecordInfo) {
					throw e;
				}
			}
		} else {
			visitRecordInfo = (VisitRecordInfo) appContext.readObject(cacheKey);

		}

		return visitRecordInfo;
	}

	public int UpdateVisitRecordInfo(int visitid, int customerid,
			String visitaddress, String remark, String location,
			String medicineids, String medicinenums) throws AppException {
		return ApiClient.getInstance().UpdateVisitRecordInfo(visitid,
				customerid, visitaddress, remark, location, medicineids,
				medicinenums);
	}

	public static List<CustGroup> GetCustGroupBySalerID(int salerid)
			throws AppException {
		return ApiClient.getInstance().GetCustGroupBySalerID(salerid);
	}

	public static List<CustomerModel> GetCustomerListByCounter(int pageIndex,
			int pageSize, int userid, String name, int groupid)
			throws AppException {
		return ApiClient.GetCustomerListByCounter(pageIndex, pageSize, userid,
				name, groupid);
	}

	public static String GetGroupNameByGroupID(int groupid) throws AppException {
		return ApiClient.GetGroupNameByGroupID(groupid);
	}

	public static int AddCustomerGroupBySaler(int salerid, String groupname)
			throws AppException {
		return ApiClient.AddCustomerGroupBySaler(salerid, groupname);
	}

	public static int UpdateCustGroup(int grpid, String groupname)
			throws AppException {
		return ApiClient.UpdateCustGroup(grpid, groupname);
	}

	public static int DeleteCustomerGroup(int groupid) throws AppException {
		return ApiClient.DeleteCustomerGroup(groupid);
	}
}
