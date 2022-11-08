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
        expect(governmentDataPublisher.publishAccountOpeningEvent(ACCOUNT_ID)).andReturn(true);
        replay(backgroundCheckService, referenceIdsManager, accountRepository, governmentDataPublisher);
        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB
        );
        assertEquals(AccountOpeningStatus.OPENED, accountOpeningStatus);
    }
}