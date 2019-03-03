package me.toyproject.mia.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.CurrentUser;
import me.toyproject.mia.service.EventService;
import me.toyproject.mia.event.EventDetailDto;
import me.toyproject.mia.event.EventDto;
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

import javax.validation.Valid;
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

    @GetMapping("")
    public ResponseEntity<PagedResources<Resource<EventDto>>> findAllRegisterOpenEvents(@RequestParam(value = "page", required = false) Integer page, @CurrentUser Account loginUser, PagedResourcesAssembler<EventDto> assembler) {
        Page<EventDto> eventDtos = eventService.findAllEvents(PageRequest.of(ObjectUtils.defaultIfNull(page, 0), DEFAULT_PAGE_SIZE));
        PagedResources<Resource<EventDto>> resources = assembler.toResource(eventDtos,
                event -> new Resource<>(event, linkTo(EventController.class).slash(event.getId()).withSelfRel()),
                linkTo(EventController.class).withSelfRel());
        if (!loginUser.isGuest()) {
            resources.add(linkTo(EventController.class).withRel("create"));
        }


        resources.add(
                linkTo(EventController.class).withSelfRel(),
                new Link("http://localhost:8080/docs/index.html#get-register-open-events").withRel("profile"));

        return ResponseEntity.ok(resources);
    }

    @PostMapping("")
    public ResponseEntity<Resource<EventDetailDto>> createEvent(@RequestBody @Valid EventDto eventDto, @CurrentUser Account loginUser) {
        EventDetailDto createdEventDto = eventService.create(eventDto, loginUser);
        Resource<EventDetailDto> resource = getEventResponseResource(createdEventDto);

        resource.add(
                linkTo(EventController.class).slash(createdEventDto.getId()).withRel("update"),
                linkTo(EventController.class).slash(createdEventDto.getId()).withRel("delete"),
                new Link("http://localhost:8080/docs/index.html#create-event").withRel("profile"));
        final URI selfLinkUri = ControllerLinkBuilder.linkTo(EventController.class).slash(eventDto.getId()).toUri();

        return ResponseEntity.created(selfLinkUri).body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource<EventDetailDto>> findById(@PathVariable("id") Long id, @CurrentUser Account loginUser) {
        EventDetailDto detailDto = eventService.findById(id);
        Resource<EventDetailDto> resource = getEventResponseResource(detailDto);
        //?
        if (detailDto.getHostDto().toDomain().isSameHost(loginUser)) {
            resource.add(
                    linkTo(EventController.class).slash(detailDto.getId()).withRel("update"),
                    linkTo(EventController.class).slash(detailDto.getId()).withRel("delete"));
        }

        resource.add(new Link("http://localhost:8080/docs/index.html#get-event").withRel("profile"));
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource<EventDetailDto>> modifyEvent(@PathVariable("id") Long id, @RequestBody @Valid EventDto eventDto, @CurrentUser Account loginUser) {
        EventDetailDto event = eventService.modifyEvent(id, eventDto, loginUser);

        Resource<EventDetailDto> resource = getEventResponseResource(event);

        resource.add(
                linkTo(EventController.class).slash(event.getId()).withRel("delete"),
                linkTo(methodOn(HostController.class).findById(event.getHostDto().getId())).withRel("host")
        );

        resource.add(new Link("http://localhost:8080/docs/index.html#modify-event").withRel("profile"));
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Resource<Long>> deleteEvent(@PathVariable("id") Long id, @CurrentUser Account loginUser) {
        EventDto event = eventService.deleteEvent(id, loginUser);
        Resource<Long> resource = new Resource<>(event.getId());
        resource.add(new Link("http://localhost:8080/docs/index.html#delete-event").withRel("profile"));
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
