package it.polimi.tiw.utils;

import java.sql.Date;

public class DateParser {
	public static Date convertToSQLDate(String stringDate) throws IllegalArgumentException{
		String[] s = stringDate.split(" ");
		String date = s[2] + "-"; //year
		
		date = date + convertMonth(s[0]) + "-"; //month
		
		date = date + s[1].split(",")[0]; //day
		
		Date d = null;
		
		try {
			d = Date.valueOf(date);
		}catch(Exception e){ throw new IllegalArgumentException("Date can't be parsed");}
		
		return d;
	}
	
	private static String convertMonth(String month) {
		return switch(month) {
		case "Jan" -> "1";
		case "gen" -> "1";
		case "Feb" -> "2";
		case "feb" -> "2";
		case "Mar" -> "3";
		case "mar" -> "3";
		case "Apr" -> "4";
		case "apr" -> "4";
		case "May" -> "5";
		case "mag" -> "5";
		case "Jun" -> "6";
		case "giu" -> "6";
		case "Jul" -> "7";
		case "lug" -> "7";
		case "Aug" -> "8";
		case "ago" -> "8";
		case "Sep" -> "9";
		case "set" -> "9";
		case "Oct" -> "10";
		case "ott" -> "10";
		case "Nov" -> "11";
		case "nov" -> "11";
		case "Dec" -> "12";
		case "dic" -> "12";
		default -> "";
		};
	}

}
