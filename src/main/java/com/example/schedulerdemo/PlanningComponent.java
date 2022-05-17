package com.example.schedulerdemo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.example.schedulerdemo.ScheduleItem;
import com.example.schedulerdemo.SchedulerRule;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.RRule;

public class PlanningComponent {

	protected PeriodList createEventsInInterval(DateTime rangeFrom, DateTime rangeTo, ScheduleItem scheduleItem) throws ParseException {
		Period period = new Period(new net.fortuna.ical4j.model.DateTime(rangeFrom.getMillis()),
				new net.fortuna.ical4j.model.DateTime(rangeTo.getMillis()));
		return createEvent(scheduleItem).calculateRecurrenceSet(period).stream()
				.map(eventPeriod -> {
					DateTime start = new DateTime(eventPeriod.getStart());
					DateTime end = new DateTime(eventPeriod.getEnd());
					if (scheduleItem.getRule().getValidFrom().isAfter(start)) {
						start = scheduleItem.getRule().getValidFrom();
					}
					DateTime validTo = getValidToFromRRule(scheduleItem.getRule().getRrule());
					if (validTo != null && validTo.isBefore(end)) {
						end = validTo;
					}
					if (end.isAfter(rangeTo)) {
						end = rangeTo;
					}
					if (start.isAfter(end)) { //start cannot be after end
						return null;
					}
					return new Period(new net.fortuna.ical4j.model.DateTime(start.getMillis()), new net.fortuna.ical4j.model.DateTime(end.getMillis()));
				}).filter(Objects::nonNull).collect(Collectors.toCollection(PeriodList::new));
	}

	private VEvent createEvent(ScheduleItem regionSchedule) throws ParseException {
		if (regionSchedule.getRule() == null) {
			throw new IllegalArgumentException("Without rule.");
		}
		VEvent e = new VEvent();
		e.getProperties().add(new DtStart(new net.fortuna.ical4j.model.DateTime(regionSchedule.getRule().getStart().getMillis())));
		if (regionSchedule.getRule().getEnd() != null) {
			e.getProperties().add(new DtEnd(new net.fortuna.ical4j.model.DateTime(regionSchedule.getRule().getEnd().getMillis())));
		}
		if (regionSchedule.getRule().getRrule() != null) {
			RRule rRule = new RRule(regionSchedule.getRule().getRrule());
			if (regionSchedule.getRule().getValidTo() != null){
				rRule.getRecur().setUntil(new net.fortuna.ical4j.model.DateTime(regionSchedule.getRule().getValidTo().getMillis()));
			}
			e.getProperties().add(rRule);
		}
		if (regionSchedule.getRule().getExRules() != null) {
			regionSchedule.getRule().getExRules().forEach(exRule -> {
				try {
					e.getProperties().add(new ExRule(new Recur(exRule)));
				} catch (ParseException parseException) {
					//invalid data
					throw new RuntimeException(parseException);
				}
			});
		}
		if (regionSchedule.getRule().getExclusions() != null) {
			DateList exclusions = new DateList();
			regionSchedule.getRule().getExclusions().forEach(exclusion -> {
				String dateString = exclusion.toString("yyyyMMdd");
				try {
					//net.fortuna.ical4j.model.Date does not have api to send just a date, you must send ms but cannot tell in which timezone
					exclusions.add(new Date(dateString));
				} catch (ParseException parseException) {
					//invalid data
					throw new RuntimeException(parseException);
				}
			});
			e.getProperties().add(new ExDate(exclusions));
		}
		return e;
	}

	public static DateTime getValidToFromRRule(String stringRRule) {
		if (stringRRule == null) {
			return null;
		}
		try {
			RRule rRule = new RRule(stringRRule);
			if (rRule.getRecur().getUntil() != null) {
				return new DateTime(rRule.getRecur().getUntil());
			}
			return null;
		} catch (ParseException e) {
			throw new IllegalArgumentException("Cannot parse Rrule");
		}
	}

}
