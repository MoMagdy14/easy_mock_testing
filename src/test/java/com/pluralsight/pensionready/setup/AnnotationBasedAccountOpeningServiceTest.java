package com.pluralsight.pensionready.setup;

import com.pluralsight.pensionready.AccountRepository;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static com.pluralsight.pensionready.setup.AccountOpeningServiceTest.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(EasyMockExtension.class)
class AnnotationBasedAccountOpeningServiceTest {
    @TestSubject
    private AccountOpeningService underTest = new AccountOpeningService();
    @Mock
    private BackgroundCheckService backgroundCheckService;
    @Mock
    private ReferenceIdsManager referenceIdsManager;
    @Mock
    private AccountRepository accountRepository;

    @Test
    public void shouldDeclineAccountOpeningIfBackgroundCheckResultsRiskProfileUnacceptable() throws IOException {
        expect(backgroundCheckService.confirm(
                AccountOpeningServiceTest.FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB)).andReturn(new BackgroundCheckResults(AccountOpeningService.UNACCEPTABLE_RISK_PROFILE, 0));
        replay(backgroundCheckService, referenceIdsManager, accountRepository);
        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB);
        assertEquals(AccountOpeningStatus.DECLINED, accountOpeningStatus);
    }
}