package xzd.mobile.android.model;

import java.io.Serializable;

public abstract class Base implements Serializable {

	private static final long serialVersionUID = 6147655444773517813L;
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "cdut";

	protected Notice notice = null;

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}

}
