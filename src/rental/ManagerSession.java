package rental;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class ManagerSession extends Session {

	private String companyname;

	public ManagerSession(String name, String companyName) {
		super(name);
		setCompanyName(companyName);
	}

	public String getCompanyName() {
		return companyname;
	}

	private void setCompanyName(String companyName) {
		this.companyname = companyName;
	}

	public int getNumberOfReservationsForCarType(String carType) throws RemoteException {

		int out = 0;

		for (ICarRentalCompany company : NamingService.getRentals().values()) {
			if (company.getAllCarTypeNames().contains(carType)) {
				out += company.getReservationsByCarType(carType).size();
			}
		}

		return out;
	}
	
	public void registerCompany() throws RemoteException{
		NamingService.registerCompany(getCompanyName());
	}

	public CarType getMostPopularCarTypeIn(int year) throws RemoteException {
		ICarRentalCompany company = NamingService.getRental(getCompanyName());

		return company.getMostPopularCarTypeIn(year);
	}

	public Set<String> getBestClients() throws RemoteException {

		Set<String> bestRenters = new HashSet<String>();

		Map<String, Integer> NBResForAllClients = new HashMap<String, Integer>();
		for (ICarRentalCompany company : NamingService.getRentals().values()) {

			Map<String, Integer> NBResPerClient = company.getNBOfResForAllClients();
			for (Map.Entry<String, Integer> entry : NBResPerClient.entrySet()) {
				addTo(NBResForAllClients, entry);
			}

		}

		int maxRes = Collections.max(NBResForAllClients.values());

		for (Map.Entry<String, Integer> entry : NBResForAllClients.entrySet()) {
			if (entry.getValue() == maxRes) {
				bestRenters.add(entry.getKey());
			}
		}

		return bestRenters;
	}

	private void addTo(Map<String, Integer> map, Map.Entry<String, Integer> entry) {
		if (map.containsKey(entry.getKey())) {
			map.put(entry.getKey(), map.get(entry.getKey()) + entry.getValue());
		} else {
			map.put(entry.getKey(), entry.getValue());
		}
	}

}
