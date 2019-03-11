package me.toyproject.mia.event;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import lombok.*;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.exception.EventException;
import me.toyproject.mia.exception.NotAuthorizedUserException;
import me.toyproject.mia.persistence.AuditingEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Where;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "event")
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
@ToString @EqualsAndHashCode(callSuper = false, of = "id")
@Where(clause = "deleted = 0")
public class Event extends AuditingEntity {
    static final int MAX_DURATION_BETWEEN_REGISTER_AND_OPEN_PERIOD = 30;
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
    @NotNull @Valid
    private Period registerOpenPeriod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(column = @Column(name = "event_start_date"), name = "startDate"),
            @AttributeOverride(column = @Column(name = "event_end_date"), name = "endDate")
    })
    @NotNull @Valid
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
    @JoinColumn(name="host_account_id", referencedColumnName = "id")
    private Account host;

    @ElementCollection
    @CollectionTable(name="event_guest_enrollments", joinColumns = @JoinColumn(name="id"))
    private Set<Long> enrolledGuestIds = new HashSet<>();

    @Transient
    private EventStatus eventStatus;


    @PostConstruct @PostPersist
    private void initEventStatus(){
    	//todo : cancel 추가할것

	    if(registerOpenPeriod.isOngoing(LocalDateTime.now())) {
		    this.eventStatus = EventStatus.REGISTERING;
		    return;
	    }
	    if(registerOpenPeriod.getEndDate().isAfter(LocalDateTime.now())){
		    this.eventStatus = EventStatus.CLOSE_REGISTER;
		    return;
	    }
        if(eventOpenPriod.isOngoing(LocalDateTime.now())){
            this.eventStatus = EventStatus.OPENED;
            return;
        }
        if(eventOpenPriod.getEndDate().isAfter(LocalDateTime.now())){
	        this.eventStatus = EventStatus.FINISHED;
	        return;
        }
    }
    @Builder
    public Event(Long id, String title, String content, Period registerOpenPeriod, Period eventOpenPriod, int maxPeopleCnt, int price, String location, Account host) {
        //?
        if(!ObjectUtils.allNotNull(title, content, registerOpenPeriod, eventOpenPriod, maxPeopleCnt, price, location)){
            throw new EventException("필수 필드값 입력하십시오");
        }
        if(eventOpenPriod.isBeforeOtherPeriodEnd(registerOpenPeriod)){
            throw new EventException("eventOpenPriod 끝 시간은 registerOpenPeriod 끝 시간보다 먼저여야 합니다");
        }
        if(eventOpenPriod.getStartDate().compareTo(LocalDateTime.now().plusDays(MAX_DURATION_BETWEEN_REGISTER_AND_OPEN_PERIOD)) > 0){
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
//        return registerOpenPeriod.isOngoing(LocalDateTime.now());
	    return this.eventStatus == EventStatus.REGISTERING;
    }

    public void update(Event event){
        Assert.notNull(event, "event는 필수입니다");

        //invariant
        if(!isHostedBy(event.getHost())){
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

    boolean isHostedBy(Account host) {
        return this.host != null && this.host.isSameHost(host);

    }

    boolean isDeletable() {
        return this.enrolledGuestIds.size() == 0;
    }

    public void delete(Account account) {
        if(account == null || !isHostedBy(account)){
            throw new NotAuthorizedUserException("권한이 없는 사용자입니다");
        }
        if(!isDeletable()){
            throw new EventException("삭제할 수 없습니다");
        }
        super.delete();
    }
    // by entity id or entity?
//    public boolean enroll(Long accountId){
//        if(isRegisterAvaiable()){
//            return false;
//        }
//        if(this.enrolledGuestIds.contains(accountId)){
//            return false;
//        }
//        this.enrolledGuestIds.add(accountId);
//        return true;
//    }

    public boolean enroll(Account account){
        if(!isRegisterAvaiable()){
            return false;
        }
        if(enrolledGuestIds.contains(account.getId())){
            return false;
        }
        if(account.isGuest()){
        	return false;
        }
        this.enrolledGuestIds.add(account.getId());
        return true;
    }

    private boolean isRegisterAvaiable() {
        return this.eventStatus != EventStatus.CLOSE_REGISTER && enrolledGuestIds.size() < maxPeopleCnt;
    }
}
