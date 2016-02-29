package com.crossover.trial.weather.endpoint;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.AirportData;
import model.DataPoint;

import com.crossover.trial.weather.exception.AirportNotFoundException;
import com.crossover.trial.weather.exception.AtmosphericInformationException;
import com.crossover.trial.weather.repository.AirportRepository;
import com.crossover.trial.weather.service.WeatherCollectorService;
import com.google.gson.Gson;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport
 * weather collection sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollector {

	public final static Logger LOGGER = Logger
			.getLogger(RestWeatherCollectorEndpoint.class.getName());

	/** shared gson json to object factory */
	public final static Gson gson = new Gson();

	@GET
	@Path("/ping")
	@Override
	public Response ping() {
		return Response.status(Response.Status.OK).entity("ready").build();
	}

	@POST
	@Path("/weather/{iata}/{pointType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response updateWeather(@PathParam("iata") String iataCode,
			@PathParam("pointType") String pointType, String datapointJson) {
		try {
			WeatherCollectorService service = new WeatherCollectorService();
			service.addDataPoint(iataCode, pointType,
					gson.fromJson(datapointJson, DataPoint.class));
		} catch (AtmosphericInformationException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (IllegalStateException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		return Response.status(Response.Status.OK).build();
	}

	@GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAirports() {
        Set<String> retval = AirportRepository.getAllAirports().stream().
        		map(AirportData::getIata).collect(Collectors.toSet());
        return Response.status(Response.Status.OK).entity(retval).build();
    }

	@GET
	@Path("/airport/{iata}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getAirport(@PathParam("iata") String iata) {
		AirportData ad;
		try {
			ad = AirportRepository.findAirportData(iata);
		} catch (AirportNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();	
		}
		return Response.status(Response.Status.OK).entity(ad).build();
	}
	
	
	@POST
	@Path("/airport/{iata}")
	@Override
	public Response addAirport(@FormParam("iata") String iata,
			@FormParam("city") String city,
			@FormParam("country") String country,
			@FormParam("icao") String icao,
			@FormParam("lat") Double latitude,
			@FormParam("long") Double longitude,
			@FormParam("altitude") Double altitude,
			@FormParam("timezone") Double timezone,
			@FormParam("dst") String dst) {
			try{
				WeatherCollectorService service = new WeatherCollectorService();
				service.addAirport(iata, city, country, icao, latitude, longitude, altitude, timezone, dst);				
			}catch(IllegalArgumentException e){
				Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
			}
		return Response.status(Response.Status.OK).build();
	}

	@DELETE
	@Path("/airport/{iata}")
	@Override
	public Response deleteAirport(@PathParam("iata") String iata) {
		WeatherCollectorService service = new WeatherCollectorService();
		try {
			service.deleteAirport(iata);
		} catch (AirportNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.status(Response.Status.OK).build();
	}
	
}
