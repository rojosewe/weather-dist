package com.crossover.trial.weather.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import model.AtmosphericInformation;
import model.DataPoint;
import model.DataPointType;

import com.crossover.trial.weather.exception.AirportNotFoundException;
import com.crossover.trial.weather.exception.AtmosphericInformationException;

/**
 * Handles the data about the Atmospheric Information
 * 
 * @author Rodrigo Weffer
 */
public class AtmosphericInformationRepository {

	/**
	 * atmospheric information for each airport
	 */
	protected static Map<String, AtmosphericInformation> atmosphericInformation = new HashMap<String, AtmosphericInformation>();

	private static ReadWriteLock lock = new ReentrantReadWriteLock();
	
	/**
	 * Get the atmospheric information associated to an airport.
	 * 
	 * @param iata
	 *            the airpot's IATA code
	 * @return The current atmospheric information for the airport
	 * @throws AirportNotFoundException
	 */
	public static AtmosphericInformation getAtmosphericInformation(String iata)
			throws AtmosphericInformationException {
		lock.readLock().lock();
		if (!atmosphericInformation.containsKey(iata)){
			throw new AtmosphericInformationException(
					"No Atmospheric information associated to " + iata);
		}
		AtmosphericInformation ai = atmosphericInformation.get(iata);
		lock.readLock().unlock();
		return ai;
	}

	/**
	 * Returns true if there is any information available for this airport
	 * 
	 * @param iata
	 *            the airpot's IATA code
	 * @return True if there is information available
	 * @throws AirportNotFoundException
	 */
	public static boolean hasAtmosphericInformation(String iata) {
		return atmosphericInformation.containsKey(iata);
	}

	/**
	 * Returns all the abvailable atmospheric information
	 * 
	 * @return a list containing all the information
	 */
	public static Collection<AtmosphericInformation> getAllAvailableAtmosphericInformation() {
		lock.readLock().lock();
		Collection<AtmosphericInformation> values = atmosphericInformation.values();
		lock.readLock().unlock();
		return values;
	}

	/**
	 * Returns a list of all the atmospheric information updated in the last 24
	 * hours.
	 * 
	 * @return
	 */
	public static Collection<AtmosphericInformation> getAllRecentAtmosphericInformation() {
		lock.readLock().lock();
		List<AtmosphericInformation> collect = atmosphericInformation
				.values()
				.stream()
				.filter(t -> t.hasAnyInformation()
						&& t.getLastUpdateTime() > System.currentTimeMillis() - 86400000)
				.collect(Collectors.toList());
		lock.readLock().unlock();
		return collect;
	}

	public static void addAtmosphericInformation(String iataCode) {
		lock.writeLock().lock();
		atmosphericInformation.put(iataCode, new AtmosphericInformation(iataCode));
		lock.writeLock().unlock();
	}

	/**
	 * update atmospheric information with the given data point for the given
	 * point type
	 *
	 * @param ai
	 *            the atmospheric information object to update
	 * @param pointType
	 *            the data point type as a string
	 * @param dp
	 *            the actual data point
	 */
	public static void updateAtmosphericInformation(String iataCode,
			String pointType, DataPoint dp)
			throws AtmosphericInformationException, IllegalStateException {
		AtmosphericInformation ai = getAtmosphericInformation(iataCode);
		lock.writeLock().lock();
		switch (DataPointType.valueOf(pointType.toUpperCase())) {
		case WIND:
			if (dp.getMean() >= 0) {
				ai.setWind(dp);
			}
			break;
		case TEMPERATURE:
			if (dp.getMean() >= -50 && dp.getMean() < 100)
				ai.setTemperature(dp);
			break;
		case HUMIDITY:
			if (dp.getMean() >= 0 && dp.getMean() < 100)
				ai.setHumidity(dp);
			break;
		case PRESSURE:
			if (dp.getMean() >= 650 && dp.getMean() < 800)
				ai.setPressure(dp);
			break;
		case CLOUDCOVER:
			if (dp.getMean() >= 0 && dp.getMean() < 100)
				ai.setCloudCover(dp);
			break;
		case PRECIPITATION:
			if (dp.getMean() >= 0 && dp.getMean() < 100)
				ai.setPrecipitation(dp);
		default:
			throw new IllegalStateException("couldn't update atmospheric data");
		}
		atmosphericInformation.put(iataCode, ai);
		lock.writeLock().unlock();
		
	}

}
