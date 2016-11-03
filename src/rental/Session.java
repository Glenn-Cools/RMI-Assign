package rental;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

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
	
	protected CarRentalCompany checkAvailableCarType(ReservationConstraints constraints, ArrayList<CarRentalCompany> companies){
		for (CarRentalCompany company: companies){
			Set<CarType> availableCars = company.getAvailableCarTypes(constraints.getStartDate(), constraints.getEndDate());
			try{
				if(availableCars.contains(company.getCarType(constraints.getCarType()))){
					return company;
				}
			}catch(IllegalArgumentException e){
				
			}
		}
		return null;
		// deze method toch in super he ? (zie comment in ReservationSession l29), Protected zodat subclassen er gebruik van kan maken
		// mss rest ook protected maken ? 
	}
	
	
	//Wat moet een session nog meer hebben?  => mss een destroy session? => Lost automatisch GC dit niet op ?
	
}
