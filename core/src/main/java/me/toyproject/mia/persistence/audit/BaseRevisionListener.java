package me.toyproject.mia.persistence.audit;

import me.toyproject.mia.persistence.AuthByAccount;
import me.toyproject.mia.persistence.AuthFinder;
import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseRevisionListener implements RevisionListener {
    @Autowired private AuthFinder authFinder;
    @Override
    public void newRevision(Object revisionEntity) {
        BaseRevEntity revEntity = (BaseRevEntity) revisionEntity;
        AuthByAccount authByAccount = authFinder.getAuth();
        revEntity.setEmail( authByAccount.getAccount().getEmail());
    }
}
