package me.toyproject.mia.event;

import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.CoreApplicatoin;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreApplicatoin.class)
@Slf4j
@ActiveProfiles("core")
public class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AccountRepository accountRepository;

	@Test
    public void test_create(){

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

        log.debug("event => {}", e);

    }

    @Test
    public void test_findAllWhereRegisterOpenPeriod(){
        List<Event> events = eventRepository.findAllRegisterOpenBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        log.debug("events => {}", events);
    }

    @Test
    public void test_findAllRegisterOpenNow(){
        Page<Event> events = eventRepository.findAllRegisterOpenNow(LocalDateTime.now(), PageRequest.of(0, 20));
        log.debug("events => {}", events);
    }
}
