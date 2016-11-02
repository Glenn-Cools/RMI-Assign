package rental;

import java.util.Set;
import java.util.HashSet;

public class Session {
	
	public Session(){
		
	}
	
	public Set<String>getAllRentalCompanies(){
		return new HashSet<String>(NamingService.getRentals().keySet());
	}
	private String sessionName;
	
	public void setSessionName(String name){
		sessionName = name;
	}
	
	public String getSessionName(){
		return sessionName;
	}
	
	//Wat moet een session nog meer hebben?
	//Ook de getAllRentalCompanies Implementeren analoog aan netbeans?
}
