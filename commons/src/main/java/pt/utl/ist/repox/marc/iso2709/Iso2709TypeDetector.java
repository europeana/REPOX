/*
 * Created on 2006/08/24
 *
 */
package pt.utl.ist.repox.marc.iso2709;

import org.apache.log4j.Logger;

import pt.utl.ist.repox.marc.iso2709.BatchInfo.CharSet;
import pt.utl.ist.repox.marc.iso2709.BatchInfo.Sgb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 */
public class Iso2709TypeDetector {
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(Iso2709TypeDetector.class);

    /**
     * @param isoFilePath
     * @return BatchInfo
     * @throws IOException
     */
    public static BatchInfo detect(String isoFilePath) throws IOException {
        return detect(new File(isoFilePath));
    }

    /**
     * @param isoFile
     * @return BatchInfo
     * @throws IOException
     */
    public static BatchInfo detect(File isoFile) throws IOException {
        BatchInfo i = new BatchInfo();

        i.setCharSet(detectCharSet(isoFile));//detect chars

        //detect separators
        //detect line breaks
        int c;
        int standardClues = 0;
        int pb4Clues = 0;
        int lineBreaks = 0;
        int spacesInLeader = 0;
        FileInputStream reader = new FileInputStream(isoFile);
        c = reader.read();
        int cnt = 0;
        for (; c != -1 && cnt < 20000; cnt++) {

            if (c == 0x1D || c == 0x1E || c == 0x1F)
                standardClues++;
            else if (c == 0x23 || c == 0x5E) pb4Clues++;

            if (c == 0x0A) lineBreaks++;

            if (cnt < 25 && c == 0x20) spacesInLeader++;

            c = (char)reader.read();
        }
        reader.close();
        i.setStandardSeparators(pb4Clues < standardClues);
        i.setLineBreaks(false);
        if (cnt > 500) {//if it read at least this number of chars
            i.setLineBreaks(lineBreaks > 2);
        } else {
            i.setLineBreaks(lineBreaks > 0);
        }

        //detect application
        if (i.getCharSet() == CharSet.CP850 && i.isLineBreaks())
            i.setSgb(Sgb.PB4);
        else if (i.getCharSet() == CharSet.ISO8859_1 && !i.isLineBreaks() && i.isStandardSeparators() && spacesInLeader == 0) {
            i.setSgb(Sgb.ALEPH);
        } else
            i.setSgb(Sgb.OTHER);

        log.info(i);
        return i;
    }

    /**
     * @param isoFile
     * @return BatchInfo
     * @throws IOException
     */
    public static BatchInfo.CharSet detectCharSet(File isoFile) throws IOException {
        int c;
        int nextC;

        int pb4Clues = 0;
        int horizonClues = 0;
        int iso8859Clues = 0;
        int anselClues = 0;

        FileInputStream reader = new FileInputStream(isoFile);
        nextC = (char)reader.read();
        int cnt = 0;
        for (; nextC != -1 && cnt < 20000; cnt++) {
            c = nextC;
            nextC = reader.read();

            if (c == 0x82 || c == 0x87 || c == 0x84 || c == 0xA1 || c == 0xA2 || c == 0x88 || c == 0x94 || c == 0xA3 || c == 0xC6 || c == 0xA0) {
                pb4Clues++;
            } else if (((c == 0xC2 || c == 0xC3 || c == 0xC4) && (nextC == 0x62 || nextC == 0x65 || nextC == 0x69 || nextC == 0x6F || nextC == 0x75)) || (c == 0xD0 && (nextC == 0x63 || nextC == 0x43))) {
                horizonClues++;
            } else if (((c == 0xE2 || c == 0xE1 || c == 0xE4) && (nextC == 'a' || nextC == 'e' || nextC == 'i' || nextC == 'o' || nextC == 'u' || nextC == 'A' || nextC == 'E' || nextC == 'I' || nextC == 'O' || nextC == 'U')) || (c == 0xF0 && (nextC == 'c' || nextC == 'C'))) {
                anselClues++;
            } else if (c == 0xE7 || c == 0xF5 || c == 0xE3 || c == 0xE1 || c == 0xE9 || c == 0xEd || c == 0xF3 || c == 0xFA || c == 0xEA || c == 0xEE) {
                iso8859Clues++;
            }
        }
        reader.close();

        if (log.isDebugEnabled()) {
            log.debug("PB4 clues - " + pb4Clues);
            log.debug("Horizon clues - " + horizonClues);
            log.debug("ISO8859-1 clues - " + iso8859Clues);
            log.debug("ANSEL clues - " + anselClues);
        }

        if (cnt > 500) {//if it read at least this number of chars
            if (pb4Clues > horizonClues && pb4Clues > 2 && iso8859Clues < pb4Clues * 2) return CharSet.CP850;
            if (horizonClues > 2 && iso8859Clues < horizonClues * 2) return CharSet.HORIZON;
            if (anselClues > 2 && iso8859Clues < anselClues * 2) return CharSet.ANSEL;
            return CharSet.ISO8859_1;
        } else {
            if (pb4Clues > horizonClues && pb4Clues > 1) return CharSet.CP850;
            if (horizonClues > 1) return CharSet.HORIZON;
            return CharSet.ISO8859_1;
        }

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\pb4.ISO")));
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\pb4-2.ISO")));
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\PB4-semQLinhas.iso")));
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\cp850.iso")));
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\horizon.ISO")));
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\latin1.iso")));
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\latin1-2.iso")));
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\Aleph.iso")));
            System.out.println(detect(new File("C:\\Desktop\\1EclipsePrj\\nmafCore\\testISOS\\ANACOM.iso")));

        } else {

            System.out.println(detect(new File(args[0])));
        }
    }
}
