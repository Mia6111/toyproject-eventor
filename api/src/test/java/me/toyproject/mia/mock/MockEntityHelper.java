package me.toyproject.mia.mock;

import lombok.RequiredArgsConstructor;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountRepository;
import me.toyproject.mia.event.Event;
import me.toyproject.mia.event.EventRepository;
import me.toyproject.mia.event.Period;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class MockEntityHelper {
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    public Event mockEvent(Account host){
        Period register = new Period(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(15));
        Period open = new Period(LocalDateTime.now().plusDays(20), LocalDateTime.now().plusDays(25));
        Event e = Event.builder()
                .title("TITLE")
                .content("CONTENT")
                .maxPeopleCnt(10)
                .eventOpenPriod(open)
                .registerOpenPeriod(register)
                .price(1000)
                .location("SOMEWHERE")
                .host(host)
                .build();
        return eventRepository.save(e);
    }
    public Account mockAccount() {
        Account a = Account.builder().email("mia@test.com").name("mia").password("PASS").passwordEncoder(new BCryptPasswordEncoder() {
        }).build();
        return accountRepository.save(a);
    }

}
