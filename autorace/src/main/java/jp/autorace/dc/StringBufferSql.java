package jp.autorace.dc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringBufferSql
{
	DateFormat DF_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	StringBuffer sb = new StringBuffer();
	
	public StringBufferSql()
	{
		
	}
	
	public void add(Object obj)
	{
		if(obj == null)
		{
			sb.append("null,");
			return;
		}
		
		String type = obj.getClass().getName();
		if(type.equals("java.lang.String"))
			sb.append("N'"+obj.toString()+"'");
		else if(type.equals("java.util.Date"))
			sb.append("'"+DF_yyyy_MM_dd_HH_mm_ss.format((Date)obj)+"'");
		else
			sb.append(obj.toString());
		
		sb.append(",");
	}	
	
	public void add(int obj)
	{
		sb.append(obj+",");
	}	
	
	public void add(long obj)
	{
		sb.append(obj+",");
	}
	
	public void add(boolean obj)
	{
		sb.append(obj==true?1:0).append(",");
	}
	
	public String toString()
	{		
		String sql = sb.toString();
		if(sql.length()>0)
			sql = sql.substring(0, sql.length()-1);
		return sql;
	}

}
