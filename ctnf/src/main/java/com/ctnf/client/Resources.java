package com.ctnf.client;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {

	public static final Resources instance =  GWT.create(Resources.class);

	@Source("com/ctnf/client/resource/images/compareticketsnow.png")
	ImageResource compareticketsnow();

	@Source("com/ctnf/client/resource/images/ticketcity.png")
	ImageResource ticketcity();

	@Source("com/ctnf/client/resource/images/tndirect.png")
	ImageResource tndirect();

	@Source("com/ctnf/client/resource/bootstrap-datepicker3-1.5.1.min.cache.css")
	TextResource datepickercss();
	@Source("com/ctnf/client/resource/bootstrap-datepicker-1.5.1.min.cache.js")
	TextResource datepicker();
	
	@Source("com/ctnf/client/resource/bootstrap-select-1.10.0.min.cache.js")
	TextResource select();
	@Source("com/ctnf/client/resource/bootstrap-select-1.10.0.min.cache.css")
	TextResource selectcss();

	@Source("com/ctnf/client/resource/jquery.hammer.min.js")
	TextResource hammer();
	
	@Source("com/ctnf/client/resource/jquery.tuMap-min.js")
	TextResource tuMap();
	@Source("com/ctnf/client/resource/tuMap.css")
	TextResource tuMapCss();
	
	
	@Source("com/ctnf/client/resource/font-awesome-4.5.0.min.cache.css")
	TextResource fontcss();

	@Source("com/ctnf/client/resource/bootstrap-3.3.6.min.cache.js")
	TextResource bootstrap();
	@Source("com/ctnf/client/resource/bootstrap-3.3.6.min.cache.css")
	TextResource bootstrapcss();
	
	@Source("com/ctnf/client/resource/bootstrap-notify-3.1.3.min.cache.js")
	TextResource notify_();
	
	@Source("com/ctnf/client/resource/bootstrap-notify-custom.min.cache.css")
	TextResource notifycss();

	@Source("com/ctnf/client/resource/jquery-1.12.0.min.cache.js")
	TextResource jQuery();

	@Source("com/ctnf/client/resource/theme.css")
	TextResource themecss();

	@Source("com/ctnf/client/resource/Ctn.css")
	TextResource ctncss();


//		
//		my theme —>
//		<stylesheet src="css/bootstrap-theme-3.3.6.min.cache.css"/>

	
	
	
	
	
//	https://github.com/gwtbootstrap3/gwtbootstrap3-extras/issues/310

//	 @ImageOptions(height=80 , width=7, flipRtl = true)
//	@Source("com/ctnf/client/resources/images/background.1.jpg")
//	ImageResource background1();
//
//	 @ImageOptions(height=80 , width=80, flipRtl = true)
//		@Source("com/ctnf/client/resources/images/background.4.jpg")
//		ImageResource background4();
	
//	@Source("resources/css/bootstrap.css")
//    CssResource bootstrap();
	
}
