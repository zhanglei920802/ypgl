package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.CustomerMedicines;
import xzd.mobile.android.business.LocateService;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.DeviceUtil;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.model.VisitRecord;
import xzd.mobile.android.model.VisitRecordInfo;

import xzd.mobile.android.ui.intf.ActivityItf;
import xzd.mobile.android.ui.wdiget.MyLoadingDialog;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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

public class ActivityVisitAdd extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {

	public static final String TAG = "ActivityVisitAdd";
	public static final String CHOOSE_CUSTOM = "choose_custom";

	public static final int HANDLER_PUB_ORDER_SUCCESS = 1;
	public static final int HANDLER_PUB_ORDER_FAILED = -1;
	public static final int HANDLER_PUB_ORDER_EXCEPTION = 2;
	public static final int HANDLER_GET_ORDER_SUCCESS = 7;
	public static final int HANDLER_GET_ORDER_FAILED = 8;
	public static final int LOCATE_SUCCESS = 3;
	public static final int LOCATE_FAILED = 4;
	public static final int HANDLER_UPDATE_ORDER_SUCCESS = 9;
	public static final int HANDLER_UPDATE_ORDER_FAILED = 10;
	public static final int ACTION_EDIT = 5;
	public static final int ACTION_ADD = 6;

	public static final String ACTION_TYPE = "action_type";
	public static final String VISIT_DATA = "visit_data";
	public static final String CUS_DATA = "cus_data";
	private AppContext appContext = null;
	private UIHelper uiHelper = null;
	private StringUtils stringUtils = null;
	private TerminateBiz terminateBiz = null;

	private EditText custom_name = null;
	private EditText visist_address = null;

	private EditText edt_extras = null;
	private TextView mylocation = null;
	private TextView detail_title = null;

	// private LinearLayout btn_add = null;
	private TextView go_back = null;
	private TextView confrim = null;
	private MyLoadingDialog loadingDialog = null;

	// Data
	private CustomerSimple chooed_custom = null;
	private VisitAddHandler visitAddHandler = null;
	private int visit_id = -1;
	private MedicineAdapter medicineAdapter = null;
	private int curType = -1;// 当前操作类型
	private VisitRecord visitRecord = null;
	private VisitRecordInfo visitRecordInfo = null;// 如果是编辑,那么又前一个Activities获取
	// locate
	// 定位
	private LocateService mBoundService = null;
	private MyServiceConnection myserviceconnection = null;
	private boolean isBind = false;
	private boolean locationFinish = false;

	//
	private ListView lv_cus_visit = null;
	private List<CustomerMedicines> mDatas = null;
	private boolean DEBUG = true;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.custom_name:
			if (chklogin()) {
				Intent intent = new Intent(this, CustomChooseV2.class);
				intent.putExtra(CustomChooseV2.CUSTOMCHOOSEDATA,
						AppContext.user.getSale_id());
				startActivityForResult(intent,
						Config.REQUEST_CODE_CHOOSE_CUSTOM);
			} else {
				UIHelper.Toast(this, "您还未登录");
			}
			break;

		case R.id.confrim:
			if (chklogin()) {
				if (!chkIpt()) {
					if (appContext.isNetworkConnected()) {
						if (mDatas == null || mDatas.isEmpty()) {
							return;
						}

						// 获取IDS,NUMS
						StringBuilder sb_ids = new StringBuilder();
						StringBuilder sb_nums = new StringBuilder();

						for (int i = 0; i < mDatas.size(); i++) {
							if (mDatas.get(i).isZero()) {
								continue;
							} else {
								sb_ids.append(mDatas.get(i).getMedicineid()
										+ ",");
								sb_nums.append(mDatas.get(i).getCount() + ",");
							}

						}
						System.out.println("ActivityVisitAdd.onClick(){sb_ids:"
								+ sb_ids.toString() + ",sb_nums:"
								+ sb_nums.toString() + "}");
						sb_ids.deleteCharAt(sb_ids.length() - 1);
						sb_nums.deleteCharAt(sb_nums.length() - 1);
						if (curType == ACTION_ADD) {

							if (DEBUG) {
								Log.d(TAG, "ids [" + sb_ids.toString()
										+ "],nums[" + sb_nums.toString() + "]");
							}
							pubVisit(AppContext.user.getM_id(),
									chooed_custom.getCustomer_id(),
									visist_address.getText().toString(),
									edt_extras.getText().toString(), mylocation
											.getText().toString(),
									sb_ids.toString(), sb_nums.toString());
						} else if (curType == ACTION_EDIT) {
							if (chooed_custom == null) {
								return;
							} else {
								UpdateVisit(
										visitRecordInfo.getVisitRecord_id(),
										chooed_custom.getCustomer_id(),
										visist_address.getText().toString(),
										edt_extras.getText().toString(),
										mylocation.getText().toString(),
										sb_ids.toString(), sb_nums.toString());
							}

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
			visitRecord = (VisitRecord) intent.getSerializableExtra(VISIT_DATA);
		} else if (curType == ACTION_ADD) {
			try {
				chooed_custom = (CustomerSimple) getIntent().getSerializableExtra(CUS_DATA);
			} catch (Exception e) {
				// TODO: handle exception
			}
		 
		}
	}

	private void UpdateVisit(final int visitid, final int customerid,
			final String visitaddress, final String remark,
			final String location, final String medicineids,
			final String medicinenums) {
		// TODO Auto-generated method stub
		loadingDialog.show();
		loadingDialog.setCanceledOnTouchOutside(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = visitAddHandler.obtainMessage();
				int result;
				try {
					result = terminateBiz.UpdateVisitRecordInfo(visitid,
							customerid, visitaddress, remark, location,
							medicineids, medicinenums);
					if (result > 0) {
						msg.what = HANDLER_UPDATE_ORDER_SUCCESS;

					} else {
						msg.what = HANDLER_UPDATE_ORDER_FAILED;

					}
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_PUB_ORDER_EXCEPTION;
				}

				visitAddHandler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		custom_name = (EditText) findViewById(R.id.custom_name);
		visist_address = (EditText) findViewById(R.id.visist_address);

		edt_extras = (EditText) findViewById(R.id.edt_extras);

		mylocation = (TextView) findViewById(R.id.mylocation);
		detail_title = (TextView) findViewById(R.id.detail_title);

		// btn_add = (LinearLayout) findViewById(R.id.btn_add);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		loadingDialog = new MyLoadingDialog(R.string.sending, this);
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setOnClickListener(this);

		mylocation.setVisibility(View.INVISIBLE);// 隐藏位置信息

		custom_name.setOnClickListener(this);

		{
			lv_cus_visit = (ListView) findViewById(R.id.lv_cus_visit);
			lv_cus_visit.setAdapter(medicineAdapter);
			lv_cus_visit.setOnItemClickListener(this);
		}
		if (curType == ACTION_ADD) {
			detail_title.setText("添加拜访");
			if(chooed_custom!=null){
				custom_name.setText(chooed_custom.getCustomer_Name());
			}

		} else if (curType == ACTION_EDIT) {
			detail_title.setText("修改拜访");

		}
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		visitAddHandler = new VisitAddHandler();
		myserviceconnection = new MyServiceConnection();
		medicineAdapter = new MedicineAdapter();
		mDatas = new ArrayList<CustomerMedicines>();
	}

	private boolean chkIpt() {
		// TODO Auto-generated method stub
		return stringUtils.isEmpty(visist_address.getText().toString())
				|| stringUtils.isEmpty(edt_extras.toString());
	}

	private boolean chklogin() {
		// TODO Auto-generated method stub
		return AppContext.is_login();
	};

	private void pubVisit(final int userid, final int customerid,
			final String visitaddress, final String remark,
			final String location, final String medicineids,
			final String medicinenums) {
		loadingDialog.show();
		loadingDialog.setCanceledOnTouchOutside(false);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = visitAddHandler.obtainMessage();
				int result;
				try {
					result = terminateBiz.SaveVisitRecordInfo(userid,
							customerid, visitaddress, remark, location,
							medicineids, medicinenums);
					if (result > 0) {
						msg.what = HANDLER_PUB_ORDER_SUCCESS;
						visit_id = result;
					} else {
						msg.what = HANDLER_PUB_ORDER_FAILED;

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_PUB_ORDER_EXCEPTION;
				}

				visitAddHandler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visit_add);

		appContext = (AppContext) getApplication();
		uiHelper = UIHelper.getInstance();
		stringUtils = StringUtils.getInstance();
		terminateBiz = TerminateBiz.getInstance();

		getPreActivityData();
		initData();
		initView();
		if (curType == ACTION_ADD) {
			doBindService();
		} else {
			Log.d(TAG, "当期为编辑状态.准备获取数据");
			GetOldData(visitRecord.getVisitRecord_id());
			Log.d(TAG, "getVisitRecord_id=" + visitRecord.getVisitRecord_id());
		}

	}

	private void GetOldData(final int visitRecord_id) {
		// TODO Auto-generated method stub
		loadingDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = visitAddHandler.obtainMessage();
				VisitRecordInfo tmp = null;
				try {
					tmp = terminateBiz.GetVisitRecordInfo(appContext,
							visitRecord_id, AppContext.user.getM_id(), true);
					msg.what = HANDLER_GET_ORDER_SUCCESS;
					msg.obj = tmp;
					Log.d(TAG, "获取数据成功:tmp=" + tmp.toString());
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_GET_ORDER_FAILED;
				}
				visitAddHandler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		appContext = null;
		uiHelper = null;
		stringUtils = null;
		terminateBiz = null;

		custom_name = null;
		visist_address = null;

		edt_extras = null;
		mylocation = null;
		detail_title = null;
		// private LinearLayout btn_add = null;
		go_back = null;
		confrim = null;
		loadingDialog = null;

		// Data

		// locate
		// 定位
		mBoundService = null;
		myserviceconnection = null;
		super.onDestroy();
	}

	/**
	 * 开始定位服务
	 */
	private void doBindService() {

		bindService(new Intent(ActivityVisitAdd.this, LocateService.class),
				myserviceconnection, Context.BIND_AUTO_CREATE);

	}

	/**
	 * 解绑定位服务
	 */
	public void UnBindService() {
		unbindService(myserviceconnection);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	public void StartLocation(final VisitAddHandler mHandler) {
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

	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((LocateService.LocalBinder) service).getService();
			isBind = true;
			StartLocation(visitAddHandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBind = false;
			mBoundService = null;// 将数据置空
		}

	}

	private final class VisitAddHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);

			switch (msg.what) {
			case HANDLER_PUB_ORDER_SUCCESS:
				loadingDialog.dismiss();
				UIHelper.Toast(ActivityVisitAdd.this, "发表成功");
				if (curType == ACTION_ADD) {
					// ShowAddLibDialog();
				}

				onDestroy();
				// finish();
				break;

			case HANDLER_PUB_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(ActivityVisitAdd.this, "发表失败");
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
				UIHelper.Toast(ActivityVisitAdd.this, "发表失败");
				break;
			case HANDLER_GET_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(ActivityVisitAdd.this, "获取订单失败");
				break;
			case HANDLER_GET_ORDER_SUCCESS:
				loadingDialog.dismiss();
				if (msg.obj != null) {
					visitRecordInfo = (VisitRecordInfo) msg.obj;
					Log.d(TAG, "主线程获取到数据:" + visitRecordInfo.toString());
					UpdateView();
				}
			case 11:
				if (msg.obj != null) {
					if (mDatas != null) {
						mDatas.clear();
						mDatas.addAll((Collection<? extends CustomerMedicines>) msg.obj);
						if (mDatas.isEmpty()) {
							lv_cus_visit.setVisibility(View.GONE);
						} else {
							LayoutParams params = lv_cus_visit
									.getLayoutParams();
							params.height = DeviceUtil.dip2px(appContext,
									mDatas.size() * 60);
							lv_cus_visit.setLayoutParams(params);
							params = null;
							lv_cus_visit.setVisibility(View.VISIBLE);

						}
						medicineAdapter.notifyDataSetChanged();

					}
				}
				break;

			case -2:
			case -3:
			case -4:
			case -5:

				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Config.RESULT_CODE_CHOOSE_CUSTOM == resultCode) {
			if (null == data) {
				return;
			}
			chooed_custom = (CustomerSimple) data
					.getSerializableExtra(CHOOSE_CUSTOM);
			custom_name.setText(chooed_custom.getCustomer_Name());
			// 清除之前存在的数据
			if (mDatas != null && !mDatas.isEmpty()) {
				mDatas.clear();
				medicineAdapter.notifyDataSetChanged();
			}
			loadMedicines(chooed_custom.getCustomer_id());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void loadMedicines(int customer_id) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if (!appContext.isNetworkConnected()) {
						visitAddHandler.sendEmptyMessage(-1);
						return;
					}

					if (!AppContext.is_login()) {
						visitAddHandler.sendEmptyMessage(-2);
						return;
					}

					if (chooed_custom == null) {
						visitAddHandler.sendEmptyMessage(-3);
						return;
					}
					List<CustomerMedicines> tmp = null;
					tmp = OrderHelper
							.GetCustomerMedcinesByCustomerID(chooed_custom
									.getCustomer_id());

					if (null == tmp || tmp.isEmpty()) {

						visitAddHandler.sendEmptyMessage(-4);
						return;
					}

					Message msg = visitAddHandler.obtainMessage();
					msg.obj = tmp;
					msg.what = 11;
					visitAddHandler.sendMessage(msg);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					visitAddHandler.sendEmptyMessage(-5);
				}
			}
		}).start();
	}

	public void UpdateView() {
		// TODO Auto-generated method stub
		custom_name.setText(visitRecordInfo.getCustomer_Name() + "");
		visist_address.setText(visitRecordInfo.getVisit_Address() + "");
		edt_extras.setText(visitRecordInfo.getVisit_remark() + "");
		mylocation.setText(visitRecordInfo.getVisit_location() + "");
	}

	public void ShowAddLibDialog() {
		// TODO Auto-generated method stub
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("添加拜访");
		builder.setMessage("添加拜访成功,是否添加到库存?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putInt(ActivityAddToLib.ACTION,
						ActivityAddToLib.ACTION_ADD);
				bundle.putSerializable(ActivityAddToLib.VISIT_DATA,
						chooed_custom);
				bundle.putInt(ActivityAddToLib.VISIST_ID, visit_id);
				uiHelper.ShowAddToLibUI(ActivityVisitAdd.this, bundle);

				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
			}
		});
		builder.create();
		builder.show();

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

			final EditText textView2 = (EditText) convertView
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
			textView2.setText(String.valueOf(mDatas.get(position).getCount()));

			return convertView;
		}
	}

	private int choosedIndex = -1;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.lv_cus_visit:
			if (position < 0 || position > mDatas.size()) {
				choosedIndex = position;
				return;
			}

			// Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle(R.string.app_name);
			// builder.setMessage("请输入输入药品数量");
			// builder.setView(getLayoutInflater().inflate(R.layout.xzd_dlg_input,
			// null));
			// builder.setNegativeButton("取消", new
			// DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// // TODO Auto-generated method stub
			// return;
			// }
			// });
			// builder.setPositiveButton("确定", new
			// DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// // TODO Auto-generated method stub
			//
			// }
			// });
			// builder.setOnItemSelectedListener(new OnItemSelectedListener() {
			//
			// @Override
			// public void onItemSelected(AdapterView<?> parent, View view,
			// int position, long id) {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// @Override
			// public void onNothingSelected(AdapterView<?> parent) {
			// // TODO Auto-generated method stub
			//
			// }
			// });
			break;

		default:
			break;
		}
	}
}
