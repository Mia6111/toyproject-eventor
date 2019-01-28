package me.toyproject.mia.domain;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import me.toyproejct.mia.CoreApplicatoin;
import me.toyproejct.mia.domain.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.event.EventDirContext;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreApplicatoin.class)
public class OrikaMapperTest {
    @Autowired
    private MapperFacade orikaMapperFacade;

    @Test
    public void test_map(){
        A a = new A();
        a.id = 1L;
        a.desc = "desc";

        B copied = orikaMapperFacade.map(a, B.class);

        assertThat(copied.id).isEqualTo(a.id);
        assertThat(copied.desc).isEqualTo(a.desc);

    }

    @Test
    public void test_map2(){
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .useBuiltinConverters(true)
                .useAutoMapping(true)
                .mapNulls(false).build();
        A a = new A();
        a.id = 1L;
        a.desc = "desc";
        MapperFacade orikaMapperFacade_try = factory.getMapperFacade();
        B copied = orikaMapperFacade_try.map(a, B.class);
        assertThat(copied.id).isEqualTo(a.id);
        assertThat(copied.desc).isEqualTo(a.desc);
    }

}


class A{
    Long id;
    String desc;
    String ext;

    public A() {
    }

    public Long getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public String getExt() {
        return ext;
    }
}
class B{
    Long id;
    String desc;
    String beEmpty;

    public B() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBeEmpty() {
        return beEmpty;
    }

    public void setBeEmpty(String beEmpty) {
        this.beEmpty = beEmpty;
    }
}
