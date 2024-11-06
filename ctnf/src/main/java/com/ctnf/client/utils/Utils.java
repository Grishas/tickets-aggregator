package com.ctnf.client.utils;
import java.util.ArrayList;
import java.util.List;
import com.ctnf.client.DeviceType;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class Utils {
		
	public static void setTitle(String title)
	{
		Document.get().setTitle(title);	
	}
	
	public static void setDescription(String description)
	{
		NodeList<Element> tags = Document.get().getElementsByTagName("meta");
	    for (int i = 0; i < tags.getLength(); i++) {
	        MetaElement metaTag = ((MetaElement) tags.getItem(i));
	        if (metaTag.getName().equals("description")) {
	            metaTag.setContent(description);
	        }
	    }	
	}
	

	
	
	
	
	
	
	public static DeviceType getDeviceType(int width)
	{
		if( width < 768 ){
			return DeviceType.Phone;
		}
		else if( width >= 768 && width < 992){
			return DeviceType.Tablet;
		}
		else if( width >= 992 && width < 1200 ){
			return DeviceType.MediumDesktop;
		}
		else if( width >= 1200 ){
			return DeviceType.LargeDesktop;
		}
		
		return null;
	}
	
	public static final NumberFormat usdNumberFormat = NumberFormat.getSimpleCurrencyFormat("USD");

	public static int getMaxNumber(List<Integer> numbers)
	{
		int maxNumber = 0;
		
		for(Integer number : numbers)
		{
			if( maxNumber < number )
			{
				maxNumber = number;
			}
		}
		
		return maxNumber;
	}
	
	public static String capitalize(String text,String delimiter)
	{
		StringBuffer result = new StringBuffer();
		
		String[] tmp = text.split( delimiter );
		
		for(String subText : tmp)
		{ 
			result.append(capitalize(subText) +" ");
		}
		
		return result.toString().trim();
	}
	
	public static String capitalize(String word)
	{
		return Character.toUpperCase( word.charAt(0) ) + word.substring(1);
	}
	
	public static String getCitiesCloseAsString(List<String> citiesClose,boolean normalizeForToken)
	{
		StringBuilder result = new StringBuilder();
		
		for(String city : citiesClose)
		{
			if(normalizeForToken){
				city = Utils.normalizeTokenText(city);
			}
			else
			{
				city = Utils.capitalize(city," ");
			}
			
			result.append(city+",");
		}
		
		return result.deleteCharAt(result.length()-1).toString();
	}
	
	public static String normalizeTokenText(String text)
	{
		text = text.replaceAll("[^A-Za-z0-9]"," ").trim();
		
		text = text.replaceAll("\\s+", "-");
	
		return text.toLowerCase();
	}
	
	private static void setPosition(String text,String subText,int offset,ArrayList<Integer> positions)
	{
		Integer index = text.toLowerCase().indexOf(subText.toLowerCase());
		
		//-1 not found 
		if(index==-1)return;
		
		String subSubText = text.substring(index+subText.length());
		
		positions.add(index+offset);
		
		offset+=index+subText.length();
		
		setPosition(subSubText,subText,offset,positions);
	}
	
	private static SafeHtmlBuilder addSubText(String text,int fromPosition,int toPosition,SafeHtmlBuilder safeHtmlBuilder)
	{
		for(int index=fromPosition;index<toPosition;index++)
		{
			safeHtmlBuilder.appendEscaped(String.valueOf(text.charAt(index)));			
		}
		
		return safeHtmlBuilder;
	}
	
	private static SafeHtml generateBoldText(String text,String subText,ArrayList<Integer> positions,String startTag,String endTag) 
	{
		int textLen = text.length();
		int subTextLen = subText.length();
		
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		
		if(textLen==subTextLen)
		{
			safeHtmlBuilder.appendHtmlConstant(startTag);
			safeHtmlBuilder.appendEscaped(text);
			safeHtmlBuilder.appendHtmlConstant(endTag);
			return safeHtmlBuilder.toSafeHtml();
		}
		
		for(int textIndex=0;textIndex<textLen;textIndex++)
		{
			char textChar = text.charAt(textIndex);
			
			if(positions.contains(textIndex))
			{
				safeHtmlBuilder.appendHtmlConstant(startTag);
				
				safeHtmlBuilder = addSubText(text,textIndex,textIndex+subTextLen,safeHtmlBuilder);
				
				safeHtmlBuilder.appendHtmlConstant(endTag);
				
				//for end string case
				if( (textIndex+subTextLen) <= textLen )
				{
					textIndex=textIndex+subTextLen-1;
				}
			}
			else
			{
				safeHtmlBuilder.appendEscaped(String.valueOf(textChar));
			}
		}
		
		return safeHtmlBuilder.toSafeHtml();
	}
	
	
	public static SafeHtml newBold(String text,String subText,String tagName)
	{
		if( subText==null || subText.length()==0 )
		{
			return SafeHtmlUtils.fromTrustedString(text);
		}
		
		//---
		
		String textTmp = text.replaceAll("\\s+", " ").trim().toLowerCase();
		
		textTmp = capitalize(textTmp, " ");
		
		String subTextTmp = subText.replaceAll("\\s+", " ").trim().toLowerCase();
		
		subTextTmp = capitalize(subTextTmp, " ");

		//---
		
		if(textTmp.equals(subTextTmp))
		{
			SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
			safeHtmlBuilder.appendHtmlConstant("<"+tagName+">");
			safeHtmlBuilder.appendEscaped(text);
			safeHtmlBuilder.appendHtmlConstant("</"+tagName+">");
			
			return safeHtmlBuilder.toSafeHtml();	
		}
		
		//---
		
		String[] subTextPieces = subText.split(" ");
		
		for(String subTextPiece : subTextPieces)
		{
			if(subTextPiece.equals(""))
			{
				continue;
			}
			
			text = text.replace( subTextPiece, "<###>" + subTextPiece + "</###>" );
		}
	
		text = text.replace("###", tagName);
		
		return SafeHtmlUtils.fromTrustedString(text);
	
	}
	
	public static SafeHtml bold(String text,String subText,String startTag,String endTag)
	{	
		if(subText==null||subText.equals(""))
		{
			SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
			safeHtmlBuilder.appendEscaped(text);
			return safeHtmlBuilder.toSafeHtml();
		}
		
		ArrayList<Integer> positions = new ArrayList<Integer>(); 
		setPosition(text,subText,0,positions);
		
		if(positions.size()==0){
			SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
			safeHtmlBuilder.appendEscaped(text);
			return safeHtmlBuilder.toSafeHtml();
		}

		SafeHtml safeHtml = generateBoldText(text,subText,positions,startTag,endTag);
				
		return safeHtml;
	}
	
}













