package cz.zajezdy.data.bengine.monitoring;

import java.util.ArrayList;
import java.util.List;


public class PerformanceMarkerMgr {

	private long performanceTimeOffset = 0;
	private List<PerformanceMarker> performanceMarkers = null;

	public PerformanceMarkerMgr() {
		this.performanceTimeOffset = System.nanoTime();
		this.performanceMarkers = new ArrayList<PerformanceMarker>();
	}

	public List<PerformanceMarker> getPerformanceMarkers() {
		return performanceMarkers;
	}

	public void addMarker(String marker) {
		long nanoTime = System.nanoTime();
		performanceMarkers.add(new PerformanceMarker(marker, nanoTime - performanceTimeOffset));
	}
}
