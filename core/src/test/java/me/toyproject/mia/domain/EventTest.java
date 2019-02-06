package me.toyproject.mia.domain;

import me.toyproject.mia.exception.EventException;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {
    @Test(expected = EventException.class)
    public void 생성자_테스트_성공(){
        LocalDateTime from = LocalDateTime.now().minusMonths(1).minusDays(1);

        Period earlier = new Period(from, from.plusDays(1));
        Period latter = new Period(from.plusHours(5), from.plusDays(5));

        Event event = Event.builder()
                .title("title")
                .eventOpenPriod(earlier)
                .registerOpenPeriod(latter)
                .maxPeopleCnt(10)
                .price(10000)
                .location("")
                .host(new Account())
                .content("content").build();
    }
    @Test(expected = EventException.class)
    public void title이_없는경우_익셉션(){
        Event event = Event.builder()
                .title(null).build();
    }
    @Test(expected = EventException.class)
    public void content이_없는경우_익셉션(){
        Event event = Event.builder()
                .title("title")
                .content(null).build();
    }
    //...


    @Test(expected = EventException.class)
    public void 등록시간을_기준으로_1달안에_시작되지않는경우_익셉션(){

        Period register = new Period(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(6));
        Period open = new Period(LocalDateTime.now().plusDays(31), LocalDateTime.now().plusDays(35));
        Event event = Event.builder()
                .title("title")
                .eventOpenPriod(open)
                .registerOpenPeriod(register)
                .maxPeopleCnt(10)
                .price(10000)
                .location("")
                .content("content").build();
    }
    @Test
    public void test_isRegisterOpen(){

        Period earlier = new Period(LocalDateTime.now().minusMonths(1).minusDays(1), LocalDateTime.now().plusDays(1));
        Period latter = new Period( LocalDateTime.now().plusDays(4),  LocalDateTime.now().plusDays(7));

        Event registerOpenEvent = Event.builder()
                .title("title")
                .eventOpenPriod(latter)
                .registerOpenPeriod(earlier)
                .maxPeopleCnt(10)
                .price(10000)
                .location("")
                .host(new Account())
                .content("content").build();

        Period past = new Period(LocalDateTime.now().minusMonths(2), LocalDateTime.now().minusMonths(1));
        Event registerClosedEvent = Event.builder()
                .title("title")
                .eventOpenPriod(latter)
                .registerOpenPeriod(past)
                .maxPeopleCnt(10)
                .price(10000)
                .location("")
                .host(new Account())
                .content("content").build();

        assertThat(registerOpenEvent.isRegisterOpen()).isTrue();
        assertThat(registerClosedEvent.isRegisterOpen()).isFalse();
    }
    @Test
    public void test_isUpdatable(){
        Period open = new Period(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(7));
        Period register = new Period( LocalDateTime.now(),  LocalDateTime.now().plusDays(1));

        Event notUpdatable = Event.builder()
                .title("title")
                .eventOpenPriod(open)
                .registerOpenPeriod(register)
                .maxPeopleCnt(10)
                .price(10000)
                .location("")
                .host(new Account())
                .content("content").build();

        assertThat(notUpdatable.isUpdatable()).isFalse();

        Period open2 = new Period(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(17));
        Event updatable = Event.builder()
                .title("title")
                .eventOpenPriod(open2)
                .registerOpenPeriod(register)
                .maxPeopleCnt(10)
                .price(10000)
                .location("")
                .host(new Account())
                .content("content").build();
        assertThat(updatable.isUpdatable()).isTrue();
    }
  //...

}
