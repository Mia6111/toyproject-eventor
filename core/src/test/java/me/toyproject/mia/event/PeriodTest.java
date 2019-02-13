package me.toyproject.mia.event;

import me.toyproject.mia.event.Period;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PeriodTest {
    @Test
    public void 기간_생성_성공(){
       Period p = new Period(LocalDateTime.now().minus(Duration.ofDays(1)), LocalDateTime.now().plus(Duration.ofDays(1)));
       assertThat(p.getStartDate()).isBefore(p.getEndDate());
    }
    @Test(expected = IllegalArgumentException.class)
    public void 기간_생성_실패(){
        new Period(LocalDateTime.now(), LocalDateTime.now().minus(Duration.ofMinutes(1)));
    }

}
