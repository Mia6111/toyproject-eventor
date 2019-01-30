import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import me.toyproejct.mia.ApiApplication;
import me.toyproejct.mia.EventService;
import me.toyproject.mia.domain.Account;
import me.toyproject.mia.domain.Event;
import me.toyproject.mia.domain.EventRepository;
import me.toyproject.mia.domain.Period;
import me.toyproject.mia.dto.EventDetailDto;
import me.toyproject.mia.dto.EventDto;
import me.toyproject.mia.dto.HostDto;
import me.toyproject.mia.exception.DataNotFoundException;
import org.assertj.core.util.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = {ApiApplication.class})
@Slf4j
public class EventServiceTest {
//    Logger logger = LoggerFactory.getLogger(EventServiceTest.class.toString());

    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private static MapperFacade orikaMapperFacade;

    private final Account account  = constructAccount("abd@abd.com");
    private final Event e1 = MockBuilder.constructEvent(1L, "test1");
    private final Event e2 = MockBuilder.constructEvent(2L, "test2");
    private final Event enrolledEvents = MockBuilder.constructEvent(3L, "test3");
    private EventDto modifyDto;

    @BeforeClass
    public static void init(){
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .useBuiltinConverters(true)
                .useAutoMapping(true)
                .mapNulls(false).build();
        orikaMapperFacade = factory.getMapperFacade();
    }

    @Test
    public void 이벤트를_생성한다() {
        when(eventRepository.save(any(Event.class))).thenReturn(e1);
        EventDto createdEvent = eventService.create(e1);

        log.debug("eventDto {}", createdEvent);
        assertThat(createdEvent.getId()).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void 유효하지않은_이벤트를_생성할_경우_익셉션() {
        Event event = Event.builder()
                .title(null).build();
        EventDto createdEvent = eventService.create(event);
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
        assertThat(dto.getEventId()).isEqualTo(e1.getId());
        assertThat(dto.getEventDto().getTitle()).isEqualTo(e1.getTitle());
        assertThat(dto.getHostDto().getEmail()).isEqualTo(e1.getHost().getEmail());
    }

    @Test(expected = DataNotFoundException.class)
    public void 존재하지않는_이벤트에_접근시_익셉션() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        EventDetailDto dto = eventService.findById(-1L);
    }

    @Test
    public void 특정_이벤트를_등록자가_수정한다(){
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(e1));
        modifyDto = constructEventDto();

        HostDto hostDto = new HostDto();
        hostDto.setEmail(e1.getHost().getEmail());

        EventDetailDto detailDto = new EventDetailDto(e1.getId(), modifyDto, hostDto);
        EventDto result = eventService.modifyEvent(e1.getId(), detailDto);

        assertThat(result.getId()).isEqualTo(e1.getId());
        assertThat(result.getTitle()).isEqualTo(modifyDto.getTitle());
        assertThat(result.getContent()).isEqualTo(modifyDto.getContent());
        assertThat(result.getEventOpenPriod()).isEqualTo(modifyDto.getEventOpenPriod());
        assertThat(result.getRegisterOpenPeriod()).isEqualTo(modifyDto.getRegisterOpenPeriod());
        assertThat(result.getPrice()).isEqualTo(modifyDto.getPrice());
        assertThat(result.getMaxPeopleCnt()).isEqualTo(modifyDto.getMaxPeopleCnt());
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
