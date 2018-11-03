package ca.mcgill.ecse420.a2;

public class Bakery {
	boolean flag[];
	long[] label;
	int numThreads;
	
	public Bakery(int n){
		flag = new boolean[n];
		label = new long[n];
		numThreads = n;
		for(int i = 0; i < n; i++){
			flag[i] = false;
			label[i] = 0;
		}
	}
	
	public void lock(int threadId){
		flag[threadId] = true;
		long max = 0;
		for(int id = 0; id < numThreads; id++){
			if(label[id] > max)
				max = label[id];
		}
		
		label[threadId] = max+1;
		for(int id = 0; id < numThreads; id++){
			while(id != threadId && flag[id] && lexicoCompare(threadId,id)){
			}
		}
	}
	
	public void unlock(int threadId){
		flag[threadId] = false;
	}
	
	private boolean lexicoCompare(int id1, int id2){
		if(label[id1] > label[id2]) {
			return true;
		}
		else if(label[id1] == label[id2]){
			return id1 > id2;
		}
		return false;
	}
	
}
