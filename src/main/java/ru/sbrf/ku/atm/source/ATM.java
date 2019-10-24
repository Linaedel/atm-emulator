package ru.sbrf.ku.atm.source;

import java.util.List;

public interface ATM {
    void putCash(List<Nominal> cashList);
    List<Nominal> getCash(Integer sum);
}
