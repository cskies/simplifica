package com.simplifica.scheduler;

import com.simplifica.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class QuotaResetScheduler {
    private final UserRepository userRepository;

    // Every month on the 1st at 00:00
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyQuota() {
        log.info("Resetting monthly quotas for all users");
        userRepository.findAll().forEach(user -> {
            user.resetMonthlyQuota();
            userRepository.save(user);
        });
        log.info("Monthly quotas reset completed");
    }
}
