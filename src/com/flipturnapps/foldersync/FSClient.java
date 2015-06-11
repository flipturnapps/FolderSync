package com.flipturnapps.foldersync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.flipturnapps.kevinLibrary.helper.FlushWriter;

public class FSClient extends Socket implements Runnable
{
	private FolderSyncOutput out;
	private long bytesTransferred;

	public FSClient(String ip, int port, FolderSyncOutput out) throws UnknownHostException, IOException
	{
		super(ip, port);
		this.out = out;
	}

	public void run()
	{
		this.out.textOutput("Client initialized.  Starting get info phase.");
		String byteCountString = null;
		String fileNamesString = null;
		try {
			BufferedReader textReader = new BufferedReader(new InputStreamReader(getInputStream()));
			byteCountString = textReader.readLine();
			fileNamesString = textReader.readLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		this.out.textOutput("Read info.  Info:");
		this.out.textOutput(" -INF- ByteCountString: " + byteCountString);
		this.out.textOutput(" -INF- FileNamesString: " + fileNamesString);
		this.out.textOutput("Attempting to parse info.");


		String[] byteCountSplit = byteCountString.split("~");
		long[] byteCountEndings = new long[byteCountSplit.length];
		this.out.textOutput("Parsing BCS.");
		for (int i = 0; i < byteCountSplit.length; i++)
		{
			byteCountEndings[i] = Long.parseLong(byteCountSplit[i]);
			this.out.textOutput(" -BCE- " + byteCountEndings[i]);
		}


		String[] fileNamesSplit = fileNamesString.split("~");
		String[] fileNames = new String[fileNamesSplit.length];
		this.out.textOutput("Parsing FNS.");
		for (int i = 0; i < fileNamesSplit.length; i++)
		{
			fileNames[i] = NewMain.dir.getAbsolutePath() + fileNamesSplit[i];
			this.out.textOutput(" -FNS- " + fileNames[i]);
		}



		long bytesTransfered = 0L;
		FileOutputStream fos = null;
		byte[] buffer = new byte[1024];
		int val = -1;
		int fileSel = -1;
		boolean init = false;
		File f = null;
		PrintWriter writer = null;
		try 
		{
			writer = new FlushWriter(getOutputStream());
		}
		catch (IOException e2) 
		{
			e2.printStackTrace();
		}
		while (bytesTransferred < byteCountEndings[byteCountEndings.length - 1])
		{
			if ((!init) || (bytesTransfered == byteCountEndings[fileSel]))
			{
				if (init) {
					try
					{
						fos.close();
					}
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				writer.println(fileSel);
				if (fileSel >= fileNames.length - 1) {
					break;
				}
				fileSel++;
				f = new File(fileNames[fileSel]);
				File par = new File(new File(f.getParent()).getAbsolutePath());
				par.mkdirs();
				try {
					f.createNewFile();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
				try
				{
					fos = new FileOutputStream(f);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				this.out.textOutput("FOS has been set to file \"" + f.getAbsolutePath() + "\"");
				init = true;
			}
			try
			{
				int bytestoget = (int)Math.min(buffer.length, byteCountEndings[fileSel] - this.bytesTransferred);

				val = getInputStream().read(buffer, 0, bytestoget);

				fos.write(buffer, 0, val);


			}
			catch (IOException ex)
			{

				this.out.textOutput("IO Exception");
			}
			bytesTransfered += val;
			this.out.progress((bytesTransfered + 0.0D) / (byteCountEndings[(byteCountEndings.length - 1)] + 0.0D));
		}
		this.out.textOutput("Finished files");
		try {
			close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		this.out.textOutput("Closed.");
	}
}

