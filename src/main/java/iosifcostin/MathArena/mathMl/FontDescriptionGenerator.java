package iosifcostin.MathArena.mathMl;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontDescriptionGenerator {
	
	//private static File folder = new File("./fonts/dejavu/");
	private static File folder = new File("C:/temp/test");
	

	public static void main(String[] args) throws FontFormatException, IOException{
		
		File[] files = folder.listFiles();
		
		for(int w = 0; w<files.length; w++){
			File f = files[w];
			if(!f.getName().toLowerCase().endsWith(".ttf")) continue;
			
			Font font = Font.createFont(Font.TRUETYPE_FONT, f);
			
			boolean isItalic = font.getFontName().toLowerCase().indexOf("italic")>-1;
			if(!isItalic){
				isItalic = font.getFontName().toLowerCase().indexOf("oblique")>-1;
			}
			
			System.out.print( "\n" + f.getName() + "|58|" + font.getFamily() + ";");
			for(int i=0; i<=0xFF; i++){
				
				StringBuffer sb = new StringBuffer();
				
	
				int firstRange = -1;
				for(int j=0; j<=0xFF; j++){
					
//					if(i==32 && j>=12 && j<=15) continue;
//					if(i==32 && j>=40 && j<=46) continue;
//					if(i==32 && j>=106 && j<=111) continue;
					
					int v = i*0x100 + j;
					if(v<0x20) continue;
					
					char c = (char)v;
					
					boolean canDisplay = font.canDisplay(c);
					if(canDisplay){
						if(firstRange==-1){
							firstRange = j;
						}
					}else{
						if(firstRange>-1){
							int lastRange = j-1;
							if(lastRange!=firstRange){
								sb.append(getHex(firstRange) + "-" + getHex(lastRange) + ",");
							}else{
								sb.append(getHex(firstRange)+ ",");
							}
						}
							
						firstRange = -1;	
					}
				}
				if(firstRange>-1){
					int lastRange = 0xff;
					if(lastRange!=firstRange){
						sb.append(getHex(firstRange) + "-" + getHex(lastRange) + ",");
					}else{
						sb.append(getHex(firstRange)+ ",");
					}
				}
	
				String value= sb.toString();
				if(value.length()>0){
					value = value.substring(0, value.length()-1);
					System.out.print(getHex(i) + ":" + value+";");
				}
			}
			
		}		
	}

	
	private static String getHex(int i){
		String code = Integer.toHexString( i );
		code = code.toUpperCase();
		return code;
	}

}
