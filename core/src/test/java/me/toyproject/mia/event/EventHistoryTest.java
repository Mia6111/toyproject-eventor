package me.toyproject.mia.event;

import com.fasterxml.jackson.annotation.JacksonInject;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.CoreApplicatoin;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountRepository;
import me.toyproject.mia.config.CoreTestConfiguration;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditQuery;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreApplicatoin.class)
@Import(CoreTestConfiguration.class)
@Slf4j
@ActiveProfiles({"test"})
public class EventHistoryTest {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Test
    public void create() {
        log.debug("create");
        Account account =
                Account.builder().email("abcdef@aa.com").name("테스트야").password("PASS").mobile("01012341234").passwordEncoder(new BCryptPasswordEncoder()).build();
        account = accountRepository.save(account);
        Period register = new Period(LocalDateTime.now().plus(Duration.ofDays(1)), LocalDateTime.now().plus(Duration.ofDays(3)));
        Period open = new Period(LocalDateTime.now().plus(Duration.ofDays(3)), LocalDateTime.now().plus(Duration.ofDays(5)));

        Event e = Event.builder()
                .title("TITLE")
                .content("CONTENT")
                .maxPeopleCnt(10)
                .eventOpenPriod(open)
                .registerOpenPeriod(register)
                .price(1000)
                .location("SOMEWHERE")
                .host(account)
                .build();

        e = eventRepository.save(e);
        // log.debug("TestTransaction.isFlaggedForRollback() {}", TestTransaction.isFlaggedForRollback()); //true
        TestTransaction.flagForCommit();
        TestTransaction.end();
        // insert 1 defaultRevisionEntity, 1 account_aud, 1 event_aud (한 커밋 당 한번 history 생성되서 그렇군)

        TestTransaction.start();
        AuditReader reader = AuditReaderFactory.get(entityManager);
        AuditQuery query = reader.createQuery()
                .forRevisionsOfEntity(Event.class, false, false);

        //This return a list of array triplets of changes concerning the specified revision.
        // The array triplet contains the entity, entity revision information and at last the revision type.

        // -> event_aud, account_aud, event_guest_enrollments_AUD 모두 select
        List resultList = query.getResultList();
        Object[] objects = (Object[]) resultList.get(0);
//        log.debug("objects {} ", objects[0]); //saved Event Object
//        log.debug("objects {} ", objects); //saved Event Object
        assertThat(Event.class.cast(objects[0]).canEqual(e));

//        query = reader.createQuery()
//                .forEntitiesAtRevision(Event.class, 2);
//        Event event = Event.class.cast(query.getSingleResult());

        TestTransaction.end();

        //Rolling back JPA transaction on EntityManager
    }
}
