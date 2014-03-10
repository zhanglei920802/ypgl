package xzd.mobile.android.ui;

import xzd.mobile.android.R;
import xzd.mobile.android.AppContext;
import xzd.mobile.android.business.LoginHelper;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.User;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class StartActivity extends Activity {
	public static final String TAG = "StartActivity";

	private AppContext appcontext = null;
	private LoginHelper loginhelper = null;
	private StringUtils stringutils = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appcontext = (AppContext) getApplication();
		loginhelper = LoginHelper.getInstance();
		stringutils = StringUtils.getInstance();
		final View view = View.inflate(this, R.layout.activity_start, null);
		setContentView(view);

		AlphaAnimation aa = new AlphaAnimation(0.7f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * 跳转到...
	 */
	private void redirectTo() {
		Intent intent = null;
		User user = loginhelper.getUser(StartActivity.this);
		AppContext.user = user;// 保存数据
		if (!stringutils.isEmpty(user.getM_mgr_logname())) {
			if (user.isRememberMe()) {
				intent = new Intent(this, MainActivity.class);
				intent.putExtra(MainActivity.STARTMETHOD,
						MainActivity.STARTMETHOD_START);
			} else {// 跳转到登录界面
				intent = new Intent(this, LoginActivity.class);
				intent.putExtra(LoginActivity.LOGINTYPE,
						LoginActivity.NOT_AUTO_LOGIN);
			}

		} else {//

			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(LoginActivity.LOGINTYPE, LoginActivity.FIRSTUSE);
		}

		startActivity(intent);
		finish();
		intent = null;
		user = null;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		appcontext = null;
		loginhelper = null;
		stringutils = null;

	}

}
