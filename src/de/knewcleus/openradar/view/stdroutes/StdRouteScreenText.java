/**
 * Copyright (C) 2013-2016 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
/**
 * A text that appears on top of the map, but does not pan, nor zoom with it.
 *
 * @author Wolfram Wagner
 *
 */
public class StdRouteScreenText extends AStdRouteElement {

    private String screenPos;
    private Double angle;
    private final boolean clickable;
    private final String text;
    private volatile Rectangle2D extBounds;

    public StdRouteScreenText(AirportData data, StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                        String screenPos, String angle, boolean clickable, String text,StdRouteAttributes attributes) {
        super(data, mapViewAdapter, new Point2D.Double(0,0),null,attributes);

        this.screenPos = screenPos;

        this.angle = angle !=null ? Double.parseDouble(angle)+magDeclination : 0;
        this.clickable = clickable;
        this.text=text;
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        Point2D displayPoint = getDisplayPoint(mapViewAdapter, screenPos);
        AffineTransform oldTransform = g2d.getTransform();
        AffineTransform newTransform = new AffineTransform();
        newTransform.setToRotation(Math.toRadians(angle), displayPoint.getX(), displayPoint.getY());
        g2d.transform(newTransform);

        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
        g2d.drawString(text, (int)(displayPoint.getX()-bounds.getWidth()/2), (int)(displayPoint.getY()+bounds.getHeight()/2-2));

        g2d.setTransform(oldTransform);

        bounds.setRect(displayPoint.getX(), displayPoint.getY(),bounds.getWidth(),bounds.getHeight());
        extBounds = new Rectangle2D.Double(displayPoint.getX()-bounds.getWidth(),displayPoint.getY()-bounds.getHeight(),2*bounds.getWidth(),2*bounds.getHeight());
        return bounds;
    }
    /**
     * This method exists to move negative values to the other side of the screen
     */
    protected Point2D getDisplayPoint(IMapViewerAdapter mapViewAdapter, String screenPos) {

        Rectangle2D maxBounds = mapViewAdapter.getViewerExtents();

        String sX = screenPos.substring(0,screenPos.indexOf(","));
        String sY = screenPos.substring(screenPos.indexOf(",")+1);

        final double x,y;

        if(sX.equalsIgnoreCase("center")) {
            x = maxBounds.getCenterX();
        } else {
            x = Double.parseDouble(sX);

        }
        if(sY.equalsIgnoreCase("center")) {
            y = maxBounds.getCenterY();
        } else {
            y = Double.parseDouble(sY);

        }
        Point2D p = new Point2D.Double(x,y);

        if(p.getX()<0) {
            p = new Point2D.Double(maxBounds.getWidth()+p.getX(),p.getY());
        }
        if(p.getY()<0) {
            p = new Point2D.Double(p.getX(),maxBounds.getHeight()+p.getY());
        }
        return p;
    }

    @Override
    public Point2D getEndPoint() {
        return null;
    }

    @Override
    public synchronized boolean contains(Point p) {
    	if(!clickable || extBounds==null) {
    		return false;
    	}
        return extBounds.contains(p);
    }

    @Override
    public boolean isClickable() {
 		return clickable;
 	}
}
