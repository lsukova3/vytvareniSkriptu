package cz.lenka.app.vytvareniSkriptu.exceptions;

public class CannotBeNullException extends Exception{
        private String nazevPolozky;


        public CannotBeNullException(String nazevPolozky) {
            this.nazevPolozky = nazevPolozky;
        }

    public String getNazevPolozky() {
        return nazevPolozky;
    }
}

