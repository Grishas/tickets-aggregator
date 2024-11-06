package com.ctnc.shared;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;

public class TicketsBlock implements IsSerializable{
	
	private String section;
	private String row;
	private long blockId;
	private boolean eticket;
	private String comments;
	private boolean instantDownload;
	private List<Integer> saleSize = new ArrayList<Integer>(5);
	private Tickets tickets;
	private String test;
	private BigDecimal price;
	private boolean star = false;
	private BigDecimal serviceCharge;
	
	public BigDecimal getServiceCharge() {
		return serviceCharge;
	}
	public void setServiceCharge(BigDecimal serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	public boolean isStar() {
		return star;
	}
	public void setStar(boolean star) {
		this.star = star;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public double getBlockId() {
		return blockId;
	}
	public void setBlockId(long blockId) {
		this.blockId = blockId;
	}
	public boolean isEticket() {
		return eticket;
	}
	public void setEticket(boolean eticket) {
		this.eticket = eticket;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public boolean isInstantDownload() {
		return instantDownload;
	}
	public void setInstantDownload(boolean instantDownload) {
		this.instantDownload = instantDownload;
	}
	public List<Integer> getSaleSize() {
		return saleSize;
	}
	public void setSaleSize(List<Integer> saleSize) {
		this.saleSize = saleSize;
	}
	public Tickets getTickets() {
		return tickets;
	}
	public void setTickets(Tickets tickets) {
		this.tickets = tickets;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	
	@Override
	public String toString() {
		return "TicketsBlock [section=" + section + ", row=" + row
				+ ", blockId=" + blockId + ", eticket=" + eticket
				+ ", comments=" + comments + ", instantDownload="
				+ instantDownload + ", saleSize=" + saleSize + ", test=" + test + ", price=" + price
				+ "]";
	}
}
