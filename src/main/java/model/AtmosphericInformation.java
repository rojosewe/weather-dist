package model;


/**
 * encapsulates sensor information for a particular location
 *
 * @author code test administrator, Rodrigo Weffer
 */
public class AtmosphericInformation {

	/** temperature in degrees celsius */
	private DataPoint temperature;

	/** wind speed in km/h */
	private DataPoint wind;

	/** humidity in percent */
	private DataPoint humidity;

	/** precipitation in cm */
	private DataPoint precipitation;

	/** pressure in mmHg */
	private DataPoint pressure;

	/** cloud cover percent from 0 - 100 (integer) */
	private DataPoint cloudCover;

	/** the last time this data was updated, in milliseconds since UTC epoch */
	private long lastUpdateTime;

	public AtmosphericInformation() {

	}

	protected AtmosphericInformation(DataPoint temperature, DataPoint wind,
			DataPoint humidity, DataPoint percipitation, DataPoint pressure,
			DataPoint cloudCover) {
		this.temperature = temperature;
		this.wind = wind;
		this.humidity = humidity;
		this.precipitation = percipitation;
		this.pressure = pressure;
		this.cloudCover = cloudCover;
		setLastUpdateTime();
	}

	public DataPoint getTemperature() {

		return temperature;
	}

	public void setTemperature(DataPoint temperature) {

		this.temperature = temperature;
		setLastUpdateTime();
	}

	public DataPoint getWind() {

		return wind;
	}

	public void setWind(DataPoint wind) {
		this.wind = wind;
		setLastUpdateTime();
	}

	public DataPoint getHumidity() {

		return humidity;
	}

	public void setHumidity(DataPoint humidity) {

		this.humidity = humidity;
		setLastUpdateTime(); 
	}

	public DataPoint getPrecipitation() {

		return precipitation;
	}

	public void setPrecipitation(DataPoint precipitation) {

		this.precipitation = precipitation;
		setLastUpdateTime();
	}

	public DataPoint getPressure() {

		return pressure;
	}

	public void setPressure(DataPoint pressure) {
		this.pressure = pressure;
		setLastUpdateTime(); 
	}

	public DataPoint getCloudCover() {

		return cloudCover;
	}

	public void setCloudCover(DataPoint cloudCover) {
		this.cloudCover = cloudCover;
		setLastUpdateTime(); 
	}

	public long getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	protected void setLastUpdateTime() {
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public boolean hasAnyInformation() {
		return getCloudCover() != null || getHumidity() != null
				|| getPressure() != null || getPrecipitation() != null
				|| getTemperature() != null || getWind() != null;
	}
}
