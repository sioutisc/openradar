package de.knewcleus.fgfs.navdata.xplane;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.fgfs.navdata.impl.NDBFrequency;
import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.INDB;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavPoint;

public class NavFile implements INavDataStream<INavPoint> {
	protected final BufferedReader bufferedReader;
	
	public NavFile(Reader reader) throws IOException {
		this(new BufferedReader(reader));
	}
	
	public NavFile(BufferedReader bufferedReader) throws IOException {
		this.bufferedReader = bufferedReader;
		// Skip the line-ending-marker (I/A)
		bufferedReader.readLine();
		// Skip the copyright-line
		bufferedReader.readLine();
	}
	
	@Override
	public INavPoint readDatum() throws NavDataStreamException {
		INavPoint record = null;
		do {
			String line;
			try {
				line = bufferedReader.readLine();
			} catch (IOException e1) {
				throw new NavDataStreamException(e1);
			}
			if (line == null) {
				/* End of file reached */
				return null;
			}
			final FieldIterator fieldIterator = new FieldIterator(line);
			if (!fieldIterator.hasNext()) {
				/* Skip empty records */
				continue;
			}
			final String recordTypeString = fieldIterator.next();
			final int recordType;
			try {
				recordType = Integer.parseInt(recordTypeString);
			} catch (NumberFormatException e) {
				throw new NavDataStreamException("Record type must be a number:'"+recordTypeString+"'", e);
			}
			if (recordType == 99) {
				/* End-of-file marker */
				return null;
			}
			record = parseRecord(recordType, fieldIterator);
		} while (record==null);
		return record;
	}
	
	protected INavPoint parseRecord(int recordType, FieldIterator fieldIterator)
		throws NavDataStreamException
	{
		switch (recordType) {
		case 2:
			return parseNDB(fieldIterator);
		default:
			return null;
		}
	}
	
	protected INDB parseNDB(FieldIterator fieldIterator) throws NavDataStreamException {
		final String latString, lonString, elevString, freqString, rangeString;
		final String identification, name;
		try {
			latString = fieldIterator.next();
			lonString = fieldIterator.next();
			elevString = fieldIterator.next();
			freqString = fieldIterator.next();
			rangeString = fieldIterator.next();
			// skip multi-purpose field
			fieldIterator.next();
			identification = fieldIterator.next();
			name = fieldIterator.restOfLine();
		} catch (NoSuchElementException e) {
			throw new NavDataStreamException(e);
		}
		final double lat, lon;
		final float elev, range;
		final float freqKHz;
		lat = Double.parseDouble(latString);
		lon = Double.parseDouble(lonString);
		elev = Float.parseFloat(elevString)*Units.FT;
		range = Float.parseFloat(rangeString)*Units.NM;
		freqKHz = Float.parseFloat(freqString);
		final Point2D geographicPosition = new Point2D.Double(lon, lat);
		final IFrequency frequency = new NDBFrequency(freqKHz);
		return new NDB(geographicPosition, elev, identification, name, frequency, range);
	}
}
