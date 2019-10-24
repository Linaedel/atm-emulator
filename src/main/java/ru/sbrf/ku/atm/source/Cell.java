package ru.sbrf.ku.atm.source;

public interface Cell {
    void put(Integer count);
    Integer get (Integer count);
    Integer getCount();
    Nominal getNominal();
}
