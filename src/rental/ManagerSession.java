package rental;

public class ManagerSession extends Session {
	
	private String companyname;

	public ManagerSession(String name,String companyName) {
		super(name);
		setCompanyName(companyName);
	}

	public String getCompanyName() {
		return companyname;
	}

	private void setCompanyName(String companyName) {
		this.companyname = companyName;
	}
	

}
