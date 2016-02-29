package model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator, Rodrigo Weffer
 */

public class AirportData {

	/**
	 * the three letter IATA code
	 */
	private String iata;

	/**
	 * latitude value in degrees
	 */
	private double latitude;

	/**
	 * longitude value in degrees
	 */
	private double longitude;
	
	/**
	 * Name of the ciy
	 */
	private String city;
	
	/**
	 * Name of the country
	 */
	private String country;
	
	/**
	 * ICAO
	 */
	private String icao;
	
	/**
	 * Altitude in feet
	 */
	private Double altitude;
	
	/**
	 * Hour offset from UTC
	 */
	private Double timezone;
	
	/**
	 * DST
	 */
	private DST dst;

	public AirportData() {
		icao = new String();
		iata = new String();
	}
	
	public AirportData(String iata, double latitude, double longitude, String city, String country, String icao,
			Double altitude, Double timezone, DST dst) {
		super();
		this.iata = iata;
		this.latitude = latitude;
		this.longitude = longitude;
		this.city = city;
		this.country = country;
		this.icao = icao;
		this.altitude = altitude;
		this.timezone = timezone;
		this.dst = dst;
	}



	public String getIata() {
		return iata;
	}

	public void setIata(String iata) {
		this.iata = iata;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {

		return longitude;
	}

	public void setLongitude(double longitude) {

		this.longitude = longitude;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getIcao() {
		return icao;
	}

	public void setIcao(String icao) {
		this.icao = icao;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getTimezone() {
		return timezone;
	}

	public void setTimezone(Double timezone) {
		this.timezone = timezone;
	}

	public DST getDst() {
		return dst;
	}

	public void setDst(DST dst) {
		this.dst = dst;
	}

	public String toString() {

		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.NO_CLASS_NAME_STYLE);
	}

	public boolean equals(Object other) {
		if (other instanceof AirportData) {
			return ((AirportData) other).getIata().equals(this.getIata());
		}
		return false;
	}
}
