package ru.sbrf.ku.atm.atm.impl;


import org.slf4j.Logger;
import ru.sbrf.ku.atm.ATMLogger;
import ru.sbrf.ku.atm.atm.ATM;
import ru.sbrf.ku.atm.cell.Cell;
import ru.sbrf.ku.atm.exceptions.ATMRuntimeException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Safe implements Serializable {
    public static final Integer MAX_CAPACITY = 10;
    private List<Cell> cells = new ArrayList<>();
    private transient Logger logger = ATMLogger.getLogger();

    public List<Cell> getCells() {
        return cells;
    }

    public void insertCell(Cell cell) {
        if (cells.size() == MAX_CAPACITY) {
            throw new ATMRuntimeException("Safe is full");
        }
        if (cell != null) {
            cells.add(cell);
        }
    }

    public Cell extractCell(String id) {
        Cell extracted = null;
        for (Cell cell : cells) {
            if (cell.getId().equalsIgnoreCase(id)) {
                extracted = cells.remove(cells.indexOf(cell));
            }
        }
        if (extracted != null) {
            logger.warn("[SAFE] Cell [ID: {}] successfully extracted", extracted.getId());
        } else {
            logger.error("[SAFE] Extraction attempt was unsuccessful! There is no cell [ID: {}] in the ATM!");
        }
        return extracted;
    }

    public Safe setCells(List<Cell> cells) {
        this.cells = cells;
        return this;
    }

    public void registerCells(ATM atm) {
        cells.forEach(с -> с.registerAtATM(atm));
    }
}
