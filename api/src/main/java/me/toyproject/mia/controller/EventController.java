package me.toyproject.mia.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.persistence.ApiAuth;
import me.toyproject.mia.service.EventService;
import me.toyproject.mia.dto.EventDetailDto;
import me.toyproject.mia.dto.EventDto;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/v1/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
@Slf4j
@AllArgsConstructor
public class EventController {
    private static final int DEFAULT_PAGE_SIZE = 30;

    private EventService eventService;

    @javax.annotation.Resource(name = "requestScopedApiAuth")
    private ApiAuth apiAuth;

    @GetMapping("")
    public ResponseEntity<PagedResources<Resource<EventDto>>> findAllRegisterOpenEvents(@RequestParam(value = "page", required = false) Integer page, PagedResourcesAssembler<EventDto> assembler) {
        Page<EventDto> eventDtos = eventService.findAllEvents(PageRequest.of(ObjectUtils.defaultIfNull(page, 0), DEFAULT_PAGE_SIZE));
        PagedResources<Resource<EventDto>> resources = assembler.toResource(eventDtos,
                event -> new Resource<>(event, linkTo(EventController.class).slash(event.getId()).withSelfRel()),
                linkTo(EventController.class).withSelfRel());
        return ResponseEntity.ok(resources);
    }

    @PostMapping("")
    public ResponseEntity<Resource<EventDto>> createEvent(@RequestBody EventDto eventDto) {
        EventDto createdEventDto = eventService.create(eventDto);
        Resource<EventDto> resource = getEventResponseResource(createdEventDto);

        resource.add(
                linkTo(EventController.class).slash(createdEventDto.getId()).withRel("update"),
                linkTo(EventController.class).slash(createdEventDto.getId()).withRel("delete"),
                new Link("http://localhost:8080/docs/index.html#resources-events-read").withRel("profile"));
        final URI selfLinkUri = ControllerLinkBuilder.linkTo(EventController.class).slash(eventDto.getId()).toUri();

        return ResponseEntity.created(selfLinkUri).body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource<EventDetailDto>> findById(@PathVariable("id") Long id) {
        EventDetailDto detailDto = eventService.findById(id);
        Resource<EventDetailDto> resource = getEventResponseResource(detailDto);
        //?
        if (detailDto.getHostDto().toDomain().isSameHost(apiAuth.getAccount())) {
            resource.add(
                    linkTo(EventController.class).slash(detailDto.getId()).withRel("update"),
                    linkTo(EventController.class).slash(detailDto.getId()).withRel("delete"));
        }

        resource.add(new Link("http://localhost:8080/docs/index.html#resources-events-read").withRel("profile"));
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource<EventDetailDto>> modifyEvent(@PathVariable("id") Long id, @RequestBody EventDetailDto eventDto) {
        EventDetailDto event = eventService.modifyEvent(id, eventDto);

        Resource<EventDetailDto> resource = getEventResponseResource(event);
        resource.add(new Link("http://localhost:8080/docs/index.html#resources-events-read").withRel("profile"));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<Long>> deleteEvent(@PathVariable("id") Long id) {
        EventDto event = eventService.deleteEvent(id);
        Resource<Long> resource = new Resource<>(event.getId());
        resource.add(new Link("http://localhost:8080/docs/index.html#resources-events-delete").withRel("profile"));
        resource.add(linkTo(EventController.class).withRel("events"));
        return ResponseEntity.ok(resource);
    }


    private Resource<EventDto> getEventResponseResource(EventDto event) {
        Resource<EventDto> resource = new Resource<>(event);
        resource.add(
                linkTo(EventController.class).slash(event.getId()).withSelfRel(),
                linkTo(EventController.class).withRel("events"));
        return resource;
    }

    private Resource<EventDetailDto> getEventResponseResource(EventDetailDto event) {
        Resource<EventDetailDto> resource = new Resource<>(event);
        resource.add(
                linkTo(EventController.class).slash(event.getId()).withSelfRel(),
                linkTo(EventController.class).withRel("events"),
                linkTo(methodOn(HostController.class).findById(event.getHostDto().getId())).withRel("host"));
        return resource;
    }

}
