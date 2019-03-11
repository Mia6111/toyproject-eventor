package me.toyproject.mia.service;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.UserNotificationMethod;
import me.toyproject.mia.event.EventDetailDto;
import me.toyproject.mia.infra.NotificationEngine;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventNotifcationService {
	private final Map<UserNotificationMethod, NotificationEngine> notificationEngineMap;
	private final AccountService accountService;

	public void notifyToGuests(Set<Long> enrolledGuestIds, EventDetailDto original, EventDetailDto modified) {
		if (CollectionUtils.isEmpty(enrolledGuestIds)) {
			return;
		}
		//todo : 요구사항 자세히 정의 및 refactor

		//create notifyingMsg
		String notifyingMsg = "변경 사항 --";
		log.debug("notifyingMsg => {}", notifyingMsg);

		//notify to all guests upon their notification methods
		enrolledGuestIds.stream().forEach(each -> {
			Account account = accountService.findById(each);
			account.getNotificationMethods().stream()
				.filter(method -> notificationEngineMap.containsKey(method))
				.forEach(method -> {
					String target = null;
					try {
						target = (String) ReflectionUtils.getField(Account.class.getField(method.getRequiredField()), account);
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
					notificationEngineMap.get(method).sendMessage(target, notifyingMsg);
				});
		});

	}

}
