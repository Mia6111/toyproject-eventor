package me.toyproject.mia.service;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import me.toyproject.mia.ApiApplication;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.event.Event;
import me.toyproject.mia.event.EventRepository;
import me.toyproject.mia.event.Period;
import me.toyproject.mia.event.EventDetailDto;
import me.toyproject.mia.event.EventDto;
import me.toyproject.mia.account.HostDto;
import me.toyproject.mia.exception.DataNotFoundException;
import me.toyproject.mia.exception.EventException;
import me.toyproject.mia.exception.NotAuthorizedUserException;
import me.toyproject.mia.mock.MockBuilder;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = {ApiApplication.class})
@Slf4j
public class EventServiceTest {

	@InjectMocks
	private EventService eventService;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	@Mock
	private EventRepository eventRepository;

	@Spy
	private static MapperFacade orikaMapperFacade;

	private final Account account = MockBuilder.constructAccount("abd@abd.com");
	private final Event e1 = MockBuilder.constructEvent(1L, "test1", account);
	private final Event e2 = MockBuilder.constructEvent(2L, "test2", account);
	private final Event enrolledEvents = MockBuilder.constructEvent(3L, "test3", account);
	private EventDto modifyDto;

	@BeforeClass
	public static void init() {
		MapperFactory factory = new DefaultMapperFactory.Builder()
			.useBuiltinConverters(true)
			.useAutoMapping(true)
			.mapNulls(false).build();
		orikaMapperFacade = factory.getMapperFacade();
	}

	@Before
	public void setup() {

	}

	@Test
	public void 이벤트를_생성한다() {
		when(eventRepository.save(any(Event.class))).thenReturn(e1);
		EventDetailDto createdEvent = eventService.create(MockBuilder.createEventDtoFrom(e1), account);

		log.debug("eventDto {}", createdEvent);
		assertThat(createdEvent.getId()).isNotNull();
	}

	@Test(expected = EventException.class)
	public void 유효하지않은_이벤트를_생성할_경우_익셉션() {
		Event event = Event.builder()
			.title(null).build();
		EventDetailDto createdEvent = eventService.create(MockBuilder.createEventDtoFrom(e1), account);
	}

	@Test
	public void 현재_참석가능한_이벤트를_모두_조회한다() {
		Period register = new Period(LocalDateTime.now().plus(Duration.ofDays(1)), LocalDateTime.now().plus(Duration.ofDays(3)));
		Period open = new Period(LocalDateTime.now().plus(Duration.ofDays(3)), LocalDateTime.now().plus(Duration.ofDays(5)));

		when(eventRepository.findAllRegisterOpenNow(any(LocalDateTime.class))).thenReturn(Lists.newArrayList(e1, e2));
		List<EventDto> result = eventService.findAllEvents();

		log.debug("result {}", result);
		assertThat(result).allMatch(EventDto::isRegisterOpen);
		assertThat(result.stream().map(EventDto::getTitle).collect(Collectors.toList())).contains(e1.getTitle(), e2.getTitle());
	}

	@Test
	public void 특정_이벤트를_상세조회한다() {
		when(eventRepository.findById(anyLong())).thenReturn(Optional.of(e1));
		modifyDto = constructEventDto();
		EventDetailDto dto = eventService.findById(e1.getId());
		assertThat(dto.getId()).isEqualTo(e1.getId());
		assertThat(dto.getTitle()).isEqualTo(e1.getTitle());
		assertThat(dto.getHostDto().getEmail()).isEqualTo(e1.getHost().getEmail());
	}

	@Test(expected = DataNotFoundException.class)
	public void 존재하지않는_이벤트에_접근시_익셉션() {
		when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
		EventDetailDto dto = eventService.findById(-1L);
	}

	@Test
	public void 특정_이벤트를_등록자가_수정한다() {
		when(eventRepository.findById(anyLong())).thenReturn(Optional.of(e1));
		modifyDto = constructEventDto();

		EventDetailDto eventDetailDto = eventService.modifyEvent(e1.getId(), modifyDto, account);

		assertThat(eventDetailDto.getId()).isEqualTo(e1.getId());
		assertThat(eventDetailDto.getTitle()).isEqualTo(modifyDto.getTitle());
		assertThat(eventDetailDto.getContent()).isEqualTo(modifyDto.getContent());
		assertThat(eventDetailDto.getEventOpenPriod()).isEqualTo(modifyDto.getEventOpenPriod());
		assertThat(eventDetailDto.getRegisterOpenPeriod()).isEqualTo(modifyDto.getRegisterOpenPeriod());
		assertThat(eventDetailDto.getPrice()).isEqualTo(modifyDto.getPrice());
		assertThat(eventDetailDto.getMaxPeopleCnt()).isEqualTo(modifyDto.getMaxPeopleCnt());

		ArgumentCaptor<EventModifyEvent> captor = ArgumentCaptor.forClass(EventModifyEvent.class);
		verify(applicationEventPublisher).publishEvent(captor.capture());

		EventModifyEvent captorValue = captor.getValue();
		assertThat(captorValue).isExactlyInstanceOf(EventModifyEvent.class);
		assertThat(captorValue.getEnrolledGuestIds()).isEmpty();
		assertThat(captorValue.getModifiedEvent()).isEqualToComparingOnlyGivenFields(modifyDto,
			"title","content","registerOpenPeriod","eventOpenPriod","maxPeopleCnt","price","location","enrolledPeopleCnt"
		);
	}

	@Test
	public void 특정이벤트를_등록자가_삭제한다() {
		when(eventRepository.findById(anyLong())).thenReturn(Optional.of(e1));
		modifyDto = constructEventDto();

		HostDto hostDto = new HostDto();
		hostDto.setEmail(e1.getHost().getEmail());

		EventDetailDto detailDto = new EventDetailDto(e1.getId(), modifyDto, hostDto);
		EventDto result = eventService.deleteEvent(e1.getId(), account);

		assertThat(result.getId()).isEqualTo(e1.getId());
	}

	@Test(expected = NotAuthorizedUserException.class)
	public void 특정이벤트를_등록자가_아닌_다른이가_삭제할경우_에러() {
		Account notHost = new Account(6L, "any@email.com", "anyname", "pass123", "01012341234");
		when(eventRepository.findById(anyLong())).thenReturn(Optional.of(e1));
		modifyDto = constructEventDto();

		HostDto hostDto = new HostDto();
		hostDto.setEmail("invalid@email.com");

		EventDetailDto detailDto = new EventDetailDto(e1.getId(), modifyDto, hostDto);
		EventDto result = eventService.deleteEvent(e1.getId(), notHost);
	}

	private EventDto constructEventDto() {
		EventDto modifyDto = new EventDto();
		modifyDto.setContent("수정된 내용");
		modifyDto.setTitle("수정된 제목");
		modifyDto.setLocation("수정된 장소");
		modifyDto.setEventOpenPriod(new Period(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(5)));
		modifyDto.setRegisterOpenPeriod(new Period(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4)));
		modifyDto.setPrice(e1.getPrice() + 1000);
		modifyDto.setMaxPeopleCnt(e1.getMaxPeopleCnt() + 1);
		return modifyDto;
	}


}
