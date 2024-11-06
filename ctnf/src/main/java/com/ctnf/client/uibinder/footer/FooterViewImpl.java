package com.ctnf.client.uibinder.footer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FooterViewImpl extends Composite implements FooterView{

	private static FooterViewImplUiBinder uiBinder = GWT.create(FooterViewImplUiBinder.class);
	interface FooterViewImplUiBinder extends UiBinder<Widget, FooterViewImpl> {}

	private FooterPresenter presenter = null;
	
	public FooterViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@Override
	public void setFooterPresenter(FooterPresenter presenter) {
		this.presenter = presenter;
	}
}
