package xzd.mobile.android.ui;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.LinkManBiz;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.model.LinkMan;
import xzd.mobile.android.ui.intf.ActivityItf;
import xzd.mobile.android.ui.wdiget.MyLoadingDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class MgrLinkMan extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {
	private final String TAG = "GroupManager";
	private final boolean DEBUG = true;
	private TextView edit_contact = null;
	private TextView go_back = null;
	private TextView detail_title = null;
	private TextView add = null;
	private GroupHandler mGroupHandler = null;

	private AppContext maAppContext = null;
	public static final int SHOW_DATE_PICKER = 4;

	public static final int HANDLER_WHAT_OBTAIN_SUCCESS = 3;
	public static final int HANDLER_WHAT_OBTAIN_FAILED = 4;
	public static final int HANDLER_WHAT_NO_LOGIN = 5;
	public static final int HANDLER_WHAT_NO_NETWORK = 6;
	public static final int HANDLER_WHAT_OBTAIN_EXCEPTION = 7;
	public static final int HANDLER_WHAT_NULL_DATA = 8;
	public static final String START_METHOD = "startmethod";
	public static final int METHOD_ADD = 1;
	public static final int METHOD_EDIT = 2;
	public static final int METHOD_VIEW = 3;
	private int mStartMethod = 0;
	private EditText contact_name;
	private EditText contact_remark;
	private EditText contact_phone = null;
	private TextView deletegroup = null;
	private EditText contact_birthday = null;
	private RadioGroup rg_gender = null;
	private RadioButton rb_male = null;
	private RadioButton rb_female = null;
	private EditText qq = null;
	private int chooedGender = 0;
	public static final String EXTRAS_LINKMAN = "linkman";
	private LinkMan mLinkMan = null;
	private MyLoadingDialog myLoadingDialog = null;

	public MgrLinkMan() {
		// TODO Auto-generated constructor stub
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub

		switch (id) {
		case SHOW_DATE_PICKER:

			DatePickerDialog datePickerDialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// TODO Auto-generated method stub

							contact_birthday
									.setText("" + year + "-"
											+ (monthOfYear + 1) + "-"
											+ dayOfMonth + "");
						}
					}, 1990, 0, 1);
			return datePickerDialog;

			// break;

		default:
			return super.onCreateDialog(id);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_back:
			setResult(RESULT_CANCELED);
			onDestroy();
			break;

		case R.id.add:
			if (AppContext.user == null) {
				return;
			}
			if (!maAppContext.isNetworkConnected()) {
				return;
			}

			if (TextUtils.isEmpty(contact_name.getText())
					|| TextUtils.isEmpty(contact_remark.getText().toString())
					|| TextUtils.isEmpty(contact_phone.getText().toString())
					|| TextUtils.isEmpty(contact_birthday.getText().toString())
					|| TextUtils.isEmpty(qq.getText().toString())) {
				UIHelper.Toast(this, "请完善资料填写");
				return;
			}
			if (mStartMethod == METHOD_ADD) {
				SaveLinkMan(contact_name.getText().toString(), chooedGender,
						contact_birthday.getText().toString(), contact_phone
								.getText().toString(), contact_remark.getText()
								.toString(), qq.getText().toString(),
						AppContext.user.getSale_id());

			} else if (mStartMethod == METHOD_EDIT) {
				UpdateLinkMan(mLinkMan.getID(), contact_name.getText()
						.toString(), chooedGender, contact_birthday.getText()
						.toString(), contact_phone.getText().toString(),
						contact_remark.getText().toString(), qq.getText()
								.toString());
			}
			break;
		case R.id.deletegroup:
			if (mStartMethod == METHOD_ADD) {

				throw new IllegalArgumentException("illegal params");
			}
			if (AppContext.user == null) {
				return;
			}
			if (!maAppContext.isNetworkConnected()) {
				return;
			}

			if (mStartMethod == METHOD_EDIT) {

				// delete

			}
			break;
		case R.id.contact_birthday:
			showDialog(SHOW_DATE_PICKER);
			break;
		case R.id.edit_contact:
			Bundle bundle = new Bundle();
			// 跳转到详细界面
			bundle.putSerializable(MgrLinkMan.EXTRAS_LINKMAN, mLinkMan);
			bundle.putInt(MgrLinkMan.START_METHOD, MgrLinkMan.METHOD_EDIT);
			UIHelper.getInstance().showLinkManDetailActivity(this, bundle);
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
		if (mStartMethod == METHOD_EDIT || mStartMethod == METHOD_VIEW) {
			mLinkMan = (LinkMan) getIntent().getSerializableExtra(
					EXTRAS_LINKMAN);
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

		// addgroup = (EditText) findViewById(R.id.groupname);
		deletegroup = (TextView) findViewById(R.id.deletegroup);
		deletegroup.setOnClickListener(this);

		// content
		{
			contact_name = (EditText) findViewById(R.id.contact_name);
			contact_remark = (EditText) findViewById(R.id.contact_remark);
			contact_phone = (EditText) findViewById(R.id.contact_phone);
			contact_birthday = (EditText) findViewById(R.id.contact_birthday);
			rg_gender = (RadioGroup) findViewById(R.id.rg_gender);
			contact_birthday.setOnClickListener(this);
			rb_male = (RadioButton) findViewById(R.id.rb_male);
			rb_female = (RadioButton) findViewById(R.id.rb_female);
			qq = (EditText) findViewById(R.id.contact_qq);
			edit_contact = (TextView) findViewById(R.id.edit_contact);
			edit_contact.setVisibility(View.GONE);
			edit_contact.setOnClickListener(this);
			rg_gender.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					switch (checkedId) {
					case R.id.rb_male:
						chooedGender = 0;
						break;

					case R.id.rb_female:
						chooedGender = 1;
						break;
					}
				}
			});
		}
		// init content
		if (mStartMethod == METHOD_ADD) {

			deletegroup.setVisibility(View.GONE);
			detail_title.setText("查看联系人");
		} else if (mStartMethod == METHOD_EDIT || mStartMethod == METHOD_VIEW) {
			deletegroup.setVisibility(View.GONE);
			deletegroup.setText("刪除联系人");
			detail_title.setText("编辑联系人");

			if (mLinkMan != null) {
				contact_name.setText(mLinkMan.getLinkManName());
				contact_remark.setText(mLinkMan.getEmail());
				contact_phone.setText(mLinkMan.getTelephone());
				contact_birthday.setText(mLinkMan.getBirthDay());
				qq.setText(mLinkMan.getQQ());
				if (mLinkMan.getSex() == 1) {
					rb_female.setChecked(true);
				} else {
					rb_male.setChecked(true);
				}
			}

			if (mStartMethod == METHOD_VIEW) {
				contact_name.setEnabled(false);
				contact_remark.setEnabled(false);
				contact_phone.setEnabled(false);
				contact_birthday.setEnabled(false);
				rb_male.setEnabled(false);
				edit_contact.setVisibility(View.VISIBLE);
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
		setContentView(R.layout.xzd_mgr_linkman);
		maAppContext = (AppContext) getApplication();
		getPreActivityData();
		initData();
		initView();

	}

	private void SaveLinkMan(final String name, final int sex,
			final String birthday, final String telephone, final String email,
			final String qq, final int salerid) {
		// TODO Auto-generated method stub
		if (myLoadingDialog == null) {
			myLoadingDialog = new MyLoadingDialog(R.string.loading, this);
		}
		myLoadingDialog.setCancelable(false);
		myLoadingDialog.show();
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
					tmp = LinkManBiz.SaveLinkMan(name, sex, birthday,
							telephone, email, qq, salerid);

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

	private void GetLinkManInfo(final int linkmanid) {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					LinkMan tmp = null;
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
					tmp = LinkManBiz.GetLinkManInfo(linkmanid);

					if (tmp == null) {
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

	private void UpdateLinkMan(final int linkmanid, final String name,
			final int sex, final String birthday, final String telephone,
			final String email, final String qq) {
		// TODO Auto-generated method stub
		if (myLoadingDialog == null) {
			myLoadingDialog = new MyLoadingDialog(R.string.loading, this);
		}
		myLoadingDialog.setCancelable(false);
		myLoadingDialog.show();
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
					tmp = LinkManBiz.UpdateLinkMan(linkmanid, name, sex,
							birthday, telephone, email, qq);

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

	private void DeleteCustomerGroup(final int groupid) {
		// TODO Auto-generated method stub
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

	public String getTAG() {
		return TAG;
	}

	private final class GroupHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			if (myLoadingDialog != null) {
				myLoadingDialog.dismiss();
			}
			switch (msg.what) {
			case HANDLER_WHAT_OBTAIN_SUCCESS:
				if (mStartMethod == METHOD_EDIT) {
					UIHelper.Toast(MgrLinkMan.this, "添加成功");
				} else if (mStartMethod == METHOD_EDIT) {
					UIHelper.Toast(MgrLinkMan.this, "编辑成功");

				}
				onDestroy();
				break;

			case HANDLER_WHAT_OBTAIN_FAILED:
			case HANDLER_WHAT_NO_LOGIN:
			case HANDLER_WHAT_NULL_DATA:
			case HANDLER_WHAT_NO_NETWORK:
			case HANDLER_WHAT_OBTAIN_EXCEPTION:

				String msgCode = "0x" + String.format("%1$,02d", msg.what);
				UIHelper.Toast(MgrLinkMan.this, "添加失败,错误代码[" + msgCode + "]");
				break;
			}
		}

	}

}
