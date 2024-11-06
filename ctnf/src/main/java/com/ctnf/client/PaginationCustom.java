package com.ctnf.client;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Pagination;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.SimplePager;

public class PaginationCustom extends Pagination{

	@Override
	public void rebuild(final SimplePager pager) 
	{	
		int setDisplayLimitNum = 10;
		
		clear();

        if (pager.getPageCount() == 0) 
        {
            return;
        }
		
	    final int currentPage = pager.getPage();
		
	    int middle = setDisplayLimitNum / 2;
		
		int start = 0;
		
		int end = setDisplayLimitNum;
		
		if ( currentPage > middle ) 
		{
			start = currentPage - middle;
			
			end = currentPage + middle;
		}

		//Show the page numbers
		for (int i = start; i < end && i < pager.getPageCount(); i++) 
		{
			final int index = i;
			
			final AnchorListItem page = new AnchorListItem(String.valueOf(index+1));
		
			page.addClickHandler(new ClickHandler() 
			{
				@Override
				public void onClick(final ClickEvent event) 
				{
					pager.setPage(index);
				} 	
			});
			
			if (i == pager.getPage()) 
			{
				page.setActive(true);
				add(page);
			}
		}
		
	
		
		
		//Previous link
		final AnchorListItem prev = addPreviousLink();
		
		prev.addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(final ClickEvent event) 
			{	
				pager.setPage(currentPage - 1);
			}
		});
		
		prev.setEnabled(pager.hasPreviousPage());
		
		
		//Next link
		final AnchorListItem next = addNextLink();
		
		next.addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				pager.setPage(currentPage + 1);
			}
		});
		
		next.setEnabled(pager.hasNextPage());
		
	}
}
		
		
		
		