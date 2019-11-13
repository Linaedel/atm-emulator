package ru.sbrf.ku.atm.atm.impl;

import org.junit.jupiter.api.Test;
import ru.sbrf.ku.atm.Nominal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ATMImplTest {

    @Test
    public void getCashTestEven() {
        List<Nominal> nominals = new ArrayList<>();
        nominals.add(Nominal.FIVE_HUNDRED);
        nominals.add(Nominal.TWO_HUNDREDS);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);

        ATMImpl atm = ATMImpl.ATMImplBuilder.build();
        atm.putCash(nominals);

        List<Nominal> gotList = atm.getCash(1000);

        Integer gotCash = 0;
        for (Nominal nominal : gotList) {
            gotCash += nominal.getNominal();
        }

        assertEquals(1000, gotCash);
        assertEquals(0, atm.getBalance());
    }


    @Test
    public void getCashTestLess() {
        List<Nominal> nominals = new ArrayList<>();
        nominals.add(Nominal.FIVE_HUNDRED);
        nominals.add(Nominal.TWO_HUNDREDS);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);

        ATMImpl atm = ATMImpl.ATMImplBuilder.build();
        atm.putCash(nominals);

        List<Nominal> gotList = atm.getCash(900);

        Integer gotCash = 0;
        for (Nominal nominal : gotList) {
            gotCash += nominal.getNominal();
        }

        assertEquals(900, gotCash);
        assertEquals(100, atm.getBalance());
    }

    @Test
    public void getCashTestNegative() {
        List<Nominal> nominals = new ArrayList<>();
        nominals.add(Nominal.FIVE_HUNDRED);
        nominals.add(Nominal.TWO_HUNDREDS);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);

        ATMImpl atm = ATMImpl.ATMImplBuilder.build();
        atm.putCash(nominals);

        assertThrows(IllegalArgumentException.class, () -> atm.getCash(950));
    }

    @Test
    public void getCashTestOverdraft() {
        List<Nominal> nominals = new ArrayList<>();
        nominals.add(Nominal.FIVE_HUNDRED);
        nominals.add(Nominal.TWO_HUNDREDS);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);

        ATMImpl atm = ATMImpl.ATMImplBuilder.build();
        atm.putCash(nominals);

        assertThrows(IllegalArgumentException.class, () -> atm.getCash(1200));
    }

    @Test
    public void testLoadFromFile() throws IOException {
        String json = "{\"cells\":[" +
                "{\"balance\":0,\"class\":\"ru.sbrf.ku.atm.cell.impl.CellImpl\",\"count\":0,\"id\":\"16c611ff-4bb8-45fd-a036-0032bacbc230\",\"nominal\":\"FIVE_THOUSANDS\"}," +
                "{\"balance\":0,\"class\":\"ru.sbrf.ku.atm.cell.impl.CellImpl\",\"count\":0,\"id\":\"81e62e24-c8b0-4593-9e3f-a461b7067d17\",\"nominal\":\"TWO_THOUSANDS\"}," +
                "{\"balance\":0,\"class\":\"ru.sbrf.ku.atm.cell.impl.CellImpl\",\"count\":0,\"id\":\"51a09cf4-93e2-4338-808b-bb0607005e81\",\"nominal\":\"ONE_THOUSAND\"}," +
                "{\"balance\":500,\"class\":\"ru.sbrf.ku.atm.cell.impl.CellImpl\",\"count\":1,\"id\":\"ff68cbce-0d46-49a5-967e-37bcd2ebf111\",\"nominal\":\"FIVE_HUNDRED\"}," +
                "{\"balance\":200,\"class\":\"ru.sbrf.ku.atm.cell.impl.CellImpl\",\"count\":1,\"id\":\"bd578ca6-0a0e-4a1e-92f0-592b92cc39bd\",\"nominal\":\"TWO_HUNDREDS\"}," +
                "{\"balance\":300,\"class\":\"ru.sbrf.ku.atm.cell.impl.CellImpl\",\"count\":3,\"id\":\"81cd8bcf-4c42-4221-8423-1e4a3b4dd624\",\"nominal\":\"ONE_HUNDRED\"}" +
                "],\"class\":\"ru.sbrf.ku.atm.atm.impl.Safe\"}\n";
        BufferedReader mockedReaded = mock(BufferedReader.class);
        when(mockedReaded.readLine()).thenReturn(json).thenReturn(null);

        ATMImpl atm = ATMImpl.ATMImplBuilder.build();
        atm.setBufferedReader(mockedReaded);
        Path filePath = Paths.get("test_atm.ss");
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        atm.loadFromFile("test_atm.ss");
        Files.delete(filePath);

        assertEquals(atm.getBalance(), 1000);
    }

    @Test
    public void saveToFile() throws IOException {
        List<String> controlList = new ArrayList<>(Arrays.asList("0", "0", "0", "500", "200", "300"));
        List<String> listFromSavedFile = new ArrayList<>();

        List<Nominal> nominals = new ArrayList<>();
        nominals.add(Nominal.FIVE_HUNDRED);
        nominals.add(Nominal.TWO_HUNDREDS);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);
        nominals.add(Nominal.ONE_HUNDRED);

        ATMImpl atm = ATMImpl.ATMImplBuilder.build();
        atm.putCash(nominals);
        atm.saveToFile("test_atm.ss");

        try (BufferedReader br = new BufferedReader(new FileReader("test_atm.ss"))) {
            String jsonFromFile = br.readLine();
            Pattern p = Pattern.compile("balance\":(.*?),");
            Matcher m = p.matcher(jsonFromFile);
            while (m.find()) {
                listFromSavedFile.add(m.group().replace("balance\":", "").replace(",", ""));
            }
            assertEquals(controlList.size(), listFromSavedFile.size());
            for (int i = 0; i < controlList.size(); i++) {
                assertEquals(controlList.get(i), listFromSavedFile.get(i));
            }
        }
        Files.delete(Paths.get("test_atm.ss"));
    }

}
