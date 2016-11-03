package rental;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
	
	private static Map<String,Session> sessions = new HashMap<String,Session>();
	
	public static Session getSession(String name){
		
		Session out = SessionManager.getSessions().get(name);
        if (out == null) {
            throw new IllegalArgumentException("Session doesn't exist!: " + name);
        }
        return out;
	}
	
	private static Map<String,Session> getSessions(){
		return sessions;
	}
	
	public static synchronized ReservationSession createReservationSession(String name){
		sessions.put(name, new ReservationSession(name));
		return (ReservationSession) SessionManager.getSession(name);
	}
	
	public static synchronized ManagerSession createManagerSession(String name){
		sessions.put(name, new ManagerSession(name));
		return (ManagerSession) SessionManager.getSession(name);
	}
	
	public static void endSession(String name){
		sessions.remove(name);
	}

}
