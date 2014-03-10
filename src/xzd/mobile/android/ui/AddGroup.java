package xzd.mobile.android.ui;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.model.CustGroup;
import xzd.mobile.android.ui.intf.ActivityItf;
import xzd.mobile.android.ui.wdiget.MyLoadingDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class AddGroup extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {
	private final String TAG = "GroupManager";
	private final boolean DEBUG = true;

	private TextView go_back = null;
	private TextView detail_title = null;
	private TextView add = null;
	private GroupHandler mGroupHandler = null;
	private MyLoadingDialog mDialog = null;
	private AppContext maAppContext = null;
	public static final int HANDLER_WHAT_OBTAIN_SUCCESS = 3;
	public static final int HANDLER_WHAT_OBTAIN_FAILED = 4;
	public static final int HANDLER_WHAT_NO_LOGIN = 5;
	public static final int HANDLER_WHAT_NO_NETWORK = 6;
	public static final int HANDLER_WHAT_OBTAIN_EXCEPTION = 7;
	public static final int HANDLER_WHAT_NULL_DATA = 8;
	public static final String START_METHOD = "startmethod";
	public static final int METHOD_VIEW = 1;
	public static final int METHOD_EDIT = 2;

	private int mStartMethod = 0;
	private EditText addgroup;
	private TextView deletegroup = null;
	public static final String EXTRAS_GROUPDATA = "groupdata";
	private CustGroup mCustGroup = null;
	private TextView edit_group = null;

	public AddGroup() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_back:
			setResult(RESULT_CANCELED);
			onDestroy();
			break;
		case R.id.edit_group:
			if (mStartMethod == METHOD_VIEW) {
				throw new IllegalArgumentException("illegal params");
			}
			if (AppContext.user == null) {
				return;
			}
			if (!maAppContext.isNetworkConnected()) {
				return;
			}

			if (mStartMethod == METHOD_EDIT) {
				if (TextUtils.isEmpty(addgroup.getText().toString())) {
					UIHelper.Toast(this, "分组名称不能为空");
					return;
				}
				UpdateCustGroup(mCustGroup.getGroupID(), addgroup.getText()
						.toString());
			}
			break;
		case R.id.add:
			if (AppContext.user == null) {
				return;
			}
			if (!maAppContext.isNetworkConnected()) {
				UIHelper.Toast(this,R.string.network_not_connected);
				return;
			}

			if (TextUtils.isEmpty(addgroup.getText().toString())) {
				UIHelper.Toast(this, "分组名称不能为空");
				return;
			}
			addGroup(AppContext.user.getSale_id(), addgroup.getText()
					.toString());
			break;
		case R.id.deletegroup:
			if(!AppContext.is_login()){
				UIHelper.Toast(this,R.string.xzd_no_login);
				return ;
			}
			
			if(!maAppContext.isNetworkConnected()){
				UIHelper.Toast(this, R.string.network_not_connected);
				return ;
			}
			DeleteCustomerGroup(mCustGroup.getGroupID());
			
			break;
		default:
			break;
		}
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		if (getIntent() == null) {
			throw new IllegalArgumentException("no parameter [startmethod]");
		}
		mStartMethod = getIntent().getIntExtra(START_METHOD, 0);
		if (mStartMethod == METHOD_EDIT) {
			mCustGroup = (CustGroup) getIntent().getSerializableExtra(
					EXTRAS_GROUPDATA);
		}

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

		// lv_user_manager.setOnItemClickListener(this);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		add = (TextView) findViewById(R.id.add);
		add.setOnClickListener(this);
		add.setVisibility(View.VISIBLE);
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("添加分组");
		addgroup = (EditText) findViewById(R.id.groupname);
		deletegroup = (TextView) findViewById(R.id.deletegroup);
		deletegroup.setOnClickListener(this);
		deletegroup.setVisibility(View.GONE);
		edit_group = (TextView) findViewById(R.id.edit_group);
		edit_group.setVisibility(View.GONE);
		edit_group.setOnClickListener(this);
		if (mStartMethod == METHOD_VIEW) {

			deletegroup.setVisibility(View.GONE);
		} else if (mStartMethod == METHOD_EDIT) {
			deletegroup.setVisibility(View.VISIBLE);
			deletegroup.setText("删除分组");
			edit_group.setVisibility(View.VISIBLE);
			if(mCustGroup!=null){
				addgroup.setText(mCustGroup.getGroupName());
				addgroup.setSelection(mCustGroup.getGroupName().length());
			}
		}

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

		mGroupHandler = new GroupHandler();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xzd_add_group);
		maAppContext = (AppContext) getApplication();
		getPreActivityData();
		initData();
		initView();

	}

	private void addGroup(final int salerid, final String groupname) {
		showLoadingDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					int tmp = 0;
					if (!AppContext.is_login()) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NO_LOGIN);
						return;
					}

					if (!maAppContext.isNetworkConnected()) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NO_NETWORK);
						return;
					}

					if (AppContext.user == null
							|| AppContext.user.getCounterMain_ID() == -1) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NULL_DATA);
						return;
					}
					tmp = TerminateBiz.AddCustomerGroupBySaler(salerid,
							groupname);

					if (tmp < 1) {
						mGroupHandler
								.sendEmptyMessage(HANDLER_WHAT_OBTAIN_FAILED);
						return;
					}

					Message msg = mGroupHandler.obtainMessage();
					msg.what = HANDLER_WHAT_OBTAIN_SUCCESS;
					msg.obj = tmp;
					mGroupHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					if (DEBUG) {
						e.printStackTrace();
					}

					mGroupHandler
							.sendEmptyMessage(HANDLER_WHAT_OBTAIN_EXCEPTION);
				}

			}
		}).start();
	}

	private void UpdateCustGroup(final int grpid, final String groupname) {
		// TODO Auto-generated method stub
		showLoadingDialog();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					int tmp = 0;
					if (!AppContext.is_login()) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NO_LOGIN);
						return;
					}

					if (!maAppContext.isNetworkConnected()) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NO_NETWORK);
						return;
					}

					if (AppContext.user == null
							|| AppContext.user.getCounterMain_ID() == -1) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NULL_DATA);
						return;
					}
					tmp = TerminateBiz.UpdateCustGroup(grpid, groupname);

					if (tmp < 1) {
						mGroupHandler
								.sendEmptyMessage(HANDLER_WHAT_OBTAIN_FAILED);
						return;
					}

					Message msg = mGroupHandler.obtainMessage();
					msg.what = HANDLER_WHAT_OBTAIN_SUCCESS;
					msg.obj = tmp;
					mGroupHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					if (DEBUG) {
						e.printStackTrace();
					}

					mGroupHandler
							.sendEmptyMessage(HANDLER_WHAT_OBTAIN_EXCEPTION);
				}

			}
		}).start();
	}

	private void showLoadingDialog() {
		if (null == mDialog) {
			mDialog = new MyLoadingDialog(R.string.loading, this);
			mDialog.setCanceledOnTouchOutside(false);
		}
		mDialog.show();
	}

	private void DeleteCustomerGroup(final int groupid) {
		// TODO Auto-generated method stub
		showLoadingDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					int tmp = 0;
					if (!AppContext.is_login()) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NO_LOGIN);
						return;
					}

					if (!maAppContext.isNetworkConnected()) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NO_NETWORK);
						return;
					}

					if (AppContext.user == null
							|| AppContext.user.getCounterMain_ID() == -1) {
						mGroupHandler.sendEmptyMessage(HANDLER_WHAT_NULL_DATA);
						return;
					}
					tmp = TerminateBiz.DeleteCustomerGroup(groupid);

					if (tmp < 1) {
						mGroupHandler
								.sendEmptyMessage(HANDLER_WHAT_OBTAIN_FAILED);
						return;
					}

					Message msg = mGroupHandler.obtainMessage();
					msg.what = HANDLER_WHAT_OBTAIN_SUCCESS;
					msg.obj = tmp;
					mGroupHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					if (DEBUG) {
						e.printStackTrace();
					}

					mGroupHandler
							.sendEmptyMessage(HANDLER_WHAT_OBTAIN_EXCEPTION);
				}

			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		onDestroy();
	}

	private final class GroupHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			if (mDialog != null) {
				mDialog.dismiss();
			}
			switch (msg.what) {
			case HANDLER_WHAT_OBTAIN_SUCCESS:
				if (mStartMethod == METHOD_VIEW) {

					UIHelper.Toast(AddGroup.this, "添加成功");
				} else if (mStartMethod == METHOD_EDIT) {
					UIHelper.Toast(AddGroup.this, "操作成功");
				}
				onDestroy();
				break;

			case HANDLER_WHAT_OBTAIN_FAILED:
			case HANDLER_WHAT_NO_LOGIN:
			case HANDLER_WHAT_NULL_DATA:
			case HANDLER_WHAT_NO_NETWORK:
			case HANDLER_WHAT_OBTAIN_EXCEPTION:

				String msgCode = "0x" + String.format("%1$,02d", msg.what);
				UIHelper.Toast(AddGroup.this, "添加失败,错误代码[" + msgCode + "]");
				break;
			}
		}

	}

}
