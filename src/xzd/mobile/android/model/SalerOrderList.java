package xzd.mobile.android.model;

import java.util.List;

public class SalerOrderList extends Entity {
	private List<SalerOrderCountInfo> datas = null;

	public List<SalerOrderCountInfo> getDatas() {
		return datas;
	}

	public void setDatas(List<SalerOrderCountInfo> datas) {
		this.datas = datas;
	}

}
