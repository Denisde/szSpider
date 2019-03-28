package com.datalabchina.bll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.datalabchina.common.CommonDB;
import com.datalabchina.common.CommonMethod;
import com.datalabchina.common.PageHelper;

public class HorseBll implements Runnable{
	private static Logger logger = Logger.getLogger(HorseBll.class.getName());
	static PageHelper pageHelper = PageHelper.getPageHelper();
	static CommonDB oCommonDB =new CommonDB();
	Map<String,List<String>> map = new HashMap<String,List<String>>();
	 static CommonMethod oCommonMethod = new CommonMethod();
	static Hashtable<String,String> dateHt = new Hashtable<String,String>();
	@Override
	public void run() 
	{
		login();
		String webHorseId = "356408";
		parsePage(webHorseId);
	}
	
	
	public void parsePage(String webHorseId) {
		if(isExtistHorse(webHorseId)&&isBithdayNull(webHorseId)){
			logger.info(webHorseId+ "*************** is Exist !!!!!!!!  *************************");
			return ;
		}
//		login();
		String url ="http://www.elturf.com/elturfcom/venta-ejemplares-home?id_ejemplar="+webHorseId;
		String body = pageHelper.doGet(url);
		if(body!=null&&body.length()>1)
			parsePage(body,webHorseId);
	}

	 private static  boolean isExtistHorse(String playerId)
	 {
		 boolean flag = true;
		 List<Integer> list = oCommonDB.getHorsIdBySth("select  top 100*  from Elturf_Horse where webHorseId = "+playerId);
		 if(list.size()>0)
			 return flag;
		 else 
			 flag = false;
		 return flag;
	 }
	 
	 private static  boolean isBithdayNull(String playerId)
	 {
		 boolean flag = true;
		 List<Integer> list = oCommonDB.getHorsIdBySth("select  top 100*  from Elturf_Horse where webHorseId = "+playerId+"and birthdate is not null");
		 if(list.size()>0)
			 return flag;
		 else 
			 flag = false;
		 return flag;
	 }
	 
	private void parsePage(String body,String webHorseId) {
		try {
			//<div class="row">\\s*<div class="col-sm-12 text-left">
			//<!--basicos-->
//			<h1><strong>Newfound Hope (2010) CHI</strong></h1>
//			Newfoundland (Storm Cat)  y  Barrique  por  Hussonet<br>
//			Macho  Alazán 6 años -   Septiembre 7, 2010<br><strong>Criador:</strong> Haras Dadinco<p></p></div></div>
//			String str = oCommonMethod.getValueByPatter(body, "<div class=\"row\">\\s*<div class=\"col-sm-12 text-left\">(.*?)</div>\\s*</div>");
			String str = oCommonMethod.getValueByPatter(body, "<div class=\"row\">\\s*<div class=\"col-sm-12 text-left\">(.*?)<div class=\"row\">");
//			<h1><strong>El Caporal (2012) CHI</strong></h1>
//			String horseName = oCommonMethod.getValueByPatter(str, "<h1>\\s*<strong>(.*?)\\(.*?</strong>\\s*</h1>");
			String horseName = oCommonMethod.getValueByPatter(str, "<h1 class=\"ej-class2\">\\s*<strong>(.*?)\\(.*?</strong>\\s*</h1>");
			//<h1><strong>Oshawa San <small>
			if(horseName.length()<1)
				horseName = oCommonMethod.getValueByPatter(str, "<h1><strong>(.*?)<small>");
			
			//Merchant Of Venice (Storm Cat)  y  Bavarois  por  Stuka II<br>
			//Un Sueño  por  Tanaasa
//			<h1 class="ej-class2"><strong>El Avatar (2009) CHI</strong></h1>
//			Milt´s Overture (Dynaformer)  y  Dalaika  por  Hussonet<br>
			//</h1>Lookin At Lucky (Smart Strike)  y  Oshawa  por  Scat Daddy<br>\
			String str0 = oCommonMethod.getValueByPatter(body, "<div class=\"row\">\\s*<div class=\"col-sm-12 text-left\">(.*?)<div class=\"row\">");
			
			Matcher m =  oCommonMethod.getMatcherStrGroup(str0, "</h1>(.*?)\\(.*?\\)\\s*y(.*?)\\s*por\\s*(.*?)<br>");
			String sire= null,dam=null,sireOfDam=null;
			while(m.find())
			{
				sire = m.group(1).trim();
				dam = m.group(2).trim();
				sireOfDam = m.group(3).trim();
			}
			//Macho  Alazán 6 años -   Septiembre 7, 2010<br><strong>Criador:</strong> Haras Dadinco<p></p></div></div>
			//Hembra  Castaña 4 años -   - , 2015<br><strong>Criador:</strong> Stud Red Rafa<p></p>
		
			//<h1><strong>Oshawa San <small>(2016) CHI</small></strong></h1>
//			Lookin At Lucky (Smart Strike)  y  Oshawa  por  Scat Daddy<br>
//			Macho  Colorado 2 años -   Julio 15, 2016			<br><strong>Criador:</strong> Haras Paso Nevado						</div>
			
			String birthday=null,sex=null,breeder=null;
			if(str.indexOf("<strong>Criador")>-1) {
				String sexInfo = oCommonMethod.getValueByPatter(str, "<br>(.*?)<br><strong>Criador");
				 sex= sexInfo.split("<br>")[0].split("-")[0].split("  ")[0];
				String rawBirthday = sexInfo.split("<br>")[0].split("-")[1];
				 birthday = getBirthday(rawBirthday);
				if(birthday==null) {
					String birthyear= oCommonMethod.getValueByPatter(str, "(\\d{4})<p></p>");
					if(birthyear.length()<1) 
						birthyear= oCommonMethod.getValueByPatter(str, "(\\d{4})<br><strong>Criador");
					if(birthyear.length()>1)
						birthday = birthyear+"0101";
				}
				 breeder = oCommonMethod.getValueByPatter(str, "Criador:</strong>(.*?)</div>"); 
			}else {
				String sexInfo = oCommonMethod.getValueByPatter(str, "<br>(.*?)</div>\\s*</div>");
				 sex= sexInfo.split("<br>")[0].split("-")[0].split("  ")[0];
					String rawBirthday = sexInfo.split("<br>")[0].split("-")[1];
					 birthday = getBirthday(rawBirthday);
					 if(birthday==null) {
							String birthyear= oCommonMethod.getValueByPatter(str, "(\\d{4})<p></p>");
							if(birthyear.length()<1) 
								birthyear= oCommonMethod.getValueByPatter(str, "(\\d{4})</div>\\s*</div>");
							if(birthyear.length()>1)
								birthday = birthyear+"0101";
						}	 
			}
			saveHorseToDB(webHorseId,horseName,birthday,sire,dam,sireOfDam,sex,breeder);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	private void saveHorseToDB(String webHorseId, String horseName,
		String birthday, String sire, String dam, String sireOfDam, String sex,
		String breeder) {
		try {
			String sSql ="";
			sSql+=covertString(webHorseId)==null?"NULL,":"N'"+covertString(webHorseId)+"',";
			sSql+=covertString(horseName)==null?"NULL,":"N'"+covertString(horseName.replaceAll("<.*?>", ""))+"',";
			sSql+=covertString(birthday)==null?"NULL,":"N'"+covertString(birthday)+"',";
			sSql+="NULL,";
			sSql+=covertString(sire)==null?"NULL,":"N'"+covertString(sire)+"',";
			sSql+=covertString(dam)==null?"NULL,":"N'"+covertString(dam)+"',";
			sSql+="NULL,";
			sSql+="NULL,";
			sSql+="NULL,";
			sSql+="NULL,";
			sSql+="NULL,";
			sSql+="NULL,";
			sSql+="NULL,";
			sSql+=covertString(sireOfDam)==null?"NULL,":"N'"+covertString(sireOfDam)+"',";
			sSql+="N'"+oCommonMethod.getCurrentTime()+"',";
			sSql+=covertString(sex)==null?"NULL,":"N'"+covertString(sex)+"',";
			sSql+=covertString(breeder)==null?"NULL,":"N'"+covertString(breeder)+"',";
			sSql+="NULL";
			
			logger.info("pr_Elturf_Horse_InsertData sql :" + sSql);
			oCommonDB.execStoredProcedures("pr_Elturf_Horse_InsertData ", sSql);
		} catch (Exception e) {
			logger.error("",e);
		}
}

	@Test
	public  String  getBirthday(String rawBirthday) {
		//Octubre 30, 1998
//		String rawBirthday = "Septiembre 7, 2010";
		//Septiembre 7, 2010
		// Marzo , 2016
		String str = null;
		String year = oCommonMethod.getValueByPatter(rawBirthday, "(\\d{4})");
		String day = oCommonMethod.getValueByPatter(rawBirthday, "(\\d{1,2})\\s*,");
		String month = dateHt.get(rawBirthday.replace(year, "").replace(day, "").replace(",", "").replaceAll("<.*?>", "").replaceAll("\\s", ""));
		if(month==null)return  null;
		while(day.length()<2)day = "0"+day;
		if(day.equals("00"))day = "01";
		
		str  = year.trim()+month.trim()+day.trim();
		if(str.length()!=8)
			return null;
		return str;
	}

	public HorseBll()
	{
//		login();
		dateHt.put("Enero", "01");
		dateHt.put("Febrero", "02");
		dateHt.put("Marzo", "03");
		dateHt.put("Abril", "04");
		dateHt.put("Mayo", "05");
		dateHt.put("Junio", "06");
		dateHt.put("Julio", "07");
		dateHt.put("Agosto", "08");
		dateHt.put("Septiembre", "09");
		dateHt.put("Octubre", "10");
		dateHt.put("Noviembre", "11");
		dateHt.put("Diciembre", "12");
	}
	
	   private static String  covertString(String str){
	    	if(str==null||str.trim().length()<1)
	    		return null;
	    	else
	    		return str.trim().replaceAll("'", "''");
	    }

	private void login() {
//		String loginurl="http://www.elturf.com/elturfcom/dashboard-login-in";
//		String refer = "http://www.elturf.com/elturfcom/dashboard-login?msg_dash=3";
		String loginurl="http://elturf.com/elturfcom/dashboard-login-in";
		String refer = "http://elturf.com/elturfcom/dashboard-login?msg_dash=2";
		try {
			List<NameValuePair> nvp =  new ArrayList<NameValuePair>();
			String  userName ="939755738@qq.com";
			String  passWord ="123456";
			nvp.add(new BasicNameValuePair("form_contacto_usuario", userName));
			nvp.add(new BasicNameValuePair("form_contacto_passwd",passWord));
			pageHelper.doPost(loginurl, refer, nvp);
			String homeUrl = "http://www.elturf.com/elturfcom/home";
			String body = pageHelper.doGet(homeUrl);
			String str = oCommonMethod.getValueByPatter(body, "<strong>(939755738@qq.com)</strong>");
			if(str.length()>1)
				logger.info("============================login Success !!!!====================");
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	public void fixRaceFromSql(){
		List<String> idlist = oCommonDB.getWebHorseId();
		for(int i=0;i<idlist.size();i++){
			String id = idlist.get(i);
			parsePage(id);
		}
	}
	
	public static void main(String[] args){
		HorseBll p = new HorseBll();
		p.login();
		p.run();
//		p.fixRaceFromSql();
	}
	
}
