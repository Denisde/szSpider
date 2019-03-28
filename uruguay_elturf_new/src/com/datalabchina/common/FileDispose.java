package com.datalabchina.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;


public class FileDispose {
	
	private static Logger logger = Logger.getLogger(FileDispose.class.getName());
	
//	private static String charType = "utf-8";
	private static String charType = "ISO-8859-1";
	
	public static boolean bIfExistFile(String sFileName) {
		try {
			File fFile = new File(sFileName);
			if (fFile.exists()) {
				return true;
			}
		} catch (Exception e) {
			logger.error("bIfExistFile ", e);
		}
		return false;
	}
	
	public Vector<File> readLocalFile(String sPathName) {
		Vector<File> vPathFileName = new Vector<File>();
		String sPathFileName = sPathName;
		File fFile = new File(sPathFileName);

		File fPathFileName[] = fFile.listFiles();
		if (fPathFileName==null) return vPathFileName;
		int iFileSize = fPathFileName.length;
		for (int i = 0; i < iFileSize; i++) {
			if(fPathFileName[i].isDirectory()){
				continue;
			}
			vPathFileName.add(fPathFileName[i]);
		}
		return vPathFileName;
	}
	
	public static String readFileFromTxt(String sPathFileName) {
		try {
			File myFile = new File(sPathFileName);
//			if (myFile.exists() == false) return null;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(myFile)));
			String s = in.readLine();
			in.close();
			return s;

		} catch (Exception e) {
			logger.error("getSentURaceIDFromFile", e);
			return null;
		}
	}
	public static String readFile1(String sPathFileName){
		try {
			java.io.File objFile;
			java.io.InputStream is;
			objFile = new java.io.File(sPathFileName);
			ByteArrayOutputStream swapStream  = new ByteArrayOutputStream();
			is = new FileInputStream(objFile);
			try {
				is= new GZIPInputStream(is);
			} catch (Exception e) {
				is = new FileInputStream(objFile);
			}
			byte [] buff = new byte[100];
			int rc=0;
			while((rc= is.read(buff, 0, 100))>0){
				swapStream.write(buff, 0, rc);
			}
			byte [] in_b = swapStream.toByteArray();
			swapStream.close();
			is.close();
			return new String (in_b,charType);
		} catch (Exception e) {
			logger.debug("read file "+sPathFileName+" find wrong\r\n", e);
			return null;
		}
	}
	//读取保存的文件的方法 返回一个字符串
	public static String readFile(String sPathFileName) {
		try{
			java.io.File objFile;
			java.io.InputStream is;
			objFile = new java.io.File(sPathFileName);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			is = new FileInputStream(objFile);
			try {
				is = new GZIPInputStream(is); //如果是Zip
			} catch (Exception e) {
				is = new FileInputStream(objFile);
			}
			byte[] buff = new byte[100];
			int rc = 0;
			while ((rc = is.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] in_b = swapStream.toByteArray();
			swapStream.close();
			is.close();			
			
			return new String(in_b,charType);
		} catch (Exception e) {
			logger.debug("read file "+sPathFileName+" find wrong\r\n", e);
			return null;
		}
	}	
	//创建目录
	public static void createDirectory(String sDirectoryPath) {
		File fFile = new File(sDirectoryPath);
		try{
			if(!fFile.exists()){
				fFile.mkdirs();
			}
		}catch(Exception e){
			logger.error("createDirectory ", e);
		}
	}
	//保存到本地目录
	public static void saveFileContentToLocal(String sFileName, String sFileContent,String chartype) {
		try{
			saveFileContentToLocal(sFileName,sFileContent.getBytes(chartype),true);
		}catch(Exception e){
			logger.error("Write Log ", e);
		}
	}
	
	public static void saveFileContentToZip(String sFileName, String sFileContent,String chartype) {
		try{
			saveFileContentToZip(sFileName,sFileContent.getBytes(chartype),true);
		}catch(Exception e){
			logger.error("Write Log ", e);
		}
	}	
	
	public static void saveFileContentToLocal(String fileName, byte[] bytes,boolean isReplace) {
		//构建保存的文件夹路径
		int i = fileName.lastIndexOf(File.separator);
		if (i > 0) {
			String filePath = fileName.substring(0, i); 
			createDirectory(filePath);
		}
		
		File f = new File(fileName);
		FileOutputStream myos;
		try {
			if(!f.exists()||isReplace){
				myos = new FileOutputStream(f);
				myos.write(bytes,0,bytes.length);
				myos.close();
			}else
				logger.info("file "+fileName+" already exist");
		} catch (java.io.IOException e) {
			logger.error("saveFileContentToLocal two ", e);
		}
	}
	
	public static void saveFileContentToZip(String fileName, byte[] bytes,boolean isReplace) {
		File f = new File(fileName);
		FileOutputStream myos;
		try {
			if(!f.exists()||isReplace){
				myos = new FileOutputStream(f);
				GZIPOutputStream gzipOS = new GZIPOutputStream(myos);
				gzipOS.write(bytes,0,bytes.length);
				gzipOS.close();
			}else
				logger.info("file "+fileName+" already exist");
		} catch (java.io.IOException e) {
			logger.error("saveFileContentToLocal two ", e);
		}
	}	
	
	public static File moveFile(String scrFile, String desDir) {
		File scr = new File(scrFile);
		File dir = new File(desDir);

		if (scr == null || dir == null) {
			logger.warn("a null reference!");
			return null;
		}
		
		if (!scr.exists() || scr.isDirectory() || dir.isFile()) {
			logger.warn("not file or directory or not exist!");
			return null;
		}
		
		createDirectory(desDir);
		File f = new File(dir, scr.getName());
		if (f.exists()) {
			f.delete();
			//logger.warn("target file has existed!");
		}
		scr.renameTo(f);
		//logger.info("move file done: " + scr + " -> " + f);
		return f;
	}	
	
	public static void deleteFile(String sBackupPath,String sFileName) {
		String sPathFileName = sFileName;
		File fFile = new File(sPathFileName);
		if(fFile.isFile()){
//			backupFile(sBackupPath,sFileName);
//			fFile.delete();
			moveFile(sFileName,sBackupPath);
		}	
	}
	
	public static void backupFile(String sBackupPath,String sFileName) {
		String sLastFileName=sFileName.substring(sFileName.lastIndexOf(System.getProperty("file.separator")));
		createDirectory(sBackupPath);
		saveFileContentToLocal(sBackupPath+sLastFileName,readFile(sFileName),charType);
	}
	
	public static List<String> getFile(String sPathName) {
		List<String> vPathFileName = new ArrayList<String>();
		File fFile = new File(sPathName);
		File fPathFileName[] = fFile.listFiles();
		if (fPathFileName==null) return vPathFileName;
		int iFileSize = fPathFileName.length;
		for (int i = 0; i < iFileSize; i++) {
			if(fPathFileName[i].isDirectory()){
				List<String> list = getFile(fPathFileName[i].toString());
				for (String file : list) {
					vPathFileName.add(file);
				}
			} else {
				vPathFileName.add(fPathFileName[i].toString());
			}
		}
		return vPathFileName;
	}
	
	public static List<String> readLocalFileDir(String sPathName) {
		List<String> vPathFileName = new ArrayList<String>();
		String sPathFileName = sPathName;
		File fFile = new File(sPathFileName);
		File fPathFileName[] = fFile.listFiles();
		if (fPathFileName==null) return vPathFileName;
		int iFileSize = fPathFileName.length;
		for (int i = 0; i < iFileSize; i++) {
			if(fPathFileName[i].isDirectory()){
				continue;
			}
			vPathFileName.add(fPathFileName[i].toString());

		}
		return vPathFileName;
	}
	
	public static void deleteDir(String sDirName) {	
		File fFile = new File(sDirName);
		fFile.delete();
	}

	public static void saveFile(String strBody,String fileName) {
		logger.info("save file to " + fileName);
		saveFileContentToLocal(fileName, strBody, charType);
	}
	
	public static void delFile(String fileName) {
		String sPathFileName = fileName;
		File fFile = new File(sPathFileName);
		if(fFile.isFile()){
			fFile.delete();
		}
	}

	public static void backupAndDelFile(String strBody,String fileName,String newFileName) {
		saveFile(strBody,newFileName);
		delFile(fileName);
	}
//	public String readFile(String sPathFileName) {
//	try{
//		java.io.File objFile;
//		java.io.FileInputStream objFileReader;
//		objFile = new java.io.File(sPathFileName);
//		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
//		byte[] buff = new byte[100]; 
//		int rc = 0;
//		objFileReader = new FileInputStream(objFile);
//		while ((rc = objFileReader.read(buff, 0, 100)) > 0) {
//			swapStream.write(buff, 0, rc);
//		}
//		byte[] in_b = swapStream.toByteArray();
//		
//		swapStream.close();
//		objFileReader.close();
//		return new String(in_b,"ISO-8859-1");
//		
//	} catch (Exception e) {
//		logger.debug("read file "+sPathFileName+" find wrong\r\n", e);
//		return null;
//	}
//}
}