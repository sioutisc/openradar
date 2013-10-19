/**
 * Copyright (C) 2013 Wolfram Wagner
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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.rpvd.contact;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.State;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.rpvd.contact.ContactShape.Type;
/**
 * This data block layout aims to be closer on the reality.
 * There is no heading displayed and as long as no or the wrong squawk code is being transmitted,
 * only the known data are transmitted.
 * If no transmitter data arrives, the contact is displayed like it is fully assigned.
 *
 * @author Wolfram Wagner
 */
public class SimulationLayout extends ADatablockLayout {

//    private final DatablockLayoutManager manager;
    private final Font font = new Font("Courier", Font.PLAIN, 11);

    public SimulationLayout(DatablockLayoutManager manager) {
//        this.manager = manager;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }


    @Override
    public String getMenuText() {
        return "Simulation (Transponder enabled)";
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public Color getBackgroundColor(GuiRadarContact contact, boolean highlighted) {
        if(highlighted || contact.isIdentActive()) {
            return Color.white;
        }
        return Palette.LANDMASS;
    }

    @Override
    public Color getColor(GuiRadarContact c) {
        Color color = Palette.RADAR_UNCONTROLLED;

        boolean assignedSquawkTunedIn = c.getAssignedSquawk()==null || (c.getTranspSquawkCode()!=null && c.getAssignedSquawk()!=null && c.getTranspSquawkCode().equals(c.getAssignedSquawk()));

        if(c.isIdentActive()) {
            color=Color.black;
            c.setHighlighted();
        } else if(c.getTranspSquawkCode()!=null && 7700==c.getTranspSquawkCode()) {
            // Emergency
            color=Color.red;

        } else if(!assignedSquawkTunedIn) {
            color = new Color(80,0,160);
        } else if(c.isSelected()) {
            // SELECTED
            color=Palette.RADAR_SELECTED;

        } else if(!c .isActive()) {
            // INCACTIVE GHOSTS
            color=Palette.RADAR_GHOST;

        } else if(c.isNeglect()) {
            // BAD GUYS
            color=Palette.RADAR_GHOST;

        } else if(c.getState()==State.IMPORTANT) {
            // CONTROLLED left column
            color=Palette.RADAR_CONTROLLED;

        } else if(c.getState()==State.CONTROLLED) {
            // WATCHED middle column
            color=Palette.RADAR_IMPORTANT;
        } else {
            // UNCONTROLLED right column
            color=Palette.RADAR_UNCONTROLLED;
        }

        return color;
    }

    @Override
    public String getDataBlockText(GuiRadarContact c) {
        if(c.isAtc()) {
            return String.format("%s\n%s",c.getCallSign(),c.getAircraftCode());

        }


        boolean transmitterAvailable = c.getTranspSquawkCode() !=null;
        if(transmitterAvailable) {
            // there is a transmitter
            boolean assignedSquawkTunedIn = c.getAssignedSquawk()==null || (c.getTranspSquawkCode()!=null && c.getAssignedSquawk()!=null && c.getTranspSquawkCode().equals(c.getAssignedSquawk()));
            StringBuilder sb = new StringBuilder();

            if(7500==c.getTranspSquawkCode()) {
                sb.append("HJ").append("\n");
            } else if(7600==c.getTranspSquawkCode()) {
                sb.append("RF").append("\n");
            } else if(7700==c.getTranspSquawkCode()) {
                sb.append("EM").append("\n");
            }

            if(!assignedSquawkTunedIn) {
                // squawk codes do not match
                sb.append(String.format("%s %s",""+c.getTranspSquawkCode(),c.getMagnCourse())).append("\n");
                if(-9999!=c.getTranspAltitude()) {
                    sb.append(String.format("%03d",c.getTranspAltitude()/100)).append(" ");
                } else {
                    sb.append(String.format("%03.0f",c.getAltitude()/100)).append("*");
                }
                sb.append(String.format("%02.0f",c.getGroundSpeedD()/10));
            } else {
                // squawk codes match
                sb.append(String.format("%s %s",c.getCallSign(),c.getMagnCourse())).append("\n");
                sb.append(c.getAircraftCode());
                String addData = getAddData(c);
                if(addData.isEmpty()) {
                    sb.append(" ").append(addData);
                }
                sb.append("\n");
                if(-9999!=c.getTranspAltitude()) {
                    sb.append(String.format("%03d",c.getTranspAltitude()/100)).append(" ");
                } else {
                    sb.append(String.format("%03.0f",c.getAltitude()/100)).append("*");
                }
                sb.append(String.format("%02.0f",c.getGroundSpeedD()/10));

            }
            return sb.toString();
        } else {
            String addData = getAddData(c);
            return String.format("%s %s\n",c.getCallSign(),c.getMagnCourse()) +
            	   String.format("%s %s\n",c.getAircraftCode(),addData)+
                   String.format("%03.0f*%02.0f",c.getAltitude()/100,c.getGroundSpeedD()/10);
        }
    }

    private String getAddData(GuiRadarContact c) {
        FlightPlanData fp = c.getFlightPlan();
        if(fp.getAssignedRoute()!=null && !fp.getAssignedRoute().isEmpty()) {
            return fp.getAssignedRoute();
        }
        if(fp.contactWillLandHere() &&
           fp.getAssignedRunway()!=null && !fp.getAssignedRunway().isEmpty()) {

            return "rw"+fp.getAssignedRunway();
        }
        if(fp.getDestinationAirport()!=null && !fp.getDestinationAirport().isEmpty()) {
            return fp.getDestinationAirport();
        }

        return "";
    }

    @Override
    public void modify(ContactShape shape, GuiRadarContact c) {

        if(c.getTranspSquawkCode()!=null && ( 1200==c.getTranspSquawkCode() || 7000==c.getTranspSquawkCode())) {
            // Squawking VFR
            shape.modify(Type.EmptySquare, c, 6);
        } else if(c.getTranspSquawkCode()!=null && c.getAtcLetter()==null) {
            // untracked
            shape.modify(Type.Asterix, c, 6);
        } else  if(c.getTranspSquawkCode()==null) {
            // no squawk or standby
            shape.modify(Type.FilledDiamond, c, 8);
        } else if(c.getTranspSquawkCode()!=null && c.getAtcLetter()!=null) {
            // controlled
            shape.modify(Type.Letter, c, 8);
        } else {
            shape.modify(Type.FilledDot, c, 6);
        }
    }
}
