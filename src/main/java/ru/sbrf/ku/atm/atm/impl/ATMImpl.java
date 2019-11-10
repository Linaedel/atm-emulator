package ru.sbrf.ku.atm.atm.impl;

import com.google.gson.Gson;
import ru.sbrf.ku.atm.ATMRuntimeException;
import ru.sbrf.ku.atm.Nominal;
import ru.sbrf.ku.atm.Observer;
import ru.sbrf.ku.atm.atm.ATM;
import ru.sbrf.ku.atm.atm.ATMService;
import ru.sbrf.ku.atm.cell.Cell;
import ru.sbrf.ku.atm.cell.impl.CellImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ATMImpl implements ATMService, ATM, Observer {
//    private Map<Nominal, Cell> atmStorage;
    private Safe safe = new Safe();
    private BufferedReader bufferedReader;
    private Long ATMbalance = 0L;



    public ATMImpl() {
//        this.atmStorage = new HashMap<>();
//        for ( Nominal nominal : Nominal.values() ) {
//            this.atmStorage.put( nominal, new CellImpl(UUID.randomUUID().toString(), nominal, 1 , this) );
//        }
    }

    @Override
    public List<Nominal> putCash( List<Nominal> cashList ) {
        List<Nominal> unacceptedBanknotes = new ArrayList<>();
        for ( Nominal banknoteNominal : cashList ) {
            boolean accepted = false;
            for ( Cell cell : safe.getCells() ) {
                if ( cell.getNominal().equals( banknoteNominal ) ) {
                    try {
                        cell.put( 1 );
                        accepted = true;
                        break;
                    } catch ( ATMRuntimeException e ) {
                        // TODO Залоггировать
                    }
                }
            }
            if ( ! accepted ) {
                unacceptedBanknotes.add( banknoteNominal );
            }
        }
        return unacceptedBanknotes;
    }

    @Override
    public List<Nominal> getCash( Integer sum ) {
        if ( sum % 100 != 0 ) {
            throw new IllegalArgumentException( "Введена некорректная сумма. Минимальная купюра - 100р." );
        }
        if ( sum > this.getBalance() ) {
            throw new IllegalArgumentException( "Запрашиваемая сумма превышает остаток денег в банкомате." );
        }
        List<Nominal> outList = new ArrayList<>();

        for ( Cell cell : safe.getCells() ) {
            Nominal nominal = cell.getNominal();
            Integer mustGive = sum / nominal.getNominal();

            Integer canGive = cell.getCount();
            int processValue = Math.min( canGive, mustGive );
            if ( processValue > 0 ) {

                sum -= processValue * nominal.getNominal();
                cell.get( processValue ); //То ли я не понимаю, то ли тут должен быть processValue, а не canGive
                for ( int i = 0; i < processValue; i++ ) {
                    outList.add( nominal );
                }
            }
        }
        if ( sum != 0 ) {
            StringBuilder sb = new StringBuilder("It`s not possible to give asked amount due to lack of banknotes in the ATM. ");
            sb.append("Nearest possible amounts to give are ");

//            outList = new ArrayList<>();
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
            putCash( outList );
            throw new ATMRuntimeException(sb.toString());
        }
        return outList;
    }

    @Override
    public Long getBalance() {
        return this.ATMbalance;
    }

    @Override
    public void saveToFile( String fileName ) throws IOException {
        File file = new File( fileName );
        if ( file.exists() ) {
            file.delete();
        }
        Gson gson = new Gson();
        String data = gson.toJson( safe );
        try ( FileWriter writer = new FileWriter( file ) ) {
            writer.write( data );
            writer.flush();
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
        safe.insertCell(cell);
    }


    public ATMImpl setBufferedReader( BufferedReader bufferedReader ) {
        this.bufferedReader = bufferedReader;
        return this;
    }

    public void loadFromFile( String fileName ) throws IOException {
        File file = new File( fileName );
        if (file.exists()) {
            safe.setCells( new ArrayList<>() );

            String data = readIniFile( fileName );
            Gson gson = new Gson();
            Safe safe = gson.fromJson( data, Safe.class );
            this.safe = safe;

        } else {
            this.safe.setCells( new ArrayList<>() );
            for ( Nominal nominal : Nominal.values() ) {
                this.safe.getCells().add( new CellImpl( UUID.randomUUID().toString(), nominal, 0 , this) );
            }
        }
        sortCells();
    }


    private String readIniFile( String fileName ) throws IOException {
        File file = new File( fileName );

        if ( bufferedReader == null ) {
            bufferedReader = new BufferedReader( new FileReader( file ) );
        }
        String line;
        StringBuilder sb = new StringBuilder(  );
        while ( ( line = bufferedReader.readLine() ) != null ) {
            sb.append( line );
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

        public static ATMImpl buildFromFile( String fileName ) {
            ATMImpl atm = new ATMImpl();

            try {
                atm.loadFromFile( fileName );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return atm;
        }
    }

    private void setInitialCells() {
        List<Cell> initialCellList = new ArrayList<>();
        for (Nominal nominal : Nominal.values()){
            initialCellList.add(new CellImpl(UUID.randomUUID().toString(),nominal,0,this));
        }
        safe.setCells(initialCellList);

    }

    private void sortCells() {
//        Collections.sort( safe.getCells(), (Comparator<Cell>) (o1, o2) -> o2.getNominal().getNominal().compareTo( o1.getNominal().getNominal() ));
        safe.getCells().sort(Comparator.naturalOrder());
//        balanceables = safe.getCells().stream().collect( Collectors.toList());

    }}
