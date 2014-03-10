package xzd.mobile.android.ui;

import xzd.mobile.android.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EditMedicineNumber extends BaseActivity implements OnClickListener {

	private TextView detail_title;
	private TextView go_back;
	private View confrim;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("输入数量");
		go_back = (TextView) findViewById(R.id.go_back);

		go_back.setOnClickListener(this);
		confrim = findViewById(R.id.confrim);
		confrim.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xzd_editmedicine_number);
		getPreActivityData();
		initData();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		setResult(RESULT_CANCELED);
		onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_back:
			setResult(RESULT_CANCELED);
			onDestroy();
			break;

		default:
			break;
		}
	}
}
