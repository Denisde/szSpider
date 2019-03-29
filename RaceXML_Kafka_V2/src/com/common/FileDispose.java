package com.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

//import common.HBaseSender;

public class FileDispose {

	private static Logger logger = Logger
			.getLogger(FileDispose.class.getName());

//	public static String charType = "utf-8"; // GBK utf-8 ISO-8859-1 gb2312
	public static String charType = "ISO-8859-1"; // GBK utf-8 ISO-8859-1 gb2312

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
	/***********************************************************/
	
//	public static Vector<File> readLocalFile(String sPathName) {
//		Vector<File> vPathFileName = new Vector<File>();
//		try {
//			String sPathFileName = sPathName;
//			File fFile = new File(sPathFileName);
//	
//			File fPathFileName[] = fFile.listFiles();
//			if (fPathFileName == null)
//				return vPathFileName;
//			int iFileSize = fPathFileName.length;
//			for (int i = 0; i < iFileSize; i++) {
//				if (fPathFileName[i].isDirectory()) {
//					continue;
//				}
//				vPathFileName.add(fPathFileName[i]);
//			}
//		} catch (Exception e) {
//			logger.error("readLocalFile ", e);
//		}
//		return vPathFileName;
//	}
//
//	public static Vector<File> readLocalFileDir(String sPathName) {
//		Vector<File> vPathFileName = new Vector<File>();
//		try {
//			String sPathFileName = sPathName;
//			File fFile = new File(sPathFileName);
//			File fPathFileName[] = fFile.listFiles();
//			if (fPathFileName == null)
//				return vPathFileName;
//			int iFileSize = fPathFileName.length;
//			for (int i = 0; i < iFileSize; i++) {
//				if (!fPathFileName[i].isDirectory()) {
//					continue;
//				}
//				vPathFileName.add(fPathFileName[i]);
//	
//			}
//		} catch (Exception e) {
//			logger.error("readLocalFileDir ", e);
//		}		
//		return vPathFileName;
//	}
	
//	public static List<String> getFileList(String dir, boolean dirOnly) {
//		List<String> dirList = new ArrayList<String>();
//		try {
//			File file = new File(dir);
//			if (file.exists() == false) return dirList;
//			File files[] = file.listFiles();
//			int iFileSize = files.length;
//			for (int i = 0; i < iFileSize; i++) {
//				if (dirOnly) {
//					if (files[i].isDirectory()) {
//						dirList.add(files[i].toString());
//					}
//				} else {
//					if (files[i].isFile()) {
//						dirList.add(files[i].toString());
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error("getFileList ", e);
//		}
//		return dirList;
//	}
	
	public static List<String> getFileList(String dir, boolean dirOnly) {
		List<String> dirList = new ArrayList<String>();
		try {
			File file = new File(dir);
			if (file.exists() == false) return dirList;
			File files[] = file.listFiles();
			int iFileSize = files.length;
			for (int i = 0; i < iFileSize; i++) {
				if (dirOnly) {
					if (files[i].isDirectory()) {
						dirList.add(files[i].toString());
					}
				} else {
					if (files[i].isFile()) {
						dirList.add(files[i].toString());
					}
				}
			}
		} catch (Exception e) {
			logger.error("getFileList ", e);
		}
		
		Collections.sort(dirList);
		return dirList;
	}	

	public static List<String> getDirs(String dir) {
		return getFileList(dir, true);
	}
	
	public static List<String> getFiles(String dir) {
		return getFileList(dir, false);
	}
	
	/***********************************************************/
	
	public static void createDirectory(String sDirectoryPath) {
		File fFile = new File(sDirectoryPath);
		try {
			if (!fFile.exists()) {
				fFile.mkdirs();
			}
		} catch (Exception e) {
			logger.error("createDirectory ", e);
		}
	}
	
	public static void deleteDir(String sDirName) {
		try {
			File fFile = new File(sDirName);
			fFile.delete();
		} catch (Exception e) {
			logger.error("deleteDir ", e);
		}		
	}

	public static void delFile(String fileName) {
		try {
			String sPathFileName = fileName;
			File fFile = new File(sPathFileName);
			if (fFile.isFile()) {
				fFile.delete();
			}
		} catch (Exception e) {
			logger.error("delFile ", e);
		}		
	}

	/***********************************************************/
	
//	public static String readFile(String filename) {
//		try {
////			java.io.InputStream is;
//			FileInputStream is = new FileInputStream(filename);
//
//			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
//			byte[] buff = new byte[100];
//			int rc = 0;
//			
//			try {
//				GZIPInputStream ZipIS = new GZIPInputStream(is); // is zip file
//				while ((rc = ZipIS.read(buff, 0, 100)) > 0) {
//					swapStream.write(buff, 0, rc);
//				}
//				ZipIS.close();
//			} catch (Exception e) { //not zip file
//				is = new FileInputStream(filename);
//				while ((rc = is.read(buff, 0, 100)) > 0) {
//					swapStream.write(buff, 0, rc);
//				}
//				is.close();
//			}
//
//			byte[] bit = swapStream.toByteArray();
//			return (new String(bit));
//		} catch (Exception e) {
//			logger.error("readFile ", e);
//			return null;
//		}
//	}
	
	public static String readFile(String filename) {
		java.io.InputStream is = null;
		try {
			is = new FileInputStream(filename);

			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			
			try {
				is = new GZIPInputStream(is); // is zip file
			} catch (Exception e) { //not zip file
				is.close();
				is = new FileInputStream(filename);
			}

			while ((rc = is.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			is.close();
			byte[] bit = swapStream.toByteArray();
			
			return (new String(bit));
		} catch (Exception e) {
			logger.error("readFile ", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}		
	}
	
	public static InputStream readFile4IS(String filename) {
		java.io.InputStream is = null;
		try {
			is = new FileInputStream(filename);

			try {
				is = new GZIPInputStream(is); // is zip file
			} catch (Exception e) { //not zip file
				is = new FileInputStream(filename);
			}

			return is;
		} catch (Exception e) {
			logger.error("readFile ", e);
			return null;
		}		
	}	
	
	public static void saveFile(String strBody, String fileName,
			boolean saveAsZip, String charType) {
		
		try {
//			HBaseSender.saveFileToHBase(fileName, strBody);
		} catch (Exception e) {
			logger.error("", e);
		}
		
		java.io.OutputStream os = null;
		try {
			
			int p = fileName.lastIndexOf(File.separator);
			if (p > 0) {
				String filePath = fileName.substring(0, p);
				createDirectory(filePath);
			}
			
			os = new FileOutputStream(fileName);
			if (saveAsZip) {
				os = new GZIPOutputStream(os);
			}
			os.write(strBody.getBytes(charType));
//			os.close();
		} catch (Exception e) {
			logger.error("saveFile ", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static void saveFile(String strBody, String fileName) {
		saveFile(strBody, fileName, false, charType);
	}

	public static void saveAsZip(String strBody, String fileName) {
		saveFile(strBody, fileName, true, charType);
	}

	public static void saveFile(String strBody, String fileName, String charType) {
		saveFile(strBody, fileName, false, charType);
	}

	public static void saveAsZip(String strBody, String fileName,
			String charType) {
		saveFile(strBody, fileName, true, charType);
	}
	
	
	/***********************************************************/
	
	public static File renameFile(String file, String name) {
		return renameFile(new File(file), name);
	}

	public static File renameFile(File file, String name) {
		File newname;
		if (file == null || !file.exists()) {
			logger.error("File not found!");
			return null;
		}
		if (file.getParent() == null) {
			newname = new File(name);
			file.renameTo(newname);
		} else {
			newname = new File(file.getParentFile(), name);
			file.renameTo(newname);
		}
		logger.info("Rename is done: " + file + " -> " + newname);
		return newname;
	}
	

	public static File moveFile(String scr, String dir) {
		return moveFile(new File(scr), new File(dir));
	}

	public static File moveFile(File scr, File dir) {
		if (scr == null || dir == null) {
			logger.error("a null reference!");
			return null;
		}
		if (!scr.exists() || !dir.exists() || scr.isDirectory() || dir.isFile()) {
			logger.error("not file or directory or not exist!");
			return null;
		}
		File f = new File(dir, scr.getName());
		if (f.exists()) {
			logger.error("target file has existed!");
		}
		scr.renameTo(f);
		logger.info("move file done: " + scr + " -> " + f);
		return f;
	}	

	/***********************************************************/
	
	public static void main(String[] arg) {
		String file = "D:\\browse_index.php";
		String s = readFile(file);
//		System.err.println(s);
		System.err.println("old file length = " + s.length());
		
		saveFile(s, "D:\\a.txt");
		s = readFile("D:\\a.txt");
		System.err.println("new file length = " + s.length());
		
		saveFile(s, "D:\\a.txt");
		s = readFile("D:\\a.txt");
		System.err.println("new file length = " + s.length());
		
		saveFile(s, "D:\\a.txt");
		s = readFile("D:\\a.txt");
		System.err.println("new file length = " + s.length());		
		
//		saveAsZip(s, "D:\\a.txt.zip");
//		s = readFile("D:\\a.txt.zip");
//		System.err.println("new file length = " + s.length());
//		
//		saveAsZip(s, "D:\\a.txt.zip");
//		s = readFile("D:\\a.txt.zip");
//		System.err.println("new file length = " + s.length());
//		
//		saveAsZip(s, "D:\\a.txt.zip");
//		s = readFile("D:\\a.txt.zip");
//		System.err.println("new file length = " + s.length());		
	}
}