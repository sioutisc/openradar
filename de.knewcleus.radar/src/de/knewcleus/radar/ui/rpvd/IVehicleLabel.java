package de.knewcleus.radar.ui.rpvd;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import de.knewcleus.radar.autolabel.ILabel;
import de.knewcleus.radar.ui.IInteractiveSymbol;

public interface IVehicleLabel extends ILabel, IDisplaySymbol, IInteractiveSymbol {
	public abstract void updateLabelContents();
	public abstract IVehicleSymbol getVehicleSymbol();
	public abstract void processMouseEvent(MouseEvent e);
	public Point2D getHookPosition();
	public void paint(Graphics2D g2d);
}
