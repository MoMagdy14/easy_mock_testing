package com.pluralsight.pensionready.setup;

import java.time.LocalDate;

public interface ReferenceIdsManager {
    String obtainId(String firstName, String lastName,String middleName, String taxId, LocalDate dob);
}
