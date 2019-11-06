package ru.sbrf.ku.atm.cell.impl;

import ru.sbrf.ku.atm.Nominal;
import ru.sbrf.ku.atm.cell.Cell;

public class CellImpl implements Cell {
    private final Nominal nominal;
    private Integer count;

    public CellImpl( Nominal cellNominal, Integer cellCount ) {
        this.nominal = cellNominal;
        this.count = cellCount;
    }

    @Override
    public void put( Integer count ) {
        this.count += count;
    }

    @Override
    public Integer get( Integer count ) {
        Integer toReturn = ( this.count >= count ) ? count : this.count;
        this.count -= toReturn;
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

}
