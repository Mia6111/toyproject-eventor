package me.toyproject.mia;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class BCryptTest {

    @Test
    public void test() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        log.debug("result 1 {} ", encoder.encode("1"));
        log.debug("result 2 {} ", encoder.encode("2"));
    }
}
