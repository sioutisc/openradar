package de.knewcleus.openradar.gui.status.runways;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBox;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.impl.Glideslope;
import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.fgfs.navdata.model.IRunway;
import de.knewcleus.fgfs.navdata.model.IRunwayEnd;
import de.knewcleus.openradar.weather.MetarData;

/**
 * This class provides the runway data for the frontend.
 * 
 * @author Wolfram Wagner
 *
 */
public class GuiRunway implements ActionListener {

    public enum Usabilty {CLOSED, HEAVY_ONLY, WARNING, OPEN}
    
    private volatile MetarData metar = null;
    private IRunwayEnd runwayEnd = null;
    private RunwayPanel runwayPanel = null;
    private static DecimalFormat df = new DecimalFormat("0.00");
    
    public GuiRunway(RunwayEnd runwayEnd) {
        this.runwayEnd = runwayEnd;
        
    }

    public void setRunwayPanel(RunwayPanel runwayPanel) {
        this.runwayPanel = runwayPanel;
    }

    public void setMetar(MetarData metar) {
        this.metar=metar;
    }
    
    public String getCode() {
        return runwayEnd.getRunwayID();
    }

   public String getTrueHeading() {
       return df.format(runwayEnd.getTrueHeading());
   }
    
    public String getIlsFrequency() {
        if(hasIls()) {
            return String.format("%3.2f",runwayEnd.getGlideslope().getFrequency().getValue()/Units.MHz);
        } else {
            return "";
        }
    }
    
    public boolean hasIls() {
        return runwayEnd.getGlideslope()!=null;
    }    
    
    public int getWindDirection() {
        return metar.getWindDirection();
    }

    /**
     * Returns the number in degrees how much the wind differs from optimal
     * direction (directly from front)
     * 
     * So 0 is optimal, 90/-90 a shear wind and 180/-180 the wind from behind
     */
    public double getWindDeviation() {
        double runwayHeading = runwayEnd.getTrueHeading();
        double windDir = metar.getWindDirection();
        
        double normalizedWindDir = windDir-runwayHeading;
        normalizedWindDir = normalizedWindDir<-180 ? normalizedWindDir+360 : normalizedWindDir;
        normalizedWindDir = normalizedWindDir>180 ? normalizedWindDir-360 : normalizedWindDir;
        
        return normalizedWindDir;
    }

    /**
     * Returns the effective wind strength in shear direction (90 degrees).
     *  
     * @return the strength of the shear component of the wind in knots. 
     */
    public double getCrossWindSpeed() {
        double angle = getWindDeviation()/360*2*Math.PI;//
        return Math.abs(Math.sin(angle)*getWindSpeed());
    }

    /**
     * Returns the effective wind strength blowing from true runway heading.
     *  
     * @return the strength of the shear component of the wind in knots. 
     */
    public double getHeadWindSpeed() {
        double angle = getWindDeviation()/360*2*Math.PI;//
        return Math.cos(angle)*getWindSpeed();
    }
    
    /**
     * Returns the effective wind strength in cross direction (90 degrees).
     *  
     * @return the strength of the shear component of the wind in knots. 
     */
    public double getCrossWindGusts() {
        if(metar.getWindSpeedGusts()==-1) return -1;
        double angle = getWindDeviation()/360*2*Math.PI;//
        return Math.abs(Math.sin(angle)*metar.getWindSpeedGusts());
    }

    /**
     * Returns the effective wind strength in cross direction (90 degrees).
     *  
     * @return the strength of the shear component of the wind in knots. 
     */
    public double getHeadWindGusts() {
        if(metar.getWindSpeedGusts()==-1) return -1;
        double angle = getWindDeviation()/360*2*Math.PI;//
        return Math.cos(angle)*metar.getWindSpeedGusts();
    }

    public int getWindSpeed() {
        return metar.getWindSpeed();
    }

    public float getHeight() {
        return runwayEnd.getRunway().getAerodrome().getElevation();
    }

    public float getWidth() {
        return runwayEnd.getRunway().getWidth();
    }

    public float getLength() {
        return runwayEnd.getRunway().getLength();
    }

    public Usabilty getUsability() {
        // land with wind
        if(Math.abs(getWindDeviation())>90 && getWindSpeed()>3) return Usabilty.CLOSED;
        // Strong sidewinds
        if(Math.abs(getWindDeviation())<=10 && getWindSpeed()>20) return Usabilty.CLOSED;
        if(Math.abs(getWindDeviation())<=90 && getWindSpeed()>10) return Usabilty.HEAVY_ONLY;
        if(Math.abs(getWindDeviation())>45 && getWindSpeed()>5) return Usabilty.HEAVY_ONLY;
        // minor deviation, mid winds
        if(Math.abs(getWindDeviation())<=45 && getWindSpeed()>5 && getWindSpeed()<=10) return Usabilty.WARNING;

        return Usabilty.OPEN;
    }
    
    public String getUseability() {
        switch(getUsability()) {
        case CLOSED:
            return "Closed";
        case HEAVY_ONLY:
            return "Heavy only";
        case OPEN:
            return "Open";
        case WARNING:
            return "Warning!";
        default:
            return "";
        }
    }
    
    public String getFormatedDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Runway: ").append(getCode()).append(" (").append(getTrueHeading()).append("!)\n");
        if(hasIls()) {
            sb.append("ILS: ").append(getIlsFrequency()).append(" / ").append(getTrueHeading()).append("�\n");
        }
        sb.append("Wind: ").append(getWindSpeed()).append("@").append(getWindDirection()).append(" Dev:").append(getWindDeviation()).append("�\n");
        sb.append("Status: ").append(getUseability());
        return sb.toString();
    }

    public IRunwayEnd getRunwayEnd() {
        return runwayEnd;
    }

    public void setRunwayEnd(IRunwayEnd runwayEnd) {
        this.runwayEnd = runwayEnd;
    }

    public boolean isStartingActive() {
        return runwayEnd.getRunway().getStartSide()==runwayEnd.getOppositeEnd();
    }

    public boolean isLandingActive() {
        return runwayEnd.getRunway().getLandSide()==runwayEnd;
    }

    public void addILS(Glideslope gs) {
        runwayEnd.setGlideslope(gs);
    }
    
    public Glideslope getGlideslope() {
        return runwayEnd.getGlideslope();
    }
    
    // ActionListener
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JCheckBox cb = ((JCheckBox)e.getSource());
        String name = cb.getName();
        
        if("STARTING".equals(name) && e.getID()==ActionEvent.ACTION_PERFORMED   ) {
            setStartingActive(!isStartingActive());
        } else if("LANDING".equals(name) && e.getID()==ActionEvent.ACTION_PERFORMED ) {
            setLandingActive(!isLandingActive());
        }
        runwayPanel.updateRunways();
    }

    public void setStartingActive(boolean startingActive) {
        IRunway rw = (IRunway) runwayEnd.getRunway();
        if(startingActive) {
            rw.setStartSide(runwayEnd.getOppositeEnd());
            if(rw.getLandSide()==runwayEnd.getOppositeEnd()) {
                // this means a direction change
                rw.setLandSide(null);
            }
        } else {
            rw.setStartSide(null);
        }
    }
    
    public void setLandingActive(boolean landingActive) {
        IRunway rw = (IRunway) runwayEnd.getRunway();
        if(landingActive) {
            rw.setLandSide(runwayEnd);
            if(rw.getStartSide()==runwayEnd) {
                // this means a direction change
                rw.setStartSide(null);
            }
        } else {
            rw.setLandSide(null);
        }
        runwayPanel.updateRunways();
    }

    public CbModel createStartCbModel() {
        return new CbModel(this, false);
    }

    public CbModel createLandingCbModel() {
        return new CbModel(this, true);
    }

    private class CbModel extends DefaultButtonModel {

        private static final long serialVersionUID = 1L;
        private GuiRunway rw = null;
        private boolean landingMode = false;
        
        public CbModel(GuiRunway rw, boolean landingMode) {
            this.rw = rw;
            this.landingMode=landingMode;
        }
        

        @Override
        public boolean isSelected() {
            boolean r = landingMode ? rw.isLandingActive() : rw.isStartingActive();
            return r;
        }
   }


}