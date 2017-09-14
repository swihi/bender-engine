package cz.zajezdy.data.bengine.monitoring;

public class PerformanceMarker {

	private String marker;
	private long nanoTime;
	private long delta;

	public PerformanceMarker(String marker, long nanoTime) {
		this.marker = marker;
		this.nanoTime = nanoTime;
	}

	public String getMarker() {
		return marker;
	}

	public long getDelta() {
		return delta;
	}

	public void setDelta(long delta) {
		this.delta = delta;
	}

	public long getNanoTime() {
		return nanoTime;
	}

	@Override
	public String toString() {
		return "{ marker=\"" + marker + "\", time=" + (nanoTime / 1000000) + " ms }";
	}
}
