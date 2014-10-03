/**
 * MARCReader.java Version 0.2, November 2001
 *
 * Copyright (C) 2001  Bas Peters (mail@bpeters.com)
 *
 * This file is part of James (Java MARC Events).
 *
 * James is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * James is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with James; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package pt.utl.ist.marc.iso2709;

import org.apache.log4j.Logger;

import pt.utl.ist.util.exceptions.marc.iso2709.IllegalIdentifierException;
import pt.utl.ist.util.exceptions.marc.iso2709.IllegalIndicatorException;
import pt.utl.ist.util.exceptions.marc.iso2709.IllegalTagException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * <code>MARCReader</code> parses MARC records and notifies the
 * {@link MARCHandler} implementation about events occuring in the parsed
 * records.
 * </p>
 * 
 * <p>
 * The following <code>main()</code> method shows the basic use of the
 * <code>MARCReader</code> object:
 * </p>
 * 
 * <pre>
 * public static void main(String args[]) {
 *     String infile = args[0];
 * 
 *     // create a new MARCReader instance
 *     MARCReader marcReader = new MARCReader();
 * 
 *     // register the MARCHandler implementation
 *     marcReader.setMARCHandler(new MARCHandlerImpl());
 * 
 *     // send the file to the parse method
 *     // the try block is there to catch exceptions
 *     // thrown by the parser
 *     try {
 *         marcReader.parse(infile);
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }
 * </pre>
 * <p>
 * <code>MARCReader</code> can throw the following exceptions when it encounters
 * problems while parsing MARC records:
 * </p>
 * <ul>
 * <li>{@link ParseRecordException}
 * <li>{@link ParseDirectoryException}
 * <li>{@link ParseVariableFieldException}
 * <li>{@link IllegalTagException}
 * <li>{@link IllegalIndicatorException}
 * <li>{@link IllegalIdentifierException}
 * </ul>
 * <p>
 * 
 * @author Bas Peters - <a href="mailto:mail@bpeters.com">mail@bpeters.com</a>
 * @version 0.2
 */
public class MARCPartialReader extends MARCReader {
    /**
     * Logger for this class
     */
    private static final Logger log             = Logger.getLogger(MARCPartialReader.class);

    /** MARCPartialReader numberOfRecords */
    protected long              numberOfRecords = 0;
    /** MARCPartialReader reader */
    protected InputStream       reader;

    byte[]                      buf             = new byte[1024];
    int                         bufIdx          = 0;
    int                         read            = 0;

    /**
     * Creates a new instance of this class.
     */
    public MARCPartialReader() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param charset
     */
    public MARCPartialReader(String charset) {
        super(charset);
    }

    /**
     * <p>
     * Sends a file to the MARC parser.
     * </p>
     * 
     * @param stream
     * @param numRecords
     *            the number of records to parse in each call
     */
    public void parse(InputStream stream, long numRecords) {
        numberOfRecords = numRecords;
        //        	if(charset==null)
        //        		reader=new BufferedReader(new FileReader(filename));
        //        	else 
        //        		reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),charset));
        //        	reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"ISO8859-1"));
        reader = stream;
        parseTape(reader);
    }

    /**
     * <p>
     * Sends a file to the MARC parser.
     * </p>
     * 
     * @param filename
     *            the filename
     * @param numRecords
     *            the number of records to parse in each call
     */
    public void parse(String filename, long numRecords) {
        numberOfRecords = numRecords;
        try {
            //        	if(charset==null)
            //        		reader=new BufferedReader(new FileReader(filename));
            //        	else 
            //        		reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),charset));
            //        	reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"ISO8859-1"));
            reader = new FileInputStream(filename);
            parseTape(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    public void continueParse() {
        parseTape(reader);
    }

    /**
     * 
     */
    public void close() {
        try {
            reader.close();
            marcHandler.endTape();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Parses the tape.
     * </p>
     * 
     * @param in
     *            the {@link BufferedReader}
     */
    private void parseTape(InputStream in) {
        boolean isFinished = false;
        long readRecords = 0;

        // if input stream contains no data
        if (in == null) throw new ParseRecordException("no data");

        try {
            // start of tape notification
            if (marcHandler != null) marcHandler.startTape();

            // read tape and process each record
            while (!isFinished) {
                int size = 0;
                byte[] rec = new byte[1024 * 5];
                //    			byte[] buf=new byte[1024];

                if (read <= 0) {
                    read = in.read(buf);
                    bufIdx = 0;
                }
                while (read != -1 && !isFinished) {
                    if (read <= 0) {
                        read = in.read(buf);
                        bufIdx = 0;
                    }
                    for (; bufIdx < buf.length && read > 0; bufIdx++) {
                        read--;
                        rec[size] = buf[bufIdx];
                        size++;
                        if (buf[bufIdx] == rt) {
                            try {
                                parseRecord(rec);
                                readRecords++;
                            } catch (RuntimeException e) {
                                log.error(e.getMessage(), e);
                            }
                            size = 0;
                            if (readRecords >= numberOfRecords) {
                                isFinished = true;
                                bufIdx++;
                                break;
                            }
                        } else if (size == 1 && (buf[bufIdx] == '\n' || buf[bufIdx] == '\r')) {
                            size = 0;
                        } else if (size == rec.length) {
                            //criar um rec maior
                            byte[] recBig = new byte[rec.length * 2];
                            System.arraycopy(rec, 0, recBig, 0, rec.length);
                            rec = recBig;
                        }
                    }

                } // end of while (true)

                if (read < 0) isFinished = true;
            } // end of while (! isFinished)

            // end of tape notification
            if (marcHandler != null) marcHandler.endTape();

        } catch (IOException e) {
            log.error(e, e);
        }
    }

}
// End of MARCREader.java
