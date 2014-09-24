package pt.utl.ist.characters;

import java.util.HashMap;

/**
 * Description: Utility for converting character references e.g.: &lt; &gt;
 * &quot; &#229; &#1048; &#x6C34;
 * 
 */
public class HtmlDecoder {

    private static final HashMap charTable;

    /**
     * @param s
     * @return the decoded String
     */
    public static String decode(String s) {
        String t;
        Character ch;
        int tmpPos, i;

        int maxPos = s.length();
        StringBuffer sb = new StringBuffer(maxPos);
        int curPos = 0;
        while (curPos < maxPos) {
            char c = s.charAt(curPos++);
            if (c == '&') {
                tmpPos = curPos;
                if (tmpPos < maxPos) {
                    char d = s.charAt(tmpPos++);
                    if (d == '#') {
                        if (tmpPos < maxPos) {
                            d = s.charAt(tmpPos++);
                            if (d == 'x' || d == 'X') {
                                if (tmpPos < maxPos) {
                                    d = s.charAt(tmpPos++);
                                    if (isHexDigit(d)) {
                                        while (tmpPos < maxPos) {
                                            d = s.charAt(tmpPos++);
                                            if (!isHexDigit(d)) {
                                                if (d == ';') {
                                                    t = s.substring(curPos + 2, tmpPos - 1);
                                                    try {
                                                        i = Integer.parseInt(t, 16);
                                                        if (i >= 0 && i < 65536) {
                                                            c = (char)i;
                                                            curPos = tmpPos;
                                                        }
                                                    } catch (NumberFormatException e) {
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else if (isDigit(d)) {
                                while (tmpPos < maxPos) {
                                    d = s.charAt(tmpPos++);
                                    if (!isDigit(d)) {
                                        if (d == ';') {
                                            t = s.substring(curPos + 1, tmpPos - 1);
                                            try {
                                                i = Integer.parseInt(t);
                                                if (i >= 0 && i < 65536) {
                                                    c = (char)i;
                                                    curPos = tmpPos;
                                                }
                                            } catch (NumberFormatException e) {
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (isLetter(d)) {
                        while (tmpPos < maxPos) {
                            d = s.charAt(tmpPos++);
                            if (!isLetterOrDigit(d)) {
                                if (d == ';') {
                                    t = s.substring(curPos, tmpPos - 1);
                                    ch = (Character)charTable.get(t);
                                    if (ch != null) {
                                        c = ch.charValue();
                                        curPos = tmpPos;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }

    private static boolean isHexDigit(char c) {
        return isHexLetter(c) || isDigit(c);
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isHexLetter(char c) {
        return (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * @param s
     * @return the compacted String
     */
    public static String compact(String s) {
        int maxPos = s.length();
        StringBuffer sb = new StringBuffer(maxPos);
        int curPos = 0;
        while (curPos < maxPos) {
            char c = s.charAt(curPos++);
            if (isWhitespace(c)) {
                while (curPos < maxPos && isWhitespace(s.charAt(curPos))) {
                    curPos++;
                }
                c = '\u0020';
            }
            sb.append(c);
        }
        return sb.toString();
    }

    // HTML is very particular about what constitutes white space.
    /**
     * @param ch
     * @return boolean if ch is a whitespace
     */
    public static boolean isWhitespace(char ch) {
        return ch == '\u0020' || ch == '\r' || ch == '\n' || ch == '\u0009' || ch == '\u000c' || ch == '\u200b';
    }

    static {
        charTable = new HashMap();
        charTable.put("quot", (char)34);
        charTable.put("amp", (char)38);
        charTable.put("apos", (char)39);
        charTable.put("lt", (char)60);
        charTable.put("gt", (char)62);
        charTable.put("nbsp", (char)160);
        charTable.put("iexcl", (char)161);
        charTable.put("cent", (char)162);
        charTable.put("pound", (char)163);
        charTable.put("curren", (char)164);
        charTable.put("yen", (char)165);
        charTable.put("brvbar", (char)166);
        charTable.put("sect", (char)167);
        charTable.put("uml", (char)168);
        charTable.put("copy", (char)169);
        charTable.put("ordf", (char)170);
        charTable.put("laquo", (char)171);
        charTable.put("not", (char)172);
        charTable.put("shy", (char)173);
        charTable.put("reg", (char)174);
        charTable.put("macr", (char)175);
        charTable.put("deg", (char)176);
        charTable.put("plusmn", (char)177);
        charTable.put("sup2", (char)178);
        charTable.put("sup3", (char)179);
        charTable.put("acute", (char)180);
        charTable.put("micro", (char)181);
        charTable.put("para", (char)182);
        charTable.put("middot", (char)183);
        charTable.put("cedil", (char)184);
        charTable.put("sup1", (char)185);
        charTable.put("ordm", (char)186);
        charTable.put("raquo", (char)187);
        charTable.put("frac14", (char)188);
        charTable.put("frac12", (char)189);
        charTable.put("frac34", (char)190);
        charTable.put("iquest", (char)191);
        charTable.put("Agrave", (char)192);
        charTable.put("Aacute", (char)193);
        charTable.put("Acirc", (char)194);
        charTable.put("Atilde", (char)195);
        charTable.put("Auml", (char)196);
        charTable.put("Aring", (char)197);
        charTable.put("AElig", (char)198);
        charTable.put("Ccedil", (char)199);
        charTable.put("Egrave", (char)200);
        charTable.put("Eacute", (char)201);
        charTable.put("Ecirc", (char)202);
        charTable.put("Euml", (char)203);
        charTable.put("Igrave", (char)204);
        charTable.put("Iacute", (char)205);
        charTable.put("Icirc", (char)206);
        charTable.put("Iuml", (char)207);
        charTable.put("ETH", (char)208);
        charTable.put("Ntilde", (char)209);
        charTable.put("Ograve", (char)210);
        charTable.put("Oacute", (char)211);
        charTable.put("Ocirc", (char)212);
        charTable.put("Otilde", (char)213);
        charTable.put("Ouml", (char)214);
        charTable.put("times", (char)215);
        charTable.put("Oslash", (char)216);
        charTable.put("Ugrave", (char)217);
        charTable.put("Uacute", (char)218);
        charTable.put("Ucirc", (char)219);
        charTable.put("Uuml", (char)220);
        charTable.put("Yacute", (char)221);
        charTable.put("THORN", (char)222);
        charTable.put("szlig", (char)223);
        charTable.put("agrave", (char)224);
        charTable.put("aacute", (char)225);
        charTable.put("acirc", (char)226);
        charTable.put("atilde", (char)227);
        charTable.put("auml", (char)228);
        charTable.put("aring", (char)229);
        charTable.put("aelig", (char)230);
        charTable.put("ccedil", (char)231);
        charTable.put("egrave", (char)232);
        charTable.put("eacute", (char)233);
        charTable.put("ecirc", (char)234);
        charTable.put("euml", (char)235);
        charTable.put("igrave", (char)236);
        charTable.put("iacute", (char)237);
        charTable.put("icirc", (char)238);
        charTable.put("iuml", (char)239);
        charTable.put("eth", (char)240);
        charTable.put("ntilde", (char)241);
        charTable.put("ograve", (char)242);
        charTable.put("oacute", (char)243);
        charTable.put("ocirc", (char)244);
        charTable.put("otilde", (char)245);
        charTable.put("ouml", (char)246);
        charTable.put("divide", (char)247);
        charTable.put("oslash", (char)248);
        charTable.put("ugrave", (char)249);
        charTable.put("uacute", (char)250);
        charTable.put("ucirc", (char)251);
        charTable.put("uuml", (char)252);
        charTable.put("yacute", (char)253);
        charTable.put("thorn", (char)254);
        charTable.put("yuml", (char)255);
        charTable.put("OElig", (char)338);
        charTable.put("oelig", (char)339);
        charTable.put("Scaron", (char)352);
        charTable.put("scaron", (char)353);
        charTable.put("Yuml", (char)376);
        charTable.put("fnof", (char)402);
        charTable.put("circ", (char)710);
        charTable.put("tilde", (char)732);
        charTable.put("Alpha", (char)913);
        charTable.put("Beta", (char)914);
        charTable.put("Gamma", (char)915);
        charTable.put("Delta", (char)916);
        charTable.put("Epsilon", (char)917);
        charTable.put("Zeta", (char)918);
        charTable.put("Eta", (char)919);
        charTable.put("Theta", (char)920);
        charTable.put("Iota", (char)921);
        charTable.put("Kappa", (char)922);
        charTable.put("Lambda", (char)923);
        charTable.put("Mu", (char)924);
        charTable.put("Nu", (char)925);
        charTable.put("Xi", (char)926);
        charTable.put("Omicron", (char)927);
        charTable.put("Pi", (char)928);
        charTable.put("Rho", (char)929);
        charTable.put("Sigma", (char)931);
        charTable.put("Tau", (char)932);
        charTable.put("Upsilon", (char)933);
        charTable.put("Phi", (char)934);
        charTable.put("Chi", (char)935);
        charTable.put("Psi", (char)936);
        charTable.put("Omega", (char)937);
        charTable.put("alpha", (char)945);
        charTable.put("beta", (char)946);
        charTable.put("gamma", (char)947);
        charTable.put("delta", (char)948);
        charTable.put("epsilon", (char)949);
        charTable.put("zeta", (char)950);
        charTable.put("eta", (char)951);
        charTable.put("theta", (char)952);
        charTable.put("iota", (char)953);
        charTable.put("kappa", (char)954);
        charTable.put("lambda", (char)955);
        charTable.put("mu", (char)956);
        charTable.put("nu", (char)957);
        charTable.put("xi", (char)958);
        charTable.put("omicron", (char)959);
        charTable.put("pi", (char)960);
        charTable.put("rho", (char)961);
        charTable.put("sigmaf", (char)962);
        charTable.put("sigma", (char)963);
        charTable.put("tau", (char)964);
        charTable.put("upsilon", (char)965);
        charTable.put("phi", (char)966);
        charTable.put("chi", (char)967);
        charTable.put("psi", (char)968);
        charTable.put("omega", (char)969);
        charTable.put("thetasym", (char)977);
        charTable.put("upsih", (char)978);
        charTable.put("piv", (char)982);
        charTable.put("ensp", (char)8194);
        charTable.put("emsp", (char)8195);
        charTable.put("thinsp", (char)8201);
        charTable.put("zwnj", (char)8204);
        charTable.put("zwj", (char)8205);
        charTable.put("lrm", (char)8206);
        charTable.put("rlm", (char)8207);
        charTable.put("ndash", (char)8211);
        charTable.put("mdash", (char)8212);
        charTable.put("lsquo", (char)8216);
        charTable.put("rsquo", (char)8217);
        charTable.put("sbquo", (char)8218);
        charTable.put("ldquo", (char)8220);
        charTable.put("rdquo", (char)8221);
        charTable.put("bdquo", (char)8222);
        charTable.put("dagger", (char)8224);
        charTable.put("Dagger", (char)8225);
        charTable.put("bull", (char)8226);
        charTable.put("hellip", (char)8230);
        charTable.put("permil", (char)8240);
        charTable.put("prime", (char)8242);
        charTable.put("Prime", (char)8243);
        charTable.put("lsaquo", (char)8249);
        charTable.put("rsaquo", (char)8250);
        charTable.put("oline", (char)8254);
        charTable.put("frasl", (char)8260);
        charTable.put("euro", (char)8364);
        charTable.put("image", (char)8465);
        charTable.put("weierp", (char)8472);
        charTable.put("real", (char)8476);
        charTable.put("trade", (char)8482);
        charTable.put("alefsym", (char)8501);
        charTable.put("larr", (char)8592);
        charTable.put("uarr", (char)8593);
        charTable.put("rarr", (char)8594);
        charTable.put("darr", (char)8595);
        charTable.put("harr", (char)8596);
        charTable.put("crarr", (char)8629);
        charTable.put("lArr", (char)8656);
        charTable.put("uArr", (char)8657);
        charTable.put("rArr", (char)8658);
        charTable.put("dArr", (char)8659);
        charTable.put("hArr", (char)8660);
        charTable.put("forall", (char)8704);
        charTable.put("part", (char)8706);
        charTable.put("exist", (char)8707);
        charTable.put("empty", (char)8709);
        charTable.put("nabla", (char)8711);
        charTable.put("isin", (char)8712);
        charTable.put("notin", (char)8713);
        charTable.put("ni", (char)8715);
        charTable.put("prod", (char)8719);
        charTable.put("sum", (char)8721);
        charTable.put("minus", (char)8722);
        charTable.put("lowast", (char)8727);
        charTable.put("radic", (char)8730);
        charTable.put("prop", (char)8733);
        charTable.put("infin", (char)8734);
        charTable.put("ang", (char)8736);
        charTable.put("and", (char)8743);
        charTable.put("or", (char)8744);
        charTable.put("cap", (char)8745);
        charTable.put("cup", (char)8746);
        charTable.put("int", (char)8747);
        charTable.put("there4", (char)8756);
        charTable.put("sim", (char)8764);
        charTable.put("cong", (char)8773);
        charTable.put("asymp", (char)8776);
        charTable.put("ne", (char)8800);
        charTable.put("equiv", (char)8801);
        charTable.put("le", (char)8804);
        charTable.put("ge", (char)8805);
        charTable.put("sub", (char)8834);
        charTable.put("sup", (char)8835);
        charTable.put("nsub", (char)8836);
        charTable.put("sube", (char)8838);
        charTable.put("supe", (char)8839);
        charTable.put("oplus", (char)8853);
        charTable.put("otimes", (char)8855);
        charTable.put("perp", (char)8869);
        charTable.put("sdot", (char)8901);
        charTable.put("lceil", (char)8968);
        charTable.put("rceil", (char)8969);
        charTable.put("lfloor", (char)8970);
        charTable.put("rfloor", (char)8971);
        charTable.put("lang", (char)9001);
        charTable.put("rang", (char)9002);
        charTable.put("loz", (char)9674);
        charTable.put("spades", (char)9824);
        charTable.put("clubs", (char)9827);
        charTable.put("hearts", (char)9829);
        charTable.put("diams", (char)9830);
    }
}