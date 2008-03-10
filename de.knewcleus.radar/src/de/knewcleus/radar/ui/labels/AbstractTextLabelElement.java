package de.knewcleus.radar.ui.labels;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import de.knewcleus.radar.ui.rpvd.AircraftSymbol;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewPanel;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;

public abstract class AbstractTextLabelElement extends AbstractLabelElement implements ILabelElement {
	public AbstractTextLabelElement(AircraftSymbol aircraftSymbol) {
		super(aircraftSymbol);
	}
	
	protected abstract String getText();

	@Override
	public void layout() {
		String text=getText();
		final RadarPlanViewSettings radarPlanViewSettings=aircraftSymbol.getRadarPlanViewContext().getRadarPlanViewSettings();
		final RadarPlanViewPanel radarPlanViewPanel=aircraftSymbol.getRadarPlanViewContext().getRadarPlanViewPanel();
		FontMetrics fm=radarPlanViewPanel.getFontMetrics(radarPlanViewSettings.getFont());
		
		ascent=fm.getMaxAscent();
		
		int height=ascent+fm.getMaxDescent();
		int width=fm.stringWidth(text);
		
		minimumSize=new Dimension(width,height);
	}

	@Override
	public void paint(Graphics2D g2d) {
		String text=getText();
		
		g2d.drawString(text, bounds.x, bounds.y+ascent);
	}
}
