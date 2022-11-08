package com.pluralsight.pensionready.setup;

import com.pluralsight.pensionready.AccountRepository;
import com.pluralsight.pensionready.report.GovernmentDataPublisher;
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
    private GovernmentDataPublisher governmentDataPublisher = mock(GovernmentDataPublisher.class);

    @Test
    public void shouldDeclineAccountOpeningWhenBackgroundCheckingAreNull() throws IOException {
        underTest = new AccountOpeningService(
                backgroundCheckService,
                referenceIdsManager,
                accountRepository,
                governmentDataPublisher);
        expect(backgroundCheckService.confirm(FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB))
                .andReturn(null);
        replay(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);
        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB);
        assertEquals(AccountOpeningStatus.DECLINED, accountOpeningStatus);
        verify(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);
    }

    @Test
    public void shouldDeclineAccountOpeningWhenBackgroundCheckingAreNull2() throws IOException {
        underTest = new AccountOpeningService(
                niceBackgroundCheckService,
                referenceIdsManager,
                accountRepository,
                governmentDataPublisher);
        expect(backgroundCheckService.confirm(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB)).andReturn(null);
        replay(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);
        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB);
        assertEquals(AccountOpeningStatus.DECLINED, accountOpeningStatus);
        verify(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);
    }
}