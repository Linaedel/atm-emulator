package ru.sbrf.ku.atm;

import ru.sbrf.ku.atm.atm.ATM;
import ru.sbrf.ku.atm.atm.impl.ATMImpl;
import ru.sbrf.ku.atm.atm.ATMService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Starter {
    private ATM atm;
    private String FILE_NAME;

    public static void main( String args[] ) throws IOException {
        Starter starter = new Starter();
        starter.FILE_NAME = args[ 0 ];
        starter.startAtm();

        starter.startClientInteraction();
    }

    private void startClientInteraction() throws IOException {
        System.out.println( "Hello, my dear friend. What's your name?" );
        Scanner scanner = new Scanner( System.in );
        String name = scanner.nextLine();
        System.out.println( "Hello " + name + "! What's your command? (add, get, exit)" );
        String operation = scanner.nextLine();
        while ( ! operation.equalsIgnoreCase( "exit" ) ) {
            switch ( operation.toLowerCase() ) {
                case "add":
                    System.out.println( "Add all banknotes, separated with space, please" );
                    String value = scanner.nextLine();

                    Nominal nominal = Nominal.getNominalFromInt( value );
                    if ( nominal != null ) {
                        List<Nominal> nominals = new ArrayList<>();
                        nominals.add( nominal );
                        atm.putCash( nominals );
                        System.out.println( "Success" );
                    } else {
                        System.out.println( "Was is das?" );
                    }
                    break;
                case "get":
                    break;
                default:
                    System.out.println( "Incorrect command" );
            }

            System.out.println( " What's your next command? (add, get, exit)" );
            operation = scanner.nextLine();
        }
        ( (ATMService) atm ).saveToFile( FILE_NAME );

    }

    private List<Nominal> getBanknotesList (String incString) {

    }

    private void startAtm() {
        atm = new ATMImpl();
    }
}