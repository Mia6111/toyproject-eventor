package me.toyproject.mia.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.event.Event;
import me.toyproject.mia.event.EventRepository;
import me.toyproject.mia.event.EventDetailDto;
import me.toyproject.mia.event.EventDto;
import me.toyproject.mia.account.HostDto;
import me.toyproject.mia.exception.DataNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;
	private final MapperFacade orikaMapperFacade;
	private final ApplicationEventPublisher applicationEventPublisher;

	public List<EventDto> findAllEvents() {
		List<Event> events = eventRepository.findAllRegisterOpenNow(LocalDateTime.now());
		return orikaMapperFacade.mapAsList(events, EventDto.class);
	}

	public Page<EventDto> findAllEvents(PageRequest pageRequest) {
		Page<Event> eventPage = eventRepository.findAllRegisterOpenNow(LocalDateTime.now(), pageRequest);
		List<EventDto> eventDto = orikaMapperFacade.mapAsList(eventPage.getContent(), EventDto.class);
		return new PageImpl<>(eventDto, pageRequest, eventPage.getTotalElements());
	}

	public EventDetailDto findById(long id) {
		Event event = findEventById(id);
		EventDto eventDto = orikaMapperFacade.map(event, EventDto.class);
		HostDto hostDto = orikaMapperFacade.map(event.getHost(), HostDto.class);
		return new EventDetailDto(event.getId(), eventDto, hostDto);
	}

	@Transactional
	public EventDetailDto create(EventDto eventDto, Account account) {
		Event createdEvent = eventRepository.save(eventDto.toDomain(account));
		return createEventDetailDto(createdEvent);
	}

	@Transactional
	public EventDetailDto modifyEvent(Long id, EventDto modifyEventDto, Account account) {
		Event event = findEventById(id);
		event.update(modifyEventDto.toDomain(account));
		final EventDetailDto modifiedEvent = createEventDetailDto(event);

		EventModifyEvent eventModifyEvent = EventModifyEvent.builder()
			.source(this)
			.originalEvent(createEventDetailDto(event))
			.modifiedEvent(modifiedEvent)
			.enrolledGuestIds(event.getEnrolledGuestIds())
			.build();

		applicationEventPublisher.publishEvent(eventModifyEvent);

		return modifiedEvent;
	}

	@Transactional
	public EventDto deleteEvent(Long id, Account account) {
		Event event = findEventById(id);
		event.delete(account);
		return createEventDto(event);
	}

	private EventDto createEventDto(Event event) {
		return orikaMapperFacade.map(event, EventDto.class);
	}

	private EventDetailDto createEventDetailDto(Event event) {
		EventDetailDto dto = orikaMapperFacade.map(event, EventDetailDto.class);
		dto.setHostDto(orikaMapperFacade.map(event.getHost(), HostDto.class));
		return dto;
	}

	private Event findEventById(Long id) {
		return eventRepository.findById(id).orElseThrow(() -> new DataNotFoundException("존재하지 않는 이벤트입니다"));
	}
}
