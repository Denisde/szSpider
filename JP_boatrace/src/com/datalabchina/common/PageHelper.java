package com.datalabchina.common;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
//import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class PageHelper {

	static Logger logger = Logger.getLogger(PageHelper.class.getName());
	static protected CloseableHttpClient httpClient;
	static int httpRetry = 3;
	private static HttpClientContext HTTP_CONTEXT = HttpClientContext.create();
	static String hostIp = "unkown";
	private static HttpHost proxy = new HttpHost("192.168.60.2", 8080);
//	private static HttpHost proxy = new HttpHost("78.129.196.103", 8080);
	private static RequestConfig config = null;
	final static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();
	public static boolean isUsePoxy = false;
	
	public static PageHelper getPageHelper() {
		PropertyConfigurator.configure("log4j.properties");
		PageHelper pageHelper = (PageHelper) threadLocal.get();
		try {
			if (pageHelper == null) {
				pageHelper = new PageHelper();
				PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
				cm.setMaxTotal(1000);
				// http://8366.iteye.com/blog/860173
//				@SuppressWarnings("unused")
//				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//						return null;
//					}
//					public void checkClientTrusted(java.security.cert.X509Certificate[] certs,String authType) {
//					}
//					public void checkServerTrusted(java.security.cert.X509Certificate[] certs,String authType) {
//					}
//				} };
				try {
					hostIp = InetAddress.getLocalHost().getHostAddress();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 创建代理
				if (hostIp.startsWith("192.168.60")) 
				{
					logger.info("use proxy : " + proxy.toURI());
					CredentialsProvider credsProvider = new BasicCredentialsProvider();
					credsProvider.setCredentials(AuthScope.ANY,new NTCredentials("Genius.Zhen", "654321","SZIT033", "datalabchina.com"));
//					CredentialsProvider credsProvider =new BasicCredentialsProvider();
//					credsProvider.setCredentials(new AuthScope("78.129.196.103",8080),new UsernamePasswordCredentials("pmu","se4rfdgy7ujh"));
					 HTTP_CONTEXT.setCredentialsProvider(credsProvider);
					httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultCredentialsProvider(credsProvider).build();
					config = RequestConfig.custom().setSocketTimeout(90000).setConnectTimeout(90000).setProxy(proxy).build();
				} 
				else
				{
					logger.info("no proxy");
					httpClient = HttpClients.custom().setConnectionManager(cm).build();
					config = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();// 设置请求和传输超时时间
				}
				// HTTP_CONTEXT.setAttribute(CoreProtocolPNames.USER_AGENT,
				// "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; FunWebProducts; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; CIBA; IEShow Toolbar; IEShow LinkWanToolBar; InfoPath.2)");
				// initHttpClient(client);
				threadLocal.set(pageHelper);
			}
			return pageHelper;
		} catch (Exception e) {
			logger.error("getPageHelper", e);
			return null;
		}
	}

	
	public byte[] doGetByte(String url, String referer) {
		logger.info("doGetByte : " + url);
		HttpGet request = new HttpGet(url);
		try {
			request.setConfig(config);// 使用代理方式获取连接
			// 如果传入的referer为null 需要手工指定一个 防止网站的防盗链的操作需要 referer
			if (referer != null) {
				request.setHeader("Referer", referer);
			}
			//执行请求 返回响应内容
			CloseableHttpResponse response = httpClient.execute(request,HTTP_CONTEXT);
			try {
				// 可能得不到行响应的内容
				if (response.getStatusLine().getStatusCode() < 200|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "+ response.getStatusLine().getStatusCode());
					return null;
				}
				HttpEntity entity = response.getEntity();
				// 将响应的内容转换为byte类型的数组 返回
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
			request.abort();
			return null;
		}
	}
	public String doGet(String url) {
		return doGet(url, null, 0);
	}
	
	public String doGet(String url,int retry) {
//		return doGet(url, null, 0);
		String body ="";
		logger.info("doGet " + retry + " : " + url);
		 CredentialsProvider cdp = new BasicCredentialsProvider();
		 cdp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("Genis.zhen","654321") );
		  CloseableHttpClient httpClient   = HttpClients.custom().setDefaultCredentialsProvider(cdp).build();
		  HttpHost proxy  = new HttpHost("192.168.60.3",8080);
		  RequestConfig config  = RequestConfig.custom().setProxy(proxy).build();
		  HttpGet httpGet  = new HttpGet(url);
		  httpGet.setConfig(config);
		  CloseableHttpResponse response = null;
		  try {
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			body = EntityUtils.toString(entity,"gb2312");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("doGet", e);
			retry = retry + 1;
			if (retry >= httpRetry) {
				httpGet.abort();
				return "";
			} else {
				httpGet.abort();
				return doGet(url, retry);
			}
		}finally{
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return body;
	}
	public String doGet(String url, String referer) {
		return doGet(url, referer, 0);
	}
	
	
	public String doGet(String url, String referer, int retry) {
		logger.info("doGet " + retry + " : " + url);
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
		//Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36
		try {
			request.setConfig(config);
			if (referer != null) {
				request.setHeader("Referer", referer);
			}
			CloseableHttpResponse response = httpClient.execute(request,HTTP_CONTEXT);
			try {
//				logger.info("The responseCode is ************************"+response.getStatusLine().getStatusCode()+"************************");
				if (response.getStatusLine().getStatusCode() < 200|| response.getStatusLine().getStatusCode() >= 400) {
					if (response.getStatusLine().getStatusCode() == 404) {
						return "404";
					}
					logger.error("Got bad response, error code = "+ response.getStatusLine().getStatusCode());
					return "";
				}
				// System.err.println(response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 302) {
					logger.warn("StatusCode = 302, Location = "+ response.getFirstHeader("Location").getValue());
					return doGet(response.getFirstHeader("Location").getValue(), "");
				}
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String body = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					body = body.replaceAll("&#039;", "'").replaceAll("&quot;","\"");
					return body;
				} 
				else 
				{
					logger.error("entity == null");
					return "";
				}
			} 
//			catch (Exception e) {
//				logger.error("",e);
//				return "";
//			}
			finally {
				response.close();
			}
		}
		catch (Exception e) 
		{
			logger.error("doGet", e);
			retry = retry + 1;
			if (retry >= httpRetry) {
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
	public String doPostByAjax(String url, String referer,List<NameValuePair> nameValuePairs, String content){
		return doPost(url, referer, nameValuePairs, 0, true,content);
	}
	public String doPost(String url, String referer,List<NameValuePair> nameValuePairs) {
		return doPost(url, referer, nameValuePairs, 0, false,null);
	}
	public String doPost(String url, String referer,List<NameValuePair> nameValuePairs, int retry, boolean Ajax, String content) {
		logger.info("doPost " + retry + " : " + url); // + "\nnameValuePairs = "// nameValuePairs.toString());
		HttpPost httppost = new HttpPost(url);
		try {
			httppost.setConfig(config);
			if (referer != null) {
				httppost.setHeader("Referer", referer);
			}
			if (Ajax){
//				httppost.setHeader("x-requested-with", "XMLHttpRequest");
				httppost.setHeader("X-AjaxPro-Method", "GetTracksExtended");
				httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
			}
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); // ,"UTF-8");
			// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			
			  if (content != null) {
				    httppost.setEntity(EntityBuilder.create().setText(content).build());
				   } else {
				    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); //,"UTF-8");    
				   }			
			CloseableHttpResponse response = httpClient.execute(httppost,HTTP_CONTEXT);
			try {
				if (response.getStatusLine().getStatusCode() < 200|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "+ response.getStatusLine().getStatusCode());
					return "";
				}
				if (response.getStatusLine().getStatusCode() == 302) {
					logger.warn("StatusCode = 302, Location = "+ response.getFirstHeader("Location").getValue());
					return doGet(response.getFirstHeader("Location").getValue(), "");
				}
				HttpEntity entity = response.getEntity();
				if (entity != null) 
				{
					String body = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					body = body.replaceAll("&#039;", "'").replaceAll("&quot;","\"");
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
				return doPost(url, referer, nameValuePairs, retry, false, content);
			}
		}
	}

	public String doPost(HttpPost httppost) {
		logger.info("doPost " + httppost.getURI());
		try {
			httppost.setConfig(config);
			CloseableHttpResponse response = httpClient.execute(httppost,HTTP_CONTEXT);
			try {
				if (response.getStatusLine().getStatusCode() < 200|| response.getStatusLine().getStatusCode() >= 400) {
					logger.error("Got bad response, error code = "+ response.getStatusLine().getStatusCode());
					return "";
				}
				if (response.getStatusLine().getStatusCode() == 302) {
					logger.warn("StatusCode = 302, Location = "+ response.getFirstHeader("Location").getValue());
					return doGet(response.getFirstHeader("Location").getValue(), "");
				}
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String body = EntityUtils.toString(entity,"UTF-8");
//					String body = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					body = body.replaceAll("&#039;", "'").replaceAll("&quot;","\"");
					return body;
				}else{
					logger.error("entity == null");
					return "";
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error("doPost", e);
			return "";
//			retry = retry + 1;
//			if (retry >= httpRetry) {
//				httppost.abort();
//				return "";
//			} else {
//				httppost.abort();
//				return doPost(url, referer, nameValuePairs, retry, false);
//			}
		}
	}	
	
	
	
	protected static void testDoGet() {
		String url = "https://www.german-racing.com/gr/renntage/rennkalender.php";
		String body = PageHelper.getPageHelper().doGet(url, null);
		System.err.println(body);
	}
	protected static void testDoPost() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		 nameValuePairs.add(new BasicNameValuePair("username", ""));
		nameValuePairs.add(new BasicNameValuePair("password", ""));
		String url = "";
		String body = PageHelper.getPageHelper().doPost(url, null,nameValuePairs);
		System.err.println(body);
	}
	protected static void test1() {
		String url = "https://www.darkhorsebet.com/";
		String body = PageHelper.getPageHelper().doGet(url, null);
		// System.err.println(body);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("accountNumber", "0124001624"));
		nameValuePairs.add(new BasicNameValuePair("pin", "8490"));
		body = PageHelper.getPageHelper().doPost("https://www.darkhorsebet.com/ebet/login", null,nameValuePairs);
		nameValuePairs = new ArrayList<NameValuePair>();
		body = PageHelper.getPageHelper().doPostByAjax("https://bet.darkhorsebet.com/start.myaccountbalance/myAccountBalance",null, nameValuePairs, null);
		System.err.println(body);
		// FileDispose.saveFile(body, "d:\\doPost.html");
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		// test1();
//		testDoGet();
//		testdoPostWebService();
		// testDoPost();
		  String url = "http://www.equibase.com/static/chart/summary/index.html?SAP=LN";
//	     String body1 = PageHelper.getPageHelper().doGet("http://www.equibase.com",url);
	      String body = PageHelper.getPageHelper().doGet(url,url);
//	       body = PageHelper.getPageHelper().doGet("http://www.equibase.com/distil_r_captcha.html?requestId=f62d53b5-1435-4258-b7a7-63c903887730&httpReferrer=%2Fstatic%2Fchart%2Fsummary%2Findex.html%3FSAP%3DLN");
	      System.out.println(body);
	}
	
}
