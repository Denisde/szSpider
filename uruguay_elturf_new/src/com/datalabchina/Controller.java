package com.datalabchina;

import java.io.File;
import java.net.InetAddress;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.bll.DividendBll;
import com.datalabchina.bll.PostRaceBll;
import com.datalabchina.bll.PreRaceBll;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.Config;
import com.datalabchina.common.DateUtils;
import com.datalabchina.common.MailSend;


//import com.common.CommonMethod;
public class Controller {
	public static int IsExtractFromWeb = 0;
	public static int IsExtractFromLocal = 0;
	public static int IsDeleteFile = 0;
	public static String sSaveFilePath = "F:\\Denis\\elturf\\Test";
	public static String sBackupFilePath = "F:\\Denis\\elturf\\Test_Bak";
	public static String sStartDate = "20190301";
	public static String sEndDate = "20190301";
//	private static String br = System.getProperty("line.separator");
	public static int ValidationCodeTimeout = 90000;
	public static int HttpTimeout = 90000;
	public static int HttpRetry = 3;
	public static String LocalFileName = null;
	private static Logger logger = Logger.getLogger(Controller.class.getName());
	public static void main(String args[])
	{
		PropertyConfigurator.configure("log4j.properties");
		CommonMethod oCommonMethod = new CommonMethod();
		if(args.length==1){
			if("readFromLocal".toLowerCase().equals(args[0])){
				LocalFileName ="1";
			}
		}
		if (args.length >= 2) {
			sStartDate = args[0];
			sEndDate = args[1];
		} else {
			sStartDate = oCommonMethod.getAddDay(-3);
			sEndDate = oCommonMethod.getAddDay(3);//结束时间为当前时间
		}
		try {
			String sConfigPath = System.getProperty("user.dir")+ System.getProperty("file.separator") + "config.xml";
			Config cfg = new Config();//读取配置文件的类
			cfg.loadcfg(new File(sConfigPath));
			Vector<Vector<String>> vBLLList = cfg.getBLLList();
			try {
				HttpTimeout = cfg.getHttpTimeout();
				HttpRetry = cfg.getHttpRetry();
			} catch (Exception e) {
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
				if (vBLLItem.get(5).equals("1")) { // run flag
					String sBLLNO = vBLLItem.get(0).toString();
					IsExtractFromWeb = Integer.parseInt(vBLLItem.get(2).toString());
					IsExtractFromLocal = Integer.parseInt(vBLLItem.get(3).toString());
					IsDeleteFile = Integer.parseInt(vBLLItem.get(6).toString());
					sSaveFilePath = vBLLItem.get(4);
					sBackupFilePath = vBLLItem.get(7).toString();
					switch (Integer.parseInt(sBLLNO)) 
					{
						case 1:
							PostRaceBll post = new PostRaceBll();
							if("1".equals(LocalFileName)){
								post.readFromLocalBySql();
							}else{
								post.run();
							}
						break;
						case 2:
							DividendBll d = new DividendBll();
							d.run();
						break;
						case 3:
							PreRaceBll pre = new PreRaceBll();
							pre.run();
						break;
					}
				}
			}
//发送邮件
		try {
				MailSend oMailSend = new MailSend();
				oMailSend.msgSubject = "";
				String hostIp = InetAddress.getLocalHost().getHostAddress();
				oMailSend.sendMail("UruGuay_elturf Denis from " + hostIp,DateUtils.getLongStr());
				logger.warn("finshed send mail");
			}
			catch (Exception e)
			{
				logger.error("send email error", e);
			}
		logger.info("All task finished  !!");
		}
		catch (Exception e) 
		{
			logger.error("main", e);
		}
	}
}
