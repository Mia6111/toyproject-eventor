package me.toyproejct.mia;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import lombok.Data;
import lombok.ToString;
import ma.glasnost.orika.MapperFacade;
import me.toyproejct.mia.domain.Account;
import me.toyproejct.mia.domain.Event;
import me.toyproejct.mia.domain.Period;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@Data @ToString
public class EventDto {
    private Long id;
    private String title;
    private String content;
    private Period registerOpenPeriod;
    private Period eventOpenPriod;
    private int maxPeopleCnt;
    private int enrolledPeopleCnt;
    private long price;
    private String location;

    public boolean isRegisterOpen() {
        return registerOpenPeriod.isOngoing(LocalDateTime.now());
    }

    public Event toDomain(){
        return Event.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .maxPeopleCnt(this.maxPeopleCnt)
                .eventOpenPriod(this.eventOpenPriod)
                .registerOpenPeriod(this.registerOpenPeriod)
                .price(this.price)
                .location(this.location)
                .host(new Account())
                .build();
    }
}
