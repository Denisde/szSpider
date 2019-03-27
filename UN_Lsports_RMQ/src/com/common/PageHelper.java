package com.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class PageHelper implements Runnable{
	
	static Logger logger = Logger.getLogger(PageHelper.class.getName());
	
	static protected CloseableHttpClient httpClient;
	
	static boolean enableProxy = false;
	static Boolean exitNoProxy = false;
	static Boolean testProxy = true;
	
	static int httpRetry = 10;
	static int iChangeProxy = 20;
	static int iCount = -1;
	static int iErrorToRemove = 20;
	static int iGetProxyBySqlFreq = 10;
	static HashMap<String, Integer> hmError = new HashMap<String, Integer>(); 
	
	static boolean debug = true;
	
	private static HttpClientContext HTTP_CONTEXT = HttpClientContext.create();
	
	static String hostIp = "unkown";
	
	static String Sql = null;
	static String TestUrl = null;
	static String KeyWord = null;
	static String proxyName = null;
	
	static String DB_Driver = null;
	static String DB_Url = null;
	static String DB_User = null;
	static String DB_Password = null;	
	
	private static Object lock = new Object();
	
	static String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

	private static HttpHost proxy = null;
	private static CredentialsProvider credsProvider = new BasicCredentialsProvider();
	
	final static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();
	private static RequestConfig config = null;
	private static BasicCookieStore cookieStore = new BasicCookieStore();
	
	private static PoolingHttpClientConnectionManager cm = null;
	
	private static List<String> proxyList = new ArrayList<String>();
	
	private static long lastTime = System.currentTimeMillis();
	
	PageHelper() {
		
	}
	
	public void run() {
		getProxybySql();
		logger.info(proxyList.size() + " proxy can use:");
		for (String url : proxyList) {
			logger.info(url);
		}		
	}

	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");
//		SSLContext sc = SSLContext.getInstance("TLS");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}
	
	static {
		try {
			PropertyConfigurator.configure("log4j.properties");			
			logger.info("PageHelper version : 20170615");
			
			// 采用绕过验证的方式处理https请求
			SSLContext sslContext = createIgnoreVerifySSL();
			
			// 设置协议http和https对应的处理socket链接工厂的对象
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslContext)).build();
			
			cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			HttpClients.custom().setConnectionManager(cm);
			
			String file = "config.xml";
			if (!bIfExistFile(file)) {
				file = "config/config.xml";
			}
			
			if (!bIfExistFile(file)) {
				logger.error("config.xml or config/config.xml not find.");
			} else {
			
				String xml = readFile(file);
				
				String sEnableProxy = extractMatchValue(xml, "<enableProxy>(.*?)</");
				if (sEnableProxy != null && sEnableProxy.equals("1")) {
					enableProxy = true;
				} else {
					logger.warn("to use proxy, set <enableProxy>1</enableProxy> in config.xml");
				}

				String sTestProxy = extractMatchValue(xml, "<testProxy>(.*?)</");
				if (sTestProxy != null && sTestProxy.equals("0")) {
					testProxy = false;
				} else {
					logger.warn("to test proxy, set <testProxy>1</testProxy> in config.xml");
				}				
								
				
				setContext();
				if (enableProxy) {
					
//					<proxyName>UK</proxyName>
//					<proxyConfigPath>D:\config</proxyConfigPath>			
					proxyName = extractMatchValue(xml, "<proxyName>(.*?)</");
					String proxyConfigPath = extractMatchValue(xml, "<proxyConfigPath>(.*?)</");
					
					String sExitNoProxy = extractMatchValue(xml, "<exitNoProxy>(.*?)</");
					if (sExitNoProxy != null && sExitNoProxy.equals("1")) {
						exitNoProxy = true;
					}
					
					if (proxyName == null) {
						proxyName = extractMatchValue(xml, "<databaseName>(.*?)</");
					}
					if (proxyConfigPath == null) {
						proxyConfigPath = extractMatchValue(xml, "<dbconfigPath>(.*?)</");
					}
					
//					<databaseName>UKDB</databaseName>
//					<dbconfigPath>D:\config</dbconfigPath>					
					
					if (debug) {
						System.out.println("proxyName = " + proxyName);
						System.out.println("proxyConfigPath = " + proxyConfigPath);
					}
					
					file = proxyConfigPath + File.separator + "proxyConfig.xml";
					
					if (!bIfExistFile(file)) {
						logger.error(file + " not find.");
					} else {
						
						xml = readFile(file);
						
						//<name>SE</name>
						
						DB_Driver = extractMatchValue(xml, "<DB_Driver>(.*?)</");
						DB_Url = extractMatchValue(xml, "<DB_Url>(.*?)</");
						DB_User = extractMatchValue(xml, "<DB_User>(.*?)</");
						DB_Password = extractMatchValue(xml, "<DB_Password>(.*?)</");
						
						String defaultRetry = extractMatchValue(xml, "<defaultRetry>(.*?)</");
						String defaultChangeProxy = extractMatchValue(xml, "<defaultChangeProxy>(.*?)</");
						String defaultErrorToRemove = extractMatchValue(xml, "<defaultErrorToRemove>(.*?)</");
						
						String defaultUrl = extractMatchValue(xml, "<defaultUrl>(.*?)</");
						String defaultKeyWord = extractMatchValue(xml, "<defaultKeyWord>(.*?)</");
						
						String sUserAgent = extractMatchValue(xml, "<defaultUserAgent>(.*?)</");
						if (sUserAgent != null && !sUserAgent.equals("")) {
							UserAgent = sUserAgent;
						}
						
						xml = extractMatchValue(xml, "<name>"+proxyName+"(.*?)</proxys>");
						Sql = extractMatchValue(xml, "<sql>(.*?)</");
						TestUrl = extractMatchValue(xml, "<url>(.*?)</");
						KeyWord = extractMatchValue(xml, "<keyWord>(.*?)</");
						
						if (TestUrl == null) TestUrl = defaultUrl;
						if (KeyWord == null) KeyWord = defaultKeyWord;
						
						if (debug) {
							System.out.println("TestUrl = " + TestUrl);
							System.out.println("Sql = " + Sql);
							System.out.println("KeyWord = " + KeyWord);
						}			
						
						String retry = extractMatchValue(xml, "<retry>(.*?)</");
						String changeProxy = extractMatchValue(xml, "<changeProxy>(.*?)</");
						String errorToRemove = extractMatchValue(xml, "<errorToRemove>(.*?)</");
						
						if (retry == null) retry = defaultRetry;
						if (changeProxy == null) changeProxy = defaultChangeProxy;
						if (errorToRemove == null) errorToRemove = defaultErrorToRemove;
						
						if (debug) {
							System.out.println("retry = " + retry);
							System.out.println("changeProxy = " + changeProxy);
							System.out.println("errorToRemove = " + errorToRemove);
						}
						
						try {
							httpRetry = Integer.parseInt(retry);	
						} catch (Exception e) {
							httpRetry = 10;	
						}
						
						try {
							iChangeProxy = Integer.parseInt(changeProxy);
						} catch (Exception e) {
							iChangeProxy = 20;
						}
						
						try {
							iErrorToRemove = Integer.parseInt(errorToRemove);
						} catch (Exception e) {
							iErrorToRemove = 20;
						}
						
						if (sExitNoProxy == null) {
							sExitNoProxy = extractMatchValue(xml, "<exitNoProxy>(.*?)</");
							if (sExitNoProxy != null && sExitNoProxy.equals("1")) {
								exitNoProxy = true;
							}
						}
						
						sUserAgent = extractMatchValue(xml, "<userAgent>(.*?)</");
						if (sUserAgent != null && !sUserAgent.equals("")) {
							UserAgent = sUserAgent;
						}
						
						getProxybySql();
						
						if (proxyList.size() == 0) {
							List<String> list = extractMatchValues(xml, "<proxy>(.*?)</proxy>");
							for (String line : list) {
								if (debug) {
									System.out.println("line = " + line);					
								}
								if (line.length() >= 4) {
									if (line.indexOf(".") > 0) {
										//System.err.println(line);
										//proxyList.add(line);
										testProxy(line);
									}
								}
							}				
						}
						
					} //else
					
				} //if (enableProxy) {
				
			} //else
			
			setContext();
			if (enableProxy == false) {
				httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultCookieStore(cookieStore).build();
				config = RequestConfig.custom().setSocketTimeout(90000).setConnectTimeout(90000).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
			}
			
			logger.info(proxyList.size() + " proxy can use:");
			for (String url : proxyList) {
				logger.info(url);
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	
	private static void getProxybySql() {
		try {
			if (enableProxy == false) return;
			if (Sql == null || Sql.equals("")) return;
				
			try {
				ResultSet rs = getResultBySelect(Sql);
				while (rs.next()) {
					
					String ProxyID = rs.getString("ProxyID");
					String ProxyURL = rs.getString("ProxyURL");
					String ProxyPort = rs.getString("ProxyPort");
					String ProxyUser = rs.getString("ProxyUser");
					String ProxyPassword = rs.getString("ProxyPassword");
					String Type = rs.getString("Type");
					String Description = rs.getString("Description");
					String Expired = rs.getString("Expired");
					String ProxyLocation = rs.getString("ProxyLocation");
					
					if (debug) {
						System.out.println("=====================================");
						System.out.println("ProxyID = " + ProxyID);
						System.out.println("ProxyURL = " + ProxyURL);
						System.out.println("ProxyPort = " + ProxyPort);
						System.out.println("ProxyUser = " + ProxyUser);
						System.out.println("ProxyPassword = " + ProxyPassword);
						
						System.out.println("Type = " + Type);
						System.out.println("Description = " + Description);
						System.out.println("Expired = " + Expired);
						
						System.out.println("ProxyLocation = " + ProxyLocation);
					}					
					
					String IsUserDisable = rs.getString("IsUserDisable");
					String LastCheckTime = rs.getString("LastCheckTime");
					String DB_URL = rs.getString("URL");
					String DB_KeyWord = rs.getString("KeyWord");
					String IsBlock = rs.getString("IsBlock");
					String ProxyStatus = rs.getString("ProxyStatus");
					
					if (debug) {
						System.out.println("IsUserDisable = " + IsUserDisable);
						System.out.println("LastCheckTime = " + LastCheckTime);
						System.out.println("URL = " + DB_URL);
						
						System.out.println("KeyWord = " + DB_KeyWord);
						System.out.println("IsBlock = " + IsBlock);
						System.out.println("ProxyStatus = " + ProxyStatus);
					}
					
					if (Expired.equals("1")) continue;
					if (IsUserDisable.equals("1")) continue;
					
					if (DB_URL != null && !DB_URL.equals("")) TestUrl = DB_URL;
					if (DB_KeyWord != null && !DB_KeyWord.equals("")) KeyWord = DB_KeyWord;
					
					String line = ProxyURL + ":" + ProxyPort + ":" + ProxyUser + ":" + ProxyPassword;
					testProxy(line);
					
				}
				
			} catch (Exception e) {
				logger.error("", e);
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}
	}
		
	private static void testProxy(String line) {
		try {
			
			if (proxyExist(line)) return;
			
			if (testProxy == false) {
				addProxy(line);
				return;
			}
			
			logger.info("Test Proxy : " + line);
			int ProxyStatus = 0;
			int IsBlock = 0;
			try {
				
				String a[] = line.split(":");
				String strProxy = a[0];
				String strPort = a[1];
				String strUser = null;
				String strPasswd = null;
				try {
					strUser = a[2];
					strPasswd = a[3];					
				} catch (Exception e) {
				}
				
				setContext();
				
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				HttpHost proxy = new HttpHost(strProxy, Integer.parseInt(strPort));
				if (strUser != null && strUser.length() > 1) {
					credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(strUser,strPasswd));																		
				}
				
				BasicCookieStore cookieStore = new BasicCookieStore();
				CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).setDefaultCookieStore(cookieStore).build();
				RequestConfig config = RequestConfig.custom().setSocketTimeout(90000).setConnectTimeout(90000).setProxy(proxy).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
				
				
				
				HttpGet request = new HttpGet(TestUrl);
				request.setConfig(config);
				request.setHeader("User-Agent", UserAgent);
				
				CloseableHttpResponse response = null;
				try {
					response = httpClient.execute(request, HTTP_CONTEXT);				
					int StatusCode = response.getStatusLine().getStatusCode();
					if (StatusCode < 200 || StatusCode >= 400) {
						if (StatusCode == 403) IsBlock = 1;
					}
					
					HttpEntity entity = response.getEntity();
					String body = EntityUtils.toString(entity);
					System.out.println("body = " + body);
					
					if (body.indexOf("Access Denied") >=0) {
						IsBlock = 1;
					}
					
					if (KeyWord != null) {
						if (body.indexOf(KeyWord) >= 0) {
							System.out.println("find KeyWord = " + KeyWord);		
							ProxyStatus = 1;
						}
					} else {
						ProxyStatus = 1;
					}
					EntityUtils.consume(entity);
				} catch (Exception e) {
					logger.error("", e);						
				} finally {
					try {
						response.close();
//							httpClient.close();
//							cm.close();
					} catch (Exception e) {
					}
				}
				
				if(ProxyStatus == 1) {
					addProxy(line);
				} else {
					logger.warn("remove " + strProxy + ":" + strPort);
				}
				
				String LastCheckTime = getLongStr();
				StringBuffer sbSql = new StringBuffer();  				//pr_Spider_Proxy_WebSite_InsertData
				appendStringParameter(sbSql,strProxy);					//varchar(20),
				appendParameter(sbSql,strPort);							//int,
				appendParameter(sbSql,"0");								//bit = NULL,
				appendStringParameter(sbSql,LastCheckTime);				//smalldatetime = NULL,
				appendStringParameter(sbSql,TestUrl);					//varchar(200),
				appendStringParameter(sbSql,KeyWord);					//varchar(100) = NULL,
				appendParameter(sbSql,IsBlock+"");						//bit = NULL,
				appendParameter(sbSql,ProxyStatus+"");					//bit = NULL					
				appendStringParameter(sbSql,proxyName);					//Label varchar(100) = NULL

				
				String sql = sbSql.toString();
				sql = sql.substring(1);
				if (debug) System.out.println("sql = " + sql);
				
				if (!execStoredProcedures("pr_Spider_Proxy_WebSite_InsertData",sql)) {
					logger.error("pr_Spider_Proxy_WebSite_InsertData " + sql); 
				}
				
			} catch (Exception e) {
				logger.error("", e);
			}
					
		} catch (Exception e){
			logger.error("", e);
		}
	}
	
	private static void addProxy(String line) {
		if (line == null) return;
		if (line.length() < 4) return;
		if (line.indexOf(".") <= 0) return;
		
		synchronized (lock) {
			boolean exist = false;
			for (String p : proxyList) {
				if (p.equalsIgnoreCase(line)) {
					exist = true;
				}
			}
			
			if (exist == false) {
				proxyList.add(line);
			}
		}
	}
	
	private static boolean proxyExist(String line) {
		if (line == null) return true;
		if (line.length() < 4) return true;
		if (line.indexOf(".") <= 0) return true;
		
		synchronized (lock) {
			boolean exist = false;
			for (String p : proxyList) {
				if (p.equalsIgnoreCase(line)) {
					exist = true;
				}
			}
			
			return exist;
		}
	}	
		
	private static String getProxy(boolean remove) {
		String p = null;
		
		long time = System.currentTimeMillis();
		if ((time - lastTime) > (1000 * 60 * iGetProxyBySqlFreq)) {
			lastTime = time;
			new Thread(new PageHelper(), "").start();
		}
		
		synchronized (lock) {
			int size = proxyList.size(); 
			if (size > 0 && remove) {
				proxyList.remove(size-1);
			}
			
			size = proxyList.size();
			if (size == 0) {
				if (exitNoProxy) {
					logger.error("no proxy to use. spider will exit.");
					System.exit(0);
				}
				return null;
			}
			
			p = proxyList.get(0);
			proxyList.remove(0);
			proxyList.add(p);			
		}
		return p;
	}	
	
	  public static void setContext() {
//		    System.out.println("----setContext");
		    
		    HTTP_CONTEXT = HttpClientContext.create();
//		    Registry<CookieSpecProvider> registry = RegistryBuilder
//		        .<CookieSpecProvider> create()
//		        .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
//		        .register(CookieSpecs.BROWSER_COMPATIBILITY,
//		            new BrowserCompatSpecFactory()).build();
//		    HTTP_CONTEXT.setCookieSpecRegistry(registry);
//		    HTTP_CONTEXT.setCookieStore(cookieStore);
		  
		  HTTP_CONTEXT.setAttribute("User-Agent", UserAgent);
		  HTTP_CONTEXT.setAttribute("Accept-Encoding", "gzip,deflate");
		  }
	
	public static PageHelper getPageHelper() {
		PageHelper pageHelper = (PageHelper) threadLocal.get();
		try {
			if (pageHelper == null) {
				pageHelper = new PageHelper();
				threadLocal.set(pageHelper);
			}
			return pageHelper;

		} catch (Exception e) {
			logger.error("getPageHelper", e);
			return null;
		}
	}
	
	
	public static void setErrorProxy() {
		try {
			if (proxy == null) return;
			String currProxy = proxy.getHostName() + ":" + proxy.getPort();
			Integer iError = hmError.get(currProxy);
			if (iError == null) iError = 0;
			iError++;
			hmError.put(currProxy, iError);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	
	public static void changeProxy(int retry) {
		try {
			
			if (enableProxy == false) return;
			
			String strProxy = null;
			String strPort = null;
			String strUser = null;
			String strPasswd = null;
	
			iCount++;			
			if (iCount % iChangeProxy > 0) {
				if (retry == 0) {
					return;
				}
			}
			
			Boolean bRemove = false;
			if (proxy != null) {
				String currProxy = proxy.getHostName() + ":" + proxy.getPort(); 
				Integer iError = hmError.get(currProxy);				
				if (iError != null && iError > iErrorToRemove) {
					logger.error(currProxy + " remove where error > " + iErrorToRemove);
					bRemove = true;
				}				
			}
			
			String p = getProxy(bRemove);
			if (p != null) {
				String a[] = p.split(":");
				strProxy = a[0];
				strPort = a[1];
				try {
					strUser = a[2];
					strPasswd = a[3];					
				} catch (Exception e) {
				}
			}
			
			setContext();
			
			if (strProxy != null && strProxy.length() > 1) {
				logger.info("use proxy : " + strProxy);
				proxy = new HttpHost(strProxy, Integer.parseInt(strPort));
				if (strUser != null && strUser.length() > 1) {
					credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(strUser,strPasswd));																		
				}
				
				httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).setDefaultCookieStore(cookieStore).build();
		    	config = RequestConfig.custom().setSocketTimeout(90000).setConnectTimeout(90000).setProxy(proxy).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();		        																		
			} else {
				
				httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
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
		changeProxy(retry);
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
					
					setErrorProxy();
					retry = retry + 1;
					request.abort();
					if (retry >= httpRetry) {
						return null;				
					} else {
						return doGetByte(url, referer, retry);
					}					
				}
	
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					byte[] b = EntityUtils.toByteArray(entity);
					EntityUtils.consume(entity);
					return b;
				} else {
					logger.error("entity == null");
					return null;
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error("doGetByte", e);
			setErrorProxy();
			retry = retry + 1;
			request.abort();
			if (retry >= httpRetry) {
				return null;
			} else {
				return doGetByte(url, referer, retry);
			}
		}

	}

	public String doGet(String url)	{
		return doGet(url, null, 0);
	}
	
	
	public String doGet(String url, String referer)	{
		return doGet(url, referer, 0);
	}
	
	
	
	public String doOptions(String url, String referer,int retry)
	{
		changeProxy(retry);
		logger.info("doOptions " + retry + " : " + url);
		HttpOptions httpOptions = new HttpOptions(url);
		try
		{
			httpOptions.setConfig(config);
			if (referer != null) 
			{
				httpOptions.setHeader("Referer", referer);
			}
			httpOptions.setHeader("User-Agent", UserAgent);
			
			CloseableHttpResponse response = httpClient.execute(httpOptions, HTTP_CONTEXT);
			try 
			{
				if (response.getStatusLine().getStatusCode() < 200	|| response.getStatusLine().getStatusCode() >= 400)
				{
					if (response.getStatusLine().getStatusCode() == 404) 
					{
						return "404";	
					}					
					
					setErrorProxy();
					logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
					retry = retry + 1;
					httpOptions.abort();
					if (retry >= httpRetry)
					{
						return "";				
					}
					else
					{
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
			logger.error("doOptions", e);
			setErrorProxy();
			retry = retry + 1;
			httpOptions.abort();
			if (retry >= httpRetry)
			{
				return "";				
			} else 
			{
				return doOptions(url, referer, retry);
			}				
		}

	}	
	
	public String doGet(String url, String referer,int retry) 
	{
		changeProxy(retry);
		logger.info("doGet " + retry + " : " + url);
		HttpGet request = new HttpGet(url);
		
		try {
//			Thread.sleep(500);
			request.setConfig(config);
			if (referer != null)
			{
				request.setHeader("Referer", referer);
			}
			
			request.setHeader("Accept-Encoding", "gzip,deflate");
			request.setHeader("User-Agent", UserAgent);

			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(request, HTTP_CONTEXT);
//				if (response.getStatusLine().getStatusCode() < 200
//						|| response.getStatusLine().getStatusCode() >= 400) {
//					if (response.getStatusLine().getStatusCode() == 404) {
//						return "404";	
//					}
//					
//					if (response.getStatusLine().getStatusCode() != 503) {
//						
//						logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
//						setErrorProxy();
//						retry = retry + 1;
//						request.abort();
//						if (retry >= httpRetry)
//						{
//							return "";				
//						}
//						else
//						{
//							return doGet(url, referer, retry);
//						}
//						
//					}
//				}
				
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
					
//					body = body + "\n\n<!-- url=" + url + " -->";
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
				if (response != null) response.close();
			}
		} 
		catch (Exception e)
		{
			logger.error("doGet", e);
			setErrorProxy();
			retry = retry + 1;
			request.abort();
			if (retry >= httpRetry)
			{
				return "";				
			}
			else
			{
				return doGet(url, referer, retry);
			}				
		}
	}
	

	public String doPostByAjax(String url, String referer,	List<NameValuePair> nameValuePairs)	{
		return doPost(url, referer,nameValuePairs, 0, true, null);
	}
	
	public String doPost(String url, String referer, List<NameValuePair> nameValuePairs) {
		return doPost(url, referer,nameValuePairs, 0, false, null);
	}
	
	public String doPost(String url, String postXML) {
		return doPost(url, null, null, 0, false, postXML);				
	}	
	
	public String doPost(String url, String referer, List<NameValuePair> nameValuePairs,int retry, boolean Ajax, String content) {		
		changeProxy(retry);
		logger.info("doPost " + retry + " : " + url); // + "\nnameValuePairs = " + nameValuePairs.toString());
		HttpPost httppost = new HttpPost(url);

		try {
			
			httppost.setConfig(config);
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			if (referer != null) {
				httppost.setHeader("Referer", referer);
			}
			httppost.setHeader("User-Agent", UserAgent);
			
			if (Ajax) httppost.setHeader("X-Requested-With", "XMLHttpRequest");

			if (content != null) {
				httppost.setEntity(EntityBuilder.create().setText(content).build());
			} else {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); //,"UTF-8");				
			}			

			CloseableHttpResponse response = httpClient.execute(httppost, HTTP_CONTEXT);
			try {
				
				if (response.getStatusLine().getStatusCode() < 200
						|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
					setErrorProxy();
					retry = retry + 1;
					httppost.abort();
					if (retry >= httpRetry) {
						return "";				
					} else {
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
			setErrorProxy();
			retry = retry + 1;
			httppost.abort();
			if (retry >= httpRetry) {
				return "";				
			} else {
				return doPost(url, referer,nameValuePairs, retry, false, content);
			}
		}

	}	
	
	public String doPost(HttpPost httppost) {
		return doPost(httppost, 0);
	}
	
	public String doPost(HttpPost httppost, int retry) {
		changeProxy(retry);
		try {
			httppost.setHeader("User-Agent", UserAgent);
			httppost.setConfig(config);
			CloseableHttpResponse response = httpClient.execute(httppost, HTTP_CONTEXT);
			try
			{			
				if (response.getStatusLine().getStatusCode() < 200
						|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "	+ response.getStatusLine().getStatusCode());
					setErrorProxy();
					httppost.abort();
					retry = retry + 1;
					if (retry >= httpRetry) {
						return "";				
					} else {
						return doPost(httppost, retry);
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
			setErrorProxy();
			logger.error("doPost", e);
			
			setErrorProxy();
			httppost.abort();
			retry = retry + 1;
			if (retry >= httpRetry) {
				return "";				
			} else {
				return doPost(httppost, retry);
			}			
		}
	}
	
	
	//======================================================
	
	public static boolean bIfExistFile(String sFileName) {
		try {
			File fFile = new File(sFileName);
			if (fFile.exists()) {
				return true;
			}
		} catch (Exception e) {
			logger.error("bIfExistFile ", e);
		}
		return false;
	}	
	 
	public static String readFile(String filename) {
		java.io.InputStream is = null;
		try {
			is = new FileInputStream(filename);

			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			
			try {
				is = new GZIPInputStream(is); // is zip file
			} catch (Exception e) { //not zip file
				is.close();
				is = new FileInputStream(filename);
			}

			while ((rc = is.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			is.close();
			byte[] bit = swapStream.toByteArray();
			
			return (new String(bit));
		} catch (Exception e) {
			logger.error("readFile ", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}		
	}
	
	public static String extractMatchValue(String strBody, String strPattern) {
		if (strBody == null)
			return null;
		String val = null;
		try {
			Matcher matcher = getMatcherStrGroup(strBody, strPattern);
			if (matcher.find()) {
				val = matcher.group(1).trim();
				if (val.equals(""))
					return null;
			}
		} catch (Exception e) {
			logger.error("extractStatusText ", e);
		}
		return val;
	}

	public static ArrayList<String> extractMatchValues(String strBody,
			String strPattern) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Matcher matcher = getMatcherStrGroup(strBody, strPattern);
			while (matcher.find()) {
				String val = matcher.group(1).trim();
				list.add(val);
			}
		} catch (Exception e) {
			logger.error("extractMatchValues ", e);
		}
		return list;
	}
	
	public static Matcher getMatcherStrGroup(String strContent,
			String strPattern) {
		Pattern pattern = Pattern.compile(strPattern, Pattern.DOTALL
				| Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(strContent);
		return matcher;
	}
	
	public static String getLongStr() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}	
	
	//======================================================
	
	public static Connection getConnection() {
		try {
			if (DB_Driver == null) return null;
			Class.forName(DB_Driver);
			Connection con = DriverManager.getConnection(DB_Url, DB_User, DB_Password);
			return con;	
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	public static ResultSet getResultBySelect(String sql) {
		Connection conn =  getConnection();
		if (conn == null) return null;
		Statement stm = null;
		ResultSet rs = null;
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			return rs;
		} catch (Exception fe) {
			logger.error("sql = " + sql, fe);
			return null;
		}
	}
	
	public static boolean execStoredProcedures(String psName, String psParaList) {
		Connection conn =  getConnection();
		if (conn == null) return false;
		CallableStatement cs = null;
		String callPsStr = "{call " + psName + "(" + psParaList + ")}";
		boolean isSucc = false;
		try {
			cs = conn.prepareCall(callPsStr);
			isSucc = cs.execute();
			isSucc = true;
		} catch (Exception fe) {
			logger.error("ExecStoredProcedures  " + callPsStr, fe);
			return false;
		} finally {
			try {
				cs.close();
				conn.close();				
			} catch (Exception e) {
			}
		}
		return isSucc;
	}	
	
//	public ResultSet query(String sql,String... pras) {  
//        con=getCon();  
//        try {  
//            ps=con.prepareStatement(sql);  
//            if(pras!=null)  
//                for(int i=0;i<pras.length;i++){  
//                    ps.setString(i+1, pras[i]);  
//                }  
//            rs=ps.executeQuery();  
//        } catch (SQLException e) {  
//            e.printStackTrace();  
//        }  
//        return rs;  
//    }  
//}	
	
	public static void appendParameter(StringBuffer sb, String parameter) {
		if (parameter != null && parameter.trim().equals("")) {
			parameter = null;
		}
			
		sb.append("," + parameter);			
	}
	
	
	public static void appendStringParameter(StringBuffer sb, String parameter) {
		if (parameter == null) {
			sb.append("," + parameter);			
		} else {
			parameter = parameter.replaceAll("'", "''");
			sb.append(",'" + parameter + "'");
		}
	}	
	
	
	//======================================================
	
	protected static void testDoGet() {
        String url = "http://www.baidu.com/";
        String body = PageHelper.getPageHelper().doGet(url, null);
        System.err.println(body);
	}
	
	
	protected static void testDoPost() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("password", ""));
        
        String url = "";
        String body = PageHelper.getPageHelper().doPost(url, null, nameValuePairs);
        System.err.println(body);
	}
	
	
	public static void testCookie() {
	    CookieStore cookieStore = new BasicCookieStore();
	    
	    CloseableHttpClient httpClient = HttpClients.custom()  
	             .setDefaultCookieStore(cookieStore)
	             //.setDefaultCookieSpecRegistry(CookieSpecs.STANDARD_STRICT)
	             .build();
	    
		try {

			HttpGet post = new HttpGet(
					"https://oreg.racingpost.com/auth/signIn");
			httpClient.execute(post);//
			List<Cookie> cookies = cookieStore.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
				System.out.println("Local cookie: " + cookies.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//======================================================
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		PageHelper.debug = true;		
	
//		String url = "http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&lang=en&oddsFormat=EU&sports=687888&countries=172";
//		String url = "http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&lang=en&oddsFormat=EU&sports=687888";
//		String url = "http://ahceur2.txodds.com/OddsData/DB/groups.jsp";
//		String url = "https://xml2.txodds.com/feed/ap_race_fixtures.php?ident=pythiasports&passwd=gu8raQeMAstu&spid=76";
//		String body = PageHelper.getPageHelper().doGet(url);
//		FileDispose.saveFile(body, "D:\\mnt\\ap_race_fixtures.xml");
//		System.err.println(body);
		
		PageHelper page = PageHelper.getPageHelper();
//		
		for (int i = -1; i< 100; i++ ) {
			System.out.println(i + "   " + i % iChangeProxy);
			String url = "https://umanity.jp/professional/race_view.php?user_id=da2ba9324a&race_id=2018112505050810";
			//"http://www.baidu.com"
			String body = page.doGet(url);
//			System.out.println(body);
			try {
				Thread.sleep(1000 * 5);
			} catch (Exception e) {
			}
		}
		
	}

}
