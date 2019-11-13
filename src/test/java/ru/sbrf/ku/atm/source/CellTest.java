package ru.sbrf.ku.atm.source;

import org.junit.jupiter.api.Test;
import ru.sbrf.ku.atm.atm.impl.ATMImpl;
import ru.sbrf.ku.atm.cell.impl.CellImpl;
import ru.sbrf.ku.atm.Nominal;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CellTest {

    @Test
    public void testPutCash(){
        CellImpl cell = new CellImpl(UUID.randomUUID().toString(),Nominal.ONE_HUNDRED,5);
        cell.put(2);
        assertEquals(cell.getCount(),7);
    }

    @Test
    public void testGetCashIfPresent(){
        CellImpl cell = new CellImpl(UUID.randomUUID().toString(),Nominal.ONE_HUNDRED,10);
        Integer gotCash = cell.get(9);
        assertEquals(gotCash,9);
    }

    @Test
    public void testGetCashIfAbsent(){
        CellImpl cell = new CellImpl(UUID.randomUUID().toString(),Nominal.ONE_HUNDRED,10);
        Integer gotCash = cell.get(11);
        assertEquals(gotCash,10);
        assertEquals(cell.getCount(),0);
    }
}
