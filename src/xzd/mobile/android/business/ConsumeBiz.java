package xzd.mobile.android.business;

import java.util.List;

import xzd.mobile.android.AppException;
import xzd.mobile.android.basic.ApiClient;

public class ConsumeBiz {
	public static List<CustomerConsumeDetail> GetCustomerConsumeDetail(
			int customerid, int medicineid, String begintime, String endtime)
			throws AppException {
		return ApiClient.GetCustomerConsumeDetail(customerid, medicineid,
				begintime, endtime);
	}
}
