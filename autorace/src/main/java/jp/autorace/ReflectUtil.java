package jp.autorace;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReflectUtil {
	
	
	/**
	 * 此方法需保证 实体类的属性字段与对应表的字段顺序一致 才可用
	 * */
	public static String  method(Object obj){
		String sql ="";
	    try {
	    	Class<?> clazz = obj.getClass();
	    	Field[] fields = obj.getClass().getDeclaredFields();//获得属性
	    	for (Field field : fields) {
	    		PropertyDescriptor pd = new PropertyDescriptor(field.getName(),clazz);
	    		String fileType = pd.getPropertyType().getSimpleName();
	    		Method getMethod = pd.getReadMethod();//获得get方法
	    		Object o = getMethod.invoke(obj);//执行get方法返回一个Object
	    		if(fileType.lastIndexOf("Id")>-1)
	    			sql+= method1(o);
	    		else if(fileType.contains("Date"))
	    			sql+= o==null?"NULL,":"N'"+parseDate((Date)o)+"',";
	    		else if(fileType.contains("Boolean")){
	    			if(o==null||o.toString().equals("false"))
	    				sql+= "'0',";
	    			else
	    				sql+= "'1',";
	    		}
	    		else
	    			sql+= o==null?"NULL,":"N'"+o.toString().replaceAll("'", "''")+"',";
	    	}
	    	sql =sql.substring(0,sql.length()-1);
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	    return sql;
	}
	
	
	public static String  method1(Object obj){
		String sql = " ";
	    try {
	    	Class<?> clazz = obj.getClass();
	    	Field[] fields = obj.getClass().getDeclaredFields();//获得属性
	    	for (Field field : fields) {
	    		PropertyDescriptor pd = new PropertyDescriptor(field.getName(),clazz);
	    		Method getMethod = pd.getReadMethod();//获得get方法
	    		Object o = getMethod.invoke(obj);//执行get方法返回一个Object
	    		String fileType = pd.getPropertyType().getSimpleName();
	    		if(fileType.contains("Date"))
	    			sql+= o==null?"NULL,":"N'"+parseDate((Date)o)+"',";
	    		else
	    			sql+= o==null?"NULL,":"N'"+o.toString().replaceAll("'", "''")+"',";
	    	}
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	    return sql;
	}
	
	public static String parseDate(Date date){
		if(date==null)return null;
		DateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String str = df_yyyyMMdd.format(date);
		if(str.startsWith("11")||str.startsWith("22")||str.startsWith("33")||str.startsWith("44")||str.startsWith("55")
				||str.startsWith("66")||str.startsWith("77")||str.startsWith("88")||str.startsWith("99"))
			return null;
		return str;
	}
}
