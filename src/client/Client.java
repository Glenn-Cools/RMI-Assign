package client;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import rental.CarType;
import rental.ManagerSession;
import rental.Reservation;
import rental.ReservationSession;

public class Client extends AbstractTestManagement<ReservationSession,ManagerSession> {

	public Client(String scriptFile) {
		super(scriptFile);
		// TODO Auto-generated constructor stub
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected ReservationSession getNewReservationSession(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ManagerSession getNewManagerSession(String name, String carRentalName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	protected Set<String> getBestClients(ManagerSession ms) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType,
			String region) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected List<Reservation> confirmQuotes(ReservationSession session, String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType)
			throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	
	

}
