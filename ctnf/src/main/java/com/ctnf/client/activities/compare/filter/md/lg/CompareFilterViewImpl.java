package com.ctnf.client.activities.compare.filter.md.lg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBoxButton;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.extras.select.client.ui.MultipleSelect;
import org.gwtbootstrap3.extras.select.client.ui.Option;

import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.DeviceType;
import com.ctnf.client.activities.compare.AboutTickets;
import com.ctnf.client.activities.compare.AboutTickets.About;
import com.ctnf.client.activities.compare.AlphanumComparator;
import com.ctnf.client.activities.compare.AlphanumComparator.SortBy;
import com.ctnf.client.activities.compare.filter.CompareFilterPresenter;
import com.ctnf.client.activities.compare.filter.CompareFilterValues;
import com.ctnf.client.activities.compare.filter.CompareFilterView;
import com.ctnf.client.activities.compare.filter.CompareFilterViewCommonImpl;
import com.ctnf.client.activities.compare.filter.PriceRange;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CompareFilterViewImpl extends Composite implements CompareFilterView {

	private static FooterViewImplUiBinder uiBinder = GWT.create(FooterViewImplUiBinder.class);
	interface FooterViewImplUiBinder extends UiBinder<Widget, CompareFilterViewImpl> {}

	@UiField
	Button buttonFilterPrice, buttonFilterQty, buttonFilterReset, buttonSummarySectionsView,buttonFilterStar;

	@UiField
	DropDownMenu dropDownMenuFilterPrice, dropDownMenuFilterQty;

	@UiField
	CheckBoxButton checkBoxButtonParking;

	@UiField
	Div divMultipleSelectSection;
	private MultipleSelect multipleSelectSections;

	private CompareFilterPresenter presenter = null;

	private CompareFilterValues compareFilterValues = null;

	private CompareFilterViewCommonImpl common = new CompareFilterViewCommonImpl();

	private int partitionsNumber = 5;
	private int qtyThreshold = 5;

	// @UiField(provided=true) Image imageGoDaddySiteSeal;
	// @UiField Span spanTrust;

	public CompareFilterViewImpl() {

		// imageGoDaddySiteSeal = new Image("godaddysiteseal.gif");
		// imageGoDaddySiteSeal.getElement().getStyle().setCursor(Cursor.POINTER);
		//
		// imageGoDaddySiteSeal.addClickHandler(new ClickHandler() {
		// @Override
		// public void onClick(ClickEvent event) {
		// Window.open("https://seal.godaddy.com/verifySeal?sealID=vpMmt3UFO2TldmJuwlxxAbW3C0ynbRM7UB9dvGXPf6MNiYQc0yB1CJYsUKiw","_blank","enabled");
		// }
		// });

		initWidget(uiBinder.createAndBindUi(this));

		
		this.buttonFilterPrice.getElement().getStyle().setBorderColor("#337ab7");
		this.buttonFilterPrice.getElement().getStyle().setColor("#337ab7");

		this.buttonFilterQty.getElement().getStyle().setBorderColor("#337ab7");
		this.buttonFilterQty.getElement().getStyle().setColor("#337ab7");

		this.buttonFilterStar.getElement().getStyle().setBorderColor("#337ab7");
		this.buttonFilterStar.getElement().getStyle().setColor("#337ab7");

		
		this.buttonFilterReset.getElement().getStyle().setBorderColor("#337ab7");
		this.buttonFilterReset.getElement().getStyle().setColor("#337ab7");

		this.buttonSummarySectionsView.getElement().getStyle().setBorderColor("#337ab7");
		this.buttonSummarySectionsView.getElement().getStyle().setColor("#337ab7");

		this.checkBoxButtonParking.getElement().getStyle().setBorderColor("white");
		this.checkBoxButtonParking.getElement().getStyle().setColor("#337ab7");

		
		//checkBoxButtonParking.setHeight("33px");
		
		
		// this.inputGroupAddonQuantity.getElement().getStyle().setBackgroundColor("white");
		// this.inputGroupAddonMinPrice.getElement().getStyle().setBackgroundColor("white");
		// this.inputGroupAddonMaxPrice.getElement().getStyle().setBackgroundColor("white");
		//
		// this.inputGroupAddonMinPrice.getElement().getStyle().setBorderColor("#337ab7");
		// this.inputGroupAddonMaxPrice.getElement().getStyle().setBorderColor("#337ab7");
		// this.inputGroupAddonQuantity.getElement().getStyle().setBorderColor("#337ab7");

		// this.getElement().getStyle().setBackgroundImage("url(squairy_light.png)");
		// this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		// this.getElement().getStyle().setBorderColor("#337ab7");
		// this.getElement().getStyle().setBorderWidth(1,Unit.PX);
		// this.getElement().getStyle().setProperty("borderRadius", "3px");
		// this.getElement().getStyle().setPadding(4,Unit.PX);

		// this.checkBoxButtonPricesFilter1.getElement().getStyle().setBorderColor("#337ab7");
		// this.checkBoxButtonPricesFilter1.getElement().getStyle().setColor("#337ab7");
		
		
		this.dropDownMenuFilterPrice.addStyleName("dropDownMenuFilterPrice");
		this.dropDownMenuFilterQty.addStyleName("dropDownMenuFilterQty");
		
	}

	@Override
	public void setPresenter(CompareFilterPresenter presenter) {
		this.presenter = presenter;
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
	
	@UiHandler("buttonSummarySectionsView")
	public void buttonSummarySectionsViewOnClick(ClickEvent event) {
		if (divMultipleSelectSection.isVisible() == true) {
			divMultipleSelectSection.setVisible(false);
			divMultipleSelectSection.setMarginTop(0);
		} else if (divMultipleSelectSection.isVisible() == false && isShowSectionSelect() == true) {
			divMultipleSelectSection.setVisible(true);
			divMultipleSelectSection.setMarginTop(5);
		}
	}

	@Override
	public void setSelectSectionState() {

		if (isShowSectionSelect() == false) {
			this.divMultipleSelectSection.setVisible(false);
			this.buttonSummarySectionsView.setColor("#777");
		} else {
			this.buttonSummarySectionsView.setColor("#337ab7");
		}
	}

	private boolean isShowSectionSelect() {
		if (compareFilterValues.getSelectedSectionsFromMap() == null
				|| compareFilterValues.getSelectedSectionsFromMap().size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void init() {

		this.compareFilterValues = new CompareFilterValues();

		this.common.pricesRange.clear();
		
		this.buttonFilterPrice.setText(this.common.ANY_PRICE);
		this.common.wa1(buttonFilterPrice,ButtonType.DEFAULT);
		this.common.pricesRange.clear();
		this.dropDownMenuFilterPrice.clear();
		
		this.buttonFilterQty.setText(this.common.ANY_QTY);
		this.common.wa1(buttonFilterQty,ButtonType.DEFAULT);
		this.dropDownMenuFilterQty.clear();
		this.common.minQty = 0;
		this.common.maxQty = 0;

		this.buttonFilterReset.setEnabled(false);
		this.buttonFilterReset.setType(ButtonType.DEFAULT);
		
		this.checkBoxButtonParking.setValue(false);
		this.checkBoxButtonParking.setEnabled(false);

		if (this.multipleSelectSections != null) {
			this.multipleSelectSections.clear();
		}		
	}

	@Override
	public void update(AboutTickets aboutTickets, List<TicketsBlock> ticketsBlocks, boolean haveParkingTickets) {
		this.setFilters(ticketsBlocks, aboutTickets, haveParkingTickets);
	}

	private void setFilters(final List<TicketsBlock> ticketBlocks, AboutTickets aboutTickets,
			boolean haveParkingTickets) {
		
		this.common.pricesRange.clear();

		Map<String, PriceRange> pricesRange = this.common.calculatePriceRange(ticketBlocks, aboutTickets,
				partitionsNumber);

		this.setFilterPriceRange(pricesRange, Math.floor(aboutTickets.getAboutAllTickets().getMinPrice()),
				Math.ceil(aboutTickets.getAboutAllTickets().getMaxPrice()),
				Math.floor(aboutTickets.getAboutAllTickets().getMinPrice()),
				Math.ceil(aboutTickets.getAboutAllTickets().getMaxPrice()));

		this.setFilterQuantity(aboutTickets.getMinQuantity(), aboutTickets.getMaxQuantity());

		this.checkBoxButtonParking.setEnabled(haveParkingTickets);

		this.setFilterSection(ticketBlocks, aboutTickets);
	}


	private void setFilterQuantity(int min, int max) 
	{	
		this.buttonFilterQty.setText(common.ANY_QTY);
		this.common.wa1(buttonFilterQty, ButtonType.DEFAULT);

		
		this.dropDownMenuFilterQty.clear();
		this.anchorQtyPrev = null;
		
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
						
						common.wa1(buttonFilterQty,ButtonType.DEFAULT);

//						if (buttonFilterQty.getText().equals(common.ANY_QTY)) 
//						{
//							common.wa1(buttonFilterQty,ButtonType.DEFAULT);
//						}
//						else
//						{
//							common.wa1(buttonFilterQty,ButtonType.DEFAULT);
//						}
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

		this.buttonFilterPrice.setText(common.ANY_PRICE);
		this.common.wa1(buttonFilterPrice, ButtonType.DEFAULT);

		this.dropDownMenuFilterPrice.clear();
		this.anchorPricePrev = null;
		
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

						common.wa1(buttonFilterPrice,ButtonType.DEFAULT);

//						if (buttonFilterPrice.getText().equals(common.ANY_PRICE)) 
//						{
//							common.wa1(buttonFilterPrice,ButtonType.DEFAULT);
//						}
//						else
//						{
//							common.wa1(buttonFilterPrice,ButtonType.WARNING);
//						}

					}
				});

				CompareFilterValues compareFilterValues = getCompareFilterValues();

				setButtonResetState(compareFilterValues);

				presenter.valueUpdated(compareFilterValues);
			}
		});
	}

	
	
	
	@Override
	public void setSectionSelectUpdate(List<TicketsBlock> ticketsBlocks, AboutTickets aboutTickets) {
		this.setFilterSection(ticketsBlocks, aboutTickets);

	}

	private void setFilterSection(final List<TicketsBlock> ticketBlocks, final AboutTickets aboutTickets) {

		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {

				Timer timer = new Timer() {
					@Override
					public void run() {

						for (int index = 0; index < divMultipleSelectSection.getWidgetCount(); index++) {
							divMultipleSelectSection.remove(index);
						}

						// if(multipleSelectSections==null)
						// {
						multipleSelectSections = new MultipleSelect();
						multipleSelectSections.addStyleName("multipleSelectSections");
						multipleSelectSections.setLiveSearch(true);
						// multipleSelectSections.setHeader("Put attention that
						// sometimes brokers set different names for same
						// section."
						// + "For example: \"LOGE 161\" and \"161LG\"");
						multipleSelectSections.setType(ButtonType.PRIMARY);
						
						
						multipleSelectSections.setTitle("Search/Select multiple sections");
						// }
						// else
						// {
						// multipleSelectSections.clear();
						// }

						// event registration here
						multipleSelectSections.addValueChangeHandler(new ValueChangeHandler<List<String>>() {
							@Override
							public void onValueChange(ValueChangeEvent<List<String>> event) {

								CompareFilterValues compareFilterValues = getCompareFilterValues();

								// user uncheck last option
								if (multipleSelectSections.getSelectedItems().size() == 0) {
									compareFilterValues.setEscapeMultipleSelectSections(true);
								}

								presenter.valueUpdated(compareFilterValues);

								setButtonResetState(compareFilterValues);
							}
						});

						List<String> avoidDuplicate = new ArrayList<String>();
						List<TicketsBlock> result = new ArrayList<TicketsBlock>();

						// remove duplicates
						for (TicketsBlock ticketsBlock : ticketBlocks) {
							if (avoidDuplicate.contains(ticketsBlock.getSection())) {
								continue;
							}

							result.add(ticketsBlock);

							avoidDuplicate.add(ticketsBlock.getSection());
						}
						avoidDuplicate = null;

						sortSummarySections(result, aboutTickets);

						// apply to options
						for (TicketsBlock ticketsBlock : result) {
							Option option = new Option();
							// option.setColor("#337ab7");
							option.setValue(ticketsBlock.getSection());
							option.setHTML(getSummarySectionInfo(ticketsBlock.getSection(), aboutTickets));

							// option.setIcon(IconType.SELLSY);

							multipleSelectSections.add(option);

						}

						multipleSelectSections.refresh();
						multipleSelectSections.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);

						divMultipleSelectSection.add(multipleSelectSections);
					}
				};
				timer.schedule(1000); // select issue wa
			}
		});
	}

	private void sortSummarySections(List<TicketsBlock> result, final AboutTickets aboutTickets) {

		Collections.sort(result, new AlphanumComparator(SortBy.section));

		// java.util.Collections.sort(result, new Comparator<TicketsBlock>() {
		// @Override
		// public int compare(TicketsBlock o1, TicketsBlock o2) {
		//
		// About a =
		// aboutTickets.getAboutPerSectionTickets().get(o1.getSection());
		// About b =
		// aboutTickets.getAboutPerSectionTickets().get(o2.getSection());
		//
		// double result = a.getMinPrice() - b.getMinPrice();
		//
		// if (result > 0) {
		// return 1;
		// } else {
		// return -1;
		// }
		// }
		// });
	}

	public String getSummarySectionInfo(String section, AboutTickets aboutTickets) {
		About about = aboutTickets.getAboutPerSectionTickets().get(section);

		return (section + about.getSummarySectionInfo());
	}

	@UiHandler("checkBoxButtonParking")
	public void checkBoxButtonParking(ClickEvent event) {

		if (this.checkBoxButtonParking.isEnabled()) {
			CompareFilterValues compareFilterValues = this.getCompareFilterValues();
			this.presenter.valueUpdated(compareFilterValues);
			this.setButtonResetState(compareFilterValues);
		}
	}
	
	@Override
	public CompareFilterValues getCompareFilterValues() {

		// price set min max price range
		PriceRange rangeMinMax = this.common.pricesRange.get(common.ANY_PRICE);
		compareFilterValues.setPriceMin(rangeMinMax.min);
		compareFilterValues.setPriceMax(rangeMinMax.max);

		PriceRange currentRange = this.common.pricesRange.get(this.buttonFilterPrice.getText());
		compareFilterValues.setPriceMinCurrentValue(currentRange.min);
		compareFilterValues.setPriceMaxCurrentValue(currentRange.max);

		// qty
		compareFilterValues.setQuantityMin(this.common.minQty);
		compareFilterValues.setQuantityMax(this.common.maxQty);
		String qtyText = this.buttonFilterQty.getText().replace("+", "");
		int qty = 0;
		if (qtyText.equals(this.common.ANY_QTY)) {
			qty = this.common.minQty;
		} else {
			qty = Integer.valueOf(qtyText);
		}

		compareFilterValues.setQuantityCurrentValue(qty);
		
		//---

		compareFilterValues.setShowParkingTickets(this.checkBoxButtonParking.getValue());

		// ---

		compareFilterValues.setSelectedSectionsFromSelect(this.getSelectedSectionsFromSelect());

		compareFilterValues.setEscapeMultipleSelectSections(false);

		return compareFilterValues;
	}

	private List<String> getSelectedSectionsFromSelect() {
		if (this.multipleSelectSections == null || this.multipleSelectSections.getSelectedItems().size() == 0) {
			return new ArrayList<String>(0);
		} else {
			List<String> result = new ArrayList<String>(this.multipleSelectSections.getSelectedItems().size());

			for (Option option : this.multipleSelectSections.getSelectedItems()) {
				result.add(option.getValue());
			}
			return result;
		}
	}

	@Override
	public void setButtonResetState(CompareFilterValues compareFilterValues) 
	{
		if (compareFilterValues.getPriceMax() != compareFilterValues.getPriceMaxCurrentValue()
		 || compareFilterValues.getPriceMin() != compareFilterValues.getPriceMinCurrentValue()
		 || compareFilterValues.getQuantityMin() != compareFilterValues.getQuantityCurrentValue()
	   	 || (compareFilterValues.getSelectedSectionsFromMap() != null && compareFilterValues.getSelectedSectionsFromMap().size() > 0)
		 || (compareFilterValues.isShowParkingTickets() == true)
		 || (compareFilterValues.getSelectedSectionsFromSelect() != null && compareFilterValues.getSelectedSectionsFromSelect().size() > 0)
		 )
		{
			this.buttonFilterReset.setType(ButtonType.WARNING);
			buttonFilterReset.setEnabled(true);
		} 
		else 
		{
			this.buttonFilterReset.setType(ButtonType.DEFAULT);
			buttonFilterReset.setEnabled(false);

		}
	}

	/*
	 
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
	 
	 
	 */
	
	
	
	
	@UiHandler({ "buttonFilterReset" })
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
		this.common.wa1(buttonFilterPrice,ButtonType.DEFAULT);
		
		this.buttonFilterQty.setText(this.common.ANY_QTY);
		this.common.wa1(buttonFilterQty,ButtonType.DEFAULT);
		
		this.buttonFilterReset.setEnabled(false);
		this.buttonFilterReset.setType(ButtonType.DEFAULT);

		this.checkBoxButtonParking.setValue(false);

//		if (this.multipleSelectSections != null) {
//			this.multipleSelectSections.deselectAll();
//		}

		this.compareFilterValues.setShowAllStared(false);
				
		this.compareFilterValues = this.getCompareFilterValues();

		this.presenter.reset(compareFilterValues);
		
		this.divMultipleSelectSection.setVisible(false);
		
		this.buttonSummarySectionsView.setColor("#337ab7");


	}

	@Override
	public void adjust(DeviceType deviceType) {

	}
}
