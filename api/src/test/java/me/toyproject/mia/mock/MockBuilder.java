package me.toyproject.mia.mock;

import me.toyproject.mia.account.Account;
import me.toyproject.mia.event.Event;
import me.toyproject.mia.event.Period;
import me.toyproject.mia.event.EventDto;
import me.toyproject.mia.account.HostDto;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class MockBuilder {
    public static Event constructEvent(Long id, String title, Account account) {
        Period register = new Period(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(15));
        Period open = new Period(LocalDateTime.now().plusDays(20), LocalDateTime.now().plusDays(25));
        return Event.builder()
                .id(id)
                .title(title)
                .content("CONTENT")
                .maxPeopleCnt(10)
                .eventOpenPriod(open)
                .registerOpenPeriod(register)
                .price(1000)
                .location("SOMEWHERE")
                .host(account)
                .build();
    }
    public static Account constructAccount(String email) {
        return Account.builder().email(email).name("NAME").password("PASS").mobile("01012341234").passwordEncoder(new BCryptPasswordEncoder()).build();
    }
    public static EventDto createEventDtoFrom(Event originalEvent) {
        EventDto modifyEventDto = new EventDto();
        BeanUtils.copyProperties(originalEvent, modifyEventDto);
        return modifyEventDto;
    }
    public static HostDto createHostDtoFrom(Account originalAccount) {
        HostDto hostDto = new HostDto();
        BeanUtils.copyProperties(originalAccount, hostDto);
        return hostDto;
    }

}
