package ru.sbrf.ku.atm.atm;

import ru.sbrf.ku.atm.cell.Cell;

import java.io.IOException;
import java.util.List;

public interface ATMService {
    Long getBalance();
    void saveToFile(String fileName) throws IOException;

    List<Cell> getCells();
    Cell extractCell(String id);
    void insertCell(Cell cell);
}

