package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.exception.AirportNotFoundException;
import com.crossover.trial.weather.exception.AtmosphericInformationException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.repository.AtmosphericInformationRepository;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import util.AirportRequestStatistics;
import model.AirportData;
import model.AtmosphericInformation;

import java.util.*;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check
 * health stats. Currently, all data is held in memory. The end point deploys to
 * a single container
 *
 * @author code test administrator, Rodrigo Weffer
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

	public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

	/** shared gson json to object factory */
	public static final Gson gson = new Gson();

	private AirportRequestStatistics airportRequestStatistics = new AirportRequestStatistics();

	/**
	 * Retrieve service health including total size of valid data points and
	 * request frequency information.
	 *
	 * @return health stats for the service as a string
	 */
	@GET
	@Path("/ping")
	public String ping() {
		Map<String, Object> retval = new HashMap<>();

		setDataSize(retval);

		setIataFreq(retval);

		setRadiusFreq(retval);

		return gson.toJson(retval);
	}

	private void setRadiusFreq(Map<String, Object> retval) {
		int[] hist = airportRequestStatistics.getAirportRadiusRequestDistribution();
		retval.put("radius_freq", hist);
	}

	private void setIataFreq(Map<String, Object> retval) {
		retval.put("iata_freq",
				airportRequestStatistics.getAirportRequestDistribution());
	}

	private void setDataSize(Map<String, Object> retval) {
		int datasize = AtmosphericInformationRepository.getAllRecentAtmosphericInformation().size();
		retval.put("datasize", datasize);
	}

	/**
	 * Given a query in json format {'iata': CODE, 'radius': km} extracts the
	 * requested airport information and return a list of matching atmosphere
	 * information.
	 *
	 * @param iata
	 *            the iataCode
	 * @param radiusString
	 *            the radius in km
	 *
	 * @return a list of atmospheric information
	 */
	@GET
	@Path("/weather/{iata}/{radius}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("iata") String iata, @PathParam("radius") String radiusString) {
		double radius = radiusString == null || radiusString.trim().isEmpty() ? 0
				: Double.valueOf(radiusString);
		List<AtmosphericInformation> retval = new ArrayList<>();
		try {
			AirportData airportData = AirportRepository.findAirportData(iata);
			airportRequestStatistics
					.updateRequestFrequency(airportData, radius);
			if (radius == 0) {
				try {
					retval.add(AtmosphericInformationRepository.getAtmosphericInformation(iata));
				} catch (AtmosphericInformationException e) {
					return Response.status(Response.Status.NOT_FOUND).build();
				}
			} else {
				for (AirportData ad : AirportRepository.getAirportsCloserThan(iata, radius)) {
					try{
						AtmosphericInformation ai = AtmosphericInformationRepository.getAtmosphericInformation(ad.getIata());
						if (ai.hasAnyInformation()) {
							retval.add(ai);
						}
					}catch(AtmosphericInformationException e){
					}
				}
			}
		} catch (AirportNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.status(Response.Status.OK).entity(retval).build();
	}

	/**
	 * Records information about how often requests are made
	 *
	 * @param iata a iata code
	 * @param radius query radius
	 * @throws AirportNotFoundException 
	 */
	public void updateRequestFrequency(String iata, Double radius) throws AirportNotFoundException {
		AirportData airportData = AirportRepository.findAirportData(iata);
		airportRequestStatistics.update(airportData, radius);
	}

}
