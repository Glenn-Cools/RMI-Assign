package rental;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Session implements Serializable{
	
	public Session(String name){
		setSessionName(name);
		
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
	
	protected ICarRentalCompany checkAvailableCarType(ReservationConstraints constraints, ArrayList<ICarRentalCompany> companies) throws RemoteException{
		
		for (ICarRentalCompany company: companies){
			Set<CarType> availableCars = company.getAvailableCarTypes(constraints.getStartDate(), constraints.getEndDate());
			try{
				if(availableCars.contains(company.getCarType(constraints.getCarType()))){
					return company;
				}
			}catch(IllegalArgumentException e){
				
			}
		}
		return null;
	}
}
