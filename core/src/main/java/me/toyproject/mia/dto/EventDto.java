package me.toyproject.mia.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown=true)
public class EventDto {
    @JsonIgnore
    private Long id;
    private String title;
    private String content;
    private Period registerOpenPeriod;
    private Period eventOpenPriod;
    private int maxPeopleCnt;
    private int enrolledPeopleCnt;
    private int price;
    private String location;

    public boolean isRegisterOpen() {
        return registerOpenPeriod.isOngoing(LocalDateTime.now());
    }

    public Event toDomain(Account account){
        return Event.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .maxPeopleCnt(this.maxPeopleCnt)
                .eventOpenPriod(this.eventOpenPriod)
                .registerOpenPeriod(this.registerOpenPeriod)
                .price(this.price)
                .location(this.location)
                .host(account)
                .build();
    }
}
