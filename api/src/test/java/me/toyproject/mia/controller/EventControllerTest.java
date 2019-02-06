package me.toyproject.mia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.ApiApplication;
import me.toyproject.mia.MockEntityHelper;
import me.toyproject.mia.domain.*;
import me.toyproject.mia.dto.EventDetailDto;
import me.toyproject.mia.persistence.ApiAuth;
import me.toyproject.mia.configuration.WebTestConfiguration;
import me.toyproject.mia.dto.EventDto;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@AutoConfigureMockMvc
@Import(WebTestConfiguration.class)
@Slf4j
@Transactional
public class EventControllerTest {
    private static final String EVENT_RESOURCE = "/api/v1/events";

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockEntityHelper mockEntityHelper;

    @MockBean
    private ApiAuth requestScopedApiAuth;

    private Event event;
    private static Account account;

    @Autowired
    private EventRepository eventRepository;
//    @Autowired
//    private AccountRepository accountRepository;

    @Before
    public void setup() {
        log.debug("setup");


        account = mockEntityHelper.mockAccount();
        event = mockEntityHelper.mockEvent(account);

        when(requestScopedApiAuth.getAccount()).thenReturn(account);
        log.debug("check api {}", requestScopedApiAuth.getAccount());
    }

//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @After
//    public void clean() {
//        log.debug("clean");
//        eventRepository.delete(event);
//        entityManager.flush();
//        accountRepository.delete(account);
//
//    }

    @Test
    public void findAllRegisterOpenEvents() throws Exception {
        mockMvc.perform(get(EVENT_RESOURCE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("_embedded").exists())
                .andExpect(jsonPath("_embedded.eventDtoList").isArray())
                .andExpect(jsonPath("_embedded.eventDtoList").isArray())
                .andExpect(jsonPath("_embedded.eventDtoList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("page").exists());
    }

    @Test
    public void createEvent() throws Exception {


        EventDto eventDto = new EventDto();
        eventDto.setTitle("title");
        eventDto.setContent("content");
        eventDto.setMaxPeopleCnt(20);
        eventDto.setLocation("장소예요");
        eventDto.setPrice(20000);
        eventDto.setEventOpenPriod(new Period(LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20)));
        eventDto.setRegisterOpenPeriod(new Period(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10)));

        mockMvc.perform(post(EVENT_RESOURCE)
                .header(HttpHeaders.AUTHORIZATION, createHeaders(account.getEmail(), account.getPassword()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("enrolledPeopleCnt").exists())
                .andExpect(jsonPath("maxPeopleCnt").value(eventDto.getMaxPeopleCnt()))
                .andExpect(jsonPath("location").value(eventDto.getLocation()))
                .andExpect(jsonPath("price").value(eventDto.getPrice()))
                .andExpect(jsonPath("registerOpenPeriod").exists())
                .andExpect(jsonPath("eventOpenPriod").exists())
                .andExpect(jsonPath("content").value(eventDto.getContent()))
                .andExpect(jsonPath("title").value(eventDto.getTitle()))

                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.events").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.update").exists())
                .andExpect(jsonPath("_links.delete").exists());

    }

    @Test
    public void findById_호스트일때() throws Exception {
        mockMvc.perform(get(EVENT_RESOURCE + "/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, createHeaders(account.getEmail(), account.getPassword()))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("hostDto.email").value(account.getEmail()))
                .andExpect(jsonPath("hostDto.name").value(account.getName()))
                .andExpect(jsonPath("enrolledPeopleCnt").exists())
                .andExpect(jsonPath("maxPeopleCnt").value(event.getMaxPeopleCnt()))
                .andExpect(jsonPath("location").value(event.getLocation()))
                .andExpect(jsonPath("price").value(event.getPrice()))
                .andExpect(jsonPath("registerOpenPeriod").exists())
                .andExpect(jsonPath("eventOpenPriod").exists())
                .andExpect(jsonPath("content").value(event.getContent()))
                .andExpect(jsonPath("title").value(event.getTitle()))
                .andExpect(jsonPath("registerOpen").value(event.isRegisterOpen()))

                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.events").exists())
                .andExpect(jsonPath("_links.host").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.update").exists())
                .andExpect(jsonPath("_links.delete").exists());

    }

    @Test
    public void findById_호스트아닐때() throws Exception {
        when(requestScopedApiAuth.getAccount()).thenReturn(null);

        mockMvc.perform(get(EVENT_RESOURCE + "/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("hostDto.email").value(account.getEmail()))
                .andExpect(jsonPath("hostDto.name").value(account.getName()))
                .andExpect(jsonPath("enrolledPeopleCnt").exists())
                .andExpect(jsonPath("maxPeopleCnt").value(event.getMaxPeopleCnt()))
                .andExpect(jsonPath("location").value(event.getLocation()))
                .andExpect(jsonPath("price").value(event.getPrice()))
                .andExpect(jsonPath("registerOpenPeriod").exists())
                .andExpect(jsonPath("eventOpenPriod").exists())
                .andExpect(jsonPath("content").value(event.getContent()))
                .andExpect(jsonPath("title").value(event.getTitle()))
                .andExpect(jsonPath("registerOpen").value(event.isRegisterOpen()))

                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.events").exists())
                .andExpect(jsonPath("_links.host").exists())
                .andExpect(jsonPath("_links.profile").exists());

    }

    @Test
    public void findById_존재하지_않는_이벤트일때() throws Exception {
        mockMvc.perform(get(EVENT_RESOURCE + "/{id}", -1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void modifyEvent_호스트일때() throws Exception {
        EventDetailDto eventRequest = new EventDetailDto();
        BeanUtils.copyProperties(event, eventRequest);
        eventRequest.setContent("CONTENT - modify");
        eventRequest.setTitle("TITLE - modify");
        eventRequest.setLocation("LOCATION - modify");
        eventRequest.setPrice(5000);
        eventRequest.setMaxPeopleCnt(60);

        when(requestScopedApiAuth.getAccount()).thenReturn(account);

        mockMvc.perform(put(EVENT_RESOURCE + "/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, createHeaders(account.getEmail(), account.getPassword()))
                .content(objectMapper.writeValueAsString(eventRequest))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("hostDto.email").value(account.getEmail()))
                .andExpect(jsonPath("hostDto.name").value(account.getName()))
                .andExpect(jsonPath("enrolledPeopleCnt").exists())
                .andExpect(jsonPath("maxPeopleCnt").value(eventRequest.getMaxPeopleCnt()))
                .andExpect(jsonPath("location").value(eventRequest.getLocation()))
                .andExpect(jsonPath("price").value(eventRequest.getPrice()))
                .andExpect(jsonPath("registerOpenPeriod").exists())
                .andExpect(jsonPath("eventOpenPriod").exists())
                .andExpect(jsonPath("content").value(eventRequest.getContent()))
                .andExpect(jsonPath("title").value(eventRequest.getTitle()))
                .andExpect(jsonPath("registerOpen").value(true))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.events").exists())
                .andExpect(jsonPath("_links.host").exists())
                .andExpect(jsonPath("_links.profile").exists());

    }

    @Test
    public void modifyEvent_호스트아닐때() throws Exception {
        when(requestScopedApiAuth.getAccount()).thenReturn(null);

        EventDetailDto eventRequest = new EventDetailDto();
        BeanUtils.copyProperties(event, eventRequest);
        eventRequest.setContent("CONTENT - modify");
        eventRequest.setTitle("TITLE - modify");
        eventRequest.setLocation("LOCATION - modify");
        eventRequest.setPrice(5000);
        eventRequest.setMaxPeopleCnt(60);

        mockMvc.perform(put(EVENT_RESOURCE + "/{id}", event.getId())
                .content(objectMapper.writeValueAsString(eventRequest))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

    @Test
    public void modifyEvent_입력데이터_이상한_경우() throws Exception {
        EventDetailDto eventRequest = new EventDetailDto();
        BeanUtils.copyProperties(event, eventRequest);
        eventRequest.setContent(null);

        mockMvc.perform(put(EVENT_RESOURCE + "/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, createHeaders(account.getEmail(), account.getPassword()))
                .content(objectMapper.writeValueAsString(eventRequest))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void modifyEvent_도메인_검증_실패_경우() throws Exception {
        Period register = new Period(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3));
        Period open = new Period(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10));
        Event notUpdatableEvent = Event.builder()
                .title("TITLE")
                .content("CONTENT")
                .maxPeopleCnt(10)
                .eventOpenPriod(open)
                .registerOpenPeriod(register)
                .price(1000)
                .location("SOMEWHERE")
                .host(account)
                .build();
        notUpdatableEvent = eventRepository.save(notUpdatableEvent);

        EventDetailDto eventRequest = new EventDetailDto();
        BeanUtils.copyProperties(notUpdatableEvent, eventRequest);

        mockMvc.perform(put(EVENT_RESOURCE + "/{id}", notUpdatableEvent.getId())
                .content(objectMapper.writeValueAsString(eventRequest))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void deleteEvent() throws Exception {
        mockMvc.perform(delete(EVENT_RESOURCE + "/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, createHeaders(account.getEmail(), account.getPassword())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value(event.getId()))
                .andExpect(jsonPath("_links.events").exists())
                .andExpect(jsonPath("_links.profile").exists());
    }
    @Test
    public void deleteEvent_호스트아닐때() throws Exception {
        when(requestScopedApiAuth.getAccount()).thenReturn(null);
        mockMvc.perform(delete(EVENT_RESOURCE + "/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    String createHeaders(String email, String password) {
        String auth = email + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
        return "Basic " + new String(encodedAuth);

    }

}