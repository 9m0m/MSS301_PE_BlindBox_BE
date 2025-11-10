package com.mss301.msaccount_se184531.config;

import com.mss301.msaccount_se184531.entity.SystemAccount;
import com.mss301.msaccount_se184531.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SystemAccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (accountRepository.count() == 0) {
            System.out.println("Initializing SystemAccounts data...");

            // Admin user
            SystemAccount admin = new SystemAccount();
            admin.setUsername("adminsys");
            admin.setEmail("admin@blindboxes.vn");
            admin.setPassword("@2");
            admin.setRole(1);
            admin.setIsActive(true);
            accountRepository.save(admin);

            // Regular user 1
            SystemAccount user1 = new SystemAccount();
            user1.setUsername("johndoe");
            user1.setEmail("john@blindboxes.vn");
            user1.setPassword("@2");
            user1.setRole(4);
            user1.setIsActive(true);
            accountRepository.save(user1);

            // Moderator
            SystemAccount mod = new SystemAccount();
            mod.setUsername("modmichel");
            mod.setEmail("michel@blindboxes.vn");
            mod.setPassword("@2");
            mod.setRole(2);
            mod.setIsActive(true);
            accountRepository.save(mod);

            // Inactive user
            SystemAccount inactive = new SystemAccount();
            inactive.setUsername("vanvan");
            inactive.setEmail("vanavan@blindboxes.vn");
            inactive.setPassword("@2");
            inactive.setRole(4);
            inactive.setIsActive(false);
            accountRepository.save(inactive);

            // DevOps user
            SystemAccount devops = new SystemAccount();
            devops.setUsername("devops");
            devops.setEmail("dev@blindboxes.vn");
            devops.setPassword("@2");
            devops.setRole(3);
            devops.setIsActive(true);
            accountRepository.save(devops);

            System.out.println("Successfully initialized 5 SystemAccounts");
        } else {
            System.out.println("SystemAccounts already exists, skipping initialization");
        }
    }
}
