package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	
	private static Semaphore[] chopsticks;
	private static Semaphore waiter = new Semaphore(1);
	private static Semaphore stoppedEating = new Semaphore(1);
	private static int eaters = 0;
	
	
	public static void main(String[] args) {

		// Initialize variables
		int numberOfPhilosophers = 5;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        chopsticks = new Semaphore[numberOfPhilosophers];
        
        // Decide whether you would like to see the situation in deadlock (0), starving (1), or neither (2)
        int situation = 2;
        
        for(int i = 0; i < numberOfPhilosophers; i++){
        	philosophers[i] = new Philosopher(i,numberOfPhilosophers, situation);
        	chopsticks[i] = new	Semaphore(1);
        }
        
        for(int i = 0; i < numberOfPhilosophers; i++){
        	executor.execute(philosophers[i]);
        }

	}

	public static class Philosopher implements Runnable {
		
		int philosopherIndex;
	    int totalNumPhilosophers;
		int situation;
		String state;
		
		public Philosopher(int philosipherIndex, int totalNumPhilosiphers, int situation){
			this.philosopherIndex = philosipherIndex;
			this.totalNumPhilosophers = totalNumPhilosiphers;
			this.situation = situation;
		}

		@Override
		public void run() {
			
			while(true){
				try{
					// Situation which causes deadlock - each philosopher goes directly for their chopstick and then the chopstick to their right
					if(situation == 0){
						// Philosopher attempts to take the closest chopstick to them
						pickupChopstick(philosopherIndex,philosopherIndex);
						
						//Simulate the fact that each philosopher goes for their chopsticks at the same exact time
						//With no sleep, some thread could obtain the lock of the next chopstick before that respective thread can run
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
										
						// Philosopher attempts to take the next closest chopstick
						pickupChopstick(philosopherIndex,(philosopherIndex+1)%totalNumPhilosophers);
						
						// Philosopher eats
						eat(philosopherIndex);
						
						// Philosopher puts down their chopsticks
						dropChopsticks(philosopherIndex);
						
						// Philosopher thinks
						think(philosopherIndex);
					}
					
					// Situation which causes starvation, but no deadlock - let the last philosopher think before picking up their chopstick
					else if(situation == 1){
						if(philosopherIndex == totalNumPhilosophers-1){
							//while(availableChopstick(philosopherIndex) || availableChopstick((philosopherIndex+1)%totalNumPhilosophers)){}
						}
						
						// Philosopher picks up chopsticks
						pickupChopsticks(philosopherIndex);
						
						// Philosopher eats
						eat(philosopherIndex);
						
						// Philosopher drops chopsticks
						dropChopsticks(philosopherIndex);
						
						// Philosopher thinks
						think(philosopherIndex);
						
					}
					
					
					// Situation which causes neither deadlock nor starvation
					else if(situation == 2){
						
					// Philosopher waits for waiters permission to take chopsticks
					System.out.println("PHILOSOPHER " + philosopherIndex + " IS HUNGRY");
					getWaiterPermission();
					
					// Philosopher takes chopsticks nearest to them
					pickupChopsticks(philosopherIndex);
					
					// Philosopher eats
					eat(philosopherIndex);
					
					// Philosopher drops chopsticks
					dropChopsticks(philosopherIndex);
					
					// Philosopher notifies waiter that they have dropped their chopsticks
					informWaiter();
					
					// Philosopher thinks
					think(philosopherIndex);
					
					}
				} catch (InterruptedException e){}
				
			}
		}
		
		private void getWaiterPermission() throws InterruptedException{
			synchronized(waiter){
				synchronized(stoppedEating){
					// Wait to get waiters attention
					waiter.acquire();
					
					// Increment the total number of eaters
					eaters++;
					
					// If there are too many eaters, make them wait until another philosopher stops eating
					// Otherwise, let the waiter continue to delegate
					if(eaters > totalNumPhilosophers-1){
						waiter.release();
						stoppedEating.acquire();
					}else
						waiter.release();			
				}
			}
		}
		
		private void informWaiter() throws InterruptedException{
			synchronized(waiter){
				synchronized(stoppedEating){
					// Wait for waiters attention
					waiter.acquire();
					
					// Notify that there is one less eater
					eaters--;
					
					// If the number of eaters previously exceeded the allowed amount and caused a philosopher to wait for them to stop, notify that they have stopped
					if(eaters == totalNumPhilosophers-1){
						stoppedEating.release();
					}
					
					// Let the waiter continue to delegate
					waiter.release();
				}
			}
		}
		
		
		private void pickupChopstick(int philosopher, int chopstick) throws InterruptedException{
			synchronized(chopsticks){
				chopsticks[chopstick].acquire();
				System.out.println("PHILOSOPHER "+philosopher+" gets chopstick "+chopstick);
			}
		}
		
		private void pickupChopsticks(int philosopher) throws InterruptedException{
			synchronized(chopsticks){
				chopsticks[philosopher].acquire();
				System.out.println("PHILOSOPHER "+philosopher+" gets chopstick "+philosopherIndex);
				chopsticks[(philosopherIndex+1)%totalNumPhilosophers].acquire();
				System.out.println("PHILOSOPHER "+philosopher+" gets chopstick "+(philosopherIndex+1)%totalNumPhilosophers);
			}
		}
		
		private void dropChopsticks(int philosopher){
				chopsticks[philosopher].release();
				chopsticks[(philosopherIndex+1)%totalNumPhilosophers].release();
				System.out.println("PHILOSOPHER "+philosopher+" puts down chopstick "+philosopher+" and chopstick "+(philosopherIndex+1)%totalNumPhilosophers);
		}
		
		private void think(int philosopher){
			System.out.println("PHILOSOPHER " + philosopherIndex + " IS THINKING");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
		}
		
		private void eat(int philosopher){
			System.out.println("PHILOSOPHER " + philosopherIndex + " IS EATING");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
		}

	}

}
