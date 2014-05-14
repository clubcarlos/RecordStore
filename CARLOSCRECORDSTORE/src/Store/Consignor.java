package Store;

public class Consignor {

	protected String name;
	protected String phoneNum;
	protected Double amountOwed;
	protected  int conID; 
	
	

	public String getName() {
		return name;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public Double getAmountOwed() {
		return amountOwed;
	}

	public int getConID() {
		return conID;
	}

	public Consignor (int conID, String n, String phoneNum, Double aO ) {
		this.name = n;
		this.phoneNum = phoneNum;
		this.amountOwed = aO;
		this.conID = conID;
	}
	

	public String toString(){
		
		return("Consignor ID= " + conID + " Name : " + name + "\nPhone number: " + phoneNum + " Amount Owed: " 
				+ amountOwed );
	}
}