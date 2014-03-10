package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.model.Notice;
import xzd.mobile.android.model.SalerVisitSimpleInfo;
import xzd.mobile.android.model.SalerVisitSimpleInfoList;
import xzd.mobile.android.model.VisistRecordList;
import xzd.mobile.android.model.VisitRecord;
import xzd.mobile.android.ui.PullToRefreshListView.OnRefreshListener;
import xzd.mobile.android.ui.adapter.AdapterTerminaterVisit;
import xzd.mobile.android.ui.intf.ActivityItf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

public class ActivityTerminateVisit extends BaseActivity implements
		ActivityItf, OnClickListener, OnItemClickListener, OnScrollListener,
		OnRefreshListener {
	public static final String TAG = "OrderManageActivity";
	public static final int HANDLER_ERROR = -1;
	public static final int HANDLER_SUCCESS = 1;
	// common vars
	public static final String START_METHOD = "START_METHOD";
	public static final int METHOD_MANAGER = 1;
	private int startMethod = 0;
	public static final String EXTRAS_SALER = "extras_saler";
	private SalerVisitSimpleInfo mSaler = null;
	// Helper
	private UIHelper uihelper = null;
	private StringUtils stringutils = null;
	private AppContext appcontext = null;
	private TerminateBiz terminateBiz = null;
	private CustomerSimple choosedCustom = null;
	// ListView
	private View listview_footer_more = null;// 加载更多View
	private ProgressBar listview_foot_progress = null;// 加载更多进度条
	private TextView listview_foot_more = null;// 加载更多文字

	private int datasum = 0;// 总的数据
	private List<VisitRecord> datas = null;
	private AdapterTerminaterVisit adpter = null;
	private ActivityTerminateHandler activityTerminateHandler = null;
	private TextView detail_title = null;
	private PullToRefreshListView lv_terminate_visit = null;
	private TextView add = null;
	private TextView go_back = null;
	// Loading
	private ProgressBar progressbar = null;
	private TextView text = null;
	private LinearLayout loading = null;
	private EditText cus_name = null;

	private Button search = null;

	//
	private List<SalerVisitSimpleInfo> mDatas = null;
	private TerminateVisitAdapterV2 mAdapterV2 = null;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.add:
			if (AppContext.is_login()) {
				Bundle data = new Bundle();
				data.putInt(ActivityVisitAdd.ACTION_TYPE,
						ActivityVisitAdd.ACTION_ADD);
				uihelper.showVisitAddActivity(this, data);
			} else {
				UIHelper.Toast(this, "对不起,您还未进行登录");
			}

			break;
		case R.id.go_back:
			onDestroy();
			break;
		case R.id.cus_name:

			if (startMethod == 0) {

				if (AppContext.user.getCounterMain_ID() == 0) {
					intent = new Intent(this, CustomChooseActivity.class);
					intent.putExtra(CustomChooseActivity.CUSTOMCHOOSEDATA,
							AppContext.user.getM_id());
					startActivityForResult(intent,
							Config.REQUEST_CODE_CHOOSE_CUSTOM);
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {

				}
			} else if (startMethod == METHOD_MANAGER) {

			}
			break;
		case R.id.search:
			if (startMethod == 0) {

				if (AppContext.user.getCounterMain_ID() == 0) {

					if (!AppContext.is_login()) {
						UIHelper.Toast(this, "对不起,您还未登陆");
						return;
					}

					if (!appcontext.isNetworkConnected()) {
						UIHelper.Toast(this, "对不起,暂无网络连接");
						return;
					}

					if (TextUtils.isEmpty(cus_name.getText().toString())) {
						UIHelper.Toast(this, "客户名称为空");
						return;
					}
					if (choosedCustom == null) {
						return;
					}

					LoadData(1, activityTerminateHandler,
							UIHelper.LISTVIEW_ACTION_REFRESH,
							choosedCustom.getCustomer_id());
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {
					if (!appcontext.isNetworkConnected()) {
						UIHelper.Toast(this, R.string.network_not_connected);
						return;
					}

					if (StringUtils.getInstance().isEmpty(
							cus_name.getText().toString())) {
						UIHelper.Toast(this, R.string.input_can_not_be_null);
						return;
					}

					if (!AppContext.is_login()) {
						UIHelper.Toast(this, R.string.xzd_no_login);
						return;
					}

					// do query
					QueryCountermenAndVisitDate(AppContext.user.getM_id(),
							cus_name.getText().toString());

				}
			} else if (startMethod == METHOD_MANAGER) {
				if (!appcontext.isNetworkConnected()) {
					UIHelper.Toast(this, R.string.network_not_connected);
					return;
				}

				if (StringUtils.getInstance().isEmpty(
						cus_name.getText().toString())) {
					UIHelper.Toast(this, R.string.input_can_not_be_null);
					return;
				}

				if (!AppContext.is_login()) {
					UIHelper.Toast(this, R.string.xzd_no_login);
					return;
				}

				if (null == mSaler) {
					return;
				}
				LoadData(1, activityTerminateHandler,
						UIHelper.LISTVIEW_ACTION_REFRESH, mSaler.getSaler_id());
			}
			break;
		}
	}

	/**
	 * 查询
	 * 
	 * @param userid
	 *            当前登录用户ID
	 * @param name
	 *            用户所输入的字符
	 */
	private void QueryCountermenAndVisitDate(final int userid, final String name) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					SalerVisitSimpleInfoList tmp = null;
					tmp = terminateBiz.QueryCountermenAndVisitDate(userid,
							name, appcontext, true);
					if (tmp != null && tmp.getmDatas() != null) {
						Message tMessage = activityTerminateHandler
								.obtainMessage();
						tMessage.what = 21;
						tMessage.obj = tmp;
						activityTerminateHandler.sendMessage(tMessage);
					}
					activityTerminateHandler.sendEmptyMessage(-21);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();

				}
			}
		}).start();
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		startMethod = getIntent().getIntExtra(START_METHOD, 0);
		if (startMethod == METHOD_MANAGER) {
			mSaler = (SalerVisitSimpleInfo) getIntent().getSerializableExtra(
					EXTRAS_SALER);
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("客户拜访");
		lv_terminate_visit = (PullToRefreshListView) findViewById(R.id.lv_terminate_visit);

		listview_footer_more = getLayoutInflater().inflate(
				R.layout.listview_foot_more, null);
		listview_foot_progress = (ProgressBar) listview_footer_more
				.findViewById(R.id.listview_foot_progress);
		listview_foot_more = (TextView) listview_footer_more
				.findViewById(R.id.listview_foot_more);

		lv_terminate_visit.addFooterView(listview_footer_more);
		if (startMethod == 0) {
			if (AppContext.user.getCounterMain_ID() == 0) {
				lv_terminate_visit.setAdapter(adpter);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				lv_terminate_visit.setAdapter(mAdapterV2);
			}

		} else if (startMethod == METHOD_MANAGER) {

			lv_terminate_visit.setAdapter(adpter);

		}

		lv_terminate_visit.setOnItemClickListener(this);
		lv_terminate_visit.setOnScrollListener(this);
		lv_terminate_visit.setOnRefreshListener(this);

		add = (TextView) findViewById(R.id.add);
		add.setOnClickListener(this);

		loading = (LinearLayout) findViewById(R.id.loading);
		text = (TextView) findViewById(R.id.text);
		text.setText("正在努力加载中");
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		lv_terminate_visit.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		{
			cus_name = (EditText) findViewById(R.id.cus_name);
			cus_name.setEnabled(true);
			if (startMethod == 0) {
				if (AppContext.user.getCounterMain_ID() == 0) {
					cus_name.setHint("点击选择客户");
					// cus_name.setFocusable(false);// 禁止输入
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {
					cus_name.setHint("业务员名称");
				}
			} else if (startMethod == METHOD_MANAGER) {
				// cus_name.setVisibility(View.GONE);
				cus_name.setHint("选择客户");// 但是只能查询当前业务员的
				// cus_name.setFocusable(false);// 禁止输入
			}
			cus_name.setText("");
			cus_name.setOnClickListener(this);
			search = (Button) findViewById(R.id.search);
			search.setOnClickListener(this);
		}

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		activityTerminateHandler = new ActivityTerminateHandler();

		if (startMethod == 0) {
			if (AppContext.user.getCounterMain_ID() == 0) {
				datas = new ArrayList<VisitRecord>();
				adpter = new AdapterTerminaterVisit(datas, this);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				mDatas = new ArrayList<SalerVisitSimpleInfo>();
				mAdapterV2 = new TerminateVisitAdapterV2();
			}
		} else if (startMethod == METHOD_MANAGER) {

			datas = new ArrayList<VisitRecord>();
			adpter = new AdapterTerminaterVisit(datas, this);

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminate_visit);
		appcontext = (AppContext) getApplication();
		if (!AppContext.is_login()) {
			UIHelper.Toast(this, R.string.xzd_no_login);
			onDestroy();
			return;
		}
		if (!appcontext.isNetworkConnected()) {

			UIHelper.Toast(this, R.string.zxd_no_network);
		}

		if (AppContext.user.getCounterMain_ID() == -1) {

			UIHelper.Toast(this, R.string.xzd_init_user_faild);
			onDestroy();

			return;

		}
		uihelper = UIHelper.getInstance();
		stringutils = StringUtils.getInstance();
		appcontext = (AppContext) getApplication();
		terminateBiz = TerminateBiz.getInstance();

		getPreActivityData();
		initData();
		initView();

	}

	private void getData() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Init Data page=" + 1);
		LoadData(1, activityTerminateHandler, UIHelper.LISTVIEW_ACTION_INIT,
				choosedCustom == null ? -1 : choosedCustom.getCustomer_id());
	}

	private void LoadData(final int pageIndex,
			final ActivityTerminateHandler handler, final int tag,
			final int name) {
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

				try {
					Log.d(TAG, "Load DataIng please wait.....");
					if (startMethod == 0) {

						if (AppContext.user.getCounterMain_ID() == 0) {
							VisistRecordList datas = null;
							datas = terminateBiz.GetVisitRecords(appcontext,
									pageIndex, AppContext.PAGE_SIZE,
									AppContext.user.getM_id(), isRefresh, name,
									isSearchMode(name));

							if (datas == null) {
								return;
							}
							msg.what = HANDLER_SUCCESS;
							msg.obj = datas;
							msg.arg1 = datas.getDatas().size();
							Log.d(TAG, "Load Data successfully......");
						} else if (AppContext.user.getCounterMain_ID() == 1
								|| AppContext.user.getCounterMain_ID() == 2) {

							SalerVisitSimpleInfoList datas = null;
							datas = TerminateBiz.GetCountermenPage(appcontext,
									pageIndex, 100, AppContext.user.getM_id(),
									true);

							if (datas == null) {
								return;
							}
							msg.what = HANDLER_SUCCESS;
							msg.obj = datas;
							msg.arg1 = datas.getmDatas().size();
							Log.d(TAG, "Load Data successfully......");
						}
					} else if (startMethod == METHOD_MANAGER) {// 如果

						System.out
								.println("ActivityTerminateVisit.LoadData(...).new Runnable() {...}.run(){startMethod == METHOD_MANAGER}");
						VisistRecordList datas = null;
						if (null == mSaler) {
							return;
						}
						datas = TerminateBiz.GetVisitRecordsBySaler(
								appcontext,
								pageIndex,
								AppContext.PAGE_SIZE,
								mSaler.getSaler_id(),
								StringUtils.getInstance().isEmpty(
										cus_name.getText().toString()) ? ""
										: cus_name.getText().toString(),
								isRefresh);

						if (datas == null) {
							return;
						}
						Log.d(TAG, "startMethod == METHOD_MANAGER{datas"
								+ datas + "}");
						msg.what = HANDLER_SUCCESS;
						msg.obj = datas;
						msg.arg1 = datas.getDatas().size();
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		onDestroy();
		super.onBackPressed();
	}

	private boolean isSearchMode(int name) {
		return name != -1;
	}

	private final class ActivityTerminateHandler extends Handler {

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case 21:
				if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {
					if (mDatas != null) {
						mDatas.clear();

						mDatas.addAll(((SalerVisitSimpleInfoList) msg.obj)
								.getmDatas());
					}
					mAdapterV2.notifyDataSetChanged();
					lv_terminate_visit.setTag(UIHelper.LISTVIEW_DATA_FULL);
					listview_foot_more.setText(getString(R.string.load_full));
				}
				break;
			case -21:
				// lv_terminate_visit.setTag(UIHelper.LISTVIEW_DATA_MORE);
				// listview_foot_more.setText(R.string.loading_error);
				// UIHelper.Toast(ActivityTerminateVisit.this,
				// R.string.http_exception_error);
				break;
			case HANDLER_SUCCESS:
				Notice notice = HandleData(msg.obj, msg.arg1, msg.arg2);
				if (startMethod == 0) {

					if (AppContext.user.getCounterMain_ID() == 1
							|| AppContext.user.getCounterMain_ID() == 2) {

						if (msg.arg1 < AppContext.PAGE_SIZE) {// 说明数据已经加载完毕
							lv_terminate_visit
									.setTag(UIHelper.LISTVIEW_DATA_FULL);
							mAdapterV2.notifyDataSetChanged();
							listview_foot_more
									.setText(getString(R.string.load_full));
						} else if (msg.arg1 == AppContext.PAGE_SIZE) {
							lv_terminate_visit
									.setTag(UIHelper.LISTVIEW_DATA_MORE);
							mAdapterV2.notifyDataSetChanged();
							listview_foot_more
									.setText(getString(R.string.load_more));
						}
						UpdateView();
						if (mAdapterV2.getCount() < 1) {

							lv_terminate_visit
									.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
							listview_foot_more
									.setText(getString(R.string.load_empty));
							lv_terminate_visit.setVisibility(View.GONE);
							loading.setVisibility(View.VISIBLE);
							progressbar.setVisibility(View.GONE);
							text.setVisibility(View.VISIBLE);
							text.setText("没有数据");

						}

						listview_foot_progress.setVisibility(View.GONE);

						if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
							lv_terminate_visit
									.onRefreshComplete(getString(R.string.pull_to_refresh_update)
											+ new Date().toLocaleString());
							lv_terminate_visit.setSelection(0);
						}
					} else if (AppContext.user.getCounterMain_ID() == 0) {

						if (msg.arg1 < AppContext.PAGE_SIZE) {// 说明数据已经加载完毕
							lv_terminate_visit
									.setTag(UIHelper.LISTVIEW_DATA_FULL);
							adpter.notifyDataSetChanged();
							listview_foot_more
									.setText(getString(R.string.load_full));
						} else if (msg.arg1 == AppContext.PAGE_SIZE) {
							lv_terminate_visit
									.setTag(UIHelper.LISTVIEW_DATA_MORE);
							adpter.notifyDataSetChanged();
							listview_foot_more
									.setText(getString(R.string.load_more));
						}
						UpdateView();
						if (adpter.getCount() < 1) {
							lv_terminate_visit
									.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
							listview_foot_more
									.setText(getString(R.string.load_empty));
							lv_terminate_visit.setVisibility(View.GONE);
							loading.setVisibility(View.VISIBLE);
							progressbar.setVisibility(View.GONE);
							text.setVisibility(View.VISIBLE);
							text.setText("没有数据");
						}

						listview_foot_progress.setVisibility(View.GONE);

						if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
							lv_terminate_visit
									.onRefreshComplete(getString(R.string.pull_to_refresh_update)
											+ new Date().toLocaleString());
							lv_terminate_visit.setSelection(0);
						}
					}
				} else if (startMethod == METHOD_MANAGER) {
					if (msg.arg1 < AppContext.PAGE_SIZE) {// 说明数据已经加载完毕
						lv_terminate_visit.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adpter.notifyDataSetChanged();
						listview_foot_more
								.setText(getString(R.string.load_full));
					} else if (msg.arg1 == AppContext.PAGE_SIZE) {
						lv_terminate_visit.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adpter.notifyDataSetChanged();
						listview_foot_more
								.setText(getString(R.string.load_more));
					}
					UpdateView();
					if (adpter.getCount() < 1) {
						lv_terminate_visit.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
						listview_foot_more
								.setText(getString(R.string.load_empty));
						lv_terminate_visit.setVisibility(View.GONE);
						loading.setVisibility(View.VISIBLE);
						progressbar.setVisibility(View.GONE);
						text.setVisibility(View.VISIBLE);
						text.setText("没有数据");
					}

					listview_foot_progress.setVisibility(View.GONE);

					if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
						lv_terminate_visit
								.onRefreshComplete(getString(R.string.pull_to_refresh_update)
										+ new Date().toLocaleString());
						lv_terminate_visit.setSelection(0);
					}
				}
				break;

			case HANDLER_ERROR:
				lv_terminate_visit.setTag(UIHelper.LISTVIEW_DATA_MORE);
				listview_foot_more.setText(R.string.loading_error);
				UIHelper.Toast(ActivityTerminateVisit.this,
						R.string.http_exception_error);

			}
		}

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Load data reresh:page=1");
		LoadData(1, activityTerminateHandler, UIHelper.LISTVIEW_ACTION_REFRESH,
				-1);
	}

	public void UpdateView() {
		// TODO Auto-generated method stub
		loading.setVisibility(View.GONE);
		lv_terminate_visit.setVisibility(View.VISIBLE);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		lv_terminate_visit.onScrollStateChanged(view, scrollState);

		if (startMethod == 0) {

			if (AppContext.user.getCounterMain_ID() == 0) {
				if (datas.isEmpty()) {
					return;
				}
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				if (mDatas.isEmpty()) {
					return;
				}
			}
		} else if (startMethod == METHOD_MANAGER) {
			if (datas.isEmpty()) {
				return;
			}
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

		int lvDataState = stringutils.toInt(lv_terminate_visit.getTag());

		if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {

			lv_terminate_visit.setTag(UIHelper.LISTVIEW_DATA_LOADING);
			listview_foot_more.setText(R.string.loading);
			listview_foot_progress.setVisibility(View.VISIBLE);
			// 当前pageIndex
			int pageIndex = 1 + (datasum / AppContext.PAGE_SIZE);// 计算pagesize
			Log.d(TAG, "load data scroll,page=" + pageIndex + ".....");
			LoadData(pageIndex, activityTerminateHandler,
					UIHelper.LISTVIEW_ACTION_SCROLL, -1);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		lv_terminate_visit.onScroll(view, firstVisibleItem, visibleItemCount,
				totalItemCount);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.lv_terminate_visit:
			Bundle bundle;
			if (position == 0 || listview_footer_more == view) {
				return;
			}
			if (startMethod == 0) {

				if (AppContext.user.getCounterMain_ID() == 0) {

					bundle = new Bundle();
					bundle.putSerializable(ActivityVisitDetail.VISIT_DATA,
							datas.get(position - 1));
					// 跳转到详细界面
					uihelper.showVisitDetail(this, bundle);
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {

					bundle = new Bundle();
					bundle.putSerializable(EXTRAS_SALER,
							mDatas.get(position - 1));
					bundle.putInt(START_METHOD, METHOD_MANAGER);
					uihelper.ShowTerminateVisitActivity(this, bundle);

				}
			} else if (startMethod == METHOD_MANAGER) {
				bundle = new Bundle();
				bundle.putSerializable(ActivityVisitDetail.VISIT_DATA,
						datas.get(position - 1));
				// 跳转到详细界面
				uihelper.showVisitDetail(this, bundle);
			}
			break;

		default:
			break;
		}
	}

	public Notice HandleData(final Object obj, final int size, final int type) {
		Notice notice = null;
		switch (type) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			if (startMethod == 0) {

				if (AppContext.user.getCounterMain_ID() == 0) {
					VisistRecordList newList = (VisistRecordList) obj;
					datasum = size;
					if (AppContext.user.getCounterMain_ID() == 0) {

						if (datas.size() > 0) {

							for (VisitRecord medicineorder : newList.getDatas()) {
								boolean tag = false;

								for (VisitRecord simplepartimejobbean2 : datas) {
									if (medicineorder.getVisitRecord_id() == simplepartimejobbean2
											.getVisitRecord_id()) {
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
						datas.addAll(newList.getDatas());
					}

					if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {// /更新数据

					}
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {
					SalerVisitSimpleInfoList newList = (SalerVisitSimpleInfoList) obj;
					datasum = size;
					if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {

						if (mDatas.size() > 0) {

							for (SalerVisitSimpleInfo medicineorder : newList
									.getmDatas()) {
								boolean tag = false;

								for (SalerVisitSimpleInfo simplepartimejobbean2 : mDatas) {
									if (medicineorder.getSaler_id() == simplepartimejobbean2
											.getSaler_id()) {
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
					if (mDatas != null) {
						mDatas.clear();
						mDatas.addAll(newList.getmDatas());
					}

					if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {// /更新数据

					}
				}
			} else if (startMethod == METHOD_MANAGER) {
				VisistRecordList newList = (VisistRecordList) obj;
				datasum = size;
				if (AppContext.user.getCounterMain_ID() == 0) {

					if (datas.size() > 0) {

						for (VisitRecord medicineorder : newList.getDatas()) {
							boolean tag = false;

							for (VisitRecord simplepartimejobbean2 : datas) {
								if (medicineorder.getVisitRecord_id() == simplepartimejobbean2
										.getVisitRecord_id()) {
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
					datas.addAll(newList.getDatas());
				}

				if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {// /更新数据

				}
			}
			break;

		case UIHelper.LISTVIEW_ACTION_SCROLL:
			// Notice list = new Notice();
			if (startMethod == 0) {

				if (AppContext.user.getCounterMain_ID() == 0) {

					VisistRecordList mlist = (VisistRecordList) obj;
					datasum += size;

					if (datas.size() > 0) {

						for (VisitRecord medicineorder : mlist.getDatas()) {
							boolean tag = false;

							for (VisitRecord simplepartimejobbean2 : datas) {
								if (medicineorder.getVisitRecord_id() == simplepartimejobbean2
										.getVisitRecord_id()) {
									tag = true;
									break;
								}
							}
							if (!tag)
								datas.add(medicineorder);
						}

					} else {

						datas.addAll(datas.size() - 1, mlist.getDatas());
					}
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {
					SalerVisitSimpleInfoList mlist = (SalerVisitSimpleInfoList) obj;
					datasum += size;

					if (mDatas.size() > 0) {

						for (SalerVisitSimpleInfo medicineorder : mlist
								.getmDatas()) {
							boolean tag = false;

							for (SalerVisitSimpleInfo simplepartimejobbean2 : mDatas) {
								if (medicineorder.getSaler_id() == simplepartimejobbean2
										.getSaler_id()) {
									tag = true;
									break;
								}
							}
							if (!tag)
								mDatas.add(medicineorder);
						}

					} else {

						mDatas.addAll(mDatas.size() - 1, mlist.getmDatas());

					}
				}
			} else if (startMethod == METHOD_MANAGER) {
				VisistRecordList mlist = (VisistRecordList) obj;
				datasum += size;

				if (datas.size() > 0) {

					for (VisitRecord medicineorder : mlist.getDatas()) {
						boolean tag = false;

						for (VisitRecord simplepartimejobbean2 : datas) {
							if (medicineorder.getVisitRecord_id() == simplepartimejobbean2
									.getVisitRecord_id()) {
								tag = true;
								break;
							}
						}
						if (!tag)
							datas.add(medicineorder);
					}

				} else {

					datas.addAll(datas.size() - 1, mlist.getDatas());
				}
			}
			break;
		}

		return notice;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getData();
		// System.out.println("ActivityTerminateVisit.onResume()");
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {

		case Config.RESULT_CODE_CHOOSE_CUSTOM:

			choosedCustom = (CustomerSimple) data
					.getSerializableExtra(AddOrderActivity.CHOOSE_CUSTOM);
			if (choosedCustom == null) {
				UIHelper.Toast(this, R.string.xzd_choose_noting);
				return;
			}
			Log.d(TAG, "选择客户{" + choosedCustom + "}");
			cus_name.setText(choosedCustom.getCustomer_Name());
			// LoadData(1, activityTerminateHandler,
			// UIHelper.LISTVIEW_ACTION_REFRESH,
			// choosedCustom.getCustomer_id());
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}

	}

	private final class TerminateVisitAdapterV2 extends BaseAdapter {

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
			tHolder.visit_date.setText(String.format(
					getResources().getString(R.string.visit_date),
					mDatas.get(position).getLastestVisitDate()));
			tHolder.hascomment.setVisibility(View.GONE);
			if (mDatas.get(position).HasComment == 1) {
				tHolder.hascomment.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		private final class ViewHolder {
			TextView salesman_name = null;
			TextView visit_date = null;
			TextView hascomment = null;

		}
	}
}
