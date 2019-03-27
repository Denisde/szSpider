package com.datalabchina.bll1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.DateUtils;
import com.common.FileDispose;
import com.datalabchina.controler.Controler;

public class SaveFileThread extends Thread {
	
	private static Logger logger = Logger.getLogger(SaveFileThread.class);

	public static boolean ShowText=false;
	
	public static long save_no = 0;
	public static long saved_no = 0;
	
	public static List<String> toSaveList = new ArrayList<String>();

	public static void save(String json) {
		synchronized (toSaveList) {
			save_no++;
			toSaveList.add(json);
//			logger.info("toSaveList.size() = " + toSaveList.size());
			if (isRunning("_SaveFileThread_") == false) {
//				logger.error("SaveFileThread not find. start one");
				new Thread(new SaveFileThread(), "_SaveFileThread_").start();
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
					int c = 50;
					if (c > s) c = s;
					if (s > 0) {
						line = "";
						while (c > 0) {
							try {
								c--;
								line = line + "\r\n" + toSaveList.get(0);
								toSaveList.remove(0);
							} catch (Exception e) {
							}							
						}
					}
				}
				
				if (line.equals("") || line.equals("\r\n")) {
					Thread.sleep(100);
					r++;
					if (r > 600) return;	//1 min no data, exit thread.
				} else {
					r=0;
					saveToFile(line.substring(2));								
				}
			}
			
		} catch (Exception e) {			
			logger.error("", e);
		}
	}

	public static void saveToFile(String conent) {
		
		try {
			
			String file = Controler.saveFilePath;
			if (file.endsWith(File.separator) == false) {
				file = file + File.separator;
			}
			
			file = file + DateUtils.getFilePath() + File.separator;
			
			if (!FileDispose.bIfExistFile(file)) {
				FileDispose.createDirectory(file);			
			}
			file = file + DateUtils.getShortStr1() + ".txt";
			
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file, true)));
				out.write(conent + "\r\n");
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			
		} catch (Exception e) {
			logger.error("", e);
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
//		System.out.println(getLongStr());
		
		SaveFileThread.save("a");
		SaveFileThread.save("b");
		SaveFileThread.save("c");
		SaveFileThread.save("d");
	}

}
