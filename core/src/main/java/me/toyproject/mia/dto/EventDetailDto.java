package me.toyproject.mia.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
