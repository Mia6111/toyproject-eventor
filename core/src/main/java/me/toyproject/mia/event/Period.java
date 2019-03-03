package me.toyproject.mia.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Objects;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Period {

    @Column(name="start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Column(name="end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    public Period(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("startDate가 endDate보다 먼저여야 합니다");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isOngoing(LocalDateTime now) {
        return now.isAfter(startDate) && now.isBefore(endDate);
    }

    boolean isBeforeOtherPeriodEnd(Period otherPeriod) {
        return this.endDate.isBefore(otherPeriod.endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return Objects.equal(startDate, period.startDate) &&
                Objects.equal(endDate, period.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(startDate, endDate);
    }
}
