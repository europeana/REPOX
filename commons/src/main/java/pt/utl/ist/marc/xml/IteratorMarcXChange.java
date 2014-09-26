/*
 * Created on 2007/07/17
 *
 */
package pt.utl.ist.marc.xml;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import pt.utl.ist.marc.Record;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 */
public class IteratorMarcXChange extends MarcSaxParserClient implements Iterator<Record>, Iterable<Record> {
    /**
     * Logger for this class
     */
    private static final Logger log                               = Logger.getLogger(IteratorMarcXChange.class);

    File                        xmlFile;
    FileInputStream             xmlFis;
    LinkedBlockingQueue<Record> queue                             = new LinkedBlockingQueue<Record>(3);
    Thread                      parserThread                      = null;

    boolean                     iteratorWasInterruptedInTheMiddle = false;

    /**
     * Creates a new instance of this class.
     * @param xmlFile
     * @throws FileNotFoundException
     * @throws SAXException
     */
    public IteratorMarcXChange(File xmlFile) throws FileNotFoundException, SAXException {
        this(xmlFile, false);
    }

    /**
     * Creates a new instance of this class.
     * @param xmlFile
     * @param useFlorenceParser
     * @throws FileNotFoundException
     * @throws SAXException
     */
    public IteratorMarcXChange(File xmlFile, boolean useFlorenceParser) throws FileNotFoundException, SAXException {
        this.xmlFile = xmlFile;
        xmlFis = new FileInputStream(xmlFile);
        parserThread = new Thread(new SaxParserRunner(xmlFis, this, useFlorenceParser));
        parserThread.start();
    }

    @Override
    public boolean hasNext() {
        log.debug("hasnext");
        while (queue.isEmpty() && !parseFinished) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        if (!queue.isEmpty())
            return true;
        else {
            if (parserThread != null) parserThread.interrupt();
            parserThread = null;
            return false;
        }
    }

    @Override
    public synchronized void signalParseFinished() {
        log.debug("signalParseFinished");
        parseFinished = true;
    }

    @Override
    public Record next() {
        log.debug("next");
        try {
            //			if(queue.poll()!=null)
            return queue.take();
            //			return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Iterator<Record> iterator() {
        return this;
    }

    @Override
    protected void processRecord(Record rec) throws InterruptedException {
        if (!iteratorWasInterruptedInTheMiddle) if (!queue.offer(rec, 60 * 30, TimeUnit.SECONDS)) {
            log.debug("interrupted");
            iteratorWasInterruptedInTheMiddle = true;
        }
    }

    /**
     * 
     */
    public void close() {
        iteratorWasInterruptedInTheMiddle = true;
    }

    class SaxParserRunner implements Runnable {
        FileInputStream     fis;
        MarcSaxParserClient client;
        boolean             useFlorenceParser;

        Exception           exception;

        public SaxParserRunner(FileInputStream fis, MarcSaxParserClient client, boolean useFlorenceParser) {
            super();
            this.fis = fis;
            this.client = client;
            this.useFlorenceParser = useFlorenceParser;
        }

        @Override
        public void run() {
            try {
                if (useFlorenceParser)
                    MarcSaxParserFlorence.parse(fis, client);
                else
                    MarcSaxParser.parse(fis, client);
            } catch (SAXException e) {
                this.exception = e;
            }
        }
    }

}
