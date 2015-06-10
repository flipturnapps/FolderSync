package com.flipturnapps.foldersync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import com.flipturnapps.kevinLibrary.helper.ThreadHelper;

public class FSHost implements Runnable
{
	static final int PORT = 12345;
	private File startDir;
	private FolderSyncOutput out;
	private ArrayList<File> fileList;
	private long totalBytes;
	private long[] byteCountEndings;
	private String[] paths;
	private long waitTime;
	private int maxClients;
	private int port;
	private boolean acceptorDone = false;


	public FSHost(FolderSyncOutput output, File startDir, long waitTime, int maxClients)
	{
		this.out = output;
		this.startDir = startDir;
		this.waitTime = waitTime;
		this.maxClients = maxClients;
		this.port = PORT;
	}


	public void run()
	{
		this.out.textOutput("Searching dir. " + this.startDir.getAbsolutePath());
		searchDir(this.startDir);
		this.out.textOutput("Search complete. Found " + this.fileList.size() + " files.");
		for (int i = 0; i < this.fileList.size(); i++)
		{
			this.totalBytes += this.fileList.get(i).length();
		}
		this.out.textOutput("Total size: " + this.totalBytes + " bytes.");
		this.out.textOutput("Byte count endings: ");
		this.byteCountEndings = new long[this.fileList.size()];
		for (int i = 0; i < this.byteCountEndings.length; i++)
		{
			if (i == 0) {
				this.byteCountEndings[i] = this.fileList.get(i).length();
			} else
				this.byteCountEndings[i] = (this.fileList.get(i).length() + this.byteCountEndings[(i - 1)]);
			this.out.textOutput(" -BCE- " + this.byteCountEndings[i]);
		}
		this.out.textOutput("Relative file paths: ");
		this.paths = new String[this.fileList.size()];
		for (int i = 0; i < this.paths.length; i++)
		{
			this.paths[i] = getClientPath(this.fileList.get(i));
			this.out.textOutput(" -RFP- " + this.paths[i]);
		}
		this.out.textOutput("Initialization complete.");
		this.out.textOutput("");



		this.out.textOutput("Starting serverside.");
		FSServerSocket server = null;
		try {
			server = new FSServerSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (server != null)
			this.out.textOutput("Server started on port " + this.port + " successfully.");
		this.out.textOutput("Starting accepting phase. Will end if time exceeds " + this.waitTime / 1000L + " seconds or if " + this.maxClients + " # of clients has been reached.");
		ThreadHelper.sleep(1000);
		this.acceptorDone = false;
		Thread acceptorThread = new Thread(new Acceptor(server));
		acceptorThread.start();
		long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime < this.waitTime) && (!this.acceptorDone))
		{
			ThreadHelper.sleep(50L);
		}
		if (!this.acceptorDone)
		{
			acceptorThread.interrupt();
		}
		this.out.textOutput("Completed acception phase. Clients: ");
		for (int i = 0; i < server.getClients().size(); i++)
		{
			Socket socket = (server.getClients().get(i)).getSocket();
			this.out.textOutput(" -CLI-  ip:" + socket.getLocalAddress().getHostAddress().toString() + " port:" + socket.getLocalPort());
		}




		String byteCountsString = "";
		String fileString = "";
		for (int i = 0; i < this.fileList.size(); i++)
		{
			byteCountsString = byteCountsString + this.byteCountEndings[i] + "~";
			fileString = fileString + this.paths[i] + "~";
		}
		byteCountsString = byteCountsString.substring(0, byteCountsString.length() - 1);
		fileString = fileString.substring(0, fileString.length() - 1);
		this.out.textOutput("Info to send to clients: ");
		this.out.textOutput(" -INF- " + byteCountsString);
		this.out.textOutput(" -INF- " + fileString);
		for (int i = 0; i < server.getClients().size(); i++)
		{
			(server.getClients().get(i)).textWriter.println(byteCountsString);
			(server.getClients().get(i)).textWriter.println(fileString);
			(server.getClients().get(i)).textWriter.flush();
		}
		this.out.textOutput("Sent info to clients.");



		this.out.textOutput("Starting file transfer");
		ArrayList<File> files = new ArrayList<File>();
		for (int i = 0; i < this.fileList.size(); i++)
		{
			files.add(this.fileList.get(i));
		}

		int fileNum = -1;
		int bytesTransfered = 0;
		ArrayList<FileInputStream> fiss = new ArrayList<FileInputStream>();
		FileInputStream fis = null;
		byte[] buffer = new byte[1024];
		int val = -1;
		boolean init = false;
		File f = null;
		do
		{
			if (val == -1)
			{
				if (init) {
					try
					{
						fis.close();
					}
					catch (IOException e1) {
						/* 153 */             e1.printStackTrace();
					}
				}

				if (files.size() <= 0) {
					break;
				}
				f = files.remove(0);
				try
				{
					fis = new FileInputStream(f);
					fiss.add(fis);
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				fileNum++;
				this.out.textOutput("FIS has been set to file \"" + getClientPath(f) + "\"");
				init = true;
			}






			try
			{
				val = fis.read(buffer, 0, buffer.length);

				if (val != -1)
				{
					for (int i = 0; i < server.getClients().size(); i++)
					{
						(server.getClients().get(i)).os.write(buffer, 0, val);
						(server.getClients().get(i)).os.flush();
					}
				}
			}
			catch (IOException ex)
			{
				this.out.textOutput("IO Exception");
			}
			bytesTransfered += val;
			this.out.progress((bytesTransfered + 0.0D) / (this.totalBytes + 0.0D));
		} 
		while (val != -1);

		for (;;)
		{
			this.out.textOutput("WAITING FOR CLIENT ACKS!");
			boolean brk = true;
			for (int i = 0; i < server.clients.size(); i++)
			{
				if ((server.clients.get(i)).getLastAckNum() < fileNum)
				{
					brk = false;
					break;
				}
			}
			if (brk)
				break;
			ThreadHelper.sleep(100L);
		}

		try
		{
			Thread.sleep(10000L);
		}
		catch (InterruptedException e1) {
			e1.printStackTrace();
		}


		this.out.textOutput("Finished transfer.  Closing phase. All closing errors will be printed to the console.");
		this.out.textOutput("Starting closing client output streams.");
		for (int i = 0; i < server.getClients().size(); i++)
		{
			Client c = server.getClients().get(i);
			try {
				c.os.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.out.textOutput("Starting closing client input streams.");
		for (int i = 0; i < server.getClients().size(); i++)
		{
			Client c = server.getClients().get(i);
			try {
				c.is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.out.textOutput("Starting closing client sockets.");
		for (int i = 0; i < server.getClients().size(); i++)
		{
			Client c = server.getClients().get(i);
			try {
				c.socket.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.out.textOutput("Closing server.");
		try 
		{

			server.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		for (int i = 0; i < fiss.size(); i++) {
			try
			{
				fiss.get(i).close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.out.textOutput("Serverside Folder Sync has been completed.");
	}

	private class Acceptor implements Runnable {
		private FSHost.FSServerSocket server;

		public Acceptor(FSHost.FSServerSocket ss) {
			this.server = ss;
		}

		public void run() {
			for (int i = 0; i < FSHost.this.maxClients; i++) 
			{
				try
				{
					this.server.accept();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
					System.out.println("whyyyyyy");
					i--;
					ThreadHelper.sleep(500);
				}
			}
			FSHost.this.acceptorDone = true;
		}
	}

	private class FSServerSocket extends ServerSocket
	{
		
		private ArrayList<FSHost.Client> clients;

		public FSServerSocket() throws IOException
		{
			super(PORT);
			
			this.clients = new ArrayList<FSHost.Client>();			       
		}

		public ArrayList<FSHost.Client> getClients() 
		{
			return this.clients;
		}

		

		public Socket accept() throws IOException 
		{
			Socket s = super.accept();

			this.clients.add(new FSHost.Client(s));
			return s;
		}
	}

	private class Client implements Runnable
	{
		private InputStream is;
		private OutputStream os;
		private PrintWriter textWriter;
		private Socket socket;
		private BufferedReader reader;
		private ArrayList<String> readInput;
		private boolean endReading = false;

		public Client(Socket s) {
			this.socket = s;
			try 
			{
				this.os = s.getOutputStream();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try 
			{
				this.textWriter = new PrintWriter(s.getOutputStream());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try 
			{
				this.is = s.getInputStream();
				this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			this.readInput = new ArrayList<String>();
			new Thread(this).start();
		}

		public int getLastAckNum()
		{
			try {
				String last = this.readInput.get(this.readInput.size() - 1);
				return Integer.parseInt(last);
			}
			catch (Exception ex) {}

			return -1;
		}

		public void stopReading()
		{
			this.endReading = true;
		}

		public Socket getSocket() 
		{
			return this.socket; 
		}

		public PrintWriter getTextWriter()
		{
			return this.textWriter;
		}

		public InputStream getIs() 
		{
			return this.is;
		}

		public OutputStream getOs() 
		{
			return this.os;
		}


		public void run()
		{
			while (!this.endReading)
			{
				String read = null;
				try 
				{
					read = this.reader.readLine();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				if ((read != null) && (!read.equals("")))
					this.readInput.add(read);
				ThreadHelper.sleep(500L);
			}
		}
	}


	private String getClientPath(File file)
	{
		String absPath = file.getAbsolutePath();
		String startDirPath = this.startDir.getAbsolutePath();
		String parPath = new File(new File(startDirPath).getParent()).getAbsolutePath();
		String path = absPath.substring(parPath.length() + 1);
		return path;
	}

	private void searchDir(File dir) {
		if (this.fileList == null)
			this.fileList = new ArrayList<File>();
		dir = new File(dir.getAbsolutePath());
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isFile()) {
				this.fileList.add(files[i]);
			} else {
				searchDir(files[i]);
			}
		}
	}
}


