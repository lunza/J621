package lunza.j621.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lunza.j621.vo.ImageVO;

public class ThreadPool {

	public static ExecutorService getFileWithThreadPool(List<ImageVO> tdData,int threadPoolSize) throws IOException {
		ExecutorService taskExecutor = Executors.newCachedThreadPool();
		int end = tdData.size();
		List<ImageVO> data = new ArrayList<ImageVO>();
		List<List<ImageVO>> li = new ArrayList<List<ImageVO>>();
		try {
			// 数据集拆分
			if (tdData.size() > threadPoolSize) {
				int startPoint;
				int endPoint;
				
				// 生成和线程数相等数量的数据集
				for (int i = 1; i <= threadPoolSize; i++) {
					startPoint = i - 1;
					endPoint = i;
					if (endPoint != threadPoolSize) {
						data = getDataList(tdData, end / threadPoolSize * startPoint,
								end / threadPoolSize * endPoint);
						li.add(data);
						// 处理余数
					} else {
						data = getDataList(tdData, end / threadPoolSize * startPoint, end);
						li.add(data);
					}
				}
				// 生成新线程
				for (int i = 0; i <= threadPoolSize - 1; i++) {
					taskExecutor.execute(new PicDownload(li.get(i)));
					// 需要加延迟5到10毫秒，否则易发生数据库死锁
					Thread.sleep(10);
				}
			} else {
				taskExecutor.execute(new PicDownload(tdData));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭线程池
			taskExecutor.shutdown();
			while (true) {
				if (taskExecutor.isTerminated()) {
					System.out.println("所有的子线程都结束了！");
					System.gc();
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return taskExecutor;

	}

	private static List<ImageVO> getDataList(List<ImageVO> tdData, int start, int end) {
		List<ImageVO> data = new ArrayList<ImageVO>();
		for (int i = start; i < end; i++) {
			data.add(tdData.get(i));
		}
		return data;
	}

}