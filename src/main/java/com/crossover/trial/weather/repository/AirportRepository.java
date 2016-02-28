package com.crossover.trial.weather.repository;

import java.util.*;
import java.util.stream.Collectors;

import util.Utils;
import model.AirportData;

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

    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
    public static AirportData findAirportData(String iataCode) throws AirportNotFoundException {
    	if(airportData.containsKey(iataCode))
    		return airportData.get(iataCode);
    	else
    		throw new AirportNotFoundException(); 
    }
    
    /**
     * Returns the data for all the airports
     * 
     * @return airport data 
     */
    public static Collection<AirportData> getAllAirports() {
    	return airportData.values();
	}

	public static List<AirportData> getAirportsCloserThan(String iata, double radius) throws AirportNotFoundException{
		Utils utils = new Utils();
		AirportData ad = findAirportData(iata);
		return airportData.values().stream().
				filter(t -> utils.calculateDistance(ad, t) <= radius).collect(Collectors.toList());
	}

	public static AirportData addAirport(String iataCode, double latitude, double longitude) {
		AirportData ad = new AirportData();
		ad.setIata(iataCode);
		ad.setLatitude(latitude);
		ad.setLongitude(longitude);
		airportData.put(iataCode, ad);
		return ad;
	}
}
