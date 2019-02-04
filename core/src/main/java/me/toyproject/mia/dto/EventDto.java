package me.toyproject.mia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.domain.Account;
import me.toyproject.mia.domain.Event;
import me.toyproject.mia.domain.Period;

import java.time.LocalDateTime;
@Slf4j
@Getter @Setter
@ToString
public class EventDto {
    private Long id;
    private String title;
    private String content;
    private Period registerOpenPeriod;
    private Period eventOpenPriod;
    private int maxPeopleCnt;
    private int enrolledPeopleCnt;
    private int price;
    private String location;

    private int enrolledGuestCnt;

    public boolean isRegisterOpen() {
        return registerOpenPeriod.isOngoing(LocalDateTime.now());
    }

    public Event toDomain(){
        return Event.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .maxPeopleCnt(this.maxPeopleCnt)
                .eventOpenPriod(this.eventOpenPriod)
                .registerOpenPeriod(this.registerOpenPeriod)
                .price(this.price)
                .location(this.location)
                .host(new Account())
                .build();
    }
}
