package me.toyproject.mia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class EventChangeListener implements ApplicationListener<EventModifyEvent> {

	@Autowired
	private EventNotifcationService notifcationService;

	@Override
	public void onApplicationEvent(EventModifyEvent event) {
		notifcationService.notifyToGuests(event.getEnrolledGuestIds(), event.getOriginalEvent(), event.getModifiedEvent());
	}
}
