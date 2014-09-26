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

package pt.utl.ist.repox.marc.iso2709;

import org.apache.log4j.Logger;

import pt.utl.ist.repox.marc.Record;
import pt.utl.ist.repox.marc.iso2709.datatype.*;
import pt.utl.ist.repox.marc.util.Leader;

import java.io.*;

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
public class MARCReader {
    private static final Logger log = Logger.getLogger(MARCReader.class);
    /** MARCReader charset */
    protected String            charset;
    /** MARCReader rt */
    public char                 rt  = Record.RT;

    /**
     * Field terminator character (ASCII octal 036). 30
     */
    public char                 ft  = Record.FT;

    /**
     * Delimiter (Unit Separator ASCII octal 037). 31
     */
    public char                 us  = Record.US;

    /**
     * Creates a new instance of this class.
     */
    public MARCReader() {
        //    	this.charset = NUtil.getSystemCharset();
        this.charset = null;
        //    	this.charset = "ASCII";
        //    	this.charset = "ISO8859-1";
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param charset
     */
    public MARCReader(String charset) {
        this.charset = charset;
    }

    @SuppressWarnings("javadoc")
    public void setRecordTerminator(char rt) {
        this.rt = rt;
    }

    @SuppressWarnings("javadoc")
    public void setFieldTerminator(char ft) {
        this.ft = ft;
    }

    @SuppressWarnings("javadoc")
    public void setUnitDelimiter(char us) {
        this.us = us;
    }

    /** The MARCHandler object. */
    protected MARCHandler marcHandler;

    /**
     * <p>
     * Registers the <code>MARCHandler</code> implementation.
     * </p>
     * 
     * @param marcHandler
     *            the {@link MARCHandler} implementation
     */
    public void setMARCHandler(MARCHandler marcHandler) {
        this.marcHandler = marcHandler;
    }

    /**
     * <p>
     * Sends a file to the MARC parser.
     * </p>
     * 
     * @param filename
     *            the filename
     */
    public void parse(String filename) {
        try {
            parseTape(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Sends an input stream to the MARC parser.
     * </p>
     * 
     * @param src
     *            the input stream source
     */
    public void parse(InputStream src) {
        parseTape(src);
    }

    //    
    //    /**
    //     * <p>Sends an input stream to the MARC parser.</p>
    //     *
    //     * @param src the input stream source
    //     */
    //    public void parse(BufferedReader src) {
    //        parseTape(src);
    //    }    

    //    /**
    //     * <p>Parses the tape.</p>
    //     *
    //     * @param in the {@link BufferedReader}
    //     */
    //    private void parseTape(BufferedReader in) {
    //        boolean isFinished = false;
    //        
    //        // if input stream contains no data
    //        if (in == null)
    //            throw new ParseRecordException("no data");
    //        
    //        try {
    //            // start of tape notification
    //            if (marcHandler != null)
    //                marcHandler.startTape();
    //            
    //            // read tape and process each record
    //            while (! isFinished) {
    //                StringBuffer sb = new StringBuffer();
    //                while (true) {
    //                    int i = in.read();
    //                    if (i == -1) {
    //                        isFinished = true;
    //                        break;
    //                    } else if(i == Record.RT) {
    //                        sb.append((char)i);
    //                        parseRecord(sb.toString());
    //                        break;
    //                    } else {
    //                        sb.append((char)i);
    //                    } // end of if (i == -1)
    //                } // end of while (true)
    //            } // end of while (! isFinished)
    //            
    //            // end of tape notification
    //            if (marcHandler != null)
    //                marcHandler.endTape();
    //            
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //    }
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

        // if input stream contains no data
        if (in == null) throw new ParseRecordException("no data");

        try {
            // start of tape notification
            if (marcHandler != null) marcHandler.startTape();

            // read tape and process each record
            while (!isFinished) {
                int size = 0;
                byte[] rec = new byte[1024 * 5];
                byte[] buf = new byte[1024];

                int read = in.read(buf);
                System.out.println((char)read);
                while (read != -1) {
                    for (byte aBuf : buf) {
                        rec[size] = aBuf;
                        size++;
                        if (aBuf == rt) {
                            parseRecord(rec);
                            size = 0;
                        } else if (aBuf == '\n' && size == 1) {
                            size = 0;
                        } else if (size == rec.length) {
                            //criar um rec maior
                            byte[] recBig = new byte[rec.length * 2];
                            System.arraycopy(rec, 0, recBig, 0, rec.length);
                            //    						rec=recBig;
                        }
                    }
                } // end of while (true)
            } // end of while (! isFinished)

            // end of tape notification
            if (marcHandler != null) marcHandler.endTape();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param record
     */
    protected void parseRecord(byte[] record) {
        //    	System.out.println(record);

        try {
            String leaderStr = getString(record, 0, 24);

            int recordLength = Numeric.getValue(leaderStr.substring(0, 5));
            int baseAddress = Numeric.getValue(leaderStr.substring(12, 17));

            // validate record by checking if the final character
            // is a record terminator and if the length of the
            // record string equals the record length in the leader.
            // If the record is not valid the method returns 1.
            if (record[recordLength - 1] != rt) throw new ParseRecordException("final character is not a record terminator:" + new String(record, 0, recordLength - 1));

            //			if (recordLength != record.length)
            //			    throw new ParseRecordException(
            //			    "logical record length not equal to length of parsed record");

            // directory and directory length
            String dir = getString(record, 24, (baseAddress - 24 - 1));
            int dirLength = dir.length();

            // validate the directory by checking if the final character
            // is a field terminator, if the length is not null and
            // if the modulo of the directory length divided by 12 equals 0.
            if (record[baseAddress - 1] != ft) throw new ParseDirectoryException("final character is not a field terminator");

            if (dirLength == 0) throw new ParseDirectoryException("directory length is null");

            if ((dirLength % 12) != 0) throw new ParseDirectoryException("directory modulo 12 is not null");

            // build leader object
            Leader leader = new Leader(leaderStr);
            //        leader.setRecordLength(recordLength);
            //        leader.setRecordStatus(record.charAt(5));
            //        leader.setTypeOfRecord(record.charAt(6));
            //        leader.setImplDefined1(record.substring(7,9).toCharArray());
            //        leader.setCharCodingScheme(record.charAt(9));
            //        leader.setIndicatorCount(record.charAt(10));
            //        leader.setSubfieldCodeLength(record.charAt(11));
            //        leader.setBaseAddressOfData(baseAddress);
            //        leader.setImplDefined2(record.substring(17, 20).toCharArray());
            //        leader.setEntryMap(record.substring(20, 24).toCharArray());

            // start of record notification
            if (marcHandler != null) marcHandler.startRecord(leader);

            // parse directory
            int entries = dirLength / 12;
            for (int i = 0; i < entries; i++) {

                try {
                    // get tag name
                    String tag = dir.substring(i * 12, i * 12 + 3);
                    if (!Tag.isValid(tag)) throw new IllegalTagException(tag);

                    // get field length
                    String fieldLength = dir.substring(i * 12 + 3, i * 12 + 7);
                    if (!Numeric.isValid(fieldLength)) throw new ParseDirectoryException("length of field is not an integer");
                    int length = Numeric.getValue(fieldLength);

                    // get starting character position
                    String fieldStart = dir.substring(i * 12 + 7, i * 12 + 12);
                    if (!Numeric.isValid(fieldStart)) throw new ParseDirectoryException("starting character position is not an integer");
                    int start = Numeric.getValue(fieldStart);

                    // check field length
                    if ((start + length) == 0) throw new ParseVariableFieldException(tag, "length is null");

                    // check field terminator
                    if ((record[start + baseAddress + length - 1]) != ft) throw new ParseVariableFieldException(tag, "final character is not a field terminator");

                    // get field data
                    //					String field = new String(record, start + baseAddress, length-1 , charset);

                    // do not parse data elements if the field
                    // contains only a field terminator
                    //					if (field.length() == 0) {
                    //					    break;
                    //					}

                    // parse data elements
                    if (Tag.isControlField(tag)) {
                        // control field notification
                        if (marcHandler != null) marcHandler.controlField(tag, getString(record, start + baseAddress, length - 1));
                    } else {

                        parseDataField(record, tag, start + baseAddress, length - 1);

                        //						int stIdx=0;
                        //						int enIdx=-1;
                        //						
                        //						
                        //						
                        //						
                        //					    if (field.lastIndexOf(Record.US)==-1){
                        //					        field="  "+String.valueOf(Record.US)+"a"+field;
                        //					    }
                        //					    
                        //					    char ind1 = field.charAt(0);
                        //					    char ind2 = field.charAt(1);
                        //					    
                        //					    // validate indicator value for ind1
                        //					    if (! Indicator.isValid(ind1))
                        //					        ind1='a';
                        //					        //throw new IllegalIndicatorException(tag, ind1);
                        //					    
                        //					    // validate indicator value for ind2
                        //					    if (! Indicator.isValid(ind2))
                        //					        ind2='a';
                        //					        //throw new IllegalIndicatorException(tag, ind2);
                        //					    
                        //					    // start of data field notification
                        //					    if (marcHandler != null)
                        //					        marcHandler.startDataField(tag,
                        //					        field.charAt(0), field.charAt(1));
                        //					    
                        //					    // do not parse other data elements if the field
                        //					    // only contains indicator values
                        //					    if (field.length() < 4) {
                        //					        break;
                        //					    }
                        //					    
                        //					    // parse data elements for data field
                        //					    parseDataField(field.substring(3));
                        //					    
                        //					    // end of data field notification
                        //					    if (marcHandler != null)
                        //					        marcHandler.endDataField(tag);

                    } // end of if (Tag.isControlField(tag))
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    log.warn(e.getMessage(), e);
                }
            } // end of for (int i=0; i<entries; i++)

            // end of record notification
            if (marcHandler != null) marcHandler.endRecord();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    //    protected void parseRecord(String record) {
    ////    	System.out.println(record);
    //    	
    //    	
    //        int recordLength = Numeric.getValue(record.substring(0, 5));
    //        int baseAddress = Numeric.getValue(record.substring(12, 17));
    //        
    //        // validate record by checking if the final character
    //        // is a record terminator and if the length of the
    //        // record string equals the record length in the leader.
    //        // If the record is not valid the method returns 1.
    //        if (record.charAt(recordLength -1) != Record.RT)
    //            throw new ParseRecordException(
    //            "final character is not a record terminator");
    //        
    //        if (recordLength != record.length())
    //            throw new ParseRecordException(
    //            "logical record length not equal to length of parsed record");
    //        
    //        // directory and directory length
    //        String dir = record.substring(24, (baseAddress -1));
    //        int dirLength = dir.length();
    //        
    //        // validate the directory by checking if the final character
    //        // is a field terminator, if the length is not null and
    //        // if the modulo of the directory length divided by 12 equals 0.
    //        if (record.charAt(baseAddress -1) != Record.FT)
    //            throw new ParseDirectoryException(
    //            "final character is not a field terminator");
    //        
    //        if (dirLength == 0)
    //            throw new ParseDirectoryException(
    //            "directory length is null");
    //        
    //        if ((dirLength % 12) != 0)
    //            throw new ParseDirectoryException(
    //            "directory modulo 12 is not null");
    //        
    //        // build leader object
    //        Leader leader = new Leader();
    //        leader.setRecordLength(recordLength);
    //        leader.setRecordStatus(record.charAt(5));
    //        leader.setTypeOfRecord(record.charAt(6));
    //        leader.setImplDefined1(record.substring(7,9).toCharArray());
    //        leader.setCharCodingScheme(record.charAt(9));
    //        leader.setIndicatorCount(record.charAt(10));
    //        leader.setSubfieldCodeLength(record.charAt(11));
    //        leader.setBaseAddressOfData(baseAddress);
    //        leader.setImplDefined2(record.substring(17, 20).toCharArray());
    //        leader.setEntryMap(record.substring(20, 24).toCharArray());
    //        
    //        // start of record notification
    //        if (marcHandler != null)
    //            marcHandler.startRecord(leader);
    //        
    //        // parse directory
    //        int entries = dirLength / 12;
    //        for (int i=0; i<entries; i++) {
    //            
    //            try {
    //				// get tag name
    //				String tag = dir.substring(i*12, i*12 + 3);
    //				if (! Tag.isValid(tag))
    //				    throw new IllegalTagException(tag);
    //				
    //				// get field length
    //				String fieldLength = dir.substring(i*12 + 3, i*12 + 7);
    //				if (! Numeric.isValid(fieldLength))
    //				    throw new ParseDirectoryException(
    //				    "length of field is not an integer");
    //				int length = Numeric.getValue(fieldLength);
    //				
    //				// get starting character position
    //				String fieldStart = dir.substring(i*12 + 7, i*12 + 12);
    //				if (! Numeric.isValid(fieldStart))
    //				    throw new ParseDirectoryException(
    //				    "starting character position is not an integer");
    //				int start = Numeric.getValue(fieldStart);
    //				
    //				// check field length
    //				if ((start + length) == 0)
    //				    throw new ParseVariableFieldException(tag, "length is null");
    //				
    //				// check field terminator
    //				if ((record.charAt(start + baseAddress + length - 1)) != Record.FT)
    //				    throw new ParseVariableFieldException(tag,
    //				    "final character is not a field terminator");
    //				
    //				// get field data
    //				String field = record.substring(
    //				(start + baseAddress),
    //				(start + baseAddress + length - 1));
    //				
    //				          
    //				
    //				// do not parse data elements if the field
    //				// contains only a field terminator
    //				if (field.length() == 0) {
    //				    break;
    //				}
    //				
    //				// parse data elements
    //				if (Tag.isControlField(tag)) {               
    //				    // control field notification
    //				    if (marcHandler != null)
    //				        marcHandler.controlField(tag, field.toCharArray());
    //				} else {
    //				    if (field.lastIndexOf(Record.US)==-1){
    //				        field="  "+String.valueOf(Record.US)+"a"+field;
    //				    }
    //				    
    //				    char ind1 = field.charAt(0);
    //				    char ind2 = field.charAt(1);
    //				    
    //				    // validate indicator value for ind1
    //				    if (! Indicator.isValid(ind1))
    //				        ind1='a';
    //				        //throw new IllegalIndicatorException(tag, ind1);
    //				    
    //				    // validate indicator value for ind2
    //				    if (! Indicator.isValid(ind2))
    //				        ind2='a';
    //				        //throw new IllegalIndicatorException(tag, ind2);
    //				    
    //				    // start of data field notification
    //				    if (marcHandler != null)
    //				        marcHandler.startDataField(tag,
    //				        field.charAt(0), field.charAt(1));
    //				    
    //				    // do not parse other data elements if the field
    //				    // only contains indicator values
    //				    if (field.length() < 4) {
    //				        break;
    //				    }
    //				    
    //				    // parse data elements for data field
    //				    parseDataField(field.substring(3));
    //				    
    //				    // end of data field notification
    //				    if (marcHandler != null)
    //				        marcHandler.endDataField(tag);
    //				    
    //				} // end of if (Tag.isControlField(tag))
    //			} catch (RuntimeException e) {
    //				log.warn(e.getMessage(), e);
    //			}
    //        } // end of for (int i=0; i<entries; i++)
    //        
    //        // end of record notification
    //        if (marcHandler != null)
    //            marcHandler.endRecord();
    //    }

    private void parseDataField(String dataField) {
        boolean isFinished = false;
        int i = 0;
        while (!isFinished) {
            StringBuffer sb = new StringBuffer();
            char c;
            while (true) {
                c = dataField.charAt(i);
                if (c == us)
                    break;
                else
                    sb.append(c);
                i++;
                if (i == dataField.length()) {
                    isFinished = true;
                    break;
                }
            } // end of while (true)

            // validate data element identifier value
            if (sb.length() > 0) {
                char identifier = sb.charAt(0);
                if (!Identifier.isValid(identifier)) {
                    identifier = 'a';
                    //                throw new IllegalIdentifierException(identifier);
                }

                // subfield notification
                marcHandler.subfield(identifier, sb.substring(1));
            }
            i++;
        } // end of while (! isFinished)
    }

    /**
     * @param record
     * @param tag
     * @param stIdx
     * @param len
     */
    protected void parseDataField(byte[] record, String tag, int stIdx, int len) {
        try {

            //    		System.out.println(tag+new String(record, stIdx, len));

            int lastUS = -1;
            int i = 0;
            for (; i < len; i++) {
                byte bt;
                if (i == len)
                    bt = (byte)us;
                else
                    bt = record[stIdx + i];
                if (bt == us) {
                    if (lastUS == -1) {
                        if (i > 2 || i == 0) {
                            if (marcHandler != null) {
                                marcHandler.startDataField(tag, ' ', ' ');
                            }
                        } else if (i == 1) {
                            if (marcHandler != null) {
                                String inds = getString(record, stIdx, 1);
                                char ind1 = inds.charAt(0);
                                if (!Indicator.isValid(ind1)) ind1 = 'a';
                                marcHandler.startDataField(tag, ' ', ind1);
                            }
                        } else {
                            String inds = getString(record, stIdx, 2);
                            if (marcHandler != null) {
                                char ind1 = inds.charAt(0);
                                char ind2 = inds.charAt(1);

                                // validate indicator value for ind1
                                if (!Indicator.isValid(ind1)) ind1 = 'a';
                                //throw new IllegalIndicatorException(tag, ind1);

                                // validate indicator value for ind2
                                if (!Indicator.isValid(ind2)) ind2 = 'a';
                                //throw new IllegalIndicatorException(tag, ind2);

                                marcHandler.startDataField(tag, inds.charAt(0), inds.charAt(1));
                            }
                        }
                    } else {
                        char identifier = (char)record[stIdx + lastUS + 1];
                        if (!Identifier.isValid(identifier)) {
                            identifier = 'a';
                            //                throw new IllegalIdentifierException(identifier);
                        }

                        if (marcHandler != null) marcHandler.subfield(identifier, getString(record, stIdx + lastUS + 2, i - lastUS - 2));
                        //			            marcHandler.subfield(identifier, new String(record, stIdx+lastUS+2, len, charset));
                    }
                    lastUS = i;
                }

                //        			    if (field.lastIndexOf(Record.US)==-1){
                //					        field="  "+String.valueOf(Record.US)+"a"+field;
                //					    }
                //					    
                //					    char ind1 = field.charAt(0);
                //					    char ind2 = field.charAt(1);
                //					    
                //					    // validate indicator value for ind1
                //					    if (! Indicator.isValid(ind1))
                //					        ind1='a';
                //					        //throw new IllegalIndicatorException(tag, ind1);
                //					    
                //					    // validate indicator value for ind2
                //					    if (! Indicator.isValid(ind2))
                //					        ind2='a';
                //					        //throw new IllegalIndicatorException(tag, ind2);
                //					    
                //					    // start of data field notification
                //					    if (marcHandler != null)
                //					        marcHandler.startDataField(tag,
                //					        field.charAt(0), field.charAt(1));
                //            		}
                //            	}
                //            }

            }

            if (lastUS == -1) {
                if (marcHandler != null) {
                    marcHandler.startDataField(tag, ' ', ' ');
                    marcHandler.subfield('a', getString(record, stIdx, len - 1));
                }
            } else {
                char identifier = (char)record[stIdx + lastUS + 1];
                if (!Identifier.isValid(identifier)) {
                    identifier = 'a';
                    //                throw new IllegalIdentifierException(identifier);
                }

                if (marcHandler != null) marcHandler.subfield(identifier, getString(record, stIdx + lastUS + 2, i - lastUS - 2));
            }

            //			
            //	    	if(lastUS==-1) {
            //	            if (marcHandler != null) {
            //	                marcHandler.startDataField(tag, ' ', ' ');
            //	                marcHandler.subfield('a', new String(record, stIdx, len, charset));
            //	            }
            //	    	}
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        //    	
        //        boolean isFinished = false;
        //        int i = 0;
        //        while(! isFinished) {
        //            StringBuffer sb = new StringBuffer();
        //            char c;
        //            while (true) {
        //                c = dataField.charAt(i);
        //                if (c == Record.US) break;
        //                else sb.append(c);
        //                i++;
        //                if (i == dataField.length()) {
        //                    isFinished = true;
        //                    break;
        //                }
        //            } // end of while (true)
        //            
        //            // validate data element identifier value
        //            if (sb.length()>0) {
        //	            char identifier = sb.charAt(0);
        //	            if (! Identifier.isValid(identifier)){
        //	                identifier='a';
        //	//                throw new IllegalIdentifierException(identifier);
        //	            }
        //	            
        //	            // subfield notification
        //	            char[] data = sb.substring(1).toCharArray();
        //	            if (marcHandler != null)
        //	                marcHandler.subfield(identifier, data);
        //            }
        //            i++;
        //        } // end of while (! isFinished)
    }

    /**
     * @param str
     * @return Record
     */
    public static Record readRecordFromIso2709String(String str) {
        return new Record(str);
    }

    @SuppressWarnings("javadoc")
    public String getCharset() {
        return charset;
    }

    @SuppressWarnings("javadoc")
    public void setCharset(String charset) {
        this.charset = charset;
    }

    //	private String getString(byte[] bytes) throws UnsupportedEncodingException {
    //		String ret;
    //		if(charset==null) {
    //			char[] cs=new char[bytes.length];
    //			for (int i=0; i<bytes.length; i++) {
    //				cs[i]=(char)bytes[i];				
    //			}
    //			ret= new String(cs);	
    //		}else {
    //			ret= new String(bytes, charset);	
    //		}
    //		System.out.println(ret);
    //		return ret;
    //	}

    private String getString(byte[] bytes, int startIdx, int size) throws UnsupportedEncodingException {
        String ret;
        if (charset == null) {
            char[] cs = new char[size];
            for (int i = startIdx; i < startIdx + size; i++) {
                //				cs[i-startIdx]=(char) (bytes[i]);				
                //				cs[i-startIdx]=(char)(bytes[i] & 0xFF);				
                //				cs[i-startIdx]=(char) (bytes[i] + 128);	
                cs[i - startIdx] = (char)(bytes[i] >= 0 ? bytes[i] : 256 + bytes[i]);
            }
            ret = new String(cs);
        } else {
            ret = new String(bytes, startIdx, size, charset);
        }
        //		System.out.println(ret);
        return ret;
    }

}

// End of MARCREader.java
