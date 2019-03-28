package com.datalabchina;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.bll.DividendBll;
import com.datalabchina.bll.EquipmentReplacementBll;
import com.datalabchina.bll.FinalOddsBll;
import com.datalabchina.bll.LiveOddsBll;
import com.datalabchina.bll.ParseYesterodayPreRaceBll;
import com.datalabchina.bll.PostRaceBll;
import com.datalabchina.bll.PostRaceLiveBll;
import com.datalabchina.bll.PostRaceWeatherBll;
import com.datalabchina.bll.PreRaceBll;
import com.datalabchina.bll.PreRacePlayerLiveBll;
import com.datalabchina.bll.PreRaceStartTimeBll;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.Config;

public class Controller {
	public static int IsExtractFromWeb = 0;//是否是网络上的资源
	public static int IsExtractFromLocal = 0;//是否是本地资源
	public static int IsDeleteFile = 0;//是否删除本地文件
	public static String sSaveFilePath = "E:\\Denis\\Jpboat\\test";//保存网页的路径
	public static String sBackupFilePath = "";//备份网页的路径
	public static String sStartDate = "20180205";//程序的开始时间
	public static String sEndDate = "20180205";//程序的结束时间
	public static int ValidationCodeTimeout = 90000;
	public static int HttpTimeout = 90000;
	public static int HttpRetry = 3;
	private static Logger logger = Logger.getLogger(Controller.class.getName());
	public static CommonMethod oCommonMethod  = new CommonMethod();
	public static ArrayList<String> endpointArrayList=new ArrayList<String>();
	public static boolean bIsSendOddsNotify=false;
	
	public static void main(String args[]){
		PropertyConfigurator.configure("log4j.properties");
		//当程序输入参数时 获得这两天之间的数据
		if (args.length >= 2) {
			sStartDate = args[0];
			sEndDate = args[1];
		} else {
			//当程序没有输入参数时获得当天的数据
			sStartDate = oCommonMethod.getAddDay(0);
			//结束时间为当前时间
			sEndDate =  oCommonMethod.getAddDay(0);
		}
		try {
			String sConfigPath = System.getProperty("user.dir")+ System.getProperty("file.separator") + "config.xml";
			Config cfg = new Config();//读取配置文件的类
			cfg.loadcfg(new File(sConfigPath));
			Vector<Vector<String>> vBLLList = cfg.getBLLList();
			try {
				HttpTimeout = cfg.getHttpTimeout();
				HttpRetry = cfg.getHttpRetry();
			}catch (Exception e) {
				HttpTimeout = 90000;
				HttpRetry = 3;
			}
			if (HttpTimeout <= 5000)
				HttpTimeout = 90000;
			if (HttpRetry <= 0)
				HttpRetry = 0;
			logger.info("HttpTimeout = " + HttpTimeout);
			logger.info("HttpRetry = " + HttpRetry);
			for (int i = 0; i < vBLLList.size(); i++) {
				Vector<String> vBLLItem = vBLLList.get(i);
				if (vBLLItem.get(5).equals("1")) { 
					String sBLLNO = vBLLItem.get(0).toString();
					IsExtractFromWeb = Integer.parseInt(vBLLItem.get(2).toString());
					IsExtractFromLocal = Integer.parseInt(vBLLItem.get(3).toString());
					IsDeleteFile = Integer.parseInt(vBLLItem.get(6).toString());
					sSaveFilePath = vBLLItem.get(4);
					sBackupFilePath = vBLLItem.get(7).toString();
					switch (Integer.parseInt(sBLLNO)) {
						case 1:
								PreRaceBll pre = new PreRaceBll();
								pre.run();
						break;
						case 2:
								LiveOddsBll lOdds = new LiveOddsBll();
								lOdds.run();
						break;
						case 3:
								PostRaceBll post = new PostRaceBll();
								post.run();
						break;
						case 4:
								DividendBll d = new DividendBll();
								d.run();
						break;
						case 5:
								FinalOddsBll fOdds = new FinalOddsBll();
								fOdds.run();
						break;
						case 6:
								PreRacePlayerLiveBll playerLive = new PreRacePlayerLiveBll();
								playerLive.run();
						break;
						case 7:
								PostRaceWeatherBll pWeath = new PostRaceWeatherBll();
								pWeath.run();
						break;
						case 8:
								PostRaceLiveBll postlive= new PostRaceLiveBll();
								postlive.run();
						break;
						case 9:
								EquipmentReplacementBll ebll= new EquipmentReplacementBll();
								ebll.run();
						break;
						case 10:
								PreRaceStartTimeBll sbll= new PreRaceStartTimeBll();
								sbll.run();
						break;
						case 11:
								ParseYesterodayPreRaceBll y = new ParseYesterodayPreRaceBll();
								y.run();
						break;
					}
				}
			}
		}catch (Exception e){
			logger.error("main", e);
		}
	}
}
