package com.ctnf.client.activities.compare.filter.xs.sm;
import java.util.List;
import java.util.Map;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.DeviceType;
import com.ctnf.client.activities.compare.AboutTickets;
import com.ctnf.client.activities.compare.filter.CompareFilterPresenter;
import com.ctnf.client.activities.compare.filter.CompareFilterValues;
import com.ctnf.client.activities.compare.filter.CompareFilterView;
import com.ctnf.client.activities.compare.filter.CompareFilterViewCommonImpl;
import com.ctnf.client.activities.compare.filter.PriceRange;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CompareFilterViewImpl extends Composite implements CompareFilterView {

	private static FooterViewImplUiBinder uiBinder = GWT.create(FooterViewImplUiBinder.class);
	interface FooterViewImplUiBinder extends UiBinder<Widget, CompareFilterViewImpl> {}

	@UiField Button buttonFilterPrice, buttonFilterQty, buttonFilterStar,buttonFilterReset;

	@UiField DropDownMenu dropDownMenuFilterPrice, dropDownMenuFilterQty;

	private CompareFilterPresenter presenter = null;
	private CompareFilterValues compareFilterValues = null;

	private int partitionsNumber = 5;
	private int qtyThreshold = 5;

	private CompareFilterViewCommonImpl common = new CompareFilterViewCommonImpl();

	public CompareFilterViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("buttonFilterStar")
	public void buttonFilterStarOnClick(ClickEvent event) {
		
		if(this.compareFilterValues.isShowAllStared()==true)
		{
			this.compareFilterValues.setShowAllStared(false);
		}
		else
		{
			this.compareFilterValues.setShowAllStared(true);
		}
		
		CompareFilterValues compareFilterValues = getCompareFilterValues();

		setButtonResetState(compareFilterValues);

		presenter.valueUpdated(compareFilterValues);
	}
	
	@Override
	public void setPresenter(CompareFilterPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void init() {

		this.compareFilterValues = new CompareFilterValues();

		this.common.pricesRange.clear();

		this.buttonFilterPrice.setText(this.common.ANY_PRICE);
		this.common.wa1(buttonFilterPrice,ButtonType.PRIMARY);
		this.common.pricesRange.clear();
		this.dropDownMenuFilterPrice.clear();
		
		this.buttonFilterQty.setText(this.common.ANY_QTY);
		this.common.wa1(buttonFilterQty,ButtonType.PRIMARY);
		this.dropDownMenuFilterQty.clear();
		this.common.minQty = 0;
		this.common.maxQty = 0;
		
		this.buttonFilterReset.setEnabled(false);
		this.buttonFilterReset.setType(ButtonType.DEFAULT);		
	}

	
	
	@Override
	public void update(AboutTickets aboutTickets, List<TicketsBlock> ticketsBlocks, boolean haveParkingTickets) {
		this.setFilters(ticketsBlocks, aboutTickets, haveParkingTickets);
	}

	private void setFilters(final List<TicketsBlock> ticketBlocks, final AboutTickets aboutTickets,boolean haveParkingTickets) {
		
		this.common.pricesRange = this.common.calculatePriceRange(ticketBlocks, aboutTickets,partitionsNumber);

		this.setFilterPriceRange(this.common.pricesRange,
				Math.floor(aboutTickets.getAboutAllTickets().getMinPrice()),
				Math.ceil(aboutTickets.getAboutAllTickets().getMaxPrice()),
				Math.floor(aboutTickets.getAboutAllTickets().getMinPrice()),
				Math.ceil(aboutTickets.getAboutAllTickets().getMaxPrice()));
		
		this.setFilterQuantity(aboutTickets.getMinQuantity(), aboutTickets.getMaxQuantity());
	}
	
	private void setFilterQuantity(int min, int max) 
	{	
		this.common.minQty = min;
		this.common.maxQty = max;
		
		for(int index=min;index<max;index++)
		{
			AnchorListItem anchorListItem = new AnchorListItem(String.valueOf(index));
			this.dropDownMenuFilterQty.add(anchorListItem);
			this.addQtyHandler(anchorListItem);

			if(index == qtyThreshold){
				anchorListItem.setText(anchorListItem.getText()+"+");
				break;
			}
		}	
		
		AnchorListItem anchorListItem = new AnchorListItem(this.common.ANY_QTY);
		this.dropDownMenuFilterQty.add(anchorListItem);	
		this.addQtyHandler(anchorListItem);
	}

	private Anchor anchorQtyPrev = null;
	private void addQtyHandler(AnchorListItem anchorListItem)
	{
		anchorListItem.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Anchor anchor = (Anchor) event.getSource();

				anchor.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				
				if(anchorQtyPrev!=null&&!anchorQtyPrev.equals(anchor)){
					anchorQtyPrev.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				}
				
				anchorQtyPrev = anchor;

				final String newQty = anchor.getText();
				buttonFilterQty.setText(newQty);

				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {
						
						if (buttonFilterQty.getText().equals(common.ANY_QTY)) 
						{
							common.wa1(buttonFilterQty,ButtonType.PRIMARY);
						}
						else
						{
							common.wa1(buttonFilterQty,ButtonType.WARNING);
						}
					}
				});
				
				CompareFilterValues compareFilterValues = getCompareFilterValues();

				setButtonResetState(compareFilterValues);

				presenter.valueUpdated(compareFilterValues);
			}
		});
	}
	
	private void setFilterPriceRange(final Map<String, PriceRange> pricesRange, double minPrice, double maxPrice,
			double currentMinPrice, double currentMaxPrice) {

		for (PriceRange range : pricesRange.values()) 
		{
			AnchorListItem anchorListItem = new AnchorListItem(range.text);
			
			this.dropDownMenuFilterPrice.add(anchorListItem);
		
			this.addPriceRangeHandler(anchorListItem);
		}
	}
	
	private Anchor anchorPricePrev = null;
	private void addPriceRangeHandler(AnchorListItem anchorListItem) {
		
		anchorListItem.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				final Anchor anchor = (Anchor) event.getSource();
				
				anchor.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				
				if(anchorPricePrev!=null&&!anchorPricePrev.equals(anchor)){
					anchorPricePrev.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				}
				
				anchorPricePrev = anchor;

				final String newRange = anchor.getText();
				buttonFilterPrice.setText(newRange);

				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {

						if (buttonFilterPrice.getText().equals(common.ANY_PRICE)) 
						{
							common.wa1(buttonFilterPrice,ButtonType.PRIMARY);
						}
						else
						{
							common.wa1(buttonFilterPrice,ButtonType.WARNING);
						}

					}
				});

				CompareFilterValues compareFilterValues = getCompareFilterValues();

				setButtonResetState(compareFilterValues);

				presenter.valueUpdated(compareFilterValues);
			}
		});
	}

	@Override
	public CompareFilterValues getCompareFilterValues() {
		
		//price set min max price range
		PriceRange rangeMinMax = this.common.pricesRange.get(common.ANY_PRICE);
		compareFilterValues.setPriceMin(rangeMinMax.min);
		compareFilterValues.setPriceMax(rangeMinMax.max);
		
		PriceRange currentRange = this.common.pricesRange.get(this.buttonFilterPrice.getText());
		compareFilterValues.setPriceMinCurrentValue(currentRange.min);
		compareFilterValues.setPriceMaxCurrentValue(currentRange.max);
		
		//qty
		compareFilterValues.setQuantityMin(this.common.minQty);
		compareFilterValues.setQuantityMax(this.common.maxQty); 
		String qtyText = this.buttonFilterQty.getText().replace("+","");
		int qty = 0;
		if(qtyText.equals(this.common.ANY_QTY)){
			qty = this.common.minQty;
		}else{
			qty= Integer.valueOf(qtyText);
		}
		
		compareFilterValues.setQuantityCurrentValue(qty);
		
		 //compareFilterValues.setShowParkingTickets(this.checkBoxButtonParking.getValue());
		
		 GWT.log(compareFilterValues.toString());

		return compareFilterValues;
	}

	@Override
	public void setButtonResetState(CompareFilterValues compareFilterValues) {

		 if(compareFilterValues.getPriceMax()!=compareFilterValues.getPriceMaxCurrentValue()
		 ||
		 compareFilterValues.getPriceMin()!=compareFilterValues.getPriceMinCurrentValue()
		 ||
		 compareFilterValues.getQuantityMin()!=compareFilterValues.getQuantityCurrentValue()
		 ||
		 (compareFilterValues.getSelectedSectionsFromMap()!=null&&compareFilterValues.getSelectedSectionsFromMap().size()>0)
		// (compareFilterValues.isShowParkingTickets()==true)
		 )
		 {
			 buttonFilterReset.setEnabled(true);
			 buttonFilterReset.setType(ButtonType.WARNING);
		 }
		 else
		 {
			 buttonFilterReset.setEnabled(false);
			 buttonFilterReset.setType(ButtonType.DEFAULT);		 
		 }
	}

	 @UiHandler({"buttonFilterReset"})
	 void reset(ClickEvent e) {
		 this.resetFilters();
	 }

	@Override
	public void resetFilters() {

		if(anchorPricePrev!=null){
			anchorPricePrev.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		}		
		if(anchorQtyPrev!=null){
			anchorQtyPrev.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		}		
		
		this.buttonFilterPrice.setText(common.ANY_PRICE);
		this.common.wa1(buttonFilterPrice,ButtonType.PRIMARY);
		
		this.buttonFilterQty.setText(this.common.ANY_QTY);
		this.common.wa1(buttonFilterQty,ButtonType.PRIMARY);
		
		this.buttonFilterReset.setEnabled(false);
		this.buttonFilterReset.setType(ButtonType.DEFAULT);

		this.compareFilterValues.setShowAllStared(false);

		//this.checkBoxButtonParking.setValue(false);

		this.compareFilterValues = this.getCompareFilterValues();

		this.presenter.reset(compareFilterValues);

	}

	@Override
	public void adjust(DeviceType deviceType) {}
	@Override//in regular devices
	public void setSelectSectionState() {}
	@Override//in regular devices
	public void setSectionSelectUpdate(List<TicketsBlock> ticketsBlocks, AboutTickets aboutTickets) {}

}









// @UiHandler("checkBoxButtonParking")
// public void checkBoxButtonParking(ClickEvent event) {
//
// if(this.checkBoxButtonParking.isEnabled())
// {
// CompareFilterValues compareFilterValues = this.getCompareFilterValues();
// this.presenter.valueUpdated(compareFilterValues);
// this.setButtonResetState(compareFilterValues);
// }
// }













