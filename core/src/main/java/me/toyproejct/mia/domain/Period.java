package me.toyproejct.mia.domain;

import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Embeddable
@NoArgsConstructor
@Getter
@ToString
public class Period {

    @Column(name="start_date")
    private LocalDateTime startDate;

    @Column(name="end_date")
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

    public boolean isBefore(Period otherPeriod) {
        return this.endDate.isBefore(otherPeriod.startDate);
    }

    public Duration diffDuration(Period latter) {
        return Duration.between(this.endDate.toInstant(ZoneOffset.UTC), latter.startDate.toInstant(ZoneOffset.UTC));
    }
}
