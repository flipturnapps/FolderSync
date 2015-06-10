package com.flipturnapps.foldersync;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

public class NewMain
{
	public static void main (String[] args)
	{
		int count = args[0].replace(".", "~").split("~").length;
		System.out.println(count);
		if(count == 4)
		{
			System.out.println("client");
			new NewMain().client(args[0]);
		}
		else
		{
			System.out.println("server");
			new NewMain().server(args[0]);
		}
	}
	private void server(String args) 
	{
		Runnable run = new FSHost(new SimpleFolderSyncOutput(),new File(getStartDir().getAbsolutePath() + "/" + args + "/"),50000,1);
		Thread t = new Thread(run);
		t.start();
	}
	private void client(String args) 
	{
		Runnable run = null;
		try {
			run = new FSClient(args,FSHost.PORT,new SimpleFolderSyncOutput());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread t = new Thread(run);
		t.start();
	}
	public static File getStartDir() 
	{
		String out = new File(NewMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
		File outfile = new File(out);
		File parFile = new File(outfile.getParent().replace("%20", " "));
		return parFile;
	}
}