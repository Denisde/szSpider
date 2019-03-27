package com.connectionpool;

import java.io.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lataw.commons.parsers.CommonUtil;
import com.lataw.commons.parsers.DBConfig;

/**
 * Manger the connection pool
 * 
 * 
 * @author��
 */
public class DBConnectionManager {
	static Logger logger = Logger.getLogger(DBConnectionManager.class.getName());
	static private DBConnectionManager instance;
	static private DBConnectionManager instance_Mirror;
	static private int clients;
	private static String[] propertyVal=null;
	public static String[] conParamUrlUserPwd=null;
	public static String[] conParamUrlUserPwd_Mirror=null;
	private Vector<Driver> drivers = new Vector<Driver>();
	private Hashtable<String,DBConnectionPool> pools = new Hashtable<String,DBConnectionPool>();
	Vector<String> driverClasses = new Vector<String>();
	Properties props=new Properties() ;
	private String configPath;
	private String dbName;
	private String poolDefaultName="ztnet";
	
	private String cfgName="dbconfig.xml";
	private String cfgName_Mirror="dbconfig_mirr.xml";
	
	
	/**
	 * 
	 */
	private DBConnectionManager() {
		init();//will call loadDrivers();createPools();
	}
	/**
	 * 
	 */
	private DBConnectionManager(String databaseName,String configFilePath) {
		dbName = databaseName;
		configPath = configFilePath;
		initWithEncrypt();
	}

	private DBConnectionManager(String databaseName,String configFilePath, String poolName) {
		dbName = databaseName;
		poolDefaultName = poolName;
		configPath = configFilePath;
		initWithEncrypt();
	}	

	private void initWithEncrypt() {
		String cfg=configPath+File.separator+cfgName;
		String cfg_mirror=configPath+File.separator+cfgName_Mirror;
		if(configPath==null){
			logger.error("Not found path of dbconfog.xml. ");
			return;
			
		}
	
		boolean bExitsFile=CommonUtil.IsFileExits(cfg);
		
		if(!bExitsFile){
//			poolDefaultName="ztnet";
			logger.warn(cfg+" is not exists, spider will use current file:db.properties for connecting db!");
			try {
				InputStream propStream = new FileInputStream(System.getProperty("user.dir")
						+ System.getProperty("file.separator") + "db.properties");
				props.load(propStream);
				
				java.util.Enumeration propNames = props.propertyNames();
				while (propNames.hasMoreElements()) {
					String name = (String) propNames.nextElement();
					if (name.endsWith(".drivers")) {
						driverClasses.add(props.getProperty(name));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			loadDrivers();
			createPools();
		}else{
			if(dbName==null||dbName.equals("")){
				logger.error("Please use \"DBConnectionManager.getInstance(\"dbname\",[\"path\"or null]);\" to init DBConnectionManager");
				return;
			}
//			if(conParamUrlUserPwd==null || conParamUrlUserPwd.length<4){
				DBConfig oDBConfig=new DBConfig(dbName,cfg);
				HashMap<String,String> dbProperty=oDBConfig.getDBConnectionStr();
				conParamUrlUserPwd=getConParameters(dbProperty);
				dbProperty=null;
//			}
			
			String url ="";
			try {
				
				if(conParamUrlUserPwd==null || conParamUrlUserPwd.length<4)return;
				
				String driverClassName=conParamUrlUserPwd[0];
				driverClasses.clear();
				driverClasses.add(driverClassName);//add driverClasses
				Driver driver = (Driver) Class.forName(driverClassName).newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
				logger.debug("successfull register  " + driverClassName);
				
				int max=1000000;
				DBConnectionPool pool = new DBConnectionPool(poolDefaultName, 
						conParamUrlUserPwd[1], conParamUrlUserPwd[2], conParamUrlUserPwd[3],max);
				pools.put(poolDefaultName, pool);
				logger.debug("successfull register" + poolDefaultName);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Can't register db : " + url, e.fillInStackTrace());
			}
			
//for mirroring database;
			boolean bExitsMirrFile=CommonUtil.IsFileExits(cfg_mirror);
			 
		
			if(bExitsMirrFile && 
(conParamUrlUserPwd_Mirror==null || conParamUrlUserPwd_Mirror.length<4)){
				DBConfig oDBConfig_Mirr=new DBConfig(dbName,cfg_mirror);;
//				HashMap<String,String> 
				dbProperty=oDBConfig_Mirr.getDBConnectionStr();
				conParamUrlUserPwd_Mirror = getConParameters(dbProperty);
				dbProperty=null;
				oDBConfig_Mirr=null;
			}
			
			
		}
		
		
		
	}
	/**
	 * 
	 * @param dbProperty
	 * @return [driverClassName,url,user,password];
	 */
	private String[] getConParameters(HashMap<String, String> dbProperty) {
		String param[]=new String[]{"","","",""};
		String driverClassName=dbProperty.get("drivers");
		String url = dbProperty.get("url");
		String instance = dbProperty.get("instance");
		if (url == null) {
			logger.error(" not specify a Name for the connection pool "+ poolDefaultName + ", spider will not insert DB!!!");
			return null;
		}
		if(instance!=null && !instance.equals("")) url += ";instance="+instance;
		
		String user = dbProperty.get("user");
		String password = dbProperty.get("password");
		param[0]=driverClassName;
		param[1]=url;
		param[2]=user;
		param[3]=password;
		
		return param;
	}
	/**
	 * create pool
	 * 
	 * @param props
	 * 
	 */
	private void createPools() {
		
		
		java.util.Enumeration propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				String url = props.getProperty(poolName + ".url");
				if (url == null) {
					logger.debug(" not specify a Name for the connection pool "+ poolName + " ");
					continue;
				}
				String user = props.getProperty(poolName + ".user");
				// String password= props.getProperty(poolName + ".password");
				String password = props.getProperty(poolName + ".password");
				String maxconn = props.getProperty(poolName + ".maxconn", "0");
				int max;
				try {
					max = Integer.valueOf(maxconn).intValue();
				} catch (NumberFormatException e) {
					logger.debug("Maxmum connection Number : " + maxconn
							+ "   : " + poolName, e.fillInStackTrace());
					max = 0;
				}
				DBConnectionPool pool = new DBConnectionPool(poolName, url,user, password, max);
				pools.put(poolName, pool);
				logger.debug("successfull register" + poolName);
			}
		}
	}

	/**
	 * return null
	 * 
	 * @param name
	 *            Name
	 * @param con
	 *            Connection
	 */
	public void freeConnection(String name, java.sql.Connection con) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			pool.freeConnection(con);
		}
	}
	
	public void freeConnection(java.sql.Connection con) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(poolDefaultName);
		if (pool != null) {
			pool.freeConnection(con);
		}
	}

	/**
	 * get an available Connection
	 * 
	 * @param name
	 * 
	 * @return Connection or null
	 */
	public java.sql.Connection getConnection(String name) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			java.sql.Connection conn = pool.getConnection();
			while (conn == null) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				conn = pool.getConnection();
			}
			return conn;
		}
		return null;
	}
	
	public java.sql.Connection getConnection() {
		return getConnection(poolDefaultName);
	}

	/**
	 * get an available Connection
	 * 
	 * @param name
	 * 
	 * @param time
	 * 
	 * @return Connection
	 */
	public java.sql.Connection getConnection(String name, long time) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(poolDefaultName);
		if (pool != null) {
			return pool.getConnection(time);
		}
		return null;
	}

	/**
	 * create an Instance<p>
	 * Must set the Parameters in config.xml or spider.xml :
	 * <li>&lt;databaseName&gt;***&lt;/databaseName&gt;:the database Name. one of them in [AUDB,FRDB,NARDB,KRADB ...]
	 * <li>&lt;dbconfigPath&gt;...&lt;/dbconfigPath&gt;:the path of the file "dbconfig.xml" in, if it is in current path, you can set it null.
	 *
	 */
	static synchronized public DBConnectionManager getInstance() {
//		if (instance == null) {
//			instance = new DBConnectionManager();
//		}
//		clients++;
//		return instance;
		if(propertyVal==null || propertyVal.length==0) propertyVal=CommonUtil.getPropertyFromCfg();
		if(propertyVal==null || propertyVal.length==0){
			logger.warn("Please add \"<databaseName>database name</databaseName>\" " +
					"and  \"<dbconfigPath>the path of dbconfig.xml</dbconfigPath>\" " +
					"in config.xml or spider.xml. \nSpider will use the current file \"db.properties\". !!");
			//return null;
			instance = new DBConnectionManager();
			clients++;
			return instance;
			
		}
		String dbName=propertyVal[0];
		String cfgPtah=propertyVal.length>1?propertyVal[1]:null;
		return getInstance(dbName,cfgPtah);
	}
	
	/**
	 * create an Instance
	 * @param dbName:the database Name. one of them in [AUDB,FRDB,NARDB,KRADB ...]
	 * @param configPath:the path ,put "dbconfig.xml" in, if it is in current path, you can set it null.
	
	 */
	static synchronized public DBConnectionManager getInstance(String databaseName,String configFilePath) {
		
		if (instance == null) {
			instance = new DBConnectionManager(databaseName,configFilePath);
		}
		clients++;
		return instance;
	}
	
	static synchronized public DBConnectionManager getInstance(String databaseName,String configFilePath, String poolName) {
		
		if (instance_Mirror == null) {
			instance_Mirror = new DBConnectionManager(databaseName,configFilePath, poolName);
		}
		clients++;
		return instance_Mirror;
	}	

	/**
	 * get the connection
	 */
	private void init() {
		try {
			InputStream propStream = new FileInputStream(System.getProperty("user.dir")
					+ System.getProperty("file.separator") + "db.properties");
			props.load(propStream);
			
			Enumeration propNames = props.propertyNames();
			while (propNames.hasMoreElements()) {
				String name = (String) propNames.nextElement();
				if (name.endsWith(".drivers")) {
					driverClasses.add(props.getProperty(name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		loadDrivers();
		createPools();
	}

	/**
	 * install and register the drives
	 * 
	 * @param props
	 * 
	 */

	private void loadDrivers() {
		
		Enumeration<String> st = driverClasses.elements();
		while (st.hasMoreElements()) {
			String driverClassName = (String) st.nextElement();
			logger.debug(driverClassName);
			try {
				java.sql.Driver driver = (java.sql.Driver) Class.forName(driverClassName).newInstance();
				java.sql.DriverManager.registerDriver(driver);
				drivers.addElement(driver);
				logger.debug("successfull register  " + driverClassName);
			} catch (Exception e) {
				logger.error(" can not register : " + driverClassName, e
						.fillInStackTrace());
			}
		}
	}

	/**
	 * close all the connection .
	 */
	public synchronized void release() {

		if (--clients != 0) {
			return;
		}
		Enumeration<DBConnectionPool> allPools = pools.elements();
		while (allPools.hasMoreElements()) {
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}
		Enumeration<Driver> allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
			Driver driver = allDrivers.nextElement();
			try {
				java.sql.DriverManager.deregisterDriver(driver);
				logger.debug("Un regist  connetion "
						+ driver.getClass().getName() + " ");
			} catch (java.sql.SQLException e) {
				logger.error("can not  regist  connetion: "
						+ driver.getClass().getName(), e.fillInStackTrace());
			}
		}
	}
	
	

}