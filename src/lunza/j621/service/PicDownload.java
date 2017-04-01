package lunza.j621.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class PicDownload implements Runnable {
	private String urlLocation;

	private String filePath;

	private long start;

	private long end;

	private String savePath;

	private int count;

	PicDownload(String urlLocation, String filePath, long start, long end, String savePath) {
		this.urlLocation = urlLocation;
		this.filePath = filePath;
		this.start = start;
		this.end = end;
		this.savePath = savePath;

	}

	@Override
	public void run() {
		try {
			
			HttpURLConnection conn = getHttp();
			conn.setRequestProperty("Range", "bytes=" + start + "-" + end);

			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			RandomAccessFile out = null;
			if (file != null) {
				out = new RandomAccessFile(filePath, "rwd");
			}
			out.seek(start);
			InputStream in = conn.getInputStream();
			byte[] b = new byte[1024];
			int len = 0;

			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			count++;
			System.out.println("渲染完成");
			
			in.close();
			out.close();
			System.err.println("渲染完成");
			
		} catch (Exception e) {
			e.getMessage();
		}

	}

	public HttpURLConnection getHttp() throws IOException {
		URL url = null;
		if (urlLocation != null) {
			url = new URL(urlLocation);
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(30 * 60 * 1000);
		conn.setRequestMethod("GET");

		return conn;
	}



}