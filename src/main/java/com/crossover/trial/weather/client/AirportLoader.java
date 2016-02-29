package com.crossover.trial.weather.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import model.AirportData;
import model.DST;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * @author code test administrator, Rodrigo Weffer
 */
public class AirportLoader {

    private static final String HOST = "http://localhost:8080";

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    public AirportLoader() {
        Client client = ClientBuilder.newClient();
        query = client.target(HOST + "/query");
        collect = client.target(HOST + "/collect");
    }

    public void upload(InputStream airportDataStream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
        String l = null;
        WebTarget path = collect.path("/airport");
        
        while ((l = reader.readLine()) != null) {
        	String[] airportData = l.split(",");
        	AirportData ad = new AirportData(airportData[2], 
        			Double.parseDouble(airportData[4]), Double.parseDouble(airportData[5]),
        			airportData[0], airportData[1], airportData[3], Double.parseDouble(airportData[6]), 
        			Double.parseDouble(airportData[7]), DST.valueOf(airportData[8]));
        	Form form = new Form();
        	form.param("iata", ad.getIata());
        	form.param("city", ad.getCity());
        	form.param("country", ad.getCountry());
        	form.param("icao", ad.getIcao());
        	form.param("lat", ad.getLatitude().toString());
        	form.param("long", ad.getLongitude().toString());
        	form.param("altitude", ad.getAltitude().toString());
        	form.param("timezone", ad.getTimezone().toString());
        	form.param("dst", ad.getDst().toString());
        	
        	path.request(MediaType.APPLICATION_JSON_TYPE)
        			.post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
            break;
        }
    }

    public static void main(String args[]) throws IOException{
        File airportDataFile = new File(args[0]);
        if (!airportDataFile.exists() || airportDataFile.length() == 0) {
            System.err.println(airportDataFile + " is not a valid input");
            System.exit(1);
        }

        AirportLoader al = new AirportLoader();
        al.upload(new FileInputStream(airportDataFile));
        System.exit(0);
    }
}
