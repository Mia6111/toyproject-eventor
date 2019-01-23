import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import me.toyproejct.mia.*;
import me.toyproejct.mia.domain.*;
import me.toyproejct.mia.exception.DataNotFoundException;
import net.rakugakibox.spring.boot.orika.OrikaAutoConfiguration;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryBuilderConfigurer;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = {ApiApplication.class})
public class EventServiceTest {
    Logger logger = LoggerFactory.getLogger(EventServiceTest.class.toString());

    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;

    @Spy
    private MapperFacade orikaMapperFacade ;

    private final Account account  = constructAccount("abd@abd.com");
    private final Event e1 = constructEvent(1L, "test1");
    private final Event e2 = constructEvent(2L, "test2");
    @Before
    public void init(){
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .useBuiltinConverters(true)
                .useAutoMapping(true)
                .mapNulls(false).build();
//        factory.classMap(Event.class, EventDto.class).byDefault().register();
//        factory.classMap(Event.class, EventDetailDto.class).byDefault().register();
        orikaMapperFacade = factory.getMapperFacade();


    }

    @Test
    public void 이벤트를_생성한다() {
        when(eventRepository.save(any(Event.class))).thenReturn(e1);
        EventDto createdEvent = eventService.create(e1);

        logger.debug("eventDto {}", createdEvent);
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

        when(eventRepository.findAll()).thenReturn(Lists.newArrayList(e1, e2));
        List<EventDto> result = eventService.findAllEvents();

        logger.debug("result {}", result);
        assertThat(result).allMatch(EventDto::isRegisterOpen);
        assertThat(result.stream().map(EventDto::getTitle).collect(Collectors.toList())).contains(e1.getTitle(), e2.getTitle());
    }

    @Test
    public void 특정_이벤트를_상세조회한다() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(e1));

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

    private Account constructAccount(String email) {
        return Account.builder().email(email).name("NAME").build();
    }

    private Event constructEvent(Long id, String title) {
        Period register = new Period(LocalDateTime.now().minusDays(1), LocalDateTime.now().plus(Duration.ofDays(2)));
        Period open = new Period(LocalDateTime.now().plus(Duration.ofDays(3)), LocalDateTime.now().plus(Duration.ofDays(5)));
        return Event.builder()
                .id(id)
                .title(title)
                .content("CONTENT")
                .maxPeopleCnt(10)
                .eventOpenPriod(open)
                .registerOpenPeriod(register)
                .price(1000)
                .location("SOMEWHERE")
                .host(account)
                .build();
    }
}
