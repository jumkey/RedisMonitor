package org.cafeboy.redis.monitor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

public class ZipFileFilter implements FilenameFilter {
	private final String ext = ".zip";

	public boolean accept(File dir, String filename) {
		boolean flag = false;
		if ((filename.toLowerCase(Locale.US).endsWith(ext) || (new File(new StringBuilder().append(dir.getPath()).append(File.separatorChar).append(filename).toString())).isDirectory()) && filename.charAt(0) != '.')
			flag = true;
		return flag;
	}

}
