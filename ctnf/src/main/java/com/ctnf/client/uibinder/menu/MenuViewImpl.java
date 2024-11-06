package com.ctnf.client.uibinder.menu;
import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.Navbar;
import org.gwtbootstrap3.client.ui.NavbarBrand;
import org.gwtbootstrap3.client.ui.NavbarCollapse;
import org.gwtbootstrap3.client.ui.NavbarHeader;
import com.ctnc.shared.QueryLocationIndex;
import com.ctnf.client.activities.generic.GenericPlace;
import com.ctnf.client.activities.generic.Page;
import com.ctnf.client.activities.search.SearchPlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MenuViewImpl extends Composite implements MenuView {

	private static MenuViewImplUiBinder uiBinder = GWT.create(MenuViewImplUiBinder.class);
	interface MenuViewImplUiBinder extends UiBinder<Widget, MenuViewImpl> {}

	@Override
	protected void onAttach() {
		super.onAttach();
		
		if(presenter.getFactory().getPlaceController().getWhere() instanceof SearchPlace)
		{
			this.anchorListItemSearch.setVisible(false);
		}
		else
		{
			this.anchorListItemSearch.setVisible(true);
		}
		
	}

	//@UiField ListDropDown listDropDownPopular;
	@UiField NavbarHeader navbarHeader;
	@UiField Navbar navbar;
	@UiField NavbarCollapse navbarCollapse;
	@UiField NavbarBrand navbarBrandHome;
	@UiField AnchorListItem anchorListItemSearch;
	@UiField AnchorListItem anchorListItemHistory;
	//@UiField AnchorListItem anchorListItemPopularEvents;
	//@UiField AnchorListItem anchorListItemPopularPerformers;
	//@UiField AnchorListItem anchorListItemPopularVenues;
	@UiField AnchorListItem anchorListItemAbout;
	@UiField AnchorListItem anchorListItemContact;
	@UiField AnchorListItem anchorListItemPolicy;
	@UiField AnchorListItem anchorListItemTermsOfUse;
	//@UiField AnchorButton anchorButtonPopular;

    private MenuPresenter presenter = null;
    
	public MenuViewImpl() {
		
		initWidget(uiBinder.createAndBindUi(this));
		
		this.navbarHeader.addStyleName("menu");
		this.navbar.addStyleName("menu");
		this.navbarCollapse.addStyleName("menu");
		
		//anchorListItemPopularEvents.addStyleName("popular");
		
		

		
//		this.navbar.getElement().setAttribute("style", "background-image:linear-gradient(to left,#337ab7, #337ab7);");
//		this.navbarCollapse.getElement().setAttribute("style", "background-image:linear-gradient(to left,#337ab7, #337ab7);");
//		
//		this.anchorListItemHistory.getElement().setAttribute("style", "background-image:linear-gradient(to left,#337ab7, white);");
//		this.anchorListItemSearch.getElement().setAttribute("style", "background-image:linear-gradient(to left,#337ab7, white);");
//		this.anchorListItemSearch.getElement().setAttribute("style", "background-image:linear-gradient(to left,#337ab7, white);");
//		this.anchorButtonPopular.getElement().setAttribute("style", "background-image:linear-gradient(to left,#337ab7, white);");
//
//		this.anchorListItemAbout.getElement().setAttribute("style", "background-image:linear-gradient(to left,#337ab7, white);");
//
//		
//			this.anchorListItemContact.getElement().setAttribute("style", "background-image:linear-gradient(to left,#337ab7, white);");

		
		//this.anchorListItemContact.getElement().getStyle().setColor("red");
		//this.anchorListItemContact.getElement().setAttribute("style", "  text-decoration-color: #E18728;");
		//this.anchorListItemContact.getElement().getStyle().setBackgroundColor("white");

		
	}
	
	@UiHandler("navbarBrandHome")
	public void onClickNavbarBrandHome(ClickEvent e){
		hideNavbarCollapse();
		presenter.goTo(new SearchPlace(QueryLocationIndex.city,presenter.getFactory().getUserLocation(),true));				
	}
	
	@UiHandler("anchorListItemSearch")
	public void onClickAnchorListItemSearch(ClickEvent e){
		hideNavbarCollapse();
		presenter.goTo(new SearchPlace(QueryLocationIndex.city,presenter.getFactory().getUserLocation(),true));				
	}
	
	@UiHandler("anchorListItemHistory")
	public void onClickAnchorListItemHistory(ClickEvent e){
		hideNavbarCollapse();
		coomingSoon();
	}
	
//	@UiHandler("anchorListItemPopularEvents")
//	public void onClickAnchorListItemPopularEvents(ClickEvent e){
//		hideNavbarCollapse();
//		coomingSoon();
//	}
//	
//	@UiHandler("anchorListItemPopularPerformers")
//	public void onClickAnchorListItemPopularPerformers(ClickEvent e){
//		hideNavbarCollapse();
//		coomingSoon();
//	}
//	
//	@UiHandler("anchorListItemPopularVenues")
//	public void onClickAnchorListItemPopularVenues(ClickEvent e){
//		hideNavbarCollapse();
//		coomingSoon();
//	}
	
	@UiHandler("anchorListItemAbout")
	public void onClickAnchorListItemAbout(ClickEvent e){
		hideNavbarCollapse();
		presenter.goTo(new GenericPlace(Page.about));
	}
	
	@UiHandler("anchorListItemPolicy")
	public void onClickAnchorListItemPolicy(ClickEvent e){
		hideNavbarCollapse();
		presenter.goTo(new GenericPlace(Page.policy));
	}
	
	@UiHandler("anchorListItemTermsOfUse")
	public void onClickAnchorListItemTermsOfUse(ClickEvent e){
		hideNavbarCollapse();
		presenter.goTo(new GenericPlace(Page.termsOfUse));
	}
		
	@UiHandler("anchorListItemContact")
	public void onClickAnchorListItemContact(ClickEvent e){
		hideNavbarCollapse();
		presenter.goTo(new GenericPlace(Page.contact));
	}
	
	private void coomingSoon()
	{
		Modal history = new Modal();
		history.setTitle("Cooming soon...");
		history.show();
	}
	
	@Override
	public void setMenuPresenter(MenuPresenter presenter) {
		this.presenter = presenter;
	}
	
	private void hideNavbarCollapse() {
		if (this.navbarCollapse.getElement().getClassName().compareTo("navbar-collapse menu collapse in") == 0) {
			navbarCollapse.toggle();
		}
	}
}
