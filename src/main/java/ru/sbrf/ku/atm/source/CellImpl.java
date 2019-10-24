package ru.sbrf.ku.atm.source;

public class CellImpl implements Cell {
    private final Nominal nominal;
    private Integer count;

    public CellImpl(Nominal cellNominal, Integer cellCount) {
        this.nominal = cellNominal;
        this.count = cellCount;
    }

    public void put(Integer count) {
        this.count += count;
    }

    public Integer get(Integer count) {
        Integer toReturn = (this.count>=count)?count:this.count;
        this.count -= toReturn;
        return toReturn;
    }

    public Integer getCount() {
        return this.count;
    }

    public Nominal getNominal() {
        return this.nominal;
    }
}
