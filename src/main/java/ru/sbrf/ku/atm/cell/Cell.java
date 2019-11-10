package ru.sbrf.ku.atm.cell;

import ru.sbrf.ku.atm.Nominal;

public interface Cell extends Comparable<Cell>{
    void put(Integer count);
    Integer get (Integer count);
    String getId();
    Integer getCount();
    Nominal getNominal();
    Integer getBalance();
}

