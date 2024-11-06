package com.ctnl;

import java.util.ArrayList;

public class Test {

	public static String newBold1(String text,String subText,String startTag,String endTag)
	{		
		String[] subTextPieces = subText.split(" ");
		
		for(String subTextPiece : subTextPieces)
		{
			text = text.replace(subTextPiece, startTag + subTextPiece + endTag);
		}
	
		return text;
	}

	public static void main(String[] args) {
		
		System.out.println(newBold1("a bc b d","a b","<#>","</#>").replace("#","bold"));
	
	}
	

}
