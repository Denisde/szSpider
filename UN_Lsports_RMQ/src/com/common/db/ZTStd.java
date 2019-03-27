/*
 * Created on 2004-7-8
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.common.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.connectionpool.DBConnectionManager;
import com.lataw.commons.parsers.CommonUtil;

/**
 * @author fsx
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ZTStd {

	static Logger logger = Logger.getLogger(ZTStd.class);
	
	String[] propertyVal = null;

	public ZTStd(String dBTypeName) {
		this.DBTypeName = dBTypeName;
	}

	public ZTStd() {
		this.DBTypeName = "ztnet";
	}

	/*
	 * method setConnectionpool() Function: set the connection In parameter:
	 * string poolname out parameter: Null
	 * 
	 * method getResultBySelect() Function: search In parameter: string
	 * selectstring out parameter: search Result Resultset
	 * 
	 * method getVectorBySelect() Function: search with SELECT In parameter:
	 * string selectstring out parameter: search Result Vector
	 * 
	 * 
	 * method getRecNumbySelect() Function�� SELECT get the record Number�� In
	 * parameter�� String selectstring out parameter��Record Number�� int
	 * 
	 * method getResultByUpdate() Function�� UPDATE or INSERT to the database in
	 * parameter�� string instring out parameter�� int n_rec
	 * 
	 * method getColValue() Function��get specil Value from the field in
	 * parameter�� String tablename,String colname,String where out parameter��
	 * String colValue
	 *  
	 */

	ResultSet pub_rs = null;

	Statement pub_stm = null;

	String DBTypeName = "";

	//	2. return Resultset

	public ResultSet getResultBySelect(String selectstring) throws SQLException {
		Connection conn = null;
		DBConnectionManager connMgr = null;
		connMgr = DBConnectionManager.getInstance();
		conn = connMgr.getConnection(this.DBTypeName);
		pub_stm = conn.createStatement();

		try {
			//String strSQL = new String(selectstring.getBytes("ISO8859_1"));
			pub_rs = null;
			String strSQL = selectstring;
			pub_rs = pub_stm.executeQuery(strSQL);
			connMgr.freeConnection(this.DBTypeName, conn);
		} catch (Exception fe) {
			logger.error("error:exec sql="+selectstring, fe);
			connMgr.freeConnection(this.DBTypeName, conn);
			return null;
		} finally {
			conn = null;
			connMgr = null;
			pub_stm = null;

		}
		return pub_rs;
	}

	//	3.search with select �� search Result type vector

//	public Vector getVectorBySelect(String selectstring) throws SQLException {
//		Vector vec = new Vector();
//		ResultSet rs =null;
//		try {
//			rs = getResultBySelect(selectstring);
//			while(rs.next()){
//				vec.add(rs.getString(1));
//			}
//		} catch (Exception fe) {
//			logger.error(fe.toString());
//			
//		}finally{
//			rs.close();
//			rs=null;
//		}
//		return vec;
//	}

	public int getRecNumbySelect(String selectstring) {
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		ResultSet std_rs = null;
		int recnum = 0;
		
		String fixedString = selectstring.substring(selectstring
				.indexOf("from") + 4);
		fixedString = "select count(*) from " + fixedString;
		try {
			Statement stm = conn.createStatement();
			std_rs = stm.executeQuery(fixedString);
			connMgr.freeConnection(this.DBTypeName, conn);
			if (std_rs.next()) {
				String temp = std_rs.getString(1);
				recnum = Integer.parseInt(temp);
			} else {
				recnum = 0;
			}
			std_rs.close();
			stm.close();
		} catch (Exception fe) {
			logger.error(fe.toString()+":"+fixedString);
			connMgr.freeConnection(this.DBTypeName, conn);
			return 0;
		}
		return recnum;
}
	public boolean bIsExist(String tableName,String wherestring){
		boolean bOut=false;
		try {
			int i=getRecNumbySelect(" from "+tableName+" "+wherestring);
			if(i>0)bOut=true;
		} catch (Exception e) {
			logger.error("error: from "+tableName+" "+wherestring+"\n"+e.getMessage());
			e.printStackTrace();
			bOut=false;
		}

		return bOut;
	}

	public String getColValue(String tablename, String colname, String where)
	throws SQLException {
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		ResultSet rs;
		Statement stm = conn.createStatement();
		String colValue = "";
		String strSql = "select " + colname + " from " + tablename + " "
				+ where;
		try {
			stm = conn.createStatement();
			String strSQL = strSql;
			//strSql = new String(strSql.getBytes("ISO8859_1"));
			rs = stm.executeQuery(strSql);
			connMgr.freeConnection(this.DBTypeName, conn);
			if (rs.next()) {
				colValue = (String) rs.getString(1);
				if (colValue == null) {
					colValue = "";
				}
			} else {
				colValue = "";
			}
			stm.close();
		} catch (Exception fe) {
			logger.error(fe.toString());
			connMgr.freeConnection(this.DBTypeName, conn);
			stm.close();
			return "";
		}
		return colValue;
}
	public String getIDSql(String strSql){
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		
		String colValue = "";
		try {
			ResultSet rs;
			
			Statement stm = conn.createStatement();
			
		
			stm = conn.createStatement();
			rs = stm.executeQuery(strSql);
			connMgr.freeConnection(this.DBTypeName, conn);
			if (rs.next()) {
				colValue = (String) rs.getString(1);
				if (colValue == null) {
					colValue = "";
				}
			} else {
				colValue = "";
			}
			stm.close();
		} catch (Exception fe) {
			logger.error(fe.toString());
			
			colValue= "";
		}finally{
			connMgr.freeConnection(this.DBTypeName, conn);
				
		}

			return colValue;
	}
	public String getHorseIDSql(String strSql,String sHorseName){
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		
		String colValue = "";
		try {
			ResultSet rs;
			
			Statement stm = conn.createStatement();
			
		
			stm = conn.createStatement();
			rs = stm.executeQuery(strSql);
			connMgr.freeConnection(this.DBTypeName, conn);
			if (rs.next()) {
				colValue = (String) rs.getString(1);
				if (colValue == null) {
					colValue = "";
				}
			} else {
				colValue = "";
			}
			stm.close();
		} catch (Exception fe) {
			logger.error(fe.toString());
			
			colValue= "";
		}finally{
			connMgr.freeConnection(this.DBTypeName, conn);
				
		}

			return colValue;
	}
	public String[] getIDArrayBySql(String strSql){
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		
		String colValue[] = {"",""};
		try {
			ResultSet rs;
			
			Statement stm = conn.createStatement();
			
		
			stm = conn.createStatement();
			rs = stm.executeQuery(strSql);
			connMgr.freeConnection(this.DBTypeName, conn);
			if (rs.next()) {
				colValue[0] = (String) rs.getString(1);
				colValue[1] = (String) rs.getString(2);
				
				
			} 
			stm.close();
		} catch (Exception fe) {
			logger.error(fe.toString());
			
			
		}finally{
			connMgr.freeConnection(this.DBTypeName, conn);
				
		}

			return colValue;
	}

	//5.UPDATE,INSERT table

public int getResultByUpdate(String instring) throws SQLException {
	int rs = 0;
	DBConnectionManager connMgr = DBConnectionManager.getInstance();
	Connection conn = connMgr.getConnection(this.DBTypeName);
	try {
		Statement stm = conn.createStatement();
		//instring = new String(instring.getBytes("ISO8859_1"));
		rs = stm.executeUpdate(instring);
		conn.commit();
		stm.close();
		connMgr.freeConnection(this.DBTypeName, conn);
	} catch (Exception fe) {
		logger.error(fe.toString());
		conn.rollback();
		connMgr.freeConnection(this.DBTypeName, conn);
	}
	return rs;
}

	

//	public boolean ExecStoredProcedures(String psName, String psParaList) {
//		DBConnectionManager connMgr = DBConnectionManager.getInstance();
//		Connection conn = connMgr.getConnection(this.DBTypeName);
//		CallableStatement cs = null;
//		String callPsStr = "{call " + psName + "(" + psParaList + ")}";
//		boolean isSucc = false;
//		try {
////			logger.debug("callPsStr=" + callPsStr);
//			cs = conn.prepareCall(callPsStr);
//			isSucc = cs.execute();
//			connMgr.freeConnection(this.DBTypeName, conn);
//			isSucc = true;
//		} catch (Exception fe) {
//			if (fe.toString().indexOf("duplicate key") >= 0) {
//				return true;
//			}			
//			logger.error("ExecStoredProcedures  " + callPsStr, fe);
//			connMgr.freeConnection(this.DBTypeName, conn);
//			return false;
//		}
//		return isSucc;
//	}
	

public boolean ExecStoredProcedures(String psName, String psParaList) {
	try {
		ExecStoredProcedures_mirr(psName, psParaList);
	} catch (Exception e) {
	}
	
	DBConnectionManager connMgr = DBConnectionManager.getInstance();
	Connection conn = connMgr.getConnection(this.DBTypeName);
	CallableStatement cs = null;
	String callPsStr = "{call " + psName + "(" + psParaList + ")}";
	boolean isSucc = false;
	try {
//		logger.debug("callPsStr=" + callPsStr);
//		System.out.println("ExecStoredProcedures " + conn.getMetaData().getURL());
		cs = conn.prepareCall(callPsStr);
		isSucc = cs.execute();
		connMgr.freeConnection(this.DBTypeName, conn);
		isSucc = true;
	} catch (Exception fe) {
		if (fe.toString().indexOf("duplicate key") >= 0) {
			return true;
		}
		logger.error("ExecStoredProcedures  " + callPsStr, fe);
		connMgr.freeConnection(this.DBTypeName, conn);
		return false;
	}
	return isSucc;
}

//for mirr SP
public boolean ExecStoredProcedures_mirr(String psName, String psParaList) {
	if(propertyVal==null) {
		propertyVal = getPropertyFromCfg_mirr();
	}

	if(propertyVal==null || propertyVal.length==0){
//		logger.warn("Please add \"<databaseName_mirr>database name</databaseName_mirr>\" " +
//				"and  \"<dbconfigPath_mirr>the path of dbconfig.xml</dbconfigPath_mirr>\" " +
//				"in config.xml or spider.xml. \nSpider will use the current file \"db.properties\". !!");
		return false;
	}
	
	String dbName=propertyVal[0];
	String cfgPtah=propertyVal.length>1?propertyVal[1]:null;
	
	DBConnectionManager connMgr = DBConnectionManager.getInstance(dbName,cfgPtah, dbName);
	Connection conn = connMgr.getConnection(dbName);
	CallableStatement cs = null;
	String callPsStr = "{call " + psName + "(" + psParaList + ")}";
	System.out.println(callPsStr);
	boolean isSucc = false;
	try {
		System.out.println("ExecStoredProcedures_mirr " + conn.getMetaData().getURL());
//		logger.debug("callPsStr=" + callPsStr);
		cs = conn.prepareCall(callPsStr);
		isSucc = cs.execute();
		connMgr.freeConnection(dbName, conn);
		isSucc = true;
	} catch (Exception fe) {
		if (fe.toString().indexOf("duplicate key") >= 0) {
			return true;
		}
		logger.error("ExecStoredProcedures  " + callPsStr, fe);
		connMgr.freeConnection(dbName, conn);
		return false;
	}
	return isSucc;
}

public static String[] getPropertyFromCfg_mirr() {
	
	try {
		
		boolean bCfg=CommonUtil.IsFileExits("config.xml");
		boolean bSpdr=CommonUtil.IsFileExits("spider.xml");
		
		String xmlContent="";
		if(bCfg){
			xmlContent=CommonUtil.readFileToStr("config.xml");
		}else if (bSpdr){
			xmlContent=CommonUtil.readFileToStr("spider.xml");
		}else{
			logger.warn("Not found \"config.xml\" and \"spider.xml\", spider will not search database info, and will not insert data into db!!");
			return (new String[]{});
		}
		String dbName=CommonUtil.getValueByPatter(xmlContent,"<databaseName_mirr>(.*?)</databaseName_mirr>");
		if(dbName==null||dbName.equals("")){
			//logger.warn("Not found database name in \"config.xml\" or \"spider.xml\", \nplease add \"<databaseName>database name</databaseName>\" and  \"<dbconfigPath>the path of dbconfig.xml</dbconfigPath>\" in config.xml or spider.xml. \nSpider will not search database info, and will not insert data into db!!");
			return (new String[]{});
		}
		String cfgPath=CommonUtil.getValueByPatter(xmlContent,"<dbconfigPath_mirr>(.*?)</dbconfigPath_mirr>");
		
		return (new String[]{dbName,cfgPath});
	
	} catch (Exception e) {
		logger.error("", e);
		return (new String[]{});
		//return null;
	}
	
}	
//for mirr SP
	

//	public void ExecSelectStoredProcedures(String psName, String psParaList) {
//		ResultSet rs = null;
//		DBConnectionManager connMgr = DBConnectionManager.getInstance();
//		Connection conn = connMgr.getConnection(this.DBTypeName);
//		CallableStatement cs = null;
//		String callPsStr = "{call " + psName + "(" + psParaList + ")}";
//		try {
//			cs = conn.prepareCall(callPsStr);
//			cs.execute();
//			cs.close();
//			connMgr.freeConnection(this.DBTypeName, conn);
//		} catch (Exception fe) {
//			fe.printStackTrace();
//			logger.error(fe.toString());
//			logger.debug(callPsStr);
//			connMgr.freeConnection(this.DBTypeName, conn);
//			
//		}
//		
//
//	}

	
	public void closeCursor() {
		try {
			pub_rs.close();
			pub_stm.close();
		} catch (Exception fe) {
			logger.error(fe.toString());
		}
	}

	
	public void execSQL(String sSql) throws Exception  {
		Connection conn = null;
		DBConnectionManager connMgr = null;
		connMgr = DBConnectionManager.getInstance();
		conn = connMgr.getConnection(this.DBTypeName);
		try {
			pub_stm = conn.createStatement();
			String strSQL = sSql;
			//String strSQL = new String(sSql.getBytes("ISO8859_1"));
			pub_stm.execute(strSQL);
		} finally {
			connMgr.freeConnection(this.DBTypeName, conn);
			pub_stm = null;
			conn = null;
			connMgr = null;

		}
	}

	public void execBatchSQL(String[] sqlStmts) throws SQLException {
		Connection conn = null;
		DBConnectionManager connMgr = null;
		connMgr = DBConnectionManager.getInstance();
		conn = connMgr.getConnection(this.DBTypeName);
		pub_stm = conn.createStatement();
		try {
			for (int i = 0; i < sqlStmts.length; i++) {
				String strSQL = sqlStmts[i];
				//String strSQL = new String(sqlStmts[i].getBytes("ISO8859_1"));
				pub_stm.execute(strSQL);
				connMgr.freeConnection(this.DBTypeName, conn);
			}

		} catch (Exception fe) {
			logger.error(fe.toString());
			connMgr.freeConnection(this.DBTypeName, conn);
		} finally {
			pub_stm = null;
			conn = null;
			connMgr = null;

		}
	}

	public boolean execStoredProceduresSQL(String sqlStmt) throws SQLException {
		boolean succ = false;
		Connection conn = null;
		DBConnectionManager connMgr = null;
		connMgr = DBConnectionManager.getInstance();
		conn = connMgr.getConnection(this.DBTypeName);
		pub_stm = conn.createStatement();
		try {
			CallableStatement myCallableStatement = conn.prepareCall(sqlStmt);
			succ = myCallableStatement.execute();
			myCallableStatement = null;
		} catch (Exception fe) {
			logger.error(fe.toString());
			connMgr.freeConnection(this.DBTypeName, conn);
		} finally {
			connMgr.freeConnection(this.DBTypeName, conn);
			conn = null;
			connMgr = null;

		}
		return succ;
	}

	public void execBatchStoredProceduresSQL(String[] sqlStmts)
			throws SQLException {
		Connection conn = null;
		DBConnectionManager connMgr = null;
		connMgr = DBConnectionManager.getInstance();
		conn = connMgr.getConnection(this.DBTypeName);
		//pub_stm = conn.createStatement();

		try {
			for (int i = 0; i < sqlStmts.length; i++) {
				//System.out.println(sqlStmts[i]);
				CallableStatement myCallableStatement = conn
						.prepareCall(sqlStmts[i]);
				myCallableStatement.execute();
				myCallableStatement = null;
			}
		} catch (Exception fe) {
			//fe.printStackTrace();
			logger.error(fe.toString());
			connMgr.freeConnection(this.DBTypeName, conn);

		} finally {
			connMgr.freeConnection(this.DBTypeName, conn);
			conn = null;
			connMgr = null;

		}

	}

}