package com.ctnf.client.activities.generic;
import com.ctnf.client.Factory;
import com.ctnf.client.utils.Utils;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class GenericActivity extends AbstractActivity implements GenericPresenter{

	private final Factory factory;
	private GenericView view = null;
	private GenericPlace place = null; 
	
	public GenericActivity(GenericPlace place, final Factory factory){
		this.factory = factory;
		this.place = place;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		
		String title = "compareticketsnow.com";
	
		switch (this.place.getPage()) 
		{
			case about:
				this.view = this.factory.getAboutView(this);
				title = "About";
				Utils.setDescription("About");
				break;
			case brokers:
				this.view = this.factory.getBrokerView(this);	
				title = "For partners";
				Utils.setDescription("For partners");
				break;
			case contact:
				this.view = this.factory.getContactView(this);	
				title = "Contact";
				Utils.setDescription("Contact: hi@compareticketsnow.com");
				break;
			case error:
				this.view = this.factory.getErrorView(this);
				title = "Error";
				Utils.setDescription("Error occured");
				
				break;
			case policy:
				this.view = this.factory.getPolicyView(this);
				title = "Policy";
				Utils.setDescription("Policy");
				break;
			case termsOfUse:
				this.view = this.factory.getTermsOfUseView(this);
				title = "Terms of use";
				Utils.setDescription("Terms of use");
				break;
			default:
				this.view = this.factory.getErrorView(this);	
				title = "Error";
				Utils.setDescription("Error occured");
				break;
		}
		
		this.factory.setAddThis(this.place.getUrl(),title);
		
		Utils.setTitle("Compare Tickets Now | "+title);
		
		panel.setWidget(this.view.asWidget());
		
		this.view.setMenu();
	}

	@Override
	public void goTo(Place place) {
		this.factory.getPlaceController().goTo(place);
	}
	@Override
	public Factory getFactory() {
		return this.factory;
	}
	
	
}
