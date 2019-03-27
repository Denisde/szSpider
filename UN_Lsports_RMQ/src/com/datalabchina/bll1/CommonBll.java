package com.datalabchina.bll1;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.CommonFun;
import com.common.Utils;
import com.common.db.ZTStd;

public class CommonBll {
	public static Logger logger = Logger.getLogger(CommonBll.class.getName());
	
	public static String ReplaceName(String sName)
	{
		if(sName!=null)
			return sName.trim().replaceAll("&nbsp;"," ").replaceAll("&acute;","'").replaceAll("<strong class=\"uppercase\">", "");
		else
			return null;
	}	
	
	public static Matcher GetMatcherStrGroup(String strContent, String strPattern) {
		Pattern pattern = Pattern.compile(strPattern, Pattern.DOTALL
				| Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(strContent);
		return matcher;
	}	
	
	public static String[] getTrack(String raceTrackName) {
		String r[] = {null,null,null};
		try {
			
//			RaceAreaID	RaceAreaName	RaceAreaFullName
//			12			HK				Hong Kong
//			13			JPN				Japan			
			String raceAreaName = null;
			Matcher matcher = GetMatcherStrGroup(raceTrackName,"(.*?)\\((.*?)\\)");
			if (matcher.find()){
				raceAreaName = matcher.group(2);
				if (raceTrackName.lastIndexOf("(") > 0) {
					raceAreaName = raceTrackName.substring(raceTrackName.lastIndexOf("(")+1);
					raceAreaName = raceAreaName.replaceAll("\\)", "").trim();
				}
				raceTrackName = matcher.group(1).trim();
				if(raceAreaName.toUpperCase().indexOf("JULY")!=-1)
				{
					raceTrackName = raceTrackName + " (" + raceAreaName + ")";
					raceAreaName = null;
				}
				if(raceAreaName!=null) {
					String ran = raceAreaName.toUpperCase();
					if (!ran.equals("IRE")&&!ran.equals("A.W")&&!ran.equals("AW")&&!ran.equals("FR")&&!ran.equals("HK")&&!ran.equals("JPN")) {
						return r;						
					}
				}
			}
			raceTrackName = ReplaceName(raceTrackName);
			r[0] = raceTrackName;
			raceTrackName = raceTrackName.replaceAll("'","''");
			String sql = "select * from code_track where TrackName='"+ raceTrackName + "'";
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (!rs.next()){
				sql = "select * from code_track where TrackID in (select TrackID from code_trackmapping where mappingName = '"+ raceTrackName + "')";
				rs = new ZTStd().getResultBySelect(sql);
				if (!rs.next()){
					//new track
					String RaceArea = null;
					if(raceAreaName!=null) {
						String ran = raceAreaName.toUpperCase();
						if (ran.equals("FR")) {
							RaceArea = "6";	
						} else if (ran.equals("IRE")) {
							RaceArea = "1";
						} else if (ran.equals("HK")) {
							RaceArea = "12";
						} else if (ran.equals("JPN")) {
							RaceArea = "13";							
						} else {
							RaceArea = "43";
						}
					}
					
					sql = "select max(TrackID) from code_track";
					rs = new ZTStd().getResultBySelect(sql);
					if (!rs.next()){
						return r;
					}
					
					Integer maxId = rs.getInt(1)+1; 
					sql = "insert into code_track(TrackID,TrackName,RaceAreaID) values("+maxId + ",'" + raceTrackName + "'," +RaceArea+ ")";
					logger.info("Insert new Track: " + sql);
					new ZTStd().execSQL(sql);
					r[1] = maxId+"";
					r[2] = RaceArea;
					return r;					
				}
			}
						
			String TrackID = rs.getString("TrackID");
			String RaceAreaID = rs.getString("RaceAreaID");
			r[1] = TrackID;
			r[2] = RaceAreaID;
			if(RaceAreaID==null) {
				logger.error("trackname = "+raceTrackName+" raceareaid is null");
				return r;
			}
			
			if(RaceAreaID.equals("1")||RaceAreaID.equals("43")||RaceAreaID.equals("6")||RaceAreaID.equals("12")||RaceAreaID.equals("13")) {
				return r;
			} else {
				logger.warn("not need parsing by RaceAreaID = " + RaceAreaID);
				String rr[] = {null,null,null};
				return rr;
			}
	
		} catch(Exception e) {
			logger.error("",e);
		}
		return r;
	}	
	
	public static String[] GetClassIDFromName(String classname) {
		String a[] = {classname,null};
		try {
			String sql = "select ClassID from code_class where ClassName='"+ classname + "'";
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (rs.next()){
				String ClassID = rs.getString("ClassID");
				a[1] = ClassID;
			} else {
				sql = "select max(ClassID) from code_class";
				rs = new ZTStd().getResultBySelect(sql);
				if (!rs.next()){
					return a;
				}
				
				Integer maxId = rs.getInt(1)+1; 
				sql = "insert into code_class(ClassID,ClassName) values("+maxId + ",'" + classname + "')";
				logger.info("Insert new code_class: " + sql);
				new ZTStd().execSQL(sql);
				a[1] = maxId+"";
				return a;					
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return a;
	}
	
	public static String[] GetClass(String title) {
		//<td width=88% class="H3">Gosforth Decorating And Building Services Maiden Auction Stakes (6)<span class='B7'>&nbsp;(2yo)</span></td>
		
		try {
			String className = Utils.extractMatchValue(title, "\\((\\d)\\)");
			if (className == null) {
				className = Utils.extractMatchValue(title, "\\([A-Z](\\d)\\)");
				if (className == null) {
					className = Utils.extractMatchValue(title, "\\(Class (\\d)\\)");
					if (className == null) {
						className = Utils.extractMatchValue(title, "\\(Class (\\d)\\)");
					}
				}			
			}
			
			if (className != null) {
				return GetClassIDFromName(className);			
			}
			
			className = Utils.extractMatchValue(title, "\\(Class ([A-Z])\\)");
			if (className != null) {
				char ch = className.charAt(0);
				return GetClassIDFromName(String.valueOf((char)(ch-16)));
			}
			
			className = Utils.extractMatchValue(title, "\\(Group (\\d)\\)");
			if (className != null) {
				return GetClassIDFromName("1");
			}
			
			if (title.indexOf("Listed") > 0) {
				return GetClassIDFromName("1");
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		
		String a[] = {null,null}; 
		return a;
	}
	
	public static String GetNewClassID(String title) {
		
		try {
			
//			String NewClassID = Utils.extractMatchValue(title, "\\(([A-Z]\\d)\\)");
//			if (NewClassID == null) return null;
//			
//			String sql = "select ClassID from Code_NewClass where ClassName='"+ classname + "'";
//			ResultSet rs = new ZTStd().getResultBySelect(sql);
//			if (rs.next()){
//				String ClassID = rs.getString("ClassID");
//				a[1] = ClassID;
//			} else {
//				sql = "select max(ClassID) from code_class";
//				rs = new ZTStd().getResultBySelect(sql);
//				if (!rs.next()){
//					return a;
//				}
//				
//				Integer maxId = rs.getInt(1)+1; 
//				sql = "insert into code_class(ClassID,ClassName) values("+maxId + ",'" + classname + "')";
//				logger.info("Insert new code_class: " + sql);
////				new ZTStd().execSQL(sql);
//				a[1] = maxId+"";
//				return a;					
//			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}	
	
	public static String GetDivisionID(String title) {
		try {			
			String Division = Utils.extractMatchValue(title, "\\((Div.*?)\\)");
			if (Division != null) {
				String sql = "select DivisionID from Code_Division where DivisionName='"+ Division + "'";
				ResultSet rs = new ZTStd().getResultBySelect(sql);
				if (rs.next()){
					return rs.getString("DivisionID");
				}			
			}
		} catch (Exception e) {
			logger.error("",e);
		}		
		return null;
	}
	
	public static String GetColumn(String sql) {
		
		try {
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (rs.next()){
				String value = rs.getString(1);
				return value;
			}
		} catch (Exception e) {
			logger.error("",e);
		}		
		
		return null;
	}	
	
	public static String GetOfficialGoing(String GoingName) {
		
		try {
			String sql = "select TrackConditionID from Code_TrackCondition where TrackConditionName='"+ GoingName + "'";
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (rs.next()){
				String OfficialGoing = rs.getString("TrackConditionID");
				return OfficialGoing;
			} else {
				sql = "select max(TrackConditionID) from Code_TrackCondition";
				rs = new ZTStd().getResultBySelect(sql);
				if (!rs.next()){
					return null;
				}
				
				Integer maxId = rs.getInt(1)+1; 
				sql = "insert into Code_TrackCondition(TrackConditionID,TrackConditionName) values("+maxId + ",'" + GoingName + "')";
				logger.info("Insert new Code_TrackCondition: " + sql);
				new ZTStd().execSQL(sql);
				return maxId+"";					
			}
		} catch (Exception e) {
			logger.error("",e);
		}		
		
		return null;
	}
	
	public static String GetGroupID(String title)
	{
		String Group = Utils.extractMatchValue(title, "\\((Group.*?)\\)");
		if (Group == null) {
			Group = Utils.extractMatchValue(title, "\\((Grade.*?)\\)");
			if (Group == null) {
				Group = Utils.extractMatchValue(title, "\\((Listed.*?)\\)");
			}			
		}
		
		if (Group == null) return null;
		
		try {
			String sql = "select GroupID from Code_Group where GroupName='"+ Group + "'";
			ResultSet rs = new ZTStd().getResultBySelect(sql);
			if (rs.next()){
				String ClassID = rs.getString("GroupID");
				return ClassID;
			} else {
				sql = "select max(GroupID) from Code_Group";
				rs = new ZTStd().getResultBySelect(sql);
				if (!rs.next()){
					return null;
				}
				
				Integer maxId = rs.getInt(1)+1; 
				sql = "insert into Code_Group(GroupID,GroupName) values("+maxId + ",'" + Group + "')";
				logger.info("Insert new Code_Group: " + sql);
				new ZTStd().execSQL(sql);
				return maxId+"";					
			}
		} catch (Exception e) {
			logger.error("",e);
		}		
		
		return null;
	}	
		
	public static String GetHandicap(String title) {
		title = title.toLowerCase();
		if (title.indexOf("handicap") != -1 || title.indexOf("hcap") != -1 || title.indexOf("h''cap") != -1 || title.indexOf("nursery") != -1)
			return "1";
		else
			return "0";
	}

	public static String GetMainden(String title) {
		if (title.indexOf("Maiden") != -1)
			return "1";
		else
			return "0";
	}

	public static String GetNovice(String title) {
		if (title.indexOf("Novice") != -1)
			return "1";
		else
			return "0";
	}

	public static String GetHcapgrade(String content) {
//		(5yo+,0-109)
//		(,0-70)
//		(5yo+)
//		(4-7yo)
		String pattern = "\\(.*?,\\d-(\\d{0,3})\\)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return matcher.group(1);
		else
			return null;
	}

	public static String GetMaxRating4Post(String content) {
		try {
//			<span class="rp-raceTimeCourseName_ratingBandAndAgesAllowed">
//          (0-65, 3yo)
//      </span>
			content = Utils.extractMatchValue(content, "_ratingBandAndAgesAllowed(.*?)<"); 
			if (content != null) {
				String val = Utils.extractMatchValue(content, "-(.*?),");
				try {
					Integer.parseInt(val);
					return val;
				} catch (Exception e) {
				}
				return null;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String GetMinRating4Post(String content) {
		try {
			content = Utils.extractMatchValue(content, "_ratingBandAndAgesAllowed(.*?)<");
			if (content != null) {
				String val = Utils.extractMatchValue(content, "\\((.*?)-");
				try {
					Integer.parseInt(val);
					return val;
				} catch (Exception e) {
				}
				return null;				
			}
		} catch (Exception e) {
		}
		return null;
	}	
	
	public static String GetMaxRating(String content) {
		try {
//			<span class="rp-raceTimeCourseName_ratingBandAndAgesAllowed">
//          (0-65, 3yo)
//      </span>
			
//			<span data-test-selector="RC-header__rpAges">
//            
//            (3yo 0-65)
//        </span>			
//			  (3-5yo)
			content = Utils.extractMatchValue(content, "__rpAges(.*?)<"); 
			if (content != null) {
				String val = Utils.extractMatchValue(content, "-(.*?)\\)");
				try {
					Integer.parseInt(val);
					return val;
				} catch (Exception e) {
				}
				return null;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String GetMinRating(String content) {
		try {
			content = Utils.extractMatchValue(content, "__rpAges(.*?)<");
			if (content != null) {
				String val = Utils.extractMatchValue(content, "\\(.*? (.*?)-");
				try {
					Integer.parseInt(val);
					return val;
				} catch (Exception e) {
				}
				return null;				
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String GetMinHcap(String content) {
//		(5yo+,0-109)
//		(,0-70)
//		(5yo+)
//		(4-7yo)
//		<li>
//		 (Class 4) (0-100, 5yo+) (3m1f110y) 3m1&frac12;f Good 19 fences </li>
//		Oyster Partnership Handicap (Class 5) (0-70, 4yo+) 1m5f Standard
//		Ashford Environmental Handicap (Class 4) (0-85, 3yo) 1m2f Standard
//		Timeform Betfair Racing Club Standard Open National Hunt Flat Race (Class 6) (4-6yo) (2m2f110y) 2m2&frac12;f Good
//		Betfair Supports Oaksey House Handicap Hurdle (Class 5) (0-90, 4yo+) 3m3f Good 13 hdles
//		Betfair Funds PJA Doctor "Hands And Heels" Jumps Series Handicap Hurdle (Conditonals & Amateurs) (Class 5) (0-95, 4yo+) 2m4f Good 10 hdles
//		Betfair Sponsors Pride of Racing Dinner Handicap Chase (Class 5) (0-95, 5yo+) 2m4f Good 15 fences
//		Great Offers At wolverhampton-racecourse.co.uk Handicap (Class 7) (0-50, 4yo+) (1m141y) 1m&frac12;f Standard
//		ladbrokespoker.com Handicap (Class 3) (0-95, 4yo+) (7f32y) 7f Standard

		String pattern = "\\(.*?,?\\s?(\\d{1,2})-(\\d{1,3})\\)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return matcher.group(1);
		
		pattern = "\\((\\d{1,2})-\\d{1,3}, \\dyo\\+?\\)";
		matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return matcher.group(1);
		
		return null;
	}
	
	public static String GetMaxHcap(String content) {
//		(5yo+,0-109)
//		(,0-70)
//		(5yo+)
//		(4-7yo)
		String pattern = "\\(.*?,?\\s?\\d{1,2}-(\\d{1,3})\\)";
		Matcher matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return matcher.group(1);
		
		pattern = "\\(\\d{1,2}-(\\d{1,3}), \\dyo\\+?\\)";
		matcher = CommonFun.GetMatcherStrGroup(content, pattern);
		if (matcher.find())
			return matcher.group(1);
		
		return null;
	}		

	public static String GetWinnerTime(String sHtml){	
//		<tr><td class="B1"><b>5 ran</b> TIME 5m 23.10s (slow by 31.10s)&nbsp;&nbsp;&nbsp;
//		<b>10 ran TIME</b> 6m 42.00s (slow by 38.00s) Total SP 119%<br />
//		 <b>15 ran TIME</b> 5m 0.80s (slow by 29.80s) Total SP 121%<br />
//		 <b>7 ran TIME</b> 2m 33.19s (slow by 0.19s) Total SP 111%<br />
//	     <b>12 ran TIME</b> 4m 54.20s (slow by 43.20s) Total SP 147%<br />
//		<b>15 ran TIME</b> 3m 54.30s (slow by 10.30s) Total SP 123%<br />
//		 <b>TIME</b> 4m 33.80s (slow by 12.80s) <b>Total SP</b> 123%<br />
		double dWinnerTime=0d;
		int iMinute=0;
		double iSecond=0;
		String sSecond;
		try {
			
			//String sPattern = "TIME</b> (.*?)(<b>)?Total SP";
			//winning-time-value">5m 49.90s</span>
//			String sPattern = "winning-time-value\">(.*?)<";
			
//			Winning time:
//                <span class="rp-raceInfo__value">
//                    4m 25.97s                  
			String sPattern = "Winning time:.*?>(.*?)<";
			Matcher matcher = CommonFun.GetMatcherStrGroup(sHtml,sPattern);
			if (matcher.find()){				
				String sTime = matcher.group(1).trim();
//				logger.info("GetWinnerTimeGetWinn1111========"+matcher.group(1).trim());
				if (sTime.indexOf("(")>-1) 
					sTime = sTime.substring(0,sTime.indexOf("("));
				if (sTime.indexOf("m")>-1){
					iMinute = Integer.parseInt(sTime.substring(0,sTime.indexOf("m")));
					if (sTime.indexOf("s")>-1){
						sSecond = sTime.substring(sTime.indexOf("m")+1,sTime.indexOf("s")).trim();
						iSecond = Double.parseDouble(sSecond);
					}
				}else if(sTime.indexOf("s")>-1){
					sSecond = sTime.substring(0,sTime.indexOf("s")).trim();
					iSecond = Double.parseDouble(sSecond);
				}
				dWinnerTime = iMinute * 60 + iSecond;
//				logger.info("dWinnerTime========"+dWinnerTime);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return dWinnerTime+"";
	}	
	
	public static String GetOdds(String rawOdds) {
		double dOdds = 0d;
		try {
			//<span class="black">16/1 </span>
			//<span class="rp-horseTable__row__price ng-binding" data-ng-bind="item.price">4/6F</span>
			
			if (rawOdds.indexOf("/") > -1) {
				double dFirst = Double.parseDouble(rawOdds.split("/")[0].trim());
				if(dFirst>1000) dFirst=999;
				String sSecond = rawOdds.split("/")[1].trim();
				double dSecond = 1;
				if (CommonFun.isNumber(sSecond)) {
					dSecond = Double.parseDouble(sSecond);
				} else {
					dSecond = Double.parseDouble(sSecond.substring(0,sSecond.length() - 1));
				}
				dOdds = 1 + dFirst / dSecond;
			} else if (rawOdds.indexOf("EvensF")>-1) {
				dOdds = 2;
			} else if (rawOdds.toLowerCase().equals("evs") || rawOdds.toLowerCase().equals("evsj")) {
				dOdds = 2;
			}

			return String.format("%.2f", dOdds);
//			return new BigDecimal(dOdds) + "";
			
		} catch (Exception e) {
			logger.error("",e);
		}
		
		return String.format("%.2f", 0.0d);
		//return new BigDecimal(0.0d) + "";
	}
	
	public static String GetBeatenDistance(String strHtml) {
		double dBeatenDistance = 0d;
		try {			
			String str = strHtml;
			if (str == null || str.equals("") || str.trim().length()==0)
				return null;
			
			//2¼
			//¼	½	¾
			str = str.replaceAll("¼", "&frac14").replaceAll("½", "&frac12").replaceAll("¾", "&frac34");
			if (str.indexOf("&frac") > -1) {
				if (str.indexOf("&frac") == 0) {
					double dStart = Double.parseDouble(str.substring(5,6));
					double dEnd = Double.parseDouble(str.substring(6,7));
					dBeatenDistance = dStart / dEnd ;
				} else {
					int iFirst = Integer.parseInt(str.substring(0, str.indexOf("&frac")));
					double dStart = Double.parseDouble(str.substring(str.indexOf("&frac") + 5, str.indexOf("&frac") + 6));
					double dEnd = Double.parseDouble(str.substring(str.indexOf("&frac") + 6, str.indexOf("&frac") + 7));
					dBeatenDistance = iFirst + dStart / dEnd;
				}
			} else {
				if (CommonFun.isNumber(str)) {
					dBeatenDistance = Double.parseDouble(str);
				} else{
					String sql = "select BeatendistanceValue from Code_BeatenDistance where BeatendistanceName = '" + str + "'";
					String BeatendistanceValue = CommonBll.GetColumn(sql);
					return BeatendistanceValue;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return new BigDecimal(dBeatenDistance)+"";		
	}
	
	public static String GetGear(String sName) {
		try {
			String sql = "select HeadGearID from Code_HeadGear where HeadGearName = '" + sName + "'";
			String HeadGearID = CommonBll.GetColumn(sql);
			return HeadGearID;
		} catch (Exception e) {
			return null;
		}
	}

	
	public static String GetAccident(String sName) {
		try {
			String sql = "select AccidentID from Code_Accident where AccidentName = '" + sName + "'";
			String AccidentID = CommonBll.GetColumn(sql);
			return AccidentID;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String GetTypeRaceCode(String sName) {
		try {
			String sql = "select Code from Code_Race where Meaning = '" + sName + "'";
			String result = CommonBll.GetColumn(sql);
			return result;
		} catch (Exception e) {
			return null;
		}
	}	
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		getTrack("Wexford (RH) (IRE)");
	}
}
