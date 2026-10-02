package com.andric;

/**
 * Utility for normalizing Serbian text to Latin or Cyrillic script.
 */
public final class SerbianScript {

    public enum ScriptMode {
        AUTO,
        LATIN,
        CYRILLIC;

        public ScriptMode next() {
            ScriptMode[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }

    public enum Script {
        LATIN,
        CYRILLIC
    }

    private static volatile ScriptMode mode = ScriptMode.AUTO;

    private SerbianScript() {
    }

    public static ScriptMode getMode() {
        return mode;
    }

    public static void setMode(ScriptMode nextMode) {
        if (nextMode != null) {
            mode = nextMode;
        }
    }

    public static Script resolveTargetScript(String nameHint) {
        if (mode == ScriptMode.LATIN) {
            return Script.LATIN;
        }
        if (mode == ScriptMode.CYRILLIC) {
            return Script.CYRILLIC;
        }
        return containsCyrillic(nameHint) ? Script.CYRILLIC : Script.LATIN;
    }

    public static String normalize(String text, Script script) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return script == Script.CYRILLIC ? latinToCyrillic(text) : cyrillicToLatin(text);
    }

    public static String modeLabel() {
        switch (mode) {
            case LATIN:
                return "Latinica";
            case CYRILLIC:
                return "Cirilica";
            default:
                return "Auto";
        }
    }

    private static boolean containsCyrillic(String text) {
        if (text == null) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= '\u0400' && ch <= '\u04FF') {
                return true;
            }
        }
        return false;
    }

    private static String cyrillicToLatin(String text) {
        StringBuilder out = new StringBuilder(text.length() + 8);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            out.append(cyrCharToLatin(c));
        }
        return out.toString();
    }

    private static String cyrCharToLatin(char c) {
        switch (c) {
            case 'А': return "A";
            case 'а': return "a";
            case 'Б': return "B";
            case 'б': return "b";
            case 'В': return "V";
            case 'в': return "v";
            case 'Г': return "G";
            case 'г': return "g";
            case 'Д': return "D";
            case 'д': return "d";
            case 'Ђ': return "Đ";
            case 'ђ': return "đ";
            case 'Е': return "E";
            case 'е': return "e";
            case 'Ж': return "Ž";
            case 'ж': return "ž";
            case 'З': return "Z";
            case 'з': return "z";
            case 'И': return "I";
            case 'и': return "i";
            case 'Ј': return "J";
            case 'ј': return "j";
            case 'К': return "K";
            case 'к': return "k";
            case 'Л': return "L";
            case 'л': return "l";
            case 'Љ': return "Lj";
            case 'љ': return "lj";
            case 'М': return "M";
            case 'м': return "m";
            case 'Н': return "N";
            case 'н': return "n";
            case 'Њ': return "Nj";
            case 'њ': return "nj";
            case 'О': return "O";
            case 'о': return "o";
            case 'П': return "P";
            case 'п': return "p";
            case 'Р': return "R";
            case 'р': return "r";
            case 'С': return "S";
            case 'с': return "s";
            case 'Т': return "T";
            case 'т': return "t";
            case 'Ћ': return "Ć";
            case 'ћ': return "ć";
            case 'У': return "U";
            case 'у': return "u";
            case 'Ф': return "F";
            case 'ф': return "f";
            case 'Х': return "H";
            case 'х': return "h";
            case 'Ц': return "C";
            case 'ц': return "c";
            case 'Ч': return "Č";
            case 'ч': return "č";
            case 'Џ': return "Dž";
            case 'џ': return "dž";
            case 'Ш': return "Š";
            case 'ш': return "š";
            default: return String.valueOf(c);
        }
    }

    private static String latinToCyrillic(String text) {
        StringBuilder out = new StringBuilder(text.length());
        int i = 0;
        while (i < text.length()) {
            if (i + 1 < text.length()) {
                String pair = text.substring(i, i + 2);
                String digraph = latinDigraphToCyr(pair);
                if (digraph != null) {
                    out.append(digraph);
                    i += 2;
                    continue;
                }
            }
            out.append(latinCharToCyr(text.charAt(i)));
            i++;
        }
        return out.toString();
    }

    private static String latinDigraphToCyr(String pair) {
        if ("nj".equalsIgnoreCase(pair)) {
            return Character.isUpperCase(pair.charAt(0)) ? "Њ" : "њ";
        }
        if ("lj".equalsIgnoreCase(pair)) {
            return Character.isUpperCase(pair.charAt(0)) ? "Љ" : "љ";
        }
        if ("dž".equalsIgnoreCase(pair)) {
            return Character.isUpperCase(pair.charAt(0)) ? "Џ" : "џ";
        }
        return null;
    }

    private static char latinCharToCyr(char c) {
        switch (c) {
            case 'A': return 'А';
            case 'a': return 'а';
            case 'B': return 'Б';
            case 'b': return 'б';
            case 'V': return 'В';
            case 'v': return 'в';
            case 'G': return 'Г';
            case 'g': return 'г';
            case 'D': return 'Д';
            case 'd': return 'д';
            case 'Đ': return 'Ђ';
            case 'đ': return 'ђ';
            case 'E': return 'Е';
            case 'e': return 'е';
            case 'Ž': return 'Ж';
            case 'ž': return 'ж';
            case 'Z': return 'З';
            case 'z': return 'з';
            case 'I': return 'И';
            case 'i': return 'и';
            case 'J': return 'Ј';
            case 'j': return 'ј';
            case 'K': return 'К';
            case 'k': return 'к';
            case 'L': return 'Л';
            case 'l': return 'л';
            case 'M': return 'М';
            case 'm': return 'м';
            case 'N': return 'Н';
            case 'n': return 'н';
            case 'O': return 'О';
            case 'o': return 'о';
            case 'P': return 'П';
            case 'p': return 'п';
            case 'R': return 'Р';
            case 'r': return 'р';
            case 'S': return 'С';
            case 's': return 'с';
            case 'T': return 'Т';
            case 't': return 'т';
            case 'Ć': return 'Ћ';
            case 'ć': return 'ћ';
            case 'U': return 'У';
            case 'u': return 'у';
            case 'F': return 'Ф';
            case 'f': return 'ф';
            case 'H': return 'Х';
            case 'h': return 'х';
            case 'C': return 'Ц';
            case 'c': return 'ц';
            case 'Č': return 'Ч';
            case 'č': return 'ч';
            case 'Š': return 'Ш';
            case 'š': return 'ш';
            default: return c;
        }
    }
}

