package me.toyproject.mia.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEngine {

	public void sendMessage(String target, String msg) {
		log.debug("target, msg => {}, {}", target, msg);
	}

}
