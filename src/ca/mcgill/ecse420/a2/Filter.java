package ca.mcgill.ecse420.a2;

public class Filter {
	int[] level;
	int[] victim;
	int numThreads;
	
	public Filter(int n) {
		level = new int[n];
		victim = new int[n];
		numThreads = n;

		for(int i = 0; i < n; i++) {
			level[i] = 0;
		}
	}
	
	public void lock(int threadId) {
		for(int i = 1; i < numThreads; i++) {
			level[threadId] = i;
			victim[i] = threadId;

			for(int id = 0; id < numThreads; id++) {
				while(id != threadId && level[id] >= i && victim[i] == threadId);
			}
		}
	}
	
	public void unlock(int threadId) {
		level[threadId] = 0;
	}
}
