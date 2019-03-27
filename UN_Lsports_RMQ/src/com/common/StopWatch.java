/*
 * Created on 2005-4-19
 * 
 * @author Alan
 * 
 */
package com.common;

import java.util.Date;
import java.util.Hashtable;

/**
 * Created on 2005-4-19
 * 
 * @author Alan
 * 
 */
public class StopWatch {
	private static Date preTime;
	private static Hashtable ht = null;
	@SuppressWarnings("unchecked")
	public static void SetTimeStart(String timekey)
	{
		preTime = new Date();
		if(ht==null)ht=new Hashtable();
		if(ht.containsKey(timekey))ht.remove(timekey);
		ht.put(timekey, preTime);
	}
	public static long GetTimeSpan(String timekey)
	{
		if(ht!=null&&ht.size()!=0)
			return new Date().getTime()- ((Date)ht.get(timekey)).getTime();
		else
			return 0;
	}
	public static void main(String[] args) {
		StopWatch.SetTimeStart("hello");
		for(int i=0;i<1000;i++)
			System.out.println(i);
		System.out.println(StopWatch.GetTimeSpan("hello"));
	}
	
}
