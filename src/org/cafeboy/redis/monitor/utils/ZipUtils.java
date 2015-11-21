package org.cafeboy.redis.monitor.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class ZipUtils {
	private static final String TAG = ZipUtils.class.getSimpleName();

	/**
	 * 取得压缩包中的 文件列表(文件夹,文件自选)
	 * 
	 * @param zipFileString
	 *            压缩包名字
	 * @param bContainFolder
	 *            是否包括 文件夹
	 * @param bContainFile
	 *            是否包括 文件
	 * @return
	 * @throws IOException
	 */
	public static List<File> GetFileList(String zipFileString, boolean bContainFolder, boolean bContainFile) throws IOException {

		Log.v(TAG, "GetFileList(String)");

		List<File> fileList = new ArrayList<File>();
		ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
		ZipEntry zipEntry;
		String szName = "";

		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();

			if (zipEntry.isDirectory()) {
				// get the folder name of the widget
				szName = szName.substring(0, szName.length() - 1);
				File folder = new File(szName);
				if (bContainFolder) {
					fileList.add(folder);
				}

			} else {
				File file = new File(szName);
				if (bContainFile) {
					fileList.add(file);
				}
			}
		}// end of while

		inZip.close();

		return fileList;
	}

	/**
	 * 返回压缩包中的文件InputStream
	 * 
	 * @param zipFileString
	 *            压缩文件的名字
	 * @param fileString
	 *            解压文件的名字
	 * @return InputStream
	 * @throws Exception
	 */
	public static byte[] UpZip(String zipFileString, String fileString) throws Exception {
		Log.v(TAG, "UpZip(String, String)");
		ZipFile zipFile = new ZipFile(zipFileString);
		ZipEntry zipEntry = zipFile.getEntry(fileString);
		long size = zipEntry.getSize();
		byte[] ret = null;
		if (size > 0) {
			InputStream is = zipFile.getInputStream(zipEntry);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();

			ret = buffer.toByteArray();
		}
		return ret;
	}
}
