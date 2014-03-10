package xzd.mobile.android.business;

import java.util.List;

import xzd.mobile.android.AppException;
import xzd.mobile.android.basic.ApiClient;
import xzd.mobile.android.model.LinkMan;

public class LinkManBiz {

	public LinkManBiz() {
		// TODO Auto-generated constructor stub
	}

	public static List<LinkMan> GetLinkMan(int pageIndex, int pageSize,
			int userid) throws AppException {
		return ApiClient.GetLinkMan(pageIndex, pageSize, userid);
	}

	public static List<LinkMan> QueryLinkMan(int pageIndex, int pageSize,
			int userid, int type, int salerid, String name) throws AppException {
		return ApiClient.QueryLinkMan(pageIndex, pageSize, userid, type,
				salerid, name);
	}

	public static LinkMan GetLinkManInfo(int linkmanid) throws AppException {
		return ApiClient.GetLinkManInfo(linkmanid);
	}
	public static int SaveLinkMan(String name, int sex, String birthday,
			String telephone, String email, String qq, int salerid)
			throws AppException {
		return ApiClient.SaveLinkMan(name, sex, birthday, telephone, email, qq, salerid);
	}

	public static int UpdateLinkMan(int linkmanid, String name, int sex,
			String birthday, String telephone, String email, String qq)
			throws AppException {
		return ApiClient.UpdateLinkMan(linkmanid, name, sex, birthday, telephone, email, qq);
	}
}
