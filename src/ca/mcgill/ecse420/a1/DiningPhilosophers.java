package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	
	private static ReentrantLock[] chopsticks;
	
	public static void main(String[] args) {

		int numberOfPhilosophers = 5;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        chopsticks = new ReentrantLock[numberOfPhilosophers];
        
        for(int i = 0; i < numberOfPhilosophers; i++){
        	philosophers[i] = new Philosopher(i,numberOfPhilosophers);
        	chopsticks[i] = new ReentrantLock();
        }
        
        for(int i = 0; i < numberOfPhilosophers; i++){
        	executor.execute(philosophers[i]);
        }
        
        executor.shutdown();
	}

	public static class Philosopher implements Runnable {
		
		int philosopherIndex;
		int totalNumPhilosophers;
		
		public Philosopher(int philosipherIndex, int totalNumPhilosiphers){
			this.philosopherIndex = philosipherIndex;
			this.totalNumPhilosophers = totalNumPhilosiphers;
		}

		@Override
		public void run() {
			// Philosopher attempts to take the closest chopstick to them
			chopsticks[philosopherIndex].lock();
			
			//Simulate the fact that each philosopher goes for their chopsticks at the same exact time
			//With no sleep, some thread could obtain the lock of the next chopstick before that respective thread can run
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			
			System.out.println("Philosopher " + philosopherIndex + " gets chopstick " + philosopherIndex);
			
			// Philosopher attempts to take the next closest chopstick to them
			chopsticks[(philosopherIndex+1)%totalNumPhilosophers].lock();
			
			System.out.println("Philosopher " + philosopherIndex + " gets chopstick " + (philosopherIndex+1)%totalNumPhilosophers);
			
			// Philosopher thinks
			chopsticks[philosopherIndex].unlock();
			chopsticks[(philosopherIndex+1)%totalNumPhilosophers].unlock();
			
			System.out.println("Philosopher " + philosopherIndex + " done eating!");
		}


	}

}
