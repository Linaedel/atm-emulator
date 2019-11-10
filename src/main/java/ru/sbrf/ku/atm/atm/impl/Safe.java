package ru.sbrf.ku.atm.atm.impl;


import ru.sbrf.ku.atm.cell.impl.CellImpl;

import java.util.ArrayList;
import java.util.List;

public class Safe {

    private List<CellImpl> cells = new ArrayList<>();

    public List<CellImpl> getCells() {
        return cells;
    }

    public Safe setCells( List<CellImpl> cells ) {
        this.cells = cells;
        return this;
    }
}
