package jp.autorace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;

import jp.autorace.dc.AutoRacePreRacePlayerForcast;
import jp.autorace.dc.AutoRacePreRacePlayerForcastId;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

import poseidon.bot.PageHelper;
import poseidon.common.CommonFun;
import poseidon.config.Proxy;
import poseidon.config.SpiderDigester;
import poseidon.db.DBAccess;
import poseidon.db.ZTStd;

public class App 
{
	public static Logger logger = Logger.getLogger(App.class.getName());
	public static SpiderDigester config = new SpiderDigester();
	public PageHelper page = null;
	public ZTStd db = null;
	private Parser parser = null;
	private static ProxyPool pp = null;
	
	static int proxyindex = 0;
	static Vector proxyv = null;
	static Vector proxyvCanUsed = null;
	static long openPageCount = 0;
	boolean isExit = false;
	static int openPageFalseCount = 0;
	
	public static Hashtable trackHt = new Hashtable();
	public static Hashtable betTypeHt = new Hashtable();
	public static Hashtable trackNameHt = new Hashtable();
	public static Hashtable dayHt = new Hashtable();
	
//	public static String TypeName = null;
	public static String TypeName = "";
	public static String DateFrom = null;
	public static String DateTo = null;
	public static boolean EnUseProxy = false;
//	public static boolean EnUseProxy = true;
	public static String Path = null;
	
//	public static boolean EnOverWrite = false;
	public static boolean EnOverWrite = true;
	public static Vector DateVector = new Vector();
	public static Vector RaceIDVector = new Vector();
	public static int MulThreadNum = 2;
	
	public DateFormat DF_MMdd = new SimpleDateFormat("MMdd");
	public DateFormat DF_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	public DateFormat DF_yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHHmm");
	public DateFormat DF_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	public DateFormat DF_yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	public DateFormat DF_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public DateFormat DF_yyyy_MM_dd_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public DateFormat DF_dd = new SimpleDateFormat("dd");

    public static void main( String[] args )
    {
		PropertyConfigurator.configure("config/log4j.properties");
		
		App app = new App();
				
		if(ParseParameter(args)==false)
		{
			logger.info("error command parameter: "+Arrays.toString(args));
			return;
		}
		
		if(App.TypeName.toUpperCase().equals("HELP"))
		{
    		PrintHelpMessage();
		}
		//
		else if(App.TypeName.toUpperCase().equals("Sample_TrackMainPage".toUpperCase()))
		{
			app.runSampleTrackMainPageTest();
		}
		//
		else if(App.TypeName.toUpperCase().equals("RaceNoFromFile".toUpperCase()))
		{
			app.runRaceNoFromFileTest();
		}
		//
		else if(App.TypeName.toUpperCase().equals("TODAY"))
		{
			app.run();
		}
		//读取odds的数据 
		else if(App.TypeName.toUpperCase().equals("ODDS"))
		{
			app.runTodayOdds();
		}
		//读取Pre的数据 
		else if(App.TypeName.toUpperCase().equals("RCFN"))
		{
			app.runPre();
		}
		else if(App.TypeName.toUpperCase().equals("RCLFN"))
		{
			app.runPreLive();
		}
		//
		else if(App.TypeName.toUpperCase().equals("RRFN"))
		{
			app.runPost();
		}
		//
		else if(App.TypeName.toUpperCase().equals("RDFN"))
		{
			app.runDividend();
		}
		//
		else if(App.TypeName.toUpperCase().equals("ROFN"))
		{
			app.runOdds();
		}
		//
		else if(App.TypeName.toUpperCase().equals("RFOFN"))
		{
			app.runFinalOdds();
		}
		//
		else if(App.TypeName.toUpperCase().equals("FORCAST"))
		{
			app.runForcast();
		}
		//
		else if(App.TypeName.toUpperCase().equals("FD"))
		{
			app.runFromDir();
		}
		//
		else if(App.TypeName.toUpperCase().equals("RRUS"))
		{
			app.runFromRaceIDSql(App.config.getParaHash().get("UpdateSqlPost").toString());
		}
		//
		else if(App.TypeName.toUpperCase().equals("RCUS"))
		{
			app.runFromRaceIDSql(App.config.getParaHash().get("UpdateSqlPre").toString());
		}	
		//
		else if(App.TypeName.toUpperCase().equals("RCLUS"))
		{
			app.runFromRaceIDSql(App.config.getParaHash().get("UpdateSqlPreLive").toString());
		}	
		//
		else if(App.TypeName.toUpperCase().equals("ROUS"))
		{
			app.runFromRaceIDSql(App.config.getParaHash().get("UpdateSqlOdds").toString());
		}
		//
		else if(App.TypeName.toUpperCase().equals("RFOUS"))
		{
			app.runFromRaceIDSql(App.config.getParaHash().get("UpdateSqlFinalOdds").toString());
		}
		//
		else if(App.TypeName.toUpperCase().equals("RDUS"))
		{
			app.runFromRaceIDSql(App.config.getParaHash().get("UpdateSqlDividend").toString());
		}
		//
		else if(App.TypeName.toUpperCase().equals("PUS"))
		{
			app.runFromRaceIDSql(App.config.getParaHash().get("UpdateSqlPlayer").toString());
		}
		//
		else if(App.TypeName.toUpperCase().equals("PRINT"))
		{
			app.runPrint(App.Path);
		}
		//
		else if(App.TypeName.toUpperCase().equals("TEST"))
		{
			try
			{
				Object o = new Boolean(true);
				logger.info(o.getClass().getName());
			}
			catch(Exception e)
			{
				logger.error(e);
			}
		}
		else
		{
			PrintHelpMessage();
		}
    }
    
    private void runRaceNoFromFileTest()
    {
		try
		{
			File dir = new File("C:\\workplace\\autorace\\autorace"); 
	        File[] files = dir.listFiles(); 
	        logger.info("total "+files.length+" sample page...");
	        
	        for(int i=0;i<files.length;i++)
	        {
	        	String filenamefull = files[i].getPath();
	        	String filename = files[i].getName();
            	if(filenamefull.endsWith(".html"))
            	{
            		String html = CommonFun.ReadFile(filenamefull);
            		String curRaceNo = CommonFun.GetStrFromPatter(html, "<a href=\"#\" class=\"racenum_box current\"><span class=\"racenum_l\">(\\d{1,2})</span><span class=\"racenum_s\">R</span></a>", 1);
            		if(curRaceNo.length()<2)
            			curRaceNo = "0"+curRaceNo;
            		String fileRaceNo = filename.split("_")[2];
            		if(curRaceNo.equals(fileRaceNo))
            			logger.info(filename+" ::: "+fileRaceNo+" ::: "+curRaceNo+" **********************************************************************");
            		else
            			logger.info(filename+" ::: "+fileRaceNo+" ::: "+curRaceNo);
            	}
	        }
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}

	private void runSampleTrackMainPageTest() 
    {
		try
		{
			String filePath = CommonFun.GetCurrPath()+CommonFun.SYS_SEPARATOR+"SamplePage"+CommonFun.SYS_SEPARATOR+"TrackMainPage";
			Date curTime = new Date();
			String yyyyMMdd = DF_yyyyMMdd.format(curTime);
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
				String trackname = trackNameHt.get(i+"").toString();
				String fileName = yyyyMMdd+"_"+trackid+"_"+trackname+".html";
				String filePathName = filePath+CommonFun.SYS_SEPARATOR+fileName;
				if(new File(filePathName).exists()==true)
					continue;
				if(this.CanOpen("http://www.autorace.jp/netstadium/Live/"+trackname, 3))
				{
					CommonFun.OutToFileByte(filePathName, page.getBodyBytes(), true);				
				}
			}
			File dir = new File(filePath); 
	        File[] files = dir.listFiles(); 
	        logger.info("total "+files.length+" sample page...");
	        for(int i=0;i<files.length;i++)
	        {
	        	String filename = files[i].getPath();
            	if(filename.endsWith(".html"))
            	{
            		String html = CommonFun.ReadFile(filename);
//            		logger.info(html);
            		String htmlRaceNo = CommonFun.GetStrFromPatter(html, "(<td[^<]*?>(<[^<]+?>)?\\d{1,2}(</[^<]+?>)?(<[^<]+?>)?R(</[^<]+?>)?</td>\\s+){3,12}", 0);
            		String htmlTimeList = CommonFun.GetStrFromPatter(html, "(<td>(<[^<]+?>| )\\d{2}:\\d{2}?( |</[^<]+?>)?</td>\\s+){3,12}", 0);
            		if(htmlRaceNo==null||htmlTimeList==null)
            		{
            			logger.info(" =====> no race in " + filename+", continue...");
            			continue;
            		}
            		String htmlTable = "<table><tr>"+htmlRaceNo+"</tr><tr>"+htmlTimeList+"</tr></table>";
            		parser.setInputHTML(htmlTable);
        			NodeFilter filter_tab = new TagNameFilter("table");
        			NodeList nodelist = parser.parse(filter_tab);
        			if(nodelist!=null && nodelist.size()>0)
        			{			
        				TableTag timeLineTable = (TableTag) nodelist.elementAt(0);
        				TableRow rowRaceNo = timeLineTable.getRow(0);
        				TableRow rowRaceTime = timeLineTable.getRow(1);
        				for(int j=0;j<rowRaceNo.getColumnCount();j++)
        				{
        					String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
        					if(raceno!=null)
        					{
        						if(raceno.length()<2)
        							raceno = "0"+raceno;        						
        						String racetime = rowRaceTime.getColumns()[j].toPlainTextString().trim();
        						logger.info("R"+raceno+" ::: "+racetime);
        					}
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
	//初始化
	public App()
	{
		//读取配置文件
		config.digester();	
		pp = new ProxyPool();
		page = new PageHelper();
		try {
			String hostIp = InetAddress.getLocalHost().getHostAddress();
			logger.info(hostIp);
			if (hostIp.startsWith("192.168.60")){
				page.setProxy("192.168.60.2", 8080);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		db = new ZTStd();
		parser = new Parser();		
		init();
	}
    
    public void runPrint(String path)
	{
		try 
		{
			File dir = new File(path); 
	        File[] files = dir.listFiles(); 
	        if (files == null) 
	            return; 
	        for (int i = 0; i < files.length; i++) { 
	            if (files[i].isDirectory())
	            { 
	            	runPrint(files[i].getPath());
	            } 
	            else
	            { 
	            	String fullfilename = files[i].getPath();
	            	if(fullfilename.endsWith(".html"))
	            	{
	            		String filename =  files[i].getName();
	            		if(filename.length()<20)
	            		{
//	           				20090327_3_1.html
	            			String[] array = filename.split("_");
	            			String yyyyMMdd = array[0];
	            			String trackid = array[1];
	            			while(trackid.length()<3)
	            				trackid="0"+trackid;
	            			String raceno = array[2].split("\\.")[0];
	            			if(raceno.length()<2)
	            				raceno = "0"+raceno;
	            			String newfilename = yyyyMMdd+"_"+trackid+"_"+raceno+".html";
	            			String fullnewfilename = fullfilename.replaceFirst(filename, newfilename);
	            			logger.info(fullfilename+" => "+fullnewfilename);
	            			if(App.EnOverWrite == true)
	            			{
	            				files[i].renameTo(new File(fullnewfilename));
	            			}
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
    
    public static boolean ParseParameter(String[] args)
    {
    	try
    	{
	    	Vector v = new Vector();
	    	Vector subV = null;
	    	//输入的参数时候是以- 开头 如果是 
	    	for(int i=0;i<args.length;i++)
	    	{
	    		if(args[i].startsWith("-"))
	    		{
	    			if(subV!=null)
	    			{
	    				v.add(subV);	
	    			}
	    			subV = new Vector();
	    		}
	    		subV.add(args[i]);
	    	}
	    	
	    	if(subV!=null)
	    		v.add(subV);
	    	
	    	for(int i=0;i<v.size();i++)
	    	{
	    		subV = (Vector)v.get(i);	    		
	    		String pname = subV.get(0).toString();
	    		String pvalue = null;
	    		if(subV.size()>1)
	    			pvalue = subV.get(1).toString();
	    		
	    		if(pname.equals("-t")||pname.equals("--typename"))
	    		{
	    			App.TypeName = pvalue;
	    		}
	    		else if(pname.equals("-P")||pname.equals("--Proxy"))
	    		{
	    			App.EnUseProxy = true;	    			    			
	    			if(pvalue==null)
	    			{
	    				pp.LoadProxy();
	    			}else{
	    				if(pvalue.equals("1")){
	    					pp.LoadProxy();
	    				}else{
	    					
	    					App.EnUseProxy = false;
	    				}
	    			}
	    		}
	    		else if(pname.equals("-p")||pname.equals("--path"))
	    		{
	    			App.Path = pvalue;
	    		}
	    		else if(pname.equals("-OW")||pname.equals("--OverWrite"))
	    		{
	    			App.EnOverWrite = true;
	    			if(pvalue!=null)
	    			{
	    				if(pvalue.equals("0"))
	    				{
	    					App.EnOverWrite = false;
	    				}
	    			}
	    		}
	    		else if(pname.equals("-id")||pname.equals("--raceid"))
	    		{
	    			for(int j=1;j<subV.size();j++)
	    			{
	    				App.RaceIDVector.add(subV.get(j).toString());
	    			}
	    		}
	    		else if(pname.equals("-d")||pname.equals("--date"))
	    		{
	    			for(int j=1;j<subV.size();j++)
	    			{
	    				App.DateVector.add(subV.get(j).toString());
	    			}
	    		}
	    		else if(pname.equals("-df")||pname.equals("--datefrom"))
	    		{
	    			App.DateFrom = pvalue;
	    		}
	    		else if(pname.equals("-dt")||pname.equals("--dateto"))
	    		{
	    			App.DateTo = pvalue;
	    		}
	    		else if(pname.equals("-m")||pname.equals("--multhread"))
	    		{
	    			App.MulThreadNum = Integer.parseInt(pvalue);
	    		}
	    	}
	    	return true;
    	}
    	catch(Exception e)
    	{
    		PrintHelpMessage();
    	}
    	return false;
    }
    
    public static void PrintHelpMessage()
	{
		StringBuffer help_message = new StringBuffer("command parameter should be :\r\n");
		
		help_message.append("-t, --parsetype : parse data type name\n");
		help_message.append("-P, --Proxy : use proxy is true\n");
		help_message.append("-OW, --OverWrite : overwrite historyfile is true\n");	
		help_message.append("-id, --raceid : raceid array, split from bank\n");
		help_message.append("-d, --date : datetime array, format is yyyyMMdd, split from bank\n");
		help_message.append("-df, --datefrom : start datetime, format is yyyyMMdd\n");
		help_message.append("-dt, --dateto : end datetime, format is yyyyMMdd\n");
		help_message.append("-m, --multhread : thread num\n");
		help_message.append("-p, --path : history file path\n");
		
		help_message.append("use -t as:\r\n");
		help_message.append("java -jar autorace-1.0.jar -t [type]\n");
		help_message.append("-------------------------------------------------------------\n");
		help_message.append("type = TODAY ::: Today RaceCard,RaceResult,Dividend From Net\r\n");
		help_message.append("type = ODDS ::: Today Odds From Net\r\n");
		help_message.append("type = RCFN ::: RaceCard From Net\r\n");
		help_message.append("type = RRFN ::: RaceResult From Net\r\n");
		help_message.append("type = ROFN ::: RaceOdds From Net\r\n");
		help_message.append("type = RFOFN ::: RaceFinalOdds From Net\r\n");
		help_message.append("type = RDFN ::: RaceDividend From Net\r\n");
		help_message.append("-------------------------------------------------------------\n");
		help_message.append("type = FD ::: Parse Data From File Directory\r\n");
		help_message.append("-------------------------------------------------------------\n");
		help_message.append("type = RCUS ::: RaceCard From SQL\r\n");
		help_message.append("type = RRUS ::: RaceResult From SQL\r\n");
		help_message.append("type = ROUS ::: RaceOdds From SQL\r\n");
		help_message.append("type = RFOUS ::: RaceFinalOdds From SQL\r\n");
		help_message.append("type = RDUS ::: RaceDividend From SQL\r\n");
		help_message.append("type = PUS ::: Player From SQL\r\n");
		
		logger.warn(help_message.toString());
	}
	 
    public boolean CanOpen(String url, int openCount)
	{		
		if(App.EnUseProxy==true)
		{			
			if(pp.size()==0){
				logger.error("no proxy to use...");
				return false;
			}
			
			if(openPageCount%50 == 0)
			{
				page = pp.GetProxyPage();
				if(page == null){
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
	/**
	 * 将 TrackName 与 TrackName 添加到 trackHt中
	 * 将 English TrackName 添加到 trackNameHt 
	 * 将 奖金的方式添加到 betTypeHt 中
	 * */
	private void init()
	{
		trackHt.put("001", "船橋");
		trackHt.put("002", "川口");
		trackHt.put("003", "伊勢崎");
		trackHt.put("004", "浜松");
		trackHt.put("005", "飯塚");
		trackHt.put("006", "山陽");		
		
		betTypeHt.put("単勝", "1");
		betTypeHt.put("複勝", "2");
		betTypeHt.put("2連単", "3");
		betTypeHt.put("2連複", "4");
		betTypeHt.put("3連単", "5");
		betTypeHt.put("3連複", "6");
		betTypeHt.put("ワイド", "7");
		
//		TrackID	TrackName	EnglishName
//		1	船橋	funabashi
//		2	川口	kawaguchi
//		3	伊勢崎	isesaki
//		4	浜松	hamamatsu
//		5	飯塚	iizuka
//		6	山陽	sanyou
		
		trackNameHt.put("1","funabashi");
		trackNameHt.put("2","kawaguchi");
		trackNameHt.put("3","isesaki");
		trackNameHt.put("4","hamamatsu");
		trackNameHt.put("5","iizuka");
		trackNameHt.put("6","sanyou");	
		
		dayHt.put("１","1");
		dayHt.put("２","2");
		dayHt.put("３","3");
		dayHt.put("４","4");
		dayHt.put("５","5");
		dayHt.put("６","6");
		dayHt.put("７","7");
		dayHt.put("８","8");
		dayHt.put("９","9");
	}
	/**
	 * 
	 * */
	public void runFromRaceIDSql(String sql)
	{
		try
		{
			if(sql.length()==0)
				return;
			
			RacePool rp = new RacePool();
			
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp, null)).start();
			}
			
			Vector v = db.getVectorBySelect(sql);
			if(v.size()>2)
			{
				for(int i=2;i<v.size();i++)
				{
					String raceid = ((Vector)v.get(i)).get(0).toString();
					rp.AddID(raceid);
				}
			}

			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 访问 http://www.autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd --pre的url
	 * 得到 比赛时间 trackId 以及 raceNo 添加到 比赛池 Racepool中  
	 * */
	private void parsePre(RacePool rp, String yyyyMMdd)
	{
		try
		{
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
				if(this.CanOpen("http://www.autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd, 3))
				{
					parser.setInputHTML(page.getBody());
					NodeFilter filter_tab = new TagNameFilter("table");
					NodeList nodelist = parser.parse(filter_tab);
					if(nodelist!=null && nodelist.size()>0)
					{
						TableTag table = (TableTag) nodelist.elementAt(2);
						if(table.getRowCount()>0)
						{
							TableRow[] rows = table.getRows();
							TableRow rowRaceNo = rows[1];

							for(int j=0;j<rowRaceNo.getColumnCount();j++)
							{
								String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
								if(raceno!=null)
								{
									if(raceno.length()<2)
										raceno = "0"+raceno;		
									rp.AddID(yyyyMMdd+trackid+raceno);
								}
							}
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
	/**
	 * 访问的链接 http://www.autorace.jp/netstadium/Live/"+trackNameHt.get(i+"").toString()
	 * 
	 * */
	public void run()
	{
		
		try
		{
			String yyyyMMdd = DF_yyyyMMdd.format(new Date());
			
			RacePool rp = new RacePool();
			
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp)).start();
			}			
			//将访问的最终链接取到 Racepool中
			this.parseToday(rp);

			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(15*1000);
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(15*1000);
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(15*1000);
			
			logger.info("start to exec pr_Consolidate_PostRace_ByOneDay '"+yyyyMMdd+"','"+yyyyMMdd+"'");
			db.ExecStoredProcedures("pr_Consolidate_PostRace_ByOneDay","'"+yyyyMMdd+"','"+yyyyMMdd+"'");
			logger.info("exec pr_Consolidate_PostRace_ByOneDay over");
			
			logger.info("start to exec pr_Consolidate_PreRace_ByOneDay '"+yyyyMMdd+"','"+yyyyMMdd+"'");
			db.ExecStoredProcedures("pr_Consolidate_PreRace_ByOneDay","'"+yyyyMMdd+"','"+yyyyMMdd+"'");
			logger.info("exec pr_Consolidate_PreRace_ByOneDay over");
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public static void proxyTest()
	{
		
		try
		{
			PageHelper page = new PageHelper();
			StringBuffer sb = new StringBuffer("\r\n**************** proxy test result ****************\r\n");
			proxyvCanUsed = new Vector();
			proxyv = config.getProxys();
			for(int i=0;i<proxyv.size();i++)
			{
				Proxy p = (Proxy)proxyv.get(i);
				String host = p.getHost();
				int port = Integer.parseInt(p.getPort());
				String user = p.getUser();
				String password = p.getPassword();		
				if(user.length()>0)
					page.setProxy(host,port,user,password);
				else
					page.setProxy(host, port);
				
				String proxystr = "proxy: "+host+":"+port+":"+user+":"+password;
				
				if(page.CanOpen("http://autorace.jp/netstadium/Live/funabashi", 3))
				{
					sb.append(proxystr+" ::: OK\r\n");
					proxyvCanUsed.add(p);
				}
				else
				{
					sb.append(proxystr+" ::: NO\r\n");
				}
			}
			logger.info(sb.toString());
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runTodayOdds()
	{
		try
		{
			RacePool rp = new RacePool();
			this.parseTodayOdds(rp);
			new RaceTask("TaskThread",rp).runOdds();
//			new RaceTask("TaskThread",rp).run();
//			for(int i=1;i<=App.MulThreadNum;i++)
//			{
//				new Thread(new RaceTask("TaskThread-"+i,rp)).start();
//			}		
//			this.parseTodayOdds(rp);
//			for(int i=0;i<App.MulThreadNum;i++)
//			{
//				rp.AddID("exit");
//			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runPre()
	{
		try
		{
			Hashtable ht = new Hashtable();
			RacePool rp = new RacePool();
			
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp, null)).start();
			}			
			
			if(App.DateVector.size()>0)
			{
				for(int i=0;i<App.DateVector.size();i++)
				{
					String yyyyMMdd = App.DateVector.get(i).toString();					
					this.parsePre(rp, yyyyMMdd);
					
					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
				}
			}
			else if(App.DateFrom!=null&&App.DateTo!=null)
			{
				Date fromDate = DF_yyyyMMdd.parse(App.DateFrom);
				Date toDate = DF_yyyyMMdd.parse(App.DateTo);
				Date curDate = fromDate;
				while(curDate.before(toDate)||curDate.equals(toDate))
				{
					String yyyyMMdd = DF_yyyyMMdd.format(curDate);
					this.parsePre(rp, yyyyMMdd);
					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
					curDate = CommonFun.DateSub(curDate, 1);
				}		
			}
			else if(App.RaceIDVector.size()>0)
			{
				for(int i=0;i<App.RaceIDVector.size();i++)
				{
					rp.AddID(App.RaceIDVector.get(i).toString());
				}
			}
			else
			{
				String yyyyMMdd = DF_yyyyMMdd.format(new Date());
				this.parsePre(rp, yyyyMMdd);
				if(ht.containsKey(yyyyMMdd)==false)
					ht.put(yyyyMMdd, yyyyMMdd);
			}

			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(15*1000);
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(15*1000);
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(15*1000);
			
			Enumeration enu = ht.keys();
			while(enu.hasMoreElements())
			{
				String yyyyMMdd = enu.nextElement().toString();
				logger.info("start to exec pr_Consolidate_PreRace_ByOneDay '"+yyyyMMdd+"','"+yyyyMMdd+"'");
				db.ExecStoredProcedures("pr_Consolidate_PreRace_ByOneDay","'"+yyyyMMdd+"','"+yyyyMMdd+"'");
				logger.info("exec pr_Consolidate_PreRace_ByOneDay over");
			}
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public void runPreLive()
	{			
		try
		{
			RacePool rp = new RacePool();	
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp, null)).start();
			}			
			
			if(App.DateVector.size()>0)
			{
				for(int i=0;i<App.DateVector.size();i++)
				{
					String yyyyMMdd = App.DateVector.get(i).toString();					
					this.parsePreLive(rp, yyyyMMdd);
				}
			}
			else if(App.DateFrom!=null&&App.DateTo!=null)
			{
				Date fromDate = DF_yyyyMMdd.parse(App.DateFrom);
				Date toDate = DF_yyyyMMdd.parse(App.DateTo);
				Date curDate = fromDate;
				while(curDate.before(toDate)||curDate.equals(toDate))
				{
					String yyyyMMdd = DF_yyyyMMdd.format(curDate);
					this.parsePreLive(rp, yyyyMMdd);
					curDate = CommonFun.DateSub(curDate, 1);
				}		
			} else {
				String yyyyMMdd = DF_yyyyMMdd.format(new Date());
				this.parsePreLive(rp, yyyyMMdd);
			}

			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	
	public synchronized static PageHelper getPage()
	{
		try
		{
			PageHelper page = new PageHelper();
			page.setTimeout(30000);			
			
			proxyindex++;
			if(proxyindex==proxyvCanUsed.size())
			{
				proxyindex=0;		
			}
		
			setProxy((Proxy)proxyvCanUsed.get(proxyindex),page);
			if(checkProxy(page,(Proxy)proxyvCanUsed.get(proxyindex)))
				return page;
			else
				return getPage();
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		return null;
	}	
	
	private static boolean checkProxy(PageHelper page, Proxy p)
	{
		String proxystr = null;
		try
		{
			String host = p.getHost();
			int port = Integer.parseInt(p.getPort());
			String user = p.getUser();
			String password = p.getPassword();
			proxystr = "proxy: "+host+":"+port+":"+user+":"+password;
				
			if(page.PageOpen("http://www.autorace.jp"))
				return true;
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		logger.error("in autorace spider, "+proxystr + " can't open page");
		return false;
	}
	
	private static void setProxy(Proxy p,PageHelper page)
	{
		if(EnUseProxy)
		{
			String host = p.getHost();
			int port = Integer.parseInt(p.getPort());
			String user = p.getUser();
			String password = p.getPassword();		
			if(user.length()>0)
				page.setProxy(host,port,user,password);
			else
				page.setProxy(host, port);
			logger.info("use proxy "+host+":"+port+":"+user+":"+password);
		}
	}
	
	public void runFromDir()
	{
		if(App.Path==null)
			return;
		
		try
		{
			Hashtable ht = new Hashtable();
			
			if(App.DateVector.size()>0)
			{
				for(int i=0;i<App.DateVector.size();i++)
				{
					String yyyyMMdd = App.DateVector.get(i).toString();					

					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
				}
			}
			else if(App.DateFrom!=null&&App.DateTo!=null)
			{
				Date fromDate = DF_yyyyMMdd.parse(App.DateFrom);
				Date toDate = DF_yyyyMMdd.parse(App.DateTo);
				Date curDate = fromDate;
				while(curDate.before(toDate)||curDate.equals(toDate))
				{
					String yyyyMMdd = DF_yyyyMMdd.format(curDate);
					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
					curDate = CommonFun.DateSub(curDate, 1);
				}		
			}
			
			RacePool rp = new RacePool();			
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp)).start();
			}			
			
			this.runFromDir(App.Path, rp, ht);
			
			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}

		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 根据传入的文件路径解析页面
	 * */
	public void runFromDir(String path, RacePool rp, Hashtable ht)
	{
		try 
		{
			File dir = new File(path); 
	        File[] files = dir.listFiles(); 
	        
	        if (files == null) 
	            return; 
	        
	        for (int i = 0; i < files.length; i++) { 

	            if (files[i].isDirectory())
	            { 
	            	runFromDir(files[i].getPath(), rp, ht);
	            } 
	            else
	            { 
	            	String filename = files[i].getPath();
	            	if(filename.endsWith(".html"))
	            	{
	            		if(ht.size()>0)
	            		{
		            		String shortfilename = files[i].getName();
		            		String[] array = shortfilename.split("_");
		            		if(array.length>1)
		            		{
		            			String yyyyMMdd = array[0];
		            			if(ht.containsKey(yyyyMMdd)==false)
		            				continue;
		            		}
	            			
	            		}
	            		
	            		rp.AddID(filename);
	            	}
	            }
	        }
		} 
		catch (Exception e) 
		{
			logger.error(e);
		}
	}
	
	private void parsePreLive(RacePool rp, String yyyyMMdd)
	{
		try
		{
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
				if(this.CanOpen("http://www.autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd, 3))
				{
//						logger.info(page.getBody());
					parser.setInputHTML(page.getBody());
					NodeFilter filter_tab = new TagNameFilter("table");
					NodeList nodelist = parser.parse(filter_tab);
					if(nodelist!=null && nodelist.size()>0)
					{
						TableTag table = (TableTag) nodelist.elementAt(2);
	//					logger.info(table.toHtml());
						if(table.getRowCount()>0)
						{
							TableRow[] rows = table.getRows();
							TableRow rowRaceNo = rows[1];
							for(int j=0;j<rowRaceNo.getColumnCount();j++)
							{
								String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
								if(raceno!=null)
								{
									if(raceno.length()<2)
										raceno = "0"+raceno;		
									rp.AddID(yyyyMMdd+trackid+raceno);
//									String raceUrl = "http://www.autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+(j+1);
//									logger.info(raceno+" ::: "+raceUrl);
//									rp.AddID(yyyy_MM_dd+"__"+trackid+"__"+raceno+"__"+raceUrl+"__Live");		
								}
							}
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
	
	public void runTest()
	{
		try
		{
			page.setProxy("192.168.10.52", 8080);
			if(this.CanOpen("http://www.autorace.jp/", 3))
			{
				logger.info(page.getBody());
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 
	 * */
	public void runPreTest()
	{
		RacePool rp = new RacePool();
		int threadnum = Integer.parseInt(App.config.getParaHash().get("download_thread_num").toString());
		int runcycle = Integer.parseInt(App.config.getParaHash().get("runcycle_seconds").toString());
		
		try
		{
			for(int i=1;i<=threadnum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp,null)).start();
			}	
			
			while(true)
			{
				rp.Clear();				
				int index=0;
				for(int i=1;i<=6;i++)
				{
					String trackid = "00"+i;
					if(this.CanOpen("http://autorace.jp/netstadium/Program/kawaguchi/2014-05-11_"+i, 3))
					{
						logger.info(page.getBody());
						Date curTime = new Date();
						String tableHtml = CommonFun.GetStrFromPatter(page.getBody(), "<table[^<]*?id=\"tblRace\">.+?</table>", 0, 2);
						if(tableHtml!=null)
						{
			//				logger.info(tableHtml);
							parser.setInputHTML(tableHtml);
							NodeFilter filter_tab = new TagNameFilter("table");
							NodeList nodelist = parser.parse(filter_tab);
							if(nodelist!=null && nodelist.size()>0)
							{
								TableTag table = (TableTag) nodelist.elementAt(0);
			//					logger.info(table.toHtml());
								if(table.getRowCount()>0)
								{
									TableRow[] rows = table.getRows();
									TableRow rowRaceNo = rows[0];
									TableRow rowRaceTime = rows[1];
									TableRow rowRaceUrl = rows[2];
									
									for(int j=1;j<rowRaceNo.getHeaderCount();j++)
									{
										String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getHeaders()[j].toPlainTextString(), "\\d{1,2}", 0);
										if(raceno.length()<2)
											raceno = "0"+raceno;
										String racetime = rowRaceTime.getColumns()[j-1].toPlainTextString();
										if(racetime.length()==4)
											racetime = "0"+racetime;
										String shortRaceUrl = CommonFun.GetStrFromPatter(rowRaceUrl.getColumns()[j-1].toHtml(), "href=\"([^<]+?)\"", 1);
										if(shortRaceUrl!=null)
										{
											shortRaceUrl = shortRaceUrl.replaceFirst("Odds", "Program");
											String raceUrl = "http://www.autorace.jp"+shortRaceUrl;
											logger.info(raceno+" ::: "+racetime+" ::: "+raceUrl);
											
											String racedate = CommonFun.GetStrFromPatter(raceUrl, "d=(\\d{4}-\\d{2}-\\d{2})", 1);								
											index++;
											rp.AddID(racedate+"__"+trackid+"__"+raceno+"__"+raceUrl);
										}
									}
								}
							}
						}
					}
				}
				
				if(index==0)
	    		{
	    			for(int i=0;i<threadnum;i++)
	    			{
	    				rp.AddID("exit");
	    			}
	    			logger.info("no race will start after 40 minutes, exit...");
	    			return;
	    		}

    			logger.info("sleep "+runcycle+" seconds...");
    			Thread.sleep(runcycle*1000);
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 
	 * */
	public void runDividend()
	{
		try
		{
			Hashtable ht = new Hashtable();
			
			RacePool rp = new RacePool();
			
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp, null)).start();
			}			
			
			if(App.DateVector.size()>0)
			{
				for(int i=0;i<App.DateVector.size();i++)
				{
					String yyyyMMdd = App.DateVector.get(i).toString();					
					this.parsePost(rp, yyyyMMdd);
					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
				}
			}
			else if(App.DateFrom!=null&&App.DateTo!=null)
			{
				Date fromDate = DF_yyyyMMdd.parse(App.DateFrom);
				Date toDate = DF_yyyyMMdd.parse(App.DateTo);
				Date curDate = fromDate;
				while(curDate.before(toDate)||curDate.equals(toDate))
				{
					String yyyyMMdd = DF_yyyyMMdd.format(curDate);
					this.parsePost(rp, yyyyMMdd);
					curDate = CommonFun.DateSub(curDate, 1);
					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
				}		
			}
			else if(App.RaceIDVector.size()>0)
			{
				for(int i=0;i<App.RaceIDVector.size();i++)
				{
					rp.AddID(App.RaceIDVector.get(i).toString());
				}
			}
			else
			{
				String yyyyMMdd = DF_yyyyMMdd.format(new Date());
				this.parsePost(rp, yyyyMMdd);
				if(ht.containsKey(yyyyMMdd)==false)
					ht.put(yyyyMMdd, yyyyMMdd);
			}

			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(30*1000);

			Enumeration enu = ht.keys();
			while(enu.hasMoreElements())
			{
				String yyyyMMdd = enu.nextElement().toString();
				
				logger.info("start to exec pr_Consolidate_Dividend '"+yyyyMMdd+"','"+yyyyMMdd+"'");
				db.ExecStoredProcedures("pr_Consolidate_Dividend","'"+yyyyMMdd+"','"+yyyyMMdd+"'");
				logger.info("exec pr_Consolidate_Dividend over");				
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 
	 * */
	public void runPost()
	{
		try
		{
			Hashtable ht = new Hashtable();
			
			RacePool rp = new RacePool();
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp, null)).start();
			}			
			
			if(App.DateVector.size()>0)
			{
				for(int i=0;i<App.DateVector.size();i++)
				{
					String yyyyMMdd = App.DateVector.get(i).toString();
					this.parsePost(rp, yyyyMMdd);
					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
				}
			}
			else if(App.DateFrom!=null&&App.DateTo!=null)
			{
				Date fromDate = DF_yyyyMMdd.parse(App.DateFrom);
				Date toDate = DF_yyyyMMdd.parse(App.DateTo);
				Date curDate = fromDate;
				while(curDate.before(toDate)||curDate.equals(toDate))
				{
					String yyyyMMdd = DF_yyyyMMdd.format(curDate);
					this.parsePost(rp, yyyyMMdd);
					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
					curDate = CommonFun.DateSub(curDate, 1);
				}		
			}
			else if(App.RaceIDVector.size()>0)
			{
				for(int i=0;i<App.RaceIDVector.size();i++)
				{
					rp.AddID(App.RaceIDVector.get(i).toString());
				}
			}
			else
			{
				String yyyyMMdd = DF_yyyyMMdd.format(new Date());
				this.parsePost(rp, yyyyMMdd);
				if(ht.containsKey(yyyyMMdd)==false)
					ht.put(yyyyMMdd, yyyyMMdd);
			}

			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(3*60*1000);
			
			Enumeration enu = ht.keys();
			while(enu.hasMoreElements())
			{
				String yyyyMMdd = enu.nextElement().toString();
				logger.info("start to exec pr_Consolidate_PostRace_ByOneDay '"+yyyyMMdd+"','"+yyyyMMdd+"'");
				db.ExecStoredProcedures("pr_Consolidate_PostRace_ByOneDay","'"+yyyyMMdd+"','"+yyyyMMdd+"'");
				logger.info("exec pr_Consolidate_PostRace_ByOneDay over");
				
				logger.info("start to exec pr_Consolidate_Dividend '"+yyyyMMdd+"','"+yyyyMMdd+"'");
				db.ExecStoredProcedures("pr_Consolidate_Dividend","'"+yyyyMMdd+"','"+yyyyMMdd+"'");
				logger.info("exec pr_Consolidate_Dividend over");				
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 解析URL:http://autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()
	 * rp.AddID(yyyyMMdd+trackid+raceno);
	 * */
	private void parsePost(RacePool rp, String yyyyMMdd)
	{
		try
		{
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
					if(this.CanOpen("http://autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd, 3))
				{
					parser.setInputHTML(page.getBody());
					NodeFilter filter_tab = new TagNameFilter("table");
					NodeList nodelist = parser.parse(filter_tab);
					if(nodelist!=null && nodelist.size()>0)
					{
						TableTag table = (TableTag) nodelist.elementAt(2);
						if(table.getRowCount()>0)
						{
							TableRow[] rows = table.getRows();
							TableRow rowRaceNo = rows[1];

							for(int j=0;j<rowRaceNo.getColumnCount();j++)
							{
								String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
								if(raceno!=null)
								{
									if(raceno.length()<2)
										raceno = "0"+raceno;		
									rp.AddID(yyyyMMdd+trackid+raceno);
//									String raceUrl = "http://www.autorace.jp/netstadium/RaceResult/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+(j+1);
//									logger.info(raceno+" ::: "+raceUrl);								
//									rp.AddID(yyyy_MM_dd+"__"+trackid+"__"+raceno+"__"+raceUrl);		
								}
							}
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
	/**
	 * 
	 * */
	public void runForcast()
	{
		try
		{
			if(App.DateVector.size()>0)
			{
				for(int i=0;i<App.DateVector.size();i++)
				{
					String yyyyMMdd = App.DateVector.get(i).toString();					
					this.parseForcast(yyyyMMdd);
				}
			}
			else if(App.DateFrom!=null&&App.DateTo!=null)
			{
				Date fromDate = DF_yyyyMMdd.parse(App.DateFrom);
				Date toDate = DF_yyyyMMdd.parse(App.DateTo);
				Date curDate = fromDate;
				while(curDate.before(toDate)||curDate.equals(toDate))
				{
					String yyyyMMdd = DF_yyyyMMdd.format(curDate);
					this.parseForcast(yyyyMMdd);
					curDate = CommonFun.DateSub(curDate, 1);
				}		
			}
			else
			{
				String yyyyMMdd = DF_yyyyMMdd.format(new Date());
				this.parseForcast(yyyyMMdd);
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 
	 * */
	public void runOdds()
	{
		RacePool rp = new RacePool();
		
		try
		{
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp)).start();
			}	

			if(App.DateVector.size()>0)
			{
				for(int i=0;i<App.DateVector.size();i++)
				{
					String yyyyMMdd = App.DateVector.get(i).toString();					
					this.parseOdds(rp, yyyyMMdd);
				}
			}
			else if(App.DateFrom!=null&&App.DateTo!=null)
			{
				Date fromDate = DF_yyyyMMdd.parse(App.DateFrom);
				Date toDate = DF_yyyyMMdd.parse(App.DateTo);
				Date curDate = fromDate;
				while(curDate.before(toDate)||curDate.equals(toDate))
				{
					String yyyyMMdd = DF_yyyyMMdd.format(curDate);
					this.parseOdds(rp, yyyyMMdd);
					curDate = CommonFun.DateSub(curDate, 1);
				}		
			}
			else
			{
				String yyyyMMdd = DF_yyyyMMdd.format(new Date());
				this.parseOdds(rp, yyyyMMdd);
			}
				
			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 通过从配置sql语句 来获得需要更新的raceid 解析finalodds
	 * rp.AddID("FinalOdds_"+raceid);
	 * */
	public void runFinalOddsFromRaceIDSql()
	{
		RacePool rp = new RacePool();
		int threadnum = Integer.parseInt(App.config.getParaHash().get("download_thread_num").toString());
		int runcycle = Integer.parseInt(App.config.getParaHash().get("runcycle_seconds").toString());		
		Hashtable historyPageHt = new Hashtable();
		
		try
		{
			for(int i=1;i<=threadnum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp,historyPageHt)).start();
			}	
			
			String sql = "select raceid from AutoRace_PostRace_Race where RaceID not in (select RaceID from AutoRace_FinalE)";
			//如果配置文件中配置了sql语句 则使用配置文件中的语句 
			if(App.config.getParaHash().get("UpdateFromRaceIDSql").toString().length()>0)
				sql = App.config.getParaHash().get("UpdateFromRaceIDSql").toString();
			Vector v = db.getVectorBySelect(sql);
			if(v.size()>2)
			{
				for(int j=2;j<v.size();j++)
				{
					String raceid = ((Vector)v.get(j)).get(0).toString();
					rp.AddID("FinalOdds_"+raceid);
				}
			}
				
			for(int i=0;i<threadnum;i++)
			{
				rp.AddID("exit");
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 解析URL:http://www.autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()
	 * 获取FinalOdds链接 
	 * rp里面存放的是：rp.AddID(yyyyMMdd+trackid+raceno);
	 * */
	private void parseFinalOdds(RacePool rp, String yyyyMMdd)
	{
		try
		{
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
				if(this.CanOpen("http://www.autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd, 3))
				{
					parser.setInputHTML(page.getBody());
					NodeFilter filter_tab = new TagNameFilter("table");
					NodeList nodelist = parser.parse(filter_tab);
					if(nodelist!=null && nodelist.size()>0)
					{
						TableTag table = (TableTag) nodelist.elementAt(2);
						if(table.getRowCount()>0)
						{
							TableRow[] rows = table.getRows();
							TableRow rowRaceNo = rows[1];
							
							for(int j=0;j<rowRaceNo.getColumnCount();j++)
							{
								String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
								if(raceno!=null)
								{
									if(raceno.length()<2)
										raceno = "0"+raceno;										
									rp.AddID(yyyyMMdd+trackid+raceno);
//									String raceUrl = "http://www.autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+(j+1);
//									logger.info(raceno+" ::: "+raceUrl);								
//									rp.AddID("FinalOdds__"+yyyy_MM_dd+"__"+trackid+"__"+raceno+"__"+raceUrl);		
								}
							}
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
	/**
	 * 解析finalodds
	 * */
	public void runFinalOdds()
	{
		Hashtable ht = new Hashtable();
		RacePool rp = new RacePool();
		
		try
		{
			for(int i=1;i<=App.MulThreadNum;i++)
			{
				new Thread(new RaceTask("TaskThread-"+i,rp)).start();
			}			
			//DateVector程序运行时指定的日期参数
			if(App.DateVector.size()>0)
			{
				for(int i=0;i<App.DateVector.size();i++)
				{
					this.parseFinalOdds(rp, App.DateVector.get(i).toString());
					if(ht.containsKey(App.DateVector.get(i).toString())==false)
						ht.put(App.DateVector.get(i).toString(), App.DateVector.get(i).toString());
				}
			}
			//指定从那天开始 从那天结束 
			else if(App.DateFrom!=null&&App.DateTo!=null)
			{
				Date fromDate = DF_yyyyMMdd.parse(App.DateFrom);
				Date toDate = DF_yyyyMMdd.parse(App.DateTo);
				Date curDate = fromDate;
				while(curDate.before(toDate)||curDate.equals(toDate))
				{
					String yyyyMMdd = DF_yyyyMMdd.format(curDate);
					this.parseFinalOdds(rp, yyyyMMdd);
					if(ht.containsKey(yyyyMMdd)==false)
						ht.put(yyyyMMdd, yyyyMMdd);
					curDate = CommonFun.DateSub(curDate, 1);//从当前日期一直加到指定的日期
				}		
			}
			//没有指定日期 就直接去当天的odds
			else
			{
				String yyyyMMdd = DF_yyyyMMdd.format(new Date());
				this.parseFinalOdds(rp, yyyyMMdd);
				if(ht.containsKey(yyyyMMdd)==false)
					ht.put(yyyyMMdd, yyyyMMdd);
			}
			
			for(int i=0;i<App.MulThreadNum;i++)
			{
				rp.AddID("exit");
			}
			
			while(rp.Size()>0)
			{
				Thread.sleep(60*1000);
			}
			
			Thread.sleep(30*1000);
			//迭代出整合的日期 做整合操作 
			Enumeration enu = ht.keys();
			while(enu.hasMoreElements())
			{
				String yyyyMMdd = enu.nextElement().toString();
				String URaceIDFrom = yyyyMMdd+"0000000";
				String URaceIDTo = yyyyMMdd+"9999999";
				logger.info("start to exec pr_Consolidate_WinOdds "+URaceIDFrom+","+URaceIDTo);
				db.ExecStoredProcedures("pr_Consolidate_WinOdds","'"+yyyyMMdd+"','"+yyyyMMdd+"'");
				logger.info("exec pr_Consolidate_WinOdds over");			
			}
		}
		catch(Exception e)
		{
			logger.error(e);
		}
	}
	/**
	 * 解析 URL:"http://autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()
	 * 直接解析页面 保存对象 AutoRacePreRacePlayerForcast
	 * */
	private void parseForcast(String yyyyMMdd)
	{
		
		try
		{
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
				if(this.CanOpen("http://autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd, 3))
				{
					parser.setInputHTML(page.getBody());
					NodeFilter filter_tab = new TagNameFilter("table");
					NodeList nodelist = parser.parse(filter_tab);
					if(nodelist!=null && nodelist.size()>0)
					{							
						TableTag table = (TableTag) nodelist.elementAt(2);
						if(table.getRowCount()>0)
						{
							TableRow[] rows = table.getRows();
							TableRow rowRaceNo = rows[1];
							
							for(int j=0;j<rowRaceNo.getColumnCount();j++)
							{
								String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
								if(raceno!=null)
								{
									if(raceno.length()<2)
										raceno = "0"+raceno;	
									if(this.CanOpen("http://autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+raceno, 3))
									{
										String html = page.getBody();
										this.parseForcast(yyyyMMdd, trackid, raceno, html);
									}
								}
							}
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
	/**
	 * 
	 * */
	public void parseForcast(String yyyyMMdd, String trackId, String raceNo, String html)
	{
		try
		{
			while(trackId.length()<3)
				trackId = "0"+trackId;
			while(raceNo.length()<2)
				raceNo = "0"+raceNo;
			
			long raceId = Long.parseLong(yyyyMMdd+trackId+raceNo);					
			Long uraceId = new Long(yyyyMMdd+"24"+trackId+raceNo);
			
			Date extractTime = new Date();
			
//			logger.info(CommonFun.GetCurrPath()+"\\test.html");
			String Forcast_Visual = "";
			
			String patter = "<table class=\"tblMain tblBasic\"[^<]+?>.+?</table>";
			Matcher matcher = CommonFun.GetMatcherStrGroup(html, patter);
			if(matcher.find())
			{
				String table1Str = matcher.group(0);
//				logger.info(this.getSunRainStr(table1Str));
				Forcast_Visual = this.getSunRainStr(table1Str);
			}
			
			patter = "<table [^<]+?class=\"tblMain tblBasic\">.+?</table>";
			matcher = CommonFun.GetMatcherStrGroup(html, patter);
			if(matcher.find())
			{
//				logger.info(matcher.group(0));
				String content = matcher.group(0);
				content = content.replaceAll("<tr class=\"tr_sec\"><tr class=\"tr_sec\">", "<tr class=\"tr_sec\">");
				content = content.replaceAll("<\tr><\tr>", "<\tr>");
				content = content.replaceAll("</tr><td", "</tr><tr><td");
				parser.setInputHTML(content);
				NodeFilter filter_tab = new TagNameFilter("table");
				NodeList nodelist = parser.parse(filter_tab);
				if(nodelist!=null && nodelist.size()>0)
				{
					TableTag table = (TableTag) nodelist.elementAt(0);
					if(table.getRowCount()==9)
					{						
						TableRow[] rows = table.getRows();
						TableColumn[] cols = rows[0].getColumns();
						TableColumn[] colsSun = rows[1].getColumns();
//						logger.info(rows[1].toHtml());
						TableColumn[] colsRain = rows[2].getColumns();
//						logger.info(rows[2].toHtml());
						TableColumn[] colsPlayer = rows[3].getColumns();						
//						logger.info(rows[3].toHtml());
						TableColumn[] colsSL = rows[6].getColumns();
//						logger.info(rows[6].toHtml());
						for(int i=1;i<cols.length;i++)
						{
							String clothno = cols[i].toPlainTextString();
//							logger.info(clothno);
							String playerid = CommonFun.GetStrFromPatter(colsPlayer[i].toHtml(), "netstadium/Profile/(\\d{1,15})\"", 1);
//							logger.info(playerid);
							String Forcast_Fine = CommonFun.GetStrFromPatter(colsSun[i].toHtml(), "/ico_([^<]+?).png\"", 1);
//							logger.info(Forcast_Fine);
							String Forcast_Rain = CommonFun.GetStrFromPatter(colsRain[i].toHtml(), "/ico_([^<]+?).png\"", 1);
//							logger.info(Forcast_Rain);
							String Forcast_Strength = CommonFun.GetStrFromPatter(colsSL[i].toHtml(), "/ico_([^<]+?).png\"", 1);
//							logger.info(Forcast_Strength);
							
							AutoRacePreRacePlayerForcast ppf = new AutoRacePreRacePlayerForcast();
							AutoRacePreRacePlayerForcastId ppfid = new AutoRacePreRacePlayerForcastId();
							ppf.setId(ppfid);
							ppfid.setPlayerId(new Integer(playerid).intValue());
							ppfid.setRaceId(raceId);
							ppf.setClothNo(new Byte(clothno));
							ppf.setForcastFine(Forcast_Fine);
							ppf.setForcastRain(Forcast_Rain);
							ppf.setForcastStrength(Forcast_Strength);
							ppf.setForcastVisual(Forcast_Visual);
							ppf.setUraceId(uraceId);
							ppf.setExtractTime(extractTime);
							DBAccess.save(ppf);
							//改为 sp 存储数据 by Denis 20170314
//							ReflectUtil.method("pr_AutoRace_PreRace_Player_Forecast_InsertData",ppf);
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
	/**
	 * 
	 * */
	private String getSunRainStr(String html)
	{
		try
		{
			parser.setInputHTML(html);
			NodeFilter filter_tab = new TagNameFilter("table");
			NodeList nodelist = parser.parse(filter_tab);
			if(nodelist!=null && nodelist.size()>0)
			{
				TableTag table = (TableTag) nodelist.elementAt(0);
				if(table.getRowCount()>0)
				{						
					TableRow[] rows = table.getRows();
					if(rows.length==1)
					{
//						logger.info(rows[0].toHtml());
						
						StringBuffer sb = new StringBuffer();
						
						TableColumn[] cols = rows[0].getColumns();
						if(cols.length==5)
						{
							String coltext1 = cols[1].toPlainTextString();
//							logger.info(coltext1);
							
							sb.append(coltext1);
							
							NodeList nl = cols[2].getChildren();
							for(int i=0;i<nl.size();i++)
							{
//								logger.info(nl.toHtml());
								NodeList nll = nl.elementAt(i).getChildren();
								StringBuffer sbb = new StringBuffer();
								for(int j=0;j<nll.size();j++)
								{
//									logger.info(nll.elementAt(j).toHtml());
									String num = CommonFun.GetStrFromPatter(nll.elementAt(j).toHtml(), "sprite-num(\\d)", 1);
									if(num!=null)
									{
										sbb.append(num);
									}
									else
									{
										sbb.append(nll.elementAt(j).toPlainTextString());
									}
								}
//								logger.info(sbb.toString());
								sb.append(" "+sbb.toString());
							}
//							logger.info(sb.toString());
							
							String coltext2 = cols[3].toPlainTextString();
							
//							logger.info(coltext1);
							
							sb.append("  "+coltext2);
							
							nl = cols[4].getChildren();
							for(int i=0;i<nl.size();i++)
							{
//								logger.info(nl.toHtml());
								NodeList nll = nl.elementAt(i).getChildren();
								StringBuffer sbb = new StringBuffer();
								for(int j=0;j<nll.size();j++)
								{
//									logger.info(nll.elementAt(j).toHtml());
									String num = CommonFun.GetStrFromPatter(nll.elementAt(j).toHtml(), "sprite-num(\\d)", 1);
									if(num!=null)
									{
										sbb.append(num);
									}
									else
									{
										sbb.append(nll.elementAt(j).toPlainTextString());
									}
								}
								sb.append(" "+sbb.toString());									
							}
						}
						
//						logger.info(sb.toString());
						return sb.toString();
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
	/**
	 * 解析 URL:http://www.autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()
	 * 获得该页面下的trackid RaceNo 以及raceDate
	 * rp 里面装的是 yyyyMMdd+trackid+raceno
	 * */
	private void parseOdds(RacePool rp, String yyyyMMdd)
	{
		try
		{
			String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
			
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
				if(this.CanOpen("http://www.autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd, 3))
				{
					parser.setInputHTML(page.getBody());
					NodeFilter filter_tab = new TagNameFilter("table");
					NodeList nodelist = parser.parse(filter_tab);
					if(nodelist!=null && nodelist.size()>0)
					{							
						TableTag table = (TableTag) nodelist.elementAt(2);
						if(table.getRowCount()>0)
						{
							TableRow[] rows = table.getRows();
							TableRow rowRaceNo = rows[1];
							
							for(int j=0;j<rowRaceNo.getColumnCount();j++)
							{
								String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
								if(raceno!=null)
								{
									if(raceno.length()<2)
										raceno = "0"+raceno;										
									String raceid = yyyyMMdd+trackid+raceno;
//									logger.info(raceid);
									rp.AddID(raceid);
//									String raceUrl = "http://www.autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+(j+1);
//									logger.info(raceno+" ::: "+raceUrl);								
//									rp.AddID(yyyy_MM_dd+"__"+trackid+"__"+raceno+"__"+raceUrl);		
								}
							}
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
	/**
	 * 解析url :http://www.autorace.jp/netstadium/Live/"+trackNameHt.get(i+"")
	 * 根据比赛的时间 获取 pre以及post的链接
	 * rp 里面装的是 rp.AddID(yyyyMMdd+"__"+trackid+"__"+raceno+"__"+raceCardUrl) 和 rp.AddID(yyyyMMdd+"__"+trackid+"__"+raceno+"__"+raceResultUrl);	
	 * */
	public void parseToday(RacePool rp)
	{
		try
		{		
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
				if(this.CanOpen("http://www.autorace.jp/netstadium/Live/"+trackNameHt.get(i+"").toString(), 3))
				{
//					logger.info(page.getBody());
					Date curTime = new Date();
					String yyyyMMdd = DF_yyyyMMdd.format(curTime);
					String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
					
					String htmlRaceNo = CommonFun.GetStrFromPatter(page.getBody(), "(<td[^<]*?>(<[^<]+?>)?\\d{1,2}(</[^<]+?>)?(<[^<]+?>)?R(</[^<]+?>)?</td>\\s+){3,12}", 0);
            		String htmlTimeList = CommonFun.GetStrFromPatter(page.getBody(), "(<td>(<[^<]+?>| )\\d{2}:\\d{2}?( |</[^<]+?>)?</td>\\s+){3,12}", 0);

            		String htmlTable = "<table><tr>"+htmlRaceNo+"</tr><tr>"+htmlTimeList+"</tr></table>";
            		parser.setInputHTML(htmlTable);
        			NodeFilter filter_tab = new TagNameFilter("table");
        			NodeList nodelist = parser.parse(filter_tab);
        			if(nodelist!=null && nodelist.size()>0)
        			{			
        				TableTag timeLineTable = (TableTag) nodelist.elementAt(0);
        				TableRow rowRaceNo = timeLineTable.getRow(0);
        				TableRow rowRaceTime = timeLineTable.getRow(1);
        				for(int j=0;j<rowRaceNo.getColumnCount();j++)
        				{
							String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
							if(raceno!=null)
							{
								if(raceno.length()<2)
									raceno = "0"+raceno;		
								
								String racetime = rowRaceTime.getColumns()[j].toPlainTextString().trim();
								String racedate = null;
								Date raceStartTime = null;
								
								if(CommonFun.GetMatcherStrGroup(racetime, "\\d{1,2}:\\d{2}").find())
								{
									if(racetime.length()==4)
										racetime = "0"+racetime;
									racedate = DF_yyyy_MM_dd.format(curTime);	
									raceStartTime = CommonFun.DateSubHour(DF_yyyy_MM_dd_HH_mm.parse(racedate+" "+racetime),-1);
								}
								
								if(raceStartTime != null)
								{
//									if(curTime.after(CommonFun.DateSubMinute(raceStartTime,-40)) && curTime.before(CommonFun.DateSubMinute(raceStartTime,5))) // today racecard
									//获取比赛时间，根据比赛时间决定获取的 pre以及Post的页面
									if(curTime.before(CommonFun.DateSubMinute(raceStartTime,5))) // today racecard
									{
										String raceCardUrl = "http://www.autorace.jp/netstadium/Program/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+(j+1);
										logger.info(raceno+" ::: "+raceCardUrl);								
										rp.AddID(yyyyMMdd+"__"+trackid+"__"+raceno+"__"+raceCardUrl);		
									}
									else if(curTime.after(CommonFun.DateSubMinute(raceStartTime,5))) // today raceresult
									{
										String raceResultUrl = "http://www.autorace.jp/netstadium/RaceResult/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+(j+1);
										logger.info(raceno+" ::: "+raceResultUrl);								
										rp.AddID(yyyyMMdd+"__"+trackid+"__"+raceno+"__"+raceResultUrl);	
									}									
								}
							}
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
	/**
	 *  解析url :http://www.autorace.jp/netstadium/Live/"+trackNameHt.get(i+"")
	 *  获取比赛时间 根据获取的比赛时间 决定 去的是 odds 还是 finalOdds
	 *  rp里面装的是：rp.AddID(yyyyMMdd+"__"+trackid+"__"+raceno+"__"+raceOddsUrl)和 rp.AddID("FinalOdds_"+raceid);
	 * */
	public void parseTodayOdds(RacePool rp)
	{
		try
		{		
			
			for(int i=1;i<=6;i++)
			{
				String trackid = "00"+i;
				if(this.CanOpen("http://www.autorace.jp/netstadium/Live/"+trackNameHt.get(i+"").toString(), 3))
				{
					Date curTime = new Date();
					String yyyyMMdd = DF_yyyyMMdd.format(curTime);
					String yyyy_MM_dd = yyyyMMdd.substring(0, 4)+"-"+yyyyMMdd.substring(4, 6)+"-"+yyyyMMdd.substring(6);
					String htmlRaceNo = CommonFun.GetStrFromPatter(page.getBody(), "(<td[^<]*?>(<[^<]+?>)?\\d{1,2}(</[^<]+?>)?(<[^<]+?>)?R(</[^<]+?>)?</td>\\s+){3,12}", 0);
            		String htmlTimeList = CommonFun.GetStrFromPatter(page.getBody(), "(<td>(<[^<]+?>| )\\d{2}:\\d{2}?( |</[^<]+?>)?</td>\\s+){3,12}", 0);

            		String htmlTable = "<table><tr>"+htmlRaceNo+"</tr><tr>"+htmlTimeList+"</tr></table>";
            		parser.setInputHTML(htmlTable);
        			NodeFilter filter_tab = new TagNameFilter("table");
        			NodeList nodelist = parser.parse(filter_tab);
        			if(nodelist!=null && nodelist.size()>0)
        			{			
        				TableTag timeLineTable = (TableTag) nodelist.elementAt(0);
        				TableRow rowRaceNo = timeLineTable.getRow(0);
        				TableRow rowRaceTime = timeLineTable.getRow(1);
        				for(int j=0;j<rowRaceNo.getColumnCount();j++)
        				{
							String raceno = CommonFun.GetStrFromPatter(rowRaceNo.getColumns()[j].toPlainTextString(), "\\d{1,2}", 0);
							if(raceno!=null)
							{
								if(raceno.length()<2)
									raceno = "0"+raceno;		
								String racetime = rowRaceTime.getColumns()[j].toPlainTextString().trim();
								String racedate = null;
								Date raceStartTime = null;
								
								if(CommonFun.GetMatcherStrGroup(racetime, "\\d{1,2}:\\d{2}").find())
								{
									if(racetime.length()==4)
										racetime = "0"+racetime;
									racedate = DF_yyyy_MM_dd.format(curTime);	
									raceStartTime = CommonFun.DateSubHour(DF_yyyy_MM_dd_HH_mm.parse(racedate+" "+racetime),-1);
								}
								if(raceStartTime != null)
								{
									if(curTime.after(CommonFun.DateSubMinute(raceStartTime,-40)) && curTime.before(CommonFun.DateSubMinute(raceStartTime,5)))
									{
										String raceOddsUrl = "http://www.autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+(j+1);
										logger.info(raceno+" ::: "+raceOddsUrl);								
										rp.AddID(yyyyMMdd+"__"+trackid+"__"+raceno+"__"+raceOddsUrl);	
									}
									else if(curTime.after(CommonFun.DateSubMinute(raceStartTime,5)))
									{
										String raceFinalOddsUrl = "http://www.autorace.jp/netstadium/Odds/"+trackNameHt.get(i+"").toString()+"/"+yyyy_MM_dd+"_"+(j+1);
										logger.info(raceno+" ::: "+raceFinalOddsUrl);								
										String raceid = yyyyMMdd+trackid+raceno;
										rp.AddID("FinalOdds_"+raceid);
									}									
								}
							}
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
	
}