package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.DateUtil;
import xzd.mobile.android.common.DeviceUtil;
import xzd.mobile.android.model.KeyValue;
import xzd.mobile.android.model.StoChange;
import xzd.mobile.android.model.StoChangeList;
import xzd.mobile.android.model.VisitRecord;
import xzd.mobile.android.model.VisitRecordInfo;

import xzd.mobile.android.ui.intf.ActivityItf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class ActivityVisitDetail extends BaseActivity implements ActivityItf,
		OnClickListener {
	private String TAG = "ActivityOrderDetail";

	public static final int HANDLER_GET_DETAIL_SUCCESS = 1;
	public static final int HANDLER_GET_DETAIL_FAILED = 2;
	public static final int HANDLER_GET_DETAIL_EXCEPTION = -1;

	public static final String VISIT_DATA = "visit_data";

	private AppContext appcontext = null;
	private UIHelper helper = null;
	private OrderHelper orderHelper = null;

	private ScrollView wrap_main = null;

	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;

	private TextView go_back = null;
	private TextView detail_title = null;
	private TextView confrim = null;

	// Content
	private TextView visit_address = null;
	private TextView salesman_name = null;
	private TextView visit_date = null;
	private TextView custom_name = null;
	private TextView visitlocation = null;
	private TextView visitremark = null;
	private TextView view_lib = null;
	private TextView add_lib = null;

	private VisitRecordInfo visitRecordInfo = null;
	private VisitHandler visitHandler = null;

	// 拜访记录
	private ListView lv_visit_record_list = null;
	private List<StoChange> mDatas = null;
	private StoAdapter mAdapter = null;
	// 快捷操作
	private TextView add_order = null;
	private TextView add_note = null;
	private TextView edit_visit = null;
	private ListView lv_mark_list = null;
	private List<KeyValue> mDatasV2 = null;
	private KeyValueAdapter mAdapterV2 = null;

	public void UpdateView() {
		// TODO Auto-generated method stub
		if (!AppContext.user.getM_mgr_name().equals(
				visitRecordInfo.getCustomer_Name())) {

			visit_address.setText("拜访地点 " + visitRecordInfo.getVisit_Address()
					+ "");
		}
		salesman_name.setText("业务员 " + visitRecordInfo.getSaler_Name() + "");
		visit_date.setText(visitRecordInfo.getVisit_Date() + "");
		custom_name.setText("终端客户 " + visitRecordInfo.getCustomer_Name() + "");
		visitlocation.setText("录入位置 " + visitRecordInfo.getVisit_location()
				+ "");
		visitremark.setText("备注 " + visitRecordInfo.getVisit_remark() + "");

		// 判定订单是否可以被修改
		if (xzd.mobile.android.common.DateUtil.getInstance().chkDate(
				visitRecordInfo.getVisit_Date(), 7)
				&& AppContext.user.getCounterMain_ID() == 0) {
			// modify.setVisibility(View.VISIBLE);
			edit_visit.setVisibility(View.VISIBLE);
		}

		// 库存数据

		if (mDatas.isEmpty()) {
			lv_visit_record_list.setVisibility(View.GONE);
		} else {
			LayoutParams layoutParameters = lv_visit_record_list
					.getLayoutParams();
			layoutParameters.height = DeviceUtil.dip2px(this,
					60 * mDatas.size());
			lv_visit_record_list.setLayoutParams(layoutParameters);
			lv_visit_record_list.setVisibility(View.VISIBLE);

		}
		mAdapter.notifyDataSetChanged();
		//
		if (AppContext.user.getCounterMain_ID() == 0) {
			add_order.setVisibility(View.VISIBLE);
		}

		if (AppContext.user.getSaler_level() == 1) {
			add_note.setVisibility(View.VISIBLE);
		}

		System.out
				.println("ActivityVisitDetail.UpdateView(){visitRecordInfo id:"
						+ visitRecordInfo.getVisitRecord_id() + "}");
		if (visitRecordInfo.getManagerID() == 0) {
			lv_mark_list.setVisibility(View.GONE);
			return;
		}
		// 点评
		{
			mDatasV2.add(new KeyValue("点评人:", visitRecordInfo.getManagerName()));
			mDatasV2.add(new KeyValue("点评意见", visitRecordInfo.getAnswer()));
			mDatasV2.add(new KeyValue("点评时间", visitRecordInfo.getAnswerTime()));
			mDatasV2.add(new KeyValue("点评地点", visitRecordInfo
					.getAnswerLocation()));

			mAdapterV2.notifyDataSetChanged();
			LayoutParams layoutParameters = lv_mark_list.getLayoutParams();
			layoutParameters.height = DeviceUtil.dip2px(this,
					60 * mDatasV2.size());
			lv_mark_list.setLayoutParams(layoutParameters);
			lv_mark_list.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle bundle = null;
		Intent intent = null;
		switch (v.getId()) {
		case R.id.view_lib:
			intent = new Intent(this, ActivityLibList.class);
			intent.putExtra(ActivityLibList.LIBDATA,
					visitRecordInfo.getVisitRecord_id());
			startActivity(intent);
			break;
		case R.id.edit_visit:
			bundle = new Bundle();
			intent = new Intent(this, ActivityVisitAdd.class);
			bundle.putInt(ActivityVisitAdd.ACTION_TYPE,
					ActivityVisitAdd.ACTION_EDIT);
			intent.putExtra(ActivityVisitAdd.VISIT_DATA,
					visitRecordInfo.getVisitRecord());
			startActivity(intent);
			break;
		case R.id.add_lib:
			Bundle data = new Bundle();
			data.putInt(ActivityAddToLib.ACTION, ActivityAddToLib.ACTION_ADD2);
			data.putSerializable(ActivityAddToLib.VISIT_DATA, visitRecordInfo);
			UIHelper.getInstance().ShowAddToLibUI(this, data);
			break;
		case R.id.go_back:
			finish();
			break;
		case R.id.add_order:
			if (AppContext.is_login()) {
				bundle = new Bundle();
				if (AppContext.user.getCounterMain_ID() == 0) {
					bundle.putInt(AddOrderActivity.ACTION_TYPE,
							AddOrderActivity.ACTION_ADD);
					UIHelper.getInstance().showOrderActivity(this, bundle);
					bundle = null;
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {

				}

			} else {
				UIHelper.Toast(this, "对不起,您还未进行登录");
			}
			break;
		case R.id.add_note:
			if (AppContext.user.getSaler_level() == 1) {
				bundle = new Bundle();
				bundle.putSerializable(AddRemark.VISIT_DATA, visitRecordInfo);
				UIHelper.showAddRemark(this, bundle);
			}
			break;
		}
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			throw new IllegalArgumentException("参数传递为空");
		}
		visitRecordInfo = new VisitRecordInfo(
				(VisitRecord) bundle.getSerializable(VISIT_DATA));
		bundle = null;
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		loading = (LinearLayout) findViewById(R.id.loading);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		text = (TextView) findViewById(R.id.text);
		loading.setVisibility(View.VISIBLE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("拜访详情");
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		text.setText("正在加载努力加载中");
		wrap_main = (ScrollView) findViewById(R.id.wrap_main);
		wrap_main.setVisibility(View.GONE);
		visit_address = (TextView) findViewById(R.id.visit_address);

		salesman_name = (TextView) findViewById(R.id.salesman_name);
		visit_date = (TextView) findViewById(R.id.visit_date);
		custom_name = (TextView) findViewById(R.id.custom_name);
		visitremark = (TextView) findViewById(R.id.visitremark);
		visitlocation = (TextView) findViewById(R.id.visitlocation);
		view_lib = (TextView) findViewById(R.id.view_lib);
		add_lib = (TextView) findViewById(R.id.add_lib);

		view_lib.setOnClickListener(this);
		add_lib.setOnClickListener(this);
		if (visitRecordInfo.getSaler_Name().equals(
				AppContext.user.getM_mgr_name())) {
			visitlocation.setVisibility(View.GONE);
		}
		if (DateUtil.getInstance().chkDate(visitRecordInfo.getVisit_Date(), 3)
				&& AppContext.user.getCounterMain_ID() == 0) {
			add_lib = (TextView) findViewById(R.id.add_lib);
		}

		// 库存相关
		{
			lv_visit_record_list = (ListView) findViewById(R.id.lv_visit_record_list);
			lv_visit_record_list.setAdapter(mAdapter);
		}

		{
			add_order = (TextView) findViewById(R.id.add_order);
			add_order.setOnClickListener(this);
			add_note = (TextView) findViewById(R.id.add_note);
			add_note.setOnClickListener(this);
			edit_visit = (TextView) findViewById(R.id.edit_visit);
			edit_visit.setOnClickListener(this);
		}
		add_note.setVisibility(View.GONE);
		if (AppContext.user.getSaler_level() == 1) {
			add_note.setVisibility(View.VISIBLE);
		}

		visitlocation.setVisibility(View.GONE);
		if (AppContext.user.getCounterMain_ID() == 1
				|| AppContext.user.getCounterMain_ID() == 2) {
			visitlocation.setVisibility(View.VISIBLE);
		}
		lv_mark_list = (ListView) findViewById(R.id.lv_mark_list);
		lv_mark_list.setAdapter(mAdapterV2);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		visitHandler = new VisitHandler();
		mAdapter = new StoAdapter();
		mDatas = new ArrayList<StoChange>();
		mDatasV2 = new ArrayList<KeyValue>();
		mAdapterV2 = new KeyValueAdapter();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visit_detail);

		appcontext = (AppContext) getApplication();
		helper = UIHelper.getInstance();
		orderHelper = OrderHelper.getInstance();

		this.getPreActivityData();
		this.initData();
		this.initView();
		this.ObtainData();
	}

	private void ObtainData() {
		// TODO Auto-generated method stub
		GetVisitContent(visitRecordInfo.getVisitRecord_id(), true);
	}

	private void GetVisitContent(final int visitRecord_id, final boolean b) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {

				VisitRecordInfo rs;
				Message msg = visitHandler.obtainMessage();
				try {
					rs = TerminateBiz.getInstance().GetVisitRecordInfo(
							appcontext, visitRecord_id,
							AppContext.user.getM_id(), b);
					System.out
							.println("ActivityVisitDetail.GetVisitContent(...).new Runnable() {...}.run(){recordinfo:"
									+ rs + "}");
					if (rs == null) {
						throw new AppException();
					}

					// 获取库存数据
					StoChangeList tmp = null;
					tmp = TerminateBiz.GetStoChanges(appcontext, 1, 1000,
							rs.getVisitRecord_id(), true);
					System.out
							.println("ActivityVisitDetail.GetVisitContent(...).new Runnable() {...}.run(){lib datas:"
									+ tmp + "}");
					if (null != tmp) {
						if (mDatas != null && tmp != null
								&& tmp.getDatas() != null) {
							mDatas.clear();
							mDatas.addAll(tmp.getDatas());
						}
					}

					msg.what = HANDLER_GET_DETAIL_SUCCESS;
					msg.obj = rs;

				} catch (Exception e) {
					Log.e(TAG, "getNotificationContent Runnable：获取数据失败");
					e.printStackTrace();
					msg.what = HANDLER_GET_DETAIL_FAILED;
					msg.obj = e;

				}
				visitHandler.sendMessage(msg);

			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private final class VisitHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case TerminateDetailActivity.HANDLER_GET_DETAIL_SUCCESS:

				if (null != msg.obj) {
					// data.UpdateContent((MedicineOrderDetail) msg.obj);
					visitRecordInfo = (VisitRecordInfo) msg.obj;
					UpdateView();
				}
				loading.setVisibility(View.GONE);

				wrap_main.setVisibility(View.VISIBLE);
				AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
				anim.setDuration(1000);
				wrap_main.startAnimation(anim);
				anim = null;
				break;

			case TerminateDetailActivity.HANDLER_GET_DETAIL_EXCEPTION:

			case TerminateDetailActivity.HANDLER_GET_DETAIL_FAILED:

				loading.setVisibility(View.VISIBLE);
				progressbar.setVisibility(View.GONE);
				text.setVisibility(View.VISIBLE);
				text.setText("加载数据失败");

				break;

			}
		}

	}

	private final class StoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public StoChange getItem(int position) {
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
					R.layout.xzd_list_item_v2, null);

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
			textView.setText(mDatas.get(position).getMedicine_Name());

			final TextView textView2 = (TextView) convertView
					.findViewById(R.id.item2);
			textView2.setText(String.valueOf(mDatas.get(position)
					.getMedicine_Num()));

			return convertView;
		}

	}

	private final class KeyValueAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatasV2.size();
		}

		@Override
		public KeyValue getItem(int position) {
			// TODO Auto-generated method stub
			return mDatasV2.get(position);
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
					R.layout.xzd_list_item_v2, null);

			if (mDatasV2.size() == 1) {
				convertView
						.setBackgroundResource(R.drawable.ca_selector_listitem_single);

			} else if (mDatasV2.size() == 2) {
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
			} else if (mDatasV2.size() > 2) {

				if (0 == position) {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_first);
				} else if ((mDatasV2.size() - 1) == position) {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_last);

				} else {
					convertView
							.setBackgroundResource(R.drawable.ca_selector_listitem_content);
				}

			}

			final TextView textView = ((TextView) convertView
					.findViewById(R.id.item1));
			textView.setText(mDatasV2.get(position).getKey());

			final TextView textView2 = (TextView) convertView
					.findViewById(R.id.item2);
			textView2
					.setText(String.valueOf(mDatasV2.get(position).getValue()));

			return convertView;
		}

	}
}
