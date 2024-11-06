package com.ctnf.client;

import com.ctnc.shared.QueryLocationIndex;
import com.ctnf.client.activities.compare.filter.CompareFilter;
import com.ctnf.client.activities.generic.GenericPlace;
import com.ctnf.client.activities.generic.Page;
import com.ctnf.client.activities.search.SearchActivity;
import com.ctnf.client.activities.search.SearchPlace;
import com.ctnf.client.activities.search.SearchPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

public class LoadResources {

	public static void compareFilter(Factory factory) {
		new CompareFilter(factory);		
	}
	
	public static void code(final Factory factory) {
		
		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
//				new CompareFilter(factory);
				GWT.log("GWT.runAsync for CompareFilter code preloaded");
			}
			@Override
			public void onFailure(Throwable reason) {
				factory.getPlaceController().goTo(new GenericPlace(Page.error));
			}
		});

		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				
					
				factory.getSearchView(null);
				
				SearchPresenter presenter = new SearchActivity(new SearchPlace(QueryLocationIndex.city, factory.getSearchLocation(), true), factory);
				
				factory.getSearchView(presenter);
				
				GWT.log("GWT.runAsync for SearchView code preloaded");
			}

			@Override
			public void onFailure(Throwable reason) {
				factory.getPlaceController().goTo(new GenericPlace(Page.error));
			}
		});

		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
								
				factory.getCompareView(com.ctnf.client.utils.Utils.getDeviceType(Window.getClientWidth()));
				GWT.log("GWT.runAsync for CompareView code preloaded");
			}

			@Override
			public void onFailure(Throwable reason) {
				factory.getPlaceController().goTo(new GenericPlace(Page.error));
			}
		});
	}
	

	
	public static void js() {

		ScriptInjector.fromString(
				com.ctnf.client.Resources.instance.jQuery().getText()).
				setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();
		
		ScriptInjector.fromString(
				com.ctnf.client.Resources.instance.bootstrap().getText()).
				setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();

		//--
		
		ScriptInjector.fromString(
				com.ctnf.client.Resources.instance.datepicker().getText()).
				setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();
		
		ScriptInjector.fromString(
				com.ctnf.client.Resources.instance.select().getText()).
				setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();
		
		ScriptInjector.fromString(
				com.ctnf.client.Resources.instance.notify_().getText()).
				setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();
		
		ScriptInjector.fromString(
				com.ctnf.client.Resources.instance.hammer().getText()).
				setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();
		
		ScriptInjector.fromString(
				com.ctnf.client.Resources.instance.tuMap().getText()).
				setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();

	}
	
	public static void css()
	{
		
//		Scheduler.get().scheduleDeferred(new Command() {
//			@Override
//			public void execute() {
//				StyleInjector.inject(com.ctnf.client.Resources.instance.tuMapCss().getText());
//			}
//		});
		
		StyleInjector.inject(com.ctnf.client.Resources.instance.themecss().getText());

//		Scheduler.get().scheduleDeferred(new Command() {
//			@Override
//			public void execute() {				
//				StyleInjector.inject(com.ctnf.client.Resources.instance.themecss().getText());
//			}
//		});
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {				
				StyleInjector.inject(com.ctnf.client.Resources.instance.ctncss().getText());
			}
		});
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {				
				StyleInjector.inject(com.ctnf.client.Resources.instance.fontcss().getText());
			}
		});
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {				
				StyleInjector.inject(com.ctnf.client.Resources.instance.bootstrapcss().getText());
			}
		});
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {				
				StyleInjector.inject(com.ctnf.client.Resources.instance.datepickercss().getText());

			}
		});
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {				
				StyleInjector.inject(com.ctnf.client.Resources.instance.datepickercss().getText());

			}
		});
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				StyleInjector.inject(com.ctnf.client.Resources.instance.selectcss().getText());
			}
		});
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				StyleInjector.inject(com.ctnf.client.Resources.instance.notifycss().getText());
			}
		});
		
	}
	
	public static void injectCss(String href) 
	{
		LinkElement link = Document.get().createLinkElement();
		link.setRel("stylesheet");
		link.setType("text/css");
		link.setHref(href);
		Document.get().getElementsByTagName("head").getItem(0).appendChild(link);
	}	
	
	private static native boolean isjQueryLoaded() /*-{
		return (typeof $wnd['jQuery'] !== 'undefined');
	}-*/;
}
