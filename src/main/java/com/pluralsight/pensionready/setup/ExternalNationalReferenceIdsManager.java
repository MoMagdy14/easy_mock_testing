package com.pluralsight.pensionready.setup;

import java.time.LocalDate;

public class ExternalNationalReferenceIdsManager implements ReferenceIdsManager {

    @Override
    public String obtainId(String firstName, String lastName, String middleName, String taxId, LocalDate dob) {
        //implementation not relevant for this course module
        return null;
    }
}
