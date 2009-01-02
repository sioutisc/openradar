package de.knewcleus.openradar.rpvd;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.map.MapViewerAdapter;

public class RadarMapViewerAdapter extends MapViewerAdapter implements IRadarMapViewerAdapter {
	protected int trackHistoryLength = 5;
	protected double headingVectorTime = 1.0 * Units.MIN;

	public RadarMapViewerAdapter() {
	}

	@Override
	public int getTrackHistoryLength() {
		return trackHistoryLength;
	}
	
	@Override
	public void setTrackHistoryLength(int trackHistoryLength) {
		this.trackHistoryLength = trackHistoryLength;
		notify(new RadarMapViewerNotification(this));
	}
	
	/**
	 * @return the lookahead-time represented by the heading vector.
	 */
	@Override
	public double getHeadingVectorTime() {
		return headingVectorTime;
	}
	
	/**
	 * Set the lookahead-time represented by the heading vector.
	 */
	@Override
	public void setHeadingVectorTime(double headingVectorTime) {
		this.headingVectorTime = headingVectorTime;
	}

}
