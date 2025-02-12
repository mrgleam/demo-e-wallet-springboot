package com.planktonsoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PocketServiceTest {
    @Mock
    private PocketRepository pocketRepository;

    @Spy
    private TryObjectMapper objectMapper;

    @InjectMocks
    private PocketService pocketService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenCreateWallet_thenControlFlowAsExpected() {
        String testMsg = "{\"userId\": \"100\", \"phoneNumber\": \"+1234567890\", \"userIdentifier\": \"NATIONAL_ID\", \"identifierValue\": \"some-unique-id-123\"}";
        when(objectMapper.tryReadValue(testMsg, UserMsg.class)).thenCallRealMethod();
        when(pocketRepository.save(any(Pocket.class)))
                .thenReturn(new Pocket());

        pocketService.createWallet(testMsg);

        verify(pocketRepository, Mockito.times(1))
                .save(any(Pocket.class));
    }
}
