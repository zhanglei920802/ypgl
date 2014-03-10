package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.DeviceUtil;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustGroup;
import xzd.mobile.android.model.CustomerInfoModel;
import xzd.mobile.android.model.CustomerModel;
import xzd.mobile.android.model.Medicine;
import xzd.mobile.android.ui.intf.ActivityItf;
import xzd.mobile.android.ui.wdiget.MyLoadingDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityAddOrEditConsumer extends BaseActivity implements
		OnItemClickListener, ActivityItf, OnClickListener {

	public static final int HANDLER_PUB_ORDER_SUCCESS = 1;
	public static final int HANDLER_PUB_ORDER_FAILED = -1;
	public static final int HANDLER_PUB_ORDER_EXCEPTION = 2;
	public static final int ACTION_EDIT = 5;
	public static final int ACTION_ADD = 6;
	public static final int HANDLER_GET_ORDER_SUCCESS = 7;
	public static final int HANDLER_GET_ORDER_FAILED = 8;
	public static final int HANDLER_UPDATE_ORDER_SUCCESS = 9;
	public static final int HANDLER_UPDATE_ORDER_FAILED = 10;

	public static final String ACTION_TYPE = "action_type";
	public static final String CUSTOM_DATA = "custom_data";
	public static final String ADDRESS_DATA = null;

	private AppContext appContext = null;
	private UIHelper uiHelper = null;
	private StringUtils stringUtils = null;
	private TerminateBiz terminateBiz = null;

	// /Content
	private EditText consumer_name = null;
	private EditText consumer_area = null;
	private EditText consumer_address = null;
	private EditText contact_man = null;
	private EditText contact_tel = null;
	private EditText contact_fax = null;
	private EditText edt_extras = null;
	private TextView contact_grpup = null;
	private ListView lv_medicine_list = null;
	private TextView detail_title = null;
	private TextView go_back = null;
	private TextView confrim = null;
	private MyLoadingDialog loadingDialog = null;

	private List<KeyValue> mDatas = null;
	private OrderAdapter mOrderAdapter = null;
	// Data
	// private CustomerModel couCustomerModel = null;

	private AddOrEditCustomerHandler addOrEditCustomerHandler = null;
	private int curType = -1;
	private CustomerInfoModel customerInfoModel = null;
	private CustGroup chooedGroup;
	private String TAG = "ActivityAddOrEditConsumer";
	private Medicine chooed_medicine;
	private int choosedIndex = -1;
	private TextView add_medicine = null;

	public void UpdateView() {
		// TODO Auto-generated method stub
		// 更新View
		consumer_name.setText(customerInfoModel.getCustomer_Name() + " ");
		consumer_area.setText(customerInfoModel.getCustomer_Area() + "");
		consumer_address.setText(customerInfoModel.getCustomer_Address() + "");
		contact_man.setText(customerInfoModel.getCustomer_LinkMan() + "");
		contact_tel.setText(customerInfoModel.getCustomer_Tel() + "");
		contact_fax.setText(customerInfoModel.getCustomer_FAX() + "");
		edt_extras.setText(customerInfoModel.getCustomer_intr() + "");

		if (null != chooedGroup) {
			contact_grpup.setText(chooedGroup.getGroupName());
		}
		// String[] medicine =
		if (!TextUtils.isEmpty(customerInfoModel.getCustomerMedicineID())
				&& !TextUtils.isEmpty(customerInfoModel
						.getCustomerMedicineName())) {
			String[] nameArray = customerInfoModel.getCustomerMedicineName()
					.split(",");
			String[] idArray = customerInfoModel.getCustomerMedicineID().split(
					",");
			mDatas.clear();
			for (int i = 0; i < idArray.length; i++) {
				mDatas.add(new KeyValue(idArray[i], nameArray[i]));
			}
			LayoutParams tLayoutParams = lv_medicine_list.getLayoutParams();
			tLayoutParams.height = DeviceUtil.dip2px(
					ActivityAddOrEditConsumer.this, 60 * mDatas.size());
			lv_medicine_list.setLayoutParams(tLayoutParams);
			lv_medicine_list.setVisibility(View.VISIBLE);
			mOrderAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case Config.RESULT_CODE_CHOOSE_PROVINCE:
			consumer_area.setText(data.getStringExtra(ADDRESS_DATA) + " ");
			break;
		case Config.RESULT_CODE_CHOOSE_GROUP:
			if (null == data) {
				return;
			}

			chooedGroup = (CustGroup) data
					.getSerializableExtra(TerminateManageActivity.GROUP_DATA);
			if (null != chooedGroup) {
				contact_grpup.setText(chooedGroup.getGroupName());
				Log.d(TAG, "selected data[" + chooedGroup + "]");
			}
			break;
		case RESULT_CANCELED:
			break;
		case Config.RESULT_CODE_CHOOSE_MEDICINE:
		
			chooed_medicine = (Medicine) data
					.getSerializableExtra(AddOrderActivity.CHOOSE_MEDICINE);
			if (chooed_medicine == null) {
				return;
			}
			Log.d(TAG, "chooed medicine[" + chooed_medicine + "]");
			if(requestCode==Config.REQUEST_CODE_ADDD_MEDICNE){
				if(mDatas==null){
				 mDatas = new ArrayList<ActivityAddOrEditConsumer.KeyValue>();
				}
				for (int i = 0; i < mDatas.size(); i++) {
					if(!mDatas.get(i).getKey().equals(chooed_medicine.getMedicine_id())){
						mDatas.add(new KeyValue(String.valueOf(chooed_medicine.getMedicine_id()), chooed_medicine.getMedicine_Name()));
					}else{
						UIHelper.Toast(this,"您已经添加过了");
						break;
					}
				}
			}else if(requestCode==Config.REQUEST_CODE_CHOOSE_MEDICNE){
				mDatas.set(
						choosedIndex,
						new KeyValue(String.valueOf(chooed_medicine
								.getMedicine_id()), chooed_medicine
								.getMedicine_Name()));

				Log.d(TAG, "mDatas[" + mDatas + "]");
			}
		

			
			mOrderAdapter.notifyDataSetChanged();

			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {

		case R.id.confrim:
			if (chklogin()) {
				if (!chkIpt()) {
					if (appContext.isNetworkConnected()) {
						// / do something

						if (curType == ACTION_ADD) {

							if (chooedGroup.getGroupID() < 0) {
								return;
							}

							// 处理数据
							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < mDatas.size(); i++) {
								if (TextUtils.isEmpty(mDatas.get(i).getValue())) {
									continue;
								} else {
									sb.append(mDatas.get(i).key + ",");
								}

							}
							sb.deleteCharAt(sb.length() - 1);
							Log.d(TAG, "order datas[" + sb.toString() + "]");

							pubCustomer(AppContext.user.getM_id(),
									appContext.area_code + "", consumer_name
											.getText().toString(), contact_man
											.getText().toString(), contact_tel
											.getText().toString(), contact_fax
											.getText().toString(), edt_extras
											.getText().toString(),
									consumer_address.getText().toString(),
									chooedGroup.getGroupID(), sb.toString());
						} else if (curType == ACTION_EDIT) {
							if (chooedGroup.getGroupID() < 0) {
								return;
							}

							// 处理数据
							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < mDatas.size(); i++) {
								if (TextUtils.isEmpty(mDatas.get(i).getValue())) {
									continue;
								} else {
									sb.append(mDatas.get(i).key + ",");
								}

							}
							sb.deleteCharAt(sb.length() - 1);
							UpdateCustomer(customerInfoModel.getCustomer_id(),
									appContext.area_code + "", consumer_name
											.getText().toString(), contact_man
											.getText().toString(), contact_tel
											.getText().toString(), contact_fax
											.getText().toString(), edt_extras
											.getText().toString(),
									consumer_address.getText().toString(),
									chooedGroup.getGroupID(), sb.toString());
						}

					} else {
						UIHelper.Toast(this, R.string.http_exception_error);
					}

				} else {
					UIHelper.Toast(this, "对不起,您的输入有误");
				}
			}
			break;
		case R.id.go_back:
			onDestroy();
			break;
		case R.id.consumer_area:
			if (chklogin()) {
				intent = new Intent(this, ActivityProvinceChoose.class);
				startActivityForResult(intent,
						Config.REQUEST_CODE_CHOOSE_PROVINCE);

			} else {
				UIHelper.Toast(this, "您还未登录");
			}
			break;
		case R.id.contact_grpup:
			intent = new Intent(this, GroupManager.class);
			intent.putExtra(GroupManager.START_METHOD, GroupManager.METHOD_VIEW);
			startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_GROUP);
			intent = null;
			break;
		case R.id.add_medicine:
			intent = new Intent(this, MedicinesActivity.class);
			intent.putExtra(MedicinesActivity.MEDCINECHOOSEDATA,
					AppContext.user.getM_id());

			this.startActivityForResult(intent,
					Config.REQUEST_CODE_ADDD_MEDICNE);
			this.overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);
			break;
		}
	}

	private void UpdateCustomer(final int order_id, final String areacode,
			final String cusname, final String linkman, final String tel,
			final String fax, final String cusintr, final String address,
			final int groupID, final String medicineid) {
		// TODO Auto-generated method stub
		loadingDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = addOrEditCustomerHandler.obtainMessage();
				boolean result = false;
				try {
					result = terminateBiz.UpdateCustomerInfo(order_id,
							areacode, cusname, linkman, tel, fax, cusintr,
							address, groupID, medicineid);
					if ((result)) {
						msg.what = HANDLER_UPDATE_ORDER_SUCCESS;
					} else {
						msg.what = HANDLER_UPDATE_ORDER_FAILED;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_PUB_ORDER_EXCEPTION;
				}
				addOrEditCustomerHandler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.lv_medicine_list:

			if (position < 0 || position >= mDatas.size()) {
				return;
			}

			if (chklogin()) {
				Intent intent = new Intent(this, MedicinesActivity.class);
				intent.putExtra(MedicinesActivity.MEDCINECHOOSEDATA,
						AppContext.user.getM_id());
				choosedIndex = position;
				this.startActivityForResult(intent,
						Config.REQUEST_CODE_CHOOSE_MEDICNE);
				this.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			} else {
				UIHelper.Toast(this, "您还未登录");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("You Must Provide params!!!");
		}
		curType = intent.getIntExtra(ACTION_TYPE, ACTION_ADD);
		if (curType == ACTION_EDIT) {
			customerInfoModel = new CustomerInfoModel(
					(CustomerModel) intent.getSerializableExtra(CUSTOM_DATA));
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		// Content
		consumer_name = (EditText) findViewById(R.id.consumer_name);
		consumer_area = (EditText) findViewById(R.id.consumer_area);
		consumer_address = (EditText) findViewById(R.id.consumer_address);
		contact_man = (EditText) findViewById(R.id.contact_man);
		contact_tel = (EditText) findViewById(R.id.contact_tel);
		contact_fax = (EditText) findViewById(R.id.contact_fax);
		edt_extras = (EditText) findViewById(R.id.edt_extras);
		consumer_area.setOnClickListener(this);

		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		detail_title = (TextView) findViewById(R.id.detail_title);
		{
			contact_grpup = (TextView) findViewById(R.id.contact_grpup);
			lv_medicine_list = (ListView) findViewById(R.id.lv_medicine_list);
			contact_grpup.setOnClickListener(this);
			lv_medicine_list.setAdapter(mOrderAdapter);
			lv_medicine_list.setOnItemClickListener(this);

		}
		loadingDialog = new MyLoadingDialog(R.string.loading, this);
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setOnClickListener(this);
		add_medicine = (TextView) findViewById(R.id.add_medicine);
		add_medicine.setOnClickListener(this);
		add_medicine.setVisibility(View.GONE);
		if (curType == ACTION_ADD) {
			detail_title.setText("增加终端");

		} else if (curType == ACTION_EDIT) {
			detail_title.setText("修改终端");
			add_medicine.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		addOrEditCustomerHandler = new AddOrEditCustomerHandler();
		mDatas = new ArrayList<ActivityAddOrEditConsumer.KeyValue>();
		for (int i = 0; i < 20; i++) {
			mDatas.add(new KeyValue("", ""));

		}
		mOrderAdapter = new OrderAdapter();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consume_add);

		appContext = (AppContext) getApplication();
		uiHelper = UIHelper.getInstance();
		stringUtils = StringUtils.getInstance();
		terminateBiz = TerminateBiz.getInstance();

		getPreActivityData();
		initData();
		initView();
		if (curType == ACTION_EDIT) {
			GetOldData(customerInfoModel.getCustomer_id());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void pubCustomer(final int userid, final String areacode,
			final String cusname, final String linkman, final String tel,
			final String fax, final String cusintr, final String address,
			final int groupID, final String medicineid) {
		loadingDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = addOrEditCustomerHandler.obtainMessage();
				boolean result = false;
				try {
					result = terminateBiz.SaveCustomerInfo(userid, areacode,
							cusname, linkman, tel, fax, cusintr, address,
							groupID, medicineid);
					if ((result)) {
						msg.what = HANDLER_PUB_ORDER_SUCCESS;
					} else {
						msg.what = HANDLER_PUB_ORDER_FAILED;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_PUB_ORDER_EXCEPTION;
				}
				addOrEditCustomerHandler.sendMessage(msg);
			}
		}).start();
	}

	private boolean chkIpt() {
		// TODO Auto-generated method stub
		return stringUtils.isEmpty(consumer_address.getText().toString())
				|| stringUtils.isEmpty(contact_man.toString())
				|| stringUtils.isEmpty(contact_tel.toString())
				|| stringUtils.isEmpty(contact_fax.toString());
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onDestroy();
		super.onBackPressed();
	}

	private boolean chklogin() {
		// TODO Auto-generated method stub
		return AppContext.is_login();
	};

	private void GetOldData(final int order_id) {
		loadingDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = addOrEditCustomerHandler.obtainMessage();
				CustomerInfoModel tmp = null;
				try {
					tmp = terminateBiz.GetCustomerInfo(appContext, order_id,
							AppContext.user.getM_id(), true);

					if (null == tmp) {
						throw new NullPointerException();
					}
					// get group
					String group = TerminateBiz.GetGroupNameByGroupID(tmp
							.getCustomerGroupID());
					if (!TextUtils.isEmpty(group)) {
						chooedGroup = new CustGroup();
						chooedGroup.setGroupID(tmp.getCustomerGroupID());
						chooedGroup.setGroupName(group);
					}

					Log.d(TAG, "tmp datas[" + tmp + "]");
					msg.what = HANDLER_GET_ORDER_SUCCESS;
					msg.obj = tmp;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_GET_ORDER_FAILED;
				}
				addOrEditCustomerHandler.sendMessage(msg);
			}
		}).start();
	}

	private final class AddOrEditCustomerHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);

			switch (msg.what) {
			case HANDLER_PUB_ORDER_SUCCESS:
				loadingDialog.dismiss();
				UIHelper.Toast(ActivityAddOrEditConsumer.this, "发表终端成功");
				finish();
				break;

			case HANDLER_PUB_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(ActivityAddOrEditConsumer.this, "发表终端失败");
				break;

			case HANDLER_PUB_ORDER_EXCEPTION:
				loadingDialog.dismiss();
				UIHelper.Toast(ActivityAddOrEditConsumer.this, "发表订单失败");
				break;
			case HANDLER_GET_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(ActivityAddOrEditConsumer.this, "获取订单失败");
				break;
			case HANDLER_GET_ORDER_SUCCESS:
				loadingDialog.dismiss();
				if (msg.obj != null) {
					// medicineOrderDetail = (MedicineOrderDetail) msg.obj;
					customerInfoModel = (CustomerInfoModel) msg.obj;
					appContext.area_code = customerInfoModel
							.getCustomerAreaCode();
					UpdateView();
				}
				break;
			case HANDLER_UPDATE_ORDER_FAILED:
				UIHelper.Toast(ActivityAddOrEditConsumer.this, "修改终端失败");
				break;
			case HANDLER_UPDATE_ORDER_SUCCESS:
				UIHelper.Toast(ActivityAddOrEditConsumer.this, "修改终端成功");
				finish();
				break;
			}
		}
	}

	private final class OrderAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = getLayoutInflater().inflate(
					R.layout.xzd_list_item_v1, null);
			if (mDatas.size() == 1) {
				convertView
						.setBackgroundResource(R.drawable.ca_selector_listitem_single);

			} else if (mDatas.size() == 2) {
				switch (position) {
				case 0:
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_first);

					break;

				case 1:
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_last);

					break;
				}
			} else if (mDatas.size() > 2) {

				if (0 == position) {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_first);
				} else if ((mDatas.size() - 1) == position) {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_last);

				} else {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_content);
				}

			}

			final TextView textView = ((TextView) convertView
					.findViewById(R.id.item1));
			textView.setText(mDatas.get(position).getValue());
			if (TextUtils.isEmpty(mDatas.get(position).getValue())) {
				textView.setHint("药品" + (1 + position));
			} else {
				textView.setText(mDatas.get(position).getValue());
			}

			return convertView;
		}

	}

	private final class KeyValue {
		private String key = null;
		private String value = null;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "KeyValue [key=" + key + ", value=" + value + "]";
		}

		public KeyValue(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

	}
}
