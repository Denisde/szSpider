package com.lataw.commons.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.lataw.commons.encrypt.Algorithm;



/**
 * 
 * @author wanguobin
 *
 */
public class DBConfig {

	
	static Logger logger = Logger.getLogger(DBConfig.class.getName());

	private String cfgPath="";
	private String dbName="";
	private File file;
	private String encryMethod="";
	private String keyCode="";
	private boolean doEncrypt=false;
	private HashMap<String,String> hpCon=new HashMap<String,String>();
		
//	public DBConfig(String datebaseName) {
//		dbName=datebaseName;
//		file=new File(cfgName);
//		loadcfg();
//	}
	
	public DBConfig(String datebaseName,String configPath) {
		dbName=datebaseName;
		cfgPath = configPath;
		file=new File(cfgPath);
		loadcfg();
	}

	
	

	private void loadcfg() {
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new FileInputStream(file)));
			Document doc = parser.getDocument();

			Node child = doc.getFirstChild();
			int ii = 0;
			while (child != null) {
				ii++;
				if (child.getNodeName().equals("configuration")) {
					parseConfig(child);

				}
				child = child.getNextSibling();
				//System.out.println(ii);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.equals(e.getMessage());
			
		}
		
	}

	private void parseConfig(Node node) {
		try {
			
		
		NodeList nodeList = node.getChildNodes();

		//Algorithm alg = Algorithm.getInstance(algorithmType);
		Algorithm alg =null;
		for (int i = 0; i < nodeList.getLength(); i++) {

			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();
			//System.out.println(nodeName+"--------------------------------------------------------");
			//parseDBcon(child.getChildNodes());
			
			for (int p = 0; p < child.getChildNodes().getLength(); p++) {
				Node childNode = child.getChildNodes().item(p);
				if(childNode.getFirstChild() == null)continue;
				//System.out.println(node.getAttributes().getNamedItem("name").getNodeValue());
				String name=childNode.getAttributes().getNamedItem("name").getNodeValue();
				String values=childNode.getFirstChild().getNodeValue();
				if(name.equals("EncryMethod")){
					encryMethod = values;
					
				}
				else if(name.equals("KeyCode")) keyCode = values;
				else if(name.equals("Flag")) doEncrypt = values.equals("1")?true:false;
				
				if(doEncrypt && nodeName.equals("connectionString") && !name.equals("Flag")){
					if(alg==null)alg = Algorithm.getInstance(encryMethod);
					//if(alg==null)alg = Algorithm.getInstance("1");
					String decryptedName=name;
					if(name.toUpperCase().indexOf("DB")<0){
						decryptedName=alg.decrypting(name, keyCode);
					}
					
					String decryptedVal=alg.decrypting(values, keyCode);
					logger.debug("decrypted:"+decryptedVal);
					hpCon.put(decryptedName.toUpperCase(),decryptedVal);
					logger.debug("name="+decryptedName+"|value="+decryptedVal);
				}else{
					hpCon.put(name.toUpperCase(),values);
					logger.debug("name="+name+"|value="+values);
				}
				//node.getFirstChild().getTextContent();
				//node.getNodeName()
				
				
				

			}
			
			//System.out.println("--------------------------------------------------------"+nodeName);
		}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
/**
 * 
 * @return HashMap:the String by call key in[drivers,url,instance,user,password]
 */
	
	public HashMap<String,String> getDBConnectionStr(){
		HashMap<String,String> hmOut=new HashMap<String,String>();
		String strCon=hpCon.get(dbName.toUpperCase());
		if(strCon==null||strCon.equals(""))return null;
		String strArray[]=strCon.split(";");
		//drivers=net.sourceforge.jtds.jdbc.Driver;
		//url=jdbc:jtds:sqlserver://192.168.60.246:1433/FRDB;
		//instance=sql2005;
		//user=spider;
		//password=pwd!@#123
		for(int i=0;i<strArray.length;i++){
			
			String tmp=strArray[i];
			if(tmp.indexOf("=")<0)continue;
			String values=tmp.substring(tmp.indexOf("=")+1);
			if(tmp.indexOf("drivers")>-1) hmOut.put("drivers",values);
			else if(tmp.indexOf("url")>-1) hmOut.put("url",values);
			else if(tmp.indexOf("instance")>-1) hmOut.put("instance",values);
			else if(tmp.indexOf("user")>-1) hmOut.put("user",values);
			else if(tmp.indexOf("password")>-1) hmOut.put("password",values);
			
		}
		strArray=null;
		
		return hmOut;
	}
	
	
}