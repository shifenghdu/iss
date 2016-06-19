//file:ByteUtil.java date:2013-6-26 //
package com.db.iss.common.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * ByteBuffer Util
 * 
 * @author Andy Shek
 */
public class ByteBufferUtil {

	/**
	 * Reversal Byte Array
	 * 
	 * @param buffer
	 * @return
	 */
	public static void reversal(ByteBuffer buffer) {
		byte[] desArray = new byte[buffer.limit()];
		byte[] srcArray = buffer.array();
		for (int i = buffer.limit() - 1, j = 0; i >= 0; i--, j++) {
			desArray[j] = srcArray[i];
		}
		buffer.clear();
		buffer.put(desArray);
		buffer.flip();
	}

	/**
	 * Trim tag in buffer
	 * 
	 * @param buffer
	 * @param tag
	 */
	public static void trim(ByteBuffer buffer, byte tag) {
		ltrim(buffer, tag);
		rtrim(buffer, tag);
	}

	/**
	 * Left trim tag in buffer
	 * 
	 * @param buffer
	 * @param tag
	 */
	public static void ltrim(ByteBuffer buffer, byte tag) {
		byte[] srcArray = buffer.array();
		int p = 0;
		for (int i = 0; i < buffer.limit(); i++) {
			if (srcArray[i] != tag) {
				p = i;
				break;
			}
		}
		byte[] desArray = new byte[buffer.limit() - p];
		System.arraycopy(srcArray, p, desArray, 0, buffer.limit() - p);
		buffer.clear();
		buffer.put(desArray);
		buffer.flip();
	}

	/**
	 * Right trim tag in buffer
	 * 
	 * @param buffer
	 * @param tag
	 */
	public static void rtrim(ByteBuffer buffer, byte tag) {
		byte[] srcArray = buffer.array();
		int p = buffer.limit() - 1;
		for (int i = buffer.limit() - 1; i >= 0; i--) {
			if (srcArray[i] != tag) {
				p = i;
				break;
			}
		}
		byte[] desArray = new byte[p + 1];
		System.arraycopy(srcArray, 0, desArray, 0, p + 1);
		buffer.clear();
		buffer.put(desArray);
		buffer.flip();
	}

	/**
	 * Split buffer by tag
	 * 
	 * @param buffer
	 * @param tag
	 * @return
	 */
	public static List<ByteBuffer> split(ByteBuffer buffer, byte tag) {
		List<ByteBuffer> list = new ArrayList<ByteBuffer>();
		byte[] srcArray = buffer.array();
		trim(buffer, tag);
		for (int i = 0, start = 0, end = 0; i < buffer.limit(); i++) {
			if (srcArray[i] != tag) {
				end = i;
			} else {
				int len = end - start + 1;
				if (len != 1 || srcArray[start] != tag) {
					ByteBuffer tmp = ByteBuffer.allocate(len);
					tmp.put(srcArray, start, len);
					tmp.flip();
					list.add(tmp);
				}
				start = i + 1;
				end = start;
			}
			if (i == buffer.limit() - 1 && start != end) {
				int len = end - start + 1;
				ByteBuffer tmp = ByteBuffer.allocate(len);
				tmp.put(srcArray, start, len);
				tmp.flip();
				list.add(tmp);
			}
		}
		return list;
	}

	/**
	 * Left padding
	 * 
	 * @param buffer
	 * @param len
	 * @param tag
	 */
	public static ByteBuffer lpadding(ByteBuffer buffer, int len, byte tag) {
		int plen = len - buffer.limit();
		if (plen == 0)
			return buffer;
		ByteBuffer desBuffer = ByteBuffer.allocate(len);
		for (int i = 0; i < plen; i++) {
			desBuffer.put(tag);
		}
		desBuffer.put(buffer.array(), 0, buffer.limit());
		desBuffer.flip();
		return desBuffer;
	}

	/**
	 * Right padding
	 * 
	 * @param buffer
	 * @param len
	 * @param tag
	 */
	public static ByteBuffer rpadding(ByteBuffer buffer, int len, byte tag) {
		int plen = len - buffer.limit();
		if (plen == 0)
			return buffer;
		ByteBuffer desBuffer = ByteBuffer.allocate(len);
		desBuffer.put(buffer.array(), 0, buffer.limit());
		for (int i = 0; i < plen; i++) {
			desBuffer.put(tag);
		}
		desBuffer.flip();
		return desBuffer;
	}

	/**
	 * Get buffer form buffer
	 * 
	 * @param buffer
	 * @param len
	 * @return
	 */
	public static ByteBuffer getBuffer(ByteBuffer buffer, int len) {
		byte[] des = new byte[len];
		buffer.get(des);
		ByteBuffer buf = ByteBuffer.allocate(len);
		buf.put(des);
		buf.flip();
		return buf;
	}

	/**
	 * Dump data in buffer
	 * 
	 * @param buffer
	 */
	public static void dump(ByteBuffer buffer) {
		byte[] data = new byte[buffer.limit()];
		buffer.get(data);
		buffer.flip();
		System.out.println(HexUtil.dump(data));
	}

}
