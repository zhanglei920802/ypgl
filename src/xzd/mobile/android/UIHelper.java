package xzd.mobile.android;

import xzd.mobile.android.R;
import xzd.mobile.android.ui.ActivityAddOrEditConsumer;
import xzd.mobile.android.ui.ActivityAddToLib;
import xzd.mobile.android.ui.ActivityCustomerConsume;
import xzd.mobile.android.ui.ActivityOrderDetail;
import xzd.mobile.android.ui.ActivityTerminateVisit;
import xzd.mobile.android.ui.ActivityVisitAdd;
import xzd.mobile.android.ui.ActivityVisitDetail;
import xzd.mobile.android.ui.AddGroup;
import xzd.mobile.android.ui.AddOrderActivity;
import xzd.mobile.android.ui.AddRemark;
import xzd.mobile.android.ui.ConsDetail;
import xzd.mobile.android.ui.GroupManager;
import xzd.mobile.android.ui.LinkMan;
import xzd.mobile.android.ui.LocationActivity;
import xzd.mobile.android.ui.LoginActivity;
import xzd.mobile.android.ui.MainActivity;
import xzd.mobile.android.ui.MgrLinkMan;
import xzd.mobile.android.ui.OrderManageActivity;
import xzd.mobile.android.ui.TerminateDetailActivity;
import xzd.mobile.android.ui.TerminateManageActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class UIHelper {
	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	private static UIHelper mUIHelper = null;

	public static UIHelper getInstance() {
		if (mUIHelper == null) {
			mUIHelper = new UIHelper();
		}
		return mUIHelper;
	}

	public static void releaseMem() {
		if (mUIHelper != null) {
			mUIHelper = null;
		}

	}

	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("程序出现出现异常");
		builder.setMessage("很抱歉，应用程序出现错误，即将退出。\n请提交错误报告，我们会尽快修复这个问题！");
		builder.setPositiveButton("提交报告",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "794857063@qq.com" });
						i.putExtra(Intent.EXTRA_SUBJECT, "鹰管家- 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getInstance().AppExit(cont);
					}
				});
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 退出
				AppManager.getInstance().AppExit(cont);
			}
		});
		builder.show();
	}

	public static void Toast(Context context, int resource_id) {
		Toast.makeText(context, resource_id, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 * @param context
	 * @param msg
	 */
	public static void Toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public void ShowMainActivity(Activity activity, Bundle data) {
		Intent intent = new Intent(activity, MainActivity.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
		if (activity instanceof LoginActivity) {
			activity.finish();
		}
	}

	public void ShowTerminateMangeActivity(Activity activity) {
		Intent intent = new Intent(activity, TerminateManageActivity.class);

		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public void ShowTerminateVisitActivity(Activity activity) {
		Intent intent = new Intent(activity, ActivityTerminateVisit.class);

		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public void ShowTerminateVisitActivity(Activity activity, Bundle pBundle) {
		Intent intent = new Intent(activity, ActivityTerminateVisit.class);
		intent.putExtras(pBundle);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public void ShowCustomerConsumeActivity(Activity activity) {
		Intent intent = new Intent(activity, ActivityCustomerConsume.class);

		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public void showContact(Activity activity) {
		Intent intent = new Intent(activity, LinkMan.class);

		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public static void showOrderManage(Activity activity, Bundle bundle) {
		Intent intent = new Intent(activity, OrderManageActivity.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public static void showGroupManager(Activity activity, Bundle bundle) {
		Intent intent = new Intent(activity, GroupManager.class);
		if(bundle!=null){
			intent.putExtras(bundle);
		}
	
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public void showTerminateDetailActivity(Activity activity, Bundle data) {
		Intent intent = new Intent(activity, TerminateDetailActivity.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public void showLinkManDetailActivity(Activity activity, Bundle data) {
		Intent intent = new Intent(activity, MgrLinkMan.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public static void showConsDetail(Activity activity, Bundle data) {
		Intent intent = new Intent(activity, ConsDetail.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public static void showAddRemark(Activity activity, Bundle data) {
		Intent intent = new Intent(activity, AddRemark.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public void showOrderDetailActivity(Activity activity, Bundle data) {
		Intent intent = new Intent(activity, ActivityOrderDetail.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}

	public void showOrderActivity(Activity activity, Bundle bundle) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, AddOrderActivity.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void showVisitAddActivity(Activity activity, Bundle data) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, ActivityVisitAdd.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void showEditOrAddCustomActivity(Activity activity, Bundle data) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, ActivityAddOrEditConsumer.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void showLoginActivty(Activity activity, Bundle data) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, LoginActivity.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void showMedicineChooseActivity(Activity activity) {

	}

	public void ShowAddToLibUI(Activity activity, Bundle data) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, ActivityAddToLib.class);
		intent.putExtras(data);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void ShowAddGroup(Activity activity,Bundle pBundle) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, AddGroup.class);
		if(pBundle!=null){
			intent.putExtras(pBundle);
		}
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void showVisitDetail(Activity activity, Bundle bundle) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, ActivityVisitDetail.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public void showLocationActivity(MainActivity activity) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, LocationActivity.class);

		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);

	}
}
