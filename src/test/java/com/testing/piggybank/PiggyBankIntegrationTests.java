package com.testing.piggybank;

import com.testing.piggybank.account.AccountService;
import com.testing.piggybank.model.Account;
import com.testing.piggybank.model.Currency;
import com.testing.piggybank.model.Direction;
import com.testing.piggybank.transaction.CreateTransactionRequest;
import com.testing.piggybank.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PiggyBankIntegrationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Test
    @Transactional
    public void testAccountRetrieval() {
        long accountId = 1;
        Optional<Account> retrievedAccount = accountService.getAccount(accountId);
        assertTrue(retrievedAccount.isPresent());
    }

    @Test
    @Transactional
    public void testAccountBalanceUpdate() {
        long accountId = 1;
        BigDecimal amountToAdd = new BigDecimal("100.00");
        accountService.updateBalance(accountId, amountToAdd, Direction.DEBIT);

        Account updatedAccount = accountService.getAccount(accountId).orElseThrow();
        assertEquals(0, updatedAccount.getBalance().compareTo(new BigDecimal("1268.64")));
    }

    @Test
    @Transactional
    public void testTransactionCreationAndUpdateBalances() {
        long senderId = 1;
        long receiverId = 2;

        BigDecimal transactionAmount = new BigDecimal("100.00");

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setSenderAccountId(senderId);
        request.setReceiverAccountId(receiverId);
        request.setAmount(transactionAmount);
        request.setCurrency(Currency.valueOf("EUR"));

        transactionService.createTransaction(request);

        Account senderAccount = accountService.getAccount(senderId).orElseThrow();
        Account receiverAccount = accountService.getAccount(receiverId).orElseThrow();

        assertEquals(0, senderAccount.getBalance().compareTo(new BigDecimal("1068.64")));
        assertEquals(0, receiverAccount.getBalance().compareTo(new BigDecimal("1068.65")));
    }
}