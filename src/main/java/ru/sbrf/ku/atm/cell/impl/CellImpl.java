package ru.sbrf.ku.atm.cell.impl;

import org.slf4j.Logger;
import ru.sbrf.ku.atm.ATMLogger;
import ru.sbrf.ku.atm.Nominal;
import ru.sbrf.ku.atm.Observer;
import ru.sbrf.ku.atm.atm.ATM;
import ru.sbrf.ku.atm.cell.Cell;
import ru.sbrf.ku.atm.exceptions.ATMRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class CellImpl implements Cell, Comparable<Cell> {
    public static final Integer MAX_CAPACITY = 10;
    private final String id;
    private final Nominal nominal;
    private Integer count;
    private transient List<Observer> observers;
    private transient Logger logger = ATMLogger.getLogger();

    public CellImpl() {
        this.id = null;
        this.nominal = null;
        this.count = null;
        observers = new ArrayList<>();
    }

    public CellImpl(String id, Nominal cellNominal, Integer cellCount) {
        this.id = id;
        this.nominal = cellNominal;
        this.count = cellCount;
        observers = new ArrayList<>();
    }

    public void registerAtATM(ATM atm) {
        observers.add(atm);
        notifyAllObservers();
    }

    public static Integer getMaxCapacity() {
        return MAX_CAPACITY;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public void put(Integer count) {
        if (this.count + count > MAX_CAPACITY) {
            throw new ATMRuntimeException("Cell [ID: " + id + "] is full");
        }
        this.count += count;
        logger.debug("[CELL] Successfully put {} of {} nominal to cell [ID: {}]", count, nominal.getNominal(), id);
        notifyAllObservers();
    }

    @Override
    public Integer get(Integer count) {
        Logger logger = ATMLogger.getLogger();
        Integer toReturn = (this.count >= count) ? count : this.count;
        this.count -= toReturn;
        logger.debug("[CELL] Successfully taken {} of {} nominal from cell [ID: {}]", count, nominal.getNominal(), id);
        notifyAllObservers();
        return toReturn;
    }

    @Override
    public Integer getCount() {
        return this.count;
    }

    @Override
    public Nominal getNominal() {
        return this.nominal;
    }

    public String getId() {
        return id;
    }

    @Override
    public Integer getBalance() {
        return nominal.getNominal() * count;
    }

    public void notifyAllObservers() {
        for (Observer observer : observers) {
            observer.updateBalance();
        }
    }

    @Override
    public int compareTo(Cell o) {
        return this.getNominal().getNominal().compareTo(o.getNominal().getNominal());
    }
}
