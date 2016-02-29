package com.crossover.trial.weather.service;

import model.AirportData;
import model.DST;
import model.DataPoint;
import model.DataPointType;

import com.crossover.trial.weather.exception.AirportNotFoundException;
import com.crossover.trial.weather.exception.AtmosphericInformationException;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphericInformationRepository;

public class WeatherCollectorService {

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
	public AirportData addAirport(String iataCode, String city, String country, String icao, Double latitude,
			Double longitude, Double altitude, Double timezone, String dst) throws IllegalArgumentException{
		DST adDst = DST.valueOf(dst);
		AirportData ad = AirportRepository.addAirport(iataCode, city, country, icao, latitude, 
				longitude, altitude, timezone, adDst);
		AtmosphericInformationRepository.addAtmosphericInformation(iataCode);
		return ad;
	}

	//
	// Internal support methods
	//

	/**
	 * Update the airports weather data with the collected data.
	 *
	 * @param iataCode
	 *            the 3 letter IATA code
	 * @param pointType
	 *            the point type {@link DataPointType}
	 * @param dp
	 *            a datapoint object holding pointType data
	 *
	 * @throws WeatherException
	 *             if the update can not be completed
	 */
	public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws AtmosphericInformationException,
			IllegalStateException {
		AtmosphericInformationRepository.updateAtmosphericInformation(iataCode, pointType, dp);
	}

	public void deleteAirport(String iata) throws AirportNotFoundException {
		AirportRepository.deleteAirport(iata);
	}

}
