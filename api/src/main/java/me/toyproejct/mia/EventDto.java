package me.toyproejct.mia;

import lombok.Data;
import lombok.ToString;
import me.toyproejct.mia.domain.Period;

import java.time.LocalDateTime;

@Data @ToString
public class EventDto {
    private Long id;
    private String title;
    private String content;
    private Period registerOpenPeriod;
    private Period eventOpenPriod;
    private int maxPeopleCnt;
    private long price;
    private String location;

    public boolean isRegisterOpen() {
        return registerOpenPeriod.isOngoing(LocalDateTime.now());
    }
}
