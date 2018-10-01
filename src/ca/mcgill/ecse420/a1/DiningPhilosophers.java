package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
	private static Philosopher[] philosophers;
	private static Semaphore[] chopsticks;
	private static Semaphore mutex = new Semaphore(1);
	private static int situation;
	private static final int NUM_PHILOSOPHERS = 5;
	
	public static void main(String[] args) {

		// Initialize variables
		ExecutorService executor = Executors.newFixedThreadPool(NUM_PHILOSOPHERS);
        philosophers = new Philosopher[NUM_PHILOSOPHERS];
        chopsticks = new Semaphore[NUM_PHILOSOPHERS];
        
        // Decide whether you would like to see the situation in deadlock (0), where some threads may starve (1), or neither (2)
        situation = 2;
        
        for(int i = 0; i < NUM_PHILOSOPHERS; i++){
        	philosophers[i] = new Philosopher(i);
        	chopsticks[i] = new	Semaphore(1);
        }
        
        for(int i = 0; i < NUM_PHILOSOPHERS; i++){
        	executor.execute(philosophers[i]);
        }

	}
	
	/**
	 * Class that defines the Philosopher thread
	 *
	 */
	public static class Philosopher implements Runnable {
		
		int philosopherIndex;
		String state;
		
		/**
		 * Philosopher constructor function
		 * 
		 * @param philosipherIndex is the index of the philosopher in the philosophers array
		 * @param totalNumPhilosiphers is the total number of philosophers
		 * @param situation is the situation in which this philosopher will run under/determines which solution will be used
		 */
		public Philosopher(int philosipherIndex){
			this.philosopherIndex = philosipherIndex;
			this.state = "HUNGRY";
		}

		@Override
		public void run() {
			
			while(true){
				try{
					/* Situation 0 is that of which ends in deadlock. Each philosopher picks up their chopstick first and then goes for the chopstick to their right (or next index).
					 * However, they will not be able to obtain the adjacent chopstick as their neighbour will hold it, and thus will wait until it becomes available. Because
					 * there are not rules to manage this situation, each Philosopher will wait on another to give up their chopstick, which ultimately will never happen. The
					 * eat, drop, and think methods are written out despite knowing that the code will never reach those steps to demonstrate that the program is in deadlock -
					 * each of the aforementioned methods have print statements, which will never be seen.
					 * */
					if(situation == 0){
						// Philosopher attempts to take the closest chopstick to them
						pickupChopstick(philosopherIndex);
						
						//Simulate the fact that each philosopher goes for their chopsticks at the same exact time
						//With no sleep, some thread could obtain the lock of the next chopstick before that respective thread can run
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
										
						// Philosopher attempts to take the next closest chopstick
						pickupChopstick(rightNeighbor());
						
						// Philosopher eats
						eat();
						
						// Philosopher drops chopsticks
						dropChopstick(philosopherIndex);
						dropChopstick(rightNeighbor());
						
						// Philosopher thinks
						think();
					}
					/* Situtation 2 is that of which will never end in deadlock, however has potential to cause starvation in some threads at some point. The Philosopher picks
					 * up their chopstick and then reaches for the chopstick to their right (or next index). They will continuously try to pickup the next chopstick until they
					 * have waited a random period of time, in which case they will drop their chopstick, wait another random period of time, and go through the same process
					 * again. This will thus give the chance of another Philosopher to pickup the chopstick that they need to eat in order for them to drop their own chopstick and
					 * allow for other Philosophers to eat as well (and so on). This solution will never end in deadlock as there are always enough chopsticks available to be
					 * effectively used and passed on to another Philosopher who has been waiting for it. However, the solution might cause starvation if a specific
					 * Philosopher is "unlucky" and the chopsticks they need are constantly in use and being swapped by other Philosophers while that Philosopher is waiting to begin
					 * the process again. This can technically happen for a long period of time and cause that Philosopher to starve.
					 * */
					else if(situation == 1){
						// Philosopher attempts to pickup both chopsticks
						while(true){
							// Philosopher attempts to take the closes chopstick to them
							pickupChopstick(this.philosopherIndex);
							
							// Philosopher attempts to pickup the next closest chopstick,
							// however if they cannot after a random period of time they drop the chopstick they currently have
							long startTime;
							boolean droppedChopstick = false;
							startTime = System.currentTimeMillis();
							while(!chopsticks[rightNeighbor()].tryAcquire()){
								
								// If philosopher has held their chopstick for too long without acquiring the next, drop it
								if(System.currentTimeMillis() - startTime > Math.random()*10000){
									
									// Philosopher drops the chopstick they currently hold
									dropChopstick(this.philosopherIndex);
									System.out.println("PHILOSOPHER " + this.philosopherIndex + " could not get the next chopstick, so dropped their own");
									
									// Philosopher waits, and then will try to acquire their chopstick again
									Thread.sleep((long) (Math.random()*10000));
									droppedChopstick = true;
									break;
								}
							}
							
							// If the chopstick was not dropped and the acquire attempt was successful, begin eating
							if(!droppedChopstick) {
								System.out.println("PHILOSOPHER "+this.philosopherIndex+" gets chopstick "+rightNeighbor());
								break;
							}
						}
						
						// Philosopher eats
						eat();
						
						// Philosopher drops chopsticks
						dropChopstick(philosopherIndex);
						dropChopstick(rightNeighbor());
						
						// Philosopher thinks
						think();
						
					} 
					/* Situation 2 is that of which causes no deadlock nor starvation. This solution forces the philosophers to be aware of the state of their neighbors 
					 * (HUNGRY, EATING, THINKING) and to act accordingly. A Philosopher will only take both chopsticks if they are both available, ie. either neighbor is not 
					 * eating and thus are not utilizing the chopsticks they will need. While a Philosopher does not have either chopstick, they wait until it is their turn 
					 * to acquire them. Once they eat and drop their chopsticks, the next Philosopher in line will have access to the chopsticks they have been waiting for. 
					 * There is no direct implementation of a queue in this solution, however the use of semaphores simulates similar functionality in that the system knows 
					 * when the resource was requested and by which Philosopher, and they will pickup their chopsticks when it is their turn. Thus, all Philosophers will have 
					 * their turn to eat without the potential of starving as there is no random variable thats causing them to miss their turn (unlike the previous solution).
					 * */
					else if(situation == 2){
						// While the philosopher is hungry, continuously try to pickup chopsticks
						this.state = "HUNGRY";
						System.out.println("PHILOSOPHER "+this.philosopherIndex+" IS HUNGRY");
						while(this.state.equals("HUNGRY")){
							mutex.acquire();
							
							// If the Philosopher is still hungry and both their left and right neighbours are not eating, pickup both chopsticks
							if(this.state.equals("HUNGRY") 
									&& !philosophers[leftNeighbor()].state.equals("EATING")
									&& !philosophers[rightNeighbor()].state.equals("EATING")){
								this.state = "EATING";
								pickupChopstick(this.philosopherIndex);
								pickupChopstick(rightNeighbor());
							}
							mutex.release();
						}
						
						// Philosopher eats
						eat();
						
						// Philosopher drops chopsticks
						mutex.acquire();
						this.state = "THINKING";
						dropChopstick(this.philosopherIndex);
						dropChopstick(rightNeighbor());
						mutex.release();
						
						// Philosopher thinks
						think();
					}
				} catch (InterruptedException e){}	
			}
		}
		
		/**
		 * Void method that (eventually) acquires a chopstick and prints a statement
		 * 
		 * @param chopstick is the index of the chopstick that is being requested for acquisition
		 * */
		private void pickupChopstick(int chopstick) throws InterruptedException{
			synchronized(chopsticks){
				chopsticks[chopstick].acquire();
				System.out.println("PHILOSOPHER "+this.philosopherIndex+" gets chopstick "+chopstick);
			}
		}
		
		/**
		 * Void method that releases a chopstick and prints a statement
		 * 
		 * @param chopstick is the index of the chopstick that is being requested for release
		 * */
		private void dropChopstick(int chopstick) throws InterruptedException{
			System.out.println("PHILOSOPHER "+this.philosopherIndex+" drops chopstick "+chopstick);
			chopsticks[chopstick].release();
		}
		
		/**
		 * Void method that prints a statement telling the user that the philosopher is thinking and puts the thread to sleep
		 * 
		 * */
		private void think(){
			System.out.println("PHILOSOPHER " + this.philosopherIndex + " IS THINKING");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}
		}
		
		/**
		 * Void method that prints a statement telling the user that the philosopher is eating and puts the thread to sleep
		 * 
		 * */
		private void eat(){
			System.out.println("PHILOSOPHER " + this.philosopherIndex + " IS EATING");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}
		}
		
		/**
		 * Returns the index of the "left" neighbor to the given philosopher
		 * 
		 * */
		private int leftNeighbor(){
			if(this.philosopherIndex-1 < 0) return NUM_PHILOSOPHERS-1;
			return this.philosopherIndex-1;
		}
		
		private int rightNeighbor(){
			return (this.philosopherIndex+1)%NUM_PHILOSOPHERS;
		}

	}

}
