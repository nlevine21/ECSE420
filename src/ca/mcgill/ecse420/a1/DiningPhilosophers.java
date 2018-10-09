package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
	private static Philosopher[] philosophers;
	private static Semaphore[] chopsticks;
	private static Semaphore waitress = new Semaphore(1);
	private static final int SITUATION = 3;
	private static final int NUM_PHILOSOPHERS = 5;
	
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
        
        for(int i = 0; i < NUM_PHILOSOPHERS; i++){
        	executor.execute(philosophers[i]);
        }

	}

	private static Philosopher initializePhilosopher(int i){
		if (SITUATION == 1) {
			return new DeadlockedPhilosopher(i);
		} else if (SITUATION == 2) {
			return new StarvingPhilosopher(i);
		} else  {
			return new SmartPhilosopher(i);
		}
	}

	/**
	 * Class that defines the Philosopher thread
	 *
	 */
	public static abstract class Philosopher implements Runnable {

		int philosopherIndex;
		String state;

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
		 * Method which defines how the philosopher will eat/think
		 */
		public abstract void run();


		/**
		 * Void method that (eventually) acquires a chopstick and prints a statement
		 *
		 * @param chopstick is the index of the chopstick that is being requested for acquisition
		 * */
		protected void pickupChopstick(int chopstick) throws InterruptedException{
			synchronized(chopsticks[chopstick]) {
				chopsticks[chopstick].acquire();
				System.out.println("PHILOSOPHER "+this.philosopherIndex+" gets chopstick "+chopstick);
			}
		}

		/**
		 * Void method that releases a chopstick and prints a statement
		 *
		 * @param chopstick is the index of the chopstick that is being requested for release
		 * */
		protected void dropChopstick(int chopstick) throws InterruptedException{
			System.out.println("PHILOSOPHER "+this.philosopherIndex+" drops chopstick "+chopstick);
			chopsticks[chopstick].release();
		}

		/**
		 * Void method that prints a statement telling the user that the philosopher is thinking
		 *
		 * */
		protected void think(){
			this.state = "THINKING";
			System.out.println("PHILOSOPHER " + this.philosopherIndex + " IS THINKING");
		}

		/**
		 * Void method that prints a statement telling the user that the philosopher is eating
		 *
		 * */
		protected void eat(){
			this.state = "EATING";
			System.out.println("PHILOSOPHER " + this.philosopherIndex + " IS EATING");
		}

		/**
		 * Void method that prints a statement telling the user that the philosopher is hungry
		 *
		 * */
		protected void hungry(){
			this.state = "HUNGRY";
			System.out.println("PHILOSOPHER "+this.philosopherIndex+" IS HUNGRY");
		}

		/**
		 * Returns the index of the left neighbor to the given philosopher
		 *
		 * */
		protected int getLeftNeighbor(){

			if(this.philosopherIndex-1 < 0) {
				return NUM_PHILOSOPHERS-1;
			}

			return this.philosopherIndex-1;
		}

		/**
		 * Returns the index of the right neighbor to the given philosopher
		 *
		 * */
		protected int getRightNeighbor(){
			return (this.philosopherIndex+1)%NUM_PHILOSOPHERS;
		}

	}

	public static class DeadlockedPhilosopher extends Philosopher {

		public DeadlockedPhilosopher(int philosopherIndex) {
			super(philosopherIndex);
		}

		@Override
		/**
		 * Implemented method for dining philosophers
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


	public static class StarvingPhilosopher extends Philosopher {

		public StarvingPhilosopher(int philosopherIndex) {
			super(philosopherIndex);
		}

		@Override
		/**
		 * Implemented method for dining philosophers
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
						// If he cannot acquire this chopstick, he will drop his current chopstick and restart the process
						dropChopstick(this.philosopherIndex);
						continue;
					}


					// At this point, philosopher has acquired both chopsticks
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

	public static class SmartPhilosopher extends Philosopher {

		public SmartPhilosopher (int philosopherIndex) {
			super(philosopherIndex);
		}

		@Override
		/**
		 * Implemented method for dining philosophers
		 */
		public void run() {
			while (true) {
				try {

					hungry();

					// While the philosopher is hungry, continuously try to pickup chopsticks
					while(this.state.equals("HUNGRY")){

						waitress.acquire();


						// If the Philosopher is still hungry and both their left and right neighbours are not eating,
						// pickup both chopsticks and eat
						if(!philosophers[getLeftNeighbor()].state.equals("EATING")
							&& !philosophers[getRightNeighbor()].state.equals("EATING")) {

							pickupChopstick(this.philosopherIndex);
							pickupChopstick(getRightNeighbor());
							eat();
						}

						waitress.release();
					}




					waitress.acquire();

					// Philosopher drops chopsticks
					dropChopstick(this.philosopherIndex);
					dropChopstick(getRightNeighbor());

					// Philosopher thinks
					think();
					waitress.release();



				} catch (InterruptedException e) {

				}
			}
		}


	}

}
