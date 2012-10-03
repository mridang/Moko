package com.mridang.huntr.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This is the class used to parse dates from strings
 */
public class DateConverter {

	/*
	 * Function to parse date from strings. It tries a variety of date formats
	 * and guess the right one
	 * 
	 * @param strDate the date in the string
	 * 
	 * @return a Date type as a the parsed date
	 */
	public static Date parseDatefromString(String strDate) {

		try {
			
		      strDate = strDate.replace("-", ".").replace("/", ".");
		      strDate = strDate.replaceAll("(?:st|nd|rd|th),", " ");
		      strDate = strDate.replace(",", "");
			
			try {
				
				DateFormat dftSimple = new SimpleDateFormat("dd.MM.yyyy");
				Date datDate = (Date) dftSimple.parse(strDate);
				return datDate;
				
			} catch (ParseException e) {
								
				try {
					
					DateFormat dftComplex = new SimpleDateFormat("MMM .yyyy");
					Date datDate = (Date) dftComplex.parse(strDate);
					return datDate;
						
				} catch (ParseException f) {
					
					return new Date();
					
				}
				
			}
			
		} catch (Exception e) {

			return new Date();
			
		}
		
	}
	
    /*
     * This method parses the human-readable date formats into a date
     * Note: this only works for min/hour/day/week/month/year only
     *
     * @param  strDate the string containing the date
     * @return A date value containing the date
     */
    @SuppressWarnings("serial")
	public static Date parseHumanDate(String strDate) {

        try  {
  
            Map<String, Integer> fields = new HashMap<String, Integer>() {{
                put("min.", Calendar.MINUTE);
                put("hour", Calendar.HOUR);
                put("day", Calendar.DATE);
                put("week", Calendar.WEEK_OF_YEAR);
                put("month", Calendar.MONTH);
                put("year", Calendar.YEAR);
            }};
  
            Matcher m = Pattern.compile("(\\d+)\\s(.*?)s?").matcher(strDate);
  
            if (m.matches()) {
  
                Integer intAmount = Integer.parseInt(m.group(1));
                String strUnit = m.group(2);
  
                Calendar calDate = Calendar.getInstance();
                calDate.add(fields.get(strUnit), -intAmount);
  
                return calDate.getTime();
  
            } else {
  
               return new Date();
  
            }
  
        } catch (Exception e) {
  
            return new Date();
  
        }

    }

}