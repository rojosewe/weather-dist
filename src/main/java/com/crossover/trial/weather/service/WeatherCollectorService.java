package com.crossover.trial.weather.service;

import model.AirportData;
import model.DataPoint;
import model.DataPointType;

import com.crossover.trial.weather.exception.AtmosphericInformationException;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphericInformationRepository;


public class WeatherCollectorService {

	/**
     * Add a new known airport to our list.
     *
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     * @return the added airport
     */
    public AirportData addAirport(String iataCode, double latitude, double longitude) {
    	AirportData ad = AirportRepository.addAirport(iataCode, latitude, longitude);
    	AtmosphericInformationRepository.addAtmosphericInformation(iataCode);
        return ad;
    }
    
	//
	// Internal support methods
	//

	/**
	 * Update the airports weather data with the collected data.
	 *
	 * @param iataCode the 3 letter IATA code
	 * @param pointType the point type {@link DataPointType}
	 * @param dp a datapoint object holding pointType data
	 *
	 * @throws WeatherException
	 *             if the update can not be completed
	 */
	public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws AtmosphericInformationException, IllegalStateException{
		AtmosphericInformationRepository.updateAtmosphericInformation(iataCode, pointType, dp);
	}
    
}
