package rental;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;


public class ReservationSession extends Session {
	
	private HashMap<Quote, CarRentalCompany> quotes = new HashMap<Quote, CarRentalCompany>();

	public Quote createQuote(Date start, Date end, String carType, String region) throws ReservationException{
		ReservationConstraints constraints = new ReservationConstraints(start,end,carType,region);
		
		ArrayList<CarRentalCompany> companyList = new ArrayList<CarRentalCompany>();
		for(String companyName:getAllRentalCompanies()){
			companyList.add(NamingService.getRental(companyName));
		}
		
		CarRentalCompany company = checkAvailableType(constraints, companyList);
		if( company == null){
			throw new ReservationException("No available cars of that type");
		}
		
		Quote quote = company.createQuote(constraints, getSessionName());
		quotes.put(quote, company);
		return quote;
	//check availableCarType moet nog geïmplementeerd worden. Eventueel in Superclass Session ? 
		
	
	
	}
	
	
	
}
