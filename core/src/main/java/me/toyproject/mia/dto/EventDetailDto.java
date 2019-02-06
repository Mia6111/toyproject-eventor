package me.toyproject.mia.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.toyproject.mia.domain.Account;
import me.toyproject.mia.domain.Event;
import me.toyproject.mia.domain.Period;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class EventDetailDto {
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

    private HostDto hostDto;

    public EventDetailDto(Long eventId, EventDto eventDto, HostDto hostDto) {
        this.id = eventId;
        this.hostDto = hostDto;
        this.title = eventDto.getTitle();
        this.content = eventDto.getContent();
        this.registerOpenPeriod = eventDto.getRegisterOpenPeriod();
        this.eventOpenPriod = eventDto.getEventOpenPriod();
        this.maxPeopleCnt = eventDto.getMaxPeopleCnt();
        this.enrolledPeopleCnt = eventDto.getEnrolledPeopleCnt();
        this.price = eventDto.getPrice();
        this.location = eventDto.getLocation();
    }


    public boolean isRegisterOpen() {
        return registerOpenPeriod.isOngoing(LocalDateTime.now());
    }

    public Event toDomain(Account host) {
        return Event.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .maxPeopleCnt(this.maxPeopleCnt)
                .eventOpenPriod(this.eventOpenPriod)
                .registerOpenPeriod(this.registerOpenPeriod)
                .price(this.price)
                .location(this.location)
                .host(host)
                .build();
    }
}
