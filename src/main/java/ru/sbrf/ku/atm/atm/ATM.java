package ru.sbrf.ku.atm.atm;

import ru.sbrf.ku.atm.Nominal;
import ru.sbrf.ku.atm.Observer;

import java.util.List;

public interface ATM extends Observer {
    List<Nominal> putCash( List<Nominal> cashList);
    List<Nominal> getCash(Integer sum);
}
