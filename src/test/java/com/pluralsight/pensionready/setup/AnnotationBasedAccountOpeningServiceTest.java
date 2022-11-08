package com.pluralsight.pensionready.setup;

import com.pluralsight.pensionready.AccountRepository;
import com.pluralsight.pensionready.report.GovernmentDataPublisher;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static com.pluralsight.pensionready.setup.AccountOpeningServiceTest.*;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(EasyMockExtension.class)
class AnnotationBasedAccountOpeningServiceTest {
    public static final String ACCOUNT_ID = "VALID ID";
    public static final BackgroundCheckResults ACCEPTABE_BACKGROUD_CHECK_RESULTS = new BackgroundCheckResults("Accepted risk", 500000);
    public static final String exceptionMessage = "RUN TIME EXCEPTION";
    public static final String governmentExceptionMessage = "government exception thrown";
    @TestSubject
    private AccountOpeningService underTest = new AccountOpeningService();
    @Mock
    private BackgroundCheckService backgroundCheckService;
    @Mock
    private ReferenceIdsManager referenceIdsManager;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private GovernmentDataPublisher governmentDataPublisher;

    @Test
    public void shouldDeclineAccountOpeningIfBackgroundCheckResultsRiskProfileUnacceptable() throws IOException {
        expect(backgroundCheckService.confirm(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB
        )).andReturn(new BackgroundCheckResults(AccountOpeningService.UNACCEPTABLE_RISK_PROFILE, 0));
        replay(backgroundCheckService, referenceIdsManager, accountRepository);
        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB);
        assertEquals(AccountOpeningStatus.DECLINED, accountOpeningStatus);
        verify(backgroundCheckService, referenceIdsManager, accountRepository);
    }

    @Test
    public void shouldOpenAccount() throws IOException {
        expect(backgroundCheckService.confirm(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB
        )).andReturn(ACCEPTABE_BACKGROUD_CHECK_RESULTS);
        expect(referenceIdsManager.obtainId(
                eq(FIRST_NAME),
                eq(LAST_NAME),
                anyString(),
                eq(TAX_ID),
                eq(DOB)
        )).andReturn(ACCOUNT_ID);
        expect(accountRepository.save(
                eq(ACCOUNT_ID),
                eq(FIRST_NAME),
                eq(LAST_NAME),
                eq(TAX_ID),
                eq(DOB),
                same(ACCEPTABE_BACKGROUD_CHECK_RESULTS)
        )).andReturn(true);
        governmentDataPublisher.publishAccountOpeningEvent(ACCOUNT_ID);

        replay(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);
        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB
        );
        assertEquals(AccountOpeningStatus.OPENED, accountOpeningStatus);
        verify(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);
    }

    @Test
    public void shouldThroughIfReferenceIdsManagerThrows() throws IOException {
        expect(backgroundCheckService.confirm(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB
        )).andReturn(ACCEPTABE_BACKGROUD_CHECK_RESULTS);

        expect(referenceIdsManager.obtainId(
                eq(FIRST_NAME),
                eq(LAST_NAME),
                anyString(),
                eq(TAX_ID),
                eq(DOB)
        )).andThrow(new RuntimeException(exceptionMessage));
        replay(backgroundCheckService, referenceIdsManager);
        final RuntimeException thrown = assertThrows(RuntimeException.class, () -> underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB
        ));
        assertEquals(exceptionMessage, thrown.getMessage());
    }

    @Test
    public void shouldThrowIfGovernmentDataPublisherThrows() throws IOException {
        expect(backgroundCheckService.confirm(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB
        )).andReturn(ACCEPTABE_BACKGROUD_CHECK_RESULTS);
        expect(referenceIdsManager.obtainId(
                eq(FIRST_NAME),
                anyString(),
                eq(LAST_NAME),
                eq(TAX_ID),
                eq(DOB)
        )).andReturn(ACCOUNT_ID);
        expect(accountRepository.save(
                eq(ACCOUNT_ID),
                eq(FIRST_NAME),
                eq(LAST_NAME),
                eq(TAX_ID),
                eq(DOB),
                same(ACCEPTABE_BACKGROUD_CHECK_RESULTS)
        )).andReturn(true);
        governmentDataPublisher.publishAccountOpeningEvent(ACCOUNT_ID);
        expectLastCall().andThrow(new RuntimeException(governmentExceptionMessage));
        replay(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);

        final RuntimeException actualThrown = assertThrows(RuntimeException.class, () -> underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB
        ));

        assertEquals(governmentExceptionMessage, actualThrown.getMessage());
        verify(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);
    }
}