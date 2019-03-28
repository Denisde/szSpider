package com.datalabchina.common;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageTester {

	static PageHelper pageHelper = PageHelper.getPageHelper();
	static CommonDB oCommonDB =new CommonDB();
	static CommonMethod oCommonMethod = new CommonMethod();
	
	public static void main(String args[]) throws Exception {
//        File file = new File("D:\\Denis\\JPBoat\\4194.jpg");
//        File file = new File("D:\\Denis\\JPBoat\\4456.jpg");
//        ImageInputStream is = ImageIO.createImageInputStream(file);
//        Iterator iter = ImageIO.getImageReaders(is);
//
//        if (!iter.hasNext())
//        {
//            System.out.println("Cannot load the specified file "+ file);
//            System.exit(1);
//        }
//        ImageReader imageReader = (ImageReader)iter.next();
//        imageReader.setInput(is);
//
//        BufferedImage image = imageReader.read(0);
//
//        int height = image.getHeight();
//        int width = image.getWidth();
//
//        Map m = new HashMap();
//        for(int i=0; i < width ; i++)
//        {
//            for(int j=0; j < height ; j++)
//            {
//                int rgb = image.getRGB(i, j);
//                int[] rgbArr = getRGBArr(rgb);                
//                // Filter out grays....                
//                if (!isGray(rgbArr)) {                
//                        Integer counter = (Integer) m.get(rgb);   
//                        if (counter == null)
//                            counter = 0;
//                        counter++;                                
//                        m.put(rgb, counter);                
//                }                
//            }
//        }        
//        String colourHex = getMostCommonColour(m);
//        System.out.println(colourHex);
//		getImagePixel("D:\\Denis\\JPBoat\\4194.jpg");
//		System.out.println();
//		List<String> list = oCommonDB.getImagePathList();
//		for(int i=0;i<list.size();i++){
//			//http://www.boatrace.jp/racerphoto/3529.jpg
//			//PlayerImage\3545.jpg
//			String ImagePath = list.get(i).replaceAll("\\\\", "/");
//			String imageUrl =null;
//			if(ImagePath.startsWith("/")||ImagePath.startsWith("\\")){
//				imageUrl = "http://www.boatrace.jp"+ImagePath;
//			}else{
//				imageUrl = "http://www.boatrace.jp/"+ImagePath;
//			}
//			byte[] imagebyte = pageHelper.doGetByte(imageUrl,null);
//			if(imagebyte!=null){
//				String fileName = "D:\\Denis\\JPBoat\\male\\"+oCommonMethod.getValueByPatter(imageUrl, "(\\d{1,})")+".jpg";
//				FileDispose.saveFileContentToLocal(fileName, imagebyte, true);
//			}
//		}
		List<String> imageList  = FileDispose.readLocalFileDir("D:\\Denis\\JPBoat\\Female");
//		List<String> imageList  = FileDispose.readLocalFileDir("D:\\Denis\\JPBoat\\male");
		for(int i=0;i<imageList.size();i++){
			String filePath= imageList.get(i);
			String playerCode = oCommonMethod.getValueByPatter(filePath, "(\\d{1,})");
			String imageSex = getSexByImage(filePath);
			oCommonDB.updSex(imageSex, playerCode);
		}
//		System.out.println(getSexByImage("D:\\Denis\\JPBoat\\male\\4147.jpg"));
//		System.err.println(getSexByImage("D:\\Denis\\JPBoat\\Female\\4714.jpg"));
    }

    /**
     * 根据图片的 rgb 值 确定性别 
     */
    public static String  getSexByImage(String image) {
        int[] rgb = new int[3];
        File file = new File(image);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int count =0;
        int width = bi.getWidth();
        int height = bi.getHeight();
        int minX = bi.getMinX();
        /*
         * rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
         * */
        for(int y = height-15; y < height; y++) {
            for(int x = minX; x < width; x++) {
                //获取包含这个像素的颜色信息的值, int型
                int pixel = bi.getRGB(x, y);
                rgb[0] = (pixel & 0xff0000) >> 16; //r 值
            	rgb[1] = (pixel & 0xff00) >> 8;
            	rgb[2] = (pixel & 0xff);	// b值
//            	if(Math.abs( rgb[0]-255)>30){ //r 值 -255 绝对值>100 的像素个数大于 1000 sex=1 否则 sex =2
            	if(Math.abs( rgb[0]-255)<=30&&Math.abs( rgb[1]-115)<=30&&Math.abs( rgb[2]-180)<=30){ 
            		count++;
            	}
            }
        }
        System.err.println(count);
        if(count>30){
        	return "2";
        }else {
        	return "1";
        }
    }
	
}
