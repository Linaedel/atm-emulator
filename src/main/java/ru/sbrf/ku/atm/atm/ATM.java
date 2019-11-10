package ru.sbrf.ku.atm.atm;

import ru.sbrf.ku.atm.Nominal;

import java.util.List;

public interface ATM {
    List<Nominal> putCash( List<Nominal> cashList);
    List<Nominal> getCash(Integer sum);
}
