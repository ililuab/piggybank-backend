package com.testing.piggybank;

import com.testing.piggybank.helper.CurrencyConverterService;
import com.testing.piggybank.model.*;
import com.testing.piggybank.transaction.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.testing.piggybank.account.AccountService;
class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrencyConverterService converterService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(accountService.getAccount(anyLong())).thenReturn(Optional.of(new Account()));
    }

    @Test
    void testGetTransactions() {
        List<Transaction> mockTransactions = new ArrayList<>();
        Account senderAccount = new Account();
        Account receiverAccount = new Account();
        mockTransactions.add(createTransaction(senderAccount, receiverAccount));
        when(transactionRepository.findAll()).thenReturn(mockTransactions);
        List<Transaction> result = transactionService.getTransactions(1, senderAccount.getId());
        verify(transactionRepository, times(1)).findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFilterAndLimitTransactions() {
        List<Transaction> mockTransactions = new ArrayList<>();
        Account senderAccount = new Account();
        Account receiverAccount = new Account();
        mockTransactions.add(createTransaction(senderAccount, receiverAccount));
        List<Transaction> result = transactionService.filterAndLimitTransactions(mockTransactions, senderAccount.getId(), 1);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateTransaction() {
        CreateTransactionRequest request = createTransactionRequest();
        when(converterService.toEuro(any(), any())).thenReturn(request.getAmount());
        transactionService.createTransaction(request);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    private CreateTransactionRequest createTransactionRequest() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setSenderAccountId(1);
        request.setReceiverAccountId(2);
        request.setDescription("test transaction");
        request.setAmount(BigDecimal.TEN);
        request.setCurrency(Currency.EUR);
        return request;
    }

    private Transaction createTransaction(Account senderAccount, Account receiverAccount) {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setCurrency(Currency.EUR);
        transaction.setDescription("test transaction");
        transaction.setDateTime(Instant.now());
        return transaction;
    }
}
