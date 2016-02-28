package com.crossover.trial.weather.endpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.DELETE;
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

	static {
		init();
	}

	@GET
	@Path("/ping")
	@Override
	public Response ping() {
		return Response.status(Response.Status.OK).entity("ready").build();
	}

	@POST
	@Path("/weather/{iata}/{pointType}")
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
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
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
	@Path("/airport/{iata}/{lat}/{long}")
	@Override
	public Response addAirport(@PathParam("iata") String iata,
			@PathParam("lat") Double latitude,
			@PathParam("long") Double longitude) {
			addAirport(iata, latitude, longitude);
		return Response.status(Response.Status.OK).build();
	}

	@DELETE
	@Path("/airport/{iata}")
	@Override
	public Response deleteAirport(@PathParam("iata") String iata) {
		return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
	}
	
	/**
	 * A dummy init method that loads hard coded data
	 */
	public static void init() {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("airports.dat");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String l = null;
		WeatherCollectorService service = new WeatherCollectorService();
		try {
			while ((l = br.readLine()) != null) {
				String[] split = l.split(",");
				service.addAirport(split[0], Double.valueOf(split[1]),
						Double.valueOf(split[2]));
			}
		} catch (IOException e) {
			LOGGER.severe("Error loading the airport file");
			System.exit(0);
		}
	}
}
