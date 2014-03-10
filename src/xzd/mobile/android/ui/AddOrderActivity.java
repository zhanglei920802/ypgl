package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.CustomerMedicines;
import xzd.mobile.android.business.LocateService;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.common.DeviceUtil;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.model.Medicine;
import xzd.mobile.android.model.MedicineOrder;
import xzd.mobile.android.model.MedicineOrderDetail;

import xzd.mobile.android.ui.intf.ActivityItf;
import xzd.mobile.android.ui.wdiget.MyLoadingDialog;

/**
 * 2013年7月7日 01:03:33
 * 
 * @author ZhangLei
 * 
 */
public class AddOrderActivity extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {

	public static final String TAG = "AddOrderActivity";
	public static final String CHOOSE_MEDICINE = "choose_medicine";
	public static final String CHOOSE_CUSTOM = "choose_custom";

	public static final int HANDLER_PUB_ORDER_SUCCESS = 1;
	public static final int HANDLER_PUB_ORDER_FAILED = -1;
	public static final int HANDLER_PUB_ORDER_EXCEPTION = 2;
	public static final int LOCATE_SUCCESS = 3;
	public static final int LOCATE_FAILED = 4;
	public static final int ACTION_EDIT = 5;
	public static final int ACTION_ADD = 6;
	public static final int HANDLER_GET_ORDER_SUCCESS = 7;
	public static final int HANDLER_GET_ORDER_FAILED = 8;
	public static final int HANDLER_UPDATE_ORDER_SUCCESS = 9;
	public static final int HANDLER_UPDATE_ORDER_FAILED = 10;

	public static final String ACTION_TYPE = "action_type";
	public static final String ORDER_DATA = "order_data";

	private AppContext appContext = null;
	private UIHelper uiHelper = null;
	private StringUtils stringUtils = null;
	private OrderHelper orderHelper = null;

	private EditText medcine_name = null;
	private EditText send_to = null;
	private EditText count = null;
	private EditText edt_extras = null;
	private TextView mylocation = null;
	private TextView detail_title = null;
	// private LinearLayout btn_add = null;
	private TextView go_back = null;
	private TextView confrim = null;
	private MyLoadingDialog loadingDialog = null;

	private List<CustomerMedicines> mDatas = null;
	private MedicineAdapter mAdapter = null;
	private ListView lv_medicine_list = null;
	double totalPrice = 0f;
	// Data
	public static final String CUS_DATA = "cus_data";
	private Medicine chooed_medicine = null;
	private CustomerSimple chooed_custom = null;
	private OrderHandler orderhandler = null;
	private int curType = -1;
	private MedicineOrderDetail medicineOrderDetail = null;
	private MedicineOrder medicineOrder = null;
	// locate
	// 定位
	private LocateService mBoundService = null;
	private MyServiceConnection myserviceconnection = null;
	private boolean locationFinish = false;
	private TextView cal_total = null;
	private TextView total_price = null;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("You Must Provide params!!!");
		}
		curType = intent.getIntExtra(ACTION_TYPE, ACTION_ADD);
		if (curType == ACTION_EDIT) {
			medicineOrderDetail = new MedicineOrderDetail(
					(MedicineOrder) intent.getSerializableExtra(ORDER_DATA));
		} else if (curType == ACTION_ADD) {
			try {
				chooed_custom = (CustomerSimple) getIntent()
						.getSerializableExtra(CUS_DATA);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		medcine_name = (EditText) findViewById(R.id.medcine_name);
		send_to = (EditText) findViewById(R.id.send_to);
		count = (EditText) findViewById(R.id.count);
		edt_extras = (EditText) findViewById(R.id.edt_extras);

		mylocation = (TextView) findViewById(R.id.mylocation);
		mylocation.setVisibility(View.INVISIBLE);
		detail_title = (TextView) findViewById(R.id.detail_title);

		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setOnClickListener(this);
		lv_medicine_list = (ListView) findViewById(R.id.lv_medicine_list);
		lv_medicine_list.setAdapter(mAdapter);
		lv_medicine_list.setOnItemClickListener(this);
		loadingDialog = new MyLoadingDialog(R.string.loading, this);

		if (curType == ACTION_ADD) {
			detail_title.setText("创建订单");
			if (chooed_medicine != null) {
				send_to.setText(chooed_custom.getCustomer_Name());
			}
		} else if (curType == ACTION_EDIT) {
			detail_title.setText("修改订单");
			mylocation.setVisibility(View.GONE);

			chooed_custom = new CustomerSimple();
			chooed_custom.setCustomer_id(medicineOrderDetail.getCustomerid());
			chooed_custom.setCustomer_Name(medicineOrderDetail
					.getCustomer_Name());
		}

		medcine_name.setOnClickListener(this);
		send_to.setOnClickListener(this);

		total_price = (TextView) findViewById(R.id.total_price);
		String price = String.format(
				getResources().getString(R.string.total_price), totalPrice);
		total_price.setText(price);
		cal_total = (TextView) findViewById(R.id.cal_total);
		cal_total.setOnClickListener(this);

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		orderhandler = new OrderHandler();
		myserviceconnection = new MyServiceConnection();
		mDatas = new ArrayList<CustomerMedicines>();
		mAdapter = new MedicineAdapter();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_add);

		appContext = (AppContext) getApplication();
		uiHelper = UIHelper.getInstance();
		stringUtils = StringUtils.getInstance();
		orderHelper = OrderHelper.getInstance();

		getPreActivityData();
		initData();
		initView();doBindService();
		if (curType == ACTION_ADD) {
			

		} else {
			GetOldData(medicineOrderDetail.getOrder_id());
		}

	}

	private void GetOldData(final int order_id) {
		loadingDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = orderhandler.obtainMessage();
				MedicineOrder tmp = null;
				try {
					tmp = orderHelper.GetMedicineOrderDetail(order_id);
					msg.what = HANDLER_GET_ORDER_SUCCESS;
					msg.obj = tmp;
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_GET_ORDER_FAILED;
				}
				orderhandler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		appContext = null;
		uiHelper = null;
		stringUtils = null;
		orderHelper = null;

		medcine_name = null;
		send_to = null;
		count = null;
		edt_extras = null;
		mylocation = null;
		detail_title = null;
		// private LinearLayout btn_add = null;
		go_back = null;
		confrim = null;
		loadingDialog = null;

		// Data
		chooed_medicine = null;
		chooed_custom = null;
		orderhandler = null;

		// locate
		// 定位
		mBoundService = null;
		myserviceconnection = null;

		super.onDestroy();
	}

	private final class OrderHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);

			switch (msg.what) {
			case HANDLER_PUB_ORDER_SUCCESS:
				loadingDialog.dismiss();
				UIHelper.Toast(AddOrderActivity.this, "发表订单成功");
				onDestroy();
				break;

			case HANDLER_PUB_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(AddOrderActivity.this, "发表订单失败");
				break;
			case LOCATE_SUCCESS:// 定位成功
				// Log.e("定位处理器", "接收到定位数据:" + msg.obj.toString());
				if (msg.obj != null) {
					mylocation.setText(msg.obj.toString());
					// 定位完成
					locationFinish = true;
					UnBindService();
				}

				break;
			case HANDLER_PUB_ORDER_EXCEPTION:
				loadingDialog.dismiss();
				UIHelper.Toast(AddOrderActivity.this, "发表订单失败");
				break;
			case HANDLER_GET_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(AddOrderActivity.this, "获取订单失败");
				break;
			case HANDLER_GET_ORDER_SUCCESS:
				loadingDialog.dismiss();
				if (msg.obj != null) {
					// medicineOrderDetail = (MedicineOrderDetail) msg.obj;
					medicineOrder = (MedicineOrder) msg.obj;
					UpdateView();
				}
				break;
			case HANDLER_UPDATE_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(AddOrderActivity.this, "修改订单失败");
				break;
			case HANDLER_UPDATE_ORDER_SUCCESS:
				loadingDialog.dismiss();
				UIHelper.Toast(AddOrderActivity.this, "修改订单成功");
				onDestroy();
				break;
			case 11:
				if (msg.obj != null) {
					if (mDatas != null) {
						mDatas.clear();
						mDatas.addAll((Collection<? extends CustomerMedicines>) msg.obj);
						if (mDatas.isEmpty()) {
							lv_medicine_list.setVisibility(View.GONE);
						} else {
							LayoutParams params = lv_medicine_list
									.getLayoutParams();
							params.height = DeviceUtil.dip2px(appContext,
									mDatas.size() * 60);
							lv_medicine_list.setLayoutParams(params);
							params = null;
							lv_medicine_list.setVisibility(View.VISIBLE);

						}
						mAdapter.notifyDataSetChanged();

					}
				}
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (Config.RESULT_CODE_CHOOSE_MEDICINE == resultCode) {
			// medcine_name.setText(((Medicine)data.getSerializableExtra(CHOOSE_MEDICINE)).getMedicine_Name());
			chooed_medicine = (Medicine) data
					.getSerializableExtra(CHOOSE_MEDICINE);
			medcine_name.setText(chooed_medicine.getMedicine_Name());
			Log.d(TAG, "Choosed medicine:" + chooed_medicine.toString());
		} else if (Config.RESULT_CODE_CHOOSE_CUSTOM == resultCode) {
			chooed_custom = (CustomerSimple) data
					.getSerializableExtra(CHOOSE_CUSTOM);
			Log.d(TAG, " chooed_custom:" + chooed_custom.toString());
			send_to.setText(chooed_custom.getCustomer_Name());
			
			if (null != chooed_custom) {
				loadMedicines(chooed_custom.getCustomer_id());
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void UpdateView() {
		// TODO Auto-generated method stub
		medcine_name.setText(medicineOrder.getMedicine_Name() + " ");
		send_to.setText(medicineOrder.getCustomer_Name() + " ");
		count.setText(medicineOrder.getMedicine_Count() + "");
		edt_extras.setText(medicineOrder.getOrder_Remark() + "");
		chooed_custom.setCustomer_id(medicineOrder.getCustomerid());
		chooed_custom.setCustomer_Name(medicineOrder.getCustomer_Name());
		if (StringUtils.getInstance().isEmpty(medicineOrder.getMedicinesName())) {
			return;
		} else {
			System.out.println("AddOrderActivity.UpdateView(){medicineOrder:"
					+ medicineOrder + "}");
			String[] medicineNames = medicineOrder.getMedicinesName()
					.split(",");
			String[] medicineIDs = medicineOrder.getMedicinesID().split(",");
			String[] medicineNums = medicineOrder.getMedicinesNUM().split(",");
			String[] eedicinesPrices = medicineOrder.getMedicinesPrice().split(
					",");

			for (int i = 0; i < medicineNames.length; i++) {
				mDatas.add(new CustomerMedicines(medicineIDs[i],
						medicineNames[i], Double
								.parseDouble(eedicinesPrices[i]),
						medicineNums[i]));
			}
			LayoutParams params = lv_medicine_list.getLayoutParams();
			params.height = DeviceUtil.dip2px(appContext, mDatas.size() * 60);
			lv_medicine_list.setLayoutParams(params);
			params = null;
			lv_medicine_list.setVisibility(View.VISIBLE);

			mAdapter.notifyDataSetChanged();

		}

		// mylocation.setText(medicineOrder.getOrder_Location() + "");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.medcine_name:
			if (chklogin()) {
				Intent intent = new Intent(this, MedicinesActivity.class);
				intent.putExtra(MedicinesActivity.MEDCINECHOOSEDATA,
						AppContext.user.getM_id());
				this.startActivityForResult(intent,
						Config.REQUEST_CODE_CHOOSE_MEDICNE);
				this.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			} else {
				UIHelper.Toast(this, "您还未登录");
			}
			break;

		case R.id.send_to:
			if (chklogin()) {
				Intent intent = new Intent(this, CustomChooseActivity.class);
				intent.putExtra(CustomChooseActivity.CUSTOMCHOOSEDATA,
						AppContext.user.getM_id());
				startActivityForResult(intent,
						Config.REQUEST_CODE_CHOOSE_CUSTOM);
			}
			break;
		case R.id.confrim:
			if (chklogin()) {
				if (chooed_custom != null) {
					if (appContext.isNetworkConnected()) {
						if (mDatas == null || mDatas.isEmpty()) {
							return;
						}
						// 处理数据sb_nums
						// ids
						StringBuilder sb_ids = new StringBuilder();
						StringBuilder sb_nums = new StringBuilder();
						for (int i = 0; i < mDatas.size(); i++) {
							if (mDatas.get(i).isZero()) {
								continue;
							} else {
								sb_nums.append(mDatas.get(i).getCount() + ",");
								sb_ids.append(mDatas.get(i).getMedicineid()
										+ ",");

							}

						}
						sb_nums.deleteCharAt(sb_nums.length() - 1);
						sb_ids.deleteCharAt(sb_ids.length() - 1);

						if (curType == ACTION_ADD) {

							pubOrder(chooed_custom.getCustomer_id(),
									sb_ids.toString(), sb_nums.toString(),
									totalPrice,
									edt_extras.getText().toString(), mylocation
											.getText().toString());
						} else if (curType == ACTION_EDIT) {
							if (chooed_custom == null) {
								return;
							}

							UpdateOrder(
									medicineOrderDetail.getOrder_id(),
									chooed_custom.getCustomer_id(),
									sb_ids.toString(),
									sb_nums.toString(),
									totalPrice,
									"备注:\r\n" + ""
											+ edt_extras.getText().toString(),
											mylocation.getText().toString());

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
		case R.id.cal_total:

			try {
				if (mDatas == null || mDatas.isEmpty()) {
					return;
				}
				totalPrice = 0f;
				for (int i = 0; i < mDatas.size(); i++) {
					if (mDatas.get(i).isZero()) {
						continue;
					}
					totalPrice += Integer.parseInt(mDatas.get(i).getCount())
							* mDatas.get(i).getMedicineprice();
				}

				// total
				total_price.setText(String.format(
						getResources().getString(R.string.total_price),
						totalPrice));
			} catch (Exception e) {
				// TODO: handle exception
			}

			break;
		}
	}

	// / <param name="orderid">订单编号</param>
	// / <param name="customerid">终端编号</param>
	// / <param name="medicineids">药品编号字符串，多个编号之间用逗号隔开，药品数量为0也需要组合进来</param>
	// / <param name="medicinenums">药品数量字符串，，多个编号之间用逗号隔开，药品数量为0也需要组合进来</param>
	// / <param name="totalprice">订单总价</param>
	// / <param name="remark">订单申请备注</param>
	// / <param name="location">订单产生位置</param>
	private void UpdateOrder(final int orderid, final int customerid,
			final String ids, final String nums, final double price,
			final String remark, final String location) {
		// TODO Auto-generated method stub
		loadingDialog.show();
		loadingDialog.setCanceledOnTouchOutside(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = orderhandler.obtainMessage();
				boolean result;
				try {
					result = orderHelper.UpdateMedicineOrders(orderid,
							customerid, ids, nums, price, remark, StringUtils.getInstance().isEmpty(location)?"无信息":location);
					System.out
							.println("AddOrderActivity.UpdateOrder(...).new Runnable() {...}.run(){result["+result+"]}");
					if (result == true) {
						msg.what = HANDLER_UPDATE_ORDER_SUCCESS;

					} else {
						msg.what = HANDLER_UPDATE_ORDER_FAILED;

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_PUB_ORDER_EXCEPTION;
				}

				orderhandler.sendMessage(msg);
			}
		}).start();
	}

	// / <param name="userid">登陆用户编号</param>
	// / <param name="customerid">终端编号</param>
	// / <param name="medicineids">药品编号字符串，多个编号之间用逗号隔开，药品数量为0也需要组合进来</param>
	// / <param name="medicinenums">药品数量字符串，，多个编号之间用逗号隔开，药品数量为0也需要组合进来</param>
	// / <param name="totalprice">订单总价</param>
	// / <param name="remark">订单申请备注</param>
	// / <param name="location">订单产生位置</param>
	private void pubOrder(final int customerid, final String ids,
			final String nums, final double price, final String remark,
			final String location) {
		loadingDialog.show();
		loadingDialog.setCanceledOnTouchOutside(false);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = orderhandler.obtainMessage();
				boolean result;
				try {
					result = OrderHelper.SaveMedicineOrders(
							AppContext.user.getM_id(), customerid, ids, nums,
							price, remark, location);

					if (result == true) {
						msg.what = HANDLER_PUB_ORDER_SUCCESS;

					} else {
						msg.what = HANDLER_PUB_ORDER_FAILED;

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_PUB_ORDER_EXCEPTION;
				}

				orderhandler.sendMessage(msg);
			}
		}).start();
	}

	private boolean chklogin() {
		// TODO Auto-generated method stub
		return AppContext.is_login();
	};

	/**
	 * 开始定位服务
	 */
	private void doBindService() {

		bindService(new Intent(AddOrderActivity.this, LocateService.class),
				myserviceconnection, Context.BIND_AUTO_CREATE);

	}

	/**
	 * 解绑定位服务
	 */
	public void UnBindService() {
		unbindService(myserviceconnection);
	}

	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((LocateService.LocalBinder) service).getService();
			StartLocation(orderhandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;// 将数据置空
		}

	}

	public void StartLocation(final OrderHandler mHandler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage();

				while (true) {
					if (null != mBoundService
							&& !stringUtils.isEmpty(mBoundService.getAdd())
							&& !locationFinish) {// 精度小雨1000

						msg.what = LOCATE_SUCCESS;// 定位成功
						msg.obj = mBoundService.getAdd();
						mHandler.sendMessage(msg);
						break;

					} else {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onDestroy();
		super.onBackPressed();
	}

	private void loadMedicines(int customer_id) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if (!appContext.isNetworkConnected()) {
						orderhandler.sendEmptyMessage(-1);
						return;
					}

					if (!AppContext.is_login()) {
						orderhandler.sendEmptyMessage(-2);
						return;
					}

					if (chooed_custom == null) {
						orderhandler.sendEmptyMessage(-3);
						return;
					}
					List<CustomerMedicines> tmp = null;
					tmp = OrderHelper
							.GetCustomerMedcinesByCustomerID(chooed_custom
									.getCustomer_id());

					if (null == tmp || tmp.isEmpty()) {

						orderhandler.sendEmptyMessage(-4);
						return;
					}

					Message msg = orderhandler.obtainMessage();
					msg.obj = tmp;
					msg.what = 11;
					orderhandler.sendMessage(msg);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					orderhandler.sendEmptyMessage(-5);
				}
			}
		}).start();
	}

	private final class MedicineAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public CustomerMedicines getItem(int position) {
			// TODO Auto-generated method stub
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			convertView = getLayoutInflater().inflate(
					R.layout.xzd_list_item_v4, null);

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
			textView.setText(mDatas.get(position).getMedicinename());

			final TextView textView2 = (TextView) convertView
					.findViewById(R.id.item2);
			textView2.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					CustomerMedicines tmp = mDatas.get(position);
					tmp.setCount(textView2.getText().toString());
					mDatas.set(position, tmp);
				}
			});
			textView2.setText(String.valueOf(mDatas.get(position).getCount()
					+ ""));

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}
}
