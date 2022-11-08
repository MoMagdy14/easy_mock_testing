package com.pluralsight.pensionready.setup;

import com.pluralsight.pensionready.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountOpeningServiceTest {

    public static final String FIRST_NAME = "Mohamed";
    public static final String LAST_NAME = "Magdy";
    public static final String TAX_ID = "1";
    public static final LocalDate DOB = LocalDate.of(2000, 8, 14);
    public static final String FIRST_NAME1 = "Mohamed";
    private AccountOpeningService underTest;
    private BackgroundCheckService backgroundCheckService = mock(BackgroundCheckService.class);
    private BackgroundCheckService niceBackgroundCheckService = niceMock(BackgroundCheckService.class);
    private ReferenceIdsManager referenceIdsManager = mock(ReferenceIdsManager.class);
    private AccountRepository accountRepository = mock(AccountRepository.class);

    @Test
    public void shouldDeclineAccountOpening() throws IOException {
        underTest = new AccountOpeningService(
                backgroundCheckService,
                referenceIdsManager,
                accountRepository);
        expect(backgroundCheckService.confirm("Mohamed",
                "Magdy",
                "1",
                LocalDate.of(2000, 8, 14)))
                .andReturn(null);
        replay(backgroundCheckService, referenceIdsManager, accountRepository);
        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB);
        assertEquals(AccountOpeningStatus.DECLINED, accountOpeningStatus);
    }

    @Test
    public void shouldDeclineAccountOpening2() throws IOException {
        underTest = new AccountOpeningService(
                niceBackgroundCheckService,
                referenceIdsManager,
                accountRepository);

        replay(backgroundCheckService, referenceIdsManager, accountRepository);
        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB);
        assertEquals(AccountOpeningStatus.DECLINED, accountOpeningStatus);
    }
}