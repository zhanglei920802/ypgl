package xzd.mobile.android.thread;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.model.Config;
import android.os.Handler;
import android.os.Message;

public class CommonThread {
	private static CommonThread commonThread = null;

	private CommonThread() {
		// TODO Auto-generated constructor stub
	}

	public static CommonThread getInstance() {
		if (null == commonThread) {
			commonThread = new CommonThread();
		}
		return commonThread;
	}

	// 判定是否为Counter Man
	public void ConfirmIsCounterMan(final AppContext appcontext,
			final Handler mHandler) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage();
				int counterman_id = -1;
				try {
					counterman_id = OrderHelper.getInstance().getCounterMan(
							AppContext.user.getSale_id());
					AppContext.user.setCounterMain_ID(counterman_id);
					msg.what = Config.HANDLER_GET_MAN_ID_SUCCESS;

				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					AppContext.user.setCounterMain_ID(-1);
					msg.what = Config.HANDLER_GET_MAIN_ID_FAILED;
				}

				mHandler.sendMessage(msg);
				msg = null;
			}
		}).start();
	}

	/*
	 * 获取今日订单
	 */
	public void GetTodayOrdersCount(final AppContext appcontext,
			final Handler mHandler) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage();

				try {
					appcontext.today_orders_count = OrderHelper.getInstance()
							.GetTodayOrdersCount(AppContext.user.getM_id());
					msg.what = Config.HANDLER_GET_TODAY_COUNT_SUCCESS;

				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					AppContext.user.setCounterMain_ID(-1);
					msg.what = Config.HANDLER_GET_TODAY_COUNT_FAILED;
				}

				mHandler.sendMessage(msg);
				msg = null;
			}
		}).start();
	}

}
