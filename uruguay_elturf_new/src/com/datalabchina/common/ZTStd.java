/*
 * Created on 2004-7-8
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.datalabchina.common;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.connectionpool.*;

/**
 * @author fsx
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ZTStd {
	static Logger logger = Logger.getLogger(ZTStd.class.getName());
	public ZTStd(String dBTypeName) {
		this.DBTypeName = dBTypeName;
	}
	public ZTStd() {
		this.DBTypeName = "ztnet";
	}

	/*
	 * method setConnectionpool() Function: set the connection In parameter:
	 * string poolname out parameter: Null
	 * method getResultBySelect() Function: search In parameter: string
	 * selectstring out parameter: search Result Resultset
	 * method getVectorBySelect() Function: search with SELECT In parameter:
	 * string selectstring out parameter: search Result Vector
	 * method getRecNumbySelect() Function�� SELECT get the record Number�� In
	 * parameter�� String selectstring out parameter��Record Number�� int
	 * method getResultByUpdate() Function�� UPDATE or INSERT to the database in
	 * parameter�� string instring out parameter�� int n_rec
	 * method getColValue() Function��get specil Value from the field in
	 * parameter�� String tablename,String colname,String where out parameter��
	 * String colValue
	 */
	ResultSet pub_rs = null;
	Statement pub_stm = null;
	String DBTypeName = "";
	//	2. return Resultset

	public ResultSet getResultBySelect(String selectstring) throws SQLException {
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		pub_stm = conn.createStatement();
		try {
			//String strSQL = new String(selectstring.getBytes("ISO8859_1"));
			pub_rs = null;
			String strSQL = selectstring;
			pub_rs = pub_stm.executeQuery(strSQL);
			connMgr.freeConnection(this.DBTypeName, conn);
		} catch (Exception fe) {
			logger.error("error:exec sql="+selectstring);
			logger.error(fe.toString());
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getVectorBySelect(String selectstring) throws SQLException {
		Vector vec = new Vector();
		ResultSet rs =null;
		try {
			rs = getResultBySelect(selectstring);
			while(rs.next()){
				vec.add(rs.getString(1));
			}
		} catch (Exception fe) {
			logger.error(fe.toString());
			
		}finally{
			rs.close();
			rs=null;
		}
		return vec;
	}
	
	public Hashtable<String,String> getHashTableBySelect(String selectstring) {
		if(selectstring.indexOf("noInsertDB")>-1)return null;
		Hashtable<String,String> vec = new Hashtable<String,String>();
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		ResultSet rs = null;
		logger.info("get Uraceid by raceid from UraceidMaping");
		try {
			Statement stm = conn.createStatement();
			rs = stm.executeQuery(selectstring);
			while(rs.next()){
				String key=rs.getString(1);
				String value=rs.getString(2);
				logger.info(key+"<=>"+value);
				vec.put(key,value);
			}
			rs.close();
			connMgr.freeConnection(this.DBTypeName, conn);
			stm.close();
		} catch (Exception fe) {
			logger.error(fe.toString());
			connMgr.freeConnection(this.DBTypeName, conn);
			return null;
		}
		return vec;
		
		
	}

	public int getRecNumbySelect(String selectstring) throws SQLException,Exception {
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		ResultSet std_rs = null;
		int recnum = 0;
		String fixedString = selectstring.substring(selectstring.indexOf("from") + 4);
		fixedString = "select count(*) from " + fixedString;
		try {
			Statement stm = conn.createStatement();
//			String strSQL = selectstring;
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
			logger.error(fe.toString());
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

//	public int getIDByName(String tableName, String sNameValue,String sFieldName) {
//		ResultSet rs = null;
//		String sSql = "";
//		int id = 0;
//		try {
//			sNameValue = sNameValue.replaceAll("&nbsp;", "").trim();
//			sNameValue = sNameValue.replaceAll("nbsp;", "").trim();
//			sNameValue = sNameValue.replaceAll("none", "").trim();
//			sNameValue = sNameValue.replaceAll("'", "''").trim();
//			sNameValue = sNameValue.trim();
//			if (sNameValue != null && !sNameValue.equals("")&& sNameValue.length() > 0) {
//				sSql = "Select max(" + getIDNameByTableName(tableName)+ ")  from " + tableName + " where " + sFieldName+ " ='" + sNameValue + "'";
//				rs = getResultBySelect(sSql);
//				if (rs.next())
//					id = rs.getInt(1);
//				if (id == 0 && tableName.equalsIgnoreCase("TC_HORSE")) {
//					sSql = "Select max(TC_HorseID)  from TC_Horse where horseName = '"+ sNameValue + "'";
//					rs = getResultBySelect(sSql);
//					if (rs.next())
//						id = rs.getInt(1);
//					// System.out.println("not find id and sSql="+sSql);
//				}
//				rs.close();
//				rs = null;
//			} else {
//				id = 0;
//			}
//		} catch (SQLException ex) {
//			ex.printStackTrace();
//		}
//		return id;
//	}
//	private String getIDNameByTableName(String sTableName) {
//		String sIDName = "";
//		if (sTableName.toUpperCase().indexOf("CODE") > -1) {// table="Code_"+
//			sIDName = sTableName.substring(sTableName.lastIndexOf("_") + 1)+ "ID";
//		} else if (sTableName.toUpperCase().indexOf("TC_HORSE") > -1) {
//			sIDName = "TC_HorseID";
//		} else {
//			sIDName = sTableName.trim() + "ID";
//		}
//		if (sTableName.equalsIgnoreCase("Code_HorseOrigin")) {
//			sIDName = "OriginID";
//		} 
//		return sIDName;
//	}

	public String getColValue(String tablename, String colname, String where)throws SQLException {
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		ResultSet rs;
		Statement stm = conn.createStatement();
		String colValue = "";
		String strSql = "select " + colname + " from " + tablename + " "+ where;
		try {
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
			logger.error(fe.toString()+":"+strSql);
			connMgr.freeConnection(this.DBTypeName, conn);
			stm.close();
			return "";
		}
		return colValue;
	}
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
	public void closeCursor() {
		try {
			pub_rs.close();
			pub_stm.close();
		} catch (Exception fe) {
			logger.error(fe.toString());
		}
	}
	
	public void execSQL(String sSql) throws SQLException {
		if(sSql==null||sSql.equals(""))return;
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		pub_stm = conn.createStatement();
		try {
			String strSQL = sSql;
			//String strSQL = new String(sSql.getBytes("ISO8859_1"));
			pub_stm.execute(strSQL);
			connMgr.freeConnection(this.DBTypeName, conn);
		} catch (Exception fe) {
			fe.printStackTrace();
			logger.error("sql ERROR:"+fe.toString()+" by"+(sSql.length()>1000?sSql.substring(0,1000)+"...":sSql)+"\n-------------------------------------");
			//logger.error(fe.toString());
			connMgr.freeConnection(this.DBTypeName, conn);
		} finally {
			pub_stm = null;
			conn = null;
			connMgr = null;
		}
	}

	public void execBatchSQL(String[] sqlStmts) throws SQLException {
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
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
	
	public boolean ExecStoredProcedures(String psName, String psParaList) {
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		CallableStatement cs = null;
		String callPsStr = "{call " + psName + "(" + psParaList + ")}";
		boolean isSucc = false;
		try {
			logger.debug("callPsStr=" + callPsStr);
			cs = conn.prepareCall(callPsStr);
			isSucc = cs.execute();
			connMgr.freeConnection(this.DBTypeName, conn);
			isSucc = true;
		} catch (Exception fe) {
			logger.error("ExecStoredProcedures  " + callPsStr, fe);
			connMgr.freeConnection(this.DBTypeName, conn);
			return false;
		}
		return isSucc;
	}	
	public boolean isExistHorse(String webHorseId){
		boolean flag = false;
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		try {
			pub_stm = conn.createStatement();
			//String strSQL = new String(selectstring.getBytes("ISO8859_1"));
			pub_rs = null;
			pub_rs = pub_stm.executeQuery("select * from DEUDB.dbo.GermanRacing_Horse where webHorseId ="+webHorseId);
			flag = pub_rs.next(); 
			connMgr.freeConnection(this.DBTypeName, conn);
		} catch (Exception fe) {
			logger.error(fe.toString());
			connMgr.freeConnection(this.DBTypeName, conn);
		} finally {
			conn = null;
			connMgr = null;
			pub_stm = null;
		}
		return flag;
	}
    public List<String> getHorseCode(String Sql)throws SQLException{
    	List<String>  vec = new ArrayList<String>();
		ResultSet rs =null;
		try {
			rs = getResultBySelect(Sql);
			while(rs.next()){
				vec.add(rs.getString(1));
			}
		} catch (Exception fe) {
			logger.error(fe.toString());
		}finally{
			rs.close();
			rs=null;
		}
		return vec;
    }
	public boolean execStoredProceduresSQL(String sqlStmt) throws SQLException {
		if(sqlStmt==null || sqlStmt.trim().equals(""))return false;
		boolean succ=false;
		DBConnectionManager connMgr = DBConnectionManager.getInstance();
		Connection conn = connMgr.getConnection(this.DBTypeName);
		pub_stm = conn.createStatement();
		try {
			CallableStatement myCallableStatement = conn.prepareCall(sqlStmt);
			succ = myCallableStatement.execute();
			myCallableStatement = null;
		} catch (Exception fe) {
			logger.error(fe.toString()+":"+sqlStmt);
			connMgr.freeConnection(this.DBTypeName, conn);
		} finally {
			connMgr.freeConnection(this.DBTypeName, conn);
			conn = null;
			connMgr = null;
		}
		return succ;
	}

	public String getUraceIDbyRaceID(String raceID) {
		String uraceid=""; 
		try {
			uraceid=getColValue("RaceTab_UraceIDMapping","UraceID","where raceid='"+raceID+"'");
		}catch (Exception e) {
			logger.error(e.getMessage()+":getUraceIDbyRaceID not found uraceid by:"+raceID);
		}
		if(uraceid.trim().equals("")){
			logger.error("getUraceIDbyRaceID not found uraceid by:"+raceID);
		}
		return uraceid.trim();
	}
}