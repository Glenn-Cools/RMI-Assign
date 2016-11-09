package rental;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CarRentalCompany implements ICarRentalCompany{

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
	
	private List<String> regions;
	private String name;
	private List<Car> cars;
	private Map<String,CarType> carTypes = new HashMap<String, CarType>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		setRegions(regions);
		for(Car car:cars)
			carTypes.put(car.getType().getName(), car.getType());
		logger.log(Level.INFO, this.toString());
	}
	
	public static void main() {
		
		System.setSecurityManager(null);
		
		registerCompany("hertz.csv");
		registerCompany("dockx.csv");
	}
	
	private static void registerCompany(String filename){
		try{
			CarRentalCompany obj = RentalServer.construct(filename);
			ICarRentalCompany stub = (ICarRentalCompany) UnicastRemoteObject.exportObject(obj, 0);
			
			// bind to remote object's stub in the registry.
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(obj.getName(), stub);
			
			System.err.println("Server rdy");
		} catch ( Exception e) {
			System.err.println("Server exp: " + e.toString());
			e.printStackTrace();
		}
		
	}

	/********
	 * NAME *
	 ********/

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

    /***********
     * Regions *
     **********/
    private void setRegions(List<String> regions) {
        this.regions = regions;
    }
    
    public List<String> getRegions() {
        return this.regions;
    }
    
    public boolean hasRegion(String region) {
        return this.regions.contains(region);
    }
	
	/*************
	 * CAR TYPES *
	 *************/

	public ArrayList<CarType> getAllCarTypes() {
		return new ArrayList<CarType>(carTypes.values());
	}
	
	public Set<String> getAllCarTypeNames(){
		return new HashSet<String>(carTypes.keySet());
	}
	
	public CarType getCarType(String carTypeName) {
		if(carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	// mark
	public boolean isAvailable(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if(carTypes.containsKey(carTypeName)) {
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		} else {
			throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
		}
	}
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}
	
	public String getCheapestCarType(Date start, Date end){
		
		String cheapestCartype = null;
		double cheapestPrice = Double.POSITIVE_INFINITY;
		Set<CarType> availableCarTypes = getAvailableCarTypes(start, end);
		
		for(CarType type: availableCarTypes){
			double price = type.getRentalPricePerDay();
			if(price < cheapestPrice){
				cheapestCartype = type.getName();
				cheapestPrice = price;
			}
		}
		
		return cheapestCartype;
	}
	
	/*********
	 * CARS *
	 *********/
	
	private Car getCar(int uid) {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}
	
	private List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}

	/****************
	 * RESERVATIONS *
	 ****************/

	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}", 
                        new Object[]{name, client, constraints.toString()});
		
				
		if(!regions.contains(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
			throw new ReservationException("<" + name
				+ "> No cars available to satisfy the given constraints.");

		CarType type = getCarType(constraints.getCarType());
		
		double price = calculateRentalPrice(type.getRentalPricePerDay(),constraints.getStartDate(), constraints.getEndDate());
		
		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
						/ (1000 * 60 * 60 * 24D));
	}

	public Reservation confirmQuote(Quote quote) throws ReservationException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if(availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
	                + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = availableCars.get((int)(Math.random()*availableCars.size()));
		
		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		return res;
	}
	
	public List<Reservation> getReservationsByRenter(String clientName){
		
		List<Reservation> renterRes = new ArrayList<Reservation>();
		
		for (Car car : cars){
			
			List<Reservation> res = car.getReservations();
			for (Reservation r: res){
				if(r.getCarRenter().equals(clientName)){
					renterRes.add(r);
				}
			}
		}
		
		return renterRes;
		
	}
	
	public List<Reservation> getReservationsByCarType(String carType){
		
		List<Reservation> carTypeRes = new ArrayList<Reservation>();
		
		for (Car car : cars){
			
			List<Reservation> res = car.getReservations();
			for (Reservation r: res){
				
				if(r.getCarType().equals(carType)){
					carTypeRes.add(r);
				}
			}
		}
		
		return carTypeRes;
		
	}
	
	public List<Reservation> getReservationsByCarTypeIn(String carType, int year) {

		List<Reservation> carTypeRes = new ArrayList<Reservation>();

		for (Car car : cars) {

			List<Reservation> res = car.getReservations();
			for (Reservation r : res) {

				if (r.getCarType().equals(carType) && testInYear(year, r.getStartDate())) {
					carTypeRes.add(r);
				}
			}
		}

		return carTypeRes;

	}

	private boolean testInYear(int year, Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR) == year;

	}

	public CarType getMostPopularCarTypeIn(int year) {

		CarType carType = null;
		double mostPopular = 0;

		for (CarType type : getAllCarTypes()) {
			double number = getReservationsByCarTypeIn(type.getName(), year).size();
			if (number > mostPopular) {
				mostPopular = number;
				carType = type;
			}

		}

		return carType;
	}

	public Map<String, Integer> getNBOfResForAllClients() {

		Map<String, Integer> out = new HashMap<String, Integer>();

		for (String renter : getAllRenters()) {
			int NBRes = getReservationsByRenter(renter).size();
			out.put(renter, NBRes);
		}

		return out;
	}

	private Set<String> getAllRenters() {

		Set<String> renters = new HashSet<String>();

		for (Car car : cars) {

			List<Reservation> res = car.getReservations();
			for (Reservation r : res) {

				renters.add(r.getCarRenter());
			}
		}

		return renters;
	}

	public void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}
	
	@Override
	public String toString() {
		return String.format("<%s> CRC is active in regions %s and serving with %d car types", name, listToString(regions), carTypes.size());
	}
	
	private static String listToString(List<? extends Object> input) {
		StringBuilder out = new StringBuilder();
		for (int i=0; i < input.size(); i++) {
			if (i == input.size()-1) {
				out.append(input.get(i).toString());
			} else {
				out.append(input.get(i).toString()+", ");
			}
		}
		return out.toString();
	}
	
}