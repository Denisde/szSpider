package com.datalabchina.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author wgb
 */

public class Config {

	java.util.Properties props;

	java.io.InputStream propStream;

	String DBTypeName = "";

	static Logger logger = Logger.getLogger(Config.class.getName());

	private Vector<Vector<String>> vBLLList;

	//private Vector vDBConn;

	private Vector<Vector<String>> vMailList;

	private String sOrderType;

	private Vector<Vector<String>> vProxyInfo;
	
	private int GetDataBeforMinute = 0;
	
	private int LoginRetry = 0;	

	private int ValidationCodeTimeout = 90000;
	
	private int HttpTimeout = 90000;
	
	private String dbconfigPath ="/home/szspider/config";
	
	private String DBName ="";
	
	public String getDBName() {
		return DBName;
	}

	public void setDBName(String dBName) {
		DBName = dBName;
	}

	public String getDbconfigPath() {
		return dbconfigPath;
	}

	public void setDbconfigPath(String dbconfigPath) {
		this.dbconfigPath = dbconfigPath;
	}

	private int HttpRetry = 3;
	
	private String LoginRoot = "";	
	
	public Config() {
		vBLLList = new Vector<Vector<String>>();
		//vDBConn = new Vector();
		vMailList = new Vector<Vector<String>>();
		vProxyInfo = new Vector<Vector<String>>();
	}

	public String GetPropertyValue(String propertyName) {
		return props.getProperty(propertyName);
	}

	public java.util.Properties GetPropertyList() {
		return props;
	}

//	public Vector GetDrivers() {
//		Vector driversV = new Vector();
//		java.util.Enumeration propNames = props.propertyNames();
//		while (propNames.hasMoreElements()) {
//			String name = (String) propNames.nextElement();
//			if (name.endsWith(".drivers")) {
//				driversV.add(props.getProperty(name));
//			}
//		}
//		return driversV;
//	}
//读取配置文件
	public boolean loadcfg(File file) {
		
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new FileInputStream(file)));
			Document doc = parser.getDocument();

			Node child = doc.getFirstChild();
			int ii = 0;
			while (child != null) {
				ii++;
				if (child.getNodeName().equals("CONFIG")) {
					parseConfig(child);
				}
				child = child.getNextSibling();
				//System.out.println(ii);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return true;
	}

	private void parseConfig(Node node) {
		NodeList nodeList = node.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {

			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();

			if (nodeName.equals("BLL")) {
				this.parseBLLDetail(child.getChildNodes());
			} else if (nodeName.equals("DB")) {
				
			} else if (nodeName.equals("MAIL")) {
				parseMailDetail(child.getChildNodes());
			} else if (nodeName.equals("PROXYS")) {
				parseProxyDetail(child.getChildNodes());
			} else if (nodeName.equals("GetDataBeforMinute")) {
				try {
					GetDataBeforMinute = Integer.parseInt(child.getFirstChild().getNodeValue());
				} catch (Exception e) {
					GetDataBeforMinute = 0;				
				}
			} else if (nodeName.equals("LoginRetry")) {
				try {					
					LoginRetry = Integer.parseInt(child.getFirstChild().getNodeValue());				
				} catch (Exception e) {
					LoginRetry = 0;	
				}
			} else if (nodeName.equals("ValidationCodeTimeout")) {
				try {					
					ValidationCodeTimeout = Integer.parseInt(child.getFirstChild().getNodeValue());				
				} catch (Exception e) {
					ValidationCodeTimeout = 90000;
				}				
			} else if (nodeName.equals("HttpTimeout")) {
				try {					
					HttpTimeout = Integer.parseInt(child.getFirstChild().getNodeValue());				
				} catch (Exception e) {
					HttpTimeout = 90000;	
				}
			} else if (nodeName.equals("dbconfigPath")) {
					dbconfigPath = child.getFirstChild().getNodeValue();				
			}else if (nodeName.equals("databaseName")) {
				DBName= child.getFirstChild().getNodeValue();				
			}else if (nodeName.equals("HttpRetry")) {
				try {					
					HttpRetry = Integer.parseInt(child.getFirstChild().getNodeValue());				
				} catch (Exception e) {
					HttpRetry = 3;	
				}
			} else if (nodeName.equals("LoginRoot")) {
				try {					
					LoginRoot = child.getFirstChild().getNodeValue();				
				} catch (Exception e) {
					LoginRoot = "";	
				}				
			}

		}
	}

	//parse node <BLL>
	private void parseProxyDetail(NodeList nodeList) {
		Vector<String> vBLLItem = new Vector<String>();
		
//	<PROXYS>
//		<PROXYURL></PROXYURL>
//		<PROXYPORT></PROXYPORT>
//		<PROXYUSER></PROXYUSER>
//		<PROXYPWD></PROXYPWD>
//		<USERNAME></USERNAME>
//		<PASSWORD></PASSWORD>
//	</PROXYS>
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("PROXYURL")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("PROXYPORT")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("PROXYUSER")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("PROXYPWD")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("USERNAME")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("PASSWORD")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("ID")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			}	
			
		}
		vProxyInfo.add(vBLLItem);
		vBLLItem=null;
		
	}

	
	private void parseBLLDetail(NodeList nodeList) {
		Vector<String> vBLLItem = new Vector<String>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("BLLNO")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("BLLNAME")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("ISEXTRACTFROMWEB")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("ISEXTRACTFROMLOCAL")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("SAVEFILEPATH")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("RUN")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("ISDELETEFILE")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("BAKFILEPATH")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("RUNYEAR")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("RUNMONTH")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			} else if (node.getNodeName().equals("RUNDAY")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			}
		}
		vBLLList.add(vBLLItem);
	}

	private void parseMailDetail(NodeList nodeList) {
		Vector<String> vBLLItem = new Vector<String>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("RETRIEVEADDR")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			}
			if (node.getNodeName().equals("SENDADDR")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			}
			if (node.getNodeName().equals("SENDPASSWORD")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			}
			if (node.getNodeName().equals("SENDHOSTSMTP")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			}
			if (node.getNodeName().equals("ISSEND")) {
				if (node.getFirstChild() != null) {
					vBLLItem.add(node.getFirstChild().getNodeValue());
				} else {
					vBLLItem.add("");
				}
			}

		}

		vMailList.add(vBLLItem);
	}

	

	public Vector<String> getBLL(int i) {
		return  vBLLList.get(i);
	}

	public Vector<Vector<String>> getBLLList() {
		return vBLLList;
	}

	public Vector<Vector<String>> getMailList() {
		return vMailList;
	}

	public int getNOS() {
		return vBLLList.size();
	}

	public String getOrderType() {
		return sOrderType;
	}

	public Vector<Vector<String>> getProxyInfoList() {
		return vProxyInfo;
	}

	public int getGetDataBeforMinute() {
		return GetDataBeforMinute;
	}

	public void setGetDataBeforMinute(int getDataBeforMinute) {
		GetDataBeforMinute = getDataBeforMinute;
	}

	public int getLoginRetry() {
		return LoginRetry;
	}

	public void setLoginRetry(int loginRetry) {
		LoginRetry = loginRetry;
	}

	public int getValidationCodeTimeout() {
		return ValidationCodeTimeout;
	}

	public void setValidationCodeTimeout(int validationCodeTimeout) {
		ValidationCodeTimeout = validationCodeTimeout;
	}

	public int getHttpTimeout() {
		return HttpTimeout;
	}

	public void setHttpTimeout(int httpTimeout) {
		HttpTimeout = httpTimeout;
	}

	public int getHttpRetry() {
		return HttpRetry;
	}

	public void setHttpRetry(int httpRetry) {
		HttpRetry = httpRetry;
	}

	public String getLoginRoot() {
		return LoginRoot;
	}

	public void setLoginRoot(String loginRoot) {
		LoginRoot = loginRoot;
	}

}