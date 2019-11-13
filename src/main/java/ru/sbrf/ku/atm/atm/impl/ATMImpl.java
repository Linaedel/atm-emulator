package ru.sbrf.ku.atm.atm.impl;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.slf4j.Logger;
import ru.sbrf.ku.atm.ATMLogger;
import ru.sbrf.ku.atm.Nominal;
import ru.sbrf.ku.atm.atm.ATM;
import ru.sbrf.ku.atm.atm.ATMService;
import ru.sbrf.ku.atm.cell.Cell;
import ru.sbrf.ku.atm.cell.impl.CellImpl;
import ru.sbrf.ku.atm.exceptions.ATMRuntimeException;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ATMImpl implements ATMService, ATM {
    private Logger logger = ATMLogger.getLogger();
    private Safe safe = new Safe();
    private transient BufferedReader bufferedReader;
    private Long ATMbalance = 0L;

    public ATMImpl() {
    }

    @Override
    public List<Nominal> putCash(List<Nominal> cashList) {
        List<Nominal> unacceptedBanknotes = new ArrayList<>();
        for (Nominal banknoteNominal : cashList) {
            boolean accepted = false;
            for (Cell cell : safe.getCells()) {
                if (cell.getNominal().equals(banknoteNominal)) {
                    try {
                        cell.put(1);
                        accepted = true;
                        break;
                    } catch (ATMRuntimeException e) {
                        logger.warn("[ATM] {}", e.getMessage());
                    }
                }
            }
            if (!accepted) {
                unacceptedBanknotes.add(banknoteNominal);
            }
        }
        return unacceptedBanknotes;
    }

    @Override
    public List<Nominal> getCash(Integer sum) {
        if (sum % 100 != 0) {
            throw new IllegalArgumentException("Amount is incorrect. Minimal nominal is 100 rubles.");
        }
        if (sum > this.getBalance()) {
            throw new IllegalArgumentException("Amount exceeds ATM balance.");
        }
        List<Nominal> outList = new ArrayList<>();

        for (Cell cell : safe.getCells()) {
            Nominal nominal = cell.getNominal();
            Integer mustGive = sum / nominal.getNominal();

            Integer canGive = cell.getCount();
            int processValue = Math.min(canGive, mustGive);
            if (processValue > 0) {

                sum -= processValue * nominal.getNominal();
                cell.get(processValue); //То ли я не понимаю, то ли тут должен быть processValue, а не canGive
                for (int i = 0; i < processValue; i++) {
                    outList.add(nominal);
                }
            }
        }
        if (sum != 0) {
            StringBuilder sb = new StringBuilder("It`s not possible to give asked amount due to lack of banknotes in the ATM. ");

            long minimalBelow = 0;
            for (Nominal nominal : outList) {
                minimalBelow += nominal.getNominal();
            }
            long minimalAbove = 0;
            List<Cell> reverseList = safe.getCells();
            reverseList.sort(Comparator.reverseOrder());
            for (Cell cell : reverseList) {
                if (cell.getCount() != 0) {
                    minimalAbove = cell.getNominal().getNominal();
                    if (minimalAbove == minimalBelow) {
                        minimalAbove += minimalBelow;
                    }
                }
            }
            if (minimalAbove == 0) {
                sb.append("Nearest amount below amount is ").append(minimalBelow);
            } else if (minimalBelow == 0) {
                sb.append("Nearest amount above is ").append(minimalAbove);
            } else {
                sb.append("Neatest possible amounts are ").append(minimalBelow).append(" and ").append(minimalAbove);
            }
            putCash(outList);
            throw new ATMRuntimeException(sb.toString());
        }
        return outList;
    }

    @Override
    public Long getBalance() {
        return this.ATMbalance;
    }

    @Override
    public void saveToFile(String fileName) throws IOException {
        JSONSerializer ser = new JSONSerializer();
        String data = ser.deepSerialize(safe);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(data);
            writer.flush();
            logger.warn("[ATM] Configuration successfully saved into {}", fileName);
        }
    }

    @Override
    public List<Cell> getCells() {
        return safe.getCells();
    }

    @Override
    public Cell extractCell(String id) {
        return safe.extractCell(id);
    }

    @Override
    public void insertCell(Cell cell) {
        cell.registerAtATM(this);
        try {
            safe.insertCell(cell);
        } catch (ATMRuntimeException e) {
            logger.warn("[ATM] {}", e.getMessage());
        }
    }

    public ATMImpl setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        return this;
    }

    public void loadFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            safe.setCells(new ArrayList<>());

            String data = readConfigurationFile(fileName);
            JSONDeserializer<Safe> der = new JSONDeserializer<>();

            Safe safe = der.deserialize(data);
            this.safe = safe;
            this.safe.registerCells(this);
            logger.warn("[ATM] Configuration was successfully loaded from {}", file.getAbsoluteFile());
        } else {
            logger.error("[ATM] Configuration loading failed! File not exists: {}", file.getAbsoluteFile());
            setInitialCells();
        }
        sortCells();
    }

    private String readConfigurationFile(String fileName) throws IOException {
        File file = new File(fileName);

        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new FileReader(file));
        }
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    public void updateBalance() {
        List<Cell> cells = safe.getCells();
        ATMbalance = 0L;
        for (Cell cell : cells) {
            ATMbalance += cell.getBalance();
        }
    }

    public static class ATMImplBuilder {
        public static ATMImpl build() {
            ATMImpl atm = new ATMImpl();
            atm.setInitialCells();
            return atm;
        }

        public static ATMImpl buildFromFile(String fileName) {
            ATMImpl atm = new ATMImpl();

            try {
                atm.loadFromFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return atm;
        }
    }

    private void setInitialCells() {
        this.safe.setCells(new ArrayList<>());
        for (Nominal nominal : Nominal.values()) {
            Cell cell = new CellImpl(UUID.randomUUID().toString(), nominal, 0);
            cell.registerAtATM(this);
            this.safe.insertCell(cell);
        }
        sortCells();
        logger.warn("[ATM] Initial set of blank cells of each nominal was loaded");
    }

    private void sortCells() {
        safe.getCells().sort(Comparator.naturalOrder());
    }
}
