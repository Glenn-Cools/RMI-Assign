package rental;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements ISessionManager{
	
	public static void main(){
		
		System.setSecurityManager(null);
		CarRentalCompany.main();
		
		try{
			SessionManager obj = new SessionManager();
			ISessionManager stub = (ISessionManager) UnicastRemoteObject.exportObject(obj, 0);
			
			// bind to remote object's stub in the registry.
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("ISessionManager", stub);
			
			System.err.println("Server rdy");
		} catch ( Exception e) {
			System.err.println("Server exp: " + e.toString());
			e.printStackTrace();
		}
		
		
	}
	
	public static boolean isReady = false;
	
	private Map<String,Session> sessions = new HashMap<String,Session>();
	
	public Session getSession(String name){
		
		Session out = getSessions().get(name);
        if (out == null) {
            throw new IllegalArgumentException("Session doesn't exist!: " + name);
        }
        return out;
	}
	
	private Map<String,Session> getSessions(){
		return sessions;
	}
	
	public synchronized ReservationSession createReservationSession(String name){
		sessions.put(name, new ReservationSession(name));
		return (ReservationSession) getSession(name);
	}
	
	public synchronized ManagerSession createManagerSession(String name,String companyName){
		sessions.put(name, new ManagerSession(name,companyName));
		return (ManagerSession) getSession(name);
	}
	
	public void endSession(String name){
		sessions.remove(name);
	}

}
