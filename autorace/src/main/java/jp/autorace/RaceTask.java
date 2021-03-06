package jp.autorace;

import java.io.File;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

import jp.autorace.dc.AutoRaceDividend;
import jp.autorace.dc.AutoRaceDividendId;
import jp.autorace.dc.AutoRaceFinalE;
import jp.autorace.dc.AutoRaceFinalQ;
import jp.autorace.dc.AutoRaceFinalQw;
import jp.autorace.dc.AutoRaceFinalT;
import jp.autorace.dc.AutoRaceFinalTi;
import jp.autorace.dc.AutoRaceFinalTiId;
import jp.autorace.dc.AutoRaceLiveE;
import jp.autorace.dc.AutoRaceLiveEId;
import jp.autorace.dc.AutoRaceLiveQ;
import jp.autorace.dc.AutoRaceLiveQId;
import jp.autorace.dc.AutoRaceLiveQw;
import jp.autorace.dc.AutoRaceLiveQwId;
import jp.autorace.dc.AutoRaceLiveT;
import jp.autorace.dc.AutoRaceLiveTId;
import jp.autorace.dc.AutoRaceLiveTi;
import jp.autorace.dc.AutoRaceLiveTiId;
import jp.autorace.dc.AutoRacePostRacePlayer;
import jp.autorace.dc.AutoRacePostRacePlayerId;
import jp.autorace.dc.AutoRacePostRaceRace;
import jp.autorace.dc.AutoRacePreRacePlayer;
import jp.autorace.dc.AutoRacePreRacePlayerId;
import jp.autorace.dc.AutoRacePreRacePlayerLive;
import jp.autorace.dc.AutoRacePreRacePlayerLiveId;
import jp.autorace.dc.AutoRacePreRaceRace;
import jp.autorace.dc.AutoRacePreRaceRaceLive;
import jp.autorace.dc.AutoRacePreRaceRaceLiveId;
import jp.autorace.dc.AutoRaceRunningPosition;
import jp.autorace.dc.AutoRaceRunningPositionId;
import jp.autorace.dc.AutorRaceCodePlayer;
import jp.autorace.dc.CodePlayer;
import jp.autorace.dc.StringBufferSql;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

import poseidon.bot.PageHelper;
import poseidon.common.CommonFun;
import poseidon.common.StopWatch;
import poseidon.db.DBAccess;
import poseidon.db.ZTStd;

public class RaceTask implements Runnable {
	private static Logger logger = Logger.getLogger(RaceTask.class.getName());

	RacePool _rp = null;
	String _threadName = null;
	PageHelper page = null;
	DateFormat df_ymd = new SimpleDateFormat("yyyy年MM月dd日");
	Parser parser = null;
	private ProxyPool pp = null;
	ZTStd db;
	Hashtable _pageHt = null;
	String savepath = null;
	long openPageCount = 0;
	boolean isExit = false;
	int openPageFalseCount = 0;
	
	public  DateFormat DF_MMdd = new SimpleDateFormat("MMdd");
	public  DateFormat DF_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	public  DateFormat DF_yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHHmm");
	public  DateFormat DF_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	public  DateFormat DF_yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	public  DateFormat DF_yyyyMMdd_HH_mm = new SimpleDateFormat("yyyyMMdd HH:mm");
	public  DateFormat DF_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public  DateFormat DF_yyyy_MM_dd_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public  DateFormat DF_dd = new SimpleDateFormat("dd");

	public RaceTask()
	{
		db = new ZTStd();
		App.config.digester();	
		savepath = App.config.getParaHash().get("savepath").toString();
		parser = new Parser();
		if(App.EnUseProxy == true)
		{
			pp = new ProxyPool();
			pp.LoadProxy();
			page = pp.GetProxyPage();
		}
		else
		{
			page = new PageHelper();	
			try {
				String hostIp = InetAddress.getLocalHost().getHostAddress();
				logger.info(hostIp);
				if (hostIp.startsWith("192.168.60")) {
					page.setProxy("192.168.60.2", 8080);
				}
			} 
			catch (Exception e) 
			{
				logger.error("",e);
			}
		}
	}
	
	public RaceTask(String threadName,RacePool rp)
	{
		_rp = rp;
		_threadName = threadName;
		db = new ZTStd();
		parser = new Parser();
		savepath = App.config.getParaHash().get("savepath").toString();
		if(App.EnUseProxy == true)
		{
			pp = new ProxyPool();
			pp.LoadProxy();
			page = pp.GetProxyPage();
		}
		else
		{
			page = new PageHelper();
			try {
				String hostIp = InetAddress.getLocalHost().getHostAddress();
				logger.info(hostIp);
				if (hostIp.startsWith("192.168.60")) {
					page.setProxy("192.168.60.2", 8080);
				}
			} 
			catch (Exception e) 
			{
				logger.error("",e);
			}
		}
	}
	
	public RaceTask(String threadName,RacePool rp,Hashtable PageHt)
	{
		_rp = rp;
		_threadName = threadName;
		db = new ZTStd();
		parser = new Parser();
		_pageHt = PageHt;
		savepath = App.config.getParaHash().get("savepath").toString();
		if(App.EnUseProxy == true)
		{
			pp = new ProxyPool();
			pp.LoadProxy();
			page = pp.GetProxyPage();
		}
		else
		{
			page = new PageHelper();
			try {
				String hostIp = InetAddress.getLocalHost().getHostAddress();
				logger.info(hostIp);
				if (hostIp.startsWith("192.168.60")) {
					page.setProxy("192.168.60.2", 8080);
				}
			} 
			catch (Exception e) 
			{
				logger.error("",e);
			}
		}
	}
	public void runOdds(){
		try {
				for(int i=0;i<_rp.getVector().size();i++){
			//	logger.info(_rp.getVector());
				String _id = (String) _rp.getVector().get(i);
				logger.info(_threadName + " >>>>>>>>>>>>>>>>>>> start parse " + _id);
			//	StopWatch.Start(id);
				if(_id.endsWith(".html"))
				{
					this.parseFromFile(_id);
				}
				else if(_id.length()==13)
				{
					this.parseFromRaceID(_id);
				}
				else if(_id.length()<=4)
				{
					String playerid = _id;
					while(playerid.length()<4)
						playerid = "0"+playerid;
					this.parsePlayer(playerid);
				}
				else
				{
					this.parseFromID(_id);
				}
			} 
		}catch (Exception e) {
			logger.error("",e);
		}
}
	public void run()
	{
		try 
		{
			int waitseconds = Integer.parseInt(App.config.getParaHash().get("download_wait_seconds").toString());
			String id = _rp.GetID();
////			int i=0;
			while (id==null || id.equals("exit") == false){				
				if(isExit == true)
					return;
				if(id==null) {
					logger.info("############################################# url list is null,  wait "+waitseconds+"s... ##################################################");
					Thread.sleep(waitseconds*1000);
				}else{
					logger.info(_threadName + " >>>>>>>>>>>>>>>>>>> start parse " + id);
//					StopWatch.Start(id);
					if(id.endsWith(".html"))
					{
						this.parseFromFile(id);
					}
					else if(id.length()==13)
					{
						this.parseFromRaceID(id);
					}
					else if(id.length()<=4)
					{
						String playerid = id;
						while(playerid.length()<4)
							playerid = "0"+playerid;
						this.parsePlayer(playerid);
					}
					else
					{
						this.parseFromID(id);
					}
				}				
				id = _rp.GetID();
			}
		} catch (Exception e)
		{
			logger.error(e);
		}
	}
	
	private void parseFromFile(String filepath)
	{
		try
		{
			String[] array = filepath.split("/");
			if(filepath.indexOf("\\")!=-1)
				array = filepath.split("\\\\");
			String filename = array[array.length-1];
			logger.info("filename = "+filename);
			String[] filenameArray = filename.split("_");
			String yyyyMMdd = filenameArray[0];
			logger.info("yyyyMMdd = "+yyyyMMdd);
			String trackid = filenameArray[1];
			String raceno = filenameArray[2].split("\\.")[0];
			if(filepath.indexOf("RaceResult")!=-1)
			{
				this.parsePost(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(filepath));
				this.parseDividend(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(filepath));
			}
			else if(filepath.indexOf("RaceCard")!=-1)
			{
				this.parsePre(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(filepath));
			}
			else if(filepath.indexOf("FinalOdds")!=-1)
			{
				this.parseFinalOdds(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(filepath));
			}
			else if(filepath.indexOf("Odds")!=-1)
			{
				this.parseOdds(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(filepath));
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void parseFromID(String id)
	{
		try 
		{
			if(id.startsWith("FinalOdds"))
			{
				String[] array = id.split("_");
				String raceid = array[1];
				String yyyy = raceid.substring(0, 4);
				String yyyyMMdd = raceid.substring(0, 8);
				String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);		
				String trackid_ddd = raceid.substring(8,11);
				String trackid = Integer.parseInt(trackid_ddd)+"";
				String raceno_dd = raceid.substring(11);
				String raceno = Integer.parseInt(raceno_dd)+"";
				
				String raceUrl = "http://www.autorace.jp/netstadium/Odds/"+App.trackNameHt.get(trackid).toString()+"/"+yyyy_MM_dd+"_"+raceno;
				String path = savepath+CommonFun.SYS_SEPARATOR+"FinalOdds"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;				
				String filename = path+CommonFun.SYS_SEPARATOR+yyyyMMdd+"_"+trackid_ddd+"_"+raceno_dd+".html";
				
				if(new File(filename).exists() == false || App.EnOverWrite == true)
				{
					if(this.CanOpen(raceUrl, 3))
					{
						CommonFun.OutToFileByte(filename, page.getBodyBytes(), true);
						this.parseFinalOdds(yyyyMMdd, trackid_ddd, raceno_dd, page.getBody());
					}
				}
				else
				{
					String pageContent = CommonFun.ReadFile(filename);
					this.parseFinalOdds(yyyyMMdd, trackid_ddd, raceno_dd, pageContent);
				}
				
			}
			else if(id.endsWith(".html"))
			{
				String[] array = id.split("/");
				if(id.indexOf("\\")!=-1)
					array = id.split("\\\\");
				String filename = array[array.length-1];
				String[] filenameArray = filename.split("_");
				String yyyyMMdd = filenameArray[0];
				String trackid = filenameArray[1];
				String raceno = filenameArray[2].split("\\.")[0];
				
				if(id.indexOf("RaceResult")!=-1)
				{
					this.parsePost(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(id));
					this.parseDividend(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(id));
				}
				else if(id.indexOf("RaceCard")!=-1)
				{
					this.parsePre(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(id));
				}
				else if(id.indexOf("FinalOdds")!=-1)
				{
					this.parseFinalOdds(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(id));
				}
				else if(id.indexOf("Odds")!=-1)
				{
					this.parseOdds(yyyyMMdd, trackid, raceno, CommonFun.ReadFile(id));
				}
			}
			else 
			{
				String[] array = id.split("__");
				if(array.length>=4)
				{
					String yyyyMMdd = array[0];
					String trackid = array[1];
					String raceno = array[2];
					String raceUrl = array[3];
					boolean isLive = (array.length == 5);
					
					String yyyy = yyyyMMdd.substring(0,4);
					String path = savepath+CommonFun.SYS_SEPARATOR+"0TYPE0"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;				
					String filename = yyyyMMdd+"_"+trackid+"_"+raceno+".html";	
					
					if(this.CanOpen(raceUrl, 3))
					{
						if(isLive == true)
						{
							path = path.replaceFirst("0TYPE0", "PreLive");
							CommonFun.OutToFileByte(path+CommonFun.SYS_SEPARATOR+filename, page.getBodyBytes(), true);
							
							this.parsePreLive(yyyyMMdd, trackid, raceno, page.getBody());
						}						
						else if(raceUrl.indexOf("Program")!=-1)
						{
							path = path.replaceFirst("0TYPE0", "RaceCard");
							CommonFun.OutToFileByte(path+CommonFun.SYS_SEPARATOR+filename, page.getBodyBytes(), true);
							
							this.parsePre(yyyyMMdd, trackid, raceno, page.getBody());
							this.parsePreLive(yyyyMMdd, trackid, raceno, page.getBody());
						}
						else if(raceUrl.indexOf("RaceResult")!=-1)
						{
							path = path.replaceFirst("0TYPE0", "RaceResult");
							CommonFun.OutToFileByte(path+CommonFun.SYS_SEPARATOR+filename, page.getBodyBytes(), true);
							
							this.parsePost(yyyyMMdd, trackid, raceno, page.getBody());
							this.parseDividend(yyyyMMdd, trackid, raceno, page.getBody());
						}
						else if(raceUrl.indexOf("Odds")!=-1)
						{
							path = path.replaceFirst("0TYPE0", "Odds");
							filename = yyyyMMdd+"_"+trackid+"_"+raceno+"__"+DF_yyyyMMddHHmmss.format(new Date())+".html";	
							CommonFun.OutToFileByte(path+CommonFun.SYS_SEPARATOR+filename, page.getBodyBytes(), true);
							
							this.parseOdds(yyyyMMdd, trackid, raceno, page.getBody());
						}
					}
				}
			}
		} 
		catch (Exception e) 
		{
			logger.error(e);
		}
	}
	
	public   void parsePagefromLocal(String fileName){
		//20170110_006_09__20170110131044.html
//		 String fileName ="D:\\Denis\\jpMotor\\20170110_006_10__20170110132447.html";
		 String page = CommonFun.ReadFile(fileName);
		 String yyyyMMdd = CommonFun.GetStrFromPatter(fileName, "(\\d{8})", 1);
		 String trackid = CommonFun.GetStrFromPatter(fileName, "\\d{8}_(\\d{3})_", 1);
		 String raceno = CommonFun.GetStrFromPatter(fileName, "_(\\d{2})_", 1);
		 this.parseOdds(yyyyMMdd, trackid, raceno, page);
	}
	
	public static List<String> getFile(String sPathName) {
		List<String> vPathFileName = new ArrayList<String>();
		File fFile = new File(sPathName);
		File fPathFileName[] = fFile.listFiles();
		if (fPathFileName==null) return vPathFileName;
		int iFileSize = fPathFileName.length;
		for (int i = 0; i < iFileSize; i++) {
			if(fPathFileName[i].isDirectory()){
				List<String> list = getFile(fPathFileName[i].toString());
				for (String file : list) {
					vPathFileName.add(file);
				}
			} else {
				vPathFileName.add(fPathFileName[i].toString());
			}
		}
		return vPathFileName;
	}
	
	public boolean CanOpen(String url, int openCount)
	{		
		if(App.EnUseProxy==true)
		{			
			if(pp.size()==0)
			{
				logger.error("no proxy to use...");
				return false;
			}
			
			if(openPageCount%50 == 0)
			{
				page = pp.GetProxyPage();
				if(page == null)
				{
					isExit=true;
					return false;
				}
			}
			
			while(page.PageOpen(url)==false)
			{
				page = pp.GetProxyPage();
			}	
			
			openPageCount++;
			
			return true;			
		}
		else
		{
			return page.PageOpen(url);
		}	
	}
	
	private void parseOdds(String yyyyMMdd, String trackId, String raceNo, String pageContent)
	{
		try
		{
			while(trackId.length()<3)
				trackId = "0"+trackId;
			while(raceNo.length()<2)
				raceNo = "0"+raceNo;
			
			Date curTime = new Date();
			parser.setInputHTML(pageContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			if(nodelist!=null && nodelist.size()>0)
			{							
				TableTag timeLineTable = (TableTag) nodelist.elementAt(0);
				TableRow rowRaceTime = timeLineTable.getRow(2);	
				String piaostoptime = CommonFun.GetStrFromPatter(rowRaceTime.getColumns()[3].toPlainTextString().trim(),"(\\d{1,2}:\\d{2})",1);	
				String racetime = rowRaceTime.getColumns()[4].toPlainTextString().trim();
				String racedate = null;
				Date raceStartTime = null;
				if(CommonFun.GetMatcherStrGroup(racetime, "\\d{1,2}:\\d{2}").find())
				{
					if(racetime.length()==4)
						racetime = "0"+racetime;
					raceStartTime = CommonFun.DateSubHour(DF_yyyyMMdd_HH_mm.parse(yyyyMMdd+" "+racetime),-1);
				}
				
				if(piaostoptime==null)
				{
					logger.info("================================================ start parse eq final...");
					this.parseLiveEQ(yyyyMMdd, trackId, raceNo, pageContent, true, false);
					logger.info("================================================ start parse ti final...");
					this.parseLiveTi(yyyyMMdd, trackId, raceNo, pageContent, true, false);
					logger.info("================================================ start parse t final...");
					this.parseLiveT(yyyyMMdd, trackId, raceNo, pageContent, true, false);
					logger.info("================================================ start parse qw final...");
					this.parseLiveQW(yyyyMMdd, trackId, raceNo, pageContent, true, false);
				}				
				else if(raceStartTime != null && curTime.after(CommonFun.DateSubMinute(raceStartTime,-40)))
				{
					logger.info("================================================ start parse eq...");
					this.parseLiveEQ(yyyyMMdd, trackId, raceNo, pageContent, false, false);
					logger.info("================================================ start parse ti...");
					this.parseLiveTi(yyyyMMdd, trackId, raceNo, pageContent, false, false);
					logger.info("================================================ start parse t...");
					this.parseLiveT(yyyyMMdd, trackId, raceNo, pageContent, false, false);
					logger.info("================================================ start parse qw...");
					this.parseLiveQW(yyyyMMdd, trackId, raceNo, pageContent, false, false);
				} 
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private void parseFinalOdds(String yyyyMMdd, String trackId, String raceNo, String pageContent)
	{
		try
		{
			while(trackId.length()<3)
				trackId = "0"+trackId;
			while(raceNo.length()<2)
				raceNo = "0"+raceNo;
			
			logger.info("================================================ start parse final eq...");
			this.parseLiveEQ(yyyyMMdd, trackId, raceNo, pageContent, false, true);
			logger.info("================================================ start parse final ti...");
			this.parseLiveTi(yyyyMMdd, trackId, raceNo, pageContent, false, true);
			logger.info("================================================ start parse final t...");
			this.parseLiveT(yyyyMMdd, trackId, raceNo, pageContent, false, true);
			logger.info("================================================ start parse final qw...");
			this.parseLiveQW(yyyyMMdd, trackId, raceNo, pageContent, false, true);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private void parseLiveEQ(String yyyyMMdd, String trackId, String raceNo, String eqHtml, boolean isfinal, boolean isfinaltype)
	{
		try
		{
			String Replacestr = CommonFun.GetStrFromPatter(eqHtml,"<table class=\"tblMain tblBasic mg_btm_15\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">.*?<tr><td class=\"txtArea light\" width=\"100\">予想提供</td>.*?</table>",0);
			if(Replacestr!=null)
				eqHtml = eqHtml.replaceAll(Replacestr,"<div class=\"mg_btm_15\"></div>");
			
			String pageContent = CommonFun.GetStrFromPatter(eqHtml, "<div id=\"main_odds_area_\\d{1,2}\"[^<]+?>(.+?)<div class=\"mg_btm_15\"></div>(.+?)</div>", 0);
			if(pageContent==null)
			{
				return;
			}
			Date curDate = new Date();
			String trackid = trackId;
			while(trackid.length()<3)
				trackid = "0"+trackid;
			String raceno = raceNo;
			if(raceno.length()<2)
				raceno = "0"+raceno;
			
			String raceid = yyyyMMdd+trackid+raceno;
			String uraceid = yyyyMMdd+"24"+trackid+raceno;
			String HHmm = this.GetUpdateTimeHHmm(eqHtml);
			String timestamp = yyyyMMdd+HHmm;
			if(HHmm==null)
			{
				timestamp = DF_yyyyMMddHHmm.format(CommonFun.DateSubHour(new Date(), 1));
			}
//			String timestamp = df_yyyyMMddHHmm.format(CommonFun.DateSubHour(new Date(), 1));
//			logger.info(pageContent);
			parser.setInputHTML(pageContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			if(nodelist!=null && nodelist.size()>0)
			{
//				logger.info("total find "+nodelist.size()+" table");				
				TableTag eTable = null;
				TableTag qTable = null;
				TableTag ticketTable = null;
				
				if(nodelist.size()==5)
				{
					eTable = (TableTag) nodelist.elementAt(0);
					qTable = (TableTag) nodelist.elementAt(2);
					ticketTable = (TableTag) nodelist.elementAt(4);
				}				
				else if(nodelist.size()==4)
				{
					eTable = (TableTag) nodelist.elementAt(0);
					qTable = (TableTag) nodelist.elementAt(2);
					ticketTable = (TableTag) nodelist.elementAt(3);					
				}
				else
				{
					logger.error("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX eq format is wrong");
					logger.info(pageContent);
					return;
				}				
				
				if(isfinaltype == false)
				{
					AutoRaceLiveE e = new AutoRaceLiveE();
					AutoRaceLiveEId eid = new AutoRaceLiveEId();
					eid.setRaceid(Long.parseLong(raceid));
					eid.setTimeStamp(DF_yyyyMMddHHmm.parse(timestamp));
					e.setId(eid);					
					e.setCorruptedOdds(new Boolean(false));
					e.setIsFinal(new Boolean(isfinal));
					e.setExtractTime(curDate);
					e.setSales(this.GetTickets(ticketTable, "02"));
					e.setReturnSales(this.GetTickets(ticketTable, "04"));
					e.setNetsales(this.GetTickets(ticketTable, "06"));
					e.setE12(this.GetBigDecimalE(eTable, "setE12"));
					e.setE13(this.GetBigDecimalE(eTable, "setE13"));
					e.setE14(this.GetBigDecimalE(eTable, "setE14"));
					e.setE15(this.GetBigDecimalE(eTable, "setE15"));
					e.setE16(this.GetBigDecimalE(eTable, "setE16"));
					e.setE17(this.GetBigDecimalE(eTable, "setE17"));
					e.setE18(this.GetBigDecimalE(eTable, "setE18"));
					e.setE21(this.GetBigDecimalE(eTable, "setE21"));
					e.setE23(this.GetBigDecimalE(eTable, "setE23"));
					e.setE24(this.GetBigDecimalE(eTable, "setE24"));
					e.setE25(this.GetBigDecimalE(eTable, "setE25"));
					e.setE26(this.GetBigDecimalE(eTable, "setE26"));
					e.setE27(this.GetBigDecimalE(eTable, "setE27"));
					e.setE28(this.GetBigDecimalE(eTable, "setE28"));
					e.setE31(this.GetBigDecimalE(eTable, "setE31"));
					e.setE32(this.GetBigDecimalE(eTable, "setE32"));
					e.setE34(this.GetBigDecimalE(eTable, "setE34"));
					e.setE35(this.GetBigDecimalE(eTable, "setE35"));
					e.setE36(this.GetBigDecimalE(eTable, "setE36"));
					e.setE37(this.GetBigDecimalE(eTable, "setE37"));
					e.setE38(this.GetBigDecimalE(eTable, "setE38"));
					e.setE41(this.GetBigDecimalE(eTable, "setE41"));
					e.setE42(this.GetBigDecimalE(eTable, "setE42"));
					e.setE43(this.GetBigDecimalE(eTable, "setE43"));
					e.setE45(this.GetBigDecimalE(eTable, "setE45"));
					e.setE46(this.GetBigDecimalE(eTable, "setE46"));
					e.setE47(this.GetBigDecimalE(eTable, "setE47"));
					e.setE48(this.GetBigDecimalE(eTable, "setE48"));
					e.setE51(this.GetBigDecimalE(eTable, "setE51"));
					e.setE52(this.GetBigDecimalE(eTable, "setE52"));
					e.setE53(this.GetBigDecimalE(eTable, "setE53"));
					e.setE54(this.GetBigDecimalE(eTable, "setE54"));
					e.setE56(this.GetBigDecimalE(eTable, "setE56"));
					e.setE57(this.GetBigDecimalE(eTable, "setE57"));
					e.setE58(this.GetBigDecimalE(eTable, "setE58"));
					e.setE61(this.GetBigDecimalE(eTable, "setE61"));
					e.setE62(this.GetBigDecimalE(eTable, "setE62"));
					e.setE63(this.GetBigDecimalE(eTable, "setE63"));
					e.setE64(this.GetBigDecimalE(eTable, "setE64"));
					e.setE65(this.GetBigDecimalE(eTable, "setE65"));
					e.setE67(this.GetBigDecimalE(eTable, "setE67"));
					e.setE68(this.GetBigDecimalE(eTable, "setE68"));
					e.setE71(this.GetBigDecimalE(eTable, "setE71"));
					e.setE72(this.GetBigDecimalE(eTable, "setE72"));
					e.setE73(this.GetBigDecimalE(eTable, "setE73"));
					e.setE74(this.GetBigDecimalE(eTable, "setE74"));
					e.setE75(this.GetBigDecimalE(eTable, "setE75"));
					e.setE76(this.GetBigDecimalE(eTable, "setE76"));
					e.setE78(this.GetBigDecimalE(eTable, "setE78"));
					e.setE81(this.GetBigDecimalE(eTable, "setE81"));
					e.setE82(this.GetBigDecimalE(eTable, "setE82"));
					e.setE83(this.GetBigDecimalE(eTable, "setE83"));
					e.setE84(this.GetBigDecimalE(eTable, "setE84"));
					e.setE85(this.GetBigDecimalE(eTable, "setE85"));
					e.setE86(this.GetBigDecimalE(eTable, "setE86"));
					e.setE87(this.GetBigDecimalE(eTable, "setE87"));
					DBAccess.save(e);
					//"pr_AutoRace_LiveE_InsertData"
					String sql = ReflectUtil.method(e);
//					db.ExecSelectStoredProcedures("pr_AutoRace_LiveE_InsertData", sql);
					
					//-----------------------------------------------------------
					AutoRaceLiveQ q = new AutoRaceLiveQ();
					AutoRaceLiveQId qid = new AutoRaceLiveQId();
					qid.setRaceid(Long.parseLong(raceid));
					qid.setTimeStamp(DF_yyyyMMddHHmm.parse(timestamp));
					q.setId(qid);
					q.setCorruptedOdds(new Boolean(false));
					q.setIsFinal(new Boolean(false));
					q.setExtractTime(curDate);
					q.setSales(this.GetTickets(ticketTable, "12"));
					q.setReturnSales(this.GetTickets(ticketTable, "14"));
					q.setNetsales(this.GetTickets(ticketTable, "16"));
					q.setQ12(this.GetBigDecimalQ(qTable, "setQ12"));
					q.setQ13(this.GetBigDecimalQ(qTable, "setQ13"));
					q.setQ14(this.GetBigDecimalQ(qTable, "setQ14"));
					q.setQ15(this.GetBigDecimalQ(qTable, "setQ15"));
					q.setQ16(this.GetBigDecimalQ(qTable, "setQ16"));
					q.setQ17(this.GetBigDecimalQ(qTable, "setQ17"));
					q.setQ18(this.GetBigDecimalQ(qTable, "setQ18"));
					q.setQ23(this.GetBigDecimalQ(qTable, "setQ23"));
					q.setQ24(this.GetBigDecimalQ(qTable, "setQ24"));
					q.setQ25(this.GetBigDecimalQ(qTable, "setQ25"));
					q.setQ26(this.GetBigDecimalQ(qTable, "setQ26"));
					q.setQ27(this.GetBigDecimalQ(qTable, "setQ27"));
					q.setQ28(this.GetBigDecimalQ(qTable, "setQ28"));
					q.setQ34(this.GetBigDecimalQ(qTable, "setQ34"));
					q.setQ35(this.GetBigDecimalQ(qTable, "setQ35"));
					q.setQ36(this.GetBigDecimalQ(qTable, "setQ36"));
					q.setQ37(this.GetBigDecimalQ(qTable, "setQ37"));
					q.setQ38(this.GetBigDecimalQ(qTable, "setQ38"));
					q.setQ45(this.GetBigDecimalQ(qTable, "setQ45"));
					q.setQ46(this.GetBigDecimalQ(qTable, "setQ46"));
					q.setQ47(this.GetBigDecimalQ(qTable, "setQ47"));
					q.setQ48(this.GetBigDecimalQ(qTable, "setQ48"));
					q.setQ56(this.GetBigDecimalQ(qTable, "setQ56"));
					q.setQ57(this.GetBigDecimalQ(qTable, "setQ57"));
					q.setQ58(this.GetBigDecimalQ(qTable, "setQ58"));
					q.setQ67(this.GetBigDecimalQ(qTable, "setQ67"));
					q.setQ68(this.GetBigDecimalQ(qTable, "setQ68"));
					q.setQ78(this.GetBigDecimalQ(qTable, "setQ78"));
					DBAccess.save(q);
				}
				else
				{
					AutoRaceFinalE e = new AutoRaceFinalE();
					e.setRaceid(Long.parseLong(raceid));
					e.setCorruptedOdds(new Boolean(false));
					e.setExtractTime(curDate);
					e.setSales(this.GetTickets(ticketTable, "02"));
					e.setReturnSales(this.GetTickets(ticketTable, "04"));
					e.setNetSales(this.GetTickets(ticketTable, "06"));
					e.setE12(this.GetBigDecimalE(eTable, "setE12"));
					e.setE13(this.GetBigDecimalE(eTable, "setE13"));
					e.setE14(this.GetBigDecimalE(eTable, "setE14"));
					e.setE15(this.GetBigDecimalE(eTable, "setE15"));
					e.setE16(this.GetBigDecimalE(eTable, "setE16"));
					e.setE17(this.GetBigDecimalE(eTable, "setE17"));
					e.setE18(this.GetBigDecimalE(eTable, "setE18"));
					e.setE21(this.GetBigDecimalE(eTable, "setE21"));
					e.setE23(this.GetBigDecimalE(eTable, "setE23"));
					e.setE24(this.GetBigDecimalE(eTable, "setE24"));
					e.setE25(this.GetBigDecimalE(eTable, "setE25"));
					e.setE26(this.GetBigDecimalE(eTable, "setE26"));
					e.setE27(this.GetBigDecimalE(eTable, "setE27"));
					e.setE28(this.GetBigDecimalE(eTable, "setE28"));
					e.setE31(this.GetBigDecimalE(eTable, "setE31"));
					e.setE32(this.GetBigDecimalE(eTable, "setE32"));
					e.setE34(this.GetBigDecimalE(eTable, "setE34"));
					e.setE35(this.GetBigDecimalE(eTable, "setE35"));
					e.setE36(this.GetBigDecimalE(eTable, "setE36"));
					e.setE37(this.GetBigDecimalE(eTable, "setE37"));
					e.setE38(this.GetBigDecimalE(eTable, "setE38"));
					e.setE41(this.GetBigDecimalE(eTable, "setE41"));
					e.setE42(this.GetBigDecimalE(eTable, "setE42"));
					e.setE43(this.GetBigDecimalE(eTable, "setE43"));
					e.setE45(this.GetBigDecimalE(eTable, "setE45"));
					e.setE46(this.GetBigDecimalE(eTable, "setE46"));
					e.setE47(this.GetBigDecimalE(eTable, "setE47"));
					e.setE48(this.GetBigDecimalE(eTable, "setE48"));
					e.setE51(this.GetBigDecimalE(eTable, "setE51"));
					e.setE52(this.GetBigDecimalE(eTable, "setE52"));
					e.setE53(this.GetBigDecimalE(eTable, "setE53"));
					e.setE54(this.GetBigDecimalE(eTable, "setE54"));
					e.setE56(this.GetBigDecimalE(eTable, "setE56"));
					e.setE57(this.GetBigDecimalE(eTable, "setE57"));
					e.setE58(this.GetBigDecimalE(eTable, "setE58"));
					e.setE61(this.GetBigDecimalE(eTable, "setE61"));
					e.setE62(this.GetBigDecimalE(eTable, "setE62"));
					e.setE63(this.GetBigDecimalE(eTable, "setE63"));
					e.setE64(this.GetBigDecimalE(eTable, "setE64"));
					e.setE65(this.GetBigDecimalE(eTable, "setE65"));
					e.setE67(this.GetBigDecimalE(eTable, "setE67"));
					e.setE68(this.GetBigDecimalE(eTable, "setE68"));
					e.setE71(this.GetBigDecimalE(eTable, "setE71"));
					e.setE72(this.GetBigDecimalE(eTable, "setE72"));
					e.setE73(this.GetBigDecimalE(eTable, "setE73"));
					e.setE74(this.GetBigDecimalE(eTable, "setE74"));
					e.setE75(this.GetBigDecimalE(eTable, "setE75"));
					e.setE76(this.GetBigDecimalE(eTable, "setE76"));
					e.setE78(this.GetBigDecimalE(eTable, "setE78"));
					e.setE81(this.GetBigDecimalE(eTable, "setE81"));
					e.setE82(this.GetBigDecimalE(eTable, "setE82"));
					e.setE83(this.GetBigDecimalE(eTable, "setE83"));
					e.setE84(this.GetBigDecimalE(eTable, "setE84"));
					e.setE85(this.GetBigDecimalE(eTable, "setE85"));
					e.setE86(this.GetBigDecimalE(eTable, "setE86"));
					e.setE87(this.GetBigDecimalE(eTable, "setE87"));
					DBAccess.save(e);
					//"pr_AutoRace_FinalE_InsertData"
					String sql = ReflectUtil.method(e);
//					db.ExecSelectStoredProcedures("pr_AutoRace_FinalE_InsertData", sql);
					
					AutoRaceFinalQ q = new AutoRaceFinalQ();
					q.setRaceid(Long.parseLong(raceid));
					q.setCorruptedOdds(new Boolean(false));
					q.setExtractTime(curDate);
					q.setSales(this.GetTickets(ticketTable, "12"));
					q.setReturnSales(this.GetTickets(ticketTable, "14"));
					q.setNetSales(this.GetTickets(ticketTable, "16"));
					q.setQ12(this.GetBigDecimalQ(qTable, "setQ12"));
					q.setQ13(this.GetBigDecimalQ(qTable, "setQ13"));
					q.setQ14(this.GetBigDecimalQ(qTable, "setQ14"));
					q.setQ15(this.GetBigDecimalQ(qTable, "setQ15"));
					q.setQ16(this.GetBigDecimalQ(qTable, "setQ16"));
					q.setQ17(this.GetBigDecimalQ(qTable, "setQ17"));
					q.setQ18(this.GetBigDecimalQ(qTable, "setQ18"));
					q.setQ23(this.GetBigDecimalQ(qTable, "setQ23"));
					q.setQ24(this.GetBigDecimalQ(qTable, "setQ24"));
					q.setQ25(this.GetBigDecimalQ(qTable, "setQ25"));
					q.setQ26(this.GetBigDecimalQ(qTable, "setQ26"));
					q.setQ27(this.GetBigDecimalQ(qTable, "setQ27"));
					q.setQ28(this.GetBigDecimalQ(qTable, "setQ28"));
					q.setQ34(this.GetBigDecimalQ(qTable, "setQ34"));
					q.setQ35(this.GetBigDecimalQ(qTable, "setQ35"));
					q.setQ36(this.GetBigDecimalQ(qTable, "setQ36"));
					q.setQ37(this.GetBigDecimalQ(qTable, "setQ37"));
					q.setQ38(this.GetBigDecimalQ(qTable, "setQ38"));
					q.setQ45(this.GetBigDecimalQ(qTable, "setQ45"));
					q.setQ46(this.GetBigDecimalQ(qTable, "setQ46"));
					q.setQ47(this.GetBigDecimalQ(qTable, "setQ47"));
					q.setQ48(this.GetBigDecimalQ(qTable, "setQ48"));
					q.setQ56(this.GetBigDecimalQ(qTable, "setQ56"));
					q.setQ57(this.GetBigDecimalQ(qTable, "setQ57"));
					q.setQ58(this.GetBigDecimalQ(qTable, "setQ58"));
					q.setQ67(this.GetBigDecimalQ(qTable, "setQ67"));
					q.setQ68(this.GetBigDecimalQ(qTable, "setQ68"));
					q.setQ78(this.GetBigDecimalQ(qTable, "setQ78"));
					DBAccess.save(q);
					//pr_AutoRace_FinalQ_InsertData
					String sql2 = ReflectUtil.method(q);
//					db.ExecSelectStoredProcedures("pr_AutoRace_FinalQ_InsertData", sql2);
				}
			}			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private void parseLiveQW(String yyyyMMdd, String trackId, String raceNo, String eqHtml, boolean isfinal, boolean isfinaltype)
	{
		try
		{
			String Replacestr = CommonFun.GetStrFromPatter(eqHtml,"<table class=\"tblMain tblBasic mg_btm_15\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">.*?<tr><td class=\"txtArea light\" width=\"100\">予想提供</td>.*?</table>",0);
			if(Replacestr!=null)
				eqHtml = eqHtml.replaceAll(Replacestr,"<div class=\"mg_btm_15\"></div>");
			String pageContent = CommonFun.GetStrFromPatter(eqHtml, "<div id=\"tabs4-\\d{1,2}-5\" class=\"ui-tabs-hide\">(.+?)<div class=\"mg_btm_15\"></div>(.+?)</div>", 0);
			if(pageContent==null)
			{
				return;
			}
			Date curDate = new Date();
			String trackid = trackId;
			while(trackid.length()<3)
				trackid = "0"+trackid;
			String raceno = raceNo;
			if(raceno.length()<2)
				raceno = "0"+raceno;
			
			String raceid = yyyyMMdd+trackid+raceno;
			String uraceid = yyyyMMdd+"24"+trackid+raceno;
			String HHmm = this.GetUpdateTimeHHmm(eqHtml);
			String timestamp = yyyyMMdd+HHmm;
			if(HHmm==null)
				timestamp = DF_yyyyMMddHHmm.format(CommonFun.DateSubHour(new Date(), 1));
			
//			String timestamp = df_yyyyMMddHHmm.format(CommonFun.DateSubHour(new Date(), 1));
			
			Integer sales = null;
			Integer returnSales = null;
			Integer netsales = null;
			
//			logger.info(pageContent);
			parser.setInputHTML(pageContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			if(nodelist!=null && nodelist.size()>0)
			{
//				logger.info("total find "+nodelist.size()+" table");
				
				if(nodelist.size()!=2)
				{
					logger.error("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX qw format is wrong");
					logger.info(pageContent);
				}
				
				TableTag qwTable = (TableTag) nodelist.elementAt(0);
				TableTag ticketTable = (TableTag) nodelist.elementAt(1);
				
				Hashtable ht = this.GetHashtableQW(pageContent);
				
				if(isfinaltype == false)
				{
					AutoRaceLiveQw e = new AutoRaceLiveQw();
					AutoRaceLiveQwId eid = new AutoRaceLiveQwId();
					eid.setRaceid(Long.parseLong(raceid));
					eid.setTimeStamp(DF_yyyyMMddHHmm.parse(timestamp));
					e.setId(eid);
					e.setCorruptedOdds(new Boolean(false));
					e.setIsFinal(new Boolean(isfinal));
					e.setExtractTime(curDate);
					e.setSales(this.GetTickets(ticketTable, "02"));
					e.setReturnSales(this.GetTickets(ticketTable, "04"));
					e.setNetsales(this.GetTickets(ticketTable, "06"));
					
					e.setQwmax12(this.GetBigDecimalQW(ht,"setQwmax12"));
					e.setQwmax13(this.GetBigDecimalQW(ht,"setQwmax13"));
					e.setQwmax14(this.GetBigDecimalQW(ht,"setQwmax14"));
					e.setQwmax15(this.GetBigDecimalQW(ht,"setQwmax15"));
					e.setQwmax16(this.GetBigDecimalQW(ht,"setQwmax16"));
					e.setQwmax17(this.GetBigDecimalQW(ht,"setQwmax17"));
					e.setQwmax18(this.GetBigDecimalQW(ht,"setQwmax18"));
					
					e.setQwmax23(this.GetBigDecimalQW(ht,"setQwmax23"));
					e.setQwmax24(this.GetBigDecimalQW(ht,"setQwmax24"));
					e.setQwmax25(this.GetBigDecimalQW(ht,"setQwmax25"));
					e.setQwmax26(this.GetBigDecimalQW(ht,"setQwmax26"));
					e.setQwmax27(this.GetBigDecimalQW(ht,"setQwmax27"));
					e.setQwmax28(this.GetBigDecimalQW(ht,"setQwmax28"));
					
					e.setQwmax34(this.GetBigDecimalQW(ht,"setQwmax34"));
					e.setQwmax35(this.GetBigDecimalQW(ht,"setQwmax35"));
					e.setQwmax36(this.GetBigDecimalQW(ht,"setQwmax36"));
					e.setQwmax37(this.GetBigDecimalQW(ht,"setQwmax37"));
					e.setQwmax38(this.GetBigDecimalQW(ht,"setQwmax38"));
					
					e.setQwmax45(this.GetBigDecimalQW(ht,"setQwmax45"));
					e.setQwmax46(this.GetBigDecimalQW(ht,"setQwmax46"));
					e.setQwmax47(this.GetBigDecimalQW(ht,"setQwmax47"));
					e.setQwmax48(this.GetBigDecimalQW(ht,"setQwmax48"));
					
					e.setQwmax56(this.GetBigDecimalQW(ht,"setQwmax56"));
					e.setQwmax57(this.GetBigDecimalQW(ht,"setQwmax57"));
					e.setQwmax58(this.GetBigDecimalQW(ht,"setQwmax58"));
					
					e.setQwmax67(this.GetBigDecimalQW(ht,"setQwmax67"));
					e.setQwmax68(this.GetBigDecimalQW(ht,"setQwmax68"));
					
					e.setQwmax78(this.GetBigDecimalQW(ht,"setQwmax78"));
					
					
					
					e.setQwmin12(this.GetBigDecimalQW(ht,"setQwmin12"));
					e.setQwmin13(this.GetBigDecimalQW(ht,"setQwmin13"));
					e.setQwmin14(this.GetBigDecimalQW(ht,"setQwmin14"));
					e.setQwmin15(this.GetBigDecimalQW(ht,"setQwmin15"));
					e.setQwmin16(this.GetBigDecimalQW(ht,"setQwmin16"));
					e.setQwmin17(this.GetBigDecimalQW(ht,"setQwmin17"));
					e.setQwmin18(this.GetBigDecimalQW(ht,"setQwmin18"));
					
					e.setQwmin23(this.GetBigDecimalQW(ht,"setQwmin23"));
					e.setQwmin24(this.GetBigDecimalQW(ht,"setQwmin24"));
					e.setQwmin25(this.GetBigDecimalQW(ht,"setQwmin25"));
					e.setQwmin26(this.GetBigDecimalQW(ht,"setQwmin26"));
					e.setQwmin27(this.GetBigDecimalQW(ht,"setQwmin27"));
					e.setQwmin28(this.GetBigDecimalQW(ht,"setQwmin28"));
					
					e.setQwmin34(this.GetBigDecimalQW(ht,"setQwmin34"));
					e.setQwmin35(this.GetBigDecimalQW(ht,"setQwmin35"));
					e.setQwmin36(this.GetBigDecimalQW(ht,"setQwmin36"));
					e.setQwmin37(this.GetBigDecimalQW(ht,"setQwmin37"));
					e.setQwmin38(this.GetBigDecimalQW(ht,"setQwmin38"));
					
					e.setQwmin45(this.GetBigDecimalQW(ht,"setQwmin45"));
					e.setQwmin46(this.GetBigDecimalQW(ht,"setQwmin46"));
					e.setQwmin47(this.GetBigDecimalQW(ht,"setQwmin47"));
					e.setQwmin48(this.GetBigDecimalQW(ht,"setQwmin48"));
					
					e.setQwmin56(this.GetBigDecimalQW(ht,"setQwmin56"));
					e.setQwmin57(this.GetBigDecimalQW(ht,"setQwmin57"));
					e.setQwmin58(this.GetBigDecimalQW(ht,"setQwmin58"));
					
					e.setQwmin67(this.GetBigDecimalQW(ht,"setQwmin67"));
					e.setQwmin68(this.GetBigDecimalQW(ht,"setQwmin68"));
					
					e.setQwmin78(this.GetBigDecimalQW(ht,"setQwmin78"));
					
					DBAccess.save(e);
				}
				else
				{
					AutoRaceFinalQw e = new AutoRaceFinalQw();
					e.setRaceid(Long.parseLong(raceid));
					e.setCorruptedOdds(new Boolean(false));
					e.setExtractTime(curDate);
					e.setSales(this.GetTickets(ticketTable, "02"));
					e.setReturnSales(this.GetTickets(ticketTable, "04"));
					e.setNetSales(this.GetTickets(ticketTable, "06"));
					
					e.setQwmax12(this.GetBigDecimalQW(ht,"setQwmax12"));
					e.setQwmax13(this.GetBigDecimalQW(ht,"setQwmax13"));
					e.setQwmax14(this.GetBigDecimalQW(ht,"setQwmax14"));
					e.setQwmax15(this.GetBigDecimalQW(ht,"setQwmax15"));
					e.setQwmax16(this.GetBigDecimalQW(ht,"setQwmax16"));
					e.setQwmax17(this.GetBigDecimalQW(ht,"setQwmax17"));
					e.setQwmax18(this.GetBigDecimalQW(ht,"setQwmax18"));
					
					e.setQwmax23(this.GetBigDecimalQW(ht,"setQwmax23"));
					e.setQwmax24(this.GetBigDecimalQW(ht,"setQwmax24"));
					e.setQwmax25(this.GetBigDecimalQW(ht,"setQwmax25"));
					e.setQwmax26(this.GetBigDecimalQW(ht,"setQwmax26"));
					e.setQwmax27(this.GetBigDecimalQW(ht,"setQwmax27"));
					e.setQwmax28(this.GetBigDecimalQW(ht,"setQwmax28"));
					
					e.setQwmax34(this.GetBigDecimalQW(ht,"setQwmax34"));
					e.setQwmax35(this.GetBigDecimalQW(ht,"setQwmax35"));
					e.setQwmax36(this.GetBigDecimalQW(ht,"setQwmax36"));
					e.setQwmax37(this.GetBigDecimalQW(ht,"setQwmax37"));
					e.setQwmax38(this.GetBigDecimalQW(ht,"setQwmax38"));
					
					e.setQwmax45(this.GetBigDecimalQW(ht,"setQwmax45"));
					e.setQwmax46(this.GetBigDecimalQW(ht,"setQwmax46"));
					e.setQwmax47(this.GetBigDecimalQW(ht,"setQwmax47"));
					e.setQwmax48(this.GetBigDecimalQW(ht,"setQwmax48"));
					
					e.setQwmax56(this.GetBigDecimalQW(ht,"setQwmax56"));
					e.setQwmax57(this.GetBigDecimalQW(ht,"setQwmax57"));
					e.setQwmax58(this.GetBigDecimalQW(ht,"setQwmax58"));
					
					e.setQwmax67(this.GetBigDecimalQW(ht,"setQwmax67"));
					e.setQwmax68(this.GetBigDecimalQW(ht,"setQwmax68"));
					
					e.setQwmax78(this.GetBigDecimalQW(ht,"setQwmax78"));
					
					e.setQwmin12(this.GetBigDecimalQW(ht,"setQwmin12"));
					e.setQwmin13(this.GetBigDecimalQW(ht,"setQwmin13"));
					e.setQwmin14(this.GetBigDecimalQW(ht,"setQwmin14"));
					e.setQwmin15(this.GetBigDecimalQW(ht,"setQwmin15"));
					e.setQwmin16(this.GetBigDecimalQW(ht,"setQwmin16"));
					e.setQwmin17(this.GetBigDecimalQW(ht,"setQwmin17"));
					e.setQwmin18(this.GetBigDecimalQW(ht,"setQwmin18"));
					
					e.setQwmin23(this.GetBigDecimalQW(ht,"setQwmin23"));
					e.setQwmin24(this.GetBigDecimalQW(ht,"setQwmin24"));
					e.setQwmin25(this.GetBigDecimalQW(ht,"setQwmin25"));
					e.setQwmin26(this.GetBigDecimalQW(ht,"setQwmin26"));
					e.setQwmin27(this.GetBigDecimalQW(ht,"setQwmin27"));
					e.setQwmin28(this.GetBigDecimalQW(ht,"setQwmin28"));
					
					e.setQwmin34(this.GetBigDecimalQW(ht,"setQwmin34"));
					e.setQwmin35(this.GetBigDecimalQW(ht,"setQwmin35"));
					e.setQwmin36(this.GetBigDecimalQW(ht,"setQwmin36"));
					e.setQwmin37(this.GetBigDecimalQW(ht,"setQwmin37"));
					e.setQwmin38(this.GetBigDecimalQW(ht,"setQwmin38"));
					
					e.setQwmin45(this.GetBigDecimalQW(ht,"setQwmin45"));
					e.setQwmin46(this.GetBigDecimalQW(ht,"setQwmin46"));
					e.setQwmin47(this.GetBigDecimalQW(ht,"setQwmin47"));
					e.setQwmin48(this.GetBigDecimalQW(ht,"setQwmin48"));
					
					e.setQwmin56(this.GetBigDecimalQW(ht,"setQwmin56"));
					e.setQwmin57(this.GetBigDecimalQW(ht,"setQwmin57"));
					e.setQwmin58(this.GetBigDecimalQW(ht,"setQwmin58"));
					
					e.setQwmin67(this.GetBigDecimalQW(ht,"setQwmin67"));
					e.setQwmin68(this.GetBigDecimalQW(ht,"setQwmin68"));
					
					e.setQwmin78(this.GetBigDecimalQW(ht,"setQwmin78"));
					
					DBAccess.save(e);
				}
			}			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private void parseLiveTi(String yyyyMMdd, String trackId, String raceNo, String pageContent, boolean isfinal, boolean isfinaltype)
	{
		try
		{
//			logger.info(pageContent);
			Hashtable ht = this.GetHashtableTi(pageContent);
			if(ht.size()==0)
			{
				return;
			}
			Date curDate = new Date();
			String trackid = trackId;
			while(trackid.length()<3)
				trackid = "0"+trackid;
			String raceno = raceNo;
			if(raceno.length()<2)
				raceno = "0"+raceno;		
			
			String raceid = yyyyMMdd+trackid+raceno;
			String uraceid = yyyyMMdd+"24"+trackid+raceno;
			String HHmm = this.GetUpdateTimeHHmm(pageContent);
			String timestamp = yyyyMMdd+HHmm;
			if(HHmm==null)
				timestamp = DF_yyyyMMddHHmm.format(CommonFun.DateSubHour(new Date(), 1));
			
//			String timestamp = df_yyyyMMddHHmm.format(CommonFun.DateSubHour(new Date(), 1));
			
			Integer sales = null;
			Integer returnSales = null;
			Integer netsales = null;
			
//			String tableContent = CommonFun.GetStrFromPatter(pageContent, "<div id=\"tabs6-\\d{1,2}-1\" class=\"ui-tabs-hide\">.+?</div>.*?<div class=\"mg_btm_15\"></div>(.+?)</div>", 0);
			String tableContent = CommonFun.GetStrFromPatter(pageContent, "<div id=\"tabs6-\\d{1,2}-1\" class=\"ui-tabs-hide\">.+?</div>.*?<table.+?><tr id=\"seles_info_trifecta-\\d{1,2}\">.+?</table></div>", 0);
			parser.setInputHTML(tableContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			
			if(nodelist!=null && nodelist.size()>0)
			{
				TableTag table = (TableTag) nodelist.elementAt(1);
				TableTag ticketTable = (TableTag) nodelist.elementAt(nodelist.size()-1);
				sales = this.GetTickets(ticketTable, "02");
				returnSales = this.GetTickets(ticketTable, "04");
				netsales = this.GetTickets(ticketTable, "06");
			}		
			
			for(int i=1;i<=8;i++)
			{
				if(isfinaltype == false)
				{
					AutoRaceLiveTi t = new AutoRaceLiveTi();
					AutoRaceLiveTiId tid = new AutoRaceLiveTiId();
					tid.setRaceId(Long.parseLong(raceid));
					tid.setTimeStamp(DF_yyyyMMddHHmm.parse(timestamp));
					tid.setClothNo(Byte.parseByte(i+""));
					t.setId(tid);
					t.setCorruptedOdds(new Boolean(false));
					t.setIsFinal(new Boolean(isfinal));
					t.setExtractTime(curDate);
					t.setSales(sales);
					t.setReturnSales(returnSales);
					t.setNetsales(netsales);
					
					t.setH12(this.GetBigDecimalTi(ht, i+"12"));
					t.setH13(this.GetBigDecimalTi(ht, i+"13"));
					t.setH14(this.GetBigDecimalTi(ht, i+"14"));
					t.setH15(this.GetBigDecimalTi(ht, i+"15"));
					t.setH16(this.GetBigDecimalTi(ht, i+"16"));
					t.setH17(this.GetBigDecimalTi(ht, i+"17"));
					t.setH18(this.GetBigDecimalTi(ht, i+"18"));
		
					t.setH21(this.GetBigDecimalTi(ht, i+"21"));
					t.setH23(this.GetBigDecimalTi(ht, i+"23"));
					t.setH24(this.GetBigDecimalTi(ht, i+"24"));
					t.setH25(this.GetBigDecimalTi(ht, i+"25"));
					t.setH26(this.GetBigDecimalTi(ht, i+"26"));
					t.setH27(this.GetBigDecimalTi(ht, i+"27"));
					t.setH28(this.GetBigDecimalTi(ht, i+"28"));
		
					t.setH31(this.GetBigDecimalTi(ht, i+"31"));
					t.setH32(this.GetBigDecimalTi(ht, i+"32"));
					t.setH34(this.GetBigDecimalTi(ht, i+"34"));
					t.setH35(this.GetBigDecimalTi(ht, i+"35"));
					t.setH36(this.GetBigDecimalTi(ht, i+"36"));
					t.setH37(this.GetBigDecimalTi(ht, i+"37"));
					t.setH38(this.GetBigDecimalTi(ht, i+"38"));
		
					t.setH41(this.GetBigDecimalTi(ht, i+"41"));
					t.setH42(this.GetBigDecimalTi(ht, i+"42"));
					t.setH43(this.GetBigDecimalTi(ht, i+"43"));
					t.setH45(this.GetBigDecimalTi(ht, i+"45"));
					t.setH46(this.GetBigDecimalTi(ht, i+"46"));
					t.setH47(this.GetBigDecimalTi(ht, i+"47"));
					t.setH48(this.GetBigDecimalTi(ht, i+"48"));
		
					t.setH51(this.GetBigDecimalTi(ht, i+"51"));
					t.setH52(this.GetBigDecimalTi(ht, i+"52"));
					t.setH53(this.GetBigDecimalTi(ht, i+"53"));
					t.setH54(this.GetBigDecimalTi(ht, i+"54"));
					t.setH56(this.GetBigDecimalTi(ht, i+"56"));
					t.setH57(this.GetBigDecimalTi(ht, i+"57"));
					t.setH58(this.GetBigDecimalTi(ht, i+"58"));
		
					t.setH61(this.GetBigDecimalTi(ht, i+"61"));
					t.setH62(this.GetBigDecimalTi(ht, i+"62"));
					t.setH63(this.GetBigDecimalTi(ht, i+"63"));
					t.setH64(this.GetBigDecimalTi(ht, i+"64"));
					t.setH65(this.GetBigDecimalTi(ht, i+"65"));
					t.setH67(this.GetBigDecimalTi(ht, i+"67"));
					t.setH68(this.GetBigDecimalTi(ht, i+"68"));
		
					t.setH71(this.GetBigDecimalTi(ht, i+"71"));
					t.setH72(this.GetBigDecimalTi(ht, i+"72"));
					t.setH73(this.GetBigDecimalTi(ht, i+"73"));
					t.setH74(this.GetBigDecimalTi(ht, i+"74"));
					t.setH75(this.GetBigDecimalTi(ht, i+"75"));
					t.setH76(this.GetBigDecimalTi(ht, i+"76"));
					t.setH78(this.GetBigDecimalTi(ht, i+"78"));
		
					t.setH81(this.GetBigDecimalTi(ht, i+"81"));
					t.setH82(this.GetBigDecimalTi(ht, i+"82"));
					t.setH83(this.GetBigDecimalTi(ht, i+"83"));
					t.setH84(this.GetBigDecimalTi(ht, i+"84"));
					t.setH85(this.GetBigDecimalTi(ht, i+"85"));
					t.setH86(this.GetBigDecimalTi(ht, i+"86"));
					t.setH87(this.GetBigDecimalTi(ht, i+"87"));
		
					DBAccess.save(t);
					String sql = ReflectUtil.method(t);
//					db.ExecStoredProcedures("pr_AutoRace_LiveTi_InsertData",sql);
					
					db.ExecStoredProcedures("pr_Consolidate_Log_tiWprob_Live", raceid+",'"+DF_yyyy_MM_dd_HH_mm_ss.format(tid.getTimeStamp())+"'");
					logger.info("success exec sp:  pr_Consolidate_Log_tiWprob_Live "+raceid+",'"+DF_yyyy_MM_dd_HH_mm_ss.format(tid.getTimeStamp())+"'");
				}
				else
				{
					AutoRaceFinalTi t = new AutoRaceFinalTi();
					AutoRaceFinalTiId tid = new AutoRaceFinalTiId();
					tid.setRaceId(Long.parseLong(raceid));
					tid.setClothNo(Byte.parseByte(i+""));
					t.setId(tid);
					t.setCorruptedOdds(new Boolean(false));
					t.setExtractTime(curDate);
					t.setSales(sales);
					t.setReturnSales(returnSales);
					t.setNetSales(netsales);
					
					t.setH12(this.GetBigDecimalTi(ht, i+"12"));
					t.setH13(this.GetBigDecimalTi(ht, i+"13"));
					t.setH14(this.GetBigDecimalTi(ht, i+"14"));
					t.setH15(this.GetBigDecimalTi(ht, i+"15"));
					t.setH16(this.GetBigDecimalTi(ht, i+"16"));
					t.setH17(this.GetBigDecimalTi(ht, i+"17"));
					t.setH18(this.GetBigDecimalTi(ht, i+"18"));
		
					t.setH21(this.GetBigDecimalTi(ht, i+"21"));
					t.setH23(this.GetBigDecimalTi(ht, i+"23"));
					t.setH24(this.GetBigDecimalTi(ht, i+"24"));
					t.setH25(this.GetBigDecimalTi(ht, i+"25"));
					t.setH26(this.GetBigDecimalTi(ht, i+"26"));
					t.setH27(this.GetBigDecimalTi(ht, i+"27"));
					t.setH28(this.GetBigDecimalTi(ht, i+"28"));
		
					t.setH31(this.GetBigDecimalTi(ht, i+"31"));
					t.setH32(this.GetBigDecimalTi(ht, i+"32"));
					t.setH34(this.GetBigDecimalTi(ht, i+"34"));
					t.setH35(this.GetBigDecimalTi(ht, i+"35"));
					t.setH36(this.GetBigDecimalTi(ht, i+"36"));
					t.setH37(this.GetBigDecimalTi(ht, i+"37"));
					t.setH38(this.GetBigDecimalTi(ht, i+"38"));
		
					t.setH41(this.GetBigDecimalTi(ht, i+"41"));
					t.setH42(this.GetBigDecimalTi(ht, i+"42"));
					t.setH43(this.GetBigDecimalTi(ht, i+"43"));
					t.setH45(this.GetBigDecimalTi(ht, i+"45"));
					t.setH46(this.GetBigDecimalTi(ht, i+"46"));
					t.setH47(this.GetBigDecimalTi(ht, i+"47"));
					t.setH48(this.GetBigDecimalTi(ht, i+"48"));
		
					t.setH51(this.GetBigDecimalTi(ht, i+"51"));
					t.setH52(this.GetBigDecimalTi(ht, i+"52"));
					t.setH53(this.GetBigDecimalTi(ht, i+"53"));
					t.setH54(this.GetBigDecimalTi(ht, i+"54"));
					t.setH56(this.GetBigDecimalTi(ht, i+"56"));
					t.setH57(this.GetBigDecimalTi(ht, i+"57"));
					t.setH58(this.GetBigDecimalTi(ht, i+"58"));
		
					t.setH61(this.GetBigDecimalTi(ht, i+"61"));
					t.setH62(this.GetBigDecimalTi(ht, i+"62"));
					t.setH63(this.GetBigDecimalTi(ht, i+"63"));
					t.setH64(this.GetBigDecimalTi(ht, i+"64"));
					t.setH65(this.GetBigDecimalTi(ht, i+"65"));
					t.setH67(this.GetBigDecimalTi(ht, i+"67"));
					t.setH68(this.GetBigDecimalTi(ht, i+"68"));
		
					t.setH71(this.GetBigDecimalTi(ht, i+"71"));
					t.setH72(this.GetBigDecimalTi(ht, i+"72"));
					t.setH73(this.GetBigDecimalTi(ht, i+"73"));
					t.setH74(this.GetBigDecimalTi(ht, i+"74"));
					t.setH75(this.GetBigDecimalTi(ht, i+"75"));
					t.setH76(this.GetBigDecimalTi(ht, i+"76"));
					t.setH78(this.GetBigDecimalTi(ht, i+"78"));
		
					t.setH81(this.GetBigDecimalTi(ht, i+"81"));
					t.setH82(this.GetBigDecimalTi(ht, i+"82"));
					t.setH83(this.GetBigDecimalTi(ht, i+"83"));
					t.setH84(this.GetBigDecimalTi(ht, i+"84"));
					t.setH85(this.GetBigDecimalTi(ht, i+"85"));
					t.setH86(this.GetBigDecimalTi(ht, i+"86"));
					t.setH87(this.GetBigDecimalTi(ht, i+"87"));
		
					DBAccess.save(t);
//					String sql = ReflectUtil.method(t);
//					db.ExecStoredProcedures("pr_AutoRace_FinalTi_InsertData",sql);
				}
			}	
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private void parseLiveT(String yyyyMMdd, String trackId, String raceNo, String pageContent, boolean isfinal, boolean isfinaltype)
	{
		try
		{
			Hashtable ht = this.GetHashtableT(pageContent);	
			if(ht.size()==0)
				return;
			
			Date curDate = new Date();
			String trackid = trackId;
			while(trackid.length()<3)
				trackid = "0"+trackid;
			String raceno = raceNo;
			if(raceno.length()<2)
				raceno = "0"+raceno;		
			
			String raceid = yyyyMMdd+trackid+raceno;
			String uraceid = yyyyMMdd+"24"+trackid+raceno;
			String HHmm = this.GetUpdateTimeHHmm(pageContent);
			String timestamp = yyyyMMdd+HHmm;
			if(HHmm==null)
				timestamp = DF_yyyyMMddHHmm.format(CommonFun.DateSubHour(new Date(), 1));
			
//			String timestamp = DF_yyyyMMddHHmm.format(CommonFun.DateSubHour(new Date(), 1));
			
			Integer sales = null;
			Integer returnSales = null;
			Integer netsales = null;
			
//			String tableContent = CommonFun.GetStrFromPatter(pageContent, "<div id=\"tabs7-\\d{1,2}-1\" class=\"ui-tabs-hide\">.+?</div>.*?<div class=\"mg_btm_15\"></div>(.+?)</div>", 0);
			String tableContent = CommonFun.GetStrFromPatter(pageContent, "<div id=\"tabs7-\\d{1,2}-1\" class=\"ui-tabs-hide\">.+?</div>.+?<table.+?><tr id=\"seles_info_trio-\\d{1,2}\">.+?</table></div>", 0);
			parser.setInputHTML(tableContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			
			if(nodelist!=null && nodelist.size()>0)
			{
				TableTag ticketTable = (TableTag) nodelist.elementAt(nodelist.size()-1);
				sales = this.GetTickets(ticketTable, "02");
				returnSales = this.GetTickets(ticketTable, "04");
				netsales = this.GetTickets(ticketTable, "06");
			}		

			if(isfinaltype == false)
			{
				AutoRaceLiveT t = new AutoRaceLiveT();
				AutoRaceLiveTId tid = new AutoRaceLiveTId();
				tid.setRaceId(Long.parseLong(raceid));
				tid.setTimeStamp(DF_yyyyMMddHHmm.parse(timestamp));
				t.setId(tid);				
				t.setCorruptedOdds(new Boolean(false));
				t.setIsFinal(new Boolean(isfinal));
				t.setExtractTime(curDate);
				t.setSales(sales);
				t.setReturnSales(returnSales);
				t.setNetsales(netsales);
				
				t.setH123(this.GetBigDecimalT(ht, "setH123"));
				t.setH124(this.GetBigDecimalT(ht, "setH124"));
				t.setH125(this.GetBigDecimalT(ht, "setH125"));
				t.setH126(this.GetBigDecimalT(ht, "setH126"));
				t.setH127(this.GetBigDecimalT(ht, "setH127"));
				t.setH128(this.GetBigDecimalT(ht, "setH128"));
	
				t.setH134(this.GetBigDecimalT(ht, "setH134"));
				t.setH135(this.GetBigDecimalT(ht, "setH135"));
				t.setH136(this.GetBigDecimalT(ht, "setH136"));
				t.setH137(this.GetBigDecimalT(ht, "setH137"));
				t.setH138(this.GetBigDecimalT(ht, "setH138"));
				
				t.setH145(this.GetBigDecimalT(ht, "setH145"));
				t.setH146(this.GetBigDecimalT(ht, "setH146"));
				t.setH147(this.GetBigDecimalT(ht, "setH147"));
				t.setH148(this.GetBigDecimalT(ht, "setH148"));
	
				t.setH156(this.GetBigDecimalT(ht, "setH156"));
				t.setH157(this.GetBigDecimalT(ht, "setH157"));
				t.setH158(this.GetBigDecimalT(ht, "setH158"));
	
				t.setH167(this.GetBigDecimalT(ht, "setH167"));
				t.setH168(this.GetBigDecimalT(ht, "setH168"));
	
				t.setH178(this.GetBigDecimalT(ht, "setH178"));
				
				t.setH234(this.GetBigDecimalT(ht, "setH234"));
				t.setH235(this.GetBigDecimalT(ht, "setH235"));
				t.setH236(this.GetBigDecimalT(ht, "setH236"));
				t.setH237(this.GetBigDecimalT(ht, "setH237"));
				t.setH238(this.GetBigDecimalT(ht, "setH238"));
				
				t.setH245(this.GetBigDecimalT(ht, "setH245"));
				t.setH246(this.GetBigDecimalT(ht, "setH246"));
				t.setH247(this.GetBigDecimalT(ht, "setH247"));
				t.setH248(this.GetBigDecimalT(ht, "setH248"));
				
				t.setH256(this.GetBigDecimalT(ht, "setH256"));
				t.setH257(this.GetBigDecimalT(ht, "setH257"));
				t.setH258(this.GetBigDecimalT(ht, "setH258"));
				
				t.setH267(this.GetBigDecimalT(ht, "setH267"));	
				t.setH268(this.GetBigDecimalT(ht, "setH268"));
				
				t.setH278(this.GetBigDecimalT(ht, "setH278"));
				
				t.setH345(this.GetBigDecimalT(ht, "setH345"));
				t.setH346(this.GetBigDecimalT(ht, "setH346"));
				t.setH347(this.GetBigDecimalT(ht, "setH347"));
				t.setH348(this.GetBigDecimalT(ht, "setH348"));
				
				t.setH356(this.GetBigDecimalT(ht, "setH356"));
				t.setH357(this.GetBigDecimalT(ht, "setH357"));
				t.setH358(this.GetBigDecimalT(ht, "setH358"));
				
				t.setH367(this.GetBigDecimalT(ht, "setH367"));
				t.setH368(this.GetBigDecimalT(ht, "setH368"));
				
				t.setH378(this.GetBigDecimalT(ht, "setH378"));
				
				t.setH456(this.GetBigDecimalT(ht, "setH456"));
				t.setH457(this.GetBigDecimalT(ht, "setH457"));
				t.setH458(this.GetBigDecimalT(ht, "setH458"));
				
				t.setH467(this.GetBigDecimalT(ht, "setH467"));
				t.setH468(this.GetBigDecimalT(ht, "setH468"));
				
				t.setH478(this.GetBigDecimalT(ht, "setH478"));
				
				t.setH567(this.GetBigDecimalT(ht, "setH567"));
				t.setH568(this.GetBigDecimalT(ht, "setH568"));
				
				t.setH578(this.GetBigDecimalT(ht, "setH578"));
				
				t.setH678(this.GetBigDecimalT(ht, "setH678"));
	
				DBAccess.save(t);
				
			}
			else
			{
				AutoRaceFinalT t = new AutoRaceFinalT();
				t.setRaceId(Long.parseLong(raceid));
				t.setCorruptedOdds(new Boolean(false));
				t.setExtractTime(curDate);
				t.setSales(sales);
				t.setReturnSales(returnSales);
				t.setNetSales(netsales);
				
				t.setH123(this.GetBigDecimalT(ht, "setH123"));
				t.setH124(this.GetBigDecimalT(ht, "setH124"));
				t.setH125(this.GetBigDecimalT(ht, "setH125"));
				t.setH126(this.GetBigDecimalT(ht, "setH126"));
				t.setH127(this.GetBigDecimalT(ht, "setH127"));
				t.setH128(this.GetBigDecimalT(ht, "setH128"));

				t.setH134(this.GetBigDecimalT(ht, "setH134"));
				t.setH135(this.GetBigDecimalT(ht, "setH135"));
				t.setH136(this.GetBigDecimalT(ht, "setH136"));
				t.setH137(this.GetBigDecimalT(ht, "setH137"));
				t.setH138(this.GetBigDecimalT(ht, "setH138"));
				
				t.setH145(this.GetBigDecimalT(ht, "setH145"));
				t.setH146(this.GetBigDecimalT(ht, "setH146"));
				t.setH147(this.GetBigDecimalT(ht, "setH147"));
				t.setH148(this.GetBigDecimalT(ht, "setH148"));

				t.setH156(this.GetBigDecimalT(ht, "setH156"));
				t.setH157(this.GetBigDecimalT(ht, "setH157"));
				t.setH158(this.GetBigDecimalT(ht, "setH158"));

				t.setH167(this.GetBigDecimalT(ht, "setH167"));
				t.setH168(this.GetBigDecimalT(ht, "setH168"));

				t.setH178(this.GetBigDecimalT(ht, "setH178"));
				
				t.setH234(this.GetBigDecimalT(ht, "setH234"));
				t.setH235(this.GetBigDecimalT(ht, "setH235"));
				t.setH236(this.GetBigDecimalT(ht, "setH236"));
				t.setH237(this.GetBigDecimalT(ht, "setH237"));
				t.setH238(this.GetBigDecimalT(ht, "setH238"));
				
				t.setH245(this.GetBigDecimalT(ht, "setH245"));
				t.setH246(this.GetBigDecimalT(ht, "setH246"));
				t.setH247(this.GetBigDecimalT(ht, "setH247"));
				t.setH248(this.GetBigDecimalT(ht, "setH248"));
				
				t.setH256(this.GetBigDecimalT(ht, "setH256"));
				t.setH257(this.GetBigDecimalT(ht, "setH257"));
				t.setH258(this.GetBigDecimalT(ht, "setH258"));
				
				t.setH267(this.GetBigDecimalT(ht, "setH267"));	
				t.setH268(this.GetBigDecimalT(ht, "setH268"));
				
				t.setH278(this.GetBigDecimalT(ht, "setH278"));
				
				t.setH345(this.GetBigDecimalT(ht, "setH345"));
				t.setH346(this.GetBigDecimalT(ht, "setH346"));
				t.setH347(this.GetBigDecimalT(ht, "setH347"));
				t.setH348(this.GetBigDecimalT(ht, "setH348"));
				
				t.setH356(this.GetBigDecimalT(ht, "setH356"));
				t.setH357(this.GetBigDecimalT(ht, "setH357"));
				t.setH358(this.GetBigDecimalT(ht, "setH358"));
				
				t.setH367(this.GetBigDecimalT(ht, "setH367"));
				t.setH368(this.GetBigDecimalT(ht, "setH368"));
				
				t.setH378(this.GetBigDecimalT(ht, "setH378"));
				
				t.setH456(this.GetBigDecimalT(ht, "setH456"));
				t.setH457(this.GetBigDecimalT(ht, "setH457"));
				t.setH458(this.GetBigDecimalT(ht, "setH458"));
				
				t.setH467(this.GetBigDecimalT(ht, "setH467"));
				t.setH468(this.GetBigDecimalT(ht, "setH468"));
				
				t.setH478(this.GetBigDecimalT(ht, "setH478"));
				
				t.setH567(this.GetBigDecimalT(ht, "setH567"));
				t.setH568(this.GetBigDecimalT(ht, "setH568"));
				
				t.setH578(this.GetBigDecimalT(ht, "setH578"));
				
				t.setH678(this.GetBigDecimalT(ht, "setH678"));

				DBAccess.save(t);
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private Hashtable GetHashtableT(String pageContent)
	{
		Hashtable ht = new Hashtable();
		
		try
		{
			
			String patter = "<div id=\"tabs7-\\d{1,2}-(\\d)\" class=\"ui-tabs-hide\">.+?</div>.*?</div>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(pageContent, patter);
			while(matcher.find())
			{
				String x = matcher.group(1);
				parser.setInputHTML(matcher.group());
				NodeFilter filter_tab = new TagNameFilter("table");
				NodeList nodelist = parser.parse(filter_tab);
				if(nodelist!=null && nodelist.size()>0)
				{
//					logger.info("total find "+nodelist.size()+" table");

					TableTag tiTable = (TableTag) nodelist.elementAt(0);
					if(x.equals("1"))
					{
						tiTable = (TableTag) nodelist.elementAt(1);
					}
					Vector yV = new Vector();
					TableRow[] rows = tiTable.getRows();
					
					TableColumn[] yCols = rows[1].getColumns();
					for(int i=0;i<yCols.length;i++)
					{
						String y = yCols[i].toPlainTextString();
						if(CommonFun.isNumber(y))
							yV.add(y);
					}
					
					for(int i=2;i<rows.length;i++)
					{
						TableColumn[] zCols = rows[i].getColumns();
						for(int j=0;j<yV.size();j++)
						{
							String y = yV.get(j).toString();
							String z = zCols[j*2].toPlainTextString();
							String value = zCols[j*2+1].toPlainTextString();
							ht.put(x+y+z, value);
//							logger.info(x+y+z+" ::: "+value);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return ht;
	}
	
	private Hashtable GetHashtableQW(String pageContent)
	{
		Hashtable ht = new Hashtable();
		
		try
		{
			
			String patter = "<div id=\"tabs4-\\d{1,2}-5\" class=\"ui-tabs-hide\">.+?</div>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(pageContent, patter);
			if(matcher.find())
			{
				parser.setInputHTML(matcher.group());
				NodeFilter filter_tab = new TagNameFilter("table");
				NodeList nodelist = parser.parse(filter_tab);
				if(nodelist!=null && nodelist.size()>0)
				{
					TableTag qwTable = (TableTag) nodelist.elementAt(0);					
					TableRow[] rows = qwTable.getRows();					
					
					for(int i=0;i<rows.length;i++)
					{
						TableColumn[] yCols = rows[i].getColumns();
						for(int j=1;j<=8;j++)
						{
							String y = yCols[(j-1)*2].toPlainTextString();
							String value = yCols[(j-1)*2+1].toPlainTextString();
							ht.put(j+y, value);
//							logger.info(j+y+" ::: "+value);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return ht;
	}
	
	private Hashtable GetHashtableTi(String pageContent)
	{
		Hashtable ht = new Hashtable();
		
		try
		{
			
			String patter = "<div id=\"tabs6-\\d{1,2}-(\\d)\" class=\"ui-tabs-hide\">.+?</div>.*?</div>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(pageContent, patter);
			while(matcher.find())
			{
				String x = matcher.group(1);
				parser.setInputHTML(matcher.group());
				NodeFilter filter_tab = new TagNameFilter("table");
				NodeList nodelist = parser.parse(filter_tab);
				if(nodelist!=null && nodelist.size()>0)
				{
//					logger.info("total find "+nodelist.size()+" table");

					TableTag tiTable = (TableTag) nodelist.elementAt(0);
					if(x.equals("1"))
					{
						tiTable = (TableTag) nodelist.elementAt(1);
					}
					Vector yV = new Vector();
					TableRow[] rows = tiTable.getRows();
					
					TableColumn[] yCols = rows[1].getColumns();
					for(int i=0;i<yCols.length;i++)
					{
						String y = yCols[i].toPlainTextString();
						if(CommonFun.isNumber(y))
							yV.add(y);
					}
					
					for(int i=2;i<rows.length;i++)
					{
						TableColumn[] zCols = rows[i].getColumns();
						for(int j=0;j<yV.size();j++)
						{
							String y = yV.get(j).toString();
							String z = zCols[j*2].toPlainTextString();
							String value = zCols[j*2+1].toPlainTextString();
							ht.put(x+y+z, value);
//							logger.info(x+y+z+" ::: "+value);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return ht;
	}
	
	private Integer GetTickets(TableTag table,String columnName)
	{
//		logger.info(table.toHtml());
		int x = Integer.parseInt(CommonFun.GetStrFromPatter(columnName, "(\\d)(\\d)", 1));
		int y = Integer.parseInt(CommonFun.GetStrFromPatter(columnName, "(\\d)(\\d)", 2));
		String exy = table.getRows()[x].getColumns()[y].toPlainTextString().replaceAll(",", "");
		if(exy.length()==0)
			return null;
		else
			return new Integer(exy);
	}
	
	private BigDecimal GetBigDecimalE(TableTag table,String columnName)
	{
		int x = Integer.parseInt(CommonFun.GetStrFromPatter(columnName, "(\\d)(\\d)", 1));
		int y = Integer.parseInt(CommonFun.GetStrFromPatter(columnName, "(\\d)(\\d)", 2));
		String exy = "";
		if(y<x)
		{
			exy = table.getRows()[y-1].getColumns()[x*2-1].toPlainTextString();
			if(y==1)
				exy = table.getRows()[y-1].getColumns()[x*2].toPlainTextString();
		}
		else
		{
			exy = table.getRows()[y-2].getColumns()[x*2-1].toPlainTextString();
			if(x==1&&y==2)
				exy = table.getRows()[y-2].getColumns()[x*2].toPlainTextString();
		}
		
		if(exy.length()==0)
			return null;
		else
			return new BigDecimal(exy);
	}
	
	private BigDecimal GetBigDecimalQW(Hashtable ht, String columnName)
	{
		String key = CommonFun.GetStrFromPatter(columnName, "(\\d{2})", 1);		
		if(ht.containsKey(key))
		{
			String value = ht.get(key).toString();
			String[] array = value.split("～");
			if(array.length==2)
			{
				if(columnName.indexOf("min")!=-1)
				{
					if(CommonFun.isDecimal(array[0]))
						return new BigDecimal(array[0]);
					else
						return null;
				}
				else
				{
					if(CommonFun.isDecimal(array[1]))
						return new BigDecimal(array[1]);
					else
						return null;
				}
			}
		}
		return null;
	}
	
	private BigDecimal GetBigDecimalQ(TableTag table,String columnName)
	{
		int x = Integer.parseInt(CommonFun.GetStrFromPatter(columnName, "(\\d)(\\d)", 1));
		int y = Integer.parseInt(CommonFun.GetStrFromPatter(columnName, "(\\d)(\\d)", 2));
		String exy = table.getRows()[y-2].getColumns()[x*2-1].toPlainTextString();
		if(x==1&&y==2)
			exy = table.getRows()[y-2].getColumns()[x*2].toPlainTextString();
		if(exy.length()==0)
			return null;
		else
			return new BigDecimal(exy);
	}
	
	private BigDecimal GetBigDecimalTi(Hashtable ht,String key)
	{
		if(ht.containsKey(key))
		{
			String value = ht.get(key).toString();
			if(CommonFun.isDecimal(value))
				return new BigDecimal(value);
			else
				return null;
		}
		return null;
	}
	
	private BigDecimal GetBigDecimalT(Hashtable ht,String key)
	{
		key = CommonFun.GetStrFromPatter(key, "(\\d{3})", 1);
		
		if(ht.containsKey(key))
		{
			String value = ht.get(key).toString();
			if(CommonFun.isDecimal(value))
				return new BigDecimal(value);
			else
				return null;
		}
		return null;
	}
	
	private String GetUpdateTimeHHmm(String pageContent)
	{
//		<td class="bd_top_none bd_left_none">
//  		15:55現在
//    </td>
//		String HHmm = CommonFun.GetStrFromPatter(pageContent, "(\\d{1,2}:\\d{2})", 1);
		String HHmm = CommonFun.GetStrFromPatter(pageContent, "<td class=\"tr\">(\\d{1,2}:\\d{2})", 1);
		if(HHmm!=null)
		{
			HHmm = HHmm.replaceFirst(":", "");
			if(HHmm.length()==3)
				HHmm = "0"+HHmm;
		}		
		return HHmm;
	}	
	
	public void parseFromRaceID(String raceid)
	{
		try
		{
			String yyyy = raceid.substring(0, 4);
			String yyyyMMdd = raceid.substring(0, 8);
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);			
			String trackid_ddd = raceid.substring(8,11);
			String trackid = Integer.parseInt(trackid_ddd)+"";
			String raceno_dd = raceid.substring(11);
			String raceno = Integer.parseInt(raceno_dd)+"";
			String trackname = App.trackNameHt.get(trackid).toString();
			
			String filename = yyyyMMdd+"_"+trackid_ddd+"_"+raceno_dd+".html";	
			
			if(App.TypeName.toUpperCase().startsWith("RCL"))
			{
				String path_pre = savepath+CommonFun.SYS_SEPARATOR+"RaceCardLive"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;		
				String filename_pre = path_pre+CommonFun.SYS_SEPARATOR+filename;
				if(new File(filename_pre).exists() == false || App.EnOverWrite == true)
				{
					if(this.CanOpen("http://www.autorace.jp/netstadium/Program/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
					{
						CommonFun.OutToFileByte(filename_pre, page.getBodyBytes(), true);
						this.parsePreLive(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
					}
				}
				else
				{
					String pageContent = CommonFun.ReadFile(filename_pre);
					this.parsePreLive(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
				}
			}
			else if(App.TypeName.toUpperCase().startsWith("RC"))
			{
				String path_pre = savepath+CommonFun.SYS_SEPARATOR+"RaceCard"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;		
				String filename_pre = path_pre+CommonFun.SYS_SEPARATOR+filename;
				if(new File(filename_pre).exists() == false || App.EnOverWrite == true)
				{
					if(this.CanOpen("http://www.autorace.jp/netstadium/Program/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
					{
						CommonFun.OutToFileByte(filename_pre, page.getBodyBytes(), true);
						this.parsePre(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
					}
				}
				else
				{
					String pageContent = CommonFun.ReadFile(filename_pre);
					this.parsePre(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
				}
			}
			
			else if(App.TypeName.toUpperCase().startsWith("RR") || App.TypeName.toUpperCase().startsWith("RD"))
			{
				String path_post = savepath+CommonFun.SYS_SEPARATOR+"RaceResult"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;	
				String filename_post = path_post+CommonFun.SYS_SEPARATOR+filename;
				//判断文件是否存在,定义参数是否需要重新取页面
				if(new File(filename_post).exists() == false || App.EnOverWrite == true)
				{
					if(this.CanOpen("http://www.autorace.jp/netstadium/RaceResult/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
					{
						CommonFun.OutToFileByte(filename_post, page.getBodyBytes(), true);
						if(App.TypeName.toUpperCase().startsWith("RR"))
						{
							this.parsePost(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
							this.parseDividend(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
						}
						else if(App.TypeName.toUpperCase().startsWith("RD"))
						{
							this.parseDividend(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
						}
					}
				}
				else
				{
					String pageContent = CommonFun.ReadFile(filename_post);
					if(App.TypeName.toUpperCase().startsWith("RR"))
					{
						this.parsePost(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
						this.parseDividend(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
					}
					else if(App.TypeName.toUpperCase().startsWith("RD"))
					{
						this.parseDividend(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
					}
				}
			}
			else if(App.TypeName.toUpperCase().startsWith("ODDS"))
			{
				String path_odds = savepath+CommonFun.SYS_SEPARATOR+"Odds"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;	
				String filename_odds = path_odds+CommonFun.SYS_SEPARATOR+filename;
				if(new File(filename_odds).exists() == false || App.EnOverWrite == true)
				{
					if(this.CanOpen("http://www.autorace.jp/netstadium/Odds/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
					{
						CommonFun.OutToFileByte(filename_odds, page.getBodyBytes(), true);
						this.parseOdds(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
					}
				}
				else
				{
					String pageContent = CommonFun.ReadFile(filename_odds);
					this.parseOdds(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
				}
			}
			else if(App.TypeName.toUpperCase().startsWith("RO"))
			{
				String path_finalodds = savepath+CommonFun.SYS_SEPARATOR+"FinalOdds"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;	
				String filename_finalodds = path_finalodds+CommonFun.SYS_SEPARATOR+filename;
				if(new File(filename_finalodds).exists() == false || App.EnOverWrite == true)
				{
					if(this.CanOpen("http://www.autorace.jp/netstadium/Odds/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
					{
						CommonFun.OutToFileByte(filename_finalodds, page.getBodyBytes(), true);
						this.parseOdds(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
					}
				}
				else
				{
					String pageContent = CommonFun.ReadFile(filename_finalodds);
					this.parseOdds(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
				}
			}
			else if(App.TypeName.toUpperCase().startsWith("RFO"))
			{
				String path_finalodds = savepath+CommonFun.SYS_SEPARATOR+"FinalOdds"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;	
				String filename_finalodds = path_finalodds+CommonFun.SYS_SEPARATOR+filename;
				if(new File(filename_finalodds).exists() == false || App.EnOverWrite == true)
				{
					if(this.CanOpen("http://www.autorace.jp/netstadium/Odds/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
					{
						CommonFun.OutToFileByte(filename_finalodds, page.getBodyBytes(), true);
						this.parseFinalOdds(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
					}
				}
				else
				{
					String pageContent = CommonFun.ReadFile(filename_finalodds);
					this.parseFinalOdds(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
				}
			}
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void parseFromRaceID(String raceid, boolean enOverWrite)
	{
		try
		{
			App.EnOverWrite = enOverWrite;
			String yyyy = raceid.substring(0, 4);
			String yyyyMMdd = raceid.substring(0, 8);
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);		
			String trackid_ddd = raceid.substring(8,11);
			String trackid = Integer.parseInt(trackid_ddd)+"";
			String raceno_dd = raceid.substring(11);
			String raceno = Integer.parseInt(raceno_dd)+"";
			String trackname = App.trackNameHt.get(trackid).toString();
			
			String filename = yyyyMMdd+"_"+trackid_ddd+"_"+raceno_dd+".html";	
			
			String path_pre = savepath+CommonFun.SYS_SEPARATOR+"RaceCard"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;		
			String filename_pre = path_pre+CommonFun.SYS_SEPARATOR+filename;
			if(new File(filename_pre).exists() == false || App.EnOverWrite == true)
			{
				if(this.CanOpen("http://www.autorace.jp/netstadium/Program/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
					CommonFun.OutToFileByte(filename_pre, page.getBodyBytes(), true);
					this.parsePre(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
				}
			}
			else
			{
				String pageContent = CommonFun.ReadFile(filename_pre);
				this.parsePre(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
			}
			
			String path_post = savepath+CommonFun.SYS_SEPARATOR+"RaceResult"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;	
			String filename_post = path_post+CommonFun.SYS_SEPARATOR+filename;
			if(new File(filename_post).exists() == false || App.EnOverWrite == true)
			{
				if(this.CanOpen("http://www.autorace.jp/netstadium/RaceResult/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
					CommonFun.OutToFileByte(filename_post, page.getBodyBytes(), true);
					this.parsePost(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
					this.parseDividend(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
				}
			}
			else
			{
				String pageContent = CommonFun.ReadFile(filename_post);
				this.parsePost(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
				this.parseDividend(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
			}
		
			String path_finalodds = savepath+CommonFun.SYS_SEPARATOR+"FinalOdds"+CommonFun.SYS_SEPARATOR+yyyy+CommonFun.SYS_SEPARATOR+yyyyMMdd;	
			String filename_finalodds = path_finalodds+CommonFun.SYS_SEPARATOR+filename;
			if(new File(filename_finalodds).exists() == false || App.EnOverWrite == true)
			{
				if(this.CanOpen("http://www.autorace.jp/netstadium/Odds/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
					CommonFun.OutToFileByte(filename_finalodds, page.getBodyBytes(), true);
					this.parseOdds(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
					this.parseFinalOdds(yyyyMMdd,trackid_ddd,raceno_dd,page.getBody());
				}
			}
			else
			{
				String pageContent = CommonFun.ReadFile(filename_finalodds);
				this.parseOdds(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
				this.parseFinalOdds(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
			}
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runTestPost(String raceid, boolean isOverWrite)
	{
		try
		{
			String yyyyMMdd = raceid.substring(0, 8);
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			String trackid_ddd = raceid.substring(8,11);
			String trackid = Integer.parseInt(trackid_ddd)+"";
			String raceno_dd = raceid.substring(11);
			String raceno = Integer.parseInt(raceno_dd)+"";
			String filename = CommonFun.GetCurrPath()+CommonFun.SYS_SEPARATOR+"HistoryPage"+CommonFun.SYS_SEPARATOR+"RaceResult"+CommonFun.SYS_SEPARATOR+raceid+".html";
			if(!new File(filename).exists() == true || isOverWrite == true)
			{
				String trackname = App.trackNameHt.get(trackid).toString();
				if(this.CanOpen("http://www.autorace.jp/netstadium/RaceResult/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
					CommonFun.OutToFileByte(filename, page.getBodyBytes(), true);
				}
			}
			String pageContent = CommonFun.ReadFile(filename);
			this.parsePost(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runTestDiv(String raceid, boolean isOverWrite)
	{
		try
		{
			String yyyyMMdd = raceid.substring(0, 8);
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			String trackid_ddd = raceid.substring(8,11);
			String trackid = Integer.parseInt(trackid_ddd)+"";
			String raceno_dd = raceid.substring(11);
			String raceno = Integer.parseInt(raceno_dd)+"";
			String filename = CommonFun.GetCurrPath()+CommonFun.SYS_SEPARATOR+"HistoryPage"+CommonFun.SYS_SEPARATOR+"RaceResult"+CommonFun.SYS_SEPARATOR+raceid+".html";
			if(!new File(filename).exists() == true || isOverWrite == true)
			{
				String trackname = App.trackNameHt.get(trackid).toString();
				if(this.CanOpen("http://www.autorace.jp/netstadium/RaceResult/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
					CommonFun.OutToFileByte(filename, page.getBodyBytes(), true);
				}
			}
			String pageContent = CommonFun.ReadFile(filename);
			this.parseDividend(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runTestOdds(String raceid, boolean isOverWrite)
	{
		try
		{
			String yyyyMMdd = raceid.substring(0, 8);
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			String trackid_ddd = raceid.substring(8,11);
			String trackid = Integer.parseInt(trackid_ddd)+"";
			String raceno_dd = raceid.substring(11);
			String raceno = Integer.parseInt(raceno_dd)+"";
			String filename = CommonFun.GetCurrPath()+CommonFun.SYS_SEPARATOR+"HistoryPage"+CommonFun.SYS_SEPARATOR+"Odds"+CommonFun.SYS_SEPARATOR+raceid+".html";
			if(!new File(filename).exists() == true || isOverWrite == true)
			{
				String trackname = App.trackNameHt.get(trackid).toString();
			if(this.CanOpen("http://www.autorace.jp/netstadium/Odds/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
				CommonFun.OutToFileByte(filename, page.getBodyBytes(), true);
				}
			}
			String pageContent = CommonFun.ReadFile(filename);
			this.parseOdds(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runTestFinalOdds(String raceid, boolean isOverWrite)
	{
		try
		{
			String yyyyMMdd = raceid.substring(0, 8);
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			String trackid_ddd = raceid.substring(8,11);
			String trackid = Integer.parseInt(trackid_ddd)+"";
			String raceno_dd = raceid.substring(11);
			String raceno = Integer.parseInt(raceno_dd)+"";
			String filename = CommonFun.GetCurrPath()+CommonFun.SYS_SEPARATOR+"HistoryPage"+CommonFun.SYS_SEPARATOR+"FinalOdds"+CommonFun.SYS_SEPARATOR+raceid+".html";
			if(!new File(filename).exists() == true || isOverWrite == true)
			{
				String trackname = App.trackNameHt.get(trackid).toString();
				if(this.CanOpen("http://www.autorace.jp/netstadium/Odds/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
					CommonFun.OutToFileByte(filename, page.getBodyBytes(), true);
				}
			}
			String pageContent = CommonFun.ReadFile(filename);
			this.parseFinalOdds(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runTestPre(String raceid, boolean isOverWrite)
	{
		try
		{
			String yyyyMMdd = raceid.substring(0, 8);
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			String trackid_ddd = raceid.substring(8,11);
			String trackid = Integer.parseInt(trackid_ddd)+"";
			String raceno_dd = raceid.substring(11);
			String raceno = Integer.parseInt(raceno_dd)+"";
			String filename = CommonFun.GetCurrPath()+CommonFun.SYS_SEPARATOR+"HistoryPage"+CommonFun.SYS_SEPARATOR+"RaceCard"+CommonFun.SYS_SEPARATOR+raceid+".html";
			if(!new File(filename).exists() == true || isOverWrite == true)
			{
				String trackname = App.trackNameHt.get(trackid).toString();
				if(this.CanOpen("http://www.autorace.jp/netstadium/Program/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
					CommonFun.OutToFileByte(filename, page.getBodyBytes(), true);
				}
			}
			String pageContent = CommonFun.ReadFile(filename);
			this.parsePre(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runTestPreLive(String raceid, boolean isOverWrite)
	{
		try
		{
			String yyyyMMdd = raceid.substring(0, 8);
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			String trackid_ddd = raceid.substring(8,11);
			String trackid = Integer.parseInt(trackid_ddd)+"";
			String raceno_dd = raceid.substring(11);
			String raceno = Integer.parseInt(raceno_dd)+"";
			String filename = CommonFun.GetCurrPath()+CommonFun.SYS_SEPARATOR+"HistoryPage"+CommonFun.SYS_SEPARATOR+"RaceCard"+CommonFun.SYS_SEPARATOR+raceid+".html";
			if(!new File(filename).exists() == true || isOverWrite == true)
			{
				String trackname = App.trackNameHt.get(trackid).toString();
				if(this.CanOpen("http://www.autorace.jp/netstadium/Program/"+trackname+"/"+yyyy_MM_dd+"_"+raceno, 3))
				{
					CommonFun.OutToFileByte(filename, page.getBodyBytes(), true);
				}
			}
			String pageContent = CommonFun.ReadFile(filename);
			this.parsePreLive(yyyyMMdd,trackid_ddd,raceno_dd,pageContent);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runTestPlayer(String playerid)
	{
		try
		{
			this.parsePlayer(playerid);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private void parsePre(String yyyyMMdd, String trackId, String raceNo, String pageContent)
	{
		try
		{			
			while(trackId.length()<3)
				trackId = "0"+trackId;
			while(raceNo.length()<2)
				raceNo = "0"+raceNo;
			
			Date extractTime = new Date();
			
			parser.setInputHTML(pageContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			if(nodelist!=null && nodelist.size()>0)
			{
				AutoRacePreRaceRace pre = new AutoRacePreRaceRace();
				TableTag tablePreLine = (TableTag) nodelist.elementAt(0);
//				logger.info(table.toHtml());
				if(tablePreLine.getRowCount()>0)
				{		
					
					TableRow[] rows = tablePreLine.getRows();
					String raceDesc = rows[0].getHeaders()[0].toPlainTextString().trim();
//					logger.info("raceDesc == "+raceDesc); 
					String raceCategory = rows[2].getColumns()[1].toPlainTextString().trim().split("\\s+")[0].trim();;
//					logger.info("raceCategory == "+raceCategory);
					String distance = rows[2].getColumns()[2].toPlainTextString().trim();
//					logger.info("distance == "+distance); 
					String telebetCutOffTime = null;
					String scheduledStartTime = null;
					int index = 0;
					if(rows[2].getColumnCount()>8)
					{
						index = 2;
						telebetCutOffTime = rows[2].getColumns()[3].toPlainTextString().trim();
	//					telebetCutOffTime = CommonFun.GetStrFromPatter(telebetCutOffTime, "\\d{1,2}:\\d{2}", 0);
	//					logger.info("telebetCutOffTime == "+telebetCutOffTime);
						scheduledStartTime = rows[2].getColumns()[4].toPlainTextString().trim();
						scheduledStartTime = CommonFun.GetStrFromPatter(scheduledStartTime, "\\d{1,2}:\\d{2}", 0);
	//					logger.info("scheduledStartTime == "+scheduledStartTime);
					}
					String weather = rows[2].getColumns()[3+index].toPlainTextString().trim();
//					logger.info("weather == "+weather);
					String temperature = rows[2].getColumns()[4+index].toPlainTextString().trim();
//					logger.info("temperature == "+temperature);
					String humidity = rows[2].getColumns()[5+index].toPlainTextString().trim();
//					logger.info("humidity == "+humidity);
					String temperatureTrack = rows[2].getColumns()[6+index].toPlainTextString().trim();
//					logger.info("temperatureTrack == "+temperatureTrack);
//					String totalMeetingDay = 
					String going = rows[2].getColumns()[7+index].toPlainTextString().trim();
//					logger.info("going == "+going);				
					
//					logger.info("raceid = "+DF_yyyyMMdd.format(raceDate)+trackId+raceNo);
//					logger.info("uraceid = "+DF_yyyyMMdd.format(raceDate)+"24"+trackId+raceNo);
					long raceId = Long.parseLong(yyyyMMdd+trackId+raceNo);					
					Long uraceId = new Long(yyyyMMdd+"24"+trackId+raceNo);
//					logger.info("long raceId = "+raceId);
//					logger.info("Long uraceId = "+uraceId);
					String hql = "from AutoRacePreRaceRace a where a.raceId = "+raceId;				
					List list = DBAccess.GetObjListByHql(hql);
					if(list!=null)
					{
						Iterator it = list.iterator();
						if(it.hasNext())
						{
							pre = (AutoRacePreRaceRace)it.next();
						}	
					}
					pre.setUraceId(uraceId);
					pre.setRaceId(raceId);					
					pre.setRaceNo(new Byte(raceNo));
					pre.setRaceDate(DF_yyyyMMdd.parse(yyyyMMdd));
					pre.setTrackId(new Short(trackId));
					pre.setTrackName(App.trackHt.get(trackId).toString());
					if(scheduledStartTime!=null)
						pre.setScheduledStartTime(scheduledStartTime);
					String title = raceDesc;
					pre.setRaceName(title.split("　")[1]);
//					pre.setRaceDesc(raceDesc);
//					pre.setClassId(classId);
		//			pre.setClassName(className);
					pre.setRaceCategory(raceCategory);
					if(telebetCutOffTime!=null)
						pre.setTelebetCutOffTime(telebetCutOffTime);
//					pre.setNoOfRound(noOfRound);
					pre.setDistance(new Integer(distance.replaceFirst("ｍ", "")));
//					pre.setGrade(grade);		
					pre.setWeather(weather);
					pre.setTemperature(temperature);
					pre.setTemperatureTrack(temperatureTrack);
					pre.setHumidity(humidity);
					pre.setGoing(going);
//					pre.setTrophy(trophy);
					pre.setCancelled(new Boolean(false));
					pre.setTotalMeetingDay(this.GetTotalMeetingDay(title));	
					pre.setMeetingDay(this.GetMeetingDay(title));
					pre.setFirstMeetingDate(this.GetFirstMeetingDay(title));
		//			pre.setBettypeNameList(bettypeNameList);					
					pre.setExtractTime(new Date());
					DBAccess.save(pre);
				}
				
				TableTag table1 = (TableTag) nodelist.elementAt(3);
				TableTag table2 = (TableTag) nodelist.elementAt(4);
//				logger.info("#################### table1 = "+table1.toHtml());
//				logger.info("#################### table2 = "+table2.toHtml());
				if(table1.getRowCount()>0)
				{
					TableRow[] rows = table1.getRows();
					TableRow[] rows1 = table2.getRows();					
					for(int i=1;i<rows[1].getColumnCount();i++)
					{
//						logger.info(rows[1].getColumns()[i].toHtml());
//						String playerid = CommonFun.GetStrFromPatter(rows[1].getColumns()[i].toHtml(),"/netstadium/Profile/(\\d{1,9})\"",1);//						
						
						String playerid = CommonFun.GetStrFromPatter(rows[3].getColumns()[i].toHtml(), "<img src=\"[^<]+?/(\\d{1,8})\\.jpg", 1);		
						if(playerid!=null)
						{
							int playerId = Integer.parseInt(playerid);
							while(playerid.length()<4)
								playerid = "0"+playerid;
							AutorRaceCodePlayer player = this.parsePlayer(playerid);
							
							String clothNo = rows[0].getColumns()[i].toPlainTextString().trim();
							String playerName = convertPlayerName(rows[1].getColumns()[i].toPlainTextString().trim());
							String playerNameEn = rows[2].getColumns()[i].getChildren().toHtml().replaceFirst("<br>", " ").trim();
							if(rows[2].getColumns()[i].getChildCount()==1)
								playerNameEn = rows[2].getColumns()[i].getChild(0).getChildren().toHtml().replaceFirst("<br>", " ").trim();
							String playerImagePath = CommonFun.GetStrFromPatter(rows[3].getColumns()[i].toHtml(), "<img src=\"(.+?)\"", 1);
							
							String age = rows1[0].getColumns()[i].toPlainTextString().trim();
							String handicap = rows1[1].getColumns()[i].toPlainTextString().trim();
							
							String trialT = rows1[2].getColumns()[i].toPlainTextString().trim();
							String trialOffset = rows1[3].getColumns()[i].toPlainTextString().trim();
							int k = 2;
							
							String lg = rows1[2+k].getColumns()[i].toPlainTextString().trim();							
							String motorName = rows1[3+k].getColumns()[i].toPlainTextString().trim();
							String motorClass = rows1[4+k].getColumns()[i].toPlainTextString().trim();
							String period = rows1[5+k].getColumns()[i].toPlainTextString().trim();
							String currentRank = rows1[6+k].getColumns()[i].toPlainTextString().trim();
							String lastRank = rows1[7+k].getColumns()[i].toPlainTextString().trim();
							String examinationPoint = rows1[8+k].getColumns()[i].toPlainTextString().trim();
							
							AutoRacePreRacePlayer prep = new AutoRacePreRacePlayer();
							AutoRacePreRacePlayerId prepid = new AutoRacePreRacePlayerId();
							prepid.setRaceId(pre.getRaceId());
							prepid.setPlayerId(playerId);
							prep.setId(prepid);
							prep.setUraceId(pre.getUraceId());
							prep.setPlayerName(playerName);	
							prep.setPlayerNameEn(playerNameEn);
							prep.setClothNo(new Byte(clothNo));
							prep.setTrialT(trialT.length()>0?new BigDecimal(trialT):null);
							prep.setTrialOffset(trialOffset.length()>0?new BigDecimal(trialOffset):null);
							prep.setLg(lg);
							prep.setAge(new Byte(age));
							prep.setHandicap(handicap.length()>0?new BigDecimal(handicap):null);
							prep.setCurrentRank(currentRank);
							prep.setPlayerImagePath(playerImagePath);
							prep.setMotorName(motorName);
							prep.setMotorClass(motorClass);
							prep.setPeriod(period);
							prep.setCurrentRank(currentRank);
							prep.setLastRank(lastRank);
							prep.setExaminationPoint(examinationPoint);
							prep.setScratch(new Boolean(false));
							prep.setExtractTime(extractTime);
							if(player!=null)
								prep.setPlayerWeight(player.getPlayerWeight());
							DBAccess.save(prep);
						}
					}
				}
			}
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private String GetSqlParameterFormat(Object obj)
	{
		if(obj == null)
			return "null";
		
		String type = obj.getClass().getName();
		if(type.equals("java.lang.String"))
			return "N'"+obj.toString()+"'";
		else if(type.equals("java.util.Date"))
			return "'"+((Date)obj).toString()+"'";

		return "null";
	}
	
	private void parsePreLive(String yyyyMMdd, String trackId, String raceNo, String pageContent)
	{
		try
		{	
			while(trackId.length()<3)
				trackId = "0"+trackId;
			while(raceNo.length()<2)
				raceNo = "0"+raceNo;
			
			Date extractTime = new Date();
			
			parser.setInputHTML(pageContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			if(nodelist!=null && nodelist.size()>0)
			{
				AutoRacePreRaceRaceLive pre = new AutoRacePreRaceRaceLive();
				AutoRacePreRaceRaceLiveId preid = new AutoRacePreRaceRaceLiveId();
				pre.setId(preid);
				TableTag tablePreLine = (TableTag) nodelist.elementAt(0);
//				logger.info(table.toHtml());
				if(tablePreLine.getRowCount()>0)
				{		
					
					TableRow[] rows = tablePreLine.getRows();
					String raceDesc = rows[0].getHeaders()[0].toPlainTextString().trim();
//					logger.info("raceDesc == "+raceDesc); 
					String raceCategory = rows[2].getColumns()[1].toPlainTextString().trim().split("\\s+")[0].trim();;
//					logger.info("raceCategory == "+raceCategory);
					String distance = rows[2].getColumns()[2].toPlainTextString().trim();
//					logger.info("distance == "+distance); 
					String telebetCutOffTime = null;
					String scheduledStartTime = null;
					int index = 0;
					if(rows[2].getColumnCount()>8)
					{
						index = 2;
						telebetCutOffTime = rows[2].getColumns()[3].toPlainTextString().trim();
	//					telebetCutOffTime = CommonFun.GetStrFromPatter(telebetCutOffTime, "\\d{1,2}:\\d{2}", 0);
	//					logger.info("telebetCutOffTime == "+telebetCutOffTime);
						scheduledStartTime = rows[2].getColumns()[4].toPlainTextString().trim();
						scheduledStartTime = CommonFun.GetStrFromPatter(scheduledStartTime, "\\d{1,2}:\\d{2}", 0);
	//					logger.info("scheduledStartTime == "+scheduledStartTime);
					}
					String weather = rows[2].getColumns()[3+index].toPlainTextString().trim();
//					logger.info("weather == "+weather);
					String temperature = rows[2].getColumns()[4+index].toPlainTextString().trim();
//					logger.info("temperature == "+temperature);
					String humidity = rows[2].getColumns()[5+index].toPlainTextString().trim();
//					logger.info("humidity == "+humidity);
					String temperatureTrack = rows[2].getColumns()[6+index].toPlainTextString().trim();
//					logger.info("temperatureTrack == "+temperatureTrack);
//					String totalMeetingDay = 
					String going = rows[2].getColumns()[7+index].toPlainTextString().trim();
//					logger.info("going == "+going);				
					long raceId = Long.parseLong(yyyyMMdd+trackId+raceNo);					
					Long uraceId = new Long(yyyyMMdd+"24"+trackId+raceNo);
//					String hql = "from AutoRacePreRaceRaceLive a where a.raceId = "+raceId;				
//					List list = DBAccess.GetObjListByHql(hql);
//					if(list!=null)
//					{
//						Iterator it = list.iterator();
//						if(it.hasNext())
//						{
//							pre = (AutoRacePreRaceRaceLive)it.next();
//						}	
//					}
					pre.setUraceId(uraceId);
					preid.setRaceId(raceId);	
					preid.setExtractTime(extractTime);
					pre.setRaceNo(new Byte(raceNo));
					pre.setRaceDate(DF_yyyyMMdd.parse(yyyyMMdd));
					pre.setTrackId(new Short(trackId));
					pre.setTrackName(App.trackHt.get(trackId).toString());
					if(scheduledStartTime!=null)
						pre.setScheduledStartTime(scheduledStartTime);
					String title = raceDesc;
					pre.setRaceName(title.split("　")[1]);
//					pre.setRaceDesc(raceDesc);
//					pre.setClassId(classId);
		//			pre.setClassName(className);
					pre.setRaceCategory(raceCategory);
					if(telebetCutOffTime!=null)
						pre.setTelebetCutOffTime(telebetCutOffTime);
//					pre.setNoOfRound(noOfRound);
					pre.setDistance(new Integer(distance.replaceFirst("ｍ", "")));
//					pre.setGrade(grade);		
					pre.setWeather(weather);
					pre.setTemperature(temperature);
					pre.setTemperatureTrack(temperatureTrack);
					pre.setHumidity(humidity);
					pre.setGoing(going);
//					pre.setTrophy(trophy);
					pre.setCancelled(new Boolean(false));
					pre.setTotalMeetingDay(this.GetTotalMeetingDay(title));	
					pre.setMeetingDay(this.GetMeetingDay(title));
					pre.setFirstMeetingDate(this.GetFirstMeetingDay(title));
		//			pre.setBettypeNameList(bettypeNameList);					
//					DBAccess.save(pre);
					StringBufferSql sb = new StringBufferSql();		
//					@URaceID bigint = NULL,
					sb.add(pre.getUraceId());
//					@RaceID bigint,
					sb.add(pre.getId().getRaceId());
//					@RaceNo tinyint = NULL,
					sb.add(pre.getRaceNo());
//					@RaceDate smalldatetime = NULL,
					sb.add(pre.getRaceDate());
//					@TrackID smallint = NULL,
					sb.add(pre.getTrackId());
//					@TrackName nvarchar(50) = NULL,
					sb.add(pre.getTrackName());
//					@ScheduledStartTime varchar(5) = NULL,
					sb.add(pre.getScheduledStartTime());
//					@RaceName nvarchar(100) = NULL,
					sb.add(pre.getRaceName());
//					@Race_Desc nvarchar(50) = NULL,
					sb.add(pre.getRaceDesc());
//					@ClassID int = NULL,
					sb.add(pre.getClassId());
//					@ClassName nvarchar(50) = NULL,
					sb.add(pre.getClassName());
//					@RaceCategory nvarchar(50) = NULL,
					sb.add(pre.getRaceCategory());
//					@TelebetCutOffTime nvarchar(5) = NULL,
					sb.add(pre.getTelebetCutOffTime());
//					@NoOfRound nvarchar(20) = NULL,
					sb.add(pre.getNoOfRound());
//					@Distance int = NULL,
					sb.add(pre.getDistance());
//					@Grade nvarchar(50) = NULL,
					sb.add(pre.getGrade());
//					@Weather nvarchar(10) = NULL,
					sb.add(pre.getWeather());
//					@Temperature_Track nvarchar(20) = NULL,
					sb.add(pre.getTemperatureTrack());
//					@Temperature nvarchar(10) = NULL,
					sb.add(pre.getTemperature());
//					@Humidity nvarchar(20) = NULL,
					sb.add(pre.getHumidity());
//					@Going nvarchar(20) = NULL,
					sb.add(pre.getGoing());
//					@Trophy nvarchar(50) = NULL,
					sb.add(pre.getTrophy());
//					@Cancelled bit = NULL,
					sb.add(pre.getCancelled());
//					@TotalMeetingDay tinyint = NULL,
					sb.add(pre.getTotalMeetingDay());
//					@BettypeNameList nvarchar(50) = NULL,
					sb.add(pre.getBettypeNameList());
//					@ExtractTime smalldatetime,
					sb.add(pre.getId().getExtractTime());
//					@MeetingDay tinyint = NULL,
					sb.add(pre.getMeetingDay());
//					@FirstMeetingDate smalldatetime = NULL
					sb.add(pre.getFirstMeetingDate());
					
//					logger.info(sb.toString());
					
					db.ExecStoredProcedures("pr_AutoRace_PreRace_Race_Live_InsertData", sb.toString());
				}
				
				TableTag table1 = (TableTag) nodelist.elementAt(3);
				TableTag table2 = (TableTag) nodelist.elementAt(4);
//				logger.info("#################### table1 = "+table1.toHtml());
//				logger.info("#################### table2 = "+table2.toHtml());
				if(table1.getRowCount()>0)
				{
					TableRow[] rows = table1.getRows();
					TableRow[] rows1 = table2.getRows();					
					for(int i=1;i<rows[1].getColumnCount();i++)
					{
//						String playerid = CommonFun.GetStrFromPatter(rows[1].getColumns()[i].toHtml(),"/netstadium/Profile/(\\d{1,9})\"",1);
						String playerid = CommonFun.GetStrFromPatter(rows[3].getColumns()[i].toHtml(), "<img src=\"[^<]+?/(\\d{1,8})\\.jpg", 1);
//						logger.info("playerid = "+playerid);
						if(playerid!=null)
						{
							int playerId = Integer.parseInt(playerid);							
							String clothNo = rows[0].getColumns()[i].toPlainTextString().trim();
							String playerName = convertPlayerName(rows[1].getColumns()[i].toPlainTextString().trim());							
							String playerNameEn = rows[2].getColumns()[i].getChildren().toHtml().replaceFirst("<br>", " ").trim();
							if(rows[2].getColumns()[i].getChildCount()==1)
								playerNameEn = rows[2].getColumns()[i].getChild(0).getChildren().toHtml().replaceFirst("<br>", " ").trim();
							String playerImagePath = CommonFun.GetStrFromPatter(rows[3].getColumns()[i].toHtml(), "<img src=\"(.+?)\"", 1);							
							
							String age = rows1[0].getColumns()[i].toPlainTextString().trim();
							String handicap = rows1[1].getColumns()[i].toPlainTextString().trim();
							
							String trialT = rows1[2].getColumns()[i].toPlainTextString().trim();
							String trialOffset = rows1[3].getColumns()[i].toPlainTextString().trim();
							int k = 2;
							
							String lg = rows1[2+k].getColumns()[i].toPlainTextString().trim();							
							String motorName = rows1[3+k].getColumns()[i].toPlainTextString().trim();
							String motorClass = rows1[4+k].getColumns()[i].toPlainTextString().trim();
							String period = rows1[5+k].getColumns()[i].toPlainTextString().trim();
							String currentRank = rows1[6+k].getColumns()[i].toPlainTextString().trim();
							String lastRank = rows1[7+k].getColumns()[i].toPlainTextString().trim();
							String examinationPoint = rows1[8+k].getColumns()[i].toPlainTextString().trim();
							
							AutoRacePreRacePlayerLive prep = new AutoRacePreRacePlayerLive();
							AutoRacePreRacePlayerLiveId prepid = new AutoRacePreRacePlayerLiveId();
							prepid.setRaceId(preid.getRaceId());
							prepid.setPlayerId(playerId);
							prep.setId(prepid);
							prep.setUraceId(pre.getUraceId());
							prep.setPlayerName(playerName);	
							prep.setPlayerNameEn(playerNameEn);
							prep.setClothNo(new Byte(clothNo));
							prep.setTrialT(trialT.length()>0?new BigDecimal(trialT):null);
							prep.setTrialOffset(trialOffset.length()>0?new BigDecimal(trialOffset):null);
							prep.setLg(lg);
							prep.setAge(new Byte(age));
							prep.setHandicap(handicap.length()>0?new BigDecimal(handicap):null);
							prep.setCurrentRank(currentRank);
							prep.setPlayerImagePath(playerImagePath);
							prep.setMotorName(motorName);
							prep.setMotorClass(motorClass);
							prep.setPeriod(period);
							prep.setCurrentRank(currentRank);
							prep.setLastRank(lastRank);
							prep.setExaminationPoint(examinationPoint);
							prep.setScratch(new Boolean(false));
							prep.setExtractTime(extractTime);
							
//							DBAccess.save(prep);
							
							StringBufferSql sbSql = new StringBufferSql();					
//							@URaceID bigint = NULL,
							sbSql.add(prep.getUraceId());
//							@RaceID bigint,
							sbSql.add(prep.getId().getRaceId());
//							@PlayerID int,
							sbSql.add(prep.getId().getPlayerId());
//							@PlayerName nvarchar(50) = NULL,
							sbSql.add(prep.getPlayerName());
//							@PlayerNameEn nvarchar(50) = NULL,
							sbSql.add(prep.getPlayerNameEn());
//							@ClothNo tinyint = NULL,
							sbSql.add(prep.getClothNo());
//							@LG nvarchar(20) = NULL,
							sbSql.add(prep.getLg());
//							@Age tinyint = NULL,
							sbSql.add(prep.getAge());
//							@Handicap decimal(9, 4) = NULL,
							sbSql.add(prep.getHandicap());
//							@PlayerImagePath varchar(50) = NULL,
							sbSql.add(prep.getPlayerImagePath());
//							@MotorName nvarchar(20) = NULL,
							sbSql.add(prep.getMotorName());
//							@MotorClass nvarchar(10) = NULL,
							sbSql.add(prep.getMotorClass());
//							@Period nvarchar(20) = NULL,
							sbSql.add(prep.getPeriod());
//							@Current_rank varchar(10) = NULL,
							sbSql.add(prep.getCurrentRank());
//							@Last_rank varchar(10) = NULL,
							sbSql.add(prep.getLastRank());
//							@ExaminationPoint nvarchar(100) = NULL,
							sbSql.add(prep.getExaminationPoint());
//							@Scratch bit = NULL,
							sbSql.add(prep.getScratch());
//							@ExtractTime smalldatetime,
							sbSql.add(prep.getExtractTime());
//							@TrialT decimal(8, 2) = NULL,
							sbSql.add(prep.getTrialT());
//							@TrialOffset decimal(8, 2) = NULL
							sbSql.add(prep.getTrialOffset());
							
//							logger.info(sbSql.toString());
							
							db.ExecStoredProcedures("pr_AutoRace_PreRace_Player_Live_InsertData", sbSql.toString());
						}
					}
					
				}
			}
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private Byte GetTotalMeetingDay(String title)
	{
		try
		{
			String patter = "開催期間：(\\d{4})年(\\d{1,2})月(\\d{1,2})日\\(.\\)～(\\d{1,2})日\\(.\\)";
			Matcher matcher = CommonFun.GetMatcherStrGroup(title,patter);
			if(matcher.find())
			{
				String yyyy = matcher.group(1);
				String MM = matcher.group(2);
				if(MM.length()==1)
					MM = "0"+MM;
				String dd = matcher.group(3);
				if(dd.length()==1)
					dd = "0"+dd;
				String enddd = matcher.group(4);
				if(enddd.length()==1)
					enddd = "0"+enddd;
				
				Date startDate = DF_yyyyMMdd.parse(yyyy+MM+dd);
				int days = 0;
				while(!DF_dd.format(startDate).equals(enddd))
				{
					days++;
					startDate = CommonFun.DateSub(startDate, 1);
				}
				return new Byte(days+1+"");
			}
			
			patter = "開催期間：(\\d{4})年(\\d{1,2})月(\\d{1,2})日\\(.\\)～(\\d{1,2})月(\\d{1,2})日\\(.\\)";
			matcher = CommonFun.GetMatcherStrGroup(title,patter);
			if(matcher.find())
			{
				String yyyy = matcher.group(1);
				String MM = matcher.group(2);
				if(MM.length()==1)
					MM = "0"+MM;
				String dd = matcher.group(3);
				if(dd.length()==1)
					dd = "0"+dd;
				
				String endMM = matcher.group(4);
				if(endMM.length()==1)
					endMM = "0"+endMM;
				String enddd = matcher.group(5);
				if(enddd.length()==1)
					enddd = "0"+enddd;
				
				Date startDate = DF_yyyyMMdd.parse(yyyy+MM+dd);
				int days = 0;
				while(!DF_MMdd.format(startDate).equals(endMM+enddd))
				{
					days++;
					startDate = CommonFun.DateSub(startDate, 1);
				}
				return new Byte(days+1+"");
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	private Date GetFirstMeetingDay(String title)
	{
		try
		{
			String patter = "開催期間：(\\d{4})年(\\d{1,2})月(\\d{1,2})日";
			Matcher matcher = CommonFun.GetMatcherStrGroup(title,patter);
			if(matcher.find())
			{
				String yyyy = matcher.group(1);
				String MM = matcher.group(2);
				if(MM.length()==1)
					MM = "0"+MM;
				String dd = matcher.group(3);
				if(dd.length()==1)
					dd = "0"+dd;				
				return DF_yyyyMMdd.parse(yyyy+MM+dd);				
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	private Byte GetMeetingDay(String title)
	{
		try
		{
			String patter = "第(.{1,2})日目";
			Matcher matcher = CommonFun.GetMatcherStrGroup(title,patter);
			if(matcher.find())
			{
				if(App.dayHt.containsKey(matcher.group(1)))
					return new Byte(App.dayHt.get(matcher.group(1)).toString());
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
	private void parseDividend(String yyyyMMdd, String trackId, String raceNo, String pageContent)
	{
		try
		{	
			//<table width="100%" class="tblMain mg_btm_15">//s*<tr>.*?</table>
			pageContent = pageContent.replaceAll("<table width=\"100%\" class=\"tblMain mg_btm_15\">\\s*<tr>.*?</table>", "");
			while(trackId.length()<3)
				trackId = "0"+trackId;
			while(raceNo.length()<2)
				raceNo = "0"+raceNo;
			
			parser.setInputHTML(pageContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			if(nodelist!=null && nodelist.size()>5)
			{
				TableTag tableRunningPosition = (TableTag) nodelist.elementAt(4);
				TableTag tableDividend = (TableTag) nodelist.elementAt(5);
				if(pageContent.indexOf("競走戒告")>-1){
					 tableRunningPosition = (TableTag) nodelist.elementAt(5);
					 tableDividend = (TableTag) nodelist.elementAt(6);
				}
//				logger.info(tableRunningPosition.toHtml());
//				logger.info(tableDividend.toHtml());
				Date extractTime = new Date();
				if(tableRunningPosition.getRowCount()>1&&tableRunningPosition.getRows()[1].getColumnCount()==9)
				{		
					AutoRaceRunningPosition p = new AutoRaceRunningPosition();
					AutoRaceRunningPositionId pid = new AutoRaceRunningPositionId();
					p.setId(pid);
					
					long raceId = Long.parseLong(yyyyMMdd+trackId+raceNo);					
					Long uraceId = new Long(yyyyMMdd+"24"+trackId+raceNo);
					
					pid.setRaceid(raceId);
					p.setRacedate(DF_yyyyMMdd.parse(yyyyMMdd));
					
					Hashtable ht = new Hashtable();
					TableRow[] rows = tableRunningPosition.getRows();
					for(int i=2;i<rows.length;i++)
					{
						TableColumn[] cols = rows[i].getColumns();
						String post = "finalpost_";
						if(i>2)
						{
							String zhou = CommonFun.GetStrFromPatter(cols[0].toPlainTextString(), "(\\d{1,2})", 1);
							post = "post"+zhou+"_";
						}
						
						for(int j=1;j<cols.length;j++)
						{							
							if(cols[j].toPlainTextString().length()>0)
								ht.put(post+cols[j].toPlainTextString(), j+"");
						}
					}

					Vector vClothno = new Vector();
					Enumeration keys = ht.keys();
					while(keys.hasMoreElements())
					{
						String key = keys.nextElement().toString();
						String clothno = key.split("_")[1];
						if(!vClothno.contains(clothno))
							vClothno.add(clothno);
					}
//					
					for(int i=0;i<vClothno.size();i++)
					{
						String clothno = vClothno.get(i).toString();
						pid.setClothno(new Byte(clothno).byteValue());
						p.setFinalPos(this.GetByte(ht,"finalpost_"+clothno));
						p.setPos1(this.GetByte(ht,"post1_"+clothno));
						p.setPos2(this.GetByte(ht,"post2_"+clothno));
						p.setPos3(this.GetByte(ht,"post3_"+clothno));
						p.setPos4(this.GetByte(ht,"post4_"+clothno));
						p.setPos5(this.GetByte(ht,"post5_"+clothno));
						p.setPos6(this.GetByte(ht,"post6_"+clothno));
						p.setPos7(this.GetByte(ht,"post7_"+clothno));
						p.setPos8(this.GetByte(ht,"post8_"+clothno));
						p.setPos9(this.GetByte(ht,"post9_"+clothno));
						p.setPos10(this.GetByte(ht,"post10_"+clothno));
						DBAccess.save(p);
					}
				}
				
//				System.err.println(tableDividend.getRows()[2].getColumnCount());
//				System.err.println(tableDividend.getRowCount());
				if(tableDividend.getRowCount()>1&&tableDividend.getRows()[2].getColumnCount()==4)
				{	
					AutoRaceDividend p = new AutoRaceDividend();
					AutoRaceDividendId pid = new AutoRaceDividendId();
					p.setId(pid);
					
					long raceId = Long.parseLong(yyyyMMdd+trackId+raceNo);					
					Long uraceId = new Long(yyyyMMdd+"24"+trackId+raceNo);
					
					pid.setRaceId(raceId);
					p.setUraceId(uraceId);
					p.setRaceDate(DF_yyyyMMdd.parse(yyyyMMdd));
					
					TableRow[] rows = tableDividend.getRows();
					String tmpBetTypeName = "";
					for(int i=2;i<rows.length;i++)
					{
						TableColumn[] cols = rows[i].getColumns();						
						if(cols.length<3)
							continue;
//						logger.info(rows[i].toHtml());
						
						String betTypeName = tmpBetTypeName;
						String combination = "";
						String dividend = "";
						String popularity = "";
						String comb = "";
						
						if(cols[0].toHtml().indexOf("light txtArea")!=-1)
						{
							betTypeName = cols[0].toPlainTextString();
							tmpBetTypeName = betTypeName;
							if(cols.length == 4)
							{
								combination = cols[1].toPlainTextString();
								dividend = cols[2].toPlainTextString();
								popularity = cols[3].toPlainTextString();
							}
							else if(cols.length == 5)
							{
								comb = cols[1].toPlainTextString();
								combination = cols[2].toPlainTextString();
								dividend = cols[3].toPlainTextString();
								popularity = cols[4].toPlainTextString();
							}
						}
						else if(cols.length == 3)
						{
							betTypeName = tmpBetTypeName;
							combination = cols[0].toPlainTextString();
							dividend = cols[1].toPlainTextString();
							popularity = cols[2].toPlainTextString();
						}
						else if(cols.length == 4)
						{
							betTypeName = tmpBetTypeName;
							comb = cols[0].toPlainTextString();
							combination = cols[1].toPlainTextString();
							dividend = cols[2].toPlainTextString();
							popularity = cols[3].toPlainTextString();
						}
						
						p.setBetTypeName(betTypeName);
						if(App.betTypeHt.containsKey(betTypeName))
							pid.setBetTypeId(new Byte(App.betTypeHt.get(betTypeName).toString()).byteValue());
						else
						{
							logger.error("find new bet byte "+betTypeName);
							continue;
						}						
						pid.setCombination(combination.trim().replaceAll("\\s+", ""));
						p.setComb1(null);
						p.setComb2(null);
						p.setComb3(null);
						if(comb.length()>0&&comb.indexOf("1")!=-1)
							p.setComb1(new Byte("1"));
						else if(comb.length()>0&&comb.indexOf("2")!=-1)
							p.setComb2(new Byte("2"));
						else if(comb.length()>0&&comb.indexOf("3")!=-1)
							p.setComb3(new Byte("3"));
						dividend = CommonFun.GetStrFromPatter(dividend.replaceAll(",", ""), "(\\d{1,9})", 1);
						if(dividend!=null)
							p.setDividend(new BigDecimal(dividend));
						popularity = CommonFun.GetStrFromPatter(popularity, "(\\d{1,9})", 1);
						if(popularity!=null)
							p.setPopularity(new BigDecimal(popularity));
						p.setExtractTime(extractTime);
						DBAccess.save(p);
//						logger.info(betTypeName+" ::: "+comb+" ::: "+combination+" ::: "+dividend+" ::: "+popularity);
					}
				}
			}
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	private Byte GetByte(Hashtable ht, String key)
	{
		if(ht.containsKey(key)&&CommonFun.isNumber(ht.get(key).toString()))
			return new Byte(ht.get(key).toString());
		else
			return null;
	}
	
	private void parsePost(String yyyyMMdd, String trackId, String raceNo, String pageContent)
	{
		try
		{			
			while(trackId.length()<3)
				trackId = "0"+trackId;
			while(raceNo.length()<2)
				raceNo = "0"+raceNo;
			
			Date extractTime = new Date();
			
			parser.setInputHTML(pageContent);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			if(nodelist!=null && nodelist.size()>0)
			{
				AutoRacePostRaceRace post = new AutoRacePostRaceRace();
				
				TableTag tablePreLine = (TableTag) nodelist.elementAt(0);
//				logger.info(tablePreLine.toHtml());
				if(tablePreLine.getRowCount()>1&&tablePreLine.getRows()[1].getColumnCount()==8)
				{		
					
					TableRow[] rows = tablePreLine.getRows();
					String humidity = rows[2].getColumns()[5].toPlainTextString().trim();
//					logger.info("humidity == "+humidity);
					String raceCategory = rows[2].getColumns()[1].children().nextNode().toPlainTextString().trim();
//					logger.info("raceCategory == "+raceCategory);
//					String telebetCutOffTime = rows[0].getColumns()[0].toPlainTextString().trim();
//					telebetCutOffTime = CommonFun.GetStrFromPatter(telebetCutOffTime, "\\d{1,2}:\\d{2}", 0);
//					logger.info("telebetCutOffTime == "+telebetCutOffTime);
//					String scheduledStartTime = rows[1].getColumns()[3].toPlainTextString().trim();
//					String scheduledStartTime = telebetCutOffTime;
//					logger.info("scheduledStartTime == "+scheduledStartTime);
					String temperature = rows[2].getColumns()[4].toPlainTextString().trim();
//					logger.info("temperature == "+temperature);
					String temperatureTrack = rows[2].getColumns()[6].toPlainTextString().trim();
//					logger.info("temperatureTrack == "+temperatureTrack);
//					String totalMeetingDay = 
					String going = rows[2].getColumns()[7].toPlainTextString().trim();
//					logger.info("going == "+going);
					String weather = rows[2].getColumns()[3].toPlainTextString().trim();
//					logger.info("weather == "+weather);
					String distance = rows[2].getColumns()[2].toPlainTextString().trim();
//					logger.info("distance == "+distance); 
//					String raceDesc = rows[2].getColumns()[1].toPlainTextString().trim();
//					logger.info("raceDesc == "+raceDesc); 
					String title = rows[0].getHeaders()[0].toPlainTextString().trim();
					
					long raceId = Long.parseLong(yyyyMMdd+trackId+raceNo);					
					Long uraceId = new Long(yyyyMMdd+"24"+trackId+raceNo);
					post.setUraceId(uraceId);
					post.setRaceId(raceId);					
					post.setRaceNo(new Byte(raceNo));
					post.setRaceDate(DF_yyyyMMdd.parse(yyyyMMdd));
					post.setTrackId(new Short(trackId));
					post.setTrackName(App.trackHt.get(trackId).toString());
					post.setRaceName(title.split("　")[1]);
//					post.setRaceDesc(raceDesc);
//					pre.setClassId(classId);
		//			pre.setClassName(className);
					post.setRaceCategory(raceCategory);
//					post.setNoOfRound(noOfRound);
					post.setDistance(new Integer(distance.replaceFirst("ｍ", "")));
//					pre.setGrade(grade);		
					post.setWeather(weather);
					post.setTemperature(temperature);
					post.setTemperatureTrack(temperatureTrack);
					post.setHumidity(humidity);
					post.setGoing(going);
//					post.setTrophy(trophy);
					post.setCancelled(new Boolean(false));
					post.setTotalMeetingDay(this.GetTotalMeetingDay(title));	
		//			pre.setBettypeNameList(bettypeNameList);					
					post.setExtractTime(extractTime);
					DBAccess.save(post);
				}
				if(nodelist.size()<4)
					return;
				
				TableTag table1 = (TableTag) nodelist.elementAt(3);
				if(table1.getRowCount()>6)
				{
					logger.info("update AutoRace_PostRace_Player set scratch = 1 where raceid = "+post.getRaceId());
					db.getResultByUpdate("update AutoRace_PostRace_Player set scratch = 1 where raceid = "+post.getRaceId());				
					
					TableRow[] rows = table1.getRows();
					for(int i=1;i<rows.length;i++)
					{
//						String playerid = CommonFun.GetStrFromPatter(rows[i].getColumns()[3].toHtml(),"/netstadium/Profile/(\\d{1,9})\"",1);
						String clothNo = rows[i].getColumns()[2].toPlainTextString().trim();
						if(clothNo!=null)
						{
							String playerid = this.GetPlayerID(post.getRaceId()+"", clothNo);
							if(playerid==null)
							{
								logger.error("!!!!!!!!!!!!!!!!! playerid = "+playerid+" not in prerace_player。。。");
								continue;
							}
							int playerId = Integer.parseInt(playerid);
							String finishPosition = rows[i].getColumns()[0].toPlainTextString().trim();
							String accidentName = rows[i].getColumns()[1].toPlainTextString().trim();
//							String clothNo = rows[i].getColumns()[2].toPlainTextString().trim();
							String playerName = convertPlayerName(rows[i].getColumns()[3].toPlainTextString().trim());
							String playerNameEn = rows[i].getColumns()[4].toPlainTextString().trim();
							String motorName = rows[i].getColumns()[5].toPlainTextString().trim();
							String handicap = rows[i].getColumns()[6].toPlainTextString().trim();							
							String trialRunT = CommonFun.GetStrFromPatter(rows[i].getColumns()[7].toPlainTextString().trim(),"([0-9\\.]{1,8})",1);
							String raceRunT = rows[i].getColumns()[8].toPlainTextString().trim();
							String st = rows[i].getColumns()[9].toPlainTextString().trim();
							String different = rows[i].getColumns()[10].toPlainTextString().trim();
//							if(finishPosition.equals("8"))
//								logger.info("come here");
							AutoRacePostRacePlayer prep = new AutoRacePostRacePlayer();
							AutoRacePostRacePlayerId prepid = new AutoRacePostRacePlayerId();
							prepid.setRaceId(post.getRaceId());
							prepid.setPlayerId(playerId);							
							prep.setId(prepid);
							prep.setUraceId(post.getUraceId());
							prep.setPlayerName(playerName);	
							prep.setPlayerNameEn(playerNameEn);
							prep.setClothNo(new Byte(clothNo));
							prep.setFinishPosition(CommonFun.isNumber(finishPosition)?new Byte(finishPosition):null);
							prep.setAccidentName(accidentName);
							prep.setHandicap(CommonFun.isNumber(handicap)?new BigDecimal(handicap):null);
							prep.setMotorName(motorName);
							prep.setTrialRunT(trialRunT==null?null:new BigDecimal(trialRunT));
							prep.setRaceRunT(CommonFun.isDecimal(raceRunT)?new BigDecimal(raceRunT):null);
							prep.setSt(CommonFun.isDecimal(st)?new BigDecimal(st):null);
							prep.setStartRemark(different);
							prep.setScratch(new Boolean(false));
							prep.setExtractTime(extractTime);
							DBAccess.save(prep);
						}
					}
				}
			}
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public String GetPlayerID(String raceid,String clothno)
	{
		try
		{
			String sql = "select playerid from autorace_prerace_player where raceid = "+raceid+" and clothno = "+clothno;
			Vector myv = db.getVectorBySelect(sql);
			if (myv.size() >= 3) {
				Vector myv0 = (Vector) myv.get(2);
				return myv0.get(0).toString();
			}			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}
	
//	private  CodePlayer parsePlayer(String playercode)
	private AutorRaceCodePlayer parsePlayer(String playercode)
	{
		try
		{
//			http://autorace.jp/new_netstadium/ns/Profile/Display/?player_code=2223
//			http://www.autorace.jp/netstadium/Profile/2201
			String url = "http://www.autorace.jp/netstadium/Profile/"+playercode;
			if(this.CanOpen(url, 3))
			{
				String h2title = CommonFun.GetStrFromPatter(page.getBody(), "<h3[^<]+?>(.+?)</h3>", 1);
				if(h2title.split("・").length==3)
				{
					parser.setInputHTML(page.getBody());
					NodeFilter filter_tab = new TagNameFilter("table");
					NodeList nodelist = parser.parse(filter_tab);
					if(nodelist!=null && nodelist.size()>0)
					{
						TableTag table = (TableTag) nodelist.elementAt(1);
						if(table.getRowCount()==13)
						{
							TableRow[] rows = table.getRows();
							
							String playerName = convertPlayerName(h2title.split("・")[0].trim());
							String playerNameEn = h2title.split("・")[2].trim();
							String playerOrigin = rows[0].getColumns()[0].toPlainTextString().trim();
							Date playerBirthday = df_ymd.parse(rows[2].getColumns()[0].toPlainTextString().trim());
							Date registrationDate = df_ymd.parse(rows[3].getColumns()[0].toPlainTextString().trim());
							String registrationNo = rows[4].getColumns()[0].toPlainTextString().trim();
							String period = rows[5].getColumns()[0].toPlainTextString().trim();
							String lg = rows[6].getColumns()[0].toPlainTextString().trim();
							String ownedCar = rows[7].getColumns()[0].toPlainTextString().trim().replaceAll("\\s+", "");
							String playerHeight = rows[8].getColumns()[0].toPlainTextString().trim().replaceFirst("cm", "");
							String playerWeight = rows[9].getColumns()[0].toPlainTextString().trim().replaceFirst("kg", "");
							String bloodType = rows[10].getColumns()[0].toPlainTextString().trim();
							String constellation = rows[11].getColumns()[0].toPlainTextString().trim();
							String hobby = rows[12].getColumns()[0].toPlainTextString().trim();
							
//							CodePlayer cp = new CodePlayer();
							AutorRaceCodePlayer cp = new AutorRaceCodePlayer();
							cp.setPlayerId(Integer.parseInt(playercode));
							cp.setPlayerName(playerName);
							cp.setPlayerNameEn(playerNameEn);
							cp.setPlayerOrigin(playerOrigin);
							cp.setPlayerBirthday(playerBirthday);
							cp.setRegistrationDate(registrationDate);
							cp.setRegistrationNo(new Integer(registrationNo));
							cp.setPeriod(period);
							cp.setLg(lg);
							cp.setOwnedCar(ownedCar);
							cp.setPlayerHeight(new BigDecimal(playerHeight));
							cp.setPlayerWeight(new BigDecimal(playerWeight));
							cp.setBloodType(bloodType);
							cp.setConstellation(constellation);
							cp.setHobby(hobby);	
//							DBAccess.save(cp);
							cp.setExtracttime(new Date());
							String sql = ReflectUtil.method(cp);
							db.ExecStoredProcedures("pr_AutoRace_code_player_InsertData", sql);
							return cp;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
		return null;
	}
	
	public String convertPlayerName(String playerName){
		try {
			if(playerName.endsWith("　")){
				playerName = playerName.substring(0,playerName.lastIndexOf("　"));
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return playerName;
	}
	
	public static void main( String[] args)
    {
		PropertyConfigurator.configure("config/log4j.properties");
//		new App();
////		new RaceTask().runTestDiv("2010071600108", false);
//		String str[] = {""};
//		for(int i = 0; i<str.length;i++){
//			
//		}
//		String pathName ="D:\\Denis\\jpMotor";
//		List FileList = getFile(pathName);
//		for(int i=0;i<FileList.size();i++){
//			String fileName =(String)FileList.get(i);
//			new RaceTask().parsePagefromLocal(fileName);
//		}
//		new RaceTask().runTestOdds("2015070100201", false); //2006-10-01_10
//		new RaceTask().runTestOdds("2015102000201", false); //2006-10-01_10
//		new RaceTask().runTestFinalOdds("2015040800203", false);
//		new RaceTask().runTestPre("2010071600108", false);
//		new RaceTask().runTestPreLive("2010071600108", false);
//		new RaceTask().runTestPost("2015040600208", true);
		List list =DBAccess.GetObjListByHql("from CodePlayer order by 1");
		if(list!=null)
		{
			Iterator it = list.iterator();
			while(it.hasNext())
			{
				CodePlayer player = (CodePlayer)it.next();
				new RaceTask().runTestPlayer(player.getPlayerId()+"");
			}	
		}
//		new RaceTask().parseFromRaceID("2015040600203",true);
//		new RaceTask().parseFromID("FinalOdds_2009053000508");
    }

}
