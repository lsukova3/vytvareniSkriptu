package cz.lenka.app.vytvareniSkriptu.exceptions;

public class WrongDateFormatException extends TheItemHasABadFormatException {
    public WrongDateFormatException(String nazevPolozky, String hodnotaPolozky, String nazevSloupce, String maska, String regexp) {
        this.setZprava("Datum " + nazevPolozky + " (" + hodnotaPolozky + ") neodpovida pozadovane masce " + maska + " (regexp: \"" + regexp + "\") pro vložení do sloupce " + nazevSloupce + ".");
    }
}
