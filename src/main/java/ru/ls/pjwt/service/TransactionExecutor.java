package ru.ls.pjwt.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class TransactionExecutor {
    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate template = new TransactionTemplate();

    @PostConstruct
    private void initTransactionTemplate() {
        template.setTransactionManager(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.setTimeout(30);
    }

    public void executeInIndependentTransaction(Runnable runnable) {
        template.execute(status -> {
            runnable.run();
            return null;
        });
    }
}
