package com.flipturnapps.foldersync;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NewMain
{
	
	public static File dir;
	public static void main (String[] args)
	{
		int count = args[0].replace(".", "~").split("~").length;
		System.out.println(count);
		Scanner scan = new Scanner(System.in);
		System.out.print("Pls write a directory: ");
		String path = scan.nextLine();
		dir = new File(path);
		boolean exists = dir.exists();
		boolean directory = dir.isDirectory();
		if(!exists || !directory)
		{
			System.out.println("Invalid!");
			System.exit(-1);
		}
		scan.close();
		if(count == 4)
		{
			System.out.println("client");
			new NewMain().client(args[0]);
		}
		else
		{
			System.out.println("server");
			new NewMain().server(dir.getAbsolutePath());
		}
	}
	private void server(String args) 
	{
		Runnable run = new FSHost(new SimpleFolderSyncOutput(),new File(getStartDir().getAbsolutePath() + "/" + args + "/"),Long.MAX_VALUE,1);
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