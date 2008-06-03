package de.knewcleus.radar.ui.core;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

public abstract class DisplayElement {
	protected DisplayElementContainer parent=null;
	protected JComponent displayComponent=null;
	protected SymbolActivationManager symbolActivationManager=null;
	protected final Set<DisplayElement> dependents = new HashSet<DisplayElement>();

	public DisplayElementContainer getParent() {
		return parent;
	}
	
	public void paint(Graphics2D g) {
		paintElement(g);
	}

	public abstract void paintElement(Graphics2D g);

	public abstract void validate();

	public abstract Rectangle2D getBounds();
	
	public abstract boolean isHit(Point2D position);
	
	public JComponent getDisplayComponent() {
		DisplayElement element=this;
		
		while (element!=null && element.displayComponent==null) {
			element=element.parent;
		}
		if (element==null)
			return null;
		return element.displayComponent;
	}
	
	public void setDisplayComponent(JComponent displayComponent) {
		this.displayComponent = displayComponent;
		validate();
	}
	
	public SymbolActivationManager getSymbolActivationManager() {
		DisplayElement element=this;
		
		while (element!=null && element.symbolActivationManager==null) {
			element=element.parent;
		}
		if (element==null)
			return null;
		return element.symbolActivationManager;
	}
	
	public void setSymbolActivationManager(SymbolActivationManager symbolActivationManager) {
		this.symbolActivationManager = symbolActivationManager;
	}

	public void invalidate() {
		final JComponent displayComponent=getDisplayComponent();
		if (displayComponent==null)
			return;
		final Rectangle2D bounds=getBounds();
		if (bounds==null)
			return;
		displayComponent.repaint(bounds.getBounds());
	}

	public void addDependent(DisplayElement dependent) {
		dependents.add(dependent);
	}

	public void removeDependent(DisplayElement dependent) {
		dependents.remove(dependent);
	}

	public void validateDependents() {
		for (DisplayElement dependent: dependents) {
			dependent.validate();
		}
	}
	
	/**
	 * Return the location of the hook point relative to the center of the bounds rectangle.
	 */
	public Point2D getRelativeHookPoint(double vx, double vy) {
		final Rectangle2D bounds=getBounds();
		final double dx, dy;
		if (Math.abs(vx)*bounds.getHeight()>Math.abs(vy)*bounds.getWidth()) {
			dx=bounds.getWidth()*Math.signum(vx)/2.0;
			dy=dx*vy/vx;
		} else {
			dy=bounds.getHeight()*Math.signum(vy)/2.0;
			dx=dy*vx/vy;
		}
		
		return new Point2D.Double(dx, dy);
	}
}
