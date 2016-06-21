/**
 * Copyright ® 2012 Eastcom Co. Ltd.
 * All right reserved.
 */

package com.db.iss.trade.api.util;

/**
 * 
 * @author shif
 *
 */
public class HexStrUtil {

	/**
	 * Long转换成Hex字符串</br>
	 * len 转换后Hex字符串的长度 不足 Ascii左补 30  二进制补 0</br>
	 * code 0 - Ascii编码    1 - 二进制编码</br>
	 * exp：longToHexStr(123,4,0) -> 30313233</br>
	 * 	    longToHexStr(123,4,1) -> 0000007b</br>
	 */
	public static String longToHexStr(long src, int len, int code){
		String HexStr = null;
		if(code == 0){
			HexStr = asciiToHexStr(String.format("%d", src));
			int llen = len - HexStr.length();
			HexStr = fillString(HexStr, 0, llen, (byte)30);
		}else if (code == 1) {
			byte[] tmp = new byte[16];
			StringBuilder sBuilder = new StringBuilder();
			for(int i=0; i<16; i++){
				tmp[i] = (byte) (src >> 4*(16-i-1) & 0xf);
				sBuilder.append(String.format("%X", tmp[i]));
			}
			HexStr = sBuilder.toString();
			int llen = len - HexStr.length();
			HexStr = fillString(HexStr, 0, llen, (byte)0);
		}else {
			throw new RuntimeException("code type is not support");
		}
		return HexStr.substring(0,len);
	}
	
	/**
	 * Int转换成Hex字符串</br>
	 * len 转换后Hex字符串的长度 不足 Ascii左补 30  二进制补 0</br>
	 * code 0 - Ascii编码    1 - 二进制编码</br>
	 * exp：intToHexStr(123,4,0) -> 30313233</br>
	 * 	    intToHexStr(123,4,1) -> 0000007b</br>
	 */
	public static String intToHexStr(int src, int len, int code){
		return longToHexStr(src, len, code);
	}
	
	/**
	 * Short转换成Hex字符串</br>
	 * len 转换后Hex字符串的长度 不足 Ascii左补 30  二进制补 0</br>
	 * code 0 - Ascii编码    1 - 二进制编码</br>
	 * exp：shortToHexStr(123,4,0) -> 30313233</br>
	 * 	    shortToHexStr(123,4,1) -> 0000007b</br>
	 */
	public static String shortToHexStr(short src, int len, int code){
		return longToHexStr(src, len, code);
	}
	
	/**
	 * Byte转换成Hex字符串</br>
	 * len 转换后Hex字符串的长度 不足 Ascii左补 30  二进制补 0</br>
	 * code 0 - Ascii编码    1 - 二进制编码</br>
	 * exp：byteToHexStr(123,4,0) -> 30313233</br>
	 * 	    byteToHexStr(123,4,1) -> 0000007b</br>
	 */
	public static String  byteToHexStr(byte src, int len, int code){
		return longToHexStr(src, len, code);
	}
	
	/**
	 * Long转换成Hex字符串</br>
	 * len 转换后Hex字符串的长度 不足 Ascii左补 30  二进制补 0</br>
	 * code 0 - Ascii编码    1 - 二进制编码</br>
	 * exp：longToHexStr(123,4,0) -> 30313233</br>
	 * 	    longToHexStr(123,4,1) -> 0000007b</br>
	 */
	public static Long HexStrToLong(String src, int code){
		long des = 0;
		if(code == 0){
			
		}else if(code == 1){
			
		}else {
			throw new RuntimeException("code type is not support");
		}
		return des;
	}
	/**
	 * Ascii字符串转换成Hex字符串</br> 
	 */
	public static String asciiToHexStr(String src){
		StringBuilder sBuilder = new StringBuilder();
		for(int i = 0; i < src.length(); i++)
			sBuilder.append(String.format("%02X", Integer.valueOf(src.charAt(i))));
		return sBuilder.toString();
	}
	
	/**
	 *  字符串补位</br>
	 *  src 源字符串</br>
	 *  direct 0 - 左补    1 - 右补</br>
	 *  len 填充位数</br>
	 *  fill  填充字节</br>
	 */
	private static String fillString(String src, int direct, int len, byte fill){
		if(len <= 0)
			return src;
		for(int i=0; i<len; i += String.valueOf(fill).length()){
			//left
			if(direct == 0){
				src = String.valueOf(fill) + src;
			}
			//right
			else {
				src += String.valueOf(fill);
			}
		}
		return src;
	}
	
	
	/**
	 * test
	 */
	public static void main(String[] args) {
		System.out.println(longToHexStr(Long.MAX_VALUE, 16, 1));
		
		System.out.println(intToHexStr(-1, 16, 0));
		
		System.out.println(shortToHexStr(Short.MAX_VALUE, 16, 1));
		
		System.out.println(byteToHexStr((byte) 1, 16, 1));
	}
	
}

