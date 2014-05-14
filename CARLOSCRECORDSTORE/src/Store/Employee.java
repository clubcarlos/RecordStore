package Store;

public class Employee {

	int EmployeeID;
	String userName;
	String password;
	int employeeType;
	

	public int getEmployeeID() {
		return EmployeeID;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	public int getEmployeeType() {
		return employeeType;
	}
	public Employee(int EID, String uName, String password, int eType){
		this.EmployeeID = EID;
		this.userName = uName;
		this.password = password;
		this.employeeType = eType;
	}
	public String toString() {
		String eType = "";
		
		switch (employeeType){
		case (8) : {
			eType = "Clerk";
			break;
		} case (9) : {
			eType = "Manager";
			break;
		}
		}
		return "Employee ID: " + EmployeeID+ " UserName: " +userName+ "   Password: " + "  Employee Type: "
		+ eType;
	}

}
