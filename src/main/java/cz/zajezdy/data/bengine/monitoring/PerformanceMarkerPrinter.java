package cz.zajezdy.data.bengine.monitoring;

import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class PerformanceMarkerPrinter {

	public void printList(List<PerformanceMarker> markers) {
		String markerHeadline = "Marker";
		String timeHeadline = "Time in ms";
		String deltaHeadline = "Delta in ms";
		int longestMarker = markerHeadline.length();
		int longestTime = timeHeadline.length();
		int longestDelta = deltaHeadline.length();

		PerformanceMarker lastMarker = null;
		long lastTime = 0;
		for (PerformanceMarker marker : markers) {
			long time = marker.getNanoTime();
			long delta = time - lastTime;
			if (lastMarker != null) {
				lastMarker.setDelta(delta);
			}

			if (marker.getMarker().length() > longestMarker) {
				longestMarker = marker.getMarker().length();
			}
			String sTime = Long.toString(marker.getNanoTime());
			if (sTime.length() > longestTime) {
				longestTime = sTime.length();
			}
			lastTime = time;
			lastMarker = marker;
		}
		int seperatorLength = 2 + longestMarker + 3 + longestTime + 3 + longestDelta + 2;
		printLine(seperatorLength);
		print("| " + padRight(markerHeadline, longestMarker) + " | " + padRight(timeHeadline, longestTime) + " | " + padRight(deltaHeadline, longestDelta)
				+ " |");
		printLine(seperatorLength);

		for (PerformanceMarker marker : markers) {
			double dtime = (double) (marker.getNanoTime() / 1000) / 1000;
			double ddelta = (double) (marker.getDelta() / 1000) / 1000;

			String markerText = padRight(marker.getMarker(), longestMarker);
			String timeText = padLeft(String.format("%.3f", dtime), longestTime);
			String deltaText = padLeft(String.format("%.3f", ddelta), longestDelta);

			print("| " + markerText + " | " + timeText + " | " + deltaText + " |");

		}
		printLine(seperatorLength);

		// print info
		print(padRight("| Marker time is measured, when encountering the marker (not when finishing the operation). ", seperatorLength - 1) + "|");
		print(padRight("| 'Time in ms' is the total passed time since the monitoring was enabled. ", seperatorLength - 1) + "|");
		print(padRight("| 'Delta in ms' is the passed time since the last marker. ", seperatorLength - 1) + "|");
		print(padRight("| ATTENTION: performance may be lower in the first run. ", seperatorLength - 1) + "|");
		printLine(seperatorLength);
	}

	private String padRight(String str, int length) {
		return StringUtils.rightPad(str, length);
	}

	private String padLeft(String str, int length) {
		return StringUtils.leftPad(str, length);
	}

	private void printLine(int length) {
		for (int i = 0; i < length; i++) {
			System.out.print("-");
		}
		System.out.println();
	}

	private void print(String str) {
		System.out.println(str);
	}
}
