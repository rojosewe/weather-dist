package util;

import java.util.HashMap;
import java.util.Map;

import model.AirportData;

import com.crossover.trial.weather.repository.AirportRepository;

/**
 * Analytics for request. 
 * 
 * @author Rodrigo Weffer
 */
public class AirportRequestStatistics{

    /**
     * performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics
     */
    private Map<AirportData, Integer> requestFrequency = new HashMap<AirportData, Integer>();

    private Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

    /**
     * Records information about how often requests are made
     *
     * @param airportData queried airport
     * @param radius query radius
     */
    public void updateRequestFrequency(AirportData airportData, Double radius) {
        requestFrequency.put(airportData, requestFrequency.getOrDefault(airportData, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0));
    }

    public void update(AirportData airportData, Double radius) {
        requestFrequency.put(airportData, requestFrequency.getOrDefault(airportData, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0));
    }
    
    public Map<String, Double> getAirportRequestDistribution(){
    	Map<String, Double> freq = new HashMap<>();
        // fraction of queries
        for (AirportData data : AirportRepository.getAllAirports()) {
            double frac = (double)requestFrequency.getOrDefault(data, 0) / requestFrequency.size();
            freq.put(data.getIata(), frac);
        }
        return freq;
    }
    
    public int[] getAirportRadiusRequestDistribution(){
    	int m = radiusFreq.keySet().stream().max(Double::compare)
				.orElse(1000.0).intValue() + 1;

		int[] hist = new int[m];
		for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
			int i = e.getKey().intValue() % 10;
			hist[i] += e.getValue();
		}
		return hist;
    }
}
