/**
 * 
 */
package com.b510.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.b510.common.Common;
import com.b510.excel.util.Util;
import com.b510.excel.vo.Student;

/**
 * @author Hongten
 * @created 2014-5-20
 */
public class ReadExcel {
    
    /**
     * read the Excel file
     * @param path the path of the Excel file
     * @return
     * @throws IOException
     */
    public List<Student> readExcel(String path) throws IOException {
        if (path == null || Common.EMPTY.equals(path)) {
            return null;
        } else {
            String postfix = Util.getPostfix(path);
            if (!Common.EMPTY.equals(postfix)) {
                if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                    return readXls(path);
                } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                    return readXlsx(path);
                }
            } else {
                System.out.println(path + Common.NOT_EXCEL_FILE);
            }
        }
        return null;
    }

    /**
     * Read the Excel 2010
     * @param path the path of the excel file
     * @return
     * @throws IOException
     */
    public List<Student> readXlsx(String path) throws IOException {
        System.out.println(Common.PROCESSING + path);
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        List<Student> list = new ArrayList<Student>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
//            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(4);
            System.out.println("============>Page :"+numSheet+"");
            if (xssfSheet == null) {
                continue;
            }
            String  RaceDate ="";
            // Read the Row
            //Race
            /*RaceDate,TrackName,RaceNo,RaceTime,Title,RaceCondiction,RaceCategory,Distance,AutoStart
             * Prize ...  LeaderFinishTime,BetTypeName,Comment,Going,ExtractTime
             * */
            //Horse 
            /*
             * RaceDate,TrackName,RaceNo,HorseName,ClothNo,ownerName,JockeyName,RawFinishposition,
             * FinishPosition,Winodds,Scratch,Sex,Age,Blinks,HandicapWeight,JockAlloWance,RawLBW,Shoeless,ExtractTime
             * 
             * */
            for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
//                if(rowNum>=5&&xssfRow.getCell(0).toString().length()>0){
                	if(xssfRow.getCell(0).toString().length()>0){
                        if (xssfRow != null) {
                        	System.out.print(xssfRow.getCell(0)+"|");//ClothNo
                        	System.out.print(xssfRow.getCell(1)+"|");//HorseName
                        	System.out.print(xssfRow.getCell(4)+"|");//Barriaw
                        	System.out.print(xssfRow.getCell(5)+"|");//Oeil
                        	System.out.print(xssfRow.getCell(6)+"|");//JockeyName
                        	System.out.print(xssfRow.getCell(7)+"|");//JockeyWeight
                        	System.out.print(xssfRow.getCell(8)+"|");//JockeyAllowance
                        	System.out.print(xssfRow.getCell(9)+"|");//Sex
                        	System.out.print(xssfRow.getCell(10)+"|");//age
                        	System.out.print(xssfRow.getCell(11)+"|");//owner
                        	System.out.print(xssfRow.getCell(14)+"|");//sire
                        	System.out.print(xssfRow.getCell(17)+"|");//dam
                        	System.out.println(xssfRow.getCell(18));//Trainer
                        }
                	}
               if(xssfRow.getCell(2).toString().indexOf("Aankomst / Arrivée")>-1){
            	    for(int i =rowNum+2;i<= xssfSheet.getLastRowNum();i++){
                        XSSFRow xssfRow1 = xssfSheet.getRow(i);
            	    	System.out.print(xssfRow1.getCell(0)+"|");//finishPosition
            	    	System.out.print(xssfRow1.getCell(1)+"|");//HorseName
            	    	System.out.print(xssfRow1.getCell(2)+"|");//Jockey
            	    	System.out.print(xssfRow1.getCell(6)+"|");//JockeyWeight
            	    	System.out.print(xssfRow1.getCell(8));//Poids
            	    	if(xssfRow1.getCell(9).toString().length()>0){
            	    		System.out.print(xssfRow1.getCell(9));//Poids
            	    	}
            	    	if(xssfRow1.getCell(0).toString().length()<0)break;
            	    }
               }
//                if(){
//                	
//                }
//                if (xssfRow != null) {
//                	System.out.println(xssfSheet.getRow(0).getCell(4));
//                	System.out.println(xssfSheet.getRow(0).getCell(5));
//                	System.out.println(xssfSheet.getRow(1).getCell(1));
////                	student = new Student();
////                    XSSFCell no = xssfRow.getCell(0);
////                    XSSFCell name = xssfRow.getCell(1);
////                    XSSFCell age = xssfRow.getCell(2);
////                    XSSFCell score = xssfRow.getCell(3);
////                    student.setNo(getValue(no));
////                    student.setName(getValue(name));
////                    student.setAge(getValue(age));
////                    student.setScore(Float.valueOf(getValue(score)));
////                    list.add(student);
//                }
            }
        }
        return list;
    }

    /**
     * Read the Excel 2003-2007
     * @param path the path of the Excel
     * @return
     * @throws IOException
     */
    public List<Student> readXls(String path) throws IOException {
        System.out.println(Common.PROCESSING + path);
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        Student student = null;
        List<Student> list = new ArrayList<Student>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    student = new Student();
                    HSSFCell no = hssfRow.getCell(0);
                    HSSFCell name = hssfRow.getCell(1);
                    HSSFCell age = hssfRow.getCell(2);
                    HSSFCell score = hssfRow.getCell(3);
                    student.setNo(getValue(no));
                    student.setName(getValue(name));
                    student.setAge(getValue(age));
                    student.setScore(Float.valueOf(getValue(score)));
                    list.add(student);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("static-access")
    private String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            return String.valueOf(xssfRow.getNumericCellValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
}