package lunza.j621.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool{
	

	
	
	

	public ExecutorService getFileWithThreadPool(String urlLocation, String filePath, int poolLength, String savePath)
			throws IOException {
		ExecutorService threadPool = Executors.newFixedThreadPool(poolLength);

		long len = getContentLength(urlLocation);
		for (int i = 0; i < poolLength; i++) {
			long start = i * len / poolLength;
			long end = (i + 1) * len / poolLength - 1;
			if (i == poolLength - 1) {
				end = len;
			}
			PicDownload download = new PicDownload(urlLocation, filePath, start, end, savePath);
			threadPool.execute(download);
		}
		return threadPool;

	}

	public static long getContentLength(String urlLocation) throws IOException {
		URL url = null;
		if (urlLocation != null) {
			url = new URL(urlLocation);
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(30 * 60 * 1000);
		conn.setRequestMethod("GET");
		long len = conn.getContentLength();

		return len;
	}

}