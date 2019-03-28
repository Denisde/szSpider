package com.datalabchina.common;

import java.util.Vector;

import org.apache.log4j.Logger;

public class RacePool {
	static Logger logger = Logger.getLogger(RacePool.class.getName());
	private Vector<String> _v = null;
//	private int totalCount = 0;
	
	public RacePool()
	{
		_v = new Vector<String>();
	}
	
	public Vector getVector(){
		return this._v;
	}
	
	public int Size()
	{
		return _v.size();
	}
	
	public synchronized String GetID()
	{
		if(_v.size()>0)
		{
			String id = _v.get(0).toString();		
			logger.info("============ leave num: "+_v.size()+" ::: current id : "+id);
			_v.remove(0);
			return id;
		}
		else
			return null;
	}
	
	public void AddID(String id)
	{
		_v.add(id);
	}
	
	public synchronized void Clear()
	{
		logger.info("###############################################################################################################");
		logger.info("############################################### clear "+_v.size()+" ###############################################");
		_v.clear();
		logger.info("###############################################################################################################");
	}
}
