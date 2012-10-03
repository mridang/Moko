package com.mridang.huntr.helpers;

/*
 * This class is used to convert between human-readable file sizes and bytes
 */
public class SizeConverter {

	/*
	 * This method parses the human-readable file sizes into bytes
	 * Note: this only works for MBs and GBs currently.
	 * 
	 * @param  strsize the string containing the size
	 * @return a Long value containing the size in bytes
	 */
	public static Long parseSize(String strSize) {
		
		try  {
			
			strSize = strSize.trim();
			strSize = strSize.replace(" ", "");
			strSize = strSize.toUpperCase();
			
			try {
				
				Long lngMegaSize = null;
				lngMegaSize = Long.valueOf((long) (Float.parseFloat(strSize.replace("GB", "")) * 1073741824L));
				return lngMegaSize;
				
			} catch (Exception e) {
								
				try {
					
					Long lngGigaSize = null;
					lngGigaSize = Long.valueOf((long) (Float.parseFloat(strSize.replace("MB", "")) * 1048576L));
					return lngGigaSize;
						
				} catch (Exception f) {
					
					return 1048576L;
					
				}
				
			}					

		} catch (Exception e) {
			
			return 1048576L;
			
		}
		
	}
	
	/*
	 * This method prints a human-readable version of a size
	 * 
	 * @param  lngSize  the size to represent as a human-readable string
	 * @return a string version of the size
	 */
	public static String printSize(Long lngSize) {
		
		if (lngSize / 1024L / 1024L < 1024L) {
			return String.valueOf(lngSize / 1024L / 1024L) + " MB";
		} else {
			return String.valueOf(lngSize / 1024F / 1024F / 1024F) + " GB";
		}
		
	}
	
}