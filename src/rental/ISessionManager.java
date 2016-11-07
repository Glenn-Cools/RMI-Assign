package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISessionManager extends Remote {
	
	public Session getSession(String name) throws RemoteException;
	public ReservationSession createReservationSession(String name) throws RemoteException;
	public ManagerSession createManagerSession(String name,String companyName) throws RemoteException;
	public void endSession(String name) throws RemoteException;

}
