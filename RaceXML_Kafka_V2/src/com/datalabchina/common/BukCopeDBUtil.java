package com.datalabchina.common;

import java.io.File;
import java.util.HashMap;

import com.lataw.commons.parsers.DBConfig;

public class BukCopeDBUtil {
	public static String getConnectString(){
		String sConfigPath = System.getProperty("user.dir")+ System.getProperty("file.separator") + "config.xml";
		Config cfg = new Config();//读取配置文件的类
		cfg.loadcfg(new File(sConfigPath));
		String DBPath = cfg.getDbconfigPath()+File.separator;
		String DBName =cfg.getDBName();
		DBConfig dBConfig = new DBConfig(DBName,DBPath);
		HashMap<String, String>  map = dBConfig.getDBConnectionStr();
		//{password=83862909, drivers=net.sourceforge.jtds.jdbc.Driver, user=spider, url=jdbc:jtds:sqlserver://192.168.120.216:1433/FRDB}
		//jdbc:jtds:sqlserver://192.168.120.216:1433;instanceName=inst12;databaseName=FRDB;user=spider;password=83862909
		//jdbc:sqlserver://192.168.28.126;instanceName=inst12;databaseName=FRDB;user=spider;password=83862909
		String connectionString =null;
		if(map.get("instance")!=null){
			connectionString =map.get("url").substring(0,map.get("url").lastIndexOf("/"))+";instanceName="+map.get("instance")+";databaseName="+
					map.get("url").substring(map.get("url").lastIndexOf("/")+1)+";user="+map.get("user")+";password="+map.get("password");
		}else{
			connectionString =map.get("url").substring(0,map.get("url").lastIndexOf("/"))+";databaseName="+
					map.get("url").substring(map.get("url").lastIndexOf("/")+1)+";user="+map.get("user")+";password="+map.get("password");
		}
		return connectionString.replaceAll(":1433","").replace("jtds:","");
	}
	
	public static void main(String[] args) {
		System.out.println(getConnectString());
	}
}
