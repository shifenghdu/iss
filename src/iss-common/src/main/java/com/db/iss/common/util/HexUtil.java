package com.db.iss.common.util;

import java.nio.ByteBuffer;

/**
 * Hex Convert util
 * 
 * @author Andy Shek
 * 
 */
public class HexUtil {

	/**
	 * Ascii字节数组转换成Bcd字节数组 src des字节数组 len 转换后bcd字节数组的长度 不足右补 0
	 * 
	 * @param src
	 * @param len
	 * @param padding
	 * @return
	 */
	public static byte[] asciiToHex(byte[] src, int len, int padding) {
		byte[] bcd = new byte[len];
		byte[] asc = null;
		if (padding == 0) {
			asc = lpBytes(src, len * 2, (byte) 0x30);
		} else {
			asc = rpBytes(src, len * 2, (byte) 0x30);
		}
		for (int i = 0; i < len; i++) {
			bcd[i] = (byte) ((byte) byteToBcd(asc[i * 2]) << 4 ^ (byteToBcd(asc[i * 2 + 1]) & 0x0f));
		}
		return bcd;
	}

	/**
	 * Ascii字节数组转换成Bcd字节数组</br> src 源字节数组</br> len 转换后bcd字节数组的长度 不足右补 0</br>
	 */
	public static byte[] hexToAscii(byte[] src) {
		byte[] asc = new byte[src.length * 2];
		asc = rpBytes(src, src.length * 2, (byte) 0x00);
		for (int i = 0; i < src.length; i++) {
			asc[i * 2] = bcdToByte((byte) (src[i] >> 4 & 0x0f));
			asc[i * 2 + 1] = bcdToByte((byte) (src[i] & 0x0f));
		}
		return asc;
	}

	/**
	 * Ascii字节数组转换成Bcd字节数组
	 * 
	 * @param src
	 * @param len
	 * @return
	 */
	public static byte[] hexToAscii(ByteBuffer src, int len) {
		byte[] tmp = new byte[len];
		System.arraycopy(src.array(), 0, tmp, 0, src.limit());
		return hexToAscii(tmp);
	}

	/**
	 * Long转换成Bcd字节数组</br> src 源数字</br> len 转换后的字节数组长度 不足右补 0</br> flag
	 * 0--转换成16进制 1--转换成10进制
	 */
	public static byte[] longToBcd(long src, int len, int flag) {
		byte[] re = new byte[len];
		long tmp, high, low;
		if (src < 0)
			throw new RuntimeException(String.format(
					"number: [%d] convert bcd error", src));

		for (int i = len - 1; i >= 0; i--) {
			if (src == 0)
				break;
			if (flag == 1) {
				tmp = src % 100;
				src /= 100;
				high = tmp / 10;
				low = tmp % 10;
			} else {
				tmp = src % 256;
				src /= 256;
				high = tmp / 16;
				low = tmp % 16;

			}
			re[i] = (byte) (high << 4 ^ low);

		}
		return re;
	}

	/**
	 * Int转换成Bcd字节数组</br> src 源数字</br> len 转换后的字节数组长度 不足右补 0</br>
	 */
	public static byte[] intToBcd(int src, int len, int flag) {
		return longToBcd(src, len, flag);
	}

	/**
	 * Short转换成Bcd字节数组</br> src 源数字</br> len 转换后的字节数组长度 不足右补 0</br>
	 */
	public static byte[] shortToBcd(short src, int len, int flag) {
		return longToBcd(src, len, flag);
	}

	/**
	 * Bcd数组转换成Long</br> src 源字节数组</br> flag 0--转换成16进制 1--转换成10进制
	 */
	public static long bcdToLong(byte[] src, int flag) {
		byte high, low;
		long re = 0;

		if (flag == 0) {
			for (int i = 0; i < src.length; i++) {
				re *= 256;
				high = (byte) (src[i] >> 4 & 0x0f);
				low = (byte) (src[i] & 0x0f);
				re += Long.valueOf(high) * 16 + Long.valueOf(low);
			}
		} else {
			for (int i = 0; i < src.length; i++) {
				re *= 100;
				high = (byte) (src[i] >> 4 & 0x0f);
				low = (byte) (src[i] & 0x0f);
				re += Long.valueOf(high) * 10 + Long.valueOf(low);
			}
		}
		return re;
	}

	/**
	 * Bcd数组转换成Int</br> src 源字节数组</br>
	 */
	public static int bcdToInt(byte[] src, int flag) {
		return (int) bcdToLong(src, flag);
	}

	/**
	 * Bcd数组转换成Short</br> src 源字节数组</br>
	 */
	public static short bcdToShort(byte[] src, int flag) {
		return (short) bcdToLong(src, flag);
	}

	/**
	 * Ascii字节转换成Bcd</br>
	 */
	private static byte byteToBcd(byte src) {
		byte re = src;
		if (src <= 0x39 && src >= 0x30)
			re = (byte) (src - 0x30);
		else if (src <= 0x46 && src >= 0x41)
			re = (byte) (src - 0x37);
		else if (src <= 0x66 && src >= 0x61)
			re = (byte) (src - 0x57);
		return re;
	}

	/**
	 * Bcd字节转换成Ascii</br>
	 */
	private static byte bcdToByte(byte src) {
		byte re = src;
		if (src <= 0x09 && src >= 0x00)
			re = (byte) (src + 0x30);
		else if (src <= 0x0f && src >= 0x0a)
			re = (byte) (src + 0x37);
		return re;
	}

	/**
	 * 尾部填充字节数组</br> len 长度</br> fill 填充字节</br>
	 */
	private static byte[] rpBytes(byte[] src, int len, byte fill) {
		byte[] des = new byte[len];
		int llen = src.length > len ? len : src.length;
		// int rlen = src.length < len ? len : src.length;
		for (int i = 0; i < llen; i++) {
			if (i < llen)
				des[i] = src[i];
			else
				des[i] = fill;
		}
		return des;
	}

	private static byte[] lpBytes(byte[] src, int len, byte fill) {
		byte[] des = new byte[len];
		int llen = src.length > len ? len : src.length;
		int rlen = src.length < len ? len : src.length;
		for (int i = 0; i < len; i++) {
			if (i >= (rlen - llen))
				des[i] = src[i - rlen + llen];
			else
				des[i] = fill;
		}
		return des;
	}

	/**
	 * 十六进制格式打印Bcd字符数组</br>
	 */
	public static String dump(byte[] bcd) {
		StringBuilder sbBuilder = new StringBuilder();
		int pre = 0;

		for (int i = 0; i < (bcd.length + 15) / 16; i++) {
			pre = i * 16;
			for (int j = pre; j < pre + 16; j++) {
				if (j >= bcd.length) {
					sbBuilder.append(String.format("   "));
					continue;
				}
				sbBuilder.append(String.format("%02X ", bcd[j]));
			}
			sbBuilder.append("     |      ");
			for (int j = pre; j < pre + 16; j++) {
				if (j >= bcd.length)
					break;
				// 0x20 - 0x7e 可打印
				if (bcd[j] >= 0x20) {
					sbBuilder.append(String.format("%c", bcd[j] & 0x000000ff));
				} else {
					sbBuilder.append("*");
				}
			}
			sbBuilder.append("\n");
		}

		return sbBuilder.toString();
	}

	/**
	 * test
	 */
	public static void main(String[] args) {

		 System.out.print(dump(intToBcd(123, 4,0)));
		// System.out.print(dump(intToBcd(474747, 16)));
		// System.out.print(dump(longToBcd(123, 4)));
		//

		System.out.print((dump(asciiToHex("22222222222222223333333333333333".getBytes(), 16, 0))));
//		System.out.print((dump(longToBcd(126, 4, 1))));
//		System.out.print((bcdToLong(longToBcd(126, 4, 0), 1)));
		//
		// System.out.print(dump(bcdToAscii(asciiToBcd("abc1234".getBytes(),
		// 16))));
		// System.out.println(bcdToLong(longToBcd(123, 16)));
		// System.out.println(bcdToLong(longToBcd(Long.MAX_VALUE, 16)));

	}
}
