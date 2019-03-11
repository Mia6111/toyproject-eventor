package me.toyproject.mia.service;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import me.toyproject.mia.event.EventDetailDto;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventModifyEvent extends ApplicationEvent {

	private EventDetailDto originalEvent;
	private EventDetailDto modifiedEvent;
	private Set<Long> enrolledGuestIds;

	@Builder
	public EventModifyEvent(Object source, EventDetailDto originalEvent, EventDetailDto modifiedEvent, Set<Long> enrolledGuestIds) {
		super(source);
		this.modifiedEvent = modifiedEvent;
		this.originalEvent = originalEvent;
		this.enrolledGuestIds = enrolledGuestIds;
	}
}
