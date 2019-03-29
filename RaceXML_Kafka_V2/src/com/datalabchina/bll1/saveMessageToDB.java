package com.datalabchina.bll1;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.CommonMethod;
import com.common.FileDispose;
import com.datalabchina.bll.Parse147;
import com.datalabchina.bll.Parse624;
import com.datalabchina.bll.Parse625;
import com.datalabchina.bll.Parse627;
import com.datalabchina.bll.Parse628;
import com.datalabchina.bll.Parse629;
import com.datalabchina.bll.Parse630;
import com.datalabchina.bll.Parse643;
import com.datalabchina.bll.Parse653;
import com.datalabchina.bll.Parse656;
import com.datalabchina.bll.ParseCPJC45;
import com.datalabchina.bll.ParseCPJC49;
import com.datalabchina.bll.ParseCPJS14;
import com.datalabchina.bll.ParseLiveOdds51Bll;
import com.datalabchina.bll.ParseLivePlace172;
import com.datalabchina.bll.ParseNews24Bll;
import com.datalabchina.bll.ParseOfficialPayouts20Bll;
import com.datalabchina.bll.ParseOfficialRaceResults21Bll;
import com.datalabchina.bll.ParseRaceInformation04Bll;
import com.datalabchina.bll.ParseReunion03Bll;
import com.datalabchina.bll.ParseRj02;
import com.datalabchina.bll.ParseRunners06Bll;

public class saveMessageToDB implements Runnable{

	private static Logger logger = Logger.getLogger(saveMessageToDB.class);
	
	@Override
	public void run() {
		
	}
	public static void saveToDBbyPath(String filePath){
		List<String> fileNamelist = FileDispose.readLocalFileDir(filePath);
		String fileName =null;
		for(int i=0;i<fileNamelist.size();i++) {
			fileName = fileNamelist.get(i);
			saveToDB(fileName);
		}
	}
	
	public static void saveToDB(String fileName){
		String message = FileDispose.readFile(fileName);
		try {
			String filePathName = CommonMethod.getValueByPatter(message, "<type_document>(.*?)</type_document>").trim();
			//Enjeux par pari par cheval
			if(filePathName.equals("Enjeux par pari par cheval")) {
				String code_Pari = CommonMethod.getValueByPatter(message, "<code_pari>(.*?)</code_pari>").trim();
				if(code_Pari.equals("9")) {
					Parse624.parseFile(fileName);
				}else if(code_Pari.equals("11")) {
					Parse627.parseFile(fileName);
				}else if(code_Pari.equals("13")) {
					Parse628.parseFile(fileName);
				}else if(code_Pari.equals("20")) {
					Parse656.parseFile(fileName);
				}else if(code_Pari.equals("26")) {
					Parse625.parseFile(fileName);
				}else if(code_Pari.equals("39")) {
					Parse643.parseFile(fileName);
				}else if(code_Pari.equals("40")) {
					Parse630.parseFile(fileName);
				}else if(code_Pari.equals("27")) {
					Parse629.parseFile(fileName);
				}else if(code_Pari.equals("1")) {
					Parse653.parseFile(fileName);
				}else if(code_Pari.equals("2")) {
					Parse653.parseFile(fileName);
				}else {
					logger.error("Enjeux par pari par cheval  ERROR Code ------------------->"+code_Pari);
				}
			}else if(filePathName.equals("Course")) {
				ParseRaceInformation04Bll.parseFile(fileName);
			}else if(filePathName.equals("Course arrivee")) {
				ParseOfficialRaceResults21Bll.parseFile(fileName);
			}else if(filePathName.equals("partants")) {
				ParseRunners06Bll.parseFile(fileName);
			}else if(filePathName.equals("Rapports definitifs")) {
				ParseOfficialPayouts20Bll.parseFile(fileName);
			}else if(filePathName.equals("Réunion")||filePathName.equals("R?union")) {
				ParseReunion03Bll.parseFile(fileName);
			}else if(filePathName.equals("Combinaisons plus jouees")) {
				String code_Pari = CommonMethod.getValueByPatter(message, "<code_pari>(.*?)</code_pari>").trim();
				if(code_Pari.equals("9")) {
					Parse147.parseFile(fileName);
				}else if(code_Pari.equals("3")) {
					ParseCPJC45.parseFile(fileName);
				}else if(code_Pari.equals("5")) {
					ParseCPJC49.parseFile(fileName);
				}else {
					logger.error("Combinaisons plus jouees ERROR Code ------------------->"+code_Pari);
				}
			}else if(filePathName.equals("Combinaisons plus jouees Simple Gagnant")) {
				Parse147.parseFile(fileName);
			}else if(filePathName.indexOf("Combinaisons plus jouees Simple plac")>-1) {
				ParseCPJS14.parseFile(fileName);
			}else if(filePathName.equals("Non partants")) {
				ParseNews24Bll.parseFile(fileName);
			}else if(filePathName.equals("Rapports probables Simple Gagnant")) {
				ParseLiveOdds51Bll.parseFile(fileName);
			}else if(filePathName.equals("Rapports probables Simple Gagnant international")) {
				ParseLiveOdds51Bll.parseFile(fileName);
			}else if(filePathName.indexOf("Rapports probables Simple plac")>-1) {
				ParseLivePlace172.parseFile(fileName);
			}else if(filePathName.equals("Réunion du jour")||filePathName.equals("R?union du jour")) {
				ParseRj02.parseFile(fileName);
			}else {
				logger.error("ERROR fileName  ------------------->"+fileName);
			}
		} catch (Exception e) {
			logger.error("fileName "+fileName+" parse Error !!!!!!!!!!!!!!!!!!!",e);
		}
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		saveMessageToDB.saveToDB("D:\\Denis\\PMU XPRO APP\\20190302\\Rapports probables Simple Gagnant\\2019030205_00_071551499264328.xml");
	}
}
