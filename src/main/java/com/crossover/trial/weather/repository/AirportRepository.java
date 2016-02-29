package com.crossover.trial.weather.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import model.AirportData;
import util.Utils;

import com.crossover.trial.weather.exception.AirportNotFoundException;

/**
 * Data provider for managing the airport data. It centralizes access to the information.
 *
 * @author Rodrigo Weffer
 *
 */

public class AirportRepository {

	/** all known airports */
	protected static Map<String, AirportData> airportData = new HashMap<String, AirportData>();

	private static ReadWriteLock lock = new ReentrantReadWriteLock();
	
	/**
	 * Given an iataCode find the airport data
	 *
	 * @param iataCode as a string
	 * @return airport data or null if not found
	 */
	public static AirportData findAirportData(String iataCode) throws AirportNotFoundException {
		lock.readLock().lock();
		AirportData ad;
			if(airportData.containsKey(iataCode)){
				ad = airportData.get(iataCode);
			}else{
				lock.readLock().unlock();
				throw new AirportNotFoundException();
			}
		lock.readLock().unlock();
		return ad;
	}

	/**
	 * Returns the data for all the airports
	 * 
	 * @return airport data 
	 */
	public static Collection<AirportData> getAllAirports() {
		lock.readLock().lock();
		Collection<AirportData> values = airportData.values();
		lock.readLock().unlock();
		return  values;
	}

	public static List<AirportData> getAirportsCloserThan(String iata, double radius) throws AirportNotFoundException{
		Utils utils = new Utils();
		lock.readLock().lock();
		AirportData ad = findAirportData(iata);
		List<AirportData> list = airportData.values().stream().
				filter(t -> utils.calculateDistance(ad, t) <= radius).collect(Collectors.toList());
		lock.readLock().unlock();
		return list;
	}

	public static AirportData addAirport(String iataCode, double latitude, double longitude) {
		AirportData ad = new AirportData();
		ad.setIata(iataCode);
		ad.setLatitude(latitude);
		ad.setLongitude(longitude);
		lock.writeLock().lock();
		airportData.put(iataCode, ad);
		lock.writeLock().unlock();
		return ad;
	}
}
