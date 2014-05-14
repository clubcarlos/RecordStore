package Store;

import java.util.LinkedList;
/*
 * REQUEST METHODS FROM INVENTORY MODEL
 * FOR METHODS LIKE UPDATIND A RECORD STATUS AND PRICE AND MORE
 * 
 */


public class InventoryController {

	static InventoryModel db ;
	//USED INT VALUES TO MAKE STATUS OF RECORD
	//GOT THIS FROM DOMINIC HARTJES REALLY HELPS WHEN YOUR UPDATING THE STATUS OF A
	//RECORD.
	public static final int NEW = 1;
	public final int SOLD = 2;
	public final int NOTIFIED = 3;
	public final int BASEMENT = 4;
	public final int DONATED = 5;
	public final int RETURNED_TO_CONSIGNOR = 6;
	public final int PAID_CONSIGNOR = 7;

	public static void main(String[] args) {
		//Add a shutdown hook.
		AddShutdownHook closeDBConnection = new AddShutdownHook();
		closeDBConnection.attachShutdownHook();
		try {
			InventoryController controller = new InventoryController();


			db = new InventoryModel(controller);

			boolean setup = db.setupDatabase();
			if (setup == false) {
				System.out.println("Error setting up database, see error messages. Clean up database connections.... Quitting program ");

				db.cleanup();

				System.out.println("Quitting program ");

				System.exit(-1);  
			}

			new InventoryView(controller).launchUI();
		}

		finally {
			if (db != null) {
				db.cleanup();
			}
		}

	}

	public String requestAddConsignor(Consignor c) {

		boolean success = db.addConsignor(c);
		if (success == true ) {
			return null;   //Null means all was well.
		}
		else {
			return "Unable to add Consignor to database";
		}

	}
	public String requestAddRecord(Record r) {

		boolean success = db.addRecord(r);
		if (success == true ) {
			return null;   //Null means all was well.
		}
		else {
			return "Unable to add Record to database";
		}

	}

	public Consignor getConsignorByID(int ConID) {
		Consignor consignorbyID = db.getConsignorByID(ConID);
		if (consignorbyID == null ) {
			System.out.println("Controller detected error in fetching Consignor BY ID");
			return null;   //Null means error. View can deal with how to display error to user.
		}
		else {
			return consignorbyID;
		}
	}

	public Record GetRecordID(int recID) {

		Record recordID = db.getRecordID(recID);
		if (recordID == null ) {
			System.out.println("Controller detected error in fetching Record ID");
			return null;   //Null means error. View can deal with how to display error to user.
		}
		else {
			return recordID;
		}
	}
	
	public Record updateRecordStatus(Record record ,int status) {
		Record update = db.updateRecordStatus(record, status);
		if (update == null ) {
			return null;

		}
		else {
			return update;
		}
	}
	public Record updateRecordPrice(Record record ,double Price) {
		Record update = db.updateRecordPrice(record, Price);
		if (update == null ) {
			return null;

		}
		else {
			return update;
		}
	}
	public Consignor updateConsignorMoney(Consignor c, double MoneyOwed) {
		Consignor update = db.updateConsignorMoneyOwed(c, MoneyOwed);
		if (update == null ) {
			return null;

		}
		else {
			return update;
		}
	}
	
	
	public String addSalesReport(Record r , double price) {
		
		boolean success = db.addSalesReport(r, price);
		if (success == true ) {
			return null;
		} else {
			return "Unable to add Sales Report to database";
		}
	}
	public LinkedList<Consignor> requestAllConsignors() {


		LinkedList<Consignor> allConsignors = db.displayAllConsignors();
		if (allConsignors == null ) {
			System.out.println("Controller detected error in fetching Consignor from database");
			return null;   //Null means error. View can deal with how to display error to user.
		}
		else {
			return allConsignors;
		}


	}
	public LinkedList<Record> requestAllRecords() {


		LinkedList<Record> allRecords = db.displayAllRecords();
		if (allRecords == null ) {
			System.out.println("Controller detected error in fetching Record from database");
			return null;   //Null means error. View can deal with how to display error to user.
		}
		else {
			return allRecords;
		}


	}
	public LinkedList<Record> viewRecordByStatus(int status) {
		
		LinkedList<Record> RecordsByStatus = db.viewRecordsByStatus(status);
		if (RecordsByStatus == null ) {
			System.out.println("Controller detected error in fetching Record by status from database");
			return null;   //Null means error. View can deal with how to display error to user.
		}
		else {
			return RecordsByStatus;
		}
	}
	
	public LinkedList<SalesReport> requestAllReports() {


		LinkedList<SalesReport> allReports = db.displayAllReports();
		if (allReports == null ) {
			System.out.println("Controller detected error in fetching Reports from database");
			return null;   //Null means error. View can deal with how to display error to user.
		}
		else {
			return allReports;
		}


	}


}

class AddShutdownHook {
	public void attachShutdownHook() { 
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutdown hook: program closed, attempting to shut database connection");
				//Unfortunately this doesn't seem to be called when a program is restarted in eclipse.
				//Avoid restarting your programs. If you do, and you get an existing connection error you can either
				// 1. restart eclipse - Menu > Restart
				// 2. Delete your database folder. In this project it's a folder called laptopinventoryDB (or similar) in the root directory of your project. 
				InventoryController.db.cleanup();
			}
		});
	}
}
