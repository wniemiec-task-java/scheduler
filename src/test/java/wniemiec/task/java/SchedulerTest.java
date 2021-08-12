package wniemiec.task.java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SchedulerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long DELAY_TIME;
	private static final long INTERVAL_TIME;
	private static final long MAX_WAIT_TIME;
	private volatile boolean insideRoutine;
	private int totInsideRoutine;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		DELAY_TIME = 1000L;
		INTERVAL_TIME = 100L;
		MAX_WAIT_TIME = 1000L;
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@Before
	public void beforeEachTest() {
		insideRoutine = false;
		totInsideRoutine = 0;
		Scheduler.clearAllTimeout();
		Scheduler.clearAllInterval();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testTimeout() throws InterruptedException {
		Scheduler.setTimeout(() -> {
			insideRoutine = true;
		}, DELAY_TIME);
		
		waitForTimeout();
		
		assertTrue(insideRoutine);
	}
	
	@Test
	public void testInterval() throws InterruptedException {
		totInsideRoutine = 0;
		
		long scheduleId = Scheduler.setInterval(() -> {
			if (totInsideRoutine == 3)
				return;

			totInsideRoutine++;
		}, INTERVAL_TIME);
		
		waitForInterval(3);

		if (totInsideRoutine == 3)
			Scheduler.clearInterval(scheduleId);
		
		assertTrue(totInsideRoutine == 3);
	}
	
	@Test
	public void testClearTimeout() throws InterruptedException {
		long scheduleId = Scheduler.setTimeout(() -> {
			insideRoutine = true;
		}, DELAY_TIME);
		
		Scheduler.clearTimeout(scheduleId);
		
		waitForTimeout();
		
		assertFalse(insideRoutine);
	}
	
	@Test
	public void testClearInterval() throws InterruptedException {
		totInsideRoutine = 0;
		
		long scheduleId = Scheduler.setInterval(() -> {
			totInsideRoutine++;
		}, INTERVAL_TIME);
		
		Scheduler.clearInterval(scheduleId);
		
		waitForInterval(2);
		
		assertTrue(totInsideRoutine == 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTimeoutWithNullRoutine() {
		Scheduler.setTimeout(null, DELAY_TIME);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntervalWithNullRoutine() {
		Scheduler.setInterval(null, INTERVAL_TIME);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTimeoutWithNegativeDelay() {
		Scheduler.setTimeout(() -> {}, -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIntervalWithNegativeDelay() {
		Scheduler.setInterval(() -> {}, -1);
	}

	@Test
	public void testSetTimeoutToRoutine() {
		boolean timeout = Scheduler.setTimeoutToRoutine(() -> {
			while (true)
				;
		}, MAX_WAIT_TIME);

		assertTrue(timeout);
	}

	@Test
	public void testSetTimeoutToRoutine2() {
		boolean timeout = Scheduler.setTimeoutToRoutine(() -> {
			int x = 1;

			while (x >= 0)
				x--;
		}, 99000);

		assertFalse(timeout);
	}


	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------\
	private void waitForInterval(int totalExecutions) throws InterruptedException {
		Thread.sleep((INTERVAL_TIME * totalExecutions) + 100);
	}

	private void waitForTimeout() throws InterruptedException {
		Thread.sleep(DELAY_TIME + 100);
	}
}
