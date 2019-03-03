package me.toyproject.mia.common;

import me.toyproject.mia.ApiApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@ActiveProfiles("test")
@Transactional
@WithUserDetails(value = "user@test.com", userDetailsServiceBeanName = "accountService")
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
public abstract class AbstractApiBaseIntegrationTest {


}
