package com.example.schedulerdemo;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class SchedulerRule {

	private DateTime start;
	private DateTime end;
	private DateTime validFrom;
	private DateTime validTo;
	private String rRule;
	private List<String> exRules;

	public void setStart(DateTime start) {
		this.start = start;
	}

	public DateTime getStart() {
		return start;
	}

	public void setEnd(DateTime end) {
		this.end = end;
	}

	public DateTime getEnd() {
		return end;
	}

	public void setRrule(String rRule) {
		this.rRule = rRule;
	}

	public String getRrule() {
		return rRule;
	}

	public List<LocalDate> getExclusions() {
		return Collections.emptyList();
	}

	public void setValidFrom(DateTime validFrom) {
		this.validFrom = validFrom;
	}

	public DateTime getValidFrom() {
		return validFrom;
	}

	public void setExRules(List<String> exRules) {
		this.exRules = exRules;
	}

	public List<String> getExRules() {
		return exRules;
	}

	public DateTime getValidTo() {
		return validTo;
	}

	public void setValidTo(DateTime validTo) {
		this.validTo = validTo;
	}
}

