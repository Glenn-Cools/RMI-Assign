package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ICarRentalCompany extends Remote {
	
	/********
	 * NAME *
	 ********/
	public String getName() throws RemoteException;
	
	 /***********
     * Regions *
     **********/
	public List<String> getRegions() throws RemoteException;
	public boolean hasRegion(String region) throws RemoteException;
	
	/*************
	 * CAR TYPES *
	 *************/
	public ArrayList<CarType> getAllCarTypes() throws RemoteException;
	public CarType getCarType(String carTypeName) throws RemoteException;
	public boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException;
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
	
	/****************
	 * RESERVATIONS *
	 ****************/
	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException, RemoteException;
	public Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;
	public void cancelReservation(Reservation res) throws RemoteException;
	public List<Reservation> getReservationsByRenter(String clientName) throws RemoteException;
	public List<Reservation> getReservationsByCarType(String carType) throws RemoteException;
	

}
