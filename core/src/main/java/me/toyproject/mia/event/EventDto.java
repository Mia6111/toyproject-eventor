package me.toyproject.mia.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.event.Event;
import me.toyproject.mia.event.Period;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
@Slf4j
@Getter @Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class EventDto {
    @JsonIgnore
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @NotNull
    private Period registerOpenPeriod;

    @NotNull
    private Period eventOpenPriod;

    @Min(0) @Max(100)
    private int maxPeopleCnt;

    @Min(0) @Max(200000)
    private int price;

    @NotEmpty
    private String location;

    private int enrolledPeopleCnt;

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
    @AssertTrue
    private boolean isOpenEndBeforeRegisterEnd(){
        return !eventOpenPriod.isBeforeOtherPeriodEnd(registerOpenPeriod);
    }
    @AssertTrue
    private boolean isOpenStartInOneMonth(){
        return eventOpenPriod.getStartDate().compareTo(LocalDateTime.now().plusDays(Event.MAX_DURATION_BETWEEN_REGISTER_AND_OPEN_PERIOD)) <= 0;
    }
}
