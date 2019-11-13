package ru.sbrf.ku.atm;

import ru.sbrf.ku.atm.exceptions.ATMRuntimeException;

public enum Nominal {
    ONE_HUNDRED(100),
    TWO_HUNDREDS(200),
    FIVE_HUNDRED(500),
    ONE_THOUSAND(1000),
    TWO_THOUSANDS(2000),
    FIVE_THOUSANDS(5000);

    private Integer nominal;

    Nominal(Integer nominal) {
        this.nominal = nominal;
    }

    public Integer getNominal() {
        return nominal;
    }

    public static Nominal getNominalFromInt(Integer value) {
        if (value == null) {
            throw new ATMRuntimeException("Seems banknote reciever is empty. Please, add a banknote.");
        }
        Nominal currentNominal = null;
        for (Nominal nominal : values()) {
            if (nominal.nominal.equals(value)) {
                currentNominal = nominal;
            }
        }
        if (currentNominal == null) {
            throw new ATMRuntimeException("Sorry, " + value + " is not a valid banknote. Try another one.");
        }
        return currentNominal;
    }
}