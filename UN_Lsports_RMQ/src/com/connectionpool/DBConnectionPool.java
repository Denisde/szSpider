package com.connectionpool;

import java.sql.DriverManager;

import org.apache.log4j.Logger;

public class DBConnectionPool {
	static Logger logger = Logger.getLogger(DBConnectionPool.class.getName());

	private int checkedOut;

	private java.util.Vector freeConnections = new java.util.Vector();

	private int maxConn;

	private String name;

	private String password;

	private String URL;

	private String user;

	private java.io.PrintWriter log;

	private String logFile;

	/**
	 * DBConnectionPool
	 */
	public DBConnectionPool() {
		super();
	}

	/**
	 * New Connection pool
	 * 
	 * @param name
	 *            Connection pool Name
	 * @param URL
	 *            Connection pool JDBC URL
	 * @param user
	 *            Database user ,or null
	 * @param password
	 *            password,or null
	 * @param maxConn
	 *            max connection
	 */
	public DBConnectionPool(String name, String URL, String user,
			String password, int maxConn) {
		this.name = name;
		this.URL = URL;
		this.user = user;
		this.password = password;
		this.maxConn = maxConn;
	}

	/**
	 * freeConnection
	 * 
	 * @param con
	 *            freeConnection
	 */
	public synchronized void freeConnection(java.sql.Connection con) {

		freeConnections.addElement(con);
		checkedOut--;
		notifyAll();

	}

	/**
	 * try to get the available connectin
	 * 
	 */

	public synchronized java.sql.Connection getConnection() {
		java.sql.Connection con = null;
		// logger.debug("" + freeConnections.size() + " valofcheck: "
		// + checkedOut);
		if (freeConnections.size() > 0) {
			con = (java.sql.Connection) freeConnections.firstElement();
			freeConnections.removeElementAt(0);
			try {
				if (con.isClosed()) {

					con = getConnection();
				}
			} catch (java.sql.SQLException e) {
				logger.error("getConnection wrong", e.fillInStackTrace());
				con = getConnection();
			}
		} else if (maxConn == 0 || checkedOut < maxConn) {
			con = newConnection();
		} else {
			logger.error("Max connection Number " + checkedOut);
		}

		if (con != null) {
			checkedOut++;
		}
		return con;
	}

	/**
	 * get the from the connection pool
	 * 
	 * @param timeout
	 *            timeout
	 */
	public synchronized java.sql.Connection getConnection(long timeout) {
		long startTime = new java.util.Date().getTime();
		java.sql.Connection con;
		while ((con = getConnection()) == null) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
			}
			if ((new java.util.Date().getTime() - startTime) >= timeout) {
				return null;
			}
		}
		return con;
	}

	/**
	 * new connectin
	 */
	private java.sql.Connection newConnection() {
		java.sql.Connection con = null;
		try {
			DriverManager.setLoginTimeout(10);
			if (user == null) {
				con = DriverManager.getConnection(URL);
			} else {
				con = DriverManager.getConnection(URL, user, password);
			}

		} catch (java.sql.SQLException e) {
			logger.warn("Can't creat the connection: " + URL+":"+user+"/"+password+",will use mirroring database. ");
			if(DBConnectionManager.conParamUrlUserPwd_Mirror!=null && DBConnectionManager.conParamUrlUserPwd_Mirror.length>3){
				
				String mirr_Url=DBConnectionManager.conParamUrlUserPwd_Mirror[1];
				String mirr_User=DBConnectionManager.conParamUrlUserPwd_Mirror[2];
				String mirr_Pwd=DBConnectionManager.conParamUrlUserPwd_Mirror[3];
				try {
					con = DriverManager.getConnection(mirr_Url, mirr_User,mirr_Pwd);
				} catch (Exception e2) {
					logger.error("Can't creat the connection: " + URL+":"+user+"/"+password
							+" and mirroring database["+mirr_Url+":"+mirr_User+"/"+mirr_Pwd+"]", e.fillInStackTrace());
					e2.printStackTrace();
					return null;
				}
				
			}else{
				logger.error("Can't creat the connection: " + URL+":"+user+"/"+password+" ", e.fillInStackTrace());
				e.printStackTrace();
				return null;
			}
			
		}
		return con;
	}

	/**
	 * close all the connection
	 */
	public synchronized void release() {
		java.util.Enumeration allConnections = freeConnections.elements();
		while (allConnections.hasMoreElements()) {
			java.sql.Connection con = (java.sql.Connection) allConnections
					.nextElement();
			try {
				con.close();
				String txt = "close the connection" + name + " ";
			} catch (java.sql.SQLException e) {
				logger.error("can not close the connection" + name + " ", e
						.fillInStackTrace());
			}
		}
		freeConnections.removeAllElements();
	}
}