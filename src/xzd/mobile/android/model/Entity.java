package xzd.mobile.android.model;

public abstract class Entity extends Base {

	private static final long serialVersionUID = 4065116298036729956L;
	protected String cacheKey = null;
	private int id;

	public int getId() {
		return this.id;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
}
