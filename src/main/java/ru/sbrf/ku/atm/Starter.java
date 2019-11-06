package ru.sbrf.ku.atm;

import ru.sbrf.ku.atm.atm.ATM;
import ru.sbrf.ku.atm.atm.impl.ATMImpl;
import ru.sbrf.ku.atm.vm.ViewModel;
import ru.sbrf.ku.atm.vm.impl.ViewModelImpl;

import java.io.IOException;

public class Starter {
    private String FILE_NAME;

    public static void main( String args[] ) throws IOException {
        Starter starter = new Starter();
        starter.FILE_NAME = args[ 0 ];
        starter.startAtm();
    }


    private void startAtm() {
        ATM atm = new ATMImpl();
        ViewModel atmViewModel = new ViewModelImpl(atm);
        atmViewModel.startClientInteraction();
    }
}