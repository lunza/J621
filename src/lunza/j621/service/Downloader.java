package lunza.j621.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader {

	public static void download(String config) {
		Properties pro = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(config));
			pro.load(in);
			int startIndex = Integer.parseInt(pro.getProperty("START_INDEX"));
			int endIndex = Integer.parseInt(pro.getProperty("END_INDEX"));
			String key = pro.getProperty("KEY");
			int minScore = Integer.parseInt(pro.getProperty("MIN_SCORE"));
			String localAddr = pro.getProperty("LOCAL_ADDR");
			int startFileName = Integer.parseInt(pro.getProperty("START_FILE_NAME"));
			System.err.println("开始分析页码信息");
			List<String> indexUrlList = getIndexUrlList(startIndex, endIndex, key);
			System.err.println("页码信息分析完毕,开始分析图片详细地址");
			List<String> SimpleImgUrlList = getSimpleImgUrlList(indexUrlList, minScore);
			System.err.println("详细地址分析完毕,开始分析图片静态地址");
			List<String> HDImgUrlList = getHDImgUrlList(SimpleImgUrlList, key);
			System.err.println("静态地址分析完毕,开始下载图片");
			downloadPic(HDImgUrlList, localAddr, key, startFileName);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取页码url
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @param key
	 * @return
	 */
	private static List<String> getIndexUrlList(int startIndex, int endIndex, String key) {
		String url = null;
		List<String> indexUrlList = new ArrayList<String>();
		for (int a = startIndex; a <= endIndex; a++) {
			url = "https://e621.net/post/index/" + a + "/" + key;
			System.out.println("正在扫描第" + a + "页,地址为" + url);
			indexUrlList.add(url);
		}

		return indexUrlList;

	}

	/**
	 * 获取图片详细地址
	 * 
	 * @param indexUrlList
	 * @param key
	 * @param minScore
	 * @return
	 * @throws Exception
	 */
	private static List<String> getSimpleImgUrlList(List<String> indexUrlList, int minScore) throws Exception {
		Document doc = null;
		int scoreInt = 0;
		String linkUrl = null;
		List<String> SimpleImgUrlList = new ArrayList<String>();
		int count = 1;
		for (String indexUrl : indexUrlList) {
			doc = Jsoup.connect(indexUrl).get();
			Elements spans = doc.getElementsByClass("thumb");
			for (Element span : spans) {
				Elements scores = span.getElementsByClass("post-score-faves");
				for (Element score : scores) {
					scoreInt = Integer.parseInt(score.text().substring(1, score.text().length()));
					if (scoreInt < minScore) {
						System.out.println("图片分数过低跳过下载,分数为" + scoreInt);
						break;
					} else {
						Elements links = span.getElementsByTag("a");
						for (Element link : links) {
							linkUrl = "https://e621.net" + link.attr("href");
							System.out.println("正在分析第" + count + "张图片,地址为:" + linkUrl);
							SimpleImgUrlList.add(linkUrl);
							count++;
						}
					}
				}
			}
		}

		return SimpleImgUrlList;
	}

	/**
	 * 
	 * @param simpleImgUrlList
	 * @param key
	 * @param localAddr
	 * @return
	 * @throws Exception
	 */
	private static List<String> getHDImgUrlList(List<String> simpleImgUrlList, String key) throws Exception {
		Document doc = null;
		String hdImgUrl = null;
		List<String> HDImgUrlList = new ArrayList<String>();
		int count = 1;
		for (String imgUrl : simpleImgUrlList) {
			doc = Jsoup.connect(imgUrl).get();
			Element img =doc.getElementById("image");
			try {
				hdImgUrl = img.attr("src");
			} catch (NullPointerException e) {
				System.err.println("跳过flash文件");
				continue;
			}
			

			System.out.println("正在分析第" + count + "张图片静态地址,地址为" + hdImgUrl);
			HDImgUrlList.add(hdImgUrl);
			count++;

		}
		return HDImgUrlList;
	}

	/**
	 * 
	 * @param hDImgUrlList
	 * @param localAddr
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void downloadPic(List<String> HDImgUrlList, String localAddr, String key, int startFileName)
			throws Exception {
		int count = startFileName;
		int total = 0;
		String filePath = null;
		String savePath = null;
		
		for (String urlLocation : HDImgUrlList) {
			savePath = localAddr + key + "\\";
			filePath = localAddr + key + "\\" + count + "."
					+ urlLocation.substring(urlLocation.length() - 3, urlLocation.length());
			ThreadPool pool = new ThreadPool();
			pool.getFileWithThreadPool(urlLocation, filePath, 4, savePath);
			System.out.println("正在扫描第" + count + "张图片,地址为" + filePath);
			count++;
			total++;
			
		}
		System.err.println("所有图片扫描完毕!本次共下载" + total + "张图片");
		System.err.println("正在渲染请稍等");
		// 关闭ExecutorService
	}
}
