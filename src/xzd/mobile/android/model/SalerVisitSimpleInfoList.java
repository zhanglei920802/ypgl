package xzd.mobile.android.model;

import java.util.List;

public class SalerVisitSimpleInfoList extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2348014164445724206L;
	private List<SalerVisitSimpleInfo> mDatas = null;
	public List<SalerVisitSimpleInfo> getmDatas() {
		return mDatas;
	}
	public void setmDatas(List<SalerVisitSimpleInfo> mDatas) {
		this.mDatas = mDatas;
	}
	@Override
	public String toString() {
		return "SalerVisitSimpleInfoList [mDatas=" + mDatas + "]";
	}
	
}
