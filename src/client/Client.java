package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import rental.CarType;
import rental.ISessionManager;
import rental.ManagerSession;
import rental.Quote;
import rental.Reservation;
import rental.ReservationException;
import rental.ReservationSession;
import rental.Session;
import rental.SessionManager;

public class Client extends AbstractTestManagement<ReservationSession,ManagerSession> {

	public Client(String scriptFile) {
		super(scriptFile);
		// TODO Auto-generated constructor stub
	}
	
	
	public static void main(String[] args) throws Exception {
		
		Client client = new Client("trips");
		// Get Hertz and Dockx preloaded
		
		ManagerSession ms  = client.getNewManagerSession("admin", "Hertz");
		client.registerCompany(ms);
		client.endSession(ms);
		ms = client.getNewManagerSession("admin", "Dockx");
		client.registerCompany(ms);
		client.endSession(ms);
		
		// Run testfile
		client.run();
	}
	
	public static ISessionManager getSessionManager(){
		
		System.setSecurityManager(null);
		ISessionManager stub;
		try {
			Registry registry = LocateRegistry.getRegistry();
			stub = (ISessionManager) registry.lookup("ISessionManager");

		} catch (Exception e) {
			System.err.println("Client exp: " + e.toString());
			e.printStackTrace();
			stub = null;
		}
		return stub;
	}

	@Override
	protected ReservationSession getNewReservationSession(String name) throws RemoteException {
		return getSessionManager().createReservationSession(name);
	}

	@Override
	protected ManagerSession getNewManagerSession(String name, String carRentalName) throws RemoteException {
		return getSessionManager().createManagerSession(name, carRentalName);
	}
	
	protected void registerCompany(ManagerSession ms) throws RemoteException{
		ms.registerCompany();
	}
		
	@Override
	protected Set<String> getBestClients(ManagerSession ms) throws RemoteException {
		return ms.getBestClients();
	}

	@Override
	protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region)
			throws RemoteException {
		String cheapestCarType = session.getCheapestCarType(start, end, region);
		return cheapestCarType;
	}

	@Override
	protected CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year)
			throws RemoteException {
		return ms.getMostPopularCarTypeIn(year);
	}

	@Override
	protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException {
		session.getAllAvailableCarTypes(start, end);
		
	}

	@Override
	protected void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType,
			String region) throws RemoteException, ReservationException {
		session.createQuote(start, end, carType, region);
		
	}

	@Override
	protected ArrayList<Reservation> confirmQuotes(ReservationSession session, String name) throws RemoteException, ReservationException {
		ArrayList<Reservation> res = session.confirmQuote();
		endSession(session);
		return res;
	}

	@Override
	protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType)
			throws RemoteException {
		return ms.getNumberOfReservationsForCarType(carType);
	}
	
	protected void endSession(Session session) throws RemoteException{
		getSessionManager().endSession(session.getSessionName());
		
	}
	
	protected ArrayList<Quote> getCurrentQuotes(ReservationSession session) throws RemoteException{
		ArrayList<Quote> quotes = session.getCurrentQuotes();
		return quotes;
	}

	
	

}
