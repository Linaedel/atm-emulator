package ru.sbrf.ku.atm.cell;

import ru.sbrf.ku.atm.Nominal;
import ru.sbrf.ku.atm.atm.ATM;

import java.io.Serializable;

public interface Cell extends Comparable<Cell>, Serializable {
    void put(Integer count);
    Integer get (Integer count);
    String getId();
    Integer getCount();
    Nominal getNominal();
    Integer getBalance();
    void registerAtATM(ATM atm);
}

