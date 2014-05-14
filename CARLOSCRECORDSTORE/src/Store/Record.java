package Store;

import java.sql.Date;


class Record {
	
	int recID;
	int ConID;
	double Price;
	Date date;
	String title;
	String artist;
	int status;		
	
	public int getRecID() {
		return recID;
	}

	public int getConID() {
		return ConID;
	}

	public double getPrice() {
		return Price;
	}

	public Date getDate() {
		return date;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public int getStatus() {
		return status;
	}
	//CONSTRUCTOR FOR RECORD DATA
	public Record(int recID, int recConID, double origPrice, Date datePutOnMarket, String title, String artist, int status){
		this.recID = recID;
		this.ConID = recConID;
		this.Price = origPrice;
		this.date = datePutOnMarket;
		this.title = title;
		this.artist = artist;
		this.status = status;
	}
	
	public String toString(){//TO STRING TO DIPSLAY WHAT THE VALUE MEAN
		String currentStatus = "";
		
		switch (status){
		case (1) : {
			currentStatus = "New";
			break;
		} case (2) : {
			currentStatus = "Sold";
			break;
		} case (3) : {
			currentStatus = "Notified";
			break;
		} case (4) : {
			currentStatus = "Basement";
			break;
		} case (5) : {
			currentStatus = "Donated";
			break;
		}case (6) : {
			currentStatus = "Returned to Consignor";
			break;
		} default : {
			currentStatus = " Sold and Paid to Consignor";
			
		}
		}
				//DISPLAYS RECORD INFO
		return "Record ID: " + recID + " Consignor ID: " + ConID + "\tDate: " + date + 
				"  Title: " + title + "\nArtist: " + artist + 
				"\tPrice: " + Price + "\tStatus: " + currentStatus + "\n";
	}
	
}