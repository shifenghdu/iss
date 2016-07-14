package com.db.iss.encrypt;

import com.db.iss.core.util.HexUtil;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;



/**
    JAR 加密发布工具，用于实现将指定的jar加密为只有自己编译的jdk才认识的版本
	cd /Users/xwarrior/Documents/myworkspace/xiaojd_engine/xiaojd.tools/bin
	java com.mrule.codepub.JarEncrypt /Users/xwarrior/Documents/export/engine_imp1.0.jar
 * @author xwarrior
 *
 */
public class JarEncrypt {

	private static long crc(byte[] buf) {
		CRC32 crc = new CRC32();
		crc.update(buf);
		return crc.getValue();
	}

	private static byte[] md5(byte[] buf) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(buf);
		return md5.digest();
	}

	private static byte[] getBytes(ZipEntry paramZipEntry, JarFile jar)
			throws IOException {
		byte[] buf = new byte[(int) paramZipEntry.getSize()];
		DataInputStream s = new DataInputStream(
				jar.getInputStream(paramZipEntry));
		try {
			s.readFully(buf);
		} finally {
			s.close();
		}
		return buf;
	}

	private static byte[] compress(byte[] value) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
		int compressionLevel = Deflater.DEFAULT_COMPRESSION;
		Deflater compressor = new Deflater();
		try {
			compressor.setLevel(compressionLevel);
			compressor.setInput(value);
			compressor.finish();
			byte[] buf = new byte[1024];
			while (!(compressor.finished())) {
				int count = compressor.deflate(buf);
				bos.write(buf, 0, count);
			}
		} finally {
			compressor.end();
		}
		return bos.toByteArray();
	}

	private static void proce(String file) throws IOException,
			NoSuchAlgorithmException {
		String baseName = file.substring(0, file.lastIndexOf("."));
		JarFile jar = new JarFile(file);
		JarOutputStream jad = new JarOutputStream(new FileOutputStream(
				new File(baseName + ".crypted.jar")), jar.getManifest());
		try {
			Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				if ( entry.getSize() == 0 || entry.getName().equals("META-INF/MANIFEST.MF") )
					 continue;
				
				byte[] buf = getBytes(entry, jar);
				final String ext = ".class";
				String s = entry.getName();
				if ( s.endsWith(ext)
						&& (s.startsWith("com/mrule/") ) ) {
					String name = s.substring(0, s.length() - ext.length());
	
					System.out.println(s + "->" + name);

					byte[] rc4_buf = new RC4(name.getBytes("utf-8"))
							.rc4(compress(buf));
					ByteArrayOutputStream ms = new ByteArrayOutputStream();
					DataOutputStream head = new DataOutputStream(ms);
					head.writeInt(0xBEEFCACE);
					head.writeInt(rc4_buf.length + 32);
					head.writeInt(buf.length);
					head.writeInt((int) crc(rc4_buf));
					head.write(md5(buf));
					head.write(rc4_buf);
					JarEntry je = new JarEntry(entry.getName());
					buf = ms.toByteArray();
					je.setMethod(JarEntry.STORED);
					je.setSize(buf.length);
					je.setCrc(crc(buf));
					jad.putNextEntry(je);
					jad.write(buf);
				} else {
					jad.putNextEntry(entry);
					jad.write(buf);
				}
			}
			System.out.println("succeed:" + file);
		} finally {
			jad.close();
			jar.close();
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws IOException,NoSuchAlgorithmException {
  		if (args.length == 0) {
 			System.out.println(JarEncrypt.class.getName() + " <1.jar> [2.jar]......");
 			return;
 		}
		//args = new String[]{"E:\\ipharmacare_common\\engineImpl\\target\\ipharmacare-plat-engineImpl-1.1.32.jar"};
		for (String str : args)
			proce(str);
	}

    @Test
    public void test() throws IOException, NoSuchAlgorithmException {
        byte[] buf = new String("123").getBytes();
        String name =  "name1";
        byte[] rc4_buf = new RC4(name.getBytes("utf-8"))
                .rc4(compress(buf));
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        DataOutputStream head = new DataOutputStream(ms);
        head.writeInt(0xBEEFCACE);
        head.writeInt(rc4_buf.length + 32);
        head.writeInt(buf.length);
        head.writeInt((int) crc(rc4_buf));
        head.write(md5(buf));
        head.write(rc4_buf);
        System.out.println(HexUtil.dump(ms.toByteArray()));
        System.out.println(ms.toByteArray().length);
    }
}
