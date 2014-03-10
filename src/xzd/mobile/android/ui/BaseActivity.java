package xzd.mobile.android.ui;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.ui.intf.ActivityItf;
import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity implements ActivityItf {
	private AppContext appcontext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appcontext = (AppContext) getApplication();
		appcontext.appmanager.addActivity(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		appcontext.appmanager.finishActivity(this);

	}

	@Override
	public abstract void getPreActivityData();

	@Override
	public abstract void initView();

	@Override
	public abstract void initData();

}
