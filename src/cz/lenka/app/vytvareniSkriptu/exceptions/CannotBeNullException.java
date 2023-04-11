package cz.lenka.app.vytvareniSkriptu.exceptions;

public class CannotBeNullException extends TheItemHasABadFormatException {

        public CannotBeNullException(String nazevPolozky) {
            this.setZprava("Polozka " + nazevPolozky + " nesmi byt null");
        }

}

