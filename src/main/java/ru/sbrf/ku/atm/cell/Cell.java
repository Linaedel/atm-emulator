package ru.sbrf.ku.atm.cell;

import ru.sbrf.ku.atm.Nominal;

public interface Cell {
    void put(Integer count);
    Integer get (Integer count);
    Integer getCount();
    Nominal getNominal();
}

