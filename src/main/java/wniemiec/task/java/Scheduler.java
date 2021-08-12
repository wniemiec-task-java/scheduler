/**
 * Copyright (c) William Niemiec.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package wniemiec.task.java;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Responsible for calling routines after a certain time or whenever the the 
 * timer expires.
 */
public class Scheduler {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores routines sent to setInterval method along with its ids.
	 */
	private static final Map<Long, Timer> intervalRoutines;
	
	/**
	 * Stores routines sent to setTimeout method along with its ids.
	 */
	private static final Map<Long, Timer> delayRoutines;

	private static final Map<Long, Boolean> timeoutRoutines;
	private static final Map<Long, Future> timeoutRoutineThreads;
	private static Long currentRoutineId;


	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		intervalRoutines = new HashMap<Long, Timer>();
		delayRoutines = new HashMap<Long, Timer>();
		timeoutRoutines = new HashMap<>();
		timeoutRoutineThreads = new HashMap<>();
	}

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private Scheduler() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Sets a timer which executes a routine once the timer expires.
	 * 
	 * @param		routine Routine to be performed
	 * @param		delay Waiting time before the routine is executed (in 
	 * milliseconds)
	 * 
	 * @return		Routine identifier (necessary to be able to stop it)
	 * 
	 * @throws		IllegalArgumentException If routine is null
	 */
	public static long setTimeout(Runnable routine, long delay) {
		if (routine == null)
			throw new IllegalArgumentException("Routine cannot be null");

		if (delay < 0)
			throw new IllegalArgumentException("Delay cannot be negative");

		initializeRoutineId();
		scheduleTimeout(routine, delay);

		return currentRoutineId;
	}

	private static void initializeRoutineId() {
		currentRoutineId = getCurrentTime();
	}
	
	private static void scheduleTimeout(Runnable routine, long delay) {
		if (currentRoutineId == null)
			throw new IllegalStateException("Routine id was not initialized");

		Timer timer = new Timer();
		timer.schedule(createTaskFromRoutine(routine), delay);

		delayRoutines.put(currentRoutineId, timer);
	}

	private static TimerTask createTaskFromRoutine(Runnable routine) {
		return new TimerTask() {
		    @Override
		    public void run() {
		       routine.run();
		    }
		};
	}
	
	/**
	 * Repeatedly calls a routine with a fixed time delay between each call.
	 * 
	 * @param		routine Routine to be performed
	 * @param		interval Interval that the routine will be invoked (in 
	 * milliseconds)
	 * 
	 * @return		Routine identifier (necessary to be able to stop it)
	 * 
	 * @throws		IllegalArgumentException If routine is null
	 */
	public static long setInterval(Runnable routine, long interval) {
		if (routine == null)
			throw new IllegalArgumentException("Routine cannot be null");

		if (interval < 0)
			throw new IllegalArgumentException("Interval cannot be negative");

		initializeRoutineId();
		scheduleInterval(routine, interval);
		
		return currentRoutineId;
	}
	
	private static void scheduleInterval(Runnable routine, long interval) {
		if (currentRoutineId == null)
			throw new IllegalStateException("Routine id was not initialized");

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(createTaskFromRoutine(routine), 0, interval);

		intervalRoutines.put(currentRoutineId, timer);
	}
	
	/**
	 * Cancels a timed, repeating action, which was previously established by a
	 * call to {@link #setInterval(Runnable, long)}.
	 * 
	 * @param		id Routine identifier
	 */
	public static void clearInterval(long id) {
		if (!intervalRoutines.containsKey(id))
			return;
		
		intervalRoutines.get(id).cancel();
		intervalRoutines.remove(id);
	}
	
	/**
	 * Cancels a timed action which was previously established by a
	 * call to {@link #setTimeout(Runnable, long)}.
	 * 
	 * @param		id Routine identifier
	 */
	public static void clearTimeout(long id) {
		if (!delayRoutines.containsKey(id))
			return;
		
		delayRoutines.get(id).cancel();
		delayRoutines.remove(id);
	}
	
	public static void clearAllTimeout() {
		for (Long timeoutId : delayRoutines.keySet()) {
			clearTimeout(timeoutId);
		}
	}
	
	public static void clearAllInterval() {
		for (Long intervalId : intervalRoutines.keySet()) {
			clearInterval(intervalId);
		}
	}

	/**
	 * Runs a routine within a timeout. If the routine does not end on time, an
	 * interrupt signal will be sent to it.
	 *
	 * @param		routine Routine
	 * @param		timeout Maximum execution time (in milliseconds)
	 *
	 * @return		True if the routine has not finished executing within the
	 * time limit; false otherwise
	 *
	 * @throws IllegalArgumentException If routine is null or if timeout is
	 * is negative
	 */
	public static boolean setTimeoutToRoutine(Runnable routine, long timeout) {
		if (routine == null)
			throw new IllegalArgumentException("Routine cannot be null");

		if (timeout < 0)
			throw new IllegalArgumentException("Timeout cannot be negative");

		runRoutine(routine);
		waitRoutineFor(timeout);
		finishRoutine();

		return !hasRoutineFinished();
	}

	private static long getCurrentTime() {
		return new Date().getTime();
	}

	private static void runRoutine(Runnable routine) {
		initializeRoutineId();
		timeoutRoutines.put(currentRoutineId, false);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		Future handler = executor.submit(() -> {
			routine.run();

			if (timeoutRoutines.containsKey(currentRoutineId)) {
				timeoutRoutines.put(currentRoutineId, true);
				timeoutRoutineThreads.remove(currentRoutineId);
			}
		});
		executor.shutdown();

		timeoutRoutineThreads.put(currentRoutineId, handler);
	}

	private static void waitRoutineFor(long time) {
		if (currentRoutineId < 0)
			return;

		long start = getCurrentTime();

		while ((timeElapsedInMilliseconds(start) < time) && !hasRoutineFinished()) {
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}

	private static long timeElapsedInMilliseconds(long start) {
		return (getCurrentTime() - start);
	}

	private static boolean hasRoutineFinished() {
		if (currentRoutineId < 0)
			return true;

		if (!timeoutRoutines.containsKey(currentRoutineId))
			return false;

		return timeoutRoutines.get(currentRoutineId);
	}

	private static void finishRoutine() {
		if (hasRoutineFinished())
			return;

		timeoutRoutines.remove(currentRoutineId);
		timeoutRoutineThreads.get(currentRoutineId).cancel(true);
		timeoutRoutineThreads.remove(currentRoutineId);
	}
}
