package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class DiningPhilosophers {

	private static Philosopher[] philosophers;									// List of threads representing philosophers
	private static Semaphore[] chopsticks;										// Chopsticks needed by philosophers to eat
	private static Semaphore waitress = new Semaphore(1);				// Waitress which grants access to the chopsticks

	private static final int SITUATION = 2;				// Situation wanted. 1 for deadlock. 2 for starvation. 3 for proper.
	private static final int NUM_PHILOSOPHERS = 5;		// Number of philosophers (threads)
	private static final int MAX_SLEEP_TIME = 2000;		// Maximum amount of time the threads can sleep
	
	public static void main(String[] args) {

		// Validate situation
		if (SITUATION > 3 || SITUATION < 1) {
			System.out.println("Invalid situation for dining philosophers!");
			return;
		}

		// Initialize variables
		ExecutorService executor = Executors.newFixedThreadPool(NUM_PHILOSOPHERS);

		philosophers = new Philosopher[NUM_PHILOSOPHERS];
        chopsticks = new Semaphore[NUM_PHILOSOPHERS];
        
        for(int i = 0; i < NUM_PHILOSOPHERS; i++){
        	philosophers[i] = initializePhilosopher(i);
        	chopsticks[i] = new	Semaphore(1);
        }

        // Start threads
        for(int i = 0; i < NUM_PHILOSOPHERS; i++){
        	executor.execute(philosophers[i]);
        }

	}

	/**
	 * Method which sleeps a thread for a random period of time
	 */
	public static void sleep() {
		try {
			Thread.sleep(System.currentTimeMillis() % MAX_SLEEP_TIME);
		} catch (Exception e) {
		}
	}

	/**
	 * Initializes a philosopher object according to the situation
	 *
	 * @param index The index of the philosopher
	 * @return Correct type of Philosopher object
	 */
	private static Philosopher initializePhilosopher(int index) {
		if (SITUATION == 1) {
			return new DeadlockedPhilosopher(index);
		} else if (SITUATION == 2) {
			return new StarvingPhilosopher(index);
		} else  {
			return new SmartPhilosopher(index);
		}
	}

	/**
	 * Abstract class that defines the Philosopher thread
	 *
	 */
	public static abstract class Philosopher implements Runnable {

		int philosopherIndex;		// The index of the philosopher
		String state;				// The state which the philosopher is in (HUNGRY, EATING OR THINKING)

		/**
		 * Philosopher constructor function
		 *
		 * @param philosopherIndex is the index of the philosopher in the philosophers array
		 */
		public Philosopher(int philosopherIndex) {
			this.philosopherIndex = philosopherIndex;
			this.state = "HUNGRY";
		}

		@Override
		/**
		 * Method which defines how the philosopher will eat/think.
		 * To be implemented by inheriting class.
		 */
		public abstract void run();


		/**
		 * Void method that acquires a chopstick when it is available
		 *
		 * @param chopstick is the index of the chopstick that is being requested for acquisition
		 * */
		protected void pickupChopstick(int chopstick) throws InterruptedException {
			synchronized(chopsticks[chopstick]) {
				chopsticks[chopstick].acquire();
			}
		}

		/**
		 * Void method that releases a chopstick
		 *
		 * @param chopstick is the index of the chopstick that is being released
		 * */
		protected void dropChopstick(int chopstick) {
			chopsticks[chopstick].release();
		}

		/**
		 * Void method that sets the philosopher's state to THINKING
		 *
		 * */
		protected void think() {
			this.state = "THINKING";
			System.out.println("PHILOSOPHER " + this.philosopherIndex + " IS THINKING");
		}

		/**
		 * Void method that sets the philosopher's state to EATING
		 *
		 * */
		protected void eat() {
			this.state = "EATING";
			System.out.println("PHILOSOPHER " + this.philosopherIndex + " IS EATING");
		}

		/**
		 * Void method that sets the philosopher's state to HUNGRY
		 *
		 * */
		protected void hungry() {
			this.state = "HUNGRY";
			System.out.println("PHILOSOPHER " + this.philosopherIndex + " IS HUNGRY");
		}

		/**
		 * Returns the index of the left neighbor to the given philosopher
		 *
		 * */
		protected int getLeftNeighbor() {

			if(this.philosopherIndex-1 < 0) {
				return NUM_PHILOSOPHERS-1;
			}

			return this.philosopherIndex-1;
		}

		/**
		 * Returns the index of the right neighbor to the given philosopher
		 *
		 * */
		protected int getRightNeighbor() {

			return (this.philosopherIndex+1)%NUM_PHILOSOPHERS;
		}

	}

	/**
	 * Implemented Philosopher class which will eventually end in Deadlock
	 */
	public static class DeadlockedPhilosopher extends Philosopher {

		public DeadlockedPhilosopher(int philosopherIndex) {

			super(philosopherIndex);
		}

		@Override
		/**
		 * Implemented run method which will end in deadlock
		 */
		public void run() {
			while (true) {
				try {

					hungry();

					// Philosopher attempts to take the closest chopstick to them
					pickupChopstick(philosopherIndex);

					// Philosopher attempts to take the next closest chopstick
					pickupChopstick(getRightNeighbor());

					// Philosopher eats
					eat();

					// Philosopher drops chopsticks
					dropChopstick(philosopherIndex);
					dropChopstick(getRightNeighbor());

					// Philosopher thinks
					think();

				} catch (InterruptedException e) {
				}
			}
		}
	}


	/**
	 * Implemented Philosopher class which can result in Starvation
	 */
	public static class StarvingPhilosopher extends Philosopher {

		public StarvingPhilosopher(int philosopherIndex) {

			super(philosopherIndex);
		}

		@Override
		/**
		 * Implemented run method which could cause starvation
		 */
		public void run() {
			while (true) {
				try {

					hungry();

					// Philosopher attempts to take the closest chopstick to them
					// Will wait until he can acquire the chopstick to continue
					if (!chopsticks[this.philosopherIndex].tryAcquire()) {
						continue;
					}


					// Philosopher attempts to take the chopstick of the right neighbour
					if (!chopsticks[getRightNeighbor()].tryAcquire()) {

						// If he cannot acquire this chopstick, he will wait a random amount of time and try again
						sleep();

						// If he still cannot pick up the chopstick, he drops his current stick
						if (!chopsticks[getRightNeighbor()].tryAcquire()) {
							dropChopstick(this.philosopherIndex);
							continue;
						}
					}


					// At this point, philosopher has acquired both chopsticks
					// Philosopher eats
					eat();

					// Allow time for eating
					sleep();

					// Philosopher drops chopsticks
					dropChopstick(philosopherIndex);
					dropChopstick(getRightNeighbor());

					// Philosopher thinks
					think();
					sleep();

				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * Implemented Philosopher class which will not result in deadlock nor starvation
	 */
	public static class SmartPhilosopher extends Philosopher {

		public SmartPhilosopher (int philosopherIndex) {

			super(philosopherIndex);
		}

		@Override
		/**
		 * Implemented run method for Dining Philosophers which will not result in deadlock or starvation
		 */
		public void run() {
			while (true) {
				try {

					hungry();


					while(this.state.equals("HUNGRY")) {

						// Ask the waitress for permission to pick up the chopsticks
						waitress.acquire();


						// If the Philosopher is still hungry and both their left and right neighbours are not eating,
						// pickup both chopsticks and eat
						if(!philosophers[getLeftNeighbor()].state.equals("EATING")
							&& !philosophers[getRightNeighbor()].state.equals("EATING")) {

							pickupChopstick(this.philosopherIndex);
							pickupChopstick(getRightNeighbor());
							eat();
						}

						// Allow other philosopher's to access the waitress
						waitress.release();
					}

					// Allow for eating time
					sleep();

					// Ask the waitress to release chopsticks
					waitress.acquire();

					// Philosopher drops chopsticks
					dropChopstick(this.philosopherIndex);
					dropChopstick(getRightNeighbor());

					// Other philosophers can acquire the waitress's attention
					waitress.release();

					// Philosopher thinks
					think();
					sleep();



				} catch (InterruptedException e) {

				}
			}
		}


	}

}
