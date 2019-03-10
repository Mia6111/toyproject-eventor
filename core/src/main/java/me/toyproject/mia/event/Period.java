package me.toyproject.mia.event;

import javax.validation.constraints.AssertTrue;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@EqualsAndHashCode(of = {"startDate", "endDate"})
public class Period {
    private static final String COMMON_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    @Column(name="start_date")
    @DateTimeFormat(pattern = COMMON_DATE_TIME)
    private LocalDateTime startDate;

    @Column(name="end_date")
    @DateTimeFormat(pattern = COMMON_DATE_TIME)
    private LocalDateTime endDate;

    public Period(LocalDateTime startDate, LocalDateTime endDate) {
	    if(!isStartBeforeOrEqualToEndDate(startDate, endDate)){
		    throw new IllegalArgumentException("startDate가 endDate보다 먼저여야 합니다");
	    }
	    this.startDate = startDate;
        this.endDate = endDate;
    }

    @AssertTrue(message = "startDate가 endDate보다 먼저여야 합니다")
	private boolean isStartBeforeOrEqualToEndDate(LocalDateTime startDate, LocalDateTime endDate) {
		return startDate.isBefore(endDate) || startDate.isEqual(endDate);

	}
	public boolean isOngoing(LocalDateTime now) {
        return now.isAfter(startDate) && now.isBefore(endDate);
    }

    boolean isBeforeOtherPeriodEnd(Period otherPeriod) {
        return this.endDate.isBefore(otherPeriod.endDate);
    }

}
