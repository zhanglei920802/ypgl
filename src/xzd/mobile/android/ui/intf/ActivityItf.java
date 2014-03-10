package xzd.mobile.android.ui.intf;

/**
 * 所有Activities都需要实现的接口
 * 
 * @author ZhangLei
 * 
 */
public interface ActivityItf {
	/* 获取上一个Activity传递进来的数据 */
	public abstract void getPreActivityData();

	/* 初始化View */
	public abstract void initView();

	/* 初始化数据 */
	public abstract void initData();
}
