package me.toyproejct.mia;

import com.google.common.base.Objects;
import ma.glasnost.orika.MapperFacade;
import me.toyproejct.mia.domain.Event;
import me.toyproejct.mia.domain.EventRepository;
import me.toyproejct.mia.exception.DataNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MapperFacade orikaMapperFacade;

    public List<EventDto> findAllEvents() {
        List<Event> events = eventRepository.findAllRegisterOpenNow();
        return orikaMapperFacade.mapAsList(events, EventDto.class);
    }

    public EventDetailDto findById(long id) {
        Event event = eventRepository.findById(id).orElseThrow(()-> new DataNotFoundException("존재하지 않는 이벤트입니다"));
        EventDto eventDto = orikaMapperFacade.map(event, EventDto.class);
        HostDto hostDto = orikaMapperFacade.map(event.getHost(), HostDto.class);
        return new EventDetailDto(event.getId(), eventDto, hostDto);
    }
    @Transactional(readOnly = false)
    public EventDto create(Event event) {
        Event createdEvent = eventRepository.save(event);
        System.out.println("id :"+createdEvent.getId());
        return orikaMapperFacade.map(createdEvent, EventDto.class);
    }

    @Transactional(readOnly = false)
    public EventDto modifyEvent(Long id, EventDetailDto modifyDto){
        Event event = eventRepository.findById(id).orElseThrow(()-> new DataNotFoundException("존재하지 않는 이벤트입니다"));
        event.update(modifyDto.getHostDto().toDomain(), modifyDto.getEventDto().toDomain());
        return orikaMapperFacade.map(event, EventDto.class);
    }
}
