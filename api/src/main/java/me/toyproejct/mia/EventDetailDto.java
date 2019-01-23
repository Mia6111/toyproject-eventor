package me.toyproejct.mia;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.toyproejct.mia.domain.Event;

@NoArgsConstructor @Getter @Setter
public class EventDetailDto {
    private Long eventId;
    private EventDto eventDto;
    private HostDto hostDto;

    public EventDetailDto(Long eventId, EventDto eventDto, HostDto hostDto) {
        this.eventId = eventId;
        this.eventDto = eventDto;
        this.hostDto = hostDto;
    }
}
