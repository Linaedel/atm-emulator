package ru.sbrf.ku.atm.atm;

import java.io.IOException;

public interface ATMService {
    Integer getBalance();
    void saveToFile(String fileName) throws IOException;
}

