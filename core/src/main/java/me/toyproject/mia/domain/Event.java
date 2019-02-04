package me.toyproject.mia.domain;

import lombok.*;
import me.toyproject.mia.exception.EventException;
import me.toyproject.mia.exception.NotAuthorizedUserException;
import org.hibernate.annotations.Where;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "event")
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter @Setter(AccessLevel.PRIVATE)
@ToString @EqualsAndHashCode(callSuper = false)
@Where(clause = "deleted = false")
public class Event extends AuditingEntity {
    static final Duration MAX_DURATION_BETWEEN_REGISTER_AND_OPEN_PERIOD = Duration.ofDays(30);
    static final int MIN_DAYS_BEFORE_UPDATE = 7;
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
    private int price = 0;

    @NotEmpty
    private String location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="account_id", referencedColumnName = "id")
    private Account host;

    @ElementCollection
    @CollectionTable(name="Enrollement", joinColumns = @JoinColumn(name="id", referencedColumnName = "eventId"))
    private Set<Long> enrolledGuestIds= new HashSet();

    @Builder
    public Event(Long id,@NonNull String title, @NonNull String content, @NonNull Period registerOpenPeriod, @NonNull Period eventOpenPriod, int maxPeopleCnt, int price, @NonNull String location, @NonNull Account host) {
        if(eventOpenPriod.isBeforeOtherPeriodEnd(registerOpenPeriod)){
            throw new EventException("eventOpenPriod 끝 시간은 registerOpenPeriod 끝 시간보다 먼저여야 합니다");
        }
        if(MAX_DURATION_BETWEEN_REGISTER_AND_OPEN_PERIOD.compareTo((eventOpenPriod.diffDuration(registerOpenPeriod))) == -1){
            throw new EventException("등록시간을 기준으로 이벤트는 1달(포함)안에 시작이 되어야 합니다");
        }
        if(price < 0 || price > 200000){
            throw new EventException("price는 0 - 200000 사이여야 합니다");
        }
        if(maxPeopleCnt < 0 || maxPeopleCnt > 100){
            throw new EventException("maxPeopleCnt는 0 - 100명 사이여야 합니다");
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

    public int getEnrolledGuestCnt() {
        return enrolledGuestIds.size();
    }

    public boolean isRegisterOpen() {
        return registerOpenPeriod.isOngoing(LocalDateTime.now());
    }

    public void update(Account host, Event event){
        Assert.notNull(host, "host는 필수입니다");
        Assert.notNull(event, "event는 필수입니다");

        //invariant
        if(!isHostedBy(host)){
            throw new NotAuthorizedUserException( "권한이 없는 유저입니다");
        }
        if(!isUpdatable()){
            throw new EventException("이벤트는 시작시간보다 최소 1주일 전에만 수정 가능");
        }

        changePrice(event.price);
        changeMaxPeopleCnt(event.maxPeopleCnt);
        changePeriod(event.registerOpenPeriod, event.eventOpenPriod);

        this.title = event.title;
        this.content = event.content;
        this.location = event.location;
    }


    private void changePeriod(Period registerOpenPeriod, Period eventOpenPriod) {
        //...
        this.registerOpenPeriod = registerOpenPeriod;
        this.eventOpenPriod = eventOpenPriod;
    }

    private void changeMaxPeopleCnt(int maxPeopleCnt) {
        if(this.enrolledGuestIds.size() > maxPeopleCnt){
            throw new EventException("참석가능인원은 현재 참여자수 보다 줄일 수 없습니다.");
        }
        this.maxPeopleCnt = maxPeopleCnt;
    }

    private void changePrice(int price) {
        if(this.enrolledGuestIds.size() > 0 && this.price != price){
            throw new EventException("1명이라도 등록을 했다면 가격은 변동할 수 없다.");
        }
        this.price = price;
    }

    boolean isUpdatable() {
        return this.eventOpenPriod != null &&
                LocalDateTime.now().compareTo(this.eventOpenPriod.getStartDate().minusDays(MIN_DAYS_BEFORE_UPDATE)) < 1 ;

    }

    public boolean isHostedBy(Account host) {
        return this.host != null && this.host.isSameHost(host);

    }

    public boolean enroll(Long accountId){
        if(this.enrolledGuestIds.contains(accountId)){
            return false;
        }
        this.enrolledGuestIds.add(accountId);
        return true;
    }

    boolean isDeletable() {
        if(this.enrolledGuestIds.size() > 0){
            return false;
        }
        return true;
    }

    public void delete() {
        if(!isDeletable()){
            throw new EventException("삭제할 수 없습니다");
        }
        super.delete();
    }
}
