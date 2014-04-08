/*
 * HtmlEncoder.java
 *
 * Created on 12 de Abril de 2004, 16:06
 */

package pt.utl.ist.characters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author  Nuno Freire
 */

public class HtmlEncoder {
    /**
     * Encodes 's' by converting each character to its equivalent HTML entity,
     * where one exists.
     * @param s the string to convert.
     * @return a string with entities encoded.
     */
    public static String encode(String s)
    {
        StringBuffer sb = new StringBuffer();
        char[] chars = s.toCharArray();
        for (char aChar : chars) {
            sb.append(getEntity(aChar));
        }
        return sb.toString();
    }

    /**
     * Encodes 's' by converting each character to its equivalent numeric
     * entity, where one exists.
     * @param s the string to convert.
     * @return a string with entities encoded.
     */
    public static String encodeNumeric(String s)
    {
        StringBuffer sb = new StringBuffer();
        char[] chars = s.toCharArray();
        for (char aChar : chars) {
            sb.append(getNumericEntity(aChar));
        }
        return sb.toString();
    }

    /**
     * Converts a character into its entity equivalent. If no entity
     * corresponding to 'c' exist, this method returns a string containing 'c'.
     * This means that 'getEntity()' is always guaranteed to return
     * a valid string representing 'c'.
     * @param c the character to convert.
     * @return a string that contains either an entity representing 'c'
     * or 'c' itself.
     */
    public static String getEntity(char c)
    {
        switch (c) {
            //no-break space = non-breaking space.
            case '\u00A0': return "&nbsp;";
            //inverted exclamation mark.
            case '\u00A1': return "&iexcl;";
            //cent sign.
            case '\u00A2': return "&cent;";
            //pound sign.
            case '\u00A3': return "&pound;";
            //currency sign.
            case '\u00A4': return "&curren;";
            //yen sign = yuan sign.
            case '\u00A5': return "&yen;";
            //broken bar = broken vertical bar.
            case '\u00A6': return "&brvbar;";
            //section sign.
            case '\u00A7': return "&sect;";
            //diaeresis = spacing diaeresis.
            case '\u00A8': return "&uml;";
            //copyright sign.
            case '\u00A9': return "&copy;";
            //feminine ordinal indicator.
            case '\u00AA': return "&ordf;";
            //left-pointing double angle quotation mark
            // = left pointing guillemet.
            case '\u00AB': return "&laquo;";
            //not sign.
            case '\u00AC': return "&not;";
            //soft hyphen = discretionary hyphen.
            case '\u00AD': return "&shy;";
            //registered sign = registered trade mark sign.
            case '\u00AE': return "&reg;";
            //macron = spacing macron = overline = APL overbar.
            case '\u00AF': return "&macr;";
            //degree sign.
            case '\u00B0': return "&deg;";
            //plus-minus sign = plus-or-minus sign.
            case '\u00B1': return "&plusmn;";
            //superscript two = superscript digit two = squared.
            case '\u00B2': return "&sup2;";
            //superscript three = superscript digit three = cubed.
            case '\u00B3': return "&sup3;";
            //acute accent = spacing acute.
            case '\u00B4': return "&acute;";
            //micro sign.
            case '\u00B5': return "&micro;";
            //pilcrow sign = paragraph sign.
            case '\u00B6': return "&para;";
            //middle dot = Georgian comma = Greek middle dot.
            case '\u00B7': return "&middot;";
            //cedilla = spacing cedilla.
            case '\u00B8': return "&cedil;";
            //superscript one = superscript digit one.
            case '\u00B9': return "&sup1;";
            //masculine ordinal indicator.
            case '\u00BA': return "&ordm;";
            //right-pointing double angle quotation mark
            // = right pointing guillemet.
            case '\u00BB': return "&raquo;";
            //vulgar fraction one quarter = fraction one quarter.
            case '\u00BC': return "&frac14;";
            //vulgar fraction one half = fraction one half.
            case '\u00BD': return "&frac12;";
            //vulgar fraction three quarters = fraction three quarters.
            case '\u00BE': return "&frac34;";
            //inverted question mark = turned question mark.
            case '\u00BF': return "&iquest;";
            //latin capital letter A with grave = latin capital letter A grave.
            case '\u00C0': return "&Agrave;";
            //latin capital letter A with acute.
            case '\u00C1': return "&Aacute;";
            //latin capital letter A with circumflex.
            case '\u00C2': return "&Acirc;";
            //latin capital letter A with tilde.
            case '\u00C3': return "&Atilde;";
            //latin capital letter A with diaeresis.
            case '\u00C4': return "&Auml;";
            //latin capital letter A with ring above
            // = latin capital letter A ring.
            case '\u00C5': return "&Aring;";
            //latin capital letter AE = latin capital ligature AE.
            case '\u00C6': return "&AElig;";
            //latin capital letter C with cedilla.
            case '\u00C7': return "&Ccedil;";
            //latin capital letter E with grave.
            case '\u00C8': return "&Egrave;";
            //latin capital letter E with acute.
            case '\u00C9': return "&Eacute;";
            //latin capital letter E with circumflex.
            case '\u00CA': return "&Ecirc;";
            //latin capital letter E with diaeresis.
            case '\u00CB': return "&Euml;";
            //latin capital letter I with grave.
            case '\u00CC': return "&Igrave;";
            //latin capital letter I with acute.
            case '\u00CD': return "&Iacute;";
            //latin capital letter I with circumflex.
            case '\u00CE': return "&Icirc;";
            //latin capital letter I with diaeresis.
            case '\u00CF': return "&Iuml;";
            //latin capital letter ETH.
            case '\u00D0': return "&ETH;";
            //latin capital letter N with tilde.
            case '\u00D1': return "&Ntilde;";
            //latin capital letter O with grave.
            case '\u00D2': return "&Ograve;";
            //latin capital letter O with acute.
            case '\u00D3': return "&Oacute;";
            //latin capital letter O with circumflex.
            case '\u00D4': return "&Ocirc;";
            //latin capital letter O with tilde.
            case '\u00D5': return "&Otilde;";
            //latin capital letter O with diaeresis.
            case '\u00D6': return "&Ouml;";
            //multiplication sign.
            case '\u00D7': return "&times;";
            //latin capital letter O with stroke = latin capital letter O slash.
            case '\u00D8': return "&Oslash;";
            //latin capital letter U with grave.
            case '\u00D9': return "&Ugrave;";
            //latin capital letter U with acute.
            case '\u00DA': return "&Uacute;";
            //latin capital letter U with circumflex.
            case '\u00DB': return "&Ucirc;";
            //latin capital letter U with diaeresis.
            case '\u00DC': return "&Uuml;";
            //latin capital letter Y with acute.
            case '\u00DD': return "&Yacute;";
            //latin capital letter THORN.
            case '\u00DE': return "&THORN;";
            //latin small letter sharp s = ess-zed.
            case '\u00DF': return "&szlig;";
            //latin small letter a with grave = latin small letter a grave.
            case '\u00E0': return "&agrave;";
            //latin small letter a with acute.
            case '\u00E1': return "&aacute;";
            //latin small letter a with circumflex.
            case '\u00E2': return "&acirc;";
            //latin small letter a with tilde.
            case '\u00E3': return "&atilde;";
            //latin small letter a with diaeresis.
            case '\u00E4': return "&auml;";
            //latin small letter a with ring above = latin small letter a ring.
            case '\u00E5': return "&aring;";
            //latin small letter ae = latin small ligature ae.
            case '\u00E6': return "&aelig;";
            //latin small letter c with cedilla.
            case '\u00E7': return "&ccedil;";
            //latin small letter e with grave.
            case '\u00E8': return "&egrave;";
            //latin small letter e with acute.
            case '\u00E9': return "&eacute;";
            //latin small letter e with circumflex.
            case '\u00EA': return "&ecirc;";
            //latin small letter e with diaeresis.
            case '\u00EB': return "&euml;";
            //latin small letter i with grave.
            case '\u00EC': return "&igrave;";
            //latin small letter i with acute.
            case '\u00ED': return "&iacute;";
            //latin small letter i with circumflex.
            case '\u00EE': return "&icirc;";
            //latin small letter i with diaeresis.
            case '\u00EF': return "&iuml;";
            //latin small letter eth.
            case '\u00F0': return "&eth;";
            //latin small letter n with tilde.
            case '\u00F1': return "&ntilde;";
            //latin small letter o with grave.
            case '\u00F2': return "&ograve;";
            //latin small letter o with acute.
            case '\u00F3': return "&oacute;";
            //latin small letter o with circumflex.
            case '\u00F4': return "&ocirc;";
            //latin small letter o with tilde.
            case '\u00F5': return "&otilde;";
            //latin small letter o with diaeresis.
            case '\u00F6': return "&ouml;";
            //division sign.
            case '\u00F7': return "&divide;";
            //latin small letter o with stroke, = latin small letter o slash.
            case '\u00F8': return "&oslash;";
            //latin small letter u with grave.
            case '\u00F9': return "&ugrave;";
            //latin small letter u with acute.
            case '\u00FA': return "&uacute;";
            //latin small letter u with circumflex.
            case '\u00FB': return "&ucirc;";
            //latin small letter u with diaeresis.
            case '\u00FC': return "&uuml;";
            //latin small letter y with acute.
            case '\u00FD': return "&yacute;";
            //latin small letter thorn.
            case '\u00FE': return "&thorn;";
            //latin small letter y with diaeresis.
            case '\u00FF': return "&yuml;";
            //latin small f with hook = function = florin.
            case '\u0192': return "&fnof;";
            //greek capital letter alpha.
            case '\u0391': return "&Alpha;";
            //greek capital letter beta.
            case '\u0392': return "&Beta;";
            //greek capital letter gamma.
            case '\u0393': return "&Gamma;";
            //greek capital letter delta.
            case '\u0394': return "&Delta;";
            //greek capital letter epsilon.
            case '\u0395': return "&Epsilon;";
            //greek capital letter zeta.
            case '\u0396': return "&Zeta;";
            //greek capital letter eta.
            case '\u0397': return "&Eta;";
            //greek capital letter theta.
            case '\u0398': return "&Theta;";
            //greek capital letter iota.
            case '\u0399': return "&Iota;";
            //greek capital letter kappa.
            case '\u039A': return "&Kappa;";
            //greek capital letter lambda.
            case '\u039B': return "&Lambda;";
            //greek capital letter mu.
            case '\u039C': return "&Mu;";
            //greek capital letter nu.
            case '\u039D': return "&Nu;";
            //greek capital letter xi.
            case '\u039E': return "&Xi;";
            //greek capital letter omicron.
            case '\u039F': return "&Omicron;";
            //greek capital letter pi.
            case '\u03A0': return "&Pi;";
            //greek capital letter rho.
            case '\u03A1': return "&Rho;";
            //greek capital letter sigma.
            case '\u03A3': return "&Sigma;";
            //greek capital letter tau.
            case '\u03A4': return "&Tau;";
            //greek capital letter upsilon.
            case '\u03A5': return "&Upsilon;";
            //greek capital letter phi.
            case '\u03A6': return "&Phi;";
            //greek capital letter chi.
            case '\u03A7': return "&Chi;";
            //greek capital letter psi.
            case '\u03A8': return "&Psi;";
            //greek capital letter omega.
            case '\u03A9': return "&Omega;";
            //greek small letter alpha.
            case '\u03B1': return "&alpha;";
            //greek small letter beta.
            case '\u03B2': return "&beta;";
            //greek small letter gamma.
            case '\u03B3': return "&gamma;";
            //greek small letter delta.
            case '\u03B4': return "&delta;";
            //greek small letter epsilon.
            case '\u03B5': return "&epsilon;";
            //greek small letter zeta.
            case '\u03B6': return "&zeta;";
            //greek small letter eta.
            case '\u03B7': return "&eta;";
            //greek small letter theta.
            case '\u03B8': return "&theta;";
            //greek small letter iota.
            case '\u03B9': return "&iota;";
            //greek small letter kappa.
            case '\u03BA': return "&kappa;";
            //greek small letter lambda.
            case '\u03BB': return "&lambda;";
            //greek small letter mu.
            case '\u03BC': return "&mu;";
            //greek small letter nu.
            case '\u03BD': return "&nu;";
            //greek small letter xi.
            case '\u03BE': return "&xi;";
            //greek small letter omicron.
            case '\u03BF': return "&omicron;";
            //greek small letter pi.
            case '\u03C0': return "&pi;";
            //greek small letter rho.
            case '\u03C1': return "&rho;";
            //greek small letter final sigma.
            case '\u03C2': return "&sigmaf;";
            //greek small letter sigma.
            case '\u03C3': return "&sigma;";
            //greek small letter tau.
            case '\u03C4': return "&tau;";
            //greek small letter upsilon.
            case '\u03C5': return "&upsilon;";
            //greek small letter phi.
            case '\u03C6': return "&phi;";
            //greek small letter chi.
            case '\u03C7': return "&chi;";
            //greek small letter psi.
            case '\u03C8': return "&psi;";
            //greek small letter omega.
            case '\u03C9': return "&omega;";
            //greek small letter theta symbol.
            case '\u03D1': return "&thetasym;";
            //greek upsilon with hook symbol.
            case '\u03D2': return "&upsih;";
            //greek pi symbol.
            case '\u03D6': return "&piv;";
            //bullet = black small circle.
            case '\u2219': return "&bull;";
            //horizontal ellipsis = three dot leader.
            case '\u2026': return "&hellip;";
            //prime = minutes = feet.
            case '\u2032': return "&prime;";
            //double prime = seconds = inches.
            case '\u2033': return "&Prime;";
            //overline = spacing overscore.
            case '\u203E': return "&oline;";
            //fraction slash.
            case '\u2044': return "&frasl;";
            //script capital P = power set = Weierstrass p.
            case '\u2118': return "&weierp;";
            //blackletter capital I = imaginary part.
            case '\u2111': return "&image;";
            //blackletter capital R = real part symbol.
            case '\u211C': return "&real;";
            //trade mark sign.
            case '\u2122': return "&trade;";
            //alef symbol = first transfinite cardinal.
            case '\u2135': return "&alefsym;";
            //leftwards arrow.
            case '\u2190': return "&larr;";
            //upwards arrow.
            case '\u2191': return "&uarr;";
            //rightwards arrow.
            case '\u2192': return "&rarr;";
            //downwards arrow.
            case '\u2193': return "&darr;";
            //left right arrow.
            case '\u2194': return "&harr;";
            //downwards arrow with corner leftwards  = carriage return.
            case '\u21B5': return "&crarr;";
            //leftwards double arrow.
            case '\u21D0': return "&lArr;";
            //upwards double arrow.
            case '\u21D1': return "&uArr;";
            //rightwards double arrow.
            case '\u21D2': return "&rArr;";
            //downwards double arrow.
            case '\u21D3': return "&dArr;";
            //left right double arrow.
            case '\u21D4': return "&hArr;";
            //for all.
            case '\u2200': return "&forall;";
            //partial differential.
            case '\u2202': return "&part;";
            //there exists.
            case '\u2203': return "&exist;";
            //empty set = null set = diameter.
            case '\u2205': return "&empty;";
            //nabla = backward difference.
            case '\u2207': return "&nabla;";
            //element of.
            case '\u2208': return "&isin;";
            //not an element of.
            case '\u2209': return "&notin;";
            //contains as member.
            case '\u220B': return "&ni;";
            //n-ary product = product sign.
            case '\u220F': return "&prod;";
            //n-ary sumation.
            case '\u2211': return "&sum;";
            //minus sign.
            case '\u2212': return "&minus;";
            //asterisk operator.
            case '\u2217': return "&lowast;";
            //square root = radical sign.
            case '\u221A': return "&radic;";
            //proportional to.
            case '\u221D': return "&prop;";
            //infinity.
            case '\u221E': return "&infin;";
            //angle.
            case '\u2220': return "&ang;";
            //logical and = wedge.
            case '\u2227': return "&and;";
            //logical or = vee.
            case '\u2228': return "&or;";
            //intersection = cap.
            case '\u2229': return "&cap;";
            //union = cup.
            case '\u222A': return "&cup;";
            //integral.
            case '\u222B': return "&int;";
            //therefore.
            case '\u2234': return "&there4;";
            //tilde operator = varies with = similar to.
            case '\u223C': return "&sim;";
            //approximately equal to.
            case '\u2245': return "&cong;";
            //almost equal to = asymptotic to.
            case '\u2248': return "&asymp;";
            //not equal to.
            case '\u2260': return "&ne;";
            //identical to.
            case '\u2261': return "&equiv;";
            //less-than or equal to.
            case '\u2264': return "&le;";
            //greater-than or equal to.
            case '\u2265': return "&ge;";
            //subset of.
            case '\u2282': return "&sub;";
            //superset of.
            case '\u2283': return "&sup;";
            //not a subset of.
            case '\u2284': return "&nsub;";
            //subset of or equal to.
            case '\u2286': return "&sube;";
            //superset of or equal to.
            case '\u2287': return "&supe;";
            //circled plus = direct sum.
            case '\u2295': return "&oplus;";
            //circled times = vector product.
            case '\u2297': return "&otimes;";
            //up tack = orthogonal to = perpendicular.
            case '\u22A5': return "&perp;";
            //dot operator.
            case '\u22C5': return "&sdot;";
            //left ceiling = apl upstile.
            case '\u2308': return "&lceil;";
            //right ceiling.
            case '\u2309': return "&rceil;";
            //left floor = apl downstile.
            case '\u230A': return "&lfloor;";
            //right floor.
            case '\u230B': return "&rfloor;";
            //left-pointing angle bracket = bra.
            case '\u2329': return "&lang;";
            //right-pointing angle bracket = ket.
            case '\u232A': return "&rang;";
            //lozenge.
            case '\u25CA': return "&loz;";
            //black spade suit.
            case '\u2660': return "&spades;";
            //black spade suit.
            case '\u2663': return "&clubs;";
            //black heart suit = valentine.
            case '\u2665': return "&hearts;";
            //black diamond suit.
            case '\u2666': return "&diams;";
            //quotation mark = APL quote.
            case '\u0022': return "&quot;";
            //ampersand.
            case '\u0026': return "&amp;";
            //less-than sign.
            case '\u003C': return "&lt;";
            //greater-than sign.
            case '\u003E': return "&gt;";
            //latin capital ligature OE.
            case '\u0152': return "&OElig;";
            //latin small ligature oe.
            case '\u0153': return "&oelig;";
            //latin capital letter S with caron.
            case '\u0160': return "&Scaron;";
            //latin small letter s with caron.
            case '\u0161': return "&scaron;";
            //latin capital letter Y with diaeresis.
            case '\u0178': return "&Yuml;";
            //modifier letter circumflex accent.
            case '\u02C6': return "&circ;";
            //small tilde.
            case '\u02DC': return "&tilde;";
            //en space.
            case '\u2002': return "&ensp;";
            //em space.
            case '\u2003': return "&emsp;";
            //thin space.
            case '\u2009': return "&thinsp;";
            //zero width non-joiner.
            case '\u200C': return "&zwnj;";
            //zero width joiner.
            case '\u200D': return "&zwj;";
            //left-to-right mark.
            case '\u200E': return "&lrm;";
            //right-to-left mark.
            case '\u200F': return "&rlm;";
            //en dash.
            case '\u2013': return "&ndash;";
            //em dash.
            case '\u2014': return "&mdash;";
            //left single quotation mark.
            case '\u2018': return "&lsquo;";
            //right single quotation mark.
            case '\u2019': return "&rsquo;";
            //single low-9 quotation mark.
            case '\u201A': return "&sbquo;";
            //left double quotation mark.
            case '\u201C': return "&ldquo;";
            //right double quotation mark.
            case '\u201D': return "&rdquo;";
            //double low-9 quotation mark.
            case '\u201E': return "&bdquo;";
            //dagger.
            case '\u2020': return "&dagger;";
            //double dagger.
            case '\u2021': return "&Dagger;";
            //per mille sign.
            case '\u2030': return "&permil;";
            //single left-pointing angle quotation mark.
            case '\u2039': return "&lsaquo;";
            //single right-pointing angle quotation mark.
            case '\u203A': return "&rsaquo;";
            //euro sign.
            case '\u20AC': return "&euro;";

            // all other characters remain the same.
            default: return String.valueOf(c);
        }
    }
    
    /**
     * Converts a character into its numeric entity equivalent. If no entity
     * corresponding to 'c' exist, this method returns a string containing 'c'.
     * This means that 'getNumericEntity()' is always guaranteed to return
     * a valid string representing 'c'.
     * @param c the character to convert.
     * @return a string that contains either a numeric entity representing 'c'
     * or 'c' itself.
     */
    public static String getNumericEntity(char c)
    {
        switch(c) {
            //no-break space = non-breaking space.
            case '\u00A0': return "&#160;";
            //inverted exclamation mark.
            case '\u00A1': return "&#161;";
            //cent sign.
            case '\u00A2': return "&#162;";
            //pound sign.
            case '\u00A3': return "&#163;";
            //currency sign.
            case '\u00A4': return "&#164;";
            //yen sign = yuan sign.
            case '\u00A5': return "&#165;";
            //broken bar = broken vertical bar.
            case '\u00A6': return "&#166;";
            //section sign.
            case '\u00A7': return "&#167;";
            //diaeresis = spacing diaeresis.
            case '\u00A8': return "&#168;";
            //copyright sign.
            case '\u00A9': return "&#169;";
            //feminine ordinal indicator.
            case '\u00AA': return "&#170;";
            //left-pointing double angle quotation mark
            // = left pointing guillemet.
            case '\u00AB': return "&#171;";
            //not sign.
            case '\u00AC': return "&#172;";
            //soft hyphen = discretionary hyphen.
            case '\u00AD': return "&#173;";
            //registered sign = registered trade mark sign.
            case '\u00AE': return "&#174;";
            //macron = spacing macron = overline = APL overbar.
            case '\u00AF': return "&#175;";
            //degree sign.
            case '\u00B0': return "&#176;";
            //plus-minus sign = plus-or-minus sign.
            case '\u00B1': return "&#177;";
            //superscript two = superscript digit two = squared.
            case '\u00B2': return "&#178;";
            //superscript three = superscript digit three = cubed.
            case '\u00B3': return "&#179;";
            //acute accent = spacing acute.
            case '\u00B4': return "&#180;";
            //micro sign.
            case '\u00B5': return "&#181;";
            //pilcrow sign = paragraph sign.
            case '\u00B6': return "&#182;";
            //middle dot = Georgian comma = Greek middle dot.
            case '\u00B7': return "&#183;";
            //cedilla = spacing cedilla.
            case '\u00B8': return "&#184;";
            //superscript one = superscript digit one.
            case '\u00B9': return "&#185;";
            //masculine ordinal indicator.
            case '\u00BA': return "&#186;";
            //right-pointing double angle quotation mark
            // = right pointing guillemet.
            case '\u00BB': return "&#187;";
            //vulgar fraction one quarter = fraction one quarter.
            case '\u00BC': return "&#188;";
            //vulgar fraction one half = fraction one half.
            case '\u00BD': return "&#189;";
            //vulgar fraction three quarters = fraction three quarters.
            case '\u00BE': return "&#190;";
            //inverted question mark = turned question mark.
            case '\u00BF': return "&#191;";
            //latin capital letter A with grave = latin capital letter A grave.
            case '\u00C0': return "&#192;";
            //latin capital letter A with acute.
            case '\u00C1': return "&#193;";
            //latin capital letter A with circumflex.
            case '\u00C2': return "&#194;";
            //latin capital letter A with tilde.
            case '\u00C3': return "&#195;";
            //latin capital letter A with diaeresis.
            case '\u00C4': return "&#196;";
            //latin capital letter A with ring above
            // = latin capital letter A ring.
            case '\u00C5': return "&#197;";
            //latin capital letter AE = latin capital ligature AE.
            case '\u00C6': return "&#198;";
            //latin capital letter C with cedilla.
            case '\u00C7': return "&#199;";
            //latin capital letter E with grave.
            case '\u00C8': return "&#200;";
            //latin capital letter E with acute.
            case '\u00C9': return "&#201;";
            //latin capital letter E with circumflex.
            case '\u00CA': return "&#202;";
            //latin capital letter E with diaeresis.
            case '\u00CB': return "&#203;";
            //latin capital letter I with grave.
            case '\u00CC': return "&#204;";
            //latin capital letter I with acute.
            case '\u00CD': return "&#205;";
            //latin capital letter I with circumflex.
            case '\u00CE': return "&#206;";
            //latin capital letter I with diaeresis.
            case '\u00CF': return "&#207;";
            //latin capital letter ETH.
            case '\u00D0': return "&#208;";
            //latin capital letter N with tilde.
            case '\u00D1': return "&#209;";
            //latin capital letter O with grave.
            case '\u00D2': return "&#210;";
            //latin capital letter O with acute.
            case '\u00D3': return "&#211;";
            //latin capital letter O with circumflex.
            case '\u00D4': return "&#212;";
            //latin capital letter O with tilde.
            case '\u00D5': return "&#213;";
            //latin capital letter O with diaeresis.
            case '\u00D6': return "&#214;";
            //multiplication sign.
            case '\u00D7': return "&#215;";
            //latin capital letter O with stroke = latin capital letter O slash.
            case '\u00D8': return "&#216;";
            //latin capital letter U with grave.
            case '\u00D9': return "&#217;";
            //latin capital letter U with acute.
            case '\u00DA': return "&#218;";
            //latin capital letter U with circumflex.
            case '\u00DB': return "&#219;";
            //latin capital letter U with diaeresis.
            case '\u00DC': return "&#220;";
            //latin capital letter Y with acute.
            case '\u00DD': return "&#221;";
            //latin capital letter THORN.
            case '\u00DE': return "&#222;";
            //latin small letter sharp s = ess-zed.
            case '\u00DF': return "&#223;";
            //latin small letter a with grave = latin small letter a grave.
            case '\u00E0': return "&#224;";
            //latin small letter a with acute.
            case '\u00E1': return "&#225;";
            //latin small letter a with circumflex.
            case '\u00E2': return "&#226;";
            //latin small letter a with tilde.
            case '\u00E3': return "&#227;";
            //latin small letter a with diaeresis.
            case '\u00E4': return "&#228;";
            //latin small letter a with ring above = latin small letter a ring.
            case '\u00E5': return "&#229;";
            //latin small letter ae = latin small ligature ae.
            case '\u00E6': return "&#230;";
            //latin small letter c with cedilla.
            case '\u00E7': return "&#231;";
            //latin small letter e with grave.
            case '\u00E8': return "&#232;";
            //latin small letter e with acute.
            case '\u00E9': return "&#233;";
            //latin small letter e with circumflex.
            case '\u00EA': return "&#234;";
            //latin small letter e with diaeresis.
            case '\u00EB': return "&#235;";
            //latin small letter i with grave.
            case '\u00EC': return "&#236;";
            //latin small letter i with acute.
            case '\u00ED': return "&#237;";
            //latin small letter i with circumflex.
            case '\u00EE': return "&#238;";
            //latin small letter i with diaeresis.
            case '\u00EF': return "&#239;";
            //latin small letter eth.
            case '\u00F0': return "&#240;";
            //latin small letter n with tilde.
            case '\u00F1': return "&#241;";
            //latin small letter o with grave.
            case '\u00F2': return "&#242;";
            //latin small letter o with acute.
            case '\u00F3': return "&#243;";
            //latin small letter o with circumflex.
            case '\u00F4': return "&#244;";
            //latin small letter o with tilde.
            case '\u00F5': return "&#245;";
            //latin small letter o with diaeresis.
            case '\u00F6': return "&#246;";
            //division sign.
            case '\u00F7': return "&#247;";
            //latin small letter o with stroke, = latin small letter o slash.
            case '\u00F8': return "&#248;";
            //latin small letter u with grave.
            case '\u00F9': return "&#249;";
            //latin small letter u with acute.
            case '\u00FA': return "&#250;";
            //latin small letter u with circumflex.
            case '\u00FB': return "&#251;";
            //latin small letter u with diaeresis.
            case '\u00FC': return "&#252;";
            //latin small letter y with acute.
            case '\u00FD': return "&#253;";
            //latin small letter thorn.
            case '\u00FE': return "&#254;";
            //latin small letter y with diaeresis.
            case '\u00FF': return "&#255;";
            //latin small f with hook = function = florin.
            case '\u0192': return "&#402;";
            //greek capital letter alpha.
            case '\u0391': return "&#913;";
            //greek capital letter beta.
            case '\u0392': return "&#914;";
            //greek capital letter gamma.
            case '\u0393': return "&#915;";
            //greek capital letter delta.
            case '\u0394': return "&#916;";
            //greek capital letter epsilon.
            case '\u0395': return "&#917;";
            //greek capital letter zeta.
            case '\u0396': return "&#918;";
            //greek capital letter eta.
            case '\u0397': return "&#919;";
            //greek capital letter theta.
            case '\u0398': return "&#920;";
            //greek capital letter iota.
            case '\u0399': return "&#921;";
            //greek capital letter kappa.
            case '\u039A': return "&#922;";
            //greek capital letter lambda.
            case '\u039B': return "&#923;";
            //greek capital letter mu.
            case '\u039C': return "&#924;";
            //greek capital letter nu.
            case '\u039D': return "&#925;";
            //greek capital letter xi.
            case '\u039E': return "&#926;";
            //greek capital letter omicron.
            case '\u039F': return "&#927;";
            //greek capital letter pi.
            case '\u03A0': return "&#928;";
            //greek capital letter rho.
            case '\u03A1': return "&#929;";
            //greek capital letter sigma.
            case '\u03A3': return "&#931;";
            //greek capital letter tau.
            case '\u03A4': return "&#932;";
            //greek capital letter upsilon.
            case '\u03A5': return "&#933;";
            //greek capital letter phi.
            case '\u03A6': return "&#934;";
            //greek capital letter chi.
            case '\u03A7': return "&#935;";
            //greek capital letter psi.
            case '\u03A8': return "&#936;";
            //greek capital letter omega.
            case '\u03A9': return "&#937;";
            //greek small letter alpha.
            case '\u03B1': return "&#945;";
            //greek small letter beta.
            case '\u03B2': return "&#946;";
            //greek small letter gamma.
            case '\u03B3': return "&#947;";
            //greek small letter delta.
            case '\u03B4': return "&#948;";
            //greek small letter epsilon.
            case '\u03B5': return "&#949;";
            //greek small letter zeta.
            case '\u03B6': return "&#950;";
            //greek small letter eta.
            case '\u03B7': return "&#951;";
            //greek small letter theta.
            case '\u03B8': return "&#952;";
            //greek small letter iota.
            case '\u03B9': return "&#953;";
            //greek small letter kappa.
            case '\u03BA': return "&#954;";
            //greek small letter lambda.
            case '\u03BB': return "&#955;";
            //greek small letter mu.
            case '\u03BC': return "&#956;";
            //greek small letter nu.
            case '\u03BD': return "&#957;";
            //greek small letter xi.
            case '\u03BE': return "&#958;";
            //greek small letter omicron.
            case '\u03BF': return "&#959;";
            //greek small letter pi.
            case '\u03C0': return "&#960;";
            //greek small letter rho.
            case '\u03C1': return "&#961;";
            //greek small letter final sigma.
            case '\u03C2': return "&#962;";
            //greek small letter sigma.
            case '\u03C3': return "&#963;";
            //greek small letter tau.
            case '\u03C4': return "&#964;";
            //greek small letter upsilon.
            case '\u03C5': return "&#965;";
            //greek small letter phi.
            case '\u03C6': return "&#966;";
            //greek small letter chi.
            case '\u03C7': return "&#967;";
            //greek small letter psi.
            case '\u03C8': return "&#968;";
            //greek small letter omega.
            case '\u03C9': return "&#969;";
            //greek small letter theta symbol.
            case '\u03D1': return "&#977;";
            //greek upsilon with hook symbol.
            case '\u03D2': return "&#978;";
            //greek pi symbol.
            case '\u03D6': return "&#982;";
            //bullet = black small circle.
            case '\u2219': return "&#8226;";
            //horizontal ellipsis = three dot leader.
            case '\u2026': return "&#8230;";
            //prime = minutes = feet.
            case '\u2032': return "&#8242;";
            //double prime = seconds = inches.
            case '\u2033': return "&#8243;";
            //overline = spacing overscore.
            case '\u203E': return "&#8254;";
            //fraction slash.
            case '\u2044': return "&#8260;";
            //script capital P = power set = Weierstrass p.
            case '\u2118': return "&#8472;";
            //blackletter capital I = imaginary part.
            case '\u2111': return "&#8465;";
            //blackletter capital R = real part symbol.
            case '\u211C': return "&#8476;";
            //trade mark sign.
            case '\u2122': return "&#8482;";
            //alef symbol = first transfinite cardinal.
            case '\u2135': return "&#8501;";
            //leftwards arrow.
            case '\u2190': return "&#8592;";
            //upwards arrow.
            case '\u2191': return "&#8593;";
            //rightwards arrow.
            case '\u2192': return "&#8594;";
            //downwards arrow.
            case '\u2193': return "&#8595;";
            //left right arrow.
            case '\u2194': return "&#8596;";
            //downwards arrow with corner leftwards  = carriage return.
            case '\u21B5': return "&#8629;";
            //leftwards double arrow.
            case '\u21D0': return "&#8656;";
            //upwards double arrow.
            case '\u21D1': return "&#8657;";
            //rightwards double arrow.
            case '\u21D2': return "&#8658;";
            //downwards double arrow.
            case '\u21D3': return "&#8659;";
            //left right double arrow.
            case '\u21D4': return "&#8660;";
            //for all.
            case '\u2200': return "&#8704;";
            //partial differential.
            case '\u2202': return "&#8706;";
            //there exists.
            case '\u2203': return "&#8707;";
            //empty set = null set = diameter.
            case '\u2205': return "&#8709;";
            //nabla = backward difference.
            case '\u2207': return "&#8711;";
            //element of.
            case '\u2208': return "&#8712;";
            //not an element of.
            case '\u2209': return "&#8713;";
            //contains as member.
            case '\u220B': return "&#8715;";
            //n-ary product = product sign.
            case '\u220F': return "&#8719;";
            //n-ary sumation.
            case '\u2211': return "&#8721;";
            //minus sign.
            case '\u2212': return "&#8722;";
            //asterisk operator.
            case '\u2217': return "&#8727;";
            //square root = radical sign.
            case '\u221A': return "&#8730;";
            //proportional to.
            case '\u221D': return "&#8733;";
            //infinity.
            case '\u221E': return "&#8734;";
            //angle.
            case '\u2220': return "&#8736;";
            //logical and = wedge.
            case '\u2227': return "&#8743;";
            //logical or = vee.
            case '\u2228': return "&#8744;";
            //intersection = cap.
            case '\u2229': return "&#8745;";
            //union = cup.
            case '\u222A': return "&#8746;";
            //integral.
            case '\u222B': return "&#8747;";
            //therefore.
            case '\u2234': return "&#8756;";
            //tilde operator = varies with = similar to.
            case '\u223C': return "&#8764;";
            //approximately equal to.
            case '\u2245': return "&#8773;";
            //almost equal to = asymptotic to.
            case '\u2248': return "&#8776;";
            //not equal to.
            case '\u2260': return "&#8800;";
            //identical to.
            case '\u2261': return "&#8801;";
            //less-than or equal to.
            case '\u2264': return "&#8804;";
            //greater-than or equal to.
            case '\u2265': return "&#8805;";
            //subset of.
            case '\u2282': return "&#8834;";
            //superset of.
            case '\u2283': return "&#8835;";
            //not a subset of.
            case '\u2284': return "&#8836;";
            //subset of or equal to.
            case '\u2286': return "&#8838;";
            //superset of or equal to.
            case '\u2287': return "&#8839;";
            //circled plus = direct sum.
            case '\u2295': return "&#8853;";
            //circled times = vector product.
            case '\u2297': return "&#8855;";
            //up tack = orthogonal to = perpendicular.
            case '\u22A5': return "&#8869;";
            //dot operator.
            case '\u22C5': return "&#8901;";
            //left ceiling = apl upstile.
            case '\u2308': return "&#8968;";
            //right ceiling.
            case '\u2309': return "&#8969;";
            //left floor = apl downstile.
            case '\u230A': return "&#8970;";
            //right floor.
            case '\u230B': return "&#8971;";
            //left-pointing angle bracket = bra.
            case '\u2329': return "&#9001;";
            //right-pointing angle bracket = ket.
            case '\u232A': return "&#9002;";
            //lozenge.
            case '\u25CA': return "&#9674;";
            //black spade suit.
            case '\u2660': return "&#9824;";
            //black club suit = shamrock.
            case '\u2663': return "&#9827;";
            //black heart suit = valentine.
            case '\u2665': return "&#9829;";
            //black diamond suit.
            case '\u2666': return "&#9830;";
            //quotation mark = APL quote.
            case '\u0022': return "&#34;";
            //ampersand.
            case '\u0026': return "&#38;";
            //less-than sign.
            case '\u003C': return "&#60;";
            //greater-than sign.
            case '\u003E': return "&#62;";
            //latin capital ligature OE.
            case '\u0152': return "&#338;";
            //latin small ligature oe.
            case '\u0153': return "&#339;";
            //latin capital letter S with caron.
            case '\u0160': return "&#352;";
            //latin small letter s with caron.
            case '\u0161': return "&#353;";
            //latin capital letter Y with diaeresis.
            case '\u0178': return "&#376;";
            //modifier letter circumflex accent.
            case '\u02C6': return "&#710;";
            //small tilde.
            case '\u02DC': return "&#732;";
            //en space.
            case '\u2002': return "&#8194;";
            //em space.
            case '\u2003': return "&#8195;";
            //thin space.
            case '\u2009': return "&#8201;";
            //zero width non-joiner.
            case '\u200C': return "&#8204;";
            //zero width joiner.
            case '\u200D': return "&#8205;";
            //left-to-right mark.
            case '\u200E': return "&#8206;";
            //right-to-left mark.
            case '\u200F': return "&#8207;";
            //en dash.
            case '\u2013': return "&#8211;";
            //em dash.
            case '\u2014': return "&#8212;";
            //left single quotation mark.
            case '\u2018': return "&#8216;";
            //right single quotation mark.
            case '\u2019': return "&#8217;";
            //single low-9 quotation mark.
            case '\u201A': return "&#8218;";
            //left double quotation mark.
            case '\u201C': return "&#8220;";
            //right double quotation mark.
            case '\u201D': return "&#8221;";
            //double low-9 quotation mark.
            case '\u201E': return "&#8222;";
            //dagger.
            case '\u2020': return "&#8224;";
            //double dagger.
            case '\u2021': return "&#8225;";
            //per mille sign.
            case '\u2030': return "&#8240;";
              //single left-pointing angle quotation mark.
            case '\u2039': return "&#8249;";
            //single right-pointing angle quotation mark.
            case '\u203A': return "&#8250;";
            //euro sign.
            case '\u20AC': return "&#8364;";

            // all other characters remain the same.
            default: return String.valueOf(c);
        }
    }
    
    /**
     * Creates a Writer that, when written to, writes entity
     * equivalents of each character to 'out'; where entity equivalents do not
     * exist, the original character is written directly to 'out'.
     * @param out the writer to which to write the output.
     */
    public static Writer createWriter(final Writer out)
    {
        return new FilterWriter(out) {
            public void write(char[] cbuf, int off, int len) 
            throws IOException {
                for (int i=off; i<off+len; i++) {
                    this.out.write(getEntity(cbuf[i]));
                }
            }
            // Not sure whether I need to override these or not, so will do to
            // play safe. The Java documentation doesn't bother to say what
            // the implementation of these methods in FilterWriter actually do.
            public void close() throws IOException {
                this.out.close();
            }
            public void flush() throws IOException {
               this.out.flush();
            }
            public void write(int c) throws IOException {
               this.out.write(getEntity((char)c));
            }
            public void write(String str, int off, int len) throws IOException {
                this.write(str.toCharArray(), off, len);
            }
        };
    }
    
    
}

