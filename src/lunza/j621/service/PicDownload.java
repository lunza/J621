package lunza.j621.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lunza.j621.vo.ImageVO;

public class PicDownload implements Runnable {
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	private List<ImageVO> list;
	private static final int POOLLENTH = 8;

	public PicDownload() {
		super();
	}

	public PicDownload(List<ImageVO> list) {
		super();
		this.list = list;
	}

	@Override
	public void run() {

		String s1 = format.format(new Date());
		System.out.println(s1);


		try {
			for (ImageVO imageVO : list) {
				//RandomAccessFile out = null;
				FileOutputStream out = null;
				InputStream in = null;
				HttpURLConnection conn = null;
				long start = 0;
				long end = 0;
				conn = getHttp(imageVO.getUrlLocation());
//				long len = conn.getContentLength();
//				for (int i = 0; i < POOLLENTH; i++) {
//					start = i * len / POOLLENTH;
//					end = (i + 1) * len / POOLLENTH - 1;
//					if (i == POOLLENTH - 1) {
//						end = len;
//					}
//				}
//				conn.setRequestProperty("Range", "bytes=" + start + "-" + end);

				File file = new File(imageVO.getSavePath());
				System.out.println(imageVO.getFilePath());
				if (!file.exists()) {
					file.mkdirs();
				}
				if (file != null) {
				//	out = new RandomAccessFile(imageVO.getFilePath(), "rwd");
					out = new FileOutputStream(imageVO.getFilePath());
				}
				//out.seek(start);
				in = conn.getInputStream();
				byte[] b = new byte[8096];
				int len2 = 0;
				while ((len2 = in.read(b)) != -1) {
					out.write(b, 0, len2);
				}
				System.out.println("下载完成");
				String s2 = format.format(new Date());
				System.out.println(s2);
				System.out.println(format.parse(s2).getTime() - format.parse(s1).getTime());
				in.close();
				out.close();
				System.gc();
			}

		} catch (Exception e) {
			e.getMessage();
		} finally {
//			try {
//				in.close();
//				out.close();
//				System.gc();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			System.err.println("线程关闭");
		}

	}

	public HttpURLConnection getHttp(String urlLocation) throws IOException {
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