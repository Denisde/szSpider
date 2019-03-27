/*
 * Created on 2005-4-18
 * 
 * @author Alan
 * 
 */
package com.common;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Created on 2005-4-18
 * 
 * @author Alan
 * 
 */
public class SpiderConfig {
	static Logger logger = Logger.getLogger(SpiderConfig.class.getName());
	private static Document doc;
	private static SAXBuilder sb;
	private static XMLOutputter xo;
	
	public static void configure(String configfile)
	{		
		try{
			sb = new SAXBuilder();
			doc = sb.build(configfile);
			xo = new XMLOutputter();
		}
		catch(Exception e)
		{
			logger.error("configure", e);
		}
	}
	
	public static Document GetDoc()
	{
		return doc;
	}
	
	private static Element getLoginElement()
	{
		return doc.getRootElement().getChild("login");
	}
	
	public static int GetLoginRepeatNum()
	{
		int loginRepeatNum = 3;
		if(SpiderConfig.getLoginElement().getChild("repeatNum")!=null)
			loginRepeatNum = Integer.parseInt(SpiderConfig.getLoginElement().getChild("repeatNum").getAttributeValue("value"));
		return loginRepeatNum;
	}
	public static void out(List list)
	{
		try{
			xo.output(list, System.out);
		}
		catch(Exception e)
		{
			logger.error("out", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Vector GetLoginPreUrl()
	{
		List list = SpiderConfig.getLoginElement().getChild("preUrl").getChildren();
		Iterator i = list.iterator();
		Vector urls = new Vector();
		while(i.hasNext())
		{
			Element el = (Element) i.next();
			urls.add(el.getAttributeValue("value"));
		}
		return urls;
	}
	
	@SuppressWarnings("unchecked")
	public static Vector GetLoginAfterUrl()
	{
		List list = SpiderConfig.getLoginElement().getChild("afterUrl").getChildren();
		Iterator i = list.iterator();
		Vector urls = new Vector();
		while(i.hasNext())
		{
			Element el = (Element) i.next();
			urls.add(el.getAttributeValue("value"));
		}
		return urls;
	}
	
	public static String GetAction()
	{
		return SpiderConfig.getLoginElement().getChild("form").getChild("action").getAttributeValue("value");
	}
	
	public static String GetBaseUrl()
	{
		return doc.getRootElement().getChild("baseUrl").getAttributeValue("value");
	}
	
	public static String GetCheckStr()
	{
		return SpiderConfig.getLoginElement().getChild("checkString").getAttributeValue("value");
	}
	
	public static String GetLoginOutStr()
	{
		return SpiderConfig.getLoginElement().getChild("loginout").getAttributeValue("value");
	}
	
	public static List GetInput()
	{
		return SpiderConfig.getLoginElement().getChild("form").getChild("input").getChildren();
	}
	
	//get page url info
	private static Element getObjUrlElement()
	{
		return doc.getRootElement().getChild("objUrl");
	}
	
	public static Element GetUrlElement(String urlname)
	{
		Iterator it = SpiderConfig.getObjUrlElement().getChildren("url").iterator();
		while (it.hasNext()) {
	        Element et = (Element) it.next();
		    String name = et.getAttributeValue("name");
			if(name.equals(urlname))
				return et.getChild("page");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Hashtable GetPageHash()
	{
		Iterator it = SpiderConfig.getObjUrlElement().getChildren("url").iterator();
		Hashtable ht = new Hashtable();
		while (it.hasNext()) {
	        Element et = (Element) it.next();
		    String name = et.getAttributeValue("name");
		    String value = et.getAttributeValue("value");			
			ht.put(name, value);
		}
		return ht;
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		SpiderConfig.configure("spider.xml");
		SpiderConfig.out(SpiderConfig.GetInput());
//		System.out.println(XmlConfig.GetLoginRepeatNum()+"");
//		 List mylist = XmlConfig.GetLoginPreUrl();
//	     Iterator i = mylist.iterator();
//	      xo.output(servlet.getChildren("form"),System.out);
//	      while (i.hasNext()) {
//		Vector v = XmlConfig.GetLoginPreUrl();
//		for(int i=0;i<v.size();i++)
//			logger.debug(v.get(i).toString());
	}
}
