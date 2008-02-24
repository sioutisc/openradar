package de.knewcleus.radar.ui.rpvd;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.Updater;
import de.knewcleus.fgfs.location.CoordinateDeviceTransformation;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.Runway;
import de.knewcleus.radar.Scenario;
import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.autolabel.Autolabeller;

public class RadarPlanViewPanel extends JPanel implements IUpdateable {
	private static final long serialVersionUID = 8996959818592227638L;
	
	protected final Scenario scenario;
	protected final RadarPlanViewSettings settings;
	protected final RadarDeviceTransformation radarDeviceTransformation;
	protected final RadarPlanViewContext radarPlanViewContext;
	
	protected final Autolabeller autolabeller=new Autolabeller();
	protected final Updater radarUpdater=new Updater(this,1000);

	protected final IMapLayer landmassLayer;
	protected final IMapLayer waterLayer;
	protected final IMapLayer restrictedLayer;
	protected final IMapLayer sectorLayer;
	protected final IMapLayer waypointDisplayLayer;
	protected final RangeMarkLayer rangeMarkLayer=new RangeMarkLayer();
	
	protected final float scaleMarkerSize=10.0f;
	protected final float scaleMarkerDistance=10.0f*(float)Units.NM;
	protected final float centrelineLength=5.0f*(float)Units.NM;
	
	protected Map<IAircraft, AircraftSymbol> aircraftSymbolMap=new HashMap<IAircraft, AircraftSymbol>();
	
	protected static int selectionRange=10;
	
	public RadarPlanViewPanel(final Scenario scenario, RadarPlanViewSettings settings) {
		super(true); /* Is double buffered */
		this.scenario=scenario;
		this.settings=settings;
		
		radarDeviceTransformation=new RadarDeviceTransformation(settings);
		radarPlanViewContext=new RadarPlanViewContext(settings,radarDeviceTransformation);
		
		landmassLayer=new PolygonMapLayer(Palette.LANDMASS,scenario.getSector().getLandmassPolygons());
		waterLayer=new PolygonMapLayer(Palette.WATERMASS,scenario.getSector().getWaterPolygons());
		restrictedLayer=new PolygonMapLayer(Palette.RESTRICTED,scenario.getSector().getRestrictedPolygons());
		sectorLayer=new PolygonMapLayer(Palette.SECTOR,scenario.getSector().getSectorPolygons());
		waypointDisplayLayer=new WaypointDisplayLayer(scenario.getSector());

		setBackground(Palette.WATERMASS);
		
		radarUpdater.start();
	}
	
	public RadarPlanViewSettings getSettings() {
		return settings;
	}
	
	public RadarDeviceTransformation getRadarDeviceTransformation() {
		return radarDeviceTransformation;
	}
	
	@Override
	protected synchronized void paintComponent(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		radarDeviceTransformation.update(getWidth(), getHeight());
		
		for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
			aircraftSymbol.prepareForDrawing(g2d);
		}
		
		/* Labelling should not take more than 250ms. */
		autolabeller.label(System.currentTimeMillis()+250);
		
		super.paintComponent(g);
		
		setFont(settings.getFont());
		
		IDeviceTransformation mapTransformation=new CoordinateDeviceTransformation(settings.getMapTransformation(), radarDeviceTransformation);
		
		if (settings.isShowingCoastline()) {
			landmassLayer.draw(g2d, mapTransformation);
			waterLayer.draw(g2d, mapTransformation);
		} else {
			Rectangle clipRect=g2d.getClipBounds();
			g2d.setColor(Palette.LANDMASS);
			g2d.fillRect(clipRect.x,clipRect.y,clipRect.width,clipRect.height);
		}
		if (settings.isShowingWaypoints()) {
			waypointDisplayLayer.draw(g2d, mapTransformation);
			drawRunwayCentrelines(g2d,radarDeviceTransformation,settings.getMapTransformation());
		}
		if (settings.isShowingSector()) {
			sectorLayer.draw(g2d, mapTransformation);
		}
		if (settings.isShowingMilitary()) {
			restrictedLayer.draw(g2d, mapTransformation);
		}
		if (settings.isShowingRings()) {
			rangeMarkLayer.draw(g2d, radarDeviceTransformation);
		}
		drawScaleMarkers(g2d, radarDeviceTransformation);
		
		if (settings.getSpeedVectorMinutes()>0) {
			for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
				aircraftSymbol.drawHeadingVector(g2d);
			}
		}
		if (settings.getTrackHistoryLength()>0) {
			for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
				aircraftSymbol.drawTrail(g2d);
			}
		}
		for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
			aircraftSymbol.drawSymbol(g2d);
			aircraftSymbol.drawLabel(g2d, autolabeller.getCurrentLabelling().get(aircraftSymbol));
		}
	}
	
	private void drawScaleMarkers(Graphics2D g2d, IDeviceTransformation mapTransformation) {
		Rectangle bounds=g2d.getClipBounds();
		
		g2d.setColor(Palette.WINDOW_BLUE);
		
		if (bounds.getMinX()<=scaleMarkerSize && bounds.getMaxX()>=scaleMarkerSize) {
			/* Draw left scale markers */
			Position bottom,top;
			bottom=mapTransformation.fromDevice(new Point2D.Double(0.0,bounds.getMaxY()));
			top=mapTransformation.fromDevice(new Point2D.Double(0.0,bounds.getMinY()));
			
			int bottomNumber=(int)Math.floor(bottom.getY()/scaleMarkerDistance);
			int topNumber=(int)Math.ceil(top.getY()/scaleMarkerDistance);
			
			for (int i=bottomNumber;i<=topNumber;i++) {
				double y=i*scaleMarkerDistance;
				
				Path2D marker=new Path2D.Double();
				
				Point2D position=mapTransformation.toDevice(new Position(0.0,y,0.0));
				
				marker.moveTo(0.0,position.getY()-scaleMarkerSize*.25f);
				marker.lineTo(0.0,position.getY()+scaleMarkerSize*.25f);
				marker.lineTo(0.0+scaleMarkerSize,position.getY());
				marker.closePath();
				
				g2d.fill(marker);
			}
		}
		
		if (bounds.getMinY()<=getHeight()-scaleMarkerSize && bounds.getMaxY()>=getHeight()) {
			/* Draw bottom scale markers */
			Position left,right;
			left=mapTransformation.fromDevice(new Point2D.Double(bounds.getMinX(),getHeight()));
			right=mapTransformation.fromDevice(new Point2D.Double(bounds.getMaxX(),getHeight()));
			
			int leftNumber=(int)Math.floor(left.getX()/scaleMarkerDistance);
			int rightNumber=(int)Math.ceil(right.getX()/scaleMarkerDistance);
			
			for (int i=leftNumber;i<=rightNumber;i++) {
				double x=i*scaleMarkerDistance;
				
				Path2D marker=new Path2D.Double();
				
				Point2D position=mapTransformation.toDevice(new Position(x,0.0,0.0));
				
				marker.moveTo(position.getX()-scaleMarkerSize*.25f,getHeight()-1.0);
				marker.lineTo(position.getX()+scaleMarkerSize*.25f,getHeight()-1.0);
				marker.lineTo(position.getX(),getHeight()-scaleMarkerSize-1.0);
				marker.closePath();
				
				g2d.fill(marker);
			}
		}
	}
	
	private void drawRunwayCentrelines(Graphics2D g2d, IDeviceTransformation deviceTransformation, ICoordinateTransformation transform) {
		Set<Aerodrome> aerodromes=scenario.getSector().getFixDB().getFixes(Aerodrome.class);
		
		g2d.setColor(Palette.BEACON);
		
		for (Aerodrome aerodrome: aerodromes) {
			for (Runway runway: aerodrome.getRunways()) {
				Position pos=transform.forward(runway.getCenter());
				double dx,dy;
				double length=runway.getLength();
				double heading=runway.getTrueHeading()/Units.RAD;
				
				dx=Math.sin(heading)*(centrelineLength+length/2.0);
				dy=Math.cos(heading)*(centrelineLength+length/2.0);
				
				Position end1=new Position(pos.getX()+dx,pos.getY()+dy,0.0);
				Position end2=new Position(pos.getX()-dx,pos.getY()-dy,0.0);
				
				Point2D deviceEnd1=deviceTransformation.toDevice(end1);
				Point2D deviceEnd2=deviceTransformation.toDevice(end2);
				
				Line2D line=new Line2D.Double(deviceEnd1,deviceEnd2);
				
				g2d.draw(line);
			}
		}
	}
	
	public synchronized void update(double dt) {
		for (IAircraft aircraft: scenario.getAircraft()) {
			AircraftSymbol aircraftSymbol;
			
			if (aircraftSymbolMap.containsKey(aircraft)) {
				aircraftSymbol=aircraftSymbolMap.get(aircraft);
			} else {
				aircraftSymbol=new AircraftSymbol(radarPlanViewContext,aircraft);
				aircraftSymbolMap.put(aircraft,aircraftSymbol);
				autolabeller.addLabeledObject(aircraftSymbol);
			}
			aircraftSymbol.update(dt);
		}
		
		Set<IAircraft> removedAircraft=new HashSet<IAircraft>();
		
		for (IAircraft aircraft: aircraftSymbolMap.keySet()) {
			if (!scenario.hasAircraft(aircraft))
				removedAircraft.add(aircraft);
		}
		
		for (IAircraft aircraft: removedAircraft) {
			autolabeller.removeLabeledObject(aircraftSymbolMap.get(aircraft));
			aircraftSymbolMap.remove(aircraft);
		}
		
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400,400);
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}
}
