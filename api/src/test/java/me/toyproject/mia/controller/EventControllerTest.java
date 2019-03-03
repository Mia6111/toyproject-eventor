package me.toyproject.mia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountDetails;
import me.toyproject.mia.account.AccountRepository;
import me.toyproject.mia.common.AbstractApiBaseIntegrationTest;
import me.toyproject.mia.common.AccountInitializingBean;
import me.toyproject.mia.event.EventDetailDto;
import me.toyproject.mia.event.Event;
import me.toyproject.mia.event.EventRepository;
import me.toyproject.mia.event.Period;
import me.toyproject.mia.mock.MockEntityHelper;
import me.toyproject.mia.event.EventDto;
import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Slf4j
public class EventControllerTest extends AbstractApiBaseIntegrationTest {
    private static final String EVENT_RESOURCE = "/api/v1/events";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockEntityHelper mockEntityHelper;

    private Event event;
    private static Account account;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AccountRepository accountRepository;

    private FieldDescriptor[] eventResponseFields;
    private FieldDescriptor[] eventRequestFields;
    private FieldDescriptor[] hostFields;

    @Before
    public void setup() {

        account = accountRepository.findByEmail(AccountInitializingBean.USER_EMAIL).get();

        event = mockEntityHelper.mockEvent(account);

        eventRequestFields = new FieldDescriptor[]{
                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                fieldWithPath("registerOpenPeriod.startDate").type(JsonFieldType.STRING).description("참가 등록 시작일시"),
                fieldWithPath("registerOpenPeriod.endDate").type(JsonFieldType.STRING).description("참가 등록 마감일시"),
                fieldWithPath("eventOpenPriod.startDate").type(JsonFieldType.STRING).description("이벤트 시작일시"),
                fieldWithPath("eventOpenPriod.endDate").type(JsonFieldType.STRING).description("이벤트 종료일시"),
                fieldWithPath("price").type(JsonFieldType.NUMBER).description("참석 비용"),
                fieldWithPath("location").type(JsonFieldType.STRING).description("장소"),
                fieldWithPath("maxPeopleCnt").type(JsonFieldType.NUMBER).description("참가자 최대 수")
        };

        eventResponseFields = new FieldDescriptor[]{
                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                fieldWithPath("registerOpenPeriod.startDate").type(JsonFieldType.STRING).description("참가 등록 시작일시"),
                fieldWithPath("registerOpenPeriod.endDate").type(JsonFieldType.STRING).description("참가 등록 마감일시"),
                fieldWithPath("eventOpenPriod.startDate").type(JsonFieldType.STRING).description("이벤트 시작일시"),
                fieldWithPath("eventOpenPriod.endDate").type(JsonFieldType.STRING).description("이벤트 종료일시"),
                fieldWithPath("price").type(JsonFieldType.NUMBER).description("참석 비용"),
                fieldWithPath("location").type(JsonFieldType.STRING).description("장소"),
                fieldWithPath("maxPeopleCnt").type(JsonFieldType.NUMBER).description("참가자 최대 수"),
                fieldWithPath("enrolledPeopleCnt").type(JsonFieldType.NUMBER).description("참가 등록자 수"),
                fieldWithPath("registerOpen").type(JsonFieldType.BOOLEAN).description("참가 등록 가능 여부")
        };
        hostFields = new FieldDescriptor[]{
                fieldWithPath("email").type(JsonFieldType.STRING).description("호스트 이메일"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("호스트 명")
        };

    }

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
                .andExpect(jsonPath("_links.create").exists())
                .andExpect(jsonPath("page").exists())
                .andDo(document("get-register-open-events",
                        links(linkWithRel("create").description("create").optional()).and(pagingLinks),
                        responseFields(
                                fieldWithPath("_embedded").type(JsonFieldType.OBJECT).description("embedded"),
                                fieldWithPath("_embedded.eventDtoList[]").type(JsonFieldType.ARRAY).description("등록기간 중인 이벤트 목록"))
                                .andWithPrefix("_embedded.eventDtoList[].", eventResponseFields)
                                .and(subsectionWithPath("_embedded.eventDtoList[]._links").ignored())
                                .and(pageFields)
                ))
        ;
    }


    @Test
    public void findAllRegisterOpenEvents_로그인안한경우() throws Exception {
        account = accountRepository.save(account);
        event = mockEntityHelper.mockEvent(account);

        mockMvc.perform(get(EVENT_RESOURCE)
                .with(anonymous()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("_embedded").exists())
                .andExpect(jsonPath("_embedded.eventDtoList").isArray())
                .andExpect(jsonPath("_embedded.eventDtoList").isArray())
                .andExpect(jsonPath("_embedded.eventDtoList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create").doesNotExist())
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
//                .header(HttpHeaders.AUTHORIZATION, createHeaders(account.getEmail(), account.getPassword()))
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
                .andExpect(jsonPath("_links.delete").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("events").description("events"),
                                linkWithRel("host").description("host"),
                                linkWithRel("update").description("update").optional(),
                                linkWithRel("delete").description("delete").optional()
                        ),
                        relaxedRequestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("registerOpenPeriod.startDate").type(JsonFieldType.STRING).description("참가 등록 시작일시"),
                                fieldWithPath("registerOpenPeriod.endDate").type(JsonFieldType.STRING).description("참가 등록 마감일시"),
                                fieldWithPath("eventOpenPriod.startDate").type(JsonFieldType.STRING).description("이벤트 시작일시"),
                                fieldWithPath("eventOpenPriod.endDate").type(JsonFieldType.STRING).description("이벤트 종료일시"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("참석 비용"),
                                fieldWithPath("location").type(JsonFieldType.STRING).description("장소"),
                                fieldWithPath("maxPeopleCnt").type(JsonFieldType.NUMBER).description("참가자 최대 수"),
                                fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 number").optional()
                        ),
                        responseFields(eventResponseFields).andWithPrefix("hostDto.", hostFields))
                );
        ;

    }

    @Test
    public void createEvent_인증받지않은_사용자() throws Exception {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("title");
        eventDto.setContent("content");
        eventDto.setMaxPeopleCnt(20);
        eventDto.setLocation("장소예요");
        eventDto.setPrice(20000);
        eventDto.setEventOpenPriod(new Period(LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20)));
        eventDto.setRegisterOpenPeriod(new Period(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10)));

        mockMvc.perform(post(EVENT_RESOURCE)
                .with(anonymous())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated());
    }

    @Test
    public void findById_호스트일때() throws Exception {
        mockMvc.perform(get(EVENT_RESOURCE + "/{id}", event.getId())
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
                .andExpect(jsonPath("_links.delete").exists())
                .andDo(document("get-event",
                        links(
                                linkWithRel("events").description("events"),
                                linkWithRel("host").description("host"),
                                linkWithRel("update").description("update").optional(),
                                linkWithRel("delete").description("delete").optional()
                        ),
                        pathParameters(parameterWithName("id").description("이벤트 id")),
                        responseFields(eventResponseFields).andWithPrefix("hostDto.", hostFields))
                );

    }

    @Test
    public void findById_호스트아닐때() throws Exception {

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
        ;


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


        mockMvc.perform(put(EVENT_RESOURCE + "/{id}", event.getId())
                .content(objectMapper.writeValueAsString(eventRequest))
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
                .andDo(document("modify-event",
                        links(
                                linkWithRel("events").description("events"),
                                linkWithRel("host").description("host"),
                                linkWithRel("delete").description("delete")
                        ),
                        pathParameters(parameterWithName("id").description("id")),
                        relaxedRequestFields(eventRequestFields),// response 에선 사용되나 request에선 사용되지 않는 필드들
                        responseFields(eventResponseFields).andWithPrefix("hostDto.", hostFields)
                ));

    }

    @Test
    public void modifyEven_인증오류() throws Exception {

        EventDetailDto eventRequest = new EventDetailDto();
        BeanUtils.copyProperties(event, eventRequest);
        eventRequest.setContent("CONTENT - modify");
        eventRequest.setTitle("TITLE - modify");
        eventRequest.setLocation("LOCATION - modify");
        eventRequest.setPrice(5000);
        eventRequest.setMaxPeopleCnt(60);

        mockMvc.perform(put(EVENT_RESOURCE + "/{id}", event.getId()).with(anonymous())
                .content(objectMapper.writeValueAsString(eventRequest))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithUserDetails(value = AccountInitializingBean.OTHER_USER_EMAIL, userDetailsServiceBeanName = "accountService")
    public void modifyEvent_호스트아닐때() throws Exception {

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
        mockMvc.perform(delete(EVENT_RESOURCE + "/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value(event.getId())) //?
                .andExpect(jsonPath("_links.events").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("delete-event",
                        pathParameters(parameterWithName("id").description("id")),
                        links(
                                linkWithRel("events").description("events")
                        )));
    }

    @Test
    public void deleteEvent_인증오류() throws Exception {
        mockMvc.perform(delete(EVENT_RESOURCE + "/{id}", event.getId())
                .with(anonymous()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = AccountInitializingBean.OTHER_USER_EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "accountService")
    public void deleteEvent_호스트아닐때() throws Exception {
        mockMvc.perform(delete(EVENT_RESOURCE + "/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void createEvent_EventDto_Validation실패() throws Exception {
        EventDto eventDto = new EventDto();
        eventDto.setTitle(Strings.EMPTY);
        eventDto.setContent(Strings.EMPTY);
        eventDto.setMaxPeopleCnt(Integer.MAX_VALUE - 100);
        eventDto.setLocation(Strings.EMPTY);
        eventDto.setPrice(Integer.MAX_VALUE - 100);
        eventDto.setEventOpenPriod(new Period(LocalDateTime.now().plusDays(35), LocalDateTime.now().plusDays(37)));
        eventDto.setRegisterOpenPeriod(new Period(LocalDateTime.now().plusDays(35), LocalDateTime.now().plusDays(40)));

        mockMvc.perform(post(EVENT_RESOURCE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors").isArray())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].message").exists())
        ;
    }

    private static LinksSnippet links(LinkDescriptor... descriptors) {
        return HypermediaDocumentation.links(halLinks(),
                linkWithRel("self").ignored().optional(),
                linkWithRel("profile").ignored().optional(),
                linkWithRel("curies").ignored().optional()
        ).and(descriptors);
    }

    private static ResponseFieldsSnippet responseFields(FieldDescriptor... descriptors) {
        return PayloadDocumentation.responseFields(subsectionWithPath("_links").ignored()).and(descriptors);

    }

    private final LinkDescriptor[] pagingLinks = new LinkDescriptor[]{
            linkWithRel("first").optional().description("The first page of results"),
            linkWithRel("last").optional().description("The last page of results"),
            linkWithRel("next").optional().description("The next page of results"),
            linkWithRel("prev").optional().description("The previous page of results")};

    private final FieldDescriptor[] pageFields = new FieldDescriptor[]{
            fieldWithPath("page.size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
            fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER).description("총 갯수"),
            fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
            fieldWithPath("page.number").type(JsonFieldType.NUMBER).description("페이지 넘버")
    };
}