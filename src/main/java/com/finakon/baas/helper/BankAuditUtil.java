package com.finakon.baas.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link BankAuditUtil} class contains common utility methods.
 * 
 * @author amit.patel
 * @version 1.0
 * 
 */
public class BankAuditUtil {
	

	/**
	 * This method is used to validate the empty or null string
	 * 
	 * @param str
	 *            specify the string that will be validated
	 * @return {@link Boolean} class's object with <code>true</code> or
	 *         <code>false</code> value
	 **/
	public static Boolean isEmptyString(String str) {
		Boolean flag = true;
		if (str != null) {
			String trimedStr = str.trim();

			if (trimedStr.length() > 0) {
				flag = false;
			}
		}
		return flag;
	}

	
	
	/**
	 * This method is used to check the given string and regex matches or not
	 * 
	 * @param regexStr
	 *            specify the regular expression
	 * @param str
	 *            specify the string that will be matched with regular
	 *            expression
	 * @return {@link Boolean} class's object with <code>true</code> or
	 *         <code>false</code> value
	 **/
	public static Boolean isRegexTrue(String regexStr, String str) {
		if (!isEmptyString(str)) {
			Pattern pattern = Pattern.compile(regexStr);
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		} else {
			return false;
		}
	}
	

	/**
	 * This method used to write files
	 * 
	 * @param content
	 *            specify the file content that will be written
	 * @param filesDirectory
	 *            specify the directory where file will be placed
	 * @param fileName
	 *            specify the name of the file
	 * @throws IOException specify the exception that may occur during file writing 
	 **/
	public static void writeFile(byte[] content, String filesDirectory, String fileName) throws IOException {

		File file = new File(filesDirectory);

		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(filesDirectory + File.separator + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}

	
	
	
	/**
	 * This method is used to read the file as base64String from given path
	 * 
	 * 
	 * @param path
	 *            specify the path of the file to be read
	 * @return {@link String} specify the file content
	 **/
	public static String readFile(String path) {
		Path filepath;
		try {
			filepath = Paths.get(path);
		} catch (Exception e) {
			return null;
		}
		byte[] data;
		try {
			if (Files.exists(filepath)) {
				data = Files.readAllBytes(filepath);

				String base64String = Base64.getEncoder().encodeToString(data);
				return base64String;

			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;

	}

	/**
	 * This method is used to read the file  from given path
	 * 
	 * 
	 * @param path
	 *            specify the path of the file to be read
	 * @return {@link String} specify the file content
	 **/
	public static byte[] readFileBytes(String path) {
		System.out.println(path);
		Path filepath = Paths.get(path);
		byte[] data;
		try {
			if (Files.exists(filepath)) {
				data = Files.readAllBytes(filepath);
				return data;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}


	

	public static Date parseDateTime(String dateString) {
	    if (dateString == null) return null;
	    
	    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	    if (dateString.contains("T")) dateString = dateString.replace('T', ' ');
	    if (dateString.contains("Z")) dateString = dateString.replace("Z", "+0000");
	    else
	        dateString = dateString.substring(0, dateString.lastIndexOf(':')) + dateString.substring(dateString.lastIndexOf(':')+1);
	    try {
	        return fmt.parse(dateString);
	    }
	    catch (ParseException e) {
	       System.out.println("Could not parse datetime: " + dateString);
	        return null;
	    }
	}
	
	
	public static Date parseDate(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	//String dateStr = "2018-11-01T05:59:33.000Z";
    	Date date =null;
    	try {
    	     date= format.parse(dateStr);
    	   // System.out.println(date.toGMTString());
    	} catch (ParseException e) {
    	    e.printStackTrace();
    	}
		return date;
	}
	

	public static Date parseStringToDate(String dateString) {
	    if (dateString == null) return null;
	    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	    try {
	        return fmt.parse(dateString);
	    }
	    catch (ParseException e) {
	       System.out.println("Could not parse datetime: " + dateString);
	        return null;
	    }
	    
	}
	
	
	public static String parseDateToString(Date d) { 
		String str = "";
		if(d!=null) {
			try {
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				str = fmt.format(d);
			} catch (Exception e) {
				System.out.println("Could not parse datetime: " + d);				
			}
		}
		return str;
	}
	
	
	public static boolean isThisDateValid(String dateToValidate, String dateFromat) throws Exception{
		boolean flag=false;
		//Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);
		try {	//if not valid, it will throw ParseException
			//date = sdf.parse(dateToValidate);
			sdf.parse(dateToValidate);
			flag=true;
		} catch(Exception exception){
			    exception.getMessage();
				throw exception;
		}
		return flag;
	}
	
public static String getFileExtension(String fileName) {
    if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
    return fileName.substring(fileName.lastIndexOf(".")+1);
    else return "";
}


public static Map<String,String> getMap(String str) {
	Map<String,String> map=new HashMap<>();
	String[] strKeys=null;
	String[] strKeyValues=null;
	try {
		if(str!=null) {
			if(str.lastIndexOf(",")>0) {
				//System.out.println("input str:"+str); 
				strKeys=str.split(",");
				for (String string : strKeys) {
					//System.out.println("input string:"+string); 
					if(string.lastIndexOf("=")>0) {
						strKeyValues=string.split("=");
						//System.out.println("key :"+strKeyValues[0]+" values :"+strKeyValues[1]);
						map.put(strKeyValues[0], strKeyValues[1]);
					}
				}
			}
		}
	}catch (Exception e) {
		e.printStackTrace();
		System.out.println(e.getMessage());
	}
	return map; 
}

	public static Boolean isNumber(String str) {
		Boolean flag = true;
		try {
			if (str != null) {
				if (str.length() > 0) {
					long i= Long.parseLong(str);
				}
			}
		}catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static Map<String, LocalDate> getFinancialYearDates(Date businessDate) {
		
		Map<String, LocalDate> financialYearMap = new HashMap<>();
		try {
			Calendar calendar=Calendar.getInstance();
			if(businessDate != null) calendar.setTime(businessDate);
			Integer year=null;
			if( calendar.get(Calendar.MONTH)>=4){
				year=calendar.get(Calendar.YEAR);
			}else{
				year=calendar.get(Calendar.YEAR)-1;
			}
			LocalDate finStartDate = LocalDate.of(year, 04, 1);
			LocalDate finEndDate = LocalDate.of(year+1, 03, 31);
			financialYearMap.put("FIN_YR_START_DATE", finStartDate);
			financialYearMap.put("FIN_YR_END_DATE", finEndDate);
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		return financialYearMap;
	}
	
	public static String parseDateToStringToCreateFileName(Date d) { 
		String str = "";
		if(d!=null) {
			try {
				SimpleDateFormat fmt = new SimpleDateFormat("E_yyyy_MM_dd_HH_mm_ss");
				str = fmt.format(d);
			} catch (Exception e) {
				System.out.println("Could not parse datetime: " + d);				
			}
		}
		return str;
	}
	
	public static String parseDateToStringddMMMyyyy(Date d) { 
		String str = "";
		if(d!=null) {
			try {
				SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
				str = fmt.format(d);
			} catch (Exception e) {
				System.out.println("Could not parse datetime: " + d);				
			}
		}
		return str;
	}
	
	public static String parseStringDateToStringDateddMMyyyy(String dateYYYYMMDD) { 
		String strDateTime = "";
		if(dateYYYYMMDD!=null) {
			try {
				String strDate = "2013-02-21";

			    DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
			    Date da = (Date)inputFormatter.parse(dateYYYYMMDD);
			    //System.out.println("==Date is ==" + da);

			    DateFormat outputFormatter = new SimpleDateFormat("dd-MM-yyyy");
			    strDateTime = outputFormatter.format(da);
			    //System.out.println("==String date is : " + strDateTime);
			} catch (Exception e) {
				System.out.println("Could not parse datetime: " + dateYYYYMMDD);				
			}
		}
		return strDateTime;
	}
	
	public static String parseDateToStringHHmmssddMMyyyy(Date d) { 
		String str = "";
		if(d!=null) {
			try {
				SimpleDateFormat fmt = new SimpleDateFormat("HHmmssddMMyyyy");
				str = fmt.format(d);
			} catch (Exception e) {
				System.out.println("Could not parse datetime: " + d);				
			}
		}
		return str;
	}
	
	public static boolean isEmailAddressValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                            "A-Z]{2,7}$";
                              
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
	
	public static String integerToRomanNumeral(int input) {
	    if (input < 1 || input > 3999)
	        return "Invalid Roman Number Value";
	    String s = "";
	    while (input >= 1000) {
	        s += "M";
	        input -= 1000;        }
	    while (input >= 900) {
	        s += "CM";
	        input -= 900;
	    }
	    while (input >= 500) {
	        s += "D";
	        input -= 500;
	    }
	    while (input >= 400) {
	        s += "CD";
	        input -= 400;
	    }
	    while (input >= 100) {
	        s += "C";
	        input -= 100;
	    }
	    while (input >= 90) {
	        s += "XC";
	        input -= 90;
	    }
	    while (input >= 50) {
	        s += "L";
	        input -= 50;
	    }
	    while (input >= 40) {
	        s += "XL";
	        input -= 40;
	    }
	    while (input >= 10) {
	        s += "X";
	        input -= 10;
	    }
	    while (input >= 9) {
	        s += "IX";
	        input -= 9;
	    }
	    while (input >= 5) {
	        s += "V";
	        input -= 5;
	    }
	    while (input >= 4) {
	        s += "IV";
	        input -= 4;
	    }
	    while (input >= 1) {
	        s += "I";
	        input -= 1;
	    }    
	    return s;
	}


}
