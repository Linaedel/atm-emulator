package ru.sbrf.ku.atm.atm.impl;


import ru.sbrf.ku.atm.cell.Cell;
import ru.sbrf.ku.atm.cell.impl.CellImpl;

import java.util.ArrayList;
import java.util.List;

public class Safe {
    private List<Cell> cells = new ArrayList<>();

    public List<Cell> getCells() {
        return cells;
    }

//    public Safe setCells( List<CellImpl> cells ) {
//        for
//        this.cells = cells;
//        return this;
//    }

    public void insertCell(Cell cell) {
        if (cell != null) {
            cells.add(cell);
        }
    }

    public Cell extractCell(String id){
        Cell extracted = null;
        for (Cell cell : cells){
            if (cell.getId().equalsIgnoreCase(id)){
                extracted = cells.remove(cells.indexOf(cell));
            }
        }
        return extracted;
    }

    public Safe setCells( List<Cell> cells ) {
        this.cells = cells;
        return this;
    }
}
