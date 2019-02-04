package me.toyproejct.mia.controller;

import me.toyproejct.mia.service.EventService;
import me.toyproject.mia.dto.EventDetailDto;
import me.toyproject.mia.dto.EventDto;
import me.toyproject.mia.dto.HostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/events", produces = "application/vnd.eventors.v1+json")
public class EventController {
    public static final int DEFAULT_PAGE_SIZE = 30;
    @Autowired
    private EventService eventService;

    @GetMapping("")
    public Page<EventDto> findAllRegisterOpenEvents(@RequestParam("page") int page){
        return eventService.findAllEvents(PageRequest.of(page, DEFAULT_PAGE_SIZE));
    }
    @PostMapping("")
    public EventDto createEvent(@RequestBody EventDto eventDto){
        return eventService.create(eventDto);
    }

    @GetMapping("/{id}")
    public EventDetailDto findById(@PathVariable("id") Long id){
        return eventService.findById(id);
    }

    @PutMapping("/{id}")
    public EventDetailDto modifyEvent(@PathVariable("id") Long id, @RequestBody EventDetailDto eventDto){
        return eventService.modifyEvent(id, eventDto);
    }

    @DeleteMapping("/{id}")
    public EventDto deleteEvent(@PathVariable("id") Long id, @RequestBody HostDto hostDto){
        return eventService.deleteEvent(id, hostDto);
    }
}
