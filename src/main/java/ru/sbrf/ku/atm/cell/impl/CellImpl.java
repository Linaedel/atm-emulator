package ru.sbrf.ku.atm.cell.impl;

import ru.sbrf.ku.atm.ATMRuntimeException;
import ru.sbrf.ku.atm.Nominal;
import ru.sbrf.ku.atm.Observer;
import ru.sbrf.ku.atm.atm.ATM;
import ru.sbrf.ku.atm.atm.impl.ATMImpl;
import ru.sbrf.ku.atm.cell.Cell;

import java.util.ArrayList;
import java.util.List;

public class CellImpl implements Cell, Comparable<Cell> {
    public static final Integer MAX_CAPACITY = 10;
    private final String id;
    private final Nominal nominal;
    private Integer count;
    private List<Observer> observers;

    public CellImpl( String id, Nominal cellNominal, Integer cellCount, ATMImpl atm ) {
        this.id = id;
        this.nominal = cellNominal;
        this.count = cellCount;
        observers = new ArrayList<>();
        observers.add(atm);
        notifyAllObservers();
    }

    @Override
    public void put( Integer count ) {
        if (this.count + count > MAX_CAPACITY){
            throw new ATMRuntimeException("Cell is full");
        }
        this.count += count;
        notifyAllObservers();
    }

    @Override
    public Integer get( Integer count ) {
        Integer toReturn = ( this.count >= count ) ? count : this.count;
        this.count -= toReturn;
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

//    @Override
    public Integer getBalance() {
        return nominal.getNominal()*count;
    }

    public void notifyAllObservers() {
        for (Observer observer : observers){
            observer.updateBalance();
        }
    }


    @Override
    public int compareTo(Cell o) {
        return this.getNominal().getNominal().compareTo( o.getNominal().getNominal());
    }
}
