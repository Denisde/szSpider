package com.datalabchina.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author wgb
 */
public class CommonDB {
	ZTStd myztstd;

	public String getRaceDateByRaceID(String raceID) {
		  String sEnName = "";
//		  String sSql = "select raceDate from prerace_race  where raceid="+raceID;
		  String sSql = "select raceDate from Kcycle_PreRace_Race  where raceid="+raceID;
		  ResultSet rs = null;
		  try {
		   myztstd = new ZTStd();
		   rs = myztstd.getResultBySelect(sSql);
		   if (rs.next()) {
		    sEnName = rs.getString(1);
		   }
		   int i=0;
		   while(sEnName.equals("")&& i<2){
		    if(i==0)sSql = "select raceDate from Kcycle_PreRace_Race  where raceid="+raceID;
		    else sSql = "select raceDate from Kcycle_postrace_race  where raceid="+raceID;
		    myztstd = new ZTStd();
		    rs = myztstd.getResultBySelect(sSql);
		    if (rs.next()) {
		     sEnName = rs.getString(1);
		    }
		    i++;
		   }
		   rs.close();
		   rs = null;
		  } catch (Exception e) {
		   sEnName="";
		  }
		  if(sEnName.length()>19)sEnName=sEnName.substring(0,19);

		  if(sEnName.equals("")||sEnName.equalsIgnoreCase("null")){
		   sEnName=raceID.substring(0,4)+"-"+raceID.substring(4,6)+"-"+raceID.substring(6)+" "+"00:00.0";
		  }
		  return sEnName;
		 }
	
	public int getIDByHorseBrand(String sHorseName, String sBrandNo) {
		ResultSet rs = null;
		String sSql = "";
		int id = 0;

		try {
			myztstd = new ZTStd();
			sSql = "Select horseid  from Horse_Import_Brandno where horsename='"
					+ sHorseName + "'";
			rs = myztstd.getResultBySelect(sSql);
			if (rs.next())
				id = rs.getInt(1);
			if (id == 0) {
				sSql = "Select horseid  from Horse_Import_Brandno where brandno='"
						+ sBrandNo + "'";
				rs = myztstd.getResultBySelect(sSql);
				if (rs.next())
					id = rs.getInt(1);
				// System.out.println("not find id and sSql="+sSql);
			}

			rs.close();
			rs = null;

			if (id == 0) {
				id = getNewID("horseid", "Horse_Import_BrandNo");
				execSQL("insert into Horse_Import_Brandno(horseid,horsename,brandno)values("
						+ id + ",'" + sHorseName + "','" + sBrandNo + "')");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return id;

	}
//
//	public List<String> getMeetingCodeSql() {
//		ResultSet rs = null;
//		String sSql = "";
//		List<String> id = new ArrayList<String>();
//
//		try {
//			myztstd = new ZTStd();
//			sSql = " select  MeetingCode ,RIGHT(a.RaceID,2) as raceno "
//					+ " from sgdb..TC_V2_PostRace_Race  a left join TC_V2_PostRace_Horse b "
//					+ "on a.RaceID =b.raceid "
//					+ "where b.raceid is null "
//					+ "order by 1 desc";
//			System.out.println(" sSql = " + sSql);
//			rs = myztstd.getResultBySelect(sSql);
//			while (rs.next()) {
//				String a=rs.getString(1);
//				String b=rs.getString(2);
//				String c=a+b;
//				id.add(c);
//			}
//			rs.close();
//			rs = null;
//
//		} catch (SQLException ex) {
//			ex.printStackTrace();
//		}
//
//		return id;
//	}
	public List<String> getfixRaceDate(String sql) {
		ResultSet rs = null;
		String sSql = "";
		List<String> id = new ArrayList<String>();
		try {
			myztstd = new ZTStd();
			sSql=sql;
			rs = myztstd.getResultBySelect(sSql);
			while (rs.next()) {
//				id.add(rs.getString(1)+"_"+rs.getString(2));
				id.add(rs.getString(1));
			}
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}
	
	
	public List<String> getRaceId() {
		ResultSet rs = null;
		String sSql = "";
		List<String> id = new ArrayList<String>();
		try {
			myztstd = new ZTStd();
			sSql="select  top 1000 RaceId,distance/LeaderFinishTime ,distance ,LeaderFinishTime ,  *  from   UruguayDB..elturf_postrace_race " +
					" where ISNUMERIC(LeaderFinishTime) =1 and ISNUMERIC(distance) =1 " +
					"and LeaderFinishTime >0 and (distance/LeaderFinishTime not between 10 and 20) " +
					"order by 4 desc";
			System.out.println(" sSql = " + sSql);
			rs = myztstd.getResultBySelect(sSql);
			while (rs.next()) {
				id.add(rs.getString(1));
			}
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}
	
	public List<String> getWebHorseId() {
		ResultSet rs = null;
		String sSql = "";
		List<String> id = new ArrayList<String>();
		try {
			myztstd = new ZTStd();
			sSql=" SELECT TOP 100 A.webhorseid " + 
					"FROM uruguaydb..elturf_prerace_horse A \r\n" + 
					"LEFT JOIN uruguaydb..elturf_horse B ON A.webhorseid=B.webhorseid \r\n" + 
					"WHERE B.webhorseid IS NULL \r\n" + 
					"ORDER BY A.racedate desc";
			rs = myztstd.getResultBySelect(sSql);
			while (rs.next()) {
				id.add(rs.getString(1));
			}
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}
	public List<String> getfixRaceID() {
		ResultSet rs = null;
		String sSql = "";
		List<String> id = new ArrayList<String>();
		try {
			myztstd = new ZTStd();
			sSql="select por.RaceID from GermanRacing_PostRace_Race por left join GermanRacing_PreRace_Race prr on prr.RaceID=por.RaceID where prr.RaceID is null ";
			rs = myztstd.getResultBySelect(sSql);
			while (rs.next()) {
				id.add(rs.getString(1));
			}
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}
	public List<String> getRaceID() {
		ResultSet rs = null;
		String sSql = "";
		List<String> id = new ArrayList<String>();
		try {
			myztstd = new ZTStd();
//			sSql="select meetingcode from sgdb..TC_V2_PostRace_Race   where  raceid='201301123200302'";
//			sSql="select top 1000 prr.* from ( select * from DEUDB..PreRace_Race where RaceTypeID=1 and RaceDate<getdate()-2) prr left join (select * from DEUDB.PostRace_Race where RaceTypeID=1 ) por on por.URaceID=prr.URaceIDwhere por.URaceID is nullorder by prr.URaceID desc ";
//			sSql="select top 1000 prr.* from (select * from PreRace_Race where RaceTypeID=1 and RaceDate<getdate()-2) prr left join ( select * from PostRace_Race where RaceTypeID=1) por on por.URaceID=prr.URaceID where por.URaceID is null";
//			sSql="select RaceID from GermanRacing_PreRace_Race where  RaceCategory is null and CountryCode =5 order by LastUpdateTime";
			sSql="select top 100 A.Surface ,B.Surface,A.RaceCategory,B.RaceCategory ,*  from GermanRacing_PreRace_Race  A inner join GermanRacing_PostRace_Race B ON A.RaceID =B.RaceID  where isnull(A.RaceCategory,'') <>isnull(B.RaceCategory ,'')";
//			sSql="select top 15* from GermanRacing_PreRace_Race where Distance<1000 order by LastUpdateTime desc ";
			rs = myztstd.getResultBySelect(sSql);
			while (rs.next()) {
				id.add(rs.getString(5));
			}
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}
	
	public String getOneHorseId(String HouseID) {
		ResultSet rs = null;
		String sSql = "";
		String id ="";

		try {
			myztstd = new ZTStd();
			sSql = "select  TC_HorseID from TC_V2_Horse where TC_HorseID="+HouseID;
			rs = myztstd.getResultBySelect(sSql);
			if (rs != null && rs.next()) {
				id = rs.getString(1);
			}
			rs.close();
			rs = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return id;
	}
	public String getOneJockeyId(String JockeyName) {
		ResultSet rs = null;
		String sSql = "";
		String id ="";

		try {
			myztstd = new ZTStd();
			sSql = "select  TC_JockeyID from TC_V2_Jockey where JockeyName='"+JockeyName+"'";
			rs = myztstd.getResultBySelect(sSql);
			if (rs != null && rs.next()) {
				id = rs.getString(1)+"";
			}
			rs.close();
			rs = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}
	public String getOneTrainerId(String TrainerName) {
		ResultSet rs = null;
		String sSql = "";
		String id ="";
		try {
			myztstd = new ZTStd();
			sSql = "select  TC_TrainerID from TC_V2_Trainer where TrainerName='"+TrainerName+"'";
			rs = myztstd.getResultBySelect(sSql);
			if (rs != null && rs.next()) {
				id = rs.getString(1)+"";
			}
			rs.close();
			rs = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}
	
		
	public List<String> getHorseId() {
		ResultSet rs = null;
		String sSql = "";
		List<String> id = new ArrayList<String>();

		try {
			myztstd = new ZTStd();
			sSql = "select  TC_HorseID from TC_V2_Horse where Birthday is null";
//			sSql = "select  TC_HorseID from TC_V2_Horse";
			rs = myztstd.getResultBySelect(sSql);
			while (rs.next()) {
				id.add(rs.getString(1));
			}
			rs.close();
			rs = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return id;
	}

	// jockey
	public List<String> getJockeyId() {
		ResultSet rs = null;
		String sSql = "";
		List<String> id = new ArrayList<String>();

		try {
			myztstd = new ZTStd();
//			sSql="select*from TC_V2_Jockey where JockeyID in(select TC_JockeyID from TC_V2_PreRace_Horse where ExtractTime>GETDATE()-7)and Nationality is null";
			sSql = "select TC_JockeyID  from TC_V2_Jockey where YearOfBorn is null";
			rs = myztstd.getResultBySelect(sSql);
			while (rs.next()) {
				id.add(rs.getString(1));
			}
			rs.close();
			rs = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}

	public void updJockey(String a, String b, int id) {
		String sSql = "";
		try {
			myztstd = new ZTStd();
			if (a != null && b != null && !"".equals(a) && !"".equals(b)) {
				sSql = "update TC_V2_Jockey set Nationality='" + a
						+ "',YearOfBorn='" + b + "' where TC_JockeyID=" + id;
				myztstd.execSQL(sSql);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}
	

	public String getJockeyName(int JockeyId) {
		ResultSet rs = null;
		String sSql = "";
		String id = "";
		try {
			myztstd = new ZTStd();
			sSql = "select JockeyName  from TC_V2_Jockey where TC_JockeyID="
					+ JockeyId;
//			System.out.println(sSql);
			rs = myztstd.getResultBySelect(sSql);
			if (rs != null && rs.next()) {
				id = rs.getString(1);
			}
			rs.close();
			rs = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return id;
	}

	public int getJockeyApp(int JockeyId) {
		ResultSet rs = null;
		String sSql = "";
		int appid = 0;
		try {
			myztstd = new ZTStd();
			sSql = "select IsApprentice  from TC_V2_Jockey where TC_JockeyID="
					+ JockeyId;
//			System.out.println(sSql);
			rs = myztstd.getResultBySelect(sSql);
			System.out.println();
			if (rs != null && rs.next()) {
				appid = rs.getInt(1);
			}
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return appid;
	}

	// Trainer
	public List<String> getTrainerId() {
		ResultSet rs = null;
		String sSql = "";
		List<String> id = new ArrayList<String>();

		try {
			myztstd = new ZTStd();
//			sSql="select*from TC_V2_Trainer where TrainerID in(select TC_TrainerID from TC_V2_PreRace_Horse where ExtractTime>GETDATE()-7)and Nationality is null";
			sSql = "select  TC_TrainerID from TC_V2_Trainer where YearOfBorn is null";
			rs = myztstd.getResultBySelect(sSql);
			while (rs.next()) {
				id.add(rs.getString(1));
			}
			rs.close();
			rs = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return id;
	}

	public String getTrainerName(int TrainerId) {
		ResultSet rs = null;
		String sSql = "";
		String id = "";

		try {
			myztstd = new ZTStd();
			sSql = "select  TrainerName from TC_V2_Trainer  where TC_TrainerID="
					+ TrainerId;
			rs = myztstd.getResultBySelect(sSql);
			if (rs != null && rs.next()) {
				id = rs.getString(1);
			}
			rs.close();
			rs = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return id;
	}

	public void updTrainer(String a, String yearOfBorn, int id) {
		String sSql = "";
		try {
			myztstd = new ZTStd();
			if (a != null && yearOfBorn != null && !"".equals(a)
					&& !"".equals(yearOfBorn)) {
				sSql = "update TC_V2_Trainer set Nationality='" + a
						+ "',YearOfBorn='" + yearOfBorn
						+ "' where TC_TrainerID=" + id;
				myztstd.execSQL(sSql);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public int getNewID(String keyID, String tableName) {

		ResultSet rs = null;
		String sSQLMax = "";
		int maxID = 1;

		try {
			myztstd = new ZTStd();
			sSQLMax = "Select max(" + keyID + ") as ID from " + tableName;
			rs = myztstd.getResultBySelect(sSQLMax);
			if (rs.next())
				maxID = rs.getInt(1) + 1;
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return maxID;
	}

	/*
	 * @iIDType 1: horseid 2: Jockeyid 3: trainerid
	 * 
	 * @ID if (iIDType==1) ID=clothno else ID=horseid
	 */
	public int getIDByURIDClothNo(String sURID, String ID, int iIDType) {
		ResultSet rs = null;
		String sSQLMax = "";
		int maxID = 0;

		try {
			myztstd = new ZTStd();
			if (iIDType == 1) {
				sSQLMax = "select horseid from to_prerace_race tpr inner join to_prerace_horse tph on tpr.raceid=tph.raceid where tpr.URID="
						+ sURID + " and tph.clothno=" + ID + "";
			} else if (iIDType == 2) {
				sSQLMax = "select Jockeyid from to_prerace_race tpr inner join to_prerace_horse tph on tpr.raceid=tph.raceid where tpr.URID="
						+ sURID + " and tph.horseid=" + ID + "";
			} else if (iIDType == 3) {
				sSQLMax = "select trainerid from to_prerace_race tpr inner join to_prerace_horse tph on tpr.raceid=tph.raceid where tpr.URID="
						+ sURID + " and tph.horseid=" + ID + "";
			}
			rs = myztstd.getResultBySelect(sSQLMax);
			if (rs != null && rs.next()) {
				maxID = rs.getInt(1);
				rs.close();

			}
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		if (maxID == 0) {
			System.out.println("not found id by :" + sSQLMax);
		}

		return maxID;

	}

	/*
	 * 
	 * @iIDType 1: horseid 2: Jockeyid 3: trainerid 4:Owner iID
	 * if(iIDType==1)iHorseID with clothno else horseid
	 */
	public int getIDByURIDHorseIDFromPreRace(String sURID, int iID, int iIDType) {
		ResultSet rs = null;
		String sSQLMax = "";
		int maxID = 0;

		try {
			myztstd = new ZTStd();
			if (iIDType == 1) {
				sSQLMax = "select horseid from tc_prerace_horse where raceid="
						+ sURID + " and clothno=" + iID + "";
			} else if (iIDType == 2) {
				sSQLMax = "select Jockeyid from tc_prerace_horse where raceid="
						+ sURID + " and horseid=" + iID + "";
			} else if (iIDType == 3) {
				sSQLMax = "select TrainerID from tc_prerace_horse where raceid="
						+ sURID + " and horseid=" + iID + "";
			} else if (iIDType == 4) {
				sSQLMax = "select OwnerID from tc_prerace_horse where raceid="
						+ sURID + " and horseid=" + iID + "";
			} else if (iIDType == 5) {
				sSQLMax = "select GearID from tc_prerace_horse where raceid="
						+ sURID + " and horseid=" + iID + "";
			} else
				return 0;

			rs = myztstd.getResultBySelect(sSQLMax);
			if (rs != null && rs.next()) {
				maxID = rs.getInt(1);
				rs.close();
			}
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		if (maxID == 0) {
			System.out.println("no found values:\n" + sSQLMax);
		}

		return maxID;

	}

	public void insertToDB(String[] sSqlArray) {
		try {
			myztstd = new ZTStd();
			System.out.println("exec batch insert object info into db  !! ");
			for (int i = 0; i < sSqlArray.length; i++) {
				String sSql = sSqlArray[i];
				// System.out.println("insertsql="+sSql);
				myztstd.execSQL(sSql);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void insertToDB(String sSql) {
		try {
			myztstd = new ZTStd();
			// System.out.println("exec batch insert object info into db !! ");
			myztstd.execSQL(sSql);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}


	public boolean bIfExistById(String sTableName, String sWhere) {
		ResultSet rs = null;
		boolean ifExistId = false;
		String sSql = "select * from " + sTableName + " where " + sWhere;
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			if (rs.next()) {
				ifExistId = true;
			} else {
				ifExistId = false;
			}
			rs.close();
			rs = null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return ifExistId;

	}
//判断某一列是在某个表中是否有数据
	public boolean bIfExistById(String sTableName, String sColName,long sColValue) {
		ResultSet rs = null;
		boolean ifExistId = false;
		String sSql = "select * from " + sTableName + " where " + sColName+ " =" + sColValue + "";
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			if (rs.next()) ifExistId = true;
			else ifExistId = false;
			rs.close();
			rs = null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return ifExistId;
	}

	public boolean bIfExistById(String sTableName, String sColName,
			String sColValue) {
		ResultSet rs = null;
		boolean ifExistId = false;
		String sSql = "select * from " + sTableName + " where " + sColName
				+ " ='" + sColValue + "'";
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			if (rs.next()) {
				ifExistId = true;
			} else {
				ifExistId = false;
			}
			rs.close();
			rs = null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return ifExistId;
	}

	public boolean bIfExistByHorseIdRaceId(String sTableName, long sRaceID,
			int iHorseID) {
		ResultSet rs = null;
		boolean ifExistId = false;
		String sSql = "select * from " + sTableName + " where RaceID ="
				+ sRaceID + " and horseid=" + iHorseID;
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			if (rs.next()) {
				ifExistId = true;
			} else {
				ifExistId = false;
			}
			rs.close();
			rs = null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return ifExistId;
	}

	public boolean bIfExistByHorseIdRecordDate(String sTableName, int iHorseID,
			String sRecordDate) {
		ResultSet rs = null;
		boolean ifExistId = false;
		String sSql = "select * from " + sTableName + " where horseid="
				+ iHorseID + " and RecordDate ='" + sRecordDate + "'";
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			if (rs.next()) {
				ifExistId = true;
			} else {
				ifExistId = false;
			}
			rs.close();
			rs = null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return ifExistId;
	}

	public boolean bIfExistByRaceNoRaceId(String sTableName, long iRaceID,
			byte iRaceNo) {
		ResultSet rs = null;
		boolean ifExistId = false;
		String sSql = "select * from " + sTableName + " where Trialid="
				+ iRaceID + " and HorseNo =" + iRaceNo;
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			if (rs != null && rs.next()) {
				ifExistId = true;
				rs.close();

			} else {
				ifExistId = false;
			}
			rs = null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return ifExistId;

	}

	public boolean bIfExistByBrandNoTrialId(String sTableName, long iRaceID,
			String sBrandNo) {
		ResultSet rs = null;
		boolean ifExistId = false;
		String sSql = "select * from " + sTableName + " where Trialid="
				+ iRaceID + " and HorseBrandno ='" + sBrandNo + "'";
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			if (rs != null && rs.next()) {
				ifExistId = true;
				rs.close();

			} else {
				ifExistId = false;
			}
			rs = null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return ifExistId;

	}

	public int getTrackConditionIDByRaceID(String sRaceID) {
		int iTrackConditionID = 0;
		String sSql = "select TrackConditionID from tc_prerace_race where raceid="
				+ sRaceID;
		ResultSet rs = null;
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			if (rs != null && rs.next()) {
				iTrackConditionID = rs.getInt(1);
				rs.close();
				rs = null;
			}

		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return iTrackConditionID;

	}

	public Vector<String> getVectorBySQL(String sSql) {
		Vector<String> vObjcet = new Vector<String>();
		ResultSet rs = null;
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sSql);
			// vObjcet = Sort(rs);
			while (rs.next()) {
				vObjcet.add(rs.getString(1));
			}
			rs.close();
			rs = null;

		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return vObjcet;
	}

	// public Vector Sort(java.sql.ResultSet rs) {
	// Vector vector = null;
	// int colcount;
	// ResultSetMetaData rsmd;
	// try {
	// colcount = rs.getMetaData().getColumnCount();
	// rsmd = rs.getMetaData();
	// } catch (Exception e) {
	// return vector;
	// }
	// try {
	// vector = new Vector();
	// Vector vectorCol = new Vector();
	// for (int j = 1; j <= colcount; j++) {
	// vectorCol.addElement(rsmd.getColumnLabel(j));
	// }
	// vector.addElement(vectorCol);
	// Vector vectorType = new Vector();
	// for (int j = 1; j <= colcount; j++) {
	// vectorType.addElement(rsmd.getColumnTypeName(j));
	// }
	// vector.addElement(vectorType);
	// while (rs.next()) {
	// Vector vectorColName = new Vector();
	// for (int j = 1; j <= colcount; j++) {
	// vectorColName.addElement(rs.getObject(j));
	//
	// }
	// vector.addElement(vectorColName);
	// }
	// } catch (Exception e0) {
	// return vector;
	// }
	// return vector;
	// }


	public void execStoredProcedures(String sqlStmts) throws SQLException {
		try {
			myztstd = new ZTStd();
			myztstd.execStoredProceduresSQL(sqlStmts);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void execStoredProcedures(String psName, String psParaList) {
		try {
			myztstd = new ZTStd();
			myztstd.ExecStoredProcedures(psName, psParaList);
		} catch (Exception fe) {
			// fe.printStackTrace();
			System.out.println(fe.getStackTrace());
		}

	}

	public void execInsertStoredProcedures(String psName, String psParaList) {
		try {
			myztstd = new ZTStd();
			myztstd.ExecStoredProcedures(psName, psParaList);

		} catch (Exception fe) {
			System.out.println(fe.toString());
		}

	}

	public void execBathStoredProcedures(String[] psParaList) {
		try {
			myztstd = new ZTStd();

			for (int i = 0; i < psParaList.length; i++) {
				String sSql = psParaList[i];
				myztstd.execStoredProceduresSQL(sSql);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public void execSQL(String sSql) {
		if (sSql.trim().equals(""))
			return;
		try {
			myztstd = new ZTStd();
			// System.out.println("sSql: "+sSql);
			myztstd.execSQL(sSql);
			// System.out.println("sSql: "+sSql);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public String enCodingChar(String sOtherChar) {
		String sOut = sOtherChar;
		try {
			sOut = new String(sOut.getBytes("gbk"), "ISO8859");

		} catch (Exception e) {

		}

		return sOut;
	}

	/**
	 * 
	 * @param horseName
	 * @param horseDam
	 * @param horseSire
	 * @return
	 */
	public int getHorsIDBySth(String horseName, String horseDam,String horseSire) {
		int id = 0;
		String sSql = "";
		try {
			if (horseName.indexOf("(") > -1)
				horseName = horseName.substring(0, horseName.indexOf("("));
			if (horseDam.indexOf("(") > -1)
				horseDam = horseDam.substring(0, horseDam.indexOf("("));
			if (horseSire.indexOf("(") > -1)
				horseSire = horseSire.substring(0, horseSire.indexOf("("));

			horseName = horseName.replace("'", "''");
			horseDam = horseDam.replace("'", "''");
			horseSire = horseSire.replace("'", "''");

			sSql = "Select horseid from horse where horsename='" + horseName
					+ "' and horseDamName like '" + horseDam + "%'";
			id = getHorsIDBySth(sSql);
			if (id == 0) {
				sSql = "Select horseid from horse where horseDamName like '"
						+ horseDam + "%' and horsesireName like '" + horseSire
						+ "%'";
				id = getHorsIDBySth(sSql);
			}
			// if(id==0){
			// sSql =
			// "Select horseid from horse where formerName like '%"+horseName+"%'";
			// id=getHorsIDBySth(sSql);
			// }
			if (id == 0) {
				sSql = "Select horseid from horse where horsename='"
						+ horseName + "'";
				id = getHorsIDBySth(sSql);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return id;
	}

	public int getHorsIDBySth(String sql) {
		ResultSet rs = null;
		int id = 0;
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sql);
			if (rs != null && rs.next())
				id = rs.getInt(1);
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return id;
	}
	
	public List<Integer>  getHorsIdBySth(String sql) 
	{
		List<Integer> idList = new ArrayList<Integer>();
		ResultSet rs = null;
		int id = 0;
		try {
			myztstd = new ZTStd();
			rs = myztstd.getResultBySelect(sql);
			while(rs!=null&&rs.next())
			{
				id = rs.getInt(1);
				idList.add(id);
			}
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return idList;
	}
	
	public String getPoolType(String betTypeName) {
		ResultSet rs = null;
		String sSql = "";
		int iTrackId = 0;
		try {
			myztstd = new ZTStd();
			sSql = "Select pooltypeID from  Code_PoolType where poolName = N'"+betTypeName+"'";
			rs = myztstd.getResultBySelect(sSql);
			if(rs.next()) {
				iTrackId= rs.getInt(1);
			} 
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return iTrackId+"";
	}
	
	public String getTrckID(String trackName,boolean isAddTrack) {
		ResultSet rs = null;
		String sSql = "";
		int iTrackId = 0;
		try {
			myztstd = new ZTStd();
//			sSql="select*from TC_V2_Trainer where TrainerID in(select TC_TrainerID from TC_V2_PreRace_Horse where ExtractTime>GETDATE()-7)and Nationality is null";
			sSql = "select  top 100*from Code_Track where TrackName= N'"+trackName+"'";
			rs = myztstd.getResultBySelect(sSql);
			if(rs.next()) {
				iTrackId= rs.getInt(1);
			} else {
//				strSql="insert  into To_Code_Track values("+iTrackId+",'"+sTrackName+"',"+null+")";
				if(isAddTrack) {
					sSql="select max(TrackID) as id from Code_Track";
					rs=myztstd.getResultBySelect(sSql);
					if(rs.next())iTrackId=rs.getInt("id")+1;
					sSql="insert  into Code_Track values("+iTrackId+",N'"+trackName+"',"+null+")";
					myztstd.execSQL(sSql);	
				} else iTrackId=999;
			}
			rs.close();
			rs = null;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return iTrackId+"";
	}
}

