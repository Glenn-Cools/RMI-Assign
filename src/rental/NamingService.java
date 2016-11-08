package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NamingService {
	private static Logger logger = Logger.getLogger(NamingService.class.getName());
	public static NamingService namingService;
	
	public static void main(String args[]) {
		
		
		System.setSecurityManager(null);
		
		try {
			CarRentalCompany.main();

			// preload hertz and dockx since they are already up and running
			NamingService.namingService = new NamingService();
			namingService.registerCompany("Hertz");
			namingService.registerCompany("Dockx");
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		SessionManager.isReady = true;
		SessionManager.main();
		
	}

	private Map<String, ICarRentalCompany> rentals = new HashMap<String, ICarRentalCompany>();

	public  void registerCompany(String registryName) throws RemoteException {

		ICarRentalCompany stub;
		try {
			Registry registry = LocateRegistry.getRegistry();
			stub = (ICarRentalCompany) registry.lookup(registryName);

		} catch (Exception e) {
			System.err.println("Client exp: " + e.toString());
			e.printStackTrace();
			stub = null;
		}

		rentals.put(registryName, stub);

	}

	public void unregisterCompany(String registryName) {
		rentals.remove(registryName);
	}

	public ICarRentalCompany getRental(String companyName) throws RemoteException {

		ICarRentalCompany out = null;

		for (ICarRentalCompany company : getRentals().values()) {
			try {
				if (company.getName().equals(companyName)) {
					return company;
				}
			} catch (RemoteException e) {
				// company (remote) wasn't available => remote exp
			}
		}
		return out;
	}

	public synchronized Map<String, ICarRentalCompany> getRentals() throws RemoteException  {
		if(rentals == null){
			
			rentals = new HashMap<String, ICarRentalCompany>();
			namingService.registerCompany("Hertz");
			namingService.registerCompany("Dockx");
			
		}
		logger.log(Level.INFO,"Rentals "+ rentals.get("Hertz").getName()) ;
		return rentals;
	}

}
