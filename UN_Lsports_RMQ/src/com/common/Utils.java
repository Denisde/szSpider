package com.common;
/**
 * @version 1.1
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

//import jregex.Replacer;

import org.apache.log4j.Logger;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class);
	
	public static HashMap<String,Integer> maxFieldSize = new HashMap<String,Integer>();

	public static String delSpaceChar(String str) {
		return str.replaceAll("&nbsp;", "");
	}	
	
	//嵌套 Table
	public static ArrayList<String> getTable(String strBody) {
		return getNestingElement(strBody,"table");
	}
	
	//嵌套 TR
	public static ArrayList<String> getTR(String strBody) {
		return getNestingElement(strBody,"tr");
	}

	//嵌套 TD
	public static ArrayList<String> getTD(String strBody) {
		return getNestingElement(strBody,"td");
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getNestingElement(String strBody,String tag) {
		ArrayList<String> resultList = new ArrayList<String>();
		tag = tag.toUpperCase();
		int fromIndex = 0;
		List list = getElement(strBody,tag,fromIndex);
		while (list != null) {
			resultList.add((String)list.get(0));
			list = getElement(strBody,tag,(Integer)list.get(1));
		}
		return resultList.size()>0 ? resultList : null;
	}
	
	@SuppressWarnings("unchecked")
	public static List getElement(String strBody, String tag, int fromIndex) {
		String upperCaseStr = strBody.toUpperCase();
		int begin = upperCaseStr.indexOf("<" + tag, fromIndex);
		if (begin == -1) {
			return null;
		}
		
		int endTag = upperCaseStr.indexOf(">",begin);
		if (endTag <= begin) {
			return null;
		}
		
		int tableBegin = endTag + 1;
		
		int tableEnd = upperCaseStr.indexOf("</"+tag+">",endTag);
		if (tableEnd <= endTag) {
			return null;
		}
		
		int p = upperCaseStr.indexOf("<"+tag,tableBegin);
		while ((p > 0) && (p < tableEnd)) {
			tableEnd = upperCaseStr.indexOf("</"+tag+">",tableEnd+tag.length()+3);
			if (tableEnd == -1) {
				return null;
			}
			p = upperCaseStr.indexOf("<"+tag,p+tag.length()+1);
			
		}
		
		fromIndex = tableEnd + tag.length() + 3;
		List list = new ArrayList();
		list.add(strBody.substring(tableBegin, tableEnd));
		list.add(fromIndex);
		return list;
	}
	
//	public static ArrayList<String> getTable(String strBody) {
//		String sPattern = "<table.*?>(.*?)</table>";
//		return extractMatchValues(strBody, sPattern);
//	}
//	
//	public static ArrayList<String> getTR(String strTable) {
//		String sPattern = "<tr.*?>(.*?)</tr>";
//		return extractMatchValues(strTable, sPattern);
//	}	
//
//	public static ArrayList<String> getTD(String strTR) {
//		String sPattern = "<td.*?>(.*?)</td>";
//		return extractMatchValues(strTR, sPattern);
//	}
	
	public static String extractMatchValue(String strBody,String strPattern) {
		if (strBody == null) return null;
		String val = null;
		try {
			Matcher matcher = CommonMethod.getMatcherStrGroup(strBody,strPattern);
			if (matcher.find()){
				val = matcher.group(1).trim();				
				if (val.equals("")) return null;
			}
		} catch (Exception e) {
			logger.error("extractStatusText ", e);
		}
		return val;
	}
	
	public static String extractMatchValue4Money(String strBody,String strPattern) {
		String val = null;
		try {
			Matcher matcher = CommonMethod.getMatcherStrGroup(strBody,strPattern);
			if (matcher.find()){
				val = matcher.group(1).trim();
				val = val.replaceAll("\n", "").replaceAll("  ", " ").replaceAll("£", "").replaceAll("&pound;", "").replaceAll("&euro;", "").trim();  
				val = Utils.deleteTag(val).trim();
				val = val.replaceAll("\n", "").replaceAll("  ", " ").trim();
				while (val.indexOf("  ") > 0) {
					val = val.replaceAll("  ", " ").trim();
				}
				val = val.replaceAll(",", "").trim();
				if (val.equals("")) return null;
			}
		} catch (Exception e) {
			logger.error("extractStatusText ", e);
		}
		return val;
	}	
	
	public static ArrayList<String> extractMatchValues(String strBody,String strPattern) {
		ArrayList<String> list = new ArrayList<String>(); 
		try {			 
			Matcher matcher = CommonMethod.getMatcherStrGroup(strBody,strPattern);
			while (matcher.find()){
				String val = matcher.group(1);
				list.add(val);
			}
		} catch (Exception e) {
			logger.error("extractMatchValues ", e);
		}
		return list;
	}	
	
	public static Byte getByteValueFromArray(String fields[],int index) {
		Byte result = null;
		try {
			result = Byte.parseByte(getValueFromArray(fields,index));
		} catch (Exception e) {
		}
		return result;
	}
	
	public static Short getShortValueFromArray(String fields[],int index) {
		Short result = null;
		try {
			result = Short.parseShort(getValueFromArray(fields,index));
		} catch (Exception e) {
		}
		return result;
	}
	
	public static Integer getIntegerValueFromArray(String fields[],int index) {
		 Integer result = null;
		try {
			result = Integer.parseInt(getValueFromArray(fields,index));
		} catch (Exception e) {
		}
		return result;
		
	}
	
	public static Long getLongValueFromArray(String fields[],int index) {
		Long result = null;
		try {
			result = Long.parseLong(getValueFromArray(fields,index));
		} catch (Exception e) {
		}
		return result;
	}
	
	public static Double getDoubleValueFromArray(String fields[],int index) {
		Double result = null;
		try {
		   String 	fieldsVal =getValueFromArray(fields,index);
		  if ( fieldsVal.equalsIgnoreCase("scr")) 
		        result = -999.0;
		  else 
			    result = Double.parseDouble(getValueFromArray(fields,index));
		} catch (Exception e) {
		}
		return result;
	}
	
	public static Date getDateValueFromArray(String fields[],int index) {
		Date result = null;
		try {
			result = DateUtils.toShortDate(getValueFromArray(fields,index));
		} catch (Exception e) {
		}
		return result;
	}

	public static String getValueFromArray(String fields[],int index) {
		try {
			return (String)fields[index]; //.trim().replaceAll("&nbsp;", "");
		} catch (Exception e) {
			return "";
		}
	}	

	
	
	public static Short getShortValueFromString(String tmpStr) {
		Short result = null;
		try {
			result = Short.parseShort(tmpStr);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static Integer getIntegerValueFromString(String tmpStr) {
		 Integer result = null;
		try {
			result = Integer.parseInt(tmpStr);
		} catch (Exception e) {
		}
		return result;
		
	}
	
	public static Long getLongValueFromString(String tmpStr) {
		Long result = null;
		try {
			result = Long.parseLong(tmpStr);
		} catch (Exception e) {
		}
		return result;
	}
	
	public static Double getDoubleValueFromString(String tmpStr) {
		Double result = null;
		try {
			result = Double.parseDouble(tmpStr);
		} catch (Exception e) {
		}
		return result;
	}
	
	
	
	
	public static String removeCountry(String str) {		
		int p = str.indexOf("(");
		if (p > 0) {
			str = str.substring(0, p);
		}
		str = str.trim();
		return str;
	}

	public static String removeNotUse(String horseName) {
		String result = removeCountry(horseName);
		int p =result.indexOf("*");
		if (p > 0) {
			result = result.substring(0, p);
		}
		result = result.replaceAll("[*]", "");
		result = result.trim();
		return result;
	}
	
	public static String removeBold(String str) {
		return str.replaceAll("</B>","").replaceAll("<B>","").replaceAll("</b>","").replaceAll("<b>","").replaceAll("&nbsp;", "").trim();
	}
	
	public static String getValueFromList(List<String> list,int index) {
		try {
			return (String)list.get(index);
		} catch (Exception e) {
			return "";
		}
	}
	
	public static void printList(List<String> list) {
		System.err.println(list.size()+ " =========================================");
		for (int i=0; i<list.size(); i++) {
			String s = (String)list.get(i);
			System.err.println(i + " =========================================");
			System.err.println(s);
		}
	}
	
	public static void printArray(String[] array) {
		System.err.println(array.length+ " =========================================");
		for (int i=0; i<array.length; i++) {
			String s = array[i];
			System.err.println(i + " =========================================");
			System.err.println(s);
		}
	}		
	
	/**
	 * 合并多个空格为一个。
	 */
	public static String mergeSpace(String str) {
		if (str.indexOf("  ") > -1) {
			str = str.replaceAll("  ", " ");
		}
		if (str.indexOf("  ") > -1) {
			str = mergeSpace(str);
		}
		return str;
	}
	
	/**
	 * 去掉圆括号
	 */
	public static String removeParenthesis(String str) {
		String leftStr = "";
		String rightStr = "";
		try {
			String result = str;
			int r = str.indexOf(")");
			if (r > -1) {
				if (r+1 <= str.length()) {
					rightStr = str.substring(r+1);
				}
				leftStr = str.substring(0,r);
				int l = leftStr.lastIndexOf("(");
				if (l > -1) {
					leftStr = leftStr.substring(0, l); 
				}
				result = leftStr + rightStr;
			}
			
			
			if (result.indexOf(")") > 0) {
				result = removeParenthesis(result);
			}
			
			result = mergeSpace(result).trim();
			return result;		
			
		} catch (Exception e) {
			return str;
		}
	}
	
	/**
	 * 去掉圆括号
	 */
	public static String removeParenthesis1(String sOld) {
		if (sOld == null) {
			return null;
		}
		String sNew = sOld;
		sNew = replaceSymbol(sOld, "\\(.*?\\)", " ");
		return sNew;
	}

	/**
	 * 将英语单词转换为2位数的月份
	 * @param str Sep,sep,September ... 
	 * @return 09
	 */
	public static String getMonth(String str) {
		String MM = str.toLowerCase();
		if (MM.startsWith("jan")) { //January
			MM = "01";
		} else if (MM.startsWith("feb")) { //February
			MM = "02";
		} else if (MM.startsWith("mar")) {	//March
			MM = "03";
		} else if (MM.startsWith("apr")) {	//April
			MM = "04";
		} else if (MM.startsWith("may")) {		//May
			MM = "05";
		} else if (MM.startsWith("jun")) {		//June
			MM = "06";
		} else if (MM.startsWith("jul")) {		//July
			MM = "07";
		} else if (MM.startsWith("aug")) {	//August
			MM = "08";
		} else if (MM.startsWith("sep")) {	//September
			MM = "09";
		} else if (MM.startsWith("oct")) {	//October
			MM = "10";
		} else if (MM.startsWith("nov")) {	//November
			MM = "11";
		} else if (MM.startsWith("dec")) {		//December
			MM = "12";
		}
		if (MM.length() == 2) {
			return MM;
		} else {
			return null;
		}
	}
	
	/**
	 * 替换字符串中的Html特殊标记为相应的字符 
	 */
	public static String replaceHtmlChar(String str) {
		return str.replaceAll("&apos;", "'")
		.replaceAll("&amp;", "&")
		.replaceAll("&quot", "\"")
		.replaceAll("&nbsp;", " ")
		.replaceAll("&lt", "<")
		.replaceAll("&gt", ">")
		.replaceAll("&lsquo", "‘")
		.replaceAll("&rsquo", "’")
		.replaceAll("&ldquo", "“")
		.replaceAll("&&rdquo", "”")		
		.replaceAll("&sbquo", "?");
//		?  版权 &copy;  &#169;  
//		?  注册商标  &reg;  &#174;  
	}	

	/**
	 * 删除字符串中的Tab、换行符、Html空格 
	 */	
	public static String del_Tab_LineSeparator(String str) {
		if (str == null) {
			return null;
		}
		
		return  str.replaceAll("&nbsp;", "")
			.replaceAll(String.valueOf((char)9), "")
			.replaceAll(String.valueOf((char)10), " ")
			.replaceAll(String.valueOf((char)13), "").trim(); 		
	}	

	/**
	 * 用反射的方法替换对象中的字符串字段值中的Html特殊标记为相应的字符 
	 */
	@SuppressWarnings("unchecked")
	public static void replaceHtmlChar(Object obj) {
		try {
			Class c = Class.forName(obj.getClass().getName());
			//Method m[] = c.getMethods();
			Field ff[] = c.getDeclaredFields();
			for (int i = 0; i < ff.length; i++) {
				if (ff[i].getType() == String.class) {
					String fieldName = ff[i].getName();
					String methodName = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
					String getMethodName = "get" + methodName; 
					String setMethodName = "set" + methodName;
					 
					Class[] argsClass = new Class[0];
					String fieldValue = (String)c.getMethod(getMethodName,argsClass).invoke(obj, new Object[0]);
					fieldValue = replaceHtmlChar(fieldValue);
							
					argsClass = new Class[1];
					argsClass[0] = String.class;
					c.getMethod(setMethodName,argsClass).invoke(obj, fieldValue);

//					argsClass = new Class[0];
//					fieldValue = (String)c.getMethod(getMethodName,argsClass).invoke(obj, argsClass);					
//					System.err.println(setMethodName + "        " + fieldValue);					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void printMaxFieldSize() {
		Iterator iter = maxFieldSize.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (java.util.Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Integer value = (Integer) entry.getValue();
			logger.warn(key + " := " + value);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void printStringField(Object obj) {
		try {
			System.err.println(obj.getClass().getName() + "  ==============================");
			Class c = Class.forName(obj.getClass().getName());
			Field ff[] = c.getDeclaredFields();
			for (int i = 0; i < ff.length; i++) {
				if (ff[i].getType() == String.class) {
					String fieldName = ff[i].getName();
					String methodName = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
					String getMethodName = "get" + methodName; 
//					String setMethodName = "set" + methodName;
					 
					Class[] argsClass = new Class[0];
					String fieldValue = (String)c.getMethod(getMethodName,argsClass).invoke(obj, new Object[0]);					
					Integer field_length = fieldValue == null ? 0 : fieldValue.length();
					String sOut = String.format("%-25s   -->   length:%-8s   value:%s\n",fieldName,field_length, fieldValue);
					logger.fatal(sOut);
					Integer oldValue = maxFieldSize.get(fieldName);
					if (oldValue == null) oldValue = 0;
					if (field_length > oldValue) {
						maxFieldSize.put(fieldName, field_length);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void printField(Object obj) {
		try {
			System.err.println(obj.getClass().getName() + "  ==============================");
			Class c = Class.forName(obj.getClass().getName());
			Field ff[] = c.getDeclaredFields();
			for (int i = 0; i < ff.length; i++) {
					String fieldName = ff[i].getName();
					String methodName = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
					String getMethodName = "get" + methodName; 
					 
					Class[] argsClass = new Class[0];
					Object fieldValue = c.getMethod(getMethodName,argsClass).invoke(obj, new Object[0]);
					System.err.printf("%-25s   -->   %s\n",fieldName, fieldValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 字符串中的所有Html标记 
	 */	
	public static String del_HtmlTag(String str) {		
		if (str == null) {
			return null;
		}
		
		String tag = "";
		String result = str;
		int beginIndex = str.indexOf("<"); 
		int endIndex = str.indexOf(">")+1;
		if ((beginIndex > -1) || (endIndex > 1)) {
			tag = str.substring(beginIndex, endIndex);
			result = str.replace(tag, "");
			result = del_HtmlTag(result);
		} else {
			return str;
		}
		
		return result;
	}	
	
	public static String deleteTag(String sOld){
		if (sOld == null) {
			return null;
		}
		String sNew = sOld;
		sNew = replaceSymbol(sOld, "<.*?>", " ");
		return sNew;
	}

	public static String deleteJavaScriptTag(String sOld){
		sOld = sOld.replaceAll("SCRIPT", "script");
		String sNew = sOld;
		sNew = replaceSymbol(sOld, "<script.*?>.*?</script>", "");
		return sNew;
	}
	
	public static String replaceSymbol(String sIn, String sSyb, String sReplace) {
		return sIn.replaceAll(sSyb, sReplace).trim();		
//		jregex.Pattern p = new jregex.Pattern(sSyb);
//		Replacer r = p.replacer(sReplace);
//		sIn = r.replace(sIn);
//		return sIn.trim();
	}	
	
//	public synchronized Integer getHorseId(String horseName) {
//		if ((horseName == null) || (horseName.trim().equals(""))) {
//			return null;
//		}
//		
//		String sql = "select HorseID from dbo.Horse where TypeRace = 5 and HorseName = '" + 
//			horseName.replaceAll("'", "''") + "'";
//		Long longId = getId(sql); 
//		Integer id = longId == null ? null : longId.intValue();
//		if (id != null) {
//			return id;
//		}
//
//		sql = "select HorseID from dbo.HorseMapping where TypeRace = 5 and HorseName = '" + 
//			horseName.replaceAll("'", "''") + "'";
//		longId = getId(sql); 
//		id = longId == null ? null : longId.intValue();
//		if (id != null) {
//			return id;
//		}
//		
//		longId = getMaxId("dbo.Horse", "HorseID");
//		id = longId == null ? null : longId.intValue();
//			
//		Horse newHorse = new Horse();
//		newHorse.setHorseId(id);
//		newHorse.setHorseName(horseName);
//		newHorse.setTypeRace(Short.valueOf("5"));
//		try {
//			DBAccess.create(newHorse);
//		} catch (Exception e) {
//			logger.error("DBAccess.create(newHorse) error horseName = " + horseName, e);
//			return null;
//		}
//		return id;
//	}

	public static void main(String arg[]) {
//		System.err.println(Utils.removeParenthesis1("CROL KG(HK (dd))"));
		String sOut = String.format("%-25s   -->   length:%-5s   value:%s\n","fieldName",7, "fieldValue");
		System.err.println(sOut);
	}
}
