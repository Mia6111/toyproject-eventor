package me.toyproject.mia.domain;

import me.toyproejct.mia.CoreApplicatoin;
import me.toyproejct.mia.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreApplicatoin.class)
public class EventRepositoryTest {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Test
    public void test_create(){

        Account account = accountRepository.save(Account.builder().email("abcdef@aa.com").name("테스트야").build());
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

        logger.debug("event {}", e);
    }

    @Test
    public void test_findAllWhereRegisterOpenPeriod(){
        List<Event> events = eventRepository.findAllRegisterOpenBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now());
    }

    @Test
    public void test_findAllRegisterOpenNow(){
        Page<Event> events = eventRepository.findAllRegisterOpenNow(PageRequest.of(0, 20));
    }
}