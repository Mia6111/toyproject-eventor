package me.toyproject.mia;

import me.toyproject.mia.domain.Account;
import me.toyproject.mia.domain.Event;
import me.toyproject.mia.domain.Period;
import me.toyproject.mia.dto.EventDto;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class MockBuilder {
    public static Event constructEvent(Long id, String title) {
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
                .host(constructAccount("test@ama.ama"))

                .build();
    }
    public static Account constructAccount(String email) {
        return Account.builder().email(email).name("NAME").build();
    }
    public static EventDto createEventDtoFrom(Event originalEvent) {
        EventDto modifyEventDto = new EventDto();
        BeanUtils.copyProperties(originalEvent, modifyEventDto);
        return modifyEventDto;
    }

}
