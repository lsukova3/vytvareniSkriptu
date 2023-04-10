package cz.lenka.app.vytvareniSkriptu.model;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class Soubor extends File {
    public Soubor(@NotNull String pathname) {
        super(pathname);
    }
}

