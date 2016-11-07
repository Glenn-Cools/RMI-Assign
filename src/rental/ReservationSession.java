package rental;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class ReservationSession extends Session {

	public ReservationSession(String name) {
		super(name);
	}

	private HashMap<Quote, CarRentalCompany> quotes = new HashMap<Quote, CarRentalCompany>();

	public Quote createQuote(Date start, Date end, String carType, String region) throws ReservationException {
		ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);

		ArrayList<CarRentalCompany> companyList = new ArrayList<CarRentalCompany>();
		for (String companyName : getAllRentalCompanies()) {
			companyList.add(NamingService.getRental(companyName));
		}

		CarRentalCompany company = checkAvailableCarType(constraints, companyList);
		if (company == null) {
			throw new ReservationException("No available cars of that type");
		}

		Quote quote = company.createQuote(constraints, getSessionName());
		quotes.put(quote, company);
		return quote;
	}

	public ArrayList<Quote> getCurrentQuotes() {
		ArrayList<Quote> quoteList = new ArrayList<Quote>();
		for (Map.Entry<Quote, CarRentalCompany> entry : quotes.entrySet()) {
			quoteList.add(entry.getKey());
		}
		return quoteList;
	}

	public ArrayList<Reservation> confirmQuote() throws ReservationException {
		ArrayList<Reservation> reservations = new ArrayList<Reservation>();
		try {
			for (Map.Entry<Quote, CarRentalCompany> entry : quotes.entrySet()) {
				reservations.add(entry.getValue().confirmQuote(entry.getKey()));
			}
		} catch (ReservationException e) {
			for (Reservation res : reservations) {
				NamingService.getRental(res.getRentalCompany()).cancelReservation(res);
			}
			reservations.clear();
			throw new ReservationException(e.getMessage());
		}
		quotes.clear();
		return reservations;
	}

	public String getCheapestCarType(Date start, Date end, String region) {
		String cheapestCar = null;
		double cheapestPrice = Double.POSITIVE_INFINITY;
		
		Map<String, CarRentalCompany> companies = NamingService.getRentals();
		for(CarRentalCompany company: companies.values()){
			String carType = company
		}
		Set<CarType> availableCars = new HashSet<CarType>();
		availableCars = getAvailableCarTypes(start, end);
		
		for (CarType C : availableCars) {
			if (C.getRentalPricePerDay() < cheapestPrice) {
				cheapestPrice = C.getRentalPricePerDay();
				cheapestCar = C.getName();
			}

		}

		return cheapestCar;
	}

}
