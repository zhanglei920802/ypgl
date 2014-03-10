package xzd.mobile.android.ui;

import xzd.mobile.android.R;
import xzd.mobile.android.AppContext;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.LoginHelper;
import xzd.mobile.android.common.StringUtils;

import xzd.mobile.android.model.User;
import xzd.mobile.android.ui.wdiget.MyLoadingDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	public static final String TAG = "LoginActivity";
	public static final int HANDLER_LOGIN_SUCCESS = 1;
	public static final int HANDLER_LOGIN_FAILED = -1;
	public static final String LOGINTYPE = "logintype";
	public static final int FIRSTUSE = 2;// 第一次使用
	public static final int NOT_AUTO_LOGIN = 3;// 非自动登录
	public static final int LOGOUT = 4;

	private EditText edt_username = null;
	private EditText edt_password = null;
	private Button btn_login = null;
	private CheckBox remember_me = null;
	private MyLoadingDialog loading = null;

	private TextView go_back = null;
	private TextView detail_title = null;
	private TextView confrim = null;
	private LoginHandler handler = null;
	private int redirect_type = -1;
	private boolean ischecked = false;
	private AppContext appcontext;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("参数错误");
		}
		redirect_type = intent.getIntExtra(LOGINTYPE, FIRSTUSE);

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		edt_username = (EditText) findViewById(R.id.edt_username);
		edt_password = (EditText) findViewById(R.id.edt_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		remember_me = (CheckBox) findViewById(R.id.remember_me);
		go_back = (TextView) findViewById(R.id.go_back);
		detail_title = (TextView) findViewById(R.id.detail_title);
		confrim = (TextView) findViewById(R.id.confrim);
		go_back.setVisibility(View.GONE);
		detail_title.setText("用户登录");
		confrim.setVisibility(View.GONE);
		btn_login.setOnClickListener(this);

		// login
		loading = new MyLoadingDialog(R.string.login_ing, this);
		remember_me.setOnCheckedChangeListener(this);

		// 还原数据
		if (redirect_type == NOT_AUTO_LOGIN || redirect_type == LOGOUT) {
			edt_username.setText(AppContext.user.getM_mgr_logname());
			edt_username.setSelection(AppContext.user.getM_mgr_logname()
					.length());
			edt_username.requestFocus();
			edt_password.setText(AppContext.user.getM_mgr_pwd());
			remember_me.setChecked(AppContext.user.isRememberMe());

		}

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		handler = new LoginHandler();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		appcontext = (AppContext) getApplication();
		AppContext.uihelper = UIHelper.getInstance();
		AppContext.stringutils = StringUtils.getInstance();
		AppContext.loginhelper = LoginHelper.getInstance();

		getPreActivityData();
		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_login:
			if (!chkIpt()) {
				if (appcontext.isNetworkConnected()) {
					loading.show();
					doLogin(edt_username.getText().toString(), edt_password
							.getText().toString());
				} else {
					UIHelper.Toast(this,
							R.string.http_exception_error);
				}

			} else {
				UIHelper.Toast(this, "对不起,用户名或者密码输入有误");
				edt_password.setText("");
				edt_username.requestFocus();
			}
			break;

		}
	}

	private void doLogin(final String username, final String pwd) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				try {
					User user = AppContext.loginhelper
							.doLogin(username, pwd, 0);// 未加密

					if (user != null && user.getM_mgr_name() != null
							&& user.getM_id() != 0) {
						// msg.obj = user;
						user.setRememberMe(ischecked);
						AppContext.user.LoginUpdate(user);
						AppContext.user.setM_mgr_pwd(pwd);
						AppContext.user.setHaveUpdate(true);
						// 保存用户信息
						AppContext.loginhelper.saveUserInfo(LoginActivity.this,
								AppContext.user);
						user = null;
						msg.what = HANDLER_LOGIN_SUCCESS;
					} else {
						msg.what = HANDLER_LOGIN_FAILED;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_LOGIN_FAILED;
				}

				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 如果用户名或者密码为空,返回true
	 * 
	 * @return
	 */
	private boolean chkIpt() {
		// TODO Auto-generated method stub

		return AppContext.stringutils
				.isEmpty(edt_username.getText().toString())
				|| AppContext.stringutils.isEmpty(edt_password.getText()
						.toString());
	}

	private final class LoginHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);

			loading.dismiss();
			switch (msg.what) {
			case HANDLER_LOGIN_SUCCESS:
				UIHelper.Toast(LoginActivity.this, "登录成功");
				// appcontext.user = (User) msg.obj;
				switch (redirect_type) {
				case FIRSTUSE:
				case LOGOUT:
				case NOT_AUTO_LOGIN:
					Bundle data = new Bundle();
					data.putInt(MainActivity.STARTMETHOD,
							MainActivity.STARTMETHOD_LOGINSUCCESS);
					AppContext.uihelper.ShowMainActivity(LoginActivity.this,
							data);
					data = null;
					break;
				}

				break;

			case HANDLER_LOGIN_FAILED:
				UIHelper.Toast(LoginActivity.this, "用户名或者密码错误");
				edt_password.setText("");
				edt_username.requestFocus();
				break;

			}
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		// appcontext.user.setRememberMe(isChecked);
		ischecked = isChecked;
	}

}
