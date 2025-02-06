package com.example.nagoyameshi.event;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotUtils {
	public static List<String> generateTimeSlots(LocalTime openTime, LocalTime closeTime, int interval) {
	    if (openTime == null || closeTime == null) {
	        throw new IllegalArgumentException("Open time or close time is null");
	    }
	    List<String> timeSlots = new ArrayList<>();
	    LocalTime currentTime = openTime;
	    while (currentTime.isBefore(closeTime)) {
	        timeSlots.add(currentTime.toString());
	        currentTime = currentTime.plusMinutes(interval);
	    }
	    return timeSlots;
	}
}
