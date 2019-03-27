package com.common.bak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.FileDispose;
import com.common.Utils;


public class PageHelper 
{
	
	static Logger logger = Logger.getLogger(PageHelper.class.getName());
	
	static protected CloseableHttpClient httpClient;

	static int httpRetry = 10;
	
	static int iChangeProxy = 20;
	
	private static HttpClientContext HTTP_CONTEXT = HttpClientContext.create();
	
	static String hostIp = "unkown";
	
	static String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

//	78.129.196.103 8080 pmu se4rfdgy7ujh
	private static HttpHost proxy = new HttpHost("78.129.196.103", 8080);
	private static CredentialsProvider credsProvider = new BasicCredentialsProvider();
	
	final static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();
	private static RequestConfig config = null;
	private static BasicCookieStore cookieStore = new BasicCookieStore();
	
	private static javax.net.ssl.SSLContext sslContext = null;
				
	private static SSLConnectionSocketFactory sslsf = null;
	
	private static List<String> proxyList = new ArrayList<String>();
	
	static {
		try {
			System.err.println("proxyList.txt");
			File file = new File("proxyList.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String line = in.readLine();
			while (line != null) {
				line = line.trim();
				if (line.equals("")) {
					line = in.readLine();
					continue;
				}
				System.err.println(line);
//				logger.info(line);
				proxyList.add(line);
				line = in.readLine();
			}
			in.close();		
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	private static Object lock = new Object();
	
	private static String getProxy() {
		String p = null;
		synchronized (lock) {
			if (proxyList.size() == 0) {
				return null;
			}
			
			
			p = proxyList.get(0);
//			logger.info("use proxy " + p);
			proxyList.remove(0);
			proxyList.add(p);			
		}
		return p;
	}	
	
	  public static void setContext() {
//		    System.out.println("----setContext");
		    
//		    HTTP_CONTEXT = HttpClientContext.create();
//		    Registry<CookieSpecProvider> registry = RegistryBuilder
//		        .<CookieSpecProvider> create()
//		        .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
//		        .register(CookieSpecs.BROWSER_COMPATIBILITY,
//		            new BrowserCompatSpecFactory()).build();
//		    HTTP_CONTEXT.setCookieSpecRegistry(registry);
//		    HTTP_CONTEXT.setCookieStore(cookieStore);
		    		    
		  }
	
	@SuppressWarnings("deprecation")
	public static PageHelper getPageHelper() {
		PageHelper pageHelper = (PageHelper) threadLocal.get();
		
		try
		{
			if (pageHelper == null) 
			{
//				String strProxy = null;
//				String strPort = null;
//				String strUser = null;
//				String strPasswd = null;				
				
//				String p = getProxy();
//				if (p != null) {
//					String a[] = p.split(":");
//					strProxy = a[0];
//					strPort = a[1];
//					strUser = a[2];
//					strPasswd = a[3];					
//				}
				
				sslContext = (new SSLContextBuilder()).loadTrustMaterial(null, new TrustStrategy() {
					public boolean isTrusted(X509Certificate chain[], String authType) throws CertificateException {
						return true;
					}
				}).build();

//				sslsf = new SSLConnectionSocketFactory(sslContext,new String[] {"SSLv3"}, null, null);
				sslsf = new SSLConnectionSocketFactory(sslContext,new String[] {"SSLv3"}, null, new X509HostnameVerifier() {
//				sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}

					public void verify(String s, SSLSocket sslsocket) throws IOException {
					}

					public void verify(String s, X509Certificate x509certificate) throws SSLException {
					}

					public void verify(String s, String as[], String as1[]) throws SSLException {
					}

				});				
								
				pageHelper = new PageHelper();
				
//				PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//				cm.setMaxTotal(1000);
//				
//				if (strProxy != null && strProxy.length() > 1) {
//					logger.info("use proxy : " + strProxy);
//					proxy = new HttpHost(strProxy, Integer.parseInt(strPort));
//					if (strUser != null && strUser.length() > 1) {
//						credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(strUser,strPasswd));																		
//					}
//			        httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).setDefaultCredentialsProvider(credsProvider).setDefaultCookieStore(cookieStore).build();
//			    	config = RequestConfig.custom().setSocketTimeout(90000).setConnectTimeout(90000).setProxy(proxy).build();		        																		
//				} else {
//					httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).setDefaultCookieStore(cookieStore).build();
//					config = RequestConfig.custom().setSocketTimeout(90000).setConnectTimeout(90000).build();						
//				}
					
				HTTP_CONTEXT.setAttribute("User-Agent", UserAgent);
				
				setContext();
				
				threadLocal.set(pageHelper);
			}
			return pageHelper;

		} catch (Exception e) {
			logger.error("getPageHelper", e);
			return null;
		}		
	}
	
	
	public static void changeProxy() {
		try {
			
			String strProxy = null;
			String strPort = null;
			String strUser = null;
			String strPasswd = null;
			
			if (iChangeProxy < 20) {
				iChangeProxy++;
				return;
			}
			
			iChangeProxy = 0;
			
			String p = getProxy();
			if (p != null) {
				String a[] = p.split(":");
				strProxy = a[0];
				strPort = a[1];
				strUser = a[2];
				strPasswd = a[3];					
			}
			
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(1000);
			
			if (strProxy != null && strProxy.length() > 1) {
				logger.info("use proxy : " + strProxy);
				proxy = new HttpHost(strProxy, Integer.parseInt(strPort));
				if (strUser != null && strUser.length() > 1) {
					credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(strUser,strPasswd));																		
				}
		        httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).setDefaultCredentialsProvider(credsProvider).setDefaultCookieStore(cookieStore).build();
		    	config = RequestConfig.custom().setSocketTimeout(90000).setConnectTimeout(90000).setProxy(proxy).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();		        																		
			} else {
				httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).setDefaultCookieStore(cookieStore).build();
				config = RequestConfig.custom().setSocketTimeout(90000).setConnectTimeout(90000).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();						
			}
					
		} catch (Exception e) {
			logger.error("getPageHelper", e);
		}		
	}	
	
	public byte[] doGetByte(String url, String referer) {
		return doGetByte(url, referer, 0);
	}
	
	public byte[] doGetByte(String url, String referer, int retry) {
		changeProxy();
		logger.info("doGetByte " + retry + " : " + url);
		HttpGet request = new HttpGet(url);
		try 
		{
			request.setConfig(config);
			if (referer != null)
			{
				request.setHeader("Referer", referer);
			}
			request.setHeader("User-Agent", UserAgent);
			
			CloseableHttpResponse response = httpClient.execute(request, HTTP_CONTEXT);
			
			try 
			{
				if (response.getStatusLine().getStatusCode() < 200
						|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "
							+ response.getStatusLine().getStatusCode());

					retry = retry + 1;
					if (retry >= httpRetry)
					{
						request.abort();
						return null;				
					}
					else
					{
						request.abort();
						return doGetByte(url, referer, retry);
					}					
				}
	
				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					byte[] b = EntityUtils.toByteArray(entity);
					EntityUtils.consume(entity);
					return b;
				}
				else 
				{
					logger.error("entity == null");
					return null;
				}
			} 
			finally 
			{
				response.close();
			}
		} 
		catch (Exception e) 
		{
			logger.error("doGetByte", e);
			retry = retry + 1;
			if (retry >= httpRetry) 
			{
				request.abort();
				return null;
			} 
			else
			{
				request.abort();
				return doGetByte(url, referer, retry);
			}
		}

	}

	public String doGet(String url)
	{
		return doGet(url, null, 0);
	}	
	
	public String doGet(String url, String referer)
	{
		return doGet(url, referer, 0);
	}
	
	//
	public String doOptions(String url, String referer,int retry)
	{
		changeProxy();
		logger.info("doGet " + retry + " : " + url);
		HttpOptions httpOptions = new HttpOptions(url);
		try
		{
			httpOptions.setConfig(config);
			if (referer != null) 
			{
				httpOptions.setHeader("Referer", referer);
			}
			httpOptions.setHeader("User-Agent", UserAgent);
			
//			httpOptions.setHeader("Access-Control-Request-Headers", "accept, x-tokenid, content-type");
//			httpOptions.setHeader("Access-Control-Request-Method", "POST");
			
			httpOptions.setHeader("Host", "edeveloper.mppglobal.com");
			httpOptions.setHeader("Origin", "https://oreg.racingpost.com");			
			httpOptions.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
			httpOptions.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpOptions.setHeader("Access-Control-Request-Headers", "content-type, x-tokenid");
			httpOptions.setHeader("Access-Control-Request-Method", "POST");					
			
			CloseableHttpResponse response = httpClient.execute(httpOptions, HTTP_CONTEXT);
			try 
			{
				if (response.getStatusLine().getStatusCode() < 200	|| response.getStatusLine().getStatusCode() >= 400)
				{
					if (response.getStatusLine().getStatusCode() == 404) 
					{
						return "404";	
					}					
					
					logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
					retry = retry + 1;
					if (retry >= httpRetry)
					{
						httpOptions.abort();
						return "";				
					}
					else
					{
						httpOptions.abort();
						return doOptions(url, referer, retry);
					}
				}
				
				if (response.getStatusLine().getStatusCode() == 302) {
					logger.warn("StatusCode = 302, Location = " + response.getFirstHeader("Location").getValue());
					return doGet(response.getFirstHeader("Location").getValue(), "");					
				}						
	
				HttpEntity entity = response.getEntity();
				if (entity != null) 
				{
					String body = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					body = body.replaceAll("&#039;", "'").replaceAll("&quot;", "\"");
					return body;
				} 
				else 
				{
					logger.error("entity == null");
					return "";
				}
			} 
			finally
			{
				response.close();
			}
		} 
		catch (Exception e)
		{
			logger.error("doGet", e);
			retry = retry + 1;
			if (retry >= httpRetry)
			{
				httpOptions.abort();
				return "";				
			} else 
			{
				httpOptions.abort();
				return doOptions(url, referer, retry);
			}				
		}

	}	
	
	public String doGet(String url, String referer,int retry) 
	{
		changeProxy();
		logger.info("doGet " + retry + " : " + url);
		HttpGet request = new HttpGet(url);
		
		try {
//			Thread.sleep(500);
			request.setConfig(config);
			if (referer != null)
			{
				request.setHeader("Referer", referer);
				request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				request.setHeader("Accept-Encoding", "gzip,deflate");
				
				request.setHeader("Host", "www.racingpost.com");
				request.setHeader("Upgrade-Insecure-Requests", "1");
			}
			request.setHeader("User-Agent", UserAgent);

			CloseableHttpResponse response = httpClient.execute(request, HTTP_CONTEXT);
			
			try {
				if (response.getStatusLine().getStatusCode() < 200
						|| response.getStatusLine().getStatusCode() >= 400) {
					if (response.getStatusLine().getStatusCode() == 404) {
						return "404";	
					}
					
					if (response.getStatusLine().getStatusCode() != 503) {
						
						logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
						retry = retry + 1;
						if (retry >= httpRetry)
						{
							request.abort();
							return "";				
						}
						else
						{
							request.abort();
							return doGet(url, referer, retry);
						}
						
					}
				}
				
				if (response.getStatusLine().getStatusCode() == 302)
				{
					logger.warn("StatusCode = 302, Location = " + response.getFirstHeader("Location").getValue());
					return doGet(response.getFirstHeader("Location").getValue(), "");					
				}		
				
//                System.out.println("Initial set of cookies:");
//                List<Cookie> cookies = cookieStore.getCookies();
//                String cookie = "";
//                if (cookies.isEmpty()) {
//                    System.out.println("None");
//                } else {
//                    for (int i = 0; i < cookies.size(); i++) {
//                        System.out.println("- " + cookies.get(i).toString());
//                        
//                        cookie += " - " + cookies.get(i).toString();
//                    }
//                }
                
				HttpEntity entity = response.getEntity();
				if (entity != null) 
				{
					String body = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					body = body.replaceAll("&#039;", "'").replaceAll("&quot;", "\"");
					
					body = body + "\n\n<!-- url=" + url + " -->";
					
//	                body += "\n\n<!-- cookie=" + cookie + " -->";
					return body;
				} 
				else 
				{
					logger.error("entity == null");
					return "";
				}
			} 
			finally
			{
				response.close();
			}
		} 
		catch (Exception e)
		{
			logger.error("doGet", e);
			retry = retry + 1;
			if (retry >= httpRetry)
			{
				request.abort();
				return "";				
			}
			else
			{
				request.abort();
				return doGet(url, referer, retry);
			}				
		}
	}

	public String doPostByAjax(String url, String referer,	List<NameValuePair> nameValuePairs)
	{
		return doPost(url, referer,nameValuePairs, 0, true, null);
	}
	
	public String doPost(String url, String referer, List<NameValuePair> nameValuePairs)
	{
		return doPost(url, referer,nameValuePairs, 0, false, null);
	}
	
	public String doPost(String url, String postXML) {		
		return doPost(url, null, null, 0, false, postXML);				
	}	
	
	public String doPost(String url, String referer, List<NameValuePair> nameValuePairs,int retry, boolean Ajax, String content) 
	{		
		changeProxy();
		logger.info("doPost " + retry + " : " + url); // + "\nnameValuePairs = " + nameValuePairs.toString());
		HttpPost httppost = new HttpPost(url);

		try {
			
			httppost.setConfig(config);
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			if (referer != null)
			{
				httppost.setHeader("Referer", referer);
			}
			httppost.setHeader("User-Agent", UserAgent);
			
			if (Ajax) httppost.setHeader("X-Requested-With", "XMLHttpRequest");

			if (content != null) {
				httppost.setEntity(EntityBuilder.create().setText(content).build());
			} else {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); //,"UTF-8");				
			}			
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

			CloseableHttpResponse response = httpClient.execute(httppost, HTTP_CONTEXT);
			try {
				
				if (response.getStatusLine().getStatusCode() < 200
						|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
					retry = retry + 1;
					if (retry >= httpRetry)
					{
						httppost.abort();
						return "";				
					}
					else
					{
						httppost.abort();
						return doPost(url, referer,nameValuePairs, retry, false, content);
					}
				}
				
				if (response.getStatusLine().getStatusCode() == 302) {
					logger.warn("StatusCode = 302, Location = " + response.getFirstHeader("Location").getValue());
					return doGet(response.getFirstHeader("Location").getValue(), "");
				}
	
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String body = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					body = body.replaceAll("&#039;", "'").replaceAll("&quot;", "\"");
					return body;
				} else {
					logger.error("entity == null");
					return "";
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error("doPost", e);
			retry = retry + 1;
			if (retry >= httpRetry) {
				httppost.abort();
				return "";				
			} else {
				httppost.abort();
				return doPost(url, referer,nameValuePairs, retry, false, content);
			}
		}

	}
	
	
	public String doPost(String url,String referer,List<NameValuePair> nameValuePairs, String content, String Tokenid, String ContentType, boolean Ajax ){
		changeProxy();
		HttpPost httppost = new HttpPost(url);

		try {
			httppost.setConfig(config);
			if (referer != null) {
				httppost.setHeader("Referer", referer);
			}
			httppost.setHeader("User-Agent", UserAgent);
			
//			if (Ajax) httppost.setHeader("x-requested-with", "XMLHttpRequest");
			if (Ajax) httppost.setHeader("X-Requested-With", "XMLHttpRequest");
			if (nameValuePairs == null) {
//				StringEntity myEntity = new StringEntity(content, "UTF-8");
				StringEntity myEntity = new StringEntity(content);
				httppost.setEntity(myEntity);
			} else {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); // ,"UTF-8");
				// httppost.setEntity(new
				// UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			}
			
//			tokenId: "3A454565167B462680CCC0FF21697630",
			if (Tokenid != null) {
				httppost.setHeader("X-Tokenid", Tokenid);				
			}
//			httppost.setHeader("Host", "edeveloper.mppglobal.com");
			httppost.setHeader("Origin", "https://reg.racingpost.com");
			if (ContentType == null) {
				httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");				
			} else {
				//httppost.setHeader("Content-Type", "application/json; chartset=utf-8");				
				httppost.setHeader("Content-Type", ContentType);
			}
			//httppost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			
			//httppost.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
			httppost.setHeader("Accept", "*/*");
			CloseableHttpResponse response = httpClient.execute(httppost, HTTP_CONTEXT);
			try {
				
//                System.out.println("Initial set of cookies:");
//                List<Cookie> cookies = cookieStore.getCookies();
//                if (cookies.isEmpty()) {
//                    System.out.println("None");
//                } else {
//                    for (int i = 0; i < cookies.size(); i++) {
//                        System.out.println("- " + cookies.get(i).toString());
//                    }
//                }				

                
				if (response.getStatusLine().getStatusCode() < 200
						|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
					httppost.abort();
					return doPost(url, referer,nameValuePairs, 0, false, content);
				}
				
				if (response.getStatusLine().getStatusCode() == 302) {
					logger.warn("StatusCode = 302, Location = " + response.getFirstHeader("Location").getValue());
					return doGet(response.getFirstHeader("Location").getValue(), "");
				}
	
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String body = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					body = body.replaceAll("&#039;", "'").replaceAll("&quot;", "\"");
					return body;
				} else {
					logger.error("entity == null");
					return "";
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error("doPost", e);
		}
		return "";
	}
	
	
	public String doPost(HttpPost httppost){
		changeProxy();
		try {
			httppost.setHeader("User-Agent", UserAgent);
			httppost.setConfig(config);
			CloseableHttpResponse response = httpClient.execute(httppost, HTTP_CONTEXT);
			try
			{			
				if (response.getStatusLine().getStatusCode() < 200
						|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
					httppost.abort();
					return doPost(httppost);
				}
				
				if (response.getStatusLine().getStatusCode() == 302) {
					logger.warn("StatusCode = 302, Location = " + response.getFirstHeader("Location").getValue());
					return doGet(response.getFirstHeader("Location").getValue(), "");
				}
	
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String body = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					body = body.replaceAll("&#039;", "'").replaceAll("&quot;", "\"");
					return body;
				} else {
					logger.error("entity == null");
					return "";
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error("doPost", e);
		}
		return "";
	}
	
	protected static void testDoGet() {
        String url = "http://www.baidu.com/";
        String body = PageHelper.getPageHelper().doGet(url, null);
        System.err.println(body);
//		FileDispose.saveFile(body, "d:\\doPost.html");		
	}
	
	protected static void testDoPost() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//        nameValuePairs.add(new BasicNameValuePair("username", ""));
        nameValuePairs.add(new BasicNameValuePair("password", ""));
        
        String url = "";
        String body = PageHelper.getPageHelper().doPost(url, null, nameValuePairs);
        System.err.println(body);
//		FileDispose.saveFile(body, "d:\\doPost.html");
	}


	protected static void UKRace() {
		
		PageHelper page = PageHelper.getPageHelper();
		
		String  host = "http://www.racingpost.com/";
//		String body = page.doGet(host);
		
//		page.doGet("http://www.racingpost.com/js/rpost_new.js.sd?2011-06-02-v1", host);
		
        String url = "https://reg.racingpost.com/mpp/sign_in.sd";
        String body = page.doGet(url, host);
//        System.err.println("---------------------------------------------------");
//        System.err.println(body);        
        
        body = page.doGet("https://reg.racingpost.com/js/rpost_new.js.sd?", url);
//        System.err.println("---------------------------------------------------");
        System.out.println(body);  
        
        String Tokenid = Utils.extractMatchValue(body, "tokenId: \"(.*?)\",");
        System.err.println("tokenId = "  + Tokenid);
        
        String post = "https://edeveloper.mppglobal.com/interface/Mpp/eDeveloper/v8/eDeveloper.json.svc/UserAuthenticateByEmail";
        body = page.doOptions(post, url, 1);
//        System.err.println("---------------------------------------------------");
//        System.err.println(body);        
        
        String arg = "{\"EmailUserInfo\":{\"EmailAddress\":\"nicamngai@gmail.com\",\"UserPassword\":\"Momu1618\"}}";        
        body = page.doPost(post, url, null, arg, Tokenid, "application/json; chartset=utf-8", false);
        System.err.println("---------------------------------------------------");
        System.err.println(body);
        
        String GUID = Utils.extractMatchValue(body, "\"GUID\":\"(.*?)\"");
        System.err.println("GUID = "  + GUID);
        
        arg = "guid="+GUID;
        body = page.doPost(url,url, null, arg, null, null, true);
        System.err.println("---------------------------------------------------");
        System.err.println(body);

        arg = "{\"VerifyActiveSession\":{\"NAME\":\"VerifyActiveSession\"}}";
        post = "https://edeveloper.mppglobal.com/interface/Mpp/eDeveloper/v8/eDeveloper.json.svc/VerifyActiveSession";       
//        arg = "{\"UserInfo\":{\"NAME\":\"UserInfo\"}}";
//        post = "https://edeveloper.mppglobal.com/interface/Mpp/eDeveloper/v8/eDeveloper.json.svc/UserInfo";
        
        HttpPost httppost = new HttpPost(post);
		httppost.setHeader("Referer", "https://reg.racingpost.com/mpp/verify_session.sd");
		try {
			httppost.setEntity(new StringEntity(arg));			
		} catch (Exception e) {
		}
		httppost.setHeader("X-Tokenid", Tokenid);
		httppost.setHeader("X-Sessionid", GUID);
		httppost.setHeader("Content-Type", "application/json; chartset=utf-8");
		httppost.setHeader("Accept", "*/*");
		httppost.setHeader("Origin", "https://reg.racingpost.com");
		httppost.setHeader("Host", "edeveloper.mppglobal.com");
		
//	    httppost.setHeader("X-Requested-With", "XMLHttpRequest");
//		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); // ,"UTF-8");
//		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");				
        
        	body = page.doPost(httppost);
	      System.err.println("---------------------------------------------------");
	      System.err.println(body);
	      
	      //{"AccountDetailParameters":[{"ParameterName":"RP_newsletterRP","ParameterValue":"0"},{"ParameterName":"RP_newsletterThirdParty","ParameterValue":"0"},{"ParameterName":"RP_isTermsAgreed","ParameterValue":"1"},{"ParameterName":"RP_rewardsForRacing","ParameterValue":"0"},{"ParameterName":"RP_username","ParameterValue":"nicamngai@gmail.com"},{"ParameterName":"RP_opt_in_bloodstock","ParameterValue":"0"},{"ParameterName":"RP_opt_in_daily_updates","ParameterValue":"0"},{"ParameterName":"RP_date_registered","ParameterValue":"2012-08-04T12:55:09Z"},{"ParameterName":"RP_DOB","ParameterValue":"1976-01-13"},{"ParameterName":"RP_VendorTxCode","ParameterValue":"RP5014564"},{"ParameterName":"RP_opt_in_daily_bloodstock","ParameterValue":"0"}],"AccountGroups":null,"AccountStatus":"Active","AccountType":"Unspecified","Address":{"Country":"Hong Kong","County":"4 Westlands Road","District":"","HouseFlatNumber":"","HouseName":"","PostCode":"","Street":"A2 11F","TownCity":"Hong Kong"},"BillingAddress":{"Country":"","County":"","District":"","HouseFlatNumber":"","HouseName":"","PostCode":"","Street":"","TownCity":""},"ClientUserId":"1280016","CreditCard":{"CardName":"Kam Ngai","CreditCardType":"Visa","DateValidated":"\/Date(1435343771197+0000)\/","ExpiryDate":"05\/18","IsValidated":true,"LastFourDigits":"0640"},"CreditsOnAccount":{"CreditAmounts":[]},"CrmField1":"","CrmField10":"","CrmField2":"","CrmField3":"","CrmField4":"","CrmField5":"","CrmField6":"","CrmField7":"","CrmField8":"","CrmField9":"","DateOfBirth":"\/Date(0+0000)\/","EmailAddress":"nicamngai@gmail.com","FirstName":"Kam","GUID":"aea6d2cf08c84b268725d161e9c2e779","Gender":"Male","HasFailedPostPayOrders":false,"InvoiceAddress":null,"NickName":"Kam","NoMarketingInformation":false,"PhoneNumbers":{"HomePhoneNumber":null,"MobilePhoneNumber":"35215432"},"PreferredCurrency":"GBP","RequiresUserPassword":false,"Surname":"Ngai","Title":"Mr","UnpaidPostPayOrderBalance":0.0000}
	      String IsSessionActive = Utils.extractMatchValue(body, "\"IsSessionActive\":(.*?),");
	      String SessionExpiryDate = Utils.extractMatchValue(body, "Date\\((.*?)\\+");
	      System.err.println("IsSessionActive = "  + IsSessionActive);
	      System.err.println("SessionExpiryDate = "  + SessionExpiryDate);
	      Date d = new Date(Long.parseLong(SessionExpiryDate));
	      System.err.println(d);
	      
//	      url = "http://www.racingpost.com/horses/result_home.sd?race_id=630257";
//	      body = page.doGet(url);
//	      System.out.println("---------------------------------------------------");
//	      System.out.println(body);
	      
	      arg = "{\"ContentSubscriptionCheckCollector\":{\"subscriptionCategoryList\":[\"EDIT NOTES\"],\"requestId\":\"ibt0sak5\"},\"RaceDetailsCollector\":{\"raceId\":630258,\"requestId\":\"ibt0sakh\"}}";
	        httppost = new HttpPost("http://www.racingpost.com/shared/json_api.sd?_1436254398579");
			httppost.setHeader("Referer", url);
			try {
				httppost.setEntity(new StringEntity(arg));			
			} catch (Exception e) {
			}
			httppost.setHeader("Content-Type", "application/x-www-form-urlencode");
			httppost.setHeader("Accept", "*/*");
			httppost.setHeader("X-Requested-With", "XMLHttpRequest");
			
        	body = page.doPost(httppost);
	      System.err.println("---------------------------------------------------");
	      System.err.println(body);
			
        
	}			
	
	public static void testCookie() {
	    CookieStore cookieStore = new BasicCookieStore();
	    
	    
	    
	    CloseableHttpClient httpClient = HttpClients.custom()  
	             .setDefaultCookieStore(cookieStore)
	             //.setDefaultCookieSpecRegistry(CookieSpecs.STANDARD_STRICT)
	             .build();
	    
	    
	    
//	    HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY); 
	    
	     try {  
	          
	          //HttpPost post = new HttpPost("http://www.baiduc.com");  
	    	 HttpGet post = new HttpGet("https://oreg.racingpost.com/auth/signIn");
	          httpClient.execute(post);//  
	          List<Cookie> cookies = cookieStore.getCookies();  
	          for (int i = 0; i < cookies.size(); i++) {  
	              System.out.println("Local cookie: " + cookies.get(i));  
	          }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }finally{  
	              
	        } 		
	}
	
	public boolean doLogin() {
		try {
			
//			String homeUrl = "https://www.racingpost.com";
//			String body = doGet(homeUrl);
			
			String loginUrl = "https://oreg.racingpost.com/auth/signIn";
			String body = doGet(loginUrl);
			String TokenId = Utils.extractMatchValue(body, "tokenId: '(.*?)'");
			System.err.println("TokenId = " + TokenId);
			
	        String post = "https://edeveloper.mppglobal.com/interface/Mpp/eDeveloper/v8/eDeveloper.json.svc/UserAuthenticateByEmail";
	        body = doOptions(post, loginUrl, 1);
	        System.err.println(body);
	        
			String arg = "{\"EmailUserInfo\":{\"EmailAddress\":\"nicamngai@gmail.com\",\"UserPassword\":\"Momu1618\"}}";
			HttpPost httppost = new HttpPost(post);
			httppost.setHeader("Referer", loginUrl);
			try {
				httppost.setEntity(new StringEntity(arg));
			} catch (Exception e) {
			}
			httppost.setHeader("X-Tokenid", TokenId);
			httppost.setHeader("Content-Type", "application/json; chartset=utf-8");
			httppost.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
			httppost.setHeader("Origin", "https://oreg.racingpost.com");
			httppost.setHeader("Host", "edeveloper.mppglobal.com");
			body = doPost(httppost);
			System.out.println(body);
			
			String ClientUserId = Utils.extractMatchValue(body, "\"ClientUserId\":\"(.*?)\"");
			String GUID = Utils.extractMatchValue(body, "\"GUID\":\"(.*?)\"");
			logger.info("ClientUserId = " + ClientUserId + "  GUID = " + GUID);
						
			body = doPost(loginUrl, loginUrl, null, 0, true, "guid="+GUID);
			System.err.println(body);
			
			body = doGet("https://oreg.racingpost.com/json/authPrefs");
			System.err.println(body);
			
			//{"isLoggedIn":true}}
			String isLoggedIn = Utils.extractMatchValue(body, "isLoggedIn\":(.*?)\\}");
			System.err.println("isLoggedIn = " + isLoggedIn);
			
			
			body = doGet("https://www.racingpost.com/results/1083/chelmsford-aw/2017-05-02/672889/analysis");
			System.err.println(body);
			
			if (isLoggedIn.equals("true")) {
				logger.info("Login Successful");
				return true;
			} else {
				logger.info("Login Failure");				
			}
						
			return false;
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}
	
	public static void testJson() {
		try {
			  String url = "http://movil2.maronas.uy:8008/XTurfRestService.svc/GetRaces";
			  String referer = "http://hipica.maronas.com.uy/RacingInfo/Race?RaceTrackId=1&RacingDate=2017-03-17&RaceNumber=6&IsPreProgram=false";
			  String post = "{\"raceTrackId\":1,\"from\":\"2017-03-17T00:00:00.000Z\",\"to\":\"2017-03-17T00:00:00.000Z\",\"ordinalFrom\":6,\"ordinalTo\":6,\"sessionInfo\":{\"PlatformId\":3,\"LanguageCode\":\"es-UY\"}}";
			  
				PageHelper page = PageHelper.getPageHelper();
				String body = page.doPost(url, referer, null, 0, false, post);
				body = body.substring(3);
				System.out.println(body);
				FileDispose.saveFile(body, "F:\\a.js");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
	
//		String url = "http://www.racingpost.com/horses/result_race.sd?race_id=656396";
//		String body = PageHelper.getPageHelper().doGet(url);
//		System.err.println(body);
		
		
//		testJson();
//		testDoGet();
//		testCookie();
		
		PageHelper page = PageHelper.getPageHelper();
		page.doLogin();
		
//		String url = "https://www.baidu.com";
//		String url = "https://www.telegraphindia.com/race/race/501";
//		String body = page.doGet(url);
//		System.err.println(body);
		
//		UKRace();
		
//		String s = "Fautif dans le premier";
//
//		for (int i = 0; i <10000; i++) {
//			System.err.println(PageHelper.getEnglish(s));   
//		}
//		PropertyConfigurator.configure("log4j.properties");
//
//		testDoGet();
		
//        String url = "https://tatts.com/pagedata/racing/2013/12/23//RaceDay.xml";
//        String body = PageHelper.getPageHelper().doGet(url, null);
//        System.err.println(body);
//		
//		String url = "http://www.baidu.com";
		
		//System.setProperty("https.protocols", "TLSv1,SSLv3");
//		System.setProperty("https.protocols", "TLSv1");
//		System.setProperty("https.protocols", "SSLv3");
		
//		SSLSocket socket = (SSLSocket) sslFactory.createSocket(host, port);
//		socket.setEnabledProtocols(new String[]{"SSLv3", "TLSv1"});
		
		//keytool -import -trustcacerts -keystore f:\fr\cacerts -file f:\fr\fr.cer -alias frgeny
//		System.setProperty("javax.net.ssl.trustStore", "f:\fr\fr.cer");
		
//		String url = "https://www2.infocentre.pmu.fr/PMU/Phantoms/PhantomDemarrage";
//		String body = PageHelper.getPageHelper().doGet(url, null);
//        System.err.println(body);
		
//		PageHelper.getPageHelper().Login();
//		test1();		
//		testDoGet();
//		testDoPost();
	}

}
