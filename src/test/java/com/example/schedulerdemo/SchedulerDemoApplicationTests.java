package com.example.schedulerdemo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import net.fortuna.ical4j.model.PeriodList;

@SpringBootTest
class SchedulerDemoApplicationTests {

	private static PlanningComponent planningComponent;

	@BeforeAll
	static void beforeAll() {
		planningComponent = new PlanningComponent();
	}

	private static DateTime getCurrentDay() {
		return new DateTime(2022, 5, 15, 10, 0);
	}

	private static SchedulerRule getSchedulerRule(DateTime currentDay) {
		SchedulerRule rule = new SchedulerRule();
		rule.setValidFrom(currentDay.minusDays(10));
		rule.setValidTo(currentDay.plusDays(10));
		rule.setStart(currentDay.minusDays(10));
		rule.setEnd(rule.getStart().plusSeconds(1));
		rule.setRrule("FREQ=MINUTELY;INTERVAL=1;BYDAY=MO;BYHOUR=15;UNTIL="+ rule.getValidTo().toString("yyyyMMdd'T'hhmmss"));
		List<String> exRules = new ArrayList<>();
		exRules.add("FREQ=MINUTELY;INTERVAL=1;BYHOUR=15;BYMINUTE=0,1,2,3,4,5,6,7,8,9");
		exRules.add("FREQ=MINUTELY;INTERVAL=1;BYHOUR=15;BYMINUTE=35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59");
		rule.setExRules(exRules);
		return rule;
	}

	@Test
	void testSchedulerBroke() throws ParseException {
		DateTime currentDay = getCurrentDay();
		SchedulerRule rule = getSchedulerRule(currentDay);

		ScheduleItem item = new ScheduleItem();
		item.setRule(rule);

		PeriodList period = planningComponent.createEventsInInterval(currentDay.minusHours(1), currentDay.plusDays(3), item);
		Assert.assertEquals(25, period.size());
		System.out.println();
	}

	/**
	 * Change start and end to be closer to current day less then 7 days
	 * @throws ParseException
	 */
	@Test
	void testScheduler() throws ParseException {
		DateTime currentDay = getCurrentDay();
		SchedulerRule rule = getSchedulerRule(currentDay);
		rule.setStart(currentDay.minusDays(2));
		rule.setEnd(rule.getStart().plusSeconds(1));
		ScheduleItem item = new ScheduleItem();
		item.setRule(rule);

		PeriodList period = planningComponent.createEventsInInterval(currentDay.minusHours(1), currentDay.plusDays(3), item);
		Assert.assertEquals(25, period.size());
	}

	/**
	 * Change only rRule - BYDAY= MO -> MO, TU
	 * @throws ParseException
	 */
	@Test
	void testScheduler2() throws ParseException {
		DateTime currentDay = getCurrentDay();
		SchedulerRule rule = getSchedulerRule(currentDay);
		rule.setRrule("FREQ=MINUTELY;INTERVAL=1;BYDAY=MO,TU;BYHOUR=15;UNTIL="+ rule.getValidTo().toString("yyyyMMdd'T'hhmmss"));
		ScheduleItem item = new ScheduleItem();
		item.setRule(rule);

		PeriodList period = planningComponent.createEventsInInterval(currentDay.minusHours(1), currentDay.plusDays(3), item);
		Assert.assertEquals(50, period.size());
	}


	@Test
	void testScheduler3() throws ParseException {
		DateTime currentDay = getCurrentDay();
		SchedulerRule rule = getSchedulerRule(currentDay);
		rule.setRrule("FREQ=MINUTELY;INTERVAL=1;BYDAY=TU;BYHOUR=15,16;UNTIL="+ rule.getValidTo().toString("yyyyMMdd'T'hhmmss"));
		List<String> exRules = new ArrayList<>();
		exRules.add("FREQ=MINUTELY;INTERVAL=1;BYHOUR=15;BYMINUTE=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54");
		exRules.add("FREQ=MINUTELY;INTERVAL=1;BYHOUR=16;BYMINUTE=10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59");
		rule.setExRules(exRules);
		ScheduleItem item = new ScheduleItem();
		item.setRule(rule);
		PeriodList period = planningComponent.createEventsInInterval(currentDay.minusHours(1), currentDay.plusDays(3), item);
		Assert.assertEquals(15, period.size());
	}

}
