package com.ctnf.client.activities.checkout;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.QueryLocationIndex;
import com.ctnf.client.Resources;
import com.ctnf.client.activities.search.SearchPlace;
import com.ctnf.client.utils.Utils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CheckoutViewImpl extends Composite implements CheckoutView{

	private static CheckoutViewImplUiBinder uiBinder = GWT
			.create(CheckoutViewImplUiBinder.class);

	interface CheckoutViewImplUiBinder extends
			UiBinder<Widget, CheckoutViewImpl> {
	}

	@UiField(provided = true)
    Image imageLogoCompareTicketsNow;
	
	private int secondsCounter = 3;
	
	@UiField Paragraph paragraph;
	
	private String brokerUrl = null;
	
	private CheckoutPresenter presenter;
	
	public CheckoutViewImpl() {
		
		this.imageLogoCompareTicketsNow = new Image(Resources.instance.compareticketsnow());

		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setPresenter(CheckoutPresenter presenter){
		this.presenter = presenter;
	}
	
	@Override
	public void init()
	{
		this.secondsCounter = 3;
		
		this.brokerUrl = presenter.getBrokerUrl().replaceFirst("http://","").replaceFirst("https://","");
		
		paragraph.setHTML("<H3>Redirecting to "+ this.brokerUrl +" in "+secondsCounter+" sec</H3>");
				
		Utils.setTitle("Compare Tickets Now | Checkout");
		Utils.setDescription("Redirecting to "+ this.brokerUrl );

		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
		{
		    @Override
		    public void execute(){
		    	
		    	 Timer timer = new Timer() 
		    	 {
		    		 @Override
		    	     public void run() 
		    	     {
		    			if(secondsCounter==1){
		    				
		    				this.cancel();
		    				
		    				presenter.checkout();
		    				
//		    				Modal modal = new Modal();
//							modal.setTitle("In beta version site checkout is not enabled");
//							modal.setDataKeyboard(true);
//							modal.show();
//							presenter.goTo(new SearchPlace(QueryLocationIndex.city,presenter.getFactory().getSearchLocation(),true));
		    			}
		    			
		    			secondsCounter--;
		    			paragraph.setHTML("<H3>Redirecting to "+ brokerUrl +" in "+secondsCounter+" sec</H3>");
		    	     }
		    	 };
		    	 
		    	 timer.scheduleRepeating(1000);
		    }
		});
	}
}
