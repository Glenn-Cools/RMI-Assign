package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NamingService {
		
	private static Map<String, ICarRentalCompany> rentals = new HashMap<String, ICarRentalCompany>();

	public static void registerCompany(String companyName) throws RemoteException {

		ICarRentalCompany stub;
		try {
			Registry registry = LocateRegistry.getRegistry();
			stub = (ICarRentalCompany) registry.lookup(companyName);

		} catch (Exception e) {
			System.err.println("Client exp: " + e.toString());
			e.printStackTrace();
			stub = null;
		}

		rentals.put(companyName, stub);

	}

	public static void unregisterCompany(String registryName) {
		rentals.remove(registryName);
	}

	public static ICarRentalCompany getRental(String companyName)  {

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

	public static synchronized Map<String, ICarRentalCompany> getRentals()  {

		return new HashMap<String,ICarRentalCompany>(rentals);
	}

}
