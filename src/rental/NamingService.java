package rental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NamingService {

	private static Map<String, CarRentalCompany> rentals = new HashMap<String, CarRentalCompany>();
	private static Map<String, ICarRentalCompany> rentals2 = new HashMap<String, ICarRentalCompany>();

	public static synchronized void registerCompany(String filename) {
		loadRental(filename);
	}

	public static synchronized void registerCompanyNew(String registryName) throws RemoteException {

		ICarRentalCompany stub;
		try {
			Registry registry = LocateRegistry.getRegistry();
			stub = (ICarRentalCompany) registry.lookup(registryName);

		} catch (Exception e) {
			System.err.println("Client exp: " + e.toString());
			e.printStackTrace();
			stub = null;
		}

		rentals2.put(registryName, stub);

	}

	public static synchronized void unregisterCompany(String name) {
		rentals.remove(name);
	}

	public static synchronized void unregisterCompanyNew(String registryName) {
		rentals2.remove(registryName);
	}

	public static CarRentalCompany getRental(String company) {
		CarRentalCompany out = NamingService.getRentals().get(company);
		if (out == null) {
			throw new IllegalArgumentException("Company doesn't exist!: " + company);
		}
		return out;
	}

	public static ICarRentalCompany getRentalNew(String companyName) {

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

	public static Map<String, CarRentalCompany> getRentals() {
		return rentals;
	}

	public static Map<String, ICarRentalCompany> getRentalsNew() {
		return rentals2;
	}

	public static void loadRental(String datafile) {
		try {
			CrcData data = loadData(datafile);
			CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);
			rentals.put(data.name, company);
			Logger.getLogger(NamingService.class.getName()).log(Level.INFO, "Loaded {0} from file {1}",
					new Object[] { data.name, datafile });
		} catch (NumberFormatException ex) {
			Logger.getLogger(NamingService.class.getName()).log(Level.SEVERE, "bad file", ex);
		} catch (IOException ex) {
			Logger.getLogger(NamingService.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public static CrcData loadData(String datafile) throws NumberFormatException, IOException {

		CrcData out = new CrcData();
		StringTokenizer csvReader;
		int nextuid = 0;

		// open file from jar
		BufferedReader in = new BufferedReader(
				new InputStreamReader(NamingService.class.getClassLoader().getResourceAsStream(datafile)));

		try {
			while (in.ready()) {
				String line = in.readLine();

				if (line.startsWith("#")) {
					// comment -> skip
				} else if (line.startsWith("-")) {
					csvReader = new StringTokenizer(line.substring(1), ",");
					out.name = csvReader.nextToken();
					out.regions = Arrays.asList(csvReader.nextToken().split(":"));
				} else {
					csvReader = new StringTokenizer(line, ",");
					// create new car type from first 5 fields
					CarType type = new CarType(csvReader.nextToken(), Integer.parseInt(csvReader.nextToken()),
							Float.parseFloat(csvReader.nextToken()), Double.parseDouble(csvReader.nextToken()),
							Boolean.parseBoolean(csvReader.nextToken()));
					// create N new cars with given type, where N is the 5th
					// field
					for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
						out.cars.add(new Car(nextuid++, type));
					}
				}
			}
		} finally {
			in.close();
		}

		return out;
	}

	static class CrcData {
		public List<Car> cars = new LinkedList<Car>();
		public String name;
		public List<String> regions = new LinkedList<String>();
	}
}
