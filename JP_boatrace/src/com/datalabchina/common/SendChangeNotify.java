package com.datalabchina.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;


import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.datalabchina.Controller;

public class SendChangeNotify extends Thread  {
	private final String  NEWLINE=System.getProperty("line.separator");
	private static Logger logger = Logger.getLogger("SendChangeNotify");
	CommonMethod oCommonMethod = new CommonMethod();
	
	private String uraceID="";
	private String timeStamp="";
	private String poolType="";
	SimpleDateFormat oSDFTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public SendChangeNotify(){}
	
	public SendChangeNotify(String sUraceID,String sTimeStamp,String sPoolType){
		uraceID=sUraceID;
		timeStamp=sTimeStamp;
		poolType=sPoolType;
	}
	public SendChangeNotify(String sUraceID,Date dTimeStamp,String sPoolType){
		uraceID=sUraceID;
		timeStamp=oSDFTime.format(dTimeStamp);
		poolType=sPoolType;
	}
	
	
	public void run() {
		boolean bFlag=true;
		while(bFlag){
			ArrayList alEndpoint=Controller.endpointArrayList;
			logger.info("WebService URL List:"+alEndpoint.toString());
			for(int i=0;i<alEndpoint.size();i++){
				String endpoint=alEndpoint.get(i).toString();
				logger.debug("will send endpoint "+(i+1)+"/"+alEndpoint.size()+" WebService URL List:"+endpoint);
				if(!endpoint.equals(""))sendChgDivNotify(endpoint);
			}
			//if(!FrPmuControler.endpoint.equals(""))sendChgDivNotify(FrPmuControler.endpoint);
			//if(!FrPmuControler.endpointTest.equals(""))sendChgDivNotify(FrPmuControler.endpointTest);
			bFlag=false;
			alEndpoint=null;
		}
		
	}

	
private void sendChgDivNotify(String endpoint){
		//String endpoint=FrPmuControler.endpoint;
		if(endpoint.equals("")){
			logger.info("<URL>is null ,please set url");
			return;
		}else{
			if(endpoint.indexOf("axis:")>-1)endpoint=endpoint.substring(endpoint.indexOf("axis:")+5);
		}
		logger.info("will send changed Live Odds notify by "+uraceID+"|"+timeStamp+"|"+poolType);
		
//		String sCountryID=uraceID.substring(8,10);
//		String today=oCommonMethod.getFranceYYYYMMDD();
//		String sIDDay=uraceID.substring(0,8);
//		//test
//		if(!today.equals(sIDDay) ){
//			logger.info("it is not live odds ,will not send changed notify");
//			return;
//		}
		
//		if(FrPmuInfoControler.IsExtractFromLocal==1){
//			logger.info("spider read file from local ,will not send notify ");
//			return;
//		}
		
		if(!Controller.bIsSendOddsNotify){
			logger.info("<send> =1, will not send notify ");
			return;
		}
		/**
		 * 			String xml ="<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
		 *  xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"
		 *   xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
" <soap:Body>"
+"  <statusUpdate xmlns=\"@Link\">"
+"<xmlString>"
+"    &lt;StatusUpdate Sender=\"JPBoatOdds\"&gt;"
+"             &lt;CountryID&gt;12&lt;/CountryID&gt;"
+"                      &lt;OddsUpdate URaceID=\"@URaceID\"  Timestamp=\"@Timestamp\" MarketID=\"1\" /&gt;"
+"                            &lt;/StatusUpdate&gt;"
+"                                </xmlString>"
+"                                      </statusUpdate>"
+"                                      </soap:Body>"
+"                                      </soap:Envelope>";
		 * */
		
		String notifyContent="<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
		notifyContent +="<soap:Body>"+NEWLINE;//function name
		notifyContent +="	<statusUpdate xmlns=\""+endpoint+"\">"+NEWLINE;
		notifyContent +="		<xmlString>"+NEWLINE;
		
		notifyContent += "&lt;StatusUpdate Sender=\"JPBoatRaceOdds\"&gt;"+NEWLINE;
		notifyContent += "&lt;CountryID&gt;12&lt;/CountryID&gt;"+NEWLINE;
		notifyContent += "&lt;OddsUpdate URaceID=\""+uraceID+"\" ";
		notifyContent += " Timestamp=\""+timeStamp+"\"" ;
		notifyContent += " MarketID=\"1\"" ;
		  //notifyContent += " Sender=\"RaceTab\"" ;
		notifyContent += " /&gt;"+NEWLINE;
		notifyContent += " &lt;/StatusUpdate&gt;"+NEWLINE;
//		end message body
		notifyContent +="		</xmlString>"+NEWLINE;
		notifyContent +="	</statusUpdate>"+NEWLINE;
		notifyContent +="</soap:Body>"+NEWLINE;
		
		notifyContent +="</soap:Envelope>";
		
		HttpClient httpclient = new HttpClient();
		
		PostMethod post = new PostMethod(endpoint);

		try {
//			System.out.println(notifyContent);
			
	        StringRequestEntity entity = new StringRequestEntity(notifyContent, "text/xml","utf-8");
	        post.setRequestEntity(entity);
	        
            int result = httpclient.executeMethod(post);
            System.out.println("Response status code: " + result);
            String returnStr=post.getResponseBodyAsString();
            String values=oCommonMethod.getValueByPatter(returnStr,"<statusUpdateReturn.*?>(.*?)</statusUpdateReturn>");
            logger.info(uraceID+"|"+timeStamp+"|"+poolType+" return:"+values+" by :"+endpoint);
    		
        } catch (HttpException e) {
    		logger.error("Please check your provided http address!");
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
    	httpclient=null;
    	post=null;
		  
			  
			 
		
	}

private void setNetWebServiceNotify(String uraceID,int type_1normalIP_2testIP) {
	//String url="http://192.168.60.199/hrm.web/wservice/servicedata_livebet.asmx/DoPostOfficialResult";
	String url="";
	//if(type_1normalIP_2testIP==1)url=FrPmuControler.sendDivsNetWebserviceURL;
	//else if(type_1normalIP_2testIP==2)url=FrPmuControler.sendDivsNetWebserviceURLtest;
	logger.info("spider will send dividend nodify to:"+url);
	if(url.trim().equals("")){
		logger.warn("sendDivsNetWebserviceURL is null ,will not send dividend notify");
		return;
	}
	HttpClient httpClient = new HttpClient();
	
	PostMethod postMethod = new PostMethod(url);
	postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,	new DefaultHttpMethodRetryHandler());
	NameValuePair[] data = {
			new NameValuePair("uraceID", uraceID),
			};
	postMethod.setRequestBody(data);
	try {
		int statusCode = httpClient.executeMethod(postMethod);
		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
				|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
			Header locationHeader = postMethod.getResponseHeader("location");
			String location = null;
			if (locationHeader != null) {
				location = locationHeader.getValue();
				logger.info("The page was redirected to:" + location);
			} else {
				logger.info("Location field value is null.");
			}
			return;
		}
		if (statusCode != HttpStatus.SC_OK) {
			logger.error("Method failed: "+ postMethod.getStatusLine());
		}
		byte[] responseBody = postMethod.getResponseBody();
		String sBody1=new String(responseBody);
		logger.info("sendDivsNetWebserviceURL return:\n"+sBody1+"\n by +"+url);
	} catch (HttpException e) {
		System.out.println("Please check your provided http address!");
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		postMethod.releaseConnection();
		httpClient=null;
		
	}
	
}


public  void setSendNotifyParameter(){
		boolean bIsExist=oCommonMethod.bIfExistFile("sendNotify.xml");
		if(!bIsExist){
			logger.warn("sendNotify.xml file is not exist ,will not send notify");
			Controller.bIsSendOddsNotify=false;
			return;
		}
		
		String fileContent=oCommonMethod.readFile("sendNotify.xml");
		String oddsSendStr=oCommonMethod.getValueByPatter(fileContent,"<Send>(\\d{1})</Send>");
		if(!oddsSendStr.equals("1")){
			logger.warn("Please set <send> as 1");
			Controller.bIsSendOddsNotify=false;
			
			return;
		}else {
			Controller.bIsSendOddsNotify=true;
			
			
		}
//		String oddsUrl=CommonMethod.getValueByPatter(fileContent,"<URL>(.*?)</URL>");
//		String oddsTestUrl=CommonMethod.getValueByPatter(fileContent,"<URLtest>(.*?)</URLtest>");
//		String divNetUrl=CommonMethod.getValueByPatter(fileContent,"<NETDIVURL>(.*?)</NETDIVURL>");
//		String divNetUrltest=CommonMethod.getValueByPatter(fileContent,"<NETDIVURLtest>(.*?)</NETDIVURLtest>");
//		if(oddsUrl.equals("")){
//			logger.warn("Please set url value");
//			FrPmuControler.bIsSendOddsNotify=false;
//			
//			return;
//		}else{
//			FrPmuControler.endpoint=oddsUrl;
//			FrPmuControler.endpointTest=oddsTestUrl;
//			
//		}
		Matcher oMatcher = oCommonMethod.getMatcherStrGroup(fileContent,"<URL>(.*?)</URL>");
		while(oMatcher.find()){
			String tmpUrl=oMatcher.group(1);
			if(!Controller.endpointArrayList.contains(tmpUrl))
				Controller.endpointArrayList.add(tmpUrl);
		}
		
		oMatcher=null;

		


		
	
	
}

	
	
	 
	public static void main(String arg[]){
		PropertyConfigurator.configure("log4j.properties");
//		ControllerKraPro.sendOddsNotifyURL="axis:http://192.168.60.251:44444/services/msgDispatcher?method=statusUpdate";
//		ControllerKraPro.bIsSendOddsNotify=true;
//		(new SendChangeNotify("20061227090901","2006-12-31 12:31:12","1")).start();
//		
		//ControllerKraPro.sendDivsNotifyURL="http://192.168.60.6/hrm.web/wservice/servicedata_livebet.asmx?method=DoPostOfficialResult";
		//String sendDivsNotifyURL="http://192.168.60.251:44444/services/msgDispatcher";
		new SendChangeNotify().setSendNotifyParameter();
//		(new SendChangeNotify("20070726090901","2006-12-31 12:31:12","WIN")).start();
		(new SendChangeNotify("2017080800401","2017-08-08 12:07:00","Ti")).run();
		
	}
	
}

