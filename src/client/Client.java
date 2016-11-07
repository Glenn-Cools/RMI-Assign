package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

import rental.CarType;
import rental.ISessionManager;
import rental.ManagerSession;
import rental.Reservation;
import rental.ReservationSession;
import rental.Session;

public class Client extends AbstractTestManagement<ReservationSession,ManagerSession> {

	public Client(String scriptFile) {
		super(scriptFile);
		// TODO Auto-generated constructor stub
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static ISessionManager getSessionManager(){
		
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
	
	
	@Override
	protected Set<String> getBestClients(ManagerSession ms) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType,
			String region) throws RemoteException {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected List<Reservation> confirmQuotes(ReservationSession session, String name) throws RemoteException {
		// TODO Auto-generated method stub
		
		//End Session?
		return null;
	}


	@Override
	protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	protected void endSession(Session session) throws RemoteException{
		getSessionManager().endSession(session.getSessionName());
		
	}

	
	

}
