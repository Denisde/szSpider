package com.common;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.*;
import java.io.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author gbwan
 * @version 1.0
 */
public class ZipTools extends Thread{
	private Hashtable<String ,Integer> htSizes=new Hashtable<String ,Integer>();
	private Hashtable<String ,byte[]> htJarContents=new Hashtable<String ,byte[]>();
	private static Logger logger = Logger.getLogger("ZipTools");
	
	private String sourceFile="";
	private String descJarFile="";
	
	/**
	 * 
	 *
	 */
	public ZipTools(){
			
	}
	/**
	 * 
	 * @param filePath 
	 * @param date
	 */
	public ZipTools(String filePath,String date){
		sourceFile=filePath+File.separator+date;
		descJarFile=filePath+File.separator+date+".zip";
		
	}
	/**
	 * 
	 * @param filePath
	 */
	public ZipTools(String filePath){
		sourceFile=filePath;
		descJarFile=filePath+".zip";
		
	}
	public void run(){
		long lstart=System.currentTimeMillis();
		try {
			if(sourceFile.equals("")||descJarFile.equals("")){
				logger.error("please use Constructor ZipTools(filePath,date) or ZipTools(filePath)");
				return;
			}
			logger.info("file="+sourceFile+"|jarFile="+descJarFile);
			zip(descJarFile,sourceFile);
			if(compareFiles(descJarFile,sourceFile)){
				logger.info(descJarFile +" and "+sourceFile +" are same files, will delete "+sourceFile);
				deleteFile(sourceFile);
			}else{
				logger.info(descJarFile +" and "+sourceFile +" are not same files");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			logger.info(e.getMessage());
			
		}
		 long lend=System.currentTimeMillis();
		 logger.info("end speet time:"+(lend-lstart));
	}
	/**
	 * 
	 * @param zipFileName
	 * @param inputFileName
	 * @throws Exception
	 */
	public boolean compareFiles(String zipFileName, String inputFileName) throws Exception {
		boolean isSame=true;
//		 zip file
		Vector<String> vZipFileName=new Vector<String>();
		ZipFile zf = new ZipFile(zipFileName);
		
		Enumeration e = zf.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) e.nextElement();
			vZipFileName.add(ze.getName());
			
		}
		zf.close();
		
		// the sources file doc
		File f=new File(inputFileName);
		if(!f.exists()){
			f=null;
			return false;
		}
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			for (int i = 0; i < fl.length; i++) {
				
				File tmpFile=fl[i].getAbsoluteFile();
				if(tmpFile.isDirectory()){
					String tmpFileName=fl[i].getName();
					File[] fileChild= tmpFile.listFiles();
					for (int c = 0; c < fileChild.length; c++) {
						File childFile=fileChild[c].getAbsoluteFile();
						if(childFile.isDirectory()){
							logger.error("ZipTools don't support more than 1 level directory in zip, pleas delete manual directory "+inputFileName);
							break;
						}else{
							String targetFileName=tmpFileName+"/"+fileChild[c].getName();
							if(!vZipFileName.contains(targetFileName)){
								logger.info(zipFileName+" is not exist "+targetFileName);
								isSame=false;
								break;
							}
						}
					}
					fileChild=null;
				}else{
					String tmpFileName = fl[i].getName();
				
					logger.info("checking "+tmpFileName);
					if(!vZipFileName.contains(tmpFileName)){
						logger.info(zipFileName+" is not exist "+tmpFileName);
						isSame=false;
						break;
					}
				}
				tmpFile=null;
			
			}
			logger.info("Zip "+fl.length+" files");
			
		} else {
			logger.info(inputFileName+" is not Directory ");
			isSame=false;
		}
		f=null;
		
		
		return isSame;
	}
	public void zip() throws Exception {
		if(sourceFile.equals("")||descJarFile.equals("")){
			logger.error("please use Controller ZipTools(filePath,date) or ZipTools(filePath)");
			return;
		}
		zip(descJarFile,sourceFile);
	}
	/**
	 * 
	 * @param zipFileName
	 * @param inputFileName
	 * @throws Exception
	 */
	public void zip(String zipFileName, String inputFileName) throws Exception {
		//zip(zipFileName, new File(inputFile));
		File inputFile=new File(inputFileName);
		if(!inputFile.exists()){
			logger.info(inputFileName+" is not exist!");
			inputFile=null;
			return;
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		zip(out, inputFile, "");
		logger.info("zip done");
		out.close();
	}
/**
 * 
 * @param out
 * @param f
 * @param base
 * @throws Exception
 */
	private void zip(ZipOutputStream out, File f, String base) throws Exception {
		System.out.println("Zipping  " + f.getName());
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
			logger.info("Zip "+fl.length+" files");
			
		} else {
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int b;
			while ((b = in.read()) != -1)
				out.write(b);
			in.close();
		}
	}
	
	public void unzip()throws Exception {
		if(sourceFile.equals("")||descJarFile.equals("")){
			logger.error("please use Controller ZipTools(filePath,date) or ZipTools(filePath)");
			return;
		}
		unzip(descJarFile,sourceFile);
	}
/**
 * 
 * @param zipFileName
 * @param outputDirectory
 * @throws Exception
 */
	public void unzip(String zipFileName, String outputDirectory)
			throws Exception {
		ZipInputStream in = new ZipInputStream(new FileInputStream(zipFileName));
		ZipEntry z;
		while ((z = in.getNextEntry()) != null) {
			System.out.println("unziping " + z.getName());
			if (z.isDirectory()) {
				String name = z.getName();
				name = name.substring(0, name.length() - 1);
				File f = new File(outputDirectory + File.separator + name);
				f.mkdir();
				logger.info("mkdir " + outputDirectory + File.separator + name);
			}else {
				File f = new File(outputDirectory + File.separator+ z.getName());
				f.createNewFile();
				FileOutputStream out = new FileOutputStream(f);
				int b;
				while ((b = in.read()) != -1)
					out.write(b);
				out.close();
			}
		}
		in.close();
	}
	/**
	 * 
	 * @param jarFileName
	 */
	
	private void extractZip(String jarFileName) {
		try {
			boolean debugOn=true;
			// extracts just sizes only.
			ZipFile zf = new ZipFile(jarFileName);
			Enumeration e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				if (debugOn) {
					//logger.info(dumpZipEntry(ze));
				}
				htSizes.put(ze.getName(), new Integer((int) ze.getSize()));
			}
			zf.close();

			// extract resources and put them into the hashtable.
			FileInputStream fis = new FileInputStream(jarFileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipInputStream zis = new ZipInputStream(bis);
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					logger.info("not found processing resources from sub directory");
					continue;// 
				}
				if (debugOn) {
					logger.debug("ze.getName()=" + ze.getName() + ","
							+ "getSize()=" + ze.getSize());
				}
				int size = (int) ze.getSize();
				// -1 means unknown size.
				if (size == -1) {
					size = ((Integer) htSizes.get(ze.getName())).intValue();
				}
				byte[] b = new byte[(int) size];
				int rb = 0;
				int chunk = 0;
				while (((int) size - rb) > 0) {
					chunk = zis.read(b, rb, (int) size - rb);
					if (chunk == -1) {
						break;
					}
					rb += chunk;
				}
				// add to internal resource hashtable
				htJarContents.put(ze.getName(), b);
				if (debugOn) {
					logger.debug(ze.getName() + " rb=" + rb + ",size="
							+ size + ",csize=" + ze.getCompressedSize());
				}
			}
		} catch (NullPointerException e) {
			logger.info("done.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param jarFileName
	 * @param name
	 * @return byte[]
	 */
	private byte[] getResource(String jarFileName,String name) {
		byte[] bOut=null;
		bOut=htJarContents.get(name);
		if(bOut==null)extractZip(jarFileName);
		bOut=htJarContents.get(name);
		if(bOut==null){
			logger.info("The flag name error in "+jarFileName+", will not unzip file from "+jarFileName);
		}
		return bOut;
		
	}
	
	/**
	 * 
	 * @param fileName
	 */
	public void unZipFileName(String fileName) {
		if(sourceFile.equals("")||descJarFile.equals("")){
			logger.error("please use Controller ZipTools(filePath,date) or ZipTools(filePath)");
			return;
		}
		unZipFileName(this.descJarFile,fileName,this.sourceFile) ;
	}
	/**
	 * 
	 * @param zipFileName
	 * @param fileName
	 * @param outputFileName
	 */
	public void unZipFileName(String zipFileName,String fileName,String outputFileName) {
		byte[] bytes=getResource(zipFileName,fileName);
		if(bytes==null){
			logger.info("The flag name error in "+zipFileName+", will not unzip file from "+zipFileName);
			return;
		}
		File f = new File(outputFileName);
		FileOutputStream myos;
		try {
			myos = new FileOutputStream(f);
			myos.write(bytes,0,bytes.length);
			myos.close();
			
		} catch (java.io.IOException e) {
			logger.error(e.toString());
		}
		
		logger.info("extract "+fileName+" finished");
		
	}
	
	/**
	 * 
	 * @param zipFileName
	 * @param fileName
	 * @return filename content string
	 */
	public String unZipFileName(String zipFileName,String fileName) {
		byte[] bytes=getResource(zipFileName,fileName);
		if(bytes==null){
			logger.info("The flag name error in "+zipFileName+", will not unzip file from "+zipFileName);
			return null;
		}
		logger.info("extract "+fileName+" finished");
		return new String(bytes);
		
		
		
	}
	
	/**
	 * 
	 * @param sPathFileName
	 */
	private void deleteFile(String sPathFileName) {
//		String today=getToday();
//		if(sPathFileName.indexOf(today)>-1){
//			logger.info("Today files will not delete!:"+sPathFileName);
//			return;
//		}
		File fFile = new File(sPathFileName);
		if(!fFile.exists()){
			logger.info(sPathFileName+" is not exist.");
			return;
		}
		if(fFile.isFile()){
			fFile.delete();
		}else if(fFile.isDirectory()){	
			File files[]=fFile.listFiles();
			for(int i=0;i<files.length;i++){
				File fTmp=files[i];
//				if(fTmp.isDirectory()){
//					File fChild[]=fTmp.listFiles();
//					for(int c=0;c<fChild.length;c++){
//						File child=fChild[c];
//						if(child.isDirectory()){
//							logger.error("ZipTools don't support more than 1 level directory in zip, pleas delete manual directory "+sPathFileName);
//							return;
//						}else{
//							System.out.println("deleteing "+child.getName());
//							if(fTmp.getName().indexOf(today)>-1){
//								System.out.println("Today files will not delete!:"+fTmp.getName());
//								return;
//							}
//							child.delete();
//						}
//						child=null;
//					}
//					fTmp.delete();
//					fChild=null;
//				}else{
					System.out.println("deleteing "+fTmp.getName());
//					if(fTmp.getName().indexOf(today)>-1){
//						System.out.println("Today files will not delete!:"+fTmp.getName());
//						return;
//					}
					fTmp.delete();
//				}
				
			}
			fFile.delete();
		}else{
			logger.info(sPathFileName+" has problem.");
		}
		fFile=null;
	}
	
	 /**
	  * 
	  * @return
	  */
		
	private String getToday(){
    	String sOut="";
    	Calendar oGC = new GregorianCalendar();		
		int year=oGC.get(Calendar.YEAR);
		int month=oGC.get(Calendar.MONTH)+1;
		int date=oGC.get(Calendar.DATE);
		sOut=(year<10?"0"+year:year+"") + (month<10?"0"+month:month+"") + (date<10?"0"+date:date+"");
		oGC=null;
    	return sOut;
    	
    }
	

}
