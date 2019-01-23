import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.springframework.boot.convert.DurationUnit;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

public class LibTest {

    @Test
    public void test(){
        SoftAssertions softAssertions = new SoftAssertions();

        softAssertions.assertThat(Duration.ofDays(30).compareTo(Duration.ofDays(1))).isEqualTo(1);
        softAssertions.assertThat(Duration.ofDays(1).compareTo(Duration.ofDays(30))).isEqualTo(-1);

        softAssertions.assertThat( Duration.between(Instant.now(), Instant.now().plus(Duration.ofDays(3)))).isEqualTo(Duration.ofDays(3));
        softAssertions.assertThat( Duration.between(Instant.now().plus(Duration.ofDays(3)), Instant.now())).isEqualTo(Duration.ofDays(3));
        softAssertions.assertThat( Duration.between(LocalDateTime.now().toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.UTC))).isEqualTo(Duration.ofDays(3));
    }
}
