package cz.lenka.app.vytvareniSkriptu.exceptions;

public class TheItemIsNotANumberException extends TheItemHasABadFormatException {
    public TheItemIsNotANumberException(String nazevPolozky) {
        this.setZprava("Číslo " + nazevPolozky + " ma spatny formát.");
    }
}
