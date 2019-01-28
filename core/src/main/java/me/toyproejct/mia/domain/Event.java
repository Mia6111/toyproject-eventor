package me.toyproejct.mia.domain;

import com.google.common.base.Objects;
import lombok.*;
import me.toyproejct.mia.exception.NotAuthorizedUserException;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table
@NoArgsConstructor @Getter @Setter(AccessLevel.PRIVATE)
@ToString
public class Event {
    public static final Duration MAX_DURATION_BETWEEN_REGISTER_AND_OPEN_PERIOD = Duration.ofDays(30);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(column = @Column(name = "register_start_date"), name = "startDate"),
            @AttributeOverride(column = @Column(name = "register_end_date"), name = "endDate")
    })
    @NotNull
    private Period registerOpenPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(column = @Column(name = "event_start_date"), name = "startDate"),
            @AttributeOverride(column = @Column(name = "event_end_date"), name = "endDate")
    })
    @NotNull
    private Period eventOpenPriod;

    @Column(nullable = false)
    @Min(0) @Max(100)
    private int maxPeopleCnt = 0;

    @Column(nullable = false)
    @Min(0) @Max(200000)
    private long price = 0L;

    @NotEmpty
    private String location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="account_id", referencedColumnName = "id")
    private Account host;

    private Set<Long> enrolledGuestIds= new HashSet();

    @Builder
    public Event(Long id,@NonNull String title, @NonNull String content, @NonNull Period registerOpenPeriod, @NonNull Period eventOpenPriod, int maxPeopleCnt, long price,@NonNull String location, @NonNull Account host) {
        if(eventOpenPriod.isBeforeOtherPeriodEnd(registerOpenPeriod)){
            throw new IllegalArgumentException("eventOpenPriod 끝 시간은 registerOpenPeriod 시작 시간보다 먼저여야 합니다");
        }
        if(MAX_DURATION_BETWEEN_REGISTER_AND_OPEN_PERIOD.compareTo((eventOpenPriod.diffDuration(registerOpenPeriod))) == -1){
            throw new IllegalArgumentException("등록시간을 기준으로 이벤트는 1달(포함)안에 시작이 되어야 합니다");
        }
        if(price < 0 || price > 200000){
            throw new IllegalArgumentException("price는 0 - 200000 사이여야 합니다");
        }
        if(maxPeopleCnt < 0 || maxPeopleCnt > 100){
            throw new IllegalArgumentException("maxPeopleCnt는 0 - 100명 사이여야 합니다");
        }
        this.id = id;
        this.title = title;
        this.content = content;
        this.registerOpenPeriod = registerOpenPeriod;
        this.eventOpenPriod = eventOpenPriod;
        this.maxPeopleCnt = maxPeopleCnt;
        this.price = price;
        this.location = location;
        this.host = host;
    }

    public boolean isRegisterOpen() {
        return registerOpenPeriod.isOngoing(LocalDateTime.now());
    }

    public void update(Account host, Event event){
        Assert.notNull(host, "host는 필수입니다");
        Assert.notNull(event, "event는 필수입니다");

        //invariant
        if(!this.host.isSameHost(host)){
            throw new NotAuthorizedUserException( "권한이 없는 유저입니다");
        }
        if(LocalDateTime.now().compareTo(this.registerOpenPeriod.getStartDate().minusDays(7)) == 1) {
            throw new IllegalStateException("이벤트는 시작시간보다 최소 1주일 전에만 수정 가능");
        }

        final int enrolledGuestCnt = this.enrolledGuestIds.size();

        if(enrolledGuestCnt > 0 && this.price != event.price){
            throw new IllegalStateException("1명이라도 등록을 했다면 가격은 변동할 수 없다.");
        }
        if(enrolledGuestCnt > event.maxPeopleCnt){
            throw new IllegalStateException("참석가능인원은 현재 참여자수 보다 줄일 수 없습니다.");
        }
        // fields not to change
//        event.id = this.id;
//        event.host = this.host;
//        BeanUtils.copyProperties(event, this);
        this.title = event.title;
        this.content = event.content;
        this.registerOpenPeriod = event.registerOpenPeriod;
        this.eventOpenPriod = event.eventOpenPriod;
        this.maxPeopleCnt = event.maxPeopleCnt;
        this.price = event.price;
        this.location = event.location;
    }

    public boolean enroll(Long accountId){
        if(this.enrolledGuestIds.contains(accountId)){
            return false;
        }
        this.enrolledGuestIds.add(accountId);
        return true;
    }
}
