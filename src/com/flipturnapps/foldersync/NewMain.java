package com.flipturnapps.foldersync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.flipturnapps.kevinLibrary.helper.FileHelper;
import com.flipturnapps.kevinLibrary.helper.JFrameHelper;
import com.flipturnapps.kevinLibrary.helper.TextFileHelper;

public class NewMain
{

	public static File dir;
	public static void main (String[] args)
	{
		int count = -1;
		try
		{
			count = args[1].replace(".", "~").split("~").length;
			System.out.println(count);
		}
		catch(Exception ex)
		{

		}
		File storeDir = new File( FileHelper.getAppDataDir("flipturnapps", "FolderSync"));
		storeDir.mkdirs();
		File store = new File(storeDir.getAbsolutePath() + "/" + "startup.cfg");
		File startDir = null;
		if(args[0].equalsIgnoreCase("chooser"))
		{

			try {
				startDir = new File(TextFileHelper.getFirstTextLine(store));
			} catch (IOException e1) 
			{
				e1.printStackTrace();
			}
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setMultiSelectionEnabled(false);
			chooser.setSelectedFile(startDir);
			int returnVal = chooser.showOpenDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION) 
			{
				System.out.println("You chose to open this file: " +
						chooser.getSelectedFile().getName());
			}

			dir = chooser.getSelectedFile();
		}
		else
			dir = new File(args[0]);

		boolean exists = dir.exists();
		boolean directory = dir.isDirectory();
		if(!exists || !directory)
		{
			System.out.println("Invalid!");
			System.exit(-1);
		}


		try {
			store.createNewFile();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		try {
			TextFileHelper.writeTextToFile(store, dir.getAbsolutePath());
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

		if(count == 4)
		{
			System.out.println("client");
			new NewMain().client(args[1]);
		}
		else
		{
			System.out.println("server");
			new NewMain().server(dir.getAbsolutePath());
		}

	}
	private void server(String args) 
	{
		Runnable run = new FSHost(new SimpleFolderSyncOutput(),new File(args),Long.MAX_VALUE,1);
		Thread t = new Thread(run);
		t.start();
	}
	private void client(String args) 
	{
		Runnable run = null;
		try {
			run = new FSClient(args,FSHost.PORT,new SimpleFolderSyncOutput());
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
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