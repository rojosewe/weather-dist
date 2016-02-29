package com.crossover.trial.weather.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import model.AirportData;
import model.DST;
import util.Utils;

import com.crossover.trial.weather.exception.AirportNotFoundException;

/**
 * Data provider for managing the airport data. It centralizes access to the
 * information.
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

	/**
	 * Get all the airports that are in a radius of distance to the specified airport
	 * @param iata iata code
	 * @param radius distance
	 * @return A list of airports
	 * @throws AirportNotFoundException
	 */
	public static List<AirportData> getAirportsCloserThan(String iata, double radius) throws AirportNotFoundException {
		Utils utils = new Utils();
		lock.readLock().lock();
		AirportData ad = findAirportData(iata);
		List<AirportData> list = airportData.values().stream().
				filter(t -> utils.calculateDistance(ad, t) <= radius).collect(Collectors.toList());
		lock.readLock().unlock();
		return list;
	}

	/**
	 * Add a new known airport to our list.
	 *
	 * @param iata
	 *            iata the 3 letter airport code of the new airport
	 * @param city
	 *            Main city served by airport. May be spelled differently from
	 *            name.
	 * @param country
	 *            Country or territory where airport is located.
	 * @param icao
	 *            4-letter ICAO code (blank or "" if not assigned)
	 * @param latitude
	 *            latString the airport's latitude in degrees as a string [-90,
	 *            90]
	 * @param longitude
	 *            longString the airport's longitude in degrees as a string
	 *            [-180, 180]
	 * @param altitude
	 *            In feet
	 * @param timezone
	 *            Hours offset from UTC. Fractional hours are expressed as
	 *            decimals. (e.g. India is 5.5)
	 * @param dst
	 *            One of E (Europe), A (US/Canada), S (South America), O
	 *            (Australia), Z (New Zealand), N (None) or U (Unknown)
	 *
	 * @return the added airport
	 */
	public static AirportData addAirport(String iataCode, String city, String country, String icao, Double latitude,
			Double longitude, Double altitude, Double timezone, DST dst) {
		AirportData ad = new AirportData(iataCode, latitude, longitude, city, country, icao, altitude, timezone, dst);
		lock.writeLock().lock();
		airportData.put(iataCode, ad);
		lock.writeLock().unlock();
		return ad;
	}

	/**
	 * Deletes the airport from the dat idetified by the iata code
	 * @param iata iata code
	 * @throws AirportNotFoundException
	 */
	public static void deleteAirport(String iata) throws AirportNotFoundException {
		if(!airportData.containsKey(iata))
			throw new AirportNotFoundException("Airport not found");
		lock.writeLock().lock();
		airportData.remove(iata);
		lock.writeLock().unlock();
	}
}
