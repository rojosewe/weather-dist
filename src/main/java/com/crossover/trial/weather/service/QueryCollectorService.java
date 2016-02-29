package com.crossover.trial.weather.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.AirportData;
import model.AtmosphericInformation;

import com.crossover.trial.weather.exception.AirportNotFoundException;
import com.crossover.trial.weather.exception.AtmosphericInformationException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphericInformationRepository;

/**
 * Handles the business logic for the Queries
 * 
 * @author Rodrigo Weffer
 *
 */
public class QueryCollectorService {

	/**
	 * Get the atmospheric information about an airport
	 * @param iata The iata code
	 * @return The atmospheric information associated to the airport
	 * @throws AtmosphericInformationException
	 */
	public AtmosphericInformation getAtmosphericInformation(String iata) throws AtmosphericInformationException {
		return AtmosphericInformationRepository.getAtmosphericInformation(iata);
	}

	/**
	 * Gets the atmospheric information about airports close to an specific airport
	 * 
	 * @param iata The iata code
	 * @param radius radius of distance to query for
	 * @return An array of atmospheric information
	 * @throws AirportNotFoundException
	 */
	public List<AtmosphericInformation> getAtmosphericInfoCloseTo(String iata, double radius) throws AirportNotFoundException {
		List<AtmosphericInformation> retval = new ArrayList<>();
		for (AirportData ad : AirportRepository.getAirportsCloserThan(iata, radius)) {
			try{
				AtmosphericInformation ai = AtmosphericInformationRepository.getAtmosphericInformation(ad.getIata());
				if (ai.hasAnyInformation()) {
					retval.add(ai);
				}
			}catch(AtmosphericInformationException e){
			}
		}
		return retval;
	}

	public Collection<AtmosphericInformation> getAllRecentAtmosphericInformation() {
		return AtmosphericInformationRepository.getAllRecentAtmosphericInformation();
	}
    
}
