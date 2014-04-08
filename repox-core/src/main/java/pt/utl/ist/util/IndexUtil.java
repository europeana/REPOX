package pt.utl.ist.util;

import java.util.regex.Pattern;

public class IndexUtil {
    public enum RemoveNonIndexableCharacters {REMOVE, DONT_REMOVE};
    public enum RemoveAllSpaces {REMOVE, DONT_REMOVE};
    public enum RemoveDiacritics {REMOVE, DONT_REMOVE};


    private IndexUtil() { /* static methods only - hide constructor */
    }

    private static Pattern cleanSupplusSpaces=Pattern.compile("\\s\\s+");
    private static Pattern cleanAllSpaces=Pattern.compile("\\s+");

    public static String encode(String s)
    {
        return encode(s, RemoveDiacritics.REMOVE, RemoveAllSpaces.DONT_REMOVE, RemoveNonIndexableCharacters.DONT_REMOVE);
    }

    public static String encode(String s, RemoveAllSpaces removeAllSpaces)
    {
        return encode(s, RemoveDiacritics.REMOVE, removeAllSpaces, RemoveNonIndexableCharacters.DONT_REMOVE);
    }

    public static String encode(String s, RemoveDiacritics removeDiacritics, RemoveAllSpaces removeAllSpaces, RemoveNonIndexableCharacters removeNonIndexableCharacters)
    {
        StringBuffer sb = new StringBuffer();
        char[] chars = s.toCharArray();
        for (char aChar : chars) {
            if (removeNonIndexableCharacters == RemoveNonIndexableCharacters.REMOVE && !Character.isLetterOrDigit(aChar) && !Character.isWhitespace(aChar))
                sb.append(' ');
            else if (removeDiacritics == RemoveDiacritics.REMOVE)
                sb.append(IndexUtil.getEntity(aChar));
            else
                sb.append(aChar);
        }
        String ret=sb.toString();
        if(removeAllSpaces==RemoveAllSpaces.REMOVE)
            ret=cleanAllSpaces.matcher(ret).replaceAll("");
        else
            ret=cleanSupplusSpaces.matcher(ret).replaceAll(" ");
        return ret.trim();
    }

    public static String getEntity(char c)
    {
        switch (c) {
            case 'á': return "a";
            case 'à': return "a";
            case 'ã': return "a";
            case 'â': return "a";
            case 'ä': return "a";
            case 'é': return "e";
            case 'è': return "e";
            case 'ê': return "e";
            case 'ë': return "e";
            case 'í': return "i";
            case 'ì': return "i";
            case 'î': return "i";
            case 'ï': return "i";
            case 'ó': return "o";
            case 'ò': return "o";
            case 'õ': return "o";
            case 'ô': return "o";
            case 'ö': return "o";
            case 'ú': return "u";
            case 'ù': return "u";
            case 'û': return "u";
            case 'ü': return "u";
            case 'ç': return "c";
            case 'ñ': return "n";
            case 'ÿ': return "y";

            case 'Á': return "a";
            case 'À': return "a";
            case 'Ã': return "a";
            case 'Â': return "a";
            case 'Ä': return "a";
            case 'É': return "e";
            case 'È': return "e";
            case 'Ê': return "e";
            case 'Ë': return "e";
            case 'Í': return "i";
            case 'Ì': return "i";
            case 'Î': return "i";
            case 'Ï': return "i";
            case 'Ó': return "o";
            case 'Ò': return "o";
            case 'Õ': return "o";
            case 'Ô': return "o";
            case 'Ö': return "o";
            case 'Ú': return "u";
            case 'Ù': return "u";
            case 'Û': return "u";
            case 'Ü': return "u";
            case 'Ç': return "c";
            case 'Ñ': return "n";

            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
                return String.valueOf(c);

            // all other characters remain the same.
            default:
                if (c >= 'a' && c <= 'z')
                    return String.valueOf(c);
                else if (c >= 'A' && c <= 'Z')
                    return String.valueOf(c).toLowerCase();
                else
                    return " ";
        }
    }


}
