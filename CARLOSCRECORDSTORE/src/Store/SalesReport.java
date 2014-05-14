package Store;

import java.sql.Date;

public class SalesReport {
	
	private int reportID;
	
	private int recID;
	private Date sale_Date;
	private Double sale_Price;
	
	
	public int getReportID() {
		return reportID;
	}
	public int getRecID() {
		return recID;
	}
	public Date getSale_Date() {
		return sale_Date;
	}
	public Double getSale_Price() {
		return sale_Price;
	}
	public SalesReport (int reportID, int recID, Date sale_Date, Double sale_Price) {
		this.reportID = reportID;
		this.recID = recID;
		this.sale_Date = sale_Date;
		this.sale_Price = sale_Price;
	}
	public String toString() {
		return "Report ID: " + reportID + " Record ID: " + recID + " Sale Date: " + sale_Date
				+ " Sale Price: " + sale_Price;
	}

}
