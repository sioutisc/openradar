package de.knewcleus.fgfs.geodata.geometry;

import java.util.List;

import de.knewcleus.fgfs.geodata.GeodataException;

public class Polygon extends GeometryContainer<Ring> {
	protected Ring outerRing=null;
	
	@Override
	public void add(Ring geometry) {
		super.add(geometry);
		if (outerRing==null) {
			outerRing=geometry;
		}
	}
	
	public Ring getOuterRing() {
		return outerRing;
	}
	
	public List<Ring> getRings() {
		return getContainedGeometry();
	}
	
	public double getArea() {
		double area=0.0;
		for (Ring ring: getRings()) {
			area+=ring.getEnclosedArea();
		}
		
		return area;
	}
	
	@Override
	public void accept(IGeometryVisitor visitor) throws GeodataException {
		visitor.visit(this);
	}
}