package com.common;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Copyright:   Copyright (c)
 * Company:     DataLibChina
 * @author:     Eric Sheng
 * @version:    1.0
 *
 * Modification History:
 * Date		  Author		Version		Description
 * ------------------------------------------------------------------
 * 2006-9-21  Eric Sheng		1.0			Initialize Version.
 */
public class Config {
	static Logger logger = Logger.getLogger(Config.class.getName());
	private static Document doc;
	private static SAXBuilder sb;
	//private static XMLOutputter xo;
	
	public static void configure(String configfile){		
		try{
			sb = new SAXBuilder();
			doc = sb.build(configfile);
			//xo = new XMLOutputter();
		}catch(Exception e){
			logger.error("configure ", e);
		}
	}
	
    public static boolean saveChange(String configfile) {
        XMLOutputter out;
        try {
            out = new XMLOutputter();
            out.output(doc, new FileOutputStream(configfile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public static Document getDoc(){
		return doc;
	}
	
	public static ArrayList getBllNoteValue(){
		ArrayList<String[]> bll = new ArrayList<String[]>();
		
		try {
			List list = doc.getRootElement().getChildren("BLL");
			Iterator it = list.iterator();
			while (it.hasNext()) {
				String[] bllText = new String[8];
				Element e = (Element)it.next();
				bllText[0] = e.getChildText("BLLNO");
				bllText[1] = e.getChildText("BLLNAME");
				bllText[2] = e.getChildText("ISEXTRACTFROMWEB");
				bllText[3] = e.getChildText("ISEXTRACTFROMLOCAL");
				bllText[4] = e.getChildText("SAVEFILEPATH");
				bllText[5] = e.getChildText("RUN");
				bllText[6] = e.getChildText("ISDELETEFILE");
				bllText[7] = e.getChildText("BAKFILEPATH");
				bll.add(bllText);
			}
		} catch (Exception e) {
			logger.error("getBllNO error: ", e);
		}
		return bll;
	}

	public static String[] getEMailValue(){
		String[] eMail = new String[5];
		try {
			List list = doc.getRootElement().getChildren("MAIL");
			Iterator it = list.iterator();
			if (it.hasNext()) {
				Element e = (Element)it.next();
				eMail[0] = e.getChildText("RETRIEVEADDR");
				eMail[1] = e.getChildText("SENDADDR");
				eMail[2] = e.getChildText("SENDPASSWORD");
				eMail[3] = e.getChildText("SENDHOSTSMTP");
				eMail[4] = e.getChildText("ISSEND");
			}
		} catch (Exception e) {
			logger.error("getEMailValue error: ", e);
		}
		return eMail;
	}
	
	public static String getValue(String name){
		try {
			return doc.getRootElement().getChildTextTrim(name).replaceAll("<!\\[CDATA\\[", "").replaceAll("\\]\\]>", "").trim();
		} catch (Exception e) {
			logger.error("getValue from xml error: ", e);
			return null;
		}
	}
	
	public static Integer getIntegerValue(String name){
		try {
			return Integer.parseInt(doc.getRootElement().getChildTextTrim(name)); 
		} catch (Exception e) {
			logger.error("getValue from xml error: ", e);
			return null;
		}
	}
	
	public static int getIntValue(String name){
		try {
			return Integer.parseInt(doc.getRootElement().getChildTextTrim(name)); 
		} catch (Exception e) {
			logger.error("getValue from xml error: ", e);
			return 0;
		}
	}	
	
	public static void setValue(String name,String value){
		try {
			doc.getRootElement().getChild(name).setText(value);
			//Integer.parseInt(doc.getRootElement().getChildTextTrim(name)); 
		} catch (Exception e) {
			logger.error("getValue from xml error: ", e);
		}
	}
	
	public static void main(String[] args) {
		Config.configure("config.xml");
		System.err.println(Config.getValue("SLEEP_MINUTE")); 
		System.err.println(getEMailValue()[0]);
	}
}
