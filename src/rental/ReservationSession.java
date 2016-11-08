package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationSession extends Session {

	private static Logger logger = Logger.getLogger(ReservationSession.class.getName());
	
	public ReservationSession(String name) {
		super(name);
	}

	private HashMap<Quote, ICarRentalCompany> quotes = new HashMap<Quote, ICarRentalCompany>();

	public Quote createQuote(Date start, Date end, String carType, String region)
			throws ReservationException, RemoteException {
		logger.log(Level.INFO, "test");
		ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
		logger.log(Level.INFO, "test2");
		ArrayList<ICarRentalCompany> companyList = new ArrayList<ICarRentalCompany>(NamingService.namingService.getRentals().values());
	
		//for (String companyName : getAllRentalCompanies()) {
			//logger.log(Level.INFO, companyName);
			//companyList.add(NamingService.getRental(companyName));
		//}
		logger.log(Level.INFO, "test4");
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
				NamingService.namingService.getRental(res.getRentalCompany()).cancelReservation(res);
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

		Map<String, ICarRentalCompany> companies = NamingService.namingService.getRentals();
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

	public Set<CarType> getAllAvailableCarTypes(Date start, Date end) throws RemoteException{
		Set<CarType> availableCars = new HashSet<CarType>();
		Collection<ICarRentalCompany> companies = NamingService.namingService.getRentals().values();
		for(ICarRentalCompany C: companies){
			availableCars.addAll(C.getAvailableCarTypes(start, end));
		}
		return availableCars;
	}
}
