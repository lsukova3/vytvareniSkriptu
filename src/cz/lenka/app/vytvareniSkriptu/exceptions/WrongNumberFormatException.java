package cz.lenka.app.vytvareniSkriptu.exceptions;

public class WrongNumberFormatException extends TheItemHasABadFormatException {
    public WrongNumberFormatException(String nazevPolozky, String hodnotaPolozky, String nazevSloupce, String maska, String regexp) {
        this.setZprava("Cislo " + nazevPolozky + "("+ hodnotaPolozky + ") neodpovida pozadovane masce " + maska + " (" + regexp + ") pro vložení do sloupce " + nazevSloupce + ".");
    }
}
