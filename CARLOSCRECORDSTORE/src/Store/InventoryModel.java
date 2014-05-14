package Store;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.NoSuchElementException;




/*
 * MAKES CONNECTION
 * SETUP DATABASE
 * CREATES TABLE
 * ADDS TEST DATA
 * ADDS DATA TO TABLES
 * VIEWS TABLES
 * UPDATES DATA IN TABLES
 * PERFORMS A CLEANUP(CLOSES STATEMENTS RESULTSET AND CLOSES DATABASE CONNECTION
 * GETS DATA FROM TABLE LIKE RECORD ID AND STATUS
 * I USED A LOT OF METHODS FROM THE LAPTOP LAB
 */




public class InventoryModel {

	// JDBC driver name, protocol, used to create a connection to the DB
	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String protocol = "jdbc:derby:";
	private static String dbName = "StoreDB";
	


	//  Database credentials - for embedded, usually defaults. A client-server DB would need to authenticate connections
	private static final String USER = "temp";
	private static final String PASS = "password";


	InventoryController myController;

	Statement statement = null;

	Connection conn = null;

	ResultSet rs = null;

	LinkedList<Statement> allStatements = new LinkedList<Statement>();

	PreparedStatement psAddConsignor = null;
	PreparedStatement psAddRecord = null;
	PreparedStatement psAddSalesReport = null;



	public InventoryModel(InventoryController controller) {

		this.myController = controller;

	}


	public boolean setupDatabase() {
		return setupDatabase(false);
	}

	public boolean setupDatabase(boolean deleteAndRecreate) {
		

		try {
			createConnection();
			
		} catch (Exception e) {
			
			System.err.println("Unable to connect to database. Error message and stack trace follow");
			System.err.println(e.getMessage());
			e.printStackTrace();
			return false;
		}


		try {
			createTables(deleteAndRecreate);
		} catch (SQLException sqle) {
			System.err.println("Unable to create database. Error message and stack trace follow");
			System.err.println(sqle.getMessage() + " " + sqle.getErrorCode());
			sqle.printStackTrace();
			return false;
		}


		
		try {
			addTestData();
		}
		catch (Exception sqle) {

			System.err.println("Unable to add test data to database. Error message and stack trace follow");
			System.err.println(sqle.getMessage());
			sqle.printStackTrace();
			return false;


		}

		//At this point, it seems like everything worked.

		return true;
	}


	private void createTables(boolean deleteAndRecreate ) throws SQLException {

		//CREATES CONSIGNOR AND RECORD TABLES
		String createConsignorTableSQL = "CREATE TABLE Consignors (conID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY ,"
				+ " Name varchar(30), Phone varchar(10), MoneyOwed double)";
		String deleteConTableSQL = "DROP TABLE Consignors";
		
		
		
		
		try {	
			statement.executeUpdate(createConsignorTableSQL);
			System.out.println("Created Consignor table");	

		} catch (SQLException sqle) {
			//Seems the table already exists, or some other error has occurred. 
			//Let's try to check if the DB exists already by checking the error code returned. If so, delete it and re-create it


			if (sqle.getSQLState().startsWith("X0") ) {    //Error code for table already existing starts with XO
				if (deleteAndRecreate == false) {

					System.out.println("Consignors table appears to exist already, delete and recreate");
					try {
						statement.executeUpdate(deleteConTableSQL);
						statement.executeUpdate(createConsignorTableSQL);
					} catch (SQLException e) {
						//Still doesn't work. Throw the exception. 
						throw e;
					}
				} else {
					//do nothing - if the table exists, leave it be. 
				}

			} else {
				//Something else went wrong. If we can't create the table, no point attempting 
				//to run the rest of the code. Throw the exception again to be handled elsewhere. of the program. 
				throw sqle;
			}
		}
		//THE RECID AUTOMATICALLY GENERATES A RECID NUMBER
		String createRecordTableSQL = "CREATE TABLE Records (recID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, conID int, "
				+ "Price double, DateAdded date, Title varchar(30), Artist varchar(30), Status int)";
		String deleteRecTableSQL = "DROP TABLE Records";
		
		
		try {	
			statement.executeUpdate(createRecordTableSQL);
			System.out.println("Created Record table");	

		} catch (SQLException sqle) {
			//Seems the table already exists, or some other error has occurred. 
			//Let's try to check if the DB exists already by checking the error code returned. If so, delete it and re-create it


			if (sqle.getSQLState().startsWith("X0") ) {    //Error code for table already existing starts with XO
				if (deleteAndRecreate == false) {

					System.out.println("Records table appears to exist already, delete and recreate");
					try {
						statement.executeUpdate(deleteRecTableSQL);
						statement.executeUpdate(createRecordTableSQL);
					} catch (SQLException e) {
						//Still doesn't work. Throw the exception. 
						throw e;
					}
				} else {
					//do nothing - if the table exists, leave it be. 
				}

			} else {
				//Something else went wrong. If we can't create the table, no point attempting 
				//to run the rest of the code. Throw the exception again to be handled elsewhere. of the program. 
				throw sqle;
			}
		}
		String createSalesReportTableSQL = "CREATE TABLE SalesReport (ReportID int PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
				+ "RecID int, Sale_Date date, Sale_Price double) ";
		String deleteSalesReportTableSQL = "DROP TABLE SalesReport";
		
		try {	
			statement.executeUpdate(createSalesReportTableSQL);
			System.out.println("Created Sales Report table");	

		} catch (SQLException sqle) {
			//Seems the table already exists, or some other error has occurred. 
			//Let's try to check if the DB exists already by checking the error code returned. If so, delete it and re-create it


			if (sqle.getSQLState().startsWith("X0") ) {    //Error code for table already existing starts with XO
				if (deleteAndRecreate == false) {

					System.out.println("Sales Report table appears to exist already, delete and recreate");
					try {
						statement.executeUpdate(deleteSalesReportTableSQL);
						statement.executeUpdate(createSalesReportTableSQL);
					} catch (SQLException e) {
						//Still doesn't work. Throw the exception. 
						throw e;
					}
				} else {
					//do nothing - if the table exists, leave it be. 
				}

			} else {
				//Something else went wrong. If we can't create the table, no point attempting 
				//to run the rest of the code. Throw the exception again to be handled elsewhere. of the program. 
				throw sqle;
			}
		}
		
		
		
	}

	private void createConnection() throws Exception {//MAKES CONNECTION

		try {
			Class.forName(driver);  //Instantiate a driver object
			conn = DriverManager.getConnection(protocol + dbName + ";create=true", USER, PASS);
			statement = conn.createStatement();
			allStatements.add(statement);
		} catch (Exception e) {
			cleanup();
		}

	}

	private void addRecordData() throws Exception {
		
		//CREATES TEST DATA FOR RECORD TABLE
	
		String addRecord1 = "INSERT INTO Records (conID, Price, DateAdded, Title, Artist, Status) "
				+ "VALUES (1, 7.01, '3/27/2014', 'Man In The Mirror', 'Michael Jackson', " + myController.NEW + " )";
		statement.executeUpdate(addRecord1);
		
		String addRecord2 = "INSERT INTO Records (conID, Price, DateAdded, Title, Artist, Status) "
				+ "VALUES (2, 5.99, '3/29/2013', 'Beat It', 'Michael Jackson', " + myController.NEW + " )";
		statement.executeUpdate(addRecord2);
		
		String addRecord3 = "INSERT INTO Records (conID, Price, DateAdded, Title, Artist, Status) "
				+ "VALUES (3, 4.50, '3/30/2013', 'Thriller', 'Michael Jackson', " + myController.BASEMENT + " )";
		statement.executeUpdate(addRecord3);
		
		String addRecord4 = "INSERT INTO Records (conID, Price, DateAdded, Title, Artist, Status) "
				+ "VALUES (3, 6.50, '4/04/2014', 'Tupac', 'California Love', " + myController.SOLD + " )";
		statement.executeUpdate(addRecord4);
	
	}
	private void addSalesReportData() throws Exception {
		
		String addSalesReport1 = "INSERT INTO SalesReport (RecID, Sale_Date, Sale_Price) "
				+ "VALUES (1, '4/23/2014', 7.01)";
		statement.executeUpdate(addSalesReport1);
		
		String addSalesReport2 = "INSERT INTO SalesReport (RecID, Sale_Date, Sale_Price) "
				+ "VALUES (2, '4/25/2014', 5.99)";
		statement.executeUpdate(addSalesReport2);
		
		String addSalesReport3 = "INSERT INTO SalesReport (RecID, Sale_Date, Sale_Price) "
				+ "VALUES (3, '4/28/2014', 4.50)";
		statement.executeUpdate(addSalesReport3);
		
	}
	
	private void addConsignorData() throws Exception {
		String addConsignorRecord = "INSERT INTO Consignors(Name, Phone, MoneyOwed) VALUES (?, ?, ?)";
		psAddConsignor = conn.prepareStatement(addConsignorRecord);
		
		//CREATES TEST DATA FOR CONSIGNOR TABLE
		psAddConsignor.setString(1, "Carlos Contreras");
		psAddConsignor.setString(2, "6123232223");
		psAddConsignor.setDouble(3, 4.99);
		psAddConsignor.execute();
		
		
		psAddConsignor.setString(1, "Ricky Ricardo");
		psAddConsignor.setString(2, "9522343432");
		psAddConsignor.setDouble(3, 24.45);
		psAddConsignor.execute();
		
		
		psAddConsignor.setString(1, "Richard Price");
		psAddConsignor.setString(2, "6545543454");
		psAddConsignor.setDouble(3, 2.50);
		psAddConsignor.execute();
	}

	private void addTestData() throws Exception {
		// Test data. 
		if (statement == null) {
			//This isn't going to work
			throw new Exception("Statement not initialized"); 
		}
		try {//ADDS TEST DATA TO BOTH TABLES
			addRecordData();
			addConsignorData();
			addSalesReportData();
		
			
		}

		catch (SQLException sqle) {// CATCHES IF SOMETHING WENT WRONG
			System.err.println("Unable to add test data, check validity of SQL statements?");
			System.err.println("Unable to create database. Error message and stack trace follow");
			System.err.println(sqle.getMessage() + " " + sqle.getErrorCode());
			sqle.printStackTrace();

			throw sqle;
		}
	}

	public void cleanup() {
	
		try {
			if (rs != null) {
				rs.close();  //Close result set
				System.out.println("ResultSet closed");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		//Close all of the statements. Stored a reference to each statement in allStatements so we can loop over all of them and close them all. 
		for (Statement s : allStatements) {

			if (s != null) {
				try {
					s.close();
					System.out.println("Statement closed");
				} catch (SQLException se) {
					System.out.println("Error closing statement");
					se.printStackTrace();
				}
			}
		}

		try {
			if (conn != null) {
				conn.close();  //Close connection to database
				System.out.println("Database connection closed");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public boolean addConsignor(Consignor C) {


		//Create SQL query to add this Consignor info to DB

		String addConsignorSQLps = "INSERT INTO Consignors (Name, Phone, MoneyOwed) VALUES (? ,? ,?)" ;
		try {
			psAddConsignor = conn.prepareStatement(addConsignorSQLps);
			allStatements.add(psAddConsignor);
			
			
			psAddConsignor.setString(1, C.getName());
			psAddConsignor.setString(2, C.getPhoneNum());
			psAddConsignor.setDouble(3, C.getAmountOwed());
			psAddConsignor.execute();
		}
		catch (SQLException sqle) {
			System.err.println("Error preparing statement or executing prepared statement to add Consignor");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean addRecord(Record r) {


		//Create SQL query to add this Record info to DB

		String addRecordSQLps = "INSERT INTO Records (conID, Price, DateAdded, Title, Artist, Status) VALUES (? ,? ,? ,? ,? ,? )" ;
		try {
			psAddRecord = conn.prepareStatement(addRecordSQLps);
			allStatements.add(psAddRecord);
			
			
			psAddRecord.setInt(1, r.getConID());
			psAddRecord.setDouble(2, r.getPrice());
			psAddRecord.setDate(3, r.getDate());
			psAddRecord.setString(4, r.getTitle());
			psAddRecord.setString(5, r.getArtist());
			psAddRecord.setInt(6, r.getStatus());
			psAddRecord.execute();
		}
		catch (SQLException sqle) {
			System.err.println("Error preparing statement or executing prepared statement to add Record");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return false;
		}
		return true;
	}
	
	

	public boolean addSalesReport(Record r, double price) {
		
		
		String addSalesReportSQL = "INSERT INTO SalesReport (RecID, Sale_Date, Sale_Price) "
				+ "VALUES (?,?,?)";
			try {
				psAddSalesReport = conn.prepareStatement(addSalesReportSQL);
				allStatements.add(psAddSalesReport);
				Date date = new java.sql.Date(System.currentTimeMillis());
				
				
				psAddSalesReport.setInt(1, r.getRecID());
				psAddSalesReport.setDate(2, date);
				psAddSalesReport.setDouble(3, price);
				
				psAddSalesReport.execute();
			}
				catch (SQLException sqle) {
					System.err.println("Error preparing statement or executing prepared statement to add Record");
					System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
					sqle.printStackTrace();
					return false;
				}
				return true;
		
	}


	public LinkedList<Consignor> displayAllConsignors() {//DISPLAY DATA IN CONSIGNOR TABLE

		LinkedList<Consignor> allConsignors = new LinkedList<Consignor>();

		String displayAll = "SELECT * FROM Consignors";
		try {
			rs = statement.executeQuery(displayAll);
		}
		catch (SQLException sqle) {
			System.err.println("Error fetching all Consignors");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return null;
		}


		try {
			while (rs.next()) {
				int conID = rs.getInt("conID");
				String Name = rs.getString("Name");
				String Phone = rs.getString("Phone");
				Double Money = rs.getDouble("MoneyOwed");
				Consignor c = new Consignor(conID, Name, Phone, Money );
				
				allConsignors.add(c);

			}
		} catch (SQLException sqle) {
			System.err.println("Error reading from result set after fetching all Consignor data");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return null;

		}

		//if we get here, everything should have worked...
		//Return the list of Consignors, which will be empty if there is no data in the database
		return allConsignors;
	}
	
	
	public LinkedList<Record> displayAllRecords() { //DISPLAYS DATA IN RECORD TABLES

		LinkedList<Record> allRecords = new LinkedList<Record>();

		String displayAll = "SELECT * FROM Records";
		try {
			rs = statement.executeQuery(displayAll);
		}
		catch (SQLException sqle) {
			System.err.println("Error fetching all Records");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return null;
		}


		try {
			while (rs.next()) {
				
				int recID = rs.getInt("recID");
				int conID = rs.getInt("conID");
				double Price = rs.getDouble("Price");
				Date date = rs.getDate("DateAdded");
				String title = rs.getString("Title");
				String artist = rs.getString("Artist");
				int status = rs.getInt("Status");
				Record r = new Record(recID, conID, Price, date, title, artist, status);
				
				allRecords.add(r);

			}
		} catch (SQLException sqle) {
			System.err.println("Error reading from result set after fetching all Consignor data");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return null;

		}

		//if we get here, everything should have worked...
		//Return the list of Records, which will be empty if there is no data in the database
		return allRecords;
	}
	public LinkedList<SalesReport> displayAllReports() { //DISPLAYS DATA IN RECORD TABLES

		LinkedList<SalesReport> allReports = new LinkedList<SalesReport>();

		String displayAll = "SELECT * FROM SalesReport";
		try {
			rs = statement.executeQuery(displayAll);
		}
		catch (SQLException sqle) {
			System.err.println("Error fetching all Reports");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return null;
		}
		
		try {
			while (rs.next()) {
				
				int reportID = rs.getInt("ReportID");
				int recID = rs.getInt("RecID");
				Date saleDate = rs.getDate("Sale_Date");
				double salePrice = rs.getDouble("Sale_Price");
				SalesReport r = new SalesReport(reportID,recID, saleDate, salePrice);
				
				allReports.add(r);

			}
		} catch (SQLException sqle) {
			System.err.println("Error reading from result set after fetching all Report data");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return null;

		}

		//if we get here, everything should have worked...
		//Return the list of Records, which will be empty if there is no data in the database
		return allReports;
	}
	
	
	public Consignor getConsignorByID(int conID) { //RETRIEVES CONSIGNOR ID FROM CONSIGNOR TABLE
		String getConsignorByIDSQL = "SELECT * FROM Consignors "
				+ "WHERE conID = " + conID;
		
		LinkedList<Consignor> results = new LinkedList<Consignor>();
		
		try {
			rs = statement.executeQuery(getConsignorByIDSQL);
			while (rs.next()){
				int cconID = rs.getInt("conID");
				String Name = rs.getString("Name");
				String Phone = rs.getString("Phone");
				Double Money = rs.getDouble("MoneyOwed");
				Consignor c = new Consignor(cconID, Name, Phone, Money );
				
				results.add(c);
			}
		}
		catch (SQLException sqle) {
			System.err.println("Error preparing statement or executing prepared statement to vew table");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return null;
		}
		
		Consignor consignor = null;
		
		try {
			consignor = (Consignor) results.pop();
		} catch (NoSuchElementException nsee){
			consignor = null;
			System.out.println("Consignor not found in database.");
		}

		return consignor;
	}
	public Record getRecordID(int recID) { //RETRIEVES RECORD ID FROM RECORDS TABLE
		String getRecordIDSQL = "SELECT * FROM Records "
				+ "WHERE recID = " + recID;
		
		LinkedList<Record> results = new LinkedList<Record>();
		
		try {
			rs = statement.executeQuery(getRecordIDSQL);
			while (rs.next()){
				int rrecID = rs.getInt("recID");
				int conID = rs.getInt("conID");
				double Price = rs.getDouble("Price");
				Date date = rs.getDate("DateAdded");
				String title = rs.getString("Title");
				String artist = rs.getString("Artist");
				int status = rs.getInt("Status");
				Record r = new Record(rrecID, conID, Price, date, title, artist, status);
				
				results.add(r);
			}
		}
		catch (SQLException sqle) {
			System.err.println("Error preparing statement or executing prepared statement to vew table");
			System.out.println(sqle.getErrorCode() + " " + sqle.getMessage());
			sqle.printStackTrace();
			return null;
		}
		
		Record record = null;
		
		try {
			record = (Record) results.pop();
		} catch (NoSuchElementException nsee){
			record = null;
			System.out.println("Record not found in database.");
		}

		return record;
	}
	

	public Record updateRecordStatus (Record r, int status) {
		String updateRecordStatusSQL = "UPDATE Records SET Status = "+ status +" WHERE recID = " + r.recID; 
		
		try {
			statement.executeUpdate(updateRecordStatusSQL);
		} catch (SQLException e) {
			System.err.println(e);
			e.getMessage();
			e.printStackTrace();
		} Record record = null;
		return record;
	}
	public Record updateRecordPrice (Record r, double price) {
		String updateRecordStatusSQL = "UPDATE Records SET Price = "+ price +" WHERE recID = " + r.recID; 
		
		try {
			statement.executeUpdate(updateRecordStatusSQL);
		} catch (SQLException e) {
			System.err.println(e);
			e.getMessage();
			e.printStackTrace();
		} Record record = null;
		return record;
	}
	public Consignor updateConsignorMoneyOwed (Consignor c, double MoneyOwed) {
		String updateConsignorMoneySQL = "UPDATE Consignors SET MoneyOwed = "+ MoneyOwed +" WHERE conID = " + c.conID; 
		
		try {
			statement.executeUpdate(updateConsignorMoneySQL);
		}catch (SQLException e) {
			System.err.println(e);
			e.getMessage();
			e.printStackTrace();
		} Consignor consignor = null;
		return consignor;
		
		
	}
	
	public LinkedList<Record> viewRecordsByStatus(int statusToView) {
		String viewRecordsByStatusSQL = "SELECT * FROM Records WHERE STATUS = " + statusToView;		
		LinkedList<Record> recordResults = new LinkedList<Record>();
		
		try {
			rs = statement.executeQuery(viewRecordsByStatusSQL);
			while (rs.next()){
				int rrecID = rs.getInt("recID");
				int conID = rs.getInt("conID");
				double Price = rs.getDouble("Price");
				Date date = rs.getDate("DateAdded");
				String title = rs.getString("Title");
				String artist = rs.getString("Artist");
				int status = rs.getInt("Status");
				Record r = new Record(rrecID, conID, Price, date, title, artist, status);
				
				recordResults.add(r);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error viewing record status");
			return null;
		}
		return recordResults;
	}
}



