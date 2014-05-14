package Store;

/*
 * DISPLAY TWO MENUS, HAS METHODS TO VIEW CONSIGNOR, RECORD TABLE, AND
 * SALES REPORT
 * FEATURES INCLUDE
 * SEARCH RECORD BY ID
 * UPDATE RECORD
 * PAY CONSIGNOR
 * SELL A RECORD
 * NOTIFY CONSIGNORS
 * THIS IS THE BUSIEST CLASS I HAVE
 * THERE MANY SMALL METHODS USED FOR VALIDATION MOSTLY WHEN INPUTING DATA
 * DOMINIC HARTJES WAS REALLY HELPFUL DURING THE REVIEW
 * I WAS ABLE TO FIGURE OUT HOW TO FIND YEAR AND 30 DAY OLD RECORDS 
 * WITH THE HELP OF DOMINIC HARTJES CODING I SAW HOW HE IMPLEMENTED HIS METHOD
 * AND KIND OF WHEN FROM THERE 
 */

import java.sql.Date;
import java.util.LinkedList;
import java.util.Scanner;


public class InventoryView {

	private final int QUIT = 7;// to quit out of main menu

	InventoryController myController;

	InventoryModel db;
	Scanner s;

	InventoryView(InventoryController c) {
		myController = c;
		s = new Scanner(System.in);
	}

	public void launchUI() {

		while (true) {

			int userChoice = displayMenuGetUserChoice();
			if (userChoice == QUIT) {
				break;
			}

			doTask(userChoice);
		}

	}

	private void doTask(int userChoice) {

		switch (userChoice) {

		case 1: {
			displayAllConsignors();
			break;
		}
		case 2: {
			addNewConsignor();
			break;
		}
		case 3: {
			displayAllRecords();
			break;
		}
		case 4: {
			addNewRecord();
			break;
		}
		case 5: {
			searchRecord();
			break;
		}
		case 6: {
			viewOperationsMenu();
		}
		}

	}

	private void doTaskAccount(int userChoice) {

		switch (userChoice) {

		case 1: {
			NotifyConsignors();
			break;
		}
		case 2: {
			PayConsignor();
			break;
		}
		case 3: {
			UpdatedRecords();
			break;
		}
		case 4: {
			SellARecord();
			break;
		}
		case 5: {
			displayAllSalesReports();
			break;
		}
		}

	}

	private void PayConsignor() {
		LinkedList<Record> recordsSold = myController.viewRecordByStatus(myController.SOLD);
		LinkedList<Consignor> ConsingorsPaid = myController.requestAllConsignors();
		
		if (recordsSold.isEmpty()) {
			System.out.println("All records have been Paid to Consignor");
		} else {

		for (Record r : recordsSold){
			for (Consignor c : ConsingorsPaid) {
				if (r.ConID==c.conID){
				System.out.println(r +"Sold for " + r.Price);
				double PriceSold = r.Price;
				double MoneyOwedtoConsignorForRecord = .4*PriceSold;// 40percent goes to consignor
				double totalMoneyOwed = c.getAmountOwed() + MoneyOwedtoConsignorForRecord;
				System.out.println(c.name +" will recieve " + MoneyOwedtoConsignorForRecord + " for " + r.title);
				System.out.println("The total amount owed to " + c.name + ": " + totalMoneyOwed +"\n" );
				myController.updateConsignorMoney(c, totalMoneyOwed);
				myController.updateRecordStatus(r, myController.PAID_CONSIGNOR);

			}
			}


		}
		}
	}

	public void viewOperationsMenu() {
		while (true) {

			int userChoice = displayOperationsMenu();
			if (userChoice == QUIT) {
				break;
			}

			doTaskAccount(userChoice);
		}

	}

	private void addNewConsignor() {

		System.out.println("Enter Consignor name");
		String name = checkName();

		System.out.println("Enter phone number");
		String phoneNum = checkPhone();

		// CONSIGNOR INFO IS ENTER CONSIGNOR ID IS SET TO ZERO INVENTORY MODEL
		// TAKES CARE OF THAT AND
		// SINCE CONSIGNOR IS NEW BALANCE IS SET TO ZERO

		Consignor firstC = new Consignor(0, name, phoneNum, 0.0);
		// addRecord(firstC);

		String errorMessage = myController.requestAddConsignor(firstC);
		// IMPORTANT TO HAVE INFOR ADDED TO SEE WHETHER OR NOR DATA HAS BEED
		// ADDED TO DATABASE OR NOT
		if (errorMessage == null) {
			System.out.println("New Consignor added to database");
		} else {
			System.out.println("New Consignor could not be added to database");
			System.out.println(errorMessage);
		}

	}
	private String checkName() {
		String name = null;

		while (true){
			name = s.nextLine();
			System.out.println(name);

			//Validation
			if (name.contains(" ")){
				//Do nothing
			} else {
				System.out.println("Please enter first and last name separated by a space."
						+ "\nIf only first name applies add space after name.");
				continue;
			}

			//Check whether input contains improper characters.
			String invalidResponse = "!@#$%^&*()_=+[{]}\"\\|;:',<.>/?`~";

			boolean hasInvalid = false;

			for (int i = 0; i<name.length(); i++){
				String testSubstring = name.substring(i, (i+1));


				//You've encountered an invalid character; break the for loop, get new input.
				if (invalidResponse.contains(testSubstring)){
					hasInvalid = true;
				}
			}

			if (hasInvalid){
				System.out.println("Invalid characters used. Name cannot contain the following:\n"
						+ "!@#$%^&*()_=+[{]}\"\\|;:',<.>/?`~");
				System.out.println("Please try again.");
				continue;
			}
			break;
		}
		//If you get this far, your name string is valid.
		return name;
	}
	private String checkTitle() {
		String title = null;

		while (true){
			title = s.nextLine();
			System.out.println(title);
			break;
		}
		return title;
	}
	private Double checkPrice(){
		String price = null;
		String priceSubstring = null;
		Double priceD = null;

		while (true) {
			price = s.next();
			String validPriceDigits = "0123456789.";
			//makes sure that you only enter these digits
			for (int i = 0; i < price.length(); i++) {
				priceSubstring = price.substring(i, (i + 1));	
			}if (!validPriceDigits.contains(priceSubstring)) {
				System.out.println("You must enter Price correctly ");
				continue;
			} priceD = Double.parseDouble(price);
			break;

		}return priceD ;
	}

	private String checkPhone() {


		String phone = null;
		String phoneSubstring = null;

		while (true) {
			phone = s.next();
			String validPhoneDigits = "0123456789";
			//makes sure you only enter digits
			for (int i = 0; i < phone.length(); i++) {
				phoneSubstring = phone.substring(i, (i + 1));	
			}if (!validPhoneDigits.contains(phoneSubstring)) {
				System.out.println("You must enter digits between 0 and 9 ");
				continue;
			}//must be  10 digits
			if (phone.length() > 10 || phone.length() < 10) {
				System.out.println("Invalid digit length. Enter 10 digits XXXXXXXXXX.....Try agian");
				continue;
			}break;
		}return phone; //if you get here return phone string
	}

	private void NotifyConsignors() {
		//notifies consignor when record hasn't sold within 30 days of the record
		//entering the system
		LinkedList<Record> recordResults = myController.viewRecordByStatus(myController.NEW);
		System.out.println("List of records that haven't been sold within 30 days");
		LinkedList<Record> thirtyDaysOld = thirtyDayOldRecords(recordResults);

		System.out.println("We have " + thirtyDaysOld.size() +" consignor to notify:");

		for (Record r : thirtyDaysOld){//will display contact info for every record
			Consignor c = myController.getConsignorByID(r.ConID);
			System.out.println("Please contact consignor " + c.name
					+ ". Consignor phone number: " + c.phoneNum);
			myController.updateRecordStatus(r, myController.NOTIFIED);
		}

		if (thirtyDaysOld.size()>0){
			System.out.println("\nRecords Updated and Consignors contacted");


		}


	}
	private void UpdatedRecords() {
		
		updateNotifiedRecords();//two separate methods to update only records that 
		//have been notified to consignor
		updateRecordsToDonate();//and records that are older than a year in the database
		//that need to be donated
		
	}
	private void updateNotifiedRecords() {
		LinkedList<Record> recordsUpdated = myController.viewRecordByStatus(myController.NOTIFIED);
		System.out.println("Go through every record that has been notified to the Consignor");
		
		if (recordsUpdated.isEmpty()) {
			System.out.println("There are no records to update Consignor must be notified first");
		}else {
		for (Record r : recordsUpdated) {//Go thru every record that the status equals notified
			System.out.println(r);
			Consignor c = myController.getConsignorByID(r.ConID);
			System.out.println("Does "+ c.name + " want " + r.title + " by: " +r.artist+ " returned to them? ");
			String choice1 = s.next();
			//if yes set status equals RETURNED_TO_CONSIGNOR
			if (choice1.equalsIgnoreCase("Y")){
				System.out.println("Ok, record is being returned to consignor.");
				myController.updateRecordStatus(r, myController.RETURNED_TO_CONSIGNOR);
			} else {	//if no set status BASEMENT
				System.out.println("Ok, put record in basement.");
				myController.updateRecordStatus(r, myController.BASEMENT);
			}
		}
		}
	}
		
		private void updateRecordsToDonate(){
		
		LinkedList<Record> basementRecords = myController.viewRecordByStatus(myController.BASEMENT);
		System.out.println("\nLets check for records that are more than a year old........");
		LinkedList<Record> recordsD = findYearOldRecords(basementRecords);
		System.out.println("We found " + recordsD.size() + " record(s) to Donate");

		for (Record r : recordsD){// will go thru every record that older than a year in the basement
			myController.updateRecordStatus(r, myController.DONATED);//and set status to donated
			System.out.println(r);
		}
		System.out.println(recordsD.size() +" Record(s) donated.");
		
	}
	@SuppressWarnings("deprecation")
	private LinkedList<Record> findYearOldRecords(LinkedList<Record> recordResults) {
		LinkedList<Record> yearOld = new LinkedList<Record>();

		for (Record r : recordResults){
			//Create a date object with todays date, then set the year -1 to find the date one year ago.
			Date yearAgo = new java.sql.Date(System.currentTimeMillis());//current date
			yearAgo.setYear(yearAgo.getYear()-1);

			//Compare the date of one year ago with the date this record was put on market.
			//Compare to returns a negative number of the date is earlier than the date argument.
			if (r.date.compareTo(yearAgo)<0){
				System.out.println(r);
				yearOld.add(r);
			}
		}
		return yearOld;
	}

	@SuppressWarnings("deprecation")
	private LinkedList<Record> thirtyDayOldRecords(LinkedList<Record> recordResults) {
		//Dominic was able to help me implement this method
		LinkedList<Record> thirtyDaysOld = new LinkedList<Record>();

		for (Record r : recordResults){
			//Create a date object with todays date, then set the year -1 to find the date one year ago.
			Date monthAgo = new java.sql.Date(System.currentTimeMillis());//current date
			monthAgo.setMonth(monthAgo.getMonth()-1);
			//Compare the date of one year ago with the date this record was put on market.
			//Compare to returns a negative number of the date is earlier than the date argument.
			if (r.date.compareTo(monthAgo)<0){
				System.out.println(r);
				thirtyDaysOld.add(r);
			}
		}
		return thirtyDaysOld;
	}

	private void addNewRecord() {

		displayAllConsignors();
		System.out.println("\nEnter Consignor ID before entering new Record");
		int conID = CheckConsignorID();
		System.out.println("Enter Artist");
		String artist = checkName();
		System.out.println("Enter Title");
		String title = checkTitle();
		System.out.println("Enter Price");
		Double price = checkPrice();
		Date date = new java.sql.Date(System.currentTimeMillis()); //currentdate
		int status = myController.NEW;//status is new 

		//record id is set to zero, the inventory model class
		//will take care of that
		Record firstR = new Record(0, conID, price, date, title, artist, status);

		String errorMessage = myController.requestAddRecord(firstR);

		if (errorMessage == null) {
			System.out.println("New Record added to database");
		} else {
			System.out.println("New Record could not be added to database");
			System.out.println(errorMessage);
		}

	}

	private void searchRecord() {
		LinkedList<Record> records = myController.requestAllRecords();

		System.out.println("We have "+ records.size()+ " records on our database\n");

		System.out.println("Enter record ID to search for record");
		int recID = s.nextInt();

		for (Record r : records){
			if(recID==r.recID){
				System.out.println(r);
			}
			

		}


	}

	private int CheckConsignorID() {
		// TODO Auto-generated method stub
		boolean inputOK = false;
		int conID = 0;

		while (!inputOK) {


			String userChoiceStr = s.next();
			try {
				conID = Integer.parseInt(userChoiceStr);
				Consignor c = myController.getConsignorByID(conID);
				// VERIFYS BY RETRIEVING USERNAME AND CONITINUES WITH USER INPUT
				System.out.println("The Consignor's name  is " + c.getName());
				break;

			} catch (Exception nfe) {
				System.out.println("Please enter Consignor ID from Table\n");
				displayAllConsignors();

				continue;
			}
		}inputOK = true;
		return conID;
	}

	private Record SellARecord() {
		double price = 0.0;


		Record record = getPriceWithRecID();//user must enter record id first 
		if (record == null){
			return record;
		}
		if (record.status == myController.BASEMENT){//if record is in basement
			//price is set to 1 dollar and price is updated in table
			price = 1.00;
			myController.updateRecordPrice(record, price );
		} else {
			price = record.Price;
		}
		System.out.println("The price for " + record.title + " by " + record.artist + " is $" + price);

		System.out.println("Purchase record? Y/N");
		//user input
		String choice = s.next();

		if (choice.equals("N")){
			viewOperationsMenu(); //return to menu
		}

		System.out.println("Thank you!!");
		myController.addSalesReport(record, price);
		System.out.println("Sales Report Updated");
		System.out.println("Status Updated!!!....");
		//Update the status of the record in the database to sold.
		myController.updateRecordStatus(record, myController.SOLD);
		displayAllRecords();

		return null;

	}

	private Record getPriceWithRecID() {

		boolean inputOK = false;
		int recID = 0;


		displayAllRecords();
		
		System.out.println("Enter Record ID");

		while (!inputOK) {
			try {
				recID = Integer.parseInt(s.next());
			} catch (NumberFormatException nfe){
				System.out.println("RecordID must be an integer value. Please try again.");
				continue;
			}

			Record record = myController.GetRecordID(recID);
			//when selling a record a record can only be new or in the basement
			if (record==null || record.status==myController.SOLD || record.status==myController.PAID_CONSIGNOR ||
						record.status==myController.NOTIFIED || record.status==myController.RETURNED_TO_CONSIGNOR ||
						record.status==myController.DONATED){
				System.out.println("Record Can't be Sold or is not in database");
				return null;
			} else {

					
				return record;
			}
		}
		return null;
	}
	private void displayAllConsignors() {

		LinkedList<Consignor> allCosignors = myController
				.requestAllConsignors();
		if (allCosignors == null) {
			System.out
			.println("Error fetching all Consignors from the database");
		} else if (allCosignors.isEmpty()) {
			System.out.println("No Cosignors found in database");
		} else {
			for (Consignor c : allCosignors) { // CALLS METHOD TO DISPLAY
				// CONSIGNORS
				System.out.println(c);
			}
		}
	}

	private void displayAllRecords() {
		LinkedList<Record> allRecords = myController.requestAllRecords();
		if (allRecords == null) {
			System.out.println("Error fetching all Records from the database");
		} else if (allRecords.isEmpty()) {
			System.out.println("No Records found in database");
		} else {
			for (Record r : allRecords) {//call method to display records
				System.out.println(r);
			}
		}

	}

	private void displayAllSalesReports() {
		System.out.println("_______SALES_REPORT______");
		LinkedList<SalesReport> allReports = myController.requestAllReports();
		if (allReports == null) {
			System.out.println("Error fetching all Reports from the database");
		} else if (allReports.isEmpty()) {
			System.out.println("No Reports found in database");
		} else {
			for (SalesReport r : allReports) {//display sales report
				System.out.println(r);
			}
		}
	}

	private int displayMenuGetUserChoice() {

		boolean inputOK = false;
		int userChoice = -1;

		while (!inputOK) {
			System.out.println("_____MAIN_______MENU____");
			System.out.println("1. View all Consignors");
			System.out.println("2. Add New Consignor");
			System.out.println("3. View All Records");
			System.out.println("4. Add New Record");
			System.out.println("5. Search Record With ID");
			System.out.println("6. Operations Menu");
			System.out.println(QUIT + ". Quit program");

			System.out.println();
			System.out.println("Please enter your selection");

			String userChoiceStr = s.next();
			try {
				userChoice = Integer.parseInt(userChoiceStr);
				if (userChoice < 1 || userChoice > 7) {
					System.out.println("Please enter a number between 1 and 7");
					continue;
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Please enter a number");
				displayAllConsignors();
				continue;
			}
			inputOK = true;

		}

		return userChoice;

	}

	private int displayOperationsMenu() {
		boolean inputOK = false;
		int userChoice = -1;
		

		while (!inputOK) {
			System.out.println("_____OPERATIONS_____MENU____");
			System.out.println("1. Notify Consignors of unsold records");
			System.out.println("2. Pay Consignor");
			System.out.println("3. Update Records");
			System.out.println("4. Sell A Record");
			System.out.println("5. View Sales Report");
			System.out.println(QUIT + ". BACK");

			System.out.println();
			System.out.println("Please enter your selection");

			String userChoiceStr = s.next();
			try {
				userChoice = Integer.parseInt(userChoiceStr);
				if (userChoice < 1 || userChoice > 7) {
					System.out.println("Please enter a number between 1 and 7");
					continue;
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Please enter a number");
				continue;
			}
			inputOK = true;

		}

		return userChoice;

	}
}