package com.datalabchina.bll;

import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.datalabchina.Controller;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.PageHelper;
import com.datalabchina.common.RacePool;

public class PostRaceLiveBll implements Runnable{
	private static Logger logger = Logger.getLogger(PostRaceLiveBll.class.getName());
	private PageHelper pageHelper = PageHelper.getPageHelper();
	private Hashtable<String,String> ht =new Hashtable<String,String>();
	private CommonMethod oCommonMethod = new CommonMethod();
	public static Hashtable<String,String> htBeatenDistance = null;
	public boolean isUseTraead = true;
	@Override
	public void run() {
		RacePool rp = new RacePool();
		String startDate = Controller.sStartDate;
		String endDate = Controller.sEndDate;
//		String startDate = "20170721";
//		String endDate = "20170721";
		Vector<String> vDate = CommonMethod.getBetwenneDate(startDate, endDate);
		try {
			for(int i =0;i<vDate.size();i++) {
				String raceDate =vDate.get(i);
				if(isUseTraead){
					int threadNum = 10;
					for(int j=1;j<=threadNum;j++)
					{
						Thread thread = new Thread(new ParsePageThread("TaskThread-"+j,rp));
						thread.start();
					}
					this.getPostRaceUrl(raceDate,rp);
					for(int k=0;k<threadNum;k++)
					{
						rp.AddID("exit");
					}
				}
			}
		}catch (Exception e){
			logger.error("",e);
			for(int i=0;i<3;i++)
			{
				rp.AddID("exit");
			}
		}
	}
	
	public PostRaceLiveBll()
	{
		ht.put("１", "1");
		ht.put("２", "2");
		ht.put("３", "3");
		ht.put("４", "4");
		ht.put("５", "5");
		ht.put("６", "6");
	}
	
	private void getPostRaceUrl(String sraceDate,RacePool rp) {
		try {
			//http://www.boatrace.jp/owpc/pc/race/pay?hd=20170720
			String basicUrl = "http://www.boatrace.jp/owpc/pc/race/pay?hd="+sraceDate;
			String basicBody = pageHelper.doGet(basicUrl);
			//<a href="/owpc/pc/race/racelist?rno=1&jcd=02&hd=20170517">11:01</a>
//			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(.*?)\">.*?</a>");
			Matcher m = oCommonMethod.getMatcherStrGroup(basicBody, "<a href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is-payout2");
			//?rno=1&jcd=02&hd=20170517
			while(m.find()){
				String url = m.group(1);
				String id = "LivePost_"+url;
				rp.AddID(id);
			}
			
			Matcher m1 = oCommonMethod.getMatcherStrGroup(basicBody, "href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<span class=\"is\\-fBold is\\-fColor1\">不成立");
			while(m1.find()){
				String url = m1.group(1);
				String id = "LivePost_"+url;
				rp.AddID(id);
			}
			
			Matcher m2 = oCommonMethod.getMatcherStrGroup(basicBody, "href=\"/owpc/pc/race/raceresult(\\?rno=\\d{1,2}\\&jcd=\\d{1,2}\\&hd=\\d{8})\">\\s*<div class=\"is-lineH20\">\\s*<span class=\"is-payout2");
			while(m2.find()){
				String url = m2.group(1);
				String id = "LivePost_"+url;
				rp.AddID(id);
			}
		} catch (Exception e){
			logger.error("",e);
		}
	}

	public static void main(String[] args) {
		PostRaceLiveBll p = new PostRaceLiveBll();
//		String fileName ="D:\\Denis\\Jpboat\\test\\20170721\\trackId_05\\raceNo_6.html";
//		p.parsePostByRaceID("2017072602303");
		p.run();
	}
	
}	
