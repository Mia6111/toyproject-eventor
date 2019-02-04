package me.toyproejct.mia.service;

import ma.glasnost.orika.MapperFacade;
import me.toyproject.mia.domain.Event;
import me.toyproject.mia.domain.EventRepository;
import me.toyproject.mia.dto.EventDetailDto;
import me.toyproject.mia.dto.EventDto;
import me.toyproject.mia.dto.HostDto;
import me.toyproject.mia.exception.DataNotFoundException;
import me.toyproject.mia.exception.NotAuthorizedUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MapperFacade orikaMapperFacade;

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
    public EventDto create(EventDto eventDto) {
        Event createdEvent = eventRepository.save(eventDto.toDomain());
        System.out.println("id :" + createdEvent.getId());
        return createDto(createdEvent);
    }

    @Transactional
    public EventDetailDto modifyEvent(Long id, EventDetailDto modifyDto) {
        Event event = findEventById(id);
        event.update(modifyDto.getHostDto().toDomain(), modifyDto.getEventDto().toDomain());
        EventDto updateEventDto = createDto(event);
        return new EventDetailDto(id, updateEventDto, modifyDto.getHostDto());
    }

    @Transactional
    public EventDto deleteEvent(Long id, HostDto hostDto) {
        Event event = findEventById(id);

        if(!event.isHostedBy(hostDto.toDomain())){
           throw new NotAuthorizedUserException("권한이 없는 유저입니다");
        }
        event.delete();
        return createDto(event);
    }

    private EventDto createDto(Event event) {
        return orikaMapperFacade.map(event, EventDto.class);
    }

    private Event findEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new DataNotFoundException("존재하지 않는 이벤트입니다"));
    }
}
