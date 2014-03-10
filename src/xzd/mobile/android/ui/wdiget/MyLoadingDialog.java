package xzd.mobile.android.ui.wdiget;

import xzd.mobile.android.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MyLoadingDialog extends Dialog {

	private int msg = 0;

	public MyLoadingDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public MyLoadingDialog(Context context, int theme) {
		super(context, theme);
		// this.msg = resID;
		// TODO Auto-generated constructor stub
	}

	public MyLoadingDialog(int resID, Context context) {
		super(context);
		this.msg = resID;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		((TextView) findViewById(R.id.loading_textview)).setText(msg);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.apptitle);

	}

}
