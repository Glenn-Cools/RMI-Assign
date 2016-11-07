package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReservationSession extends Session {

	public ReservationSession(String name) {
		super(name);
	}

	private HashMap<Quote, ICarRentalCompany> quotes = new HashMap<Quote, ICarRentalCompany>();

	public Quote createQuote(Date start, Date end, String carType, String region)
			throws ReservationException, RemoteException {
		ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);

		ArrayList<ICarRentalCompany> companyList = new ArrayList<ICarRentalCompany>();
		for (String companyName : getAllRentalCompanies()) {
			companyList.add(NamingService.getRental(companyName));
		}

		ICarRentalCompany company = checkAvailableCarType(constraints, companyList);
		if (company == null) {
			throw new ReservationException("No available cars of that type");
		}

		Quote quote = company.createQuote(constraints, getSessionName());
		quotes.put(quote, company);
		return quote;
	}

	public ArrayList<Quote> getCurrentQuotes() {
		ArrayList<Quote> quoteList = new ArrayList<Quote>();
		for (Map.Entry<Quote, ICarRentalCompany> entry : quotes.entrySet()) {
			quoteList.add(entry.getKey());
		}
		return quoteList;
	}

	public ArrayList<Reservation> confirmQuote() throws ReservationException, RemoteException {
		ArrayList<Reservation> reservations = new ArrayList<Reservation>();
		try {
			for (Map.Entry<Quote, ICarRentalCompany> entry : quotes.entrySet()) {
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

	public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
		String cheapestCarType = null;
		double cheapestPrice = Double.POSITIVE_INFINITY;

		Map<String, ICarRentalCompany> companies = NamingService.getRentals();
		for (ICarRentalCompany company : companies.values()) {
			if (company.hasRegion(region)) {
				String carType = company.getCheapestCarType(start, end);
				double price = company.getCarType(carType).getRentalPricePerDay();
				if (price < cheapestPrice) {
					cheapestCarType = carType;
					cheapestPrice = price;
				}
			}
		}

		return cheapestCarType;
	}

}
