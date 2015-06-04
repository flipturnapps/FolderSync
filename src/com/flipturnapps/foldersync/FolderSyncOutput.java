package com.flipturnapps.foldersync;

public abstract class FolderSyncOutput
{
	protected abstract void progress(double paramDouble);

	protected abstract void textOutput(String paramString);

	protected double computeCountPercent(int totalFiles, int currentFile, long bytesOfCurrent, long[] byteCountEndings)
	{
		long bytesForThisFile;
		if (currentFile == 0) 
		{
			bytesForThisFile = byteCountEndings[0];
		}
		else
		{
			bytesForThisFile = byteCountEndings[currentFile] - byteCountEndings[(currentFile - 1)];
		}
		double ret = (currentFile + 0.0D) / (totalFiles + 0.0D) + (bytesOfCurrent + 0.0D) / (bytesForThisFile + 0.0D) * (1.0D / (totalFiles + 0.0D));
		return ret;

	}
}