package kz.iitu.spring_lab_01.web;

import kz.iitu.spring_lab_01.entity.Account;
import kz.iitu.spring_lab_01.repository.AccountRepository;
import kz.iitu.spring_lab_01.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public AccountController(
            AccountRepository accountRepository,
            AccountService accountService
    ) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public String transfer(
            @RequestParam Long fromId,
            @RequestParam Long toId,
            @RequestParam BigDecimal amount
    ) {
        accountService.transfer(fromId, toId, amount);
        return "Transfer completed successfully";
    }
}