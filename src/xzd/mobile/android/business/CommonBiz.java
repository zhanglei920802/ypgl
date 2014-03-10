package xzd.mobile.android.business;

import xzd.mobile.android.AppContext;

public class CommonBiz {

	public CommonBiz() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 判断该销售人员是否是Countermman
	 * @param appcontext
	 */
	public static void isCounerMan(final AppContext appcontext) {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				int counterman_id = -1;
				try {
					counterman_id = OrderHelper.getInstance().getCounterMan(
							AppContext.user.getSale_id());
					AppContext.user.setCounterMain_ID(counterman_id);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					AppContext.user.setCounterMain_ID(-1);

				}

			}
		}).start();
	}

}
