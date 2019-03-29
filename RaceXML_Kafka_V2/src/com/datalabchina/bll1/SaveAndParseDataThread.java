package com.datalabchina.bll1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SaveAndParseDataThread extends Thread {
	private static Logger logger = Logger.getLogger(SaveAndParseDataThread.class);
	public static boolean ShowText=true;
	public static boolean bSendKafka = false;
	public static long save_no = 0;
	public static long saved_no = 0;
	static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
	public static List<String> toSaveList = new ArrayList<String>();
	public static void save(String json) {
		String addTime = getLongStr();
		synchronized (toSaveList) {
			save_no++;
			toSaveList.add(json + "##" + addTime);
//			logger.info("toSaveList.size() = " + toSaveList.size());
			if (isRunning("_SaveAndParseDataThread_") == false) {
//				logger.error("SaveAndParseDataThread not find. start one");
				new Thread(new SaveAndParseDataThread(), "_SaveAndParseDataThread_").start();
			}
		}		
	}
	
	private static boolean isRunning(String name) {
		ThreadGroup sys;
		Thread[] all;
		String sThreadName = null;
		Boolean isRunning = false;

		sys = Thread.currentThread().getThreadGroup();
		all = new Thread[sys.activeCount()];
		sys.enumerate(all);
		for (int i = 0; i < all.length; i++) {
			sThreadName = all[i].getName();
//			logger.info("Thread Name=" + sThreadName);
			if (sThreadName.equals(name)) {
				isRunning = true;
				break;
			}
		}
		
		sys = null;
		all = null;
		sThreadName = null;
		return isRunning;
	}

	public void run() {
		try {
			int r = 0;
			while (true) {
				String line = "";
				synchronized (toSaveList) {
					int s = toSaveList.size();
					if (s > 0) {
						try {
							line = toSaveList.get(0);
							toSaveList.remove(0);
						} catch (Exception e) {
						}
					}
				}
				
				if (line.equals("")) {
					Thread.sleep(10);
					r++;
					if (r > 6000) return;	//1 min no data, exit thread.					
				} else {
					saveLine(line);								
				}
			}
			
		} catch (Exception e) {			
			logger.error("", e);
		}
	}
	
	private void saveLine(String line) {
		try {
			String a[] = line.split("##");
			String json = a[0];
			String addTime = a[1];
			saved_no++;
			if (ShowText) {
				logger.info(saved_no + "/" + save_no + " " + getLongStr() + " " + addTime); // + " json = " + json);
				//" Thread.activeCount = " + Thread.activeCount() +
			}
		} catch (Exception e) {			
			logger.error(getLongStr() + " " + line, e);
		}
	}

	public static String getLongStr() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	public static void main(String arg[]) {
		PropertyConfigurator.configure("log4j.properties");
		System.out.println(getLongStr());
	}

}
