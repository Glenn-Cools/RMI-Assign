package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class NamingService {

	private static Map<String, ICarRentalCompany> rentals = new HashMap<String, ICarRentalCompany>();

	public static synchronized void registerCompany(String registryName) throws RemoteException {

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

	public static synchronized void unregisterCompany(String registryName) {
		rentals.remove(registryName);
	}

	public static ICarRentalCompany getRental(String companyName) {

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

	public static Map<String, ICarRentalCompany> getRentals() {
		return rentals;
	}

}
