package me.toyproejct.mia.domain;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

    @Builder
    public Event(Long id,@NonNull String title, @NonNull String content, @NonNull Period registerOpenPeriod, @NonNull Period eventOpenPriod, int maxPeopleCnt, long price,@NonNull String location, @NonNull Account host) {
        if(eventOpenPriod.isBefore(registerOpenPeriod)){
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

}
