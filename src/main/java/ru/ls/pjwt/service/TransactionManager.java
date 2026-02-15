package ru.ls.pjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class TransactionManager {
    private final PlatformTransactionManager transactionManager;

    private TransactionTemplate getNewTransactionTemplate() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.setTimeout(30);
        return template;
    }

    public void executeInNonRollbackableTransaction(Runnable runnable) {
        getNewTransactionTemplate().execute(status -> {
            runnable.run();
            return null;
        });
    }
}
