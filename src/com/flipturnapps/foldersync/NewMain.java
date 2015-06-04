package com.flipturnapps.foldersync;

import java.io.File;

public class NewMain
{
	public static File getStartDir() 
	{
		String out = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
		File outfile = new File(out);
		File parFile = new File(outfile.getParent().replace("%20", " "));
		return parFile;
	}
}