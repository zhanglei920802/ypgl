package xzd.mobile.android.basic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import xzd.mobile.android.AppException;
import xzd.mobile.android.business.CustomerConsumeDetail;
import xzd.mobile.android.business.CustomerMedicines;
import xzd.mobile.android.model.AreaInfo;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustGroup;
import xzd.mobile.android.model.CustomerConsume;
import xzd.mobile.android.model.CustomerInfoModel;
import xzd.mobile.android.model.CustomerModel;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.model.LinkMan;
import xzd.mobile.android.model.Medicine;
import xzd.mobile.android.model.MedicineOrder;
import xzd.mobile.android.model.MedicineOrderDetail;
import xzd.mobile.android.model.SalerOrderCountInfo;
import xzd.mobile.android.model.SalerSimple;
import xzd.mobile.android.model.SalerVisitSimpleInfo;
import xzd.mobile.android.model.StoChange;
import xzd.mobile.android.model.User;
import xzd.mobile.android.model.VisitRecord;
import xzd.mobile.android.model.VisitRecordInfo;

public class ApiClient {
	private final static String CHATSET_UTF8 = "utf-8";
	private static ApiClient mApiClient = null;
	public static final String TAG = "ApiClient";

	public static synchronized ApiClient getInstance() {
		if (mApiClient == null) {
			mApiClient = new ApiClient();
		}
		return mApiClient;
	}

	public static void releaseMem() {

		if (mApiClient != null) {
			mApiClient = null;
		}

	}

	/**
	 * 获取一个HttpPost对象
	 * 
	 * @param url
	 *            请求Url
	 * @param json
	 *            发送数据
	 * @return
	 * @throws AppException
	 */
	public static HttpPost getHttpPost(String url, JSONObject json) {
		HttpPost httpPost = null;

		HttpEntity enity = null;

		try {

			httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type",
					"application/json; charset=utf-8");
			enity = new StringEntity(json.toString(), CHATSET_UTF8);
			httpPost.setEntity(enity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		} finally {

			if (null != enity) {
				try {
					enity.consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				enity = null;
			}

		}

		return httpPost;
	}

	/**
	 * 发送一个Post请求
	 * 
	 * @param post
	 * @return
	 * @throws AppException
	 */
	public static byte[] doHttpPost(HttpPost post) throws AppException {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		if (null == post) {
			return null;

		}
		HttpResponse response;
		HttpEntity httpentity = null;
		InputStream ins = null;
		ByteArrayOutputStream baos = null;
		try {
			response = httpclient.execute(post);
			httpentity = response.getEntity();
			ins = httpentity.getContent();

			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;

			while ((len = ins.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}

			baos.flush();
			httpentity.consumeContent();
			ins.close();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof AppException) {
				throw (AppException) e;
			}
		} finally {
			ins = null;
			httpentity = null;
			httpclient = null;

		}

		return baos.toByteArray();
	}

	/**
	 * 登录成功,返回用户模型
	 * 
	 * 登录失败,抛出异常
	 * 
	 * @param uname
	 * @param pwd
	 * @param isEncryp
	 * @return
	 * @throws Exception
	 */
	public User doLogin(String uname, String pwd, int isEncryp)
			throws Exception {
		JSONObject params = null;

		User user = null;
		try {
			params = new JSONObject();
			params.put("isEncryp", isEncryp);
			params.put("uname", uname);
			params.put("pwd", pwd);

			user = User.PasersJson(doHttpPost(getHttpPost(Config.LOGIN_URL,
					params)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			params = null;
		}

		if (user == null) {
			throw new Exception("登录失败");
		}

		return user;
	}

	/**
	 * 获取药品订单列表
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @param userid
	 * @return
	 * @throws Exception
	 * 
	 *             解析数据失败,抛出异常
	 */
	public List<MedicineOrder> GetMedicineOrders(int pageIndex, int pageSize,
			String userid) throws AppException {
		JSONObject params = null;
		List<MedicineOrder> datas = null;

		try {
			params = new JSONObject();
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);

			datas = MedicineOrder.parseMediceOrders(doHttpPost(getHttpPost(
					Config.GETMEDICINEORDERS_URL, params)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.http(e);

		} finally {
			params = null;
		}

		if (null == datas) {
			datas = new ArrayList<MedicineOrder>();
		}

		return datas;
	}

	/**
	 * 获取药品订单列表
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @param userid
	 * @return
	 * @throws Exception
	 * 
	 *             解析数据失败,抛出异常
	 */
	public List<MedicineOrder> QueryMedicineOrder(int pageindex, int pagesize,
			int id, int type) throws AppException {
		JSONObject params = null;
		List<MedicineOrder> datas = null;

		try {
			params = new JSONObject();
			params.put("pageindex", pageindex);
			params.put("pagesize", pagesize);
			params.put("id", id);
			params.put("type", type);
			datas = MedicineOrder.parseMediceOrders(doHttpPost(getHttpPost(
					Config.QUERYMEDICINEORDER_URL, params)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.http(e);

		} finally {
			params = null;
		}

		return datas;
	}

	public MedicineOrder GetMedicineOrderInfo(int orderid) throws AppException {
		JSONObject params = null;
		MedicineOrder datas = null;

		try {
			params = new JSONObject();

			params.put("orderid", orderid);
			datas = MedicineOrder.GetMedicineOrderInfo(doHttpPost(getHttpPost(
					Config.GETMEDICINEORDERINFO_URL, params)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.http(e);

		} finally {
			params = null;
		}

		return datas;
	}

	/**
	 * 
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public List<Medicine> GetMedicinesBySalerID(int userid) throws Exception {
		JSONObject params = null;
		List<Medicine> datas = null;

		try {
			params = new JSONObject();
			params.put("userid", userid);

			datas = Medicine.ParseMedicineList(doHttpPost(getHttpPost(
					Config.GETMEDICINESBYSALERID_URL, params)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			params = null;
		}

		return datas;
	}

	public List<Medicine> GetSimMedicinesBySalerID(int salerid)
			throws Exception {
		JSONObject params = null;
		List<Medicine> datas = null;

		try {
			params = new JSONObject();
			params.put("salerid", salerid);

			datas = Medicine.ParseMedicineList(doHttpPost(getHttpPost(
					Config.GETSIMMEDICINESBYSALERID_URL, params)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			params = null;
		}

		return datas;
	}

	public List<CustomerSimple> GetSimpleCustomersBySalerID(int userid)
			throws Exception {
		JSONObject params = null;
		List<CustomerSimple> datas = null;

		try {
			params = new JSONObject();

			params.put("userid", userid);

			datas = CustomerSimple
					.parseCustomSimpleList(doHttpPost(getHttpPost(
							Config.GETSIMPLECUSTOMERSBYSALERID_URL, params)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			params = null;
		}

		return datas;
	}

	public static List<CustomerSimple> GetSimCustomerListByCounter(
			int pageIndex, int pageSize, int userid) throws Exception {
		JSONObject params = null;
		List<CustomerSimple> datas = null;

		try {
			params = new JSONObject();
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);

			datas = CustomerSimple
					.parseCustomSimpleList(doHttpPost(getHttpPost(
							Config.GETGETSIMCUSTOMERLISTBYCOUNTER, params)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			params = null;
		}

		return datas;
	}

	public static List<CustomerSimple> QuerySimCustomerListByCounter(
			int userid, String name) throws Exception {
		JSONObject params = null;
		List<CustomerSimple> datas = null;

		try {
			params = new JSONObject();
			params.put("userid", userid);
			params.put("name", name);

			datas = CustomerSimple
					.parseCustomSimpleList(doHttpPost(getHttpPost(
							Config.QUERYSIMCUSTOMERLISTBYCOUNTER, params)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			params = null;
		}

		return datas;
	}

	public static List<CustomerSimple> GetSimCustomersBySalerID(int salerid)
			throws Exception {
		JSONObject params = null;
		List<CustomerSimple> datas = null;

		try {
			params = new JSONObject();

			params.put("salerid", salerid);

			datas = CustomerSimple
					.parseCustomSimpleList(doHttpPost(getHttpPost(
							Config.GETGETSIMCUSTOMERSBYSALERID, params)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			params = null;
		}

		return datas;
	}

	/**
	 * 提交药品订单，成功返回true.失败返回false
	 * 
	 * @param userid
	 * @param customerid
	 * @param medicineid
	 * @param account
	 * @param remark
	 * @param location
	 * @return
	 * @throws AppException
	 */
	// / <summary>
	// / 保存药品订单
	// / </summary>
	// / <param name="userid">登陆用户编号</param>
	// / <param name="customerid">终端编号</param>
	// / <param name="medicineid">药品编号</param>
	// / <param name="account">数量</param>
	// / <param name="remark">订单申请备注</param>
	// / <param name="location">订单产生位置</param>
	// / <returns>成功返回添加的该记录的编号，否则返回0</returns>
	public static boolean SaveMedicineOrders(int userid, int customerid,
			String medicineids, String medicinenums, double totalprice,
			String remark, String location) throws AppException {
		JSONObject params = null;
		boolean result = false;
		try {
			params = new JSONObject();

			params.put("userid", userid);
			params.put("customerid", customerid);
			params.put("medicineids", medicineids);
			params.put("medicinenums", medicinenums);
			params.put("totalprice", totalprice);
			params.put("remark", remark);
			params.put("location", location);
			if (new JSONObject(new String(doHttpPost(getHttpPost(
					Config.SAVEMEDICINEORDERS_URL, params)))).getInt("d") > 0) {
				result = true;
			} else {
				result = false;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	public static boolean UpdateMedicineOrders(int orderid, int customerid,
			String medicineids, String medicinenums, double totalprice,
			String remark, String location) throws AppException {
		JSONObject params = null;
		boolean result = false;
		try {
			params = new JSONObject();

			params.put("orderid", orderid);
			params.put("customerid", customerid);
			params.put("medicineids", medicineids);
			params.put("medicinenums", medicinenums);
			params.put("totalprice", totalprice);
			params.put("remark", remark);
			params.put("location", location);
			String json = new String(doHttpPost(getHttpPost(
					Config.UPDATEMEDICINEORDERS_URL, params)));
			System.out.println("ApiClient.UpdateMedicineOrders() json_data{"+json+"}");
			if (new JSONObject(json).getInt("d") > 0) {
				result = true;
			} else {
				result = false;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	/**
	 * 获取终端列表
	 * 
	 * @param pageIndex
	 *            索引
	 * @param pageSize
	 *            页面大小
	 * @param userid
	 *            用户ID
	 * @return
	 * 
	 * @throws AppException
	 */
	public List<CustomerModel> GetCustomerList(int pageIndex, int pageSize,
			int userid) throws AppException {
		JSONObject params = null;
		List<CustomerModel> datas = null;

		try {
			params = new JSONObject();

			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);
			datas = CustomerModel.GetCustomerList((doHttpPost(getHttpPost(
					Config.GETCUSTOMERLIST_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public List<SalerSimple> GetCountermen(int userid) throws AppException {
		JSONObject params = null;
		List<SalerSimple> datas = null;

		try {
			params = new JSONObject();

			params.put("userid", userid);

			datas = SalerSimple.GetCountermen((doHttpPost(getHttpPost(
					Config.GETCOUNTERMEN_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public List<CustomerConsume> GetCustomerConsume(int saleid, int customerid,
			int medicineid) throws AppException {
		JSONObject params = null;
		List<CustomerConsume> datas = null;

		try {
			params = new JSONObject();

			params.put("saleid", saleid);
			params.put("customerid", customerid);
			params.put("medicineid", medicineid);
			datas = CustomerConsume.GetCustomerConsume((doHttpPost(getHttpPost(
					Config.GETCUSTOMERCONSUME_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public List<SalerVisitSimpleInfo> GetCountermenPage(int pageIndex,
			int pageSize, int userid) throws AppException {
		JSONObject params = null;
		List<SalerVisitSimpleInfo> datas = null;

		try {
			params = new JSONObject();

			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);
			datas = SalerVisitSimpleInfo
					.GetCountermenPage((doHttpPost(getHttpPost(
							Config.GETCOUNTERMENPAGE_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public List<SalerVisitSimpleInfo> QueryCountermenAndVisitDate(int userid,
			String name) throws AppException {
		JSONObject params = null;
		List<SalerVisitSimpleInfo> datas = null;

		try {
			params = new JSONObject();

			params.put("userid", userid);
			params.put("name", name);

			datas = SalerVisitSimpleInfo
					.GetCountermenPage((doHttpPost(getHttpPost(
							Config.QUERYCOUNTERMENANDVISITDATE_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	/**
	 * 更根据用户ID获取终端详情
	 * 
	 * @param cusid
	 *            用户ID
	 * @return
	 * @throws AppException
	 */
	public CustomerInfoModel GetCustomerInfo(int cusid) throws AppException {
		JSONObject params = null;
		CustomerInfoModel data = null;

		try {
			params = new JSONObject();

			params.put("cusid", cusid);

			data = CustomerInfoModel.GetCustomerInfo((doHttpPost(getHttpPost(
					Config.GETCUSTOMERINFO_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return data;
	}

	public VisitRecordInfo GetVisitRecordInfo(int visitid) throws AppException {
		JSONObject params = null;
		VisitRecordInfo data = null;

		try {
			params = new JSONObject();

			params.put("visitid", visitid);

			data = VisitRecordInfo
					.GetMedicineOrderDetail((doHttpPost(getHttpPost(
							Config.GETVISITRECORDINFO_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return data;
	}

	/**
	 * 更根据用户ID获取终端详情
	 * 
	 * @param cusid
	 *            用户ID
	 * @return
	 * @throws AppException
	 */
	public MedicineOrderDetail GetMedicineOrderDetail(int orderid)
			throws AppException {
		JSONObject params = null;
		MedicineOrderDetail data = null;

		try {
			params = new JSONObject();

			params.put("orderid", orderid);

			data = MedicineOrderDetail
					.GetMedicineOrderDetail((doHttpPost(getHttpPost(
							Config.GETMEDICINEORDERDETAIL_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return data;
	}

	public static List<VisitRecord> GetVisitRecords(int pageIndex,
			int pageSize, int userid) throws AppException {
		JSONObject params = null;
		List<VisitRecord> datas = null;

		try {
			params = new JSONObject();

			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);
			datas = VisitRecord.GetVisitRecords((doHttpPost(getHttpPost(
					Config.GetVisitRecords_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static List<VisitRecord> GetVisitRecordsBySaler(int pageIndex,
			int pageSize, int salerid, String customername) throws AppException {
		JSONObject params = null;
		List<VisitRecord> datas = null;

		try {
			params = new JSONObject();

			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("salerid", salerid);
			params.put("customername", customername);
			datas = VisitRecord.GetVisitRecords((doHttpPost(getHttpPost(
					Config.GETVISITRECORDSBYSALER_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public List<VisitRecord> QueryVisitRecords(int pageIndex, int pageSize,
			int userid, int customerid) throws AppException {
		JSONObject params = null;
		List<VisitRecord> datas = null;

		try {
			params = new JSONObject();

			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);
			params.put("customerid", customerid);
			datas = VisitRecord.GetVisitRecords((doHttpPost(getHttpPost(
					Config.QUERYVISITRECORDS_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public List<AreaInfo> GetAreaList(String level, String code)
			throws AppException {
		JSONObject params = null;
		List<AreaInfo> datas = null;

		try {
			params = new JSONObject();

			params.put("level", level);
			params.put("code", code);

			datas = AreaInfo.GetAreaList((doHttpPost(getHttpPost(
					Config.GETAREALIST_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public List<StoChange> GetStoChanges(int pageIndex, int pageSize,
			int visitid) throws AppException {
		JSONObject params = null;
		List<StoChange> datas = null;

		try {
			params = new JSONObject();

			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("visitid", visitid);

			datas = StoChange.GetStoChanges((doHttpPost(getHttpPost(
					Config.GETSTOCHANGES_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public int SaveVisitRecordInfo(int userid, int customerid,
			String visitaddress, String remark, String location,
			String medicineids, String medicinenums) throws AppException {
		JSONObject params = null;
		int result = -1;
		try {
			params = new JSONObject();

			params.put("userid", userid);
			params.put("customerid", customerid);
			params.put("visitaddress", visitaddress);
			params.put("remark", remark);
			params.put("location", location);
			params.put("medicineids", medicineids);
			params.put("medicinenums", medicinenums);
			result = new JSONObject(new String(doHttpPost(getHttpPost(
					Config.SAVEVISITRECORDINFO_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	public int SaveStoChangeInfo(int userid, int visitid, int customerid,
			int medicineid, int num, String location) throws AppException {
		JSONObject params = null;
		int result = -1;
		try {
			params = new JSONObject();

			params.put("userid", userid);
			params.put("visitid", visitid);
			params.put("customerid", customerid);
			params.put("medicineid", medicineid);
			params.put("num", num);
			params.put("location", location);
			result = new JSONObject(new String(doHttpPost(getHttpPost(
					Config.SAVESTOCHANGEINFO_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	/**
	 * 判断给定编号的销售人员是什么类型
	 * 
	 * @param salerid
	 *            销售人员编号
	 * @return 返回0表示终端经理(业务员)，返回1表示地区经理，返回2表示省级经理，为-1表示错误的销售人员编号
	 * @throws AppException
	 */
	public int IsCounterMan(int salerid) throws AppException {
		JSONObject params = null;
		int result = -1;
		try {
			params = new JSONObject();

			params.put("salerid", salerid);

			result = new JSONObject(new String(doHttpPost(getHttpPost(
					Config.ISCOUNTERMAN_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	// / <summary>
	// / 添加终端客户信息
	// / </summary>
	// / <param name="userid">登陆用户编号</param>
	// / <param name="areacode">终端客户所属区域编码，只需传递区县编码</param>
	// / <param name="cusname">终端客户姓名</param>
	// / <param name="linkman">终端客户联系人</param>
	// / <param name="tel">终端客户联系电话</param>
	// / <param name="fax">终端客户传真</param>
	// / <param name="cusintr">终端客户介绍</param>
	// / <param name="address">终端客户地址</param>
	// / <returns>成功返回大于0的整型数据，否则返回0</returns>
	public boolean SaveCustomerInfo(int userid, String areacode,
			String cusname, String linkman, String tel, String fax,
			String cusintr, String address, int groupID, String medicineid)
			throws AppException {
		JSONObject params = null;
		boolean result = false;
		try {
			params = new JSONObject();

			params.put("userid", userid);
			params.put("areacode", areacode);
			params.put("cusname", cusname);
			params.put("linkman", linkman);
			params.put("tel", tel);
			params.put("fax", fax);
			params.put("cusintr", cusintr);
			params.put("address", address);
			params.put("groupid", groupID);
			params.put("medicineid", medicineid);
			if (new JSONObject(new String(doHttpPost(getHttpPost(
					Config.SAVECUSTOMERINFO_URL, params)))).getInt("d") > 0) {
				result = true;
			} else {
				result = false;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	public boolean UpdateCustomerInfo(int cusid, String areacode,
			String cusname, String linkman, String tel, String fax,
			String cusintr, String address, int groupID, String medicineid)
			throws AppException {
		JSONObject params = null;
		boolean result = false;
		try {
			params = new JSONObject();

			params.put("cusid", cusid);
			params.put("areacode", areacode);
			params.put("cusname", cusname);
			params.put("linkman", linkman);
			params.put("tel", tel);
			params.put("fax", fax);
			params.put("cusintr", cusintr);
			params.put("address", address);
			params.put("groupid", groupID);
			params.put("medicineid", medicineid);
			if (new JSONObject(new String(doHttpPost(getHttpPost(
					Config.UPDATECUSTOMERINFO_URL, params)))).getInt("d") > 0) {
				result = true;
			} else {
				result = false;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	public int UpdateVisitRecordInfo(int visitid, int customerid,
			String visitaddress, String remark, String location,
			String medicineids, String medicinenums) throws AppException {
		JSONObject params = null;
		int result = -1;
		try {
			params = new JSONObject();

			params.put("visitid", visitid);
			params.put("customerid", customerid);
			params.put("visitaddress", visitaddress);
			params.put("remark", remark);
			params.put("location", location);
			params.put("medicineids", medicineids);
			params.put("medicinenums", medicinenums);
			result = new JSONObject(new String(doHttpPost(getHttpPost(
					Config.UPDATEVISITRECORDINFO_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	public String TestLib(String username, String password) throws AppException {
		JSONObject params = null;
		String result = "";
		try {
			params = new JSONObject();

			params.put("uid", username);
			params.put("password", password);
			params.put("submit", "提交");
			result = new String(
					doHttpPost(getHttpPost(Config.CUDT_URL, params)));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	public int GetTodayOrdersCount(int userid) throws AppException {
		JSONObject params = null;
		int result = -1;
		try {
			params = new JSONObject();

			params.put("userid", userid);

			result = new JSONObject(new String(doHttpPost(getHttpPost(
					Config.GETTODAYORDERSCOUNT_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

		return result;

	}

	public int SaveLocation(int userid, String location, double longtitude,
			double latitude) throws AppException {
		JSONObject params = null;
		try {
			params = new JSONObject();
			params.put("salerid", userid);
			params.put("location", location);
			params.put("longitude", longtitude);
			params.put("latitude", latitude);
			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.SAVELOCATION_URL, params)))).getInt("d");
		} catch (Exception e) {
			// TODO: handle exception
			throw AppException.http(e);
		}
	}

	public List<CustGroup> GetCustGroupBySalerID(int salerid)
			throws AppException {
		JSONObject params = null;
		List<CustGroup> datas = null;

		try {
			params = new JSONObject();

			params.put("salerid", salerid);

			datas = CustGroup.GetCustGroupBySalerID((doHttpPost(getHttpPost(
					Config.GETCUSTGROUPBYSALERID_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static List<CustomerModel> GetCustomerListByCounter(int pageIndex,
			int pageSize, int userid, String name, int groupid)
			throws AppException {
		JSONObject params = null;
		List<CustomerModel> datas = null;

		try {
			params = new JSONObject();
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);
			params.put("name", name);
			params.put("groupid", groupid);
			datas = CustomerModel
					.GetCustomerListByCounter((doHttpPost(getHttpPost(
							Config.GETCUSTOMERLISTBYCOUNTER_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static List<CustomerModel> GetCustomerListByManager(int pageIndex,
			int pageSize, int userid, String name, String areacode)
			throws AppException {
		JSONObject params = null;
		List<CustomerModel> datas = null;

		try {
			params = new JSONObject();
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);
			params.put("name", name);
			params.put("areacode", areacode);
			datas = CustomerModel
					.GetCustomerListByCounter((doHttpPost(getHttpPost(
							Config.GETCUSTOMERLISTBYMANAGER_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static List<SalerSimple> QueryCountermen(int userid, String name)
			throws AppException {
		JSONObject params = null;
		List<SalerSimple> datas = null;

		try {
			params = new JSONObject();

			params.put("userid", userid);

			datas = SalerSimple.GetCountermen((doHttpPost(getHttpPost(
					Config.QUERYCOUNTERMEN_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static String GetGroupNameByGroupID(int groupid) throws AppException {
		JSONObject params = null;

		try {
			params = new JSONObject();

			params.put("groupid", groupid);

			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.GETGROUPNAMEBYGROUPID_URL, params)))).getString("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

	}

	public static int AddCustomerGroupBySaler(int salerid, String groupname)
			throws AppException {
		JSONObject params = null;

		try {
			params = new JSONObject();

			params.put("salerid", salerid);
			params.put("groupname", groupname);
			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.ADDCUSTOMERGROUPBYSALER_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

	}

	public static int UpdateCustGroup(int grpid, String groupname)
			throws AppException {
		JSONObject params = null;

		try {
			params = new JSONObject();

			params.put("grpid", grpid);
			params.put("groupname", groupname);
			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.UPDATECUSTGROUP_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

	}

	public static int DeleteCustomerGroup(int groupid) throws AppException {
		JSONObject params = null;

		try {
			params = new JSONObject();

			params.put("groupid", groupid);

			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.DELETECUSTOMERGROUP_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

	}

	public static List<LinkMan> GetLinkMan(int pageIndex, int pageSize,
			int userid) throws AppException {
		JSONObject params = null;
		List<LinkMan> datas = null;

		try {
			params = new JSONObject();
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);

			datas = LinkMan.GetLinkMan((doHttpPost(getHttpPost(
					Config.GETLINKMAN_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static List<LinkMan> QueryLinkMan(int pageIndex, int pageSize,
			int userid, int type, int salerid, String name) throws AppException {
		JSONObject params = null;
		List<LinkMan> datas = null;

		try {
			params = new JSONObject();
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);
			params.put("type", type);
			params.put("salerid", salerid);
			params.put("name", name);
			datas = LinkMan.GetLinkMan((doHttpPost(getHttpPost(
					Config.QUERYLINKMAN_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static LinkMan GetLinkManInfo(int linkmanid) throws AppException {
		JSONObject params = null;
		LinkMan datas = null;

		try {
			params = new JSONObject();

			params.put("linkmanid", linkmanid);
			datas = LinkMan.GetLinkManInfo(doHttpPost(getHttpPost(
					Config.GETLINKMANINFO_URL, params)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.http(e);

		} finally {
			params = null;
		}

		return datas;
	}

	public static int SaveLinkMan(String name, int sex, String birthday,
			String telephone, String email, String qq, int salerid)
			throws AppException {
		JSONObject params = null;

		try {
			params = new JSONObject();

			params.put("name", name);
			params.put("sex", sex);
			params.put("birthday", birthday);
			params.put("telephone", telephone);
			params.put("email", email);
			params.put("qq", qq);
			params.put("salerid", salerid);
			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.SAVELINKMAN_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

	}

	public static int UpdateLinkMan(int linkmanid, String name, int sex,
			String birthday, String telephone, String email, String qq)
			throws AppException {
		JSONObject params = null;

		try {
			params = new JSONObject();
			params.put("linkmanid", linkmanid);
			params.put("name", name);
			params.put("sex", sex);
			params.put("birthday", birthday);
			params.put("telephone", telephone);
			params.put("email", email);
			params.put("qq", qq);

			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.UPDATELINKMAN_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

	}

	public static List<CustomerConsumeDetail> GetCustomerConsumeDetail(
			int customerid, int medicineid, String begintime, String endtime)
			throws AppException {
		JSONObject params = null;
		List<CustomerConsumeDetail> datas = null;

		try {
			params = new JSONObject();
			params.put("customerid", customerid);
			params.put("medicineid", medicineid);
			params.put(" begintime", begintime);
			params.put("endtime", endtime);

			datas = CustomerConsumeDetail
					.GetCustomerConsumeDetail((doHttpPost(getHttpPost(
							Config.GETCUSTOMERCONSUMEDETAIL_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;
	}

	public static List<CustomerMedicines> GetCustomerMedcinesByCustomerID(
			int custid) throws AppException {
		JSONObject params = null;
		List<CustomerMedicines> datas = null;

		try {
			params = new JSONObject();
			params.put("custid", custid);

			datas = CustomerMedicines
					.GetCustomerMedcinesByCustomerID((doHttpPost(getHttpPost(
							Config.GETCUSTOMERMEDCINESBYCUSTOMERID_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;
	}

	public static List<SalerOrderCountInfo> GetSalerAndOrderCount(
			int pageIndex, int pageSize, int userid) throws AppException {
		JSONObject params = null;
		List<SalerOrderCountInfo> datas = null;

		try {
			params = new JSONObject();
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("userid", userid);

			datas = SalerOrderCountInfo
					.GetSalerAndOrderCount((doHttpPost(getHttpPost(
							Config.GETSALERANDORDERCOUNT_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static List<SalerOrderCountInfo> QuerySalerAndOrderCount(int userid,
			String name) throws AppException {
		JSONObject params = null;
		List<SalerOrderCountInfo> datas = null;

		try {
			params = new JSONObject();
			params.put("userid", userid);
			params.put("name", name);

			datas = SalerOrderCountInfo
					.GetSalerAndOrderCount((doHttpPost(getHttpPost(
							Config.QUERYSALERANDORDERCOUNT_URL, params))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw AppException.http(e);
		} finally {
			params = null;
		}

		return datas;

	}

	public static int SaveOrderReview(int orderid, int salerlevel,
			int reviewresult, String location) throws AppException {
		JSONObject params = null;

		try {
			params = new JSONObject();

			params.put("orderid", orderid);
			params.put("salerlevel", salerlevel);
			params.put("reviewresult", reviewresult);
			params.put("location", location);
			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.SAVEORDERREVIEW_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

	}

	public static int SaveVisitReview(int visitid, int managerid,
			String managername, String answer, String location)
			throws AppException {
		JSONObject params = null;

		try {
			params = new JSONObject();

			params.put("visitid", visitid);
			params.put("managerid", managerid);
			params.put("managername", managername);
			params.put("answer", answer);
			params.put("location", location);
			return new JSONObject(new String(doHttpPost(getHttpPost(
					Config.SAVEVISITREVIEW_URL, params)))).getInt("d");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw AppException.IO(e);
		} finally {
			params = null;
		}

	}
}
