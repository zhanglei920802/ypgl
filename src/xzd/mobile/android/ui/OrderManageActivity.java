package xzd.mobile.android.ui;

import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import xzd.mobile.android.AppContext;
import xzd.mobile.android.business.CommonBiz;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.model.MedicineList;
import xzd.mobile.android.model.MedicineOrder;
import xzd.mobile.android.model.Notice;
import xzd.mobile.android.model.SalerOrderCountInfo;
import xzd.mobile.android.model.SalerOrderList;
import xzd.mobile.android.model.SalerSimple;
import xzd.mobile.android.ui.PullToRefreshListView.OnRefreshListener;
import xzd.mobile.android.ui.adapter.MedicineOrderAdapter;
import xzd.mobile.android.ui.intf.ActivityItf;

/**
 * 二〇一三年七月七日 09:21:13
 * 
 * @author ZhangLei
 * 
 */
public class OrderManageActivity extends BaseActivity implements ActivityItf,
		OnItemClickListener, OnScrollListener, OnRefreshListener,
		OnClickListener {
	public static final String TAG = "OrderManageActivity";
	public static final int HANDLER_ERROR = -1;
	public static final int HANDLER_SUCCESS = 1;

	public static final int HANDLER_GET_MAN_ID_SUCCESS = 2;
	public static final int HANDLER_GET_MAIN_ID_FAILED = 3;

	public static final int HANDLER_QUERY_ERROR = 4;
	public static final int HANDLER_QUERY_SUCCESS = 5;

	// Helper
	private UIHelper uihelper = null;
	private StringUtils stringutils = null;
	private AppContext appcontext = null;
	private OrderHelper orderhelper = null;

	// ListView
	private View listview_footer_more = null;// 加载更多View
	private ProgressBar listview_foot_progress = null;// 加载更多进度条
	private TextView listview_foot_more = null;// 加载更多文字

	private int datasum = 0;// 总的数据
	private List<MedicineOrder> datas = null;
	private List<SalerOrderCountInfo> mDatas = null;
	private MedicineOrderAdapter adpter = null;
	private MedicineOrderHandeler medicineorderhandeler = null;
	private TextView detail_title = null;
	private PullToRefreshListView order_manage_list = null;
	private TextView add = null;
	private TextView go_back = null;
	private MedicineAdapterV2 mAdapterV2 = null;
	// Loading
	private ProgressBar progressbar = null;
	private TextView text = null;
	private LinearLayout loading = null;
	private TextView btn_search = null;
	public static final String START_METHOD = "START_METHOD";
	public static final int METHOD_MANAGER = 1;
	private int startMethod = 0;
	public static final String EXTRAS_USERID = "USERID";
	private int userID = -1;
	// Search 搜索
	private EditText search_order = null;
	private int cur_type = 0;// 0表示默认,1表示业务员查询,2表示客户查询
	private TextView friendly_alert = null;//
	public static final String COUNTER_MAN_DATA = "counter_man_data";
	public static final String CUSTOM_DATA = "custom_data";
	private SalerSimple choosedSalerMan = null;
	private CustomerSimple chooseedCustomerModel = null;

	@Override
	public void getPreActivityData() {
		startMethod = getIntent().getIntExtra(START_METHOD, 0);
		if (startMethod == METHOD_MANAGER) {
			userID = getIntent().getIntExtra(EXTRAS_USERID, 0);
			if (userID < 1) {
				onDestroy();
			}
		}
	}

	@Override
	public void initView() {
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("订单管理\r\n(全部)");
		order_manage_list = (PullToRefreshListView) findViewById(R.id.order_manage_list);

		listview_footer_more = getLayoutInflater().inflate(
				R.layout.listview_foot_more, null);
		listview_foot_progress = (ProgressBar) listview_footer_more
				.findViewById(R.id.listview_foot_progress);
		listview_foot_more = (TextView) listview_footer_more
				.findViewById(R.id.listview_foot_more);

		order_manage_list.addFooterView(listview_footer_more);
		if (startMethod == 0) {
			if (AppContext.user.getCounterMain_ID() == 0) {
				order_manage_list.setAdapter(adpter);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				order_manage_list.setAdapter(mAdapterV2);
			}
		} else if (startMethod == METHOD_MANAGER) {
			if (userID < 1) {
				return;
			}
			order_manage_list.setAdapter(adpter);
		}

		order_manage_list.setOnItemClickListener(this);
		order_manage_list.setOnScrollListener(this);
		order_manage_list.setOnRefreshListener(this);

		add = (TextView) findViewById(R.id.add);
		add.setOnClickListener(this);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);

		loading = (LinearLayout) findViewById(R.id.loading);
		text = (TextView) findViewById(R.id.text);
		text.setText("正在努力加载中");
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		order_manage_list.setVisibility(View.GONE);
		btn_search = (TextView) findViewById(R.id.btn_search);
		search_order = (EditText) findViewById(R.id.search_order);
		if (startMethod == 0) {
			if (AppContext.user.getCounterMain_ID() == 0) {
				search_order.setOnClickListener(this);
				search_order.setHint("点击选择客户");
				search_order.setClickable(false);
				btn_search.setVisibility(View.GONE);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				search_order.setClickable(true);
				search_order.setHint("输入业务员进行查询");
				btn_search.setVisibility(View.VISIBLE);
				btn_search.setOnClickListener(this);
			}
		} else if (startMethod == METHOD_MANAGER) {
			search_order.setVisibility(View.GONE);
			btn_search.setVisibility(View.GONE);
			add.setVisibility(View.GONE);
		}

		friendly_alert = (TextView) findViewById(R.id.friendly_alert);
		friendly_alert.setText("今日订单" + appcontext.today_orders_count + "条");
	}

	@Override
	public void initData() {
		medicineorderhandeler = new MedicineOrderHandeler();

		if (startMethod == 0) {
			if (AppContext.user.getCounterMain_ID() == 0) {
				datas = new ArrayList<MedicineOrder>();
				adpter = new MedicineOrderAdapter(datas, this);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				mDatas = new ArrayList<SalerOrderCountInfo>();
				mAdapterV2 = new MedicineAdapterV2();

			} else if (startMethod == METHOD_MANAGER) {
				datas = new ArrayList<MedicineOrder>();
				adpter = new MedicineOrderAdapter(datas, this);
			}

		} else if (startMethod == METHOD_MANAGER) {
			if (userID < 1) {
				return;
			}
			datas = new ArrayList<MedicineOrder>();
			adpter = new MedicineOrderAdapter(datas, this);
		}
	}

	private void LoadData(final int pageIndex,
			final MedicineOrderHandeler handler, final int tag) {
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
						|| tag == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}

				if (startMethod == 0) {

					if (AppContext.user.getCounterMain_ID() == 0) {
						MedicineList datas = null;
						try {
							Log.d(TAG, "Load DataIng please wait.....");
							datas = orderhelper.GetMedicineOrders(pageIndex,
									AppContext.PAGE_SIZE,
									String.valueOf(AppContext.user.getM_id()),
									isRefresh, appcontext);
							if (datas == null) {
								throw new AppException();
							}
							msg.what = HANDLER_SUCCESS;
							msg.obj = datas;
							msg.arg1 = datas.getMedicine_list().size();
							Log.d(TAG, "Load Data successfully......");
						} catch (Exception e) {
							Log.d(TAG, "Load Data Faild......");
							e.printStackTrace();
							msg.what = HANDLER_ERROR;
							msg.obj = e;
						}

					} else if (AppContext.user.getCounterMain_ID() == 1
							|| AppContext.user.getCounterMain_ID() == 2) {
						SalerOrderList datas = null;
						try {
							Log.d(TAG, "Load DataIng please wait.....");
							datas = OrderHelper.GetSalerAndOrderCount(
									pageIndex, AppContext.PAGE_SIZE,
									AppContext.user.getM_id(), isRefresh,
									appcontext);

							if (datas == null) {
								throw new AppException();
							}
							msg.what = HANDLER_SUCCESS;
							msg.obj = datas;
							msg.arg1 = datas.getDatas().size();
							Log.d(TAG, "Load Data successfully......");
						} catch (Exception e) {
							Log.d(TAG, "Load Data Faild......");
							e.printStackTrace();
							msg.what = HANDLER_ERROR;
							msg.obj = e;
						}

					}
				} else if (startMethod == 1) {

				}
				msg.arg2 = tag;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppContext.is_login() || AppContext.user.getCounterMain_ID() == -1) {
			UIHelper.Toast(appcontext, "初始化失败");
			onDestroy();
		}
		setContentView(R.layout.activity_order_manage);

		uihelper = UIHelper.getInstance();
		stringutils = StringUtils.getInstance();
		appcontext = (AppContext) getApplication();
		orderhelper = OrderHelper.getInstance();

		getPreActivityData();
		initData();
		initView();
		getData();
		// ConfirmIsCounterMan();
	}

	// /初始化数据
	private void getData() {

		Log.d(TAG, "Init Data page=" + 1);
		if (startMethod == 0) {
			LoadData(1, medicineorderhandeler, UIHelper.LISTVIEW_ACTION_INIT);
		} else if (startMethod == METHOD_MANAGER) {
			if (userID < 1) {
				return;
			}
			LoadQueryData(1, medicineorderhandeler,
					UIHelper.LISTVIEW_ACTION_REFRESH, userID, 1);
		}

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
		case R.id.order_manage_list:
			if (position < 1) {
				return;
			}

			Bundle bundle = new Bundle();
			if (AppContext.user.getCounterMain_ID() == 0) {
				// 跳转到详细界面
				bundle.putSerializable(ActivityOrderDetail.ORDER_DATA,
						datas.get(position - 1));

				uihelper.showOrderDetailActivity(this, bundle);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				if (startMethod == 0) {
					Bundle bundle2 = new Bundle();
					bundle2.putInt(START_METHOD, METHOD_MANAGER);
					bundle2.putInt(EXTRAS_USERID, mDatas.get(position - 1)
							.getSaler_id());
					UIHelper.showOrderManage(this, bundle2);
					bundle2 = null;
				}
			}

			bundle = null;
			break;

		default:
			break;
		}
	}

	private final class MedicineOrderHandeler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_QUERY_SUCCESS:

			case HANDLER_SUCCESS:
				Notice notice = HandleData(msg.obj, msg.arg1, msg.arg2);

				if (msg.arg1 < AppContext.PAGE_SIZE) {// 说明数据已经加载完毕
					order_manage_list.setTag(UIHelper.LISTVIEW_DATA_FULL);

					if (startMethod == 0) {
						if (AppContext.user.getCounterMain_ID() == 0) {
							adpter.notifyDataSetChanged();
						} else if (AppContext.user.getCounterMain_ID() == 1
								|| AppContext.user.getCounterMain_ID() == 2) {
							mAdapterV2.notifyDataSetChanged();
						}
					} else if (startMethod == METHOD_MANAGER) {
						if (userID < 1) {
							return;
						}
						adpter.notifyDataSetChanged();
					}

					listview_foot_more.setText(getString(R.string.load_full));
				} else if (msg.arg1 == AppContext.PAGE_SIZE) {
					order_manage_list.setTag(UIHelper.LISTVIEW_DATA_MORE);
					if (startMethod == 0) {
						if (AppContext.user.getCounterMain_ID() == 0) {
							adpter.notifyDataSetChanged();
						} else if (AppContext.user.getCounterMain_ID() == 1
								|| AppContext.user.getCounterMain_ID() == 2) {
							mAdapterV2.notifyDataSetChanged();
						}
					} else if (startMethod == METHOD_MANAGER) {
						if (userID < 1) {
							return;
						}
						adpter.notifyDataSetChanged();
					}
					listview_foot_more.setText(getString(R.string.load_more));
				}
				UpdateView();
				if (startMethod == 0) {
					if (AppContext.user.getCounterMain_ID() == 0) {
						if (adpter.getCount() < 1) {
							order_manage_list
									.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
							listview_foot_more
									.setText(getString(R.string.load_empty));
							order_manage_list.setVisibility(View.GONE);
							loading.setVisibility(View.VISIBLE);
							progressbar.setVisibility(View.GONE);
							text.setVisibility(View.VISIBLE);
							text.setText("没有数据");
						}

					} else if (AppContext.user.getCounterMain_ID() == 1
							|| AppContext.user.getCounterMain_ID() == 2) {
						if (mAdapterV2.getCount() < 1) {
							order_manage_list
									.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
							listview_foot_more
									.setText(getString(R.string.load_empty));
							order_manage_list.setVisibility(View.GONE);
							loading.setVisibility(View.VISIBLE);
							progressbar.setVisibility(View.GONE);
							text.setVisibility(View.VISIBLE);
							text.setText("没有数据");
						}

					}
				} else if (startMethod == METHOD_MANAGER) {
					if (userID < 1) {
						return;
					}
					if (adpter.getCount() < 1) {
						order_manage_list.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
						listview_foot_more
								.setText(getString(R.string.load_empty));
						order_manage_list.setVisibility(View.GONE);
						loading.setVisibility(View.VISIBLE);
						progressbar.setVisibility(View.GONE);
						text.setVisibility(View.VISIBLE);
						text.setText("没有数据");
					}
				}

				listview_foot_progress.setVisibility(View.GONE);

				if (msg.arg2 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					order_manage_list
							.onRefreshComplete(getString(R.string.pull_to_refresh_update)
									+ new Date().toLocaleString());
					order_manage_list.setSelection(0);
				}
				break;

			case HANDLER_ERROR:
				order_manage_list.setTag(UIHelper.LISTVIEW_DATA_MORE);
				listview_foot_more.setText(R.string.loading_error);
				UIHelper.Toast(OrderManageActivity.this,
						R.string.http_exception_error);
				break;

			}
		}

	}

	protected void UpdateView() {
		loading.setVisibility(View.GONE);
		order_manage_list.setVisibility(View.VISIBLE);

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Load data reresh:page=1");
		if (startMethod == 0) {
			if (cur_type == 0) {
				LoadData(1, medicineorderhandeler,
						UIHelper.LISTVIEW_ACTION_REFRESH);
			} else if (cur_type == 2) {
				LoadQueryData(1, medicineorderhandeler,
						UIHelper.LISTVIEW_ACTION_REFRESH,
						chooseedCustomerModel.getCustomer_id(), 2);
			} else if (cur_type == 1) {
				LoadQueryData(1, medicineorderhandeler,
						UIHelper.LISTVIEW_ACTION_REFRESH,
						choosedSalerMan.getSaler_id(), 1);
			}
		} else if (startMethod == METHOD_MANAGER) {
			if (userID < 1) {
				return;
			}
			LoadQueryData(1, medicineorderhandeler,
					UIHelper.LISTVIEW_ACTION_REFRESH, userID, 1);
		}

	}

	public Notice HandleData(final Object obj, final int size, final int type) {
		if (startMethod == 0) {

			if (AppContext.user.getCounterMain_ID() == 0) {
				Notice notice = null;
				switch (type) {
				case UIHelper.LISTVIEW_ACTION_INIT:
				case UIHelper.LISTVIEW_ACTION_REFRESH:
					// Notice notice = new Notice();
					int newData = 0;// 新数据

					MedicineList newList = (MedicineList) obj;
					datasum = size;
					if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {

						if (datas.size() > 0) {

							for (MedicineOrder medicineorder : newList
									.getMedicine_list()) {
								boolean tag = false;

								for (MedicineOrder simplepartimejobbean2 : datas) {
									if (medicineorder.getOrder_id() == simplepartimejobbean2
											.getOrder_id()) {
										tag = true;
										break;
									}
								}
								if (!tag)
									newData++;
							}
							// datas.addAll(0, newList.getMedicine_list());
						} else {

							newData = size;
						}

					}
					datas.clear();
					datas.addAll(newList.getMedicine_list());
					if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {// /更新数据

					}
					break;

				case UIHelper.LISTVIEW_ACTION_SCROLL:
					// Notice list = new Notice();

					MedicineList mlist = (MedicineList) obj;
					datasum += size;

					if (datas.size() > 0) {

						for (MedicineOrder medicineorder : mlist
								.getMedicine_list()) {
							boolean tag = false;

							for (MedicineOrder simplepartimejobbean2 : datas) {
								if (medicineorder.getOrder_id() == simplepartimejobbean2
										.getOrder_id()) {
									tag = true;
									break;
								}
							}
							if (!tag)
								datas.add(medicineorder);
						}

					} else {

						datas.addAll(datas.size() - 1, mlist.getMedicine_list());
					}

					break;
				}

				return notice;
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				Notice notice = null;
				switch (type) {
				case UIHelper.LISTVIEW_ACTION_INIT:
				case UIHelper.LISTVIEW_ACTION_REFRESH:
					// Notice notice = new Notice();
					int newData = 0;// 新数据

					SalerOrderList newList = (SalerOrderList) obj;
					datasum = size;
					if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {

						if (mDatas.size() > 0) {

							for (SalerOrderCountInfo medicineorder : newList
									.getDatas()) {
								boolean tag = false;

								for (SalerOrderCountInfo simplepartimejobbean2 : mDatas) {
									if (medicineorder.getSaler_id() == simplepartimejobbean2
											.getSaler_id()) {
										tag = true;
										break;
									}
								}
								if (!tag)
									newData++;
							}
							// datas.addAll(0, newList.getMedicine_list());
						} else {

							newData = size;
						}

					}
					mDatas.clear();
					mDatas.addAll(newList.getDatas());
					if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {// /更新数据

					}
					break;

				case UIHelper.LISTVIEW_ACTION_SCROLL:
					// Notice list = new Notice();

					SalerOrderList mlist = (SalerOrderList) obj;
					datasum += size;

					if (mDatas.size() > 0) {

						for (SalerOrderCountInfo medicineorder : mlist
								.getDatas()) {
							boolean tag = false;

							for (SalerOrderCountInfo simplepartimejobbean2 : mDatas) {
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

						mDatas.addAll(mDatas.size() - 1, mlist.getDatas());
					}

					break;
				}

				return notice;
			}
		} else if (startMethod == METHOD_MANAGER) {
			if (userID < 1) {
				return null;
			}
			Notice notice = null;
			switch (type) {
			case UIHelper.LISTVIEW_ACTION_INIT:
			case UIHelper.LISTVIEW_ACTION_REFRESH:
				// Notice notice = new Notice();
				int newData = 0;// 新数据

				MedicineList newList = (MedicineList) obj;
				datasum = size;
				if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {

					if (datas.size() > 0) {

						for (MedicineOrder medicineorder : newList
								.getMedicine_list()) {
							boolean tag = false;

							for (MedicineOrder simplepartimejobbean2 : datas) {
								if (medicineorder.getOrder_id() == simplepartimejobbean2
										.getOrder_id()) {
									tag = true;
									break;
								}
							}
							if (!tag)
								newData++;
						}
						// datas.addAll(0, newList.getMedicine_list());
					} else {

						newData = size;
					}

				}
				datas.clear();
				datas.addAll(newList.getMedicine_list());
				if (type == UIHelper.LISTVIEW_ACTION_REFRESH) {// /更新数据

				}
				break;

			case UIHelper.LISTVIEW_ACTION_SCROLL:
				// Notice list = new Notice();

				MedicineList mlist = (MedicineList) obj;
				datasum += size;

				if (datas.size() > 0) {

					for (MedicineOrder medicineorder : mlist.getMedicine_list()) {
						boolean tag = false;

						for (MedicineOrder simplepartimejobbean2 : datas) {
							if (medicineorder.getOrder_id() == simplepartimejobbean2
									.getOrder_id()) {
								tag = true;
								break;
							}
						}
						if (!tag)
							datas.add(medicineorder);
					}

				} else {

					datas.addAll(datas.size() - 1, mlist.getMedicine_list());
				}

				break;
			}

			return notice;
		}
		return null;

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		order_manage_list.onScrollStateChanged(view, scrollState);

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
			if (userID < 1) {
				return;
			}
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

		int lvDataState = stringutils.toInt(order_manage_list.getTag());

		if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {

			order_manage_list.setTag(UIHelper.LISTVIEW_DATA_LOADING);
			listview_foot_more.setText(R.string.loading);
			listview_foot_progress.setVisibility(View.VISIBLE);
			// 当前pageIndex
			int pageIndex = 1 + (datasum / AppContext.PAGE_SIZE);// 计算pagesize
			Log.d(TAG, "load data scroll,page=" + pageIndex + ".....");
			if (startMethod == 0) {
				if (cur_type == 0) {
					LoadData(pageIndex, medicineorderhandeler,
							UIHelper.LISTVIEW_ACTION_SCROLL);
				} else if (cur_type == 2) {
					LoadQueryData(pageIndex, medicineorderhandeler,
							UIHelper.LISTVIEW_ACTION_SCROLL,
							chooseedCustomerModel.getCustomer_id(), 2);
				} else if (cur_type == 1) {
					LoadQueryData(pageIndex, medicineorderhandeler,
							UIHelper.LISTVIEW_ACTION_SCROLL,
							choosedSalerMan.getSaler_id(), 1);
				}
			} else if (startMethod == METHOD_MANAGER) {
				if (userID < 1) {
					return;
				}
				LoadQueryData(pageIndex, medicineorderhandeler,
						UIHelper.LISTVIEW_ACTION_SCROLL, userID, 1);
			}

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		order_manage_list.onScroll(view, firstVisibleItem, visibleItemCount,
				totalItemCount);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add:
			if (AppContext.is_login()) {
				Bundle bundle = new Bundle();
				if (AppContext.user.getCounterMain_ID() == 0) {
					bundle.putInt(AddOrderActivity.ACTION_TYPE,
							AddOrderActivity.ACTION_ADD);
					uihelper.showOrderActivity(this, bundle);
					bundle = null;
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {

				}

			} else {
				UIHelper.Toast(this, "对不起,您还未进行登录");
			}

			break;
		case R.id.btn_search:
			if (AppContext.user.getCounterMain_ID() == 0
					|| !appcontext.isNetworkConnected() || startMethod == 1) {
				return;
			}
			if (TextUtils.isEmpty(search_order.getText().toString())) {
				return;
			}
			QuerySalerAndOrderCount(AppContext.user.getM_id(), search_order
					.getText().toString(), medicineorderhandeler,
					UIHelper.LISTVIEW_ACTION_REFRESH);
			break;
		case R.id.go_back:
			onDestroy();
			break;
		case R.id.search_order:
			if (AppContext.is_login()) {
				Intent intent = null;
				if (AppContext.user.getCounterMain_ID() == 0) {// 按照客户查询
					intent = new Intent(this, CustomChooseActivity.class);
					intent.putExtra(CustomChooseActivity.CUSTOMCHOOSEDATA,
							AppContext.user.getM_id());
					startActivityForResult(intent,
							Config.REQUEST_CODE_CHOOSE_CUSTOM);
				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {// 按照业务员查询
					intent = new Intent(this, ActivityCounterManChoose.class);
					intent.putExtra(ActivityCounterManChoose.COUNTERMANDATA,
							AppContext.user.getSale_id());

					startActivityForResult(intent,
							Config.REQUEST_CODE_CHOOSE_CONTERMAN);
				}
			} else {
				Log.d(TAG, "对不起,您还未登录");
			}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case Config.RESULT_CODE_CHOOSE_COUNTMAN:
			choosedSalerMan = (SalerSimple) data
					.getSerializableExtra(COUNTER_MAN_DATA);
			Log.d(TAG, "Obtain Data:" + choosedSalerMan.toString());
			detail_title.setText("订单管理\r\n(" + choosedSalerMan.getSaler_name()
					+ ")");
			LoadQueryData(1, medicineorderhandeler,
					UIHelper.LISTVIEW_ACTION_INIT,
					choosedSalerMan.getSaler_id(), 1);
			cur_type = 1;
			break;

		case Config.RESULT_CODE_CHOOSE_CUSTOM:
			if (null == data) {
				return;
			}
			chooseedCustomerModel = (CustomerSimple) data
					.getSerializableExtra(AddOrderActivity.CHOOSE_CUSTOM);
			if (null == chooseedCustomerModel) {
				return;
			}
			Log.d(TAG, "Obtain Data:" + chooseedCustomerModel.toString());
			cur_type = 2;

			detail_title.setText("订单管理\r\n("
					+ chooseedCustomerModel.getCustomer_Name() + ")");
			LoadQueryData(1, medicineorderhandeler,
					UIHelper.LISTVIEW_ACTION_INIT,
					chooseedCustomerModel.getCustomer_id(), 2);

			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	};

	private void LoadQueryData(final int pageIndex,
			final MedicineOrderHandeler handler, final int tag, final int id,
			final int type) {
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
						|| tag == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}
				if (startMethod == 0) {
					if (AppContext.user.getCounterMain_ID() == 0) {
						MedicineList datas = null;
						try {
							Log.d(TAG, "Load DataIng please wait.....");
							datas = orderhelper.QueryMedicineOrder(pageIndex,
									AppContext.PAGE_SIZE, id, type, appcontext,
									isRefresh);
							if (datas == null) {
								throw new AppException();
							}
							Log.d(TAG, datas.getMedicine_list().toString());
							msg.what = HANDLER_QUERY_SUCCESS;
							msg.obj = datas;
							msg.arg1 = datas.getMedicine_list().size();
							Log.d(TAG, "Load Data successfully......");
						} catch (Exception e) {
							Log.d(TAG, "Load Data Faild......");
							e.printStackTrace();
							msg.what = HANDLER_ERROR;
							msg.obj = e;
						}
					} else if (AppContext.user.getCounterMain_ID() == 1
							|| AppContext.user.getCounterMain_ID() == 2) {

					}
				} else if (startMethod == METHOD_MANAGER) {
					if (userID < 1) {
						return;
					}
					MedicineList datas = null;
					try {
						Log.d(TAG, "Load DataIng please wait.....");
						datas = orderhelper.QueryMedicineOrder(pageIndex,
								AppContext.PAGE_SIZE, id, type, appcontext,
								isRefresh);
						if (datas == null) {
							throw new AppException();
						}
						Log.d(TAG, datas.getMedicine_list().toString());
						msg.what = HANDLER_QUERY_SUCCESS;
						msg.obj = datas;
						msg.arg1 = datas.getMedicine_list().size();
						Log.d(TAG, "Load Data successfully......");
					} catch (Exception e) {
						Log.d(TAG, "Load Data Faild......");
						e.printStackTrace();
						msg.what = HANDLER_ERROR;
						msg.obj = e;
					}
				}

				msg.arg2 = tag;
				handler.sendMessage(msg);
			}
		}).start();
	}

	private void QuerySalerAndOrderCount(final int userid, final String name,
			final MedicineOrderHandeler handler, final int tag) {
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
						|| tag == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}

				if (AppContext.user.getCounterMain_ID() == 0) {

				} else if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {
					SalerOrderList datas = null;
					try {
						Log.d(TAG, "Load DataIng please wait.....");
						datas = OrderHelper.QuerySalerAndOrderCount(userid,
								name, isRefresh, appcontext);

						if (datas == null) {
							throw new AppException();
						}
						msg.what = HANDLER_SUCCESS;
						msg.obj = datas;
						msg.arg1 = datas.getDatas().size();
						Log.d(TAG, "Load Data successfully......");
					} catch (Exception e) {
						Log.d(TAG, "Load Data Faild......");
						e.printStackTrace();
						msg.what = HANDLER_ERROR;
						msg.obj = e;
					}
				}

				msg.arg2 = tag;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// counterman
		if (appcontext.isNetworkConnected()) {
			if (AppContext.user != null
					&& TextUtils.isEmpty(AppContext.user.getM_mgr_logname())) {
				if (AppContext.user.getCounterMain_ID() == -1) {
					CommonBiz.isCounerMan(appcontext);
					Log.d(TAG, "get counerman id...");
				}
			}
		}
		super.onResume();
	}

	private final class MedicineAdapterV2 extends BaseAdapter {

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
			ViewHolder holder;

			if (null == convertView) {
				convertView = getLayoutInflater().inflate(
						R.layout.order_list_item_v3, null);
				holder = new ViewHolder();
				holder.orders_count = (TextView) convertView
						.findViewById(R.id.orders_count);
				holder.salers_name = (TextView) convertView
						.findViewById(R.id.salers_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.salers_name.setText("业务员:"
					+ mDatas.get(position).getSaler_name());
			holder.orders_count.setText("订单数:"
					+ mDatas.get(position).getOrderCount());
			return convertView;
		}

		private final class ViewHolder {
			TextView salers_name = null;
			TextView orders_count = null;

		}
	}
}
