package xzd.mobile.android.common;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class CyptoUtils {

	public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
	private static CyptoUtils mCyptoUtils = null;

	public static CyptoUtils getInstance() {
		if (mCyptoUtils == null) {
			mCyptoUtils = new CyptoUtils();
		}
		return mCyptoUtils;
	}

	public static void releaseMem() {
		if (mCyptoUtils != null) {
			mCyptoUtils = null;
		}

	}

	public String encode(String key, String data) {
		if (data == null)
			return null;
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字�?
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
			byte[] bytes = cipher.doFinal(data.getBytes());
			return byte2hex(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}
	}

	/**
	 * DES算法，解�?
	 * 
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8�?
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public String decode(String key, String data) {
		if (data == null)
			return null;
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字�?
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return new String(cipher.doFinal(hex2byte(data.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}
	}

	/**
	 * 
	 * 
	 * @param b
	 * @return
	 */
	private String byte2hex(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}

	private byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException();
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	/**
	 * 使用MD5 32位加密
	 * 
	 * @param str
	 *            需要加密的字符串
	 * @return 加密后的字符串
	 */
	private static String md5(String str) {

		String re_md5 = new String();

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(str.getBytes());

			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");

			for (int offset = 0; offset < b.length; offset++) {

				i = b[offset];

				if (i < 0)

					i += 256;

				if (i < 16)

					buf.append("0");

				buf.append(Integer.toHexString(i));

			}

			re_md5 = buf.toString();

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

		}

		return re_md5;

	}

	/**
	 * md5二次加密
	 * 
	 * @param str
	 * @return
	 */
	public String MD5(String str) {
		return md5(md5(str).substring(8, 24));
	}
}
