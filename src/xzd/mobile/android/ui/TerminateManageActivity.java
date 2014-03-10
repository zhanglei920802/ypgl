package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import xzd.mobile.android.business.CommonBiz;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.AreaInfo;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustGroup;
import xzd.mobile.android.model.CustomList;
import xzd.mobile.android.model.CustomerModel;
import xzd.mobile.android.model.Notice;
import xzd.mobile.android.model.SalerSimple;

import xzd.mobile.android.ui.PullToRefreshListView.OnRefreshListener;
import xzd.mobile.android.ui.adapter.AdapterTerminate;
import xzd.mobile.android.ui.intf.ActivityItf;

public class TerminateManageActivity extends BaseActivity implements
		ActivityItf, OnItemClickListener, OnClickListener, OnScrollListener,
		OnRefreshListener {

	public static final String TAG = "TerminateManageActivity";

	public static final int HANDLER_ERROR = -1;
	public static final int HANDLER_SUCCESS = 1;
	public static final int HANDLER_GET_MAN_ID_SUCCESS = 2;
	public static final int HANDLER_GET_MAIN_ID_FAILED = 3;
	// Helper
	private UIHelper uihelper = null;
	private StringUtils stringutils = null;
	private AppContext appcontext = null;
	private TerminateBiz terminateBiz = null;
	// ListView
	private View listview_footer_more = null;// 加载更多View
	private ProgressBar listview_foot_progress = null;// 加载更多进度条
	private TextView listview_foot_more = null;// 加载更多文字

	private int datasum = 0;// 总的数据
	private List<CustomerModel> datas = null;
	private AdapterTerminate adpter = null;
	private TerminateHandeler terminateHandeler = null;
	private TextView detail_title = null;
	private PullToRefreshListView terminate_manage_list = null;
	private TextView add = null;
	private TextView go_back = null;
	// Loading
	private ProgressBar progressbar = null;
	private TextView text = null;
	private LinearLayout loading = null;

	// group data
	public static final String GROUP_DATA = "groupdata";
	private CustGroup chooedGroup = null;
	public static final String CONTERMAN_DATA = "countermandata";
	private SalerSimple chooedCounterMan = null;
	// search
	private EditText cusmgr_input1 = null;
	private TextView cusmgr_input2 = null;
	private Button cusmgr_search = null;

	// 管理员
	private List<SalerSimple> mDatas = null;
	private MangerAdapter mAdapter = null;
	public static final String START_METHOD = "START_METHOD";
	public static final int METHOD_MANAGER = 1;
	private int startMethod = 0;
	private AreaInfo mAreaInfo = null;
	private TextView group_edit = null;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		if (!AppContext.is_login()) {
			onDestroy();
			return;
		}

		if (AppContext.user.getCounterMain_ID() == -1) {
			CommonBiz.isCounerMan(appcontext);
		}

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("客户管理");
		terminate_manage_list = (PullToRefreshListView) findViewById(R.id.terminate_manage_list);

		listview_footer_more = getLayoutInflater().inflate(
				R.layout.listview_foot_more, null);
		listview_foot_progress = (ProgressBar) listview_footer_more
				.findViewById(R.id.listview_foot_progress);
		listview_foot_more = (TextView) listview_footer_more
				.findViewById(R.id.listview_foot_more);

		terminate_manage_list.addFooterView(listview_footer_more);
		terminate_manage_list.setAdapter(adpter);
		terminate_manage_list.setOnItemClickListener(this);
		terminate_manage_list.setOnScrollListener(this);
		terminate_manage_list.setOnRefreshListener(this);

		add = (TextView) findViewById(R.id.add);
		add.setOnClickListener(this);

		loading = (LinearLayout) findViewById(R.id.loading);
		text = (TextView) findViewById(R.id.text);
		text.setText("正在努力加载中");
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		terminate_manage_list.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);

		{
			cusmgr_input1 = (EditText) findViewById(R.id.cusmgr_input1);
			cusmgr_input2 = (TextView) findViewById(R.id.cusmgr_input2);
			cusmgr_search = (Button) findViewById(R.id.cusmgr_search);
			if (AppContext.user.getCounterMain_ID() == 0) {

			} else if (AppContext.user.getCounterMain_ID() != -1
					&& AppContext.user.getCounterMain_ID() != 0) {
				cusmgr_input1.setOnClickListener(this);
				cusmgr_input1.setHint("业务或日期");
				cusmgr_input2.setHint("地区");
				cusmgr_input2.setKeyListener(null);
			}
			cusmgr_input2.setOnClickListener(this);
			cusmgr_search.setOnClickListener(this);
		}
		group_edit = (TextView) findViewById(R.id.group_edit);
		group_edit.setVisibility(View.GONE);
		if (AppContext.user.getCounterMain_ID() == 1
				|| AppContext.user.getCounterMain_ID() == 2) {

			// 选择销售人员
			cusmgr_input1.setHint("业务员名称");
			cusmgr_input2.setHint("点击选择区域");

			Intent intent = new Intent(this, ActivityCounterManChoose.class);
			intent.putExtra(ActivityCounterManChoose.COUNTERMANDATA,
					AppContext.user.getM_id());
			startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_CONTERMAN);
		} else if (AppContext.user.getCounterMain_ID() == 0) {
			group_edit.setOnClickListener(this);
			group_edit.setVisibility(View.VISIBLE);
			getData();
		}

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		terminateHandeler = new TerminateHandeler();

		datas = new ArrayList<CustomerModel>();
		adpter = new AdapterTerminate(datas, this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminate_manage);
		uihelper = UIHelper.getInstance();
		stringutils = StringUtils.getInstance();
		appcontext = (AppContext) getApplication();
		terminateBiz = TerminateBiz.getInstance();

		getPreActivityData();
		initData();
		initView();

		// ConfirmIsCounterMan();
	}

	private void getData() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Init Data page=" + 1);
		LoadData(1, terminateHandeler, UIHelper.LISTVIEW_ACTION_INIT,
				AppContext.user.getM_id(), cusmgr_input1.getText().toString(),
				chooedGroup == null ? -1 : chooedGroup.getGroupID());
	}

	private boolean isSearchMode(String name) {
		return !TextUtils.isEmpty(name);
	}

	private void LoadData(final int pageIndex, final TerminateHandeler handler,
			final int tag, final int userID, final String name,
			final int groupID) {
		// TODO Auto-generated method stub
		/**
		 * 开启线程加载数据
		 */

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				boolean isRefresh = false;

				if (tag == UIHelper.LISTVIEW_ACTION_REFRESH
						|| tag == UIHelper.LISTVIEW_ACTION_SCROLL
						|| tag == UIHelper.LISTVIEW_ACTION_INIT) {
					isRefresh = true;
				}
				CustomList datas = null;
				try {
					if (AppContext.user.getCounterMain_ID() == 0) {

						Log.d(TAG, "Load DataIng please wait.....");
						datas = terminateBiz.GetCustomerList(appcontext,
								pageIndex, AppContext.PAGE_SIZE, userID,
								isRefresh, name, groupID, isSearchMode(name));
						if (datas == null) {
							throw new AppException();
						}
						System.out
								.println("TerminateManageActivity.LoadData(...).new Runnable() {...}.run(){datas"
										+ datas.getCuLists() + "}");
						Log.d(TAG, "获取数据:" + datas.getCuLists().toString());
						msg.what = HANDLER_SUCCESS;
						msg.obj = datas;
						msg.arg1 = datas.getCuLists().size();
						Log.d(TAG, "Load Data successfully......");
					} else if (AppContext.user.getCounterMain_ID() == 1
							|| AppContext.user.getCounterMain_ID() == 2) {
						Log.d(TAG, "Load DataIng please wait.....");
						/**
						 * 传递的前面3个参数与之前的接口 完全相同，后面的name是用户在业务员输入框输入的查询名称，
						 * areacode为用户选择的区域编号，如果查询条件没有，则传递空 字符串。
						 */
						datas = terminateBiz.GetCustomerListByManager(
								appcontext,
								pageIndex,
								AppContext.PAGE_SIZE,
								AppContext.user.getM_id(),
								isRefresh,
								cusmgr_input1.getText().toString(),
								StringUtils.getInstance().isEmpty(
										appcontext.area_code) ? ""
										: appcontext.area_code);
						if (datas == null || datas.getCuLists() == null) {
							return;
						}
						System.out
								.println("TerminateManageActivity.LoadData(...).new Runnable() {...}.run(){type[manager]datas"
										+ datas.getCuLists() + "}");
						Log.d(TAG, "获取数据:" + datas.getCuLists().toString());
						msg.what = HANDLER_SUCCESS;
						msg.obj = datas;
						msg.arg1 = datas.getCuLists().size();
						Log.d(TAG, "Load Data successfully......");
					}
				} catch (Exception e) {
					Log.d(TAG, "Load Data Faild......");
					e.printStackTrace();
					msg.what = HANDLER_ERROR;
					msg.obj = e;
				}

				msg.arg2 = tag;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.terminate_manage_list:

			if (position == 0 || listview_footer_more == view) {
				return;
			}

			Bundle bundle = new Bundle();
			// 跳转到详细界面
			bundle.putSerializable(TerminateDetailActivity.TERMINATE_DATA,
					datas.get(position - 1));
			uihelper.showTerminateDetailActivity(this, bundle);
			bundle = null;
			break;

		default:
			break;
		}
	}

	private final class TerminateHandeler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_SUCCESS:
				HandleData(msg.obj, msg.arg1, msg.arg2);

				if (msg.arg1 < AppContext.PAGE_SIZE) {// 说明数据已经加载完毕
					terminate_manage_list.setTag(UIHelper.LISTVIEW_DATA_FULL);
					adpter.notifyDataSetChanged();
					listview_foot_more.setText(getString(R.string.load_full));
				} else if (msg.arg1 == AppContext.PAGE_SIZE) {
					terminate_manage_list.setTag(UIHelper.LISTVIEW_DATA_MORE);
					adpter.notifyDataSetChanged();
					listview_foot_more.setText(getString(R.string.load_more));
				}
				UpdateView();
				if (adpter.getCount() < 1) {
					terminate_manage_list.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					listview_foot_more.setText(getString(R.string.load_empty));
					terminate_manage_list.setVisibility(View.GONE);
					loading.setVisibility(View.VISIBLE);
					progressbar.setVisibility(View.GONE);
					text.setVisibility(View.VISIBLE);
					text.setText("没有数据");
				}

				listview_foot_progress.setVisibility(View.GONE);

				if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					terminate_manage_list
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());
					terminate_manage_list.setSelection(0);
				}
				break;

			case HANDLER_ERROR:
				terminate_manage_list.setTag(UIHelper.LISTVIEW_DATA_MORE);
				listview_foot_more.setText(R.string.loading_error);
				UIHelper.Toast(TerminateManageActivity.this,
						R.string.http_exception_error);
				break;
			case HANDLER_GET_MAIN_ID_FAILED:
				break;
			case HANDLER_GET_MAN_ID_SUCCESS:

				break;
			}
		}

	}

	public Notice HandleData(final Object obj, final int size, final int type) {
		Notice notice = null;
		switch (type) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			CustomList newList = (CustomList) obj;
			datasum = size;
			if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {

				if (datas.size() > 0) {

					for (CustomerModel medicineorder : newList.getCuLists()) {
						boolean tag = false;

						for (CustomerModel simplepartimejobbean2 : datas) {
							if (medicineorder.getCustomer_id() == simplepartimejobbean2
									.getCustomer_id()) {
								tag = true;
								break;
							}
						}
						if (!tag) {
						}
					}
					// datas.addAll(0, newList.getMedicine_list());
				} else {
				}

			}
			if (datas != null) {
				datas.clear();
				datas.addAll(newList.getCuLists());
			}

			if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {// /更新数据

			}
			break;

		case UIHelper.LISTVIEW_ACTION_SCROLL:
			// Notice list = new Notice();

			CustomList mlist = (CustomList) obj;
			datasum += size;

			if (datas.size() > 0) {

				for (CustomerModel medicineorder : mlist.getCuLists()) {
					boolean tag = false;

					for (CustomerModel simplepartimejobbean2 : datas) {
						if (medicineorder.getCustomer_id() == simplepartimejobbean2
								.getCustomer_id()) {
							tag = true;
							break;
						}
					}
					if (!tag)
						datas.add(medicineorder);
				}

			} else {

				datas.addAll(datas.size() - 1, mlist.getCuLists());
			}

			break;
		}

		return notice;
	}

	public void UpdateView() {
		// TODO Auto-generated method stub
		loading.setVisibility(View.GONE);
		terminate_manage_list.setVisibility(View.VISIBLE);
	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Load data reresh:page=1");
		LoadData(1, terminateHandeler, UIHelper.LISTVIEW_ACTION_REFRESH,
				AppContext.user.getM_id(), cusmgr_input1.getText().toString(),
				chooedGroup == null ? -1 : chooedGroup.getGroupID());
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		terminate_manage_list.onScrollStateChanged(view, scrollState);
		if (datas.isEmpty()) {
			return;
		}

		// 判断是否滚动到底部
		boolean scrollEnd = false;

		try {
			if (view.getPositionForView(listview_footer_more) == view
					.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}

		int lvDataState = stringutils.toInt(terminate_manage_list.getTag());

		if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {

			terminate_manage_list.setTag(UIHelper.LISTVIEW_DATA_LOADING);
			listview_foot_more.setText(R.string.loading);
			listview_foot_progress.setVisibility(View.VISIBLE);
			// 当前pageIndex
			int pageIndex = 1 + (datasum / AppContext.PAGE_SIZE);// 计算pagesize
			Log.d(TAG, "load data scroll,page=" + pageIndex + ".....");
			LoadData(pageIndex, terminateHandeler,
					UIHelper.LISTVIEW_ACTION_SCROLL, AppContext.user.getM_id(),
					cusmgr_input1.getText().toString(),
					chooedGroup == null ? -1 : chooedGroup.getGroupID());
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		try {
			terminate_manage_list.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		Bundle bundle = null;
		switch (v.getId()) {
		case R.id.group_edit:
			if (AppContext.user.getCounterMain_ID() == 0) {
				bundle = new Bundle();
				bundle.putInt(GroupManager.START_METHOD,
						GroupManager.METHOD_EDIT);
				UIHelper.getInstance();
				UIHelper.showGroupManager(this, bundle);
			}
			break;
		case R.id.add:
			if (AppContext.is_login()) {
				// uihelper.showOrderActivity(this);
				bundle = new Bundle();
				bundle.putInt(ActivityAddOrEditConsumer.ACTION_TYPE,
						AddOrderActivity.ACTION_ADD);
				uihelper.showEditOrAddCustomActivity(this, bundle);

			} else {
				UIHelper.Toast(this, "对不起,您还未进行登录");
			}

			break;
		case R.id.go_back:
			onDestroy();
			break;
		case R.id.cusmgr_input2:
			if (AppContext.user.getCounterMain_ID() < 0) {
				return;
			}

			if (AppContext.user.getCounterMain_ID() == 0) {
				intent = new Intent(this, GroupManager.class);
				intent.putExtra(GroupManager.START_METHOD, 0);
				startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_GROUP);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				intent = new Intent(this, ActivityProvinceChoose.class);
				startActivityForResult(intent,
						Config.REQUEST_CODE_CHOOSE_PROVINCE);

			}

			intent = null;
			break;
		case R.id.cusmgr_search:
			if (!AppContext.is_login()) {
				UIHelper.Toast(this, "对不起,您还未登陆");
				return;
			}
			int userID = AppContext.user.getM_id();
			if (!appcontext.isNetworkConnected()) {
				UIHelper.Toast(this, "对不起,暂无网络连接");
				return;
			}

			if (TextUtils.isEmpty(cusmgr_input1.getText().toString())) {
				UIHelper.Toast(this, R.string.input_can_not_be_null);
				return;
			}

			if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				System.out
						.println("TerminateManageActivity.onClick(){type[manager]}");
				LoadData(1, terminateHandeler,
						UIHelper.LISTVIEW_ACTION_REFRESH, userID, "", 0);
			} else if (AppContext.user.getCounterMain_ID() == 0) {

				String name = cusmgr_input1.getText().toString();
				int groupID = -1;
				if (StringUtils.getInstance().isEmpty(
						cusmgr_input2.getText().toString())) {
					groupID = -1;
				} else {
					groupID = chooedGroup.getGroupID();
				}

				LoadData(1, terminateHandeler,
						UIHelper.LISTVIEW_ACTION_REFRESH, userID, name, groupID);
			}

			break;
		case R.id.cusmgr_input1:
			if (AppContext.user == null) {
				return;
			}

			if (AppContext.user.getCounterMain_ID() < 1) {
				UIHelper.Toast(this, "无权限");
				return;
			}

			intent = new Intent(this, ActivityCounterManChoose.class);
			intent.putExtra(ActivityCounterManChoose.COUNTERMANDATA,
					AppContext.user.getM_id());
			startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_CONTERMAN);
			intent = null;

			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onDestroy();
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case Config.RESULT_CODE_CHOOSE_GROUP:
			if (null == data) {
				return;
			}

			chooedGroup = (CustGroup) data.getSerializableExtra(GROUP_DATA);
			if (null != chooedGroup) {
				cusmgr_input2.setText(chooedGroup.getGroupName());
				Log.d(TAG, "selected data[" + chooedGroup + "]");
			}
			break;
		case RESULT_CANCELED:

			break;
		case Config.RESULT_CODE_CHOOSE_COUNTMAN:
			if (null == data) {
				return;
			}
			chooedCounterMan = (SalerSimple) data
					.getSerializableExtra(OrderManageActivity.COUNTER_MAN_DATA);
			if (null != chooedCounterMan && AppContext.user != null
					&& AppContext.user.getCounterMain_ID() > 0) {

				cusmgr_input1.setText(chooedCounterMan.getSaler_name());
				LoadData(1, terminateHandeler,
						UIHelper.LISTVIEW_ACTION_REFRESH, 0, "", 0);
			}

			break;
		case Config.RESULT_CODE_CHOOSE_PROVINCE:
			if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				cusmgr_input2.setText(data
						.getStringExtra(ActivityAddOrEditConsumer.ADDRESS_DATA)
						+ " ");

			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}

	};

	private final class MangerAdapter extends BaseAdapter {

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
			ViewHolder tHolder = null;
			if (null == convertView) {
				convertView = getLayoutInflater().inflate(
						R.layout.terminate_visit_item_v2, null);
				tHolder = new ViewHolder();
				tHolder.visit_date = (TextView) convertView
						.findViewById(R.id.visit_date);
				tHolder.salesman_name = (TextView) convertView
						.findViewById(R.id.salesman_name);
				tHolder.hascomment = (TextView) convertView
						.findViewById(R.id.hascomment);

				convertView.setTag(tHolder);
			} else {
				tHolder = (ViewHolder) convertView.getTag();
			}

			tHolder.salesman_name.setText(String.format(getResources()
					.getString(R.string.sales_man_name), mDatas.get(position)
					.getSaler_name()));
			// tHolder.visit_date.setText(String.format(
			// getResources().getString(R.string.visit_date),
			// mDatas.get(position).getLastestVisitDate()));
			// tHolder.hascomment.setVisibility(View.GONE);
			// if (mDatas.get(position).HasComment == 1) {
			// tHolder.hascomment.setVisibility(View.VISIBLE);
			// }

			return convertView;
		}

		private final class ViewHolder {
			TextView salesman_name = null;
			TextView visit_date = null;
			TextView hascomment = null;

		}
	}
}
