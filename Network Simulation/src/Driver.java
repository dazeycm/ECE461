import java.util.ArrayList;

public class Driver {
	final double PHI = .5;
	final int SRVTIME1 = 5;
	final int SRVTIME2 = 5;
	final int BUFFER = 20;
	final int LAMDA = 8;
	final int NUMPKTS = 50;
	final int NUMTOSKIP = 0;
	
	
	double currTime;
	double lastDepart;
	ArrayList<Event> events;
	int pktCount;
	int buffer1Count;
	int buffer2Count;
	
	int buffer1Dropped;
	int buffer2Dropped;
	int buffer1Serviced;
	int buffer2Serviced;
	ArrayList<Double> serviceTimesBuf1;
	ArrayList<Double> serviceTimesBuf2;
	
	//ArrayList<Double> serviceTimes;
	//ArrayList<Double> arrivalTimes;
	
	
	public void run() {
		events.add(new Event(currTime, "arrival", pktCount, -1));
		pktCount++;
		
		/**
		double sum = 0;
		int count = 0;
		for(int i = 0; i < 50000; i++) {
			double rand = getExponential(SRVTIME1);
			if (rand > .125)
				count++;
			//System.out.println(rand);
			sum+=rand;
		}
		System.out.println(sum/50000.0);
		System.out.println(count);
		**/
		
		double firstTimeToTrack = 0;
		while(!events.isEmpty()) {
			Event e = findNextEvent();
			currTime = e.time;
			
			System.out.println(lastDepart);
			if(e.pktNum == NUMTOSKIP) {	//reset stats while getting system to steady state
				buffer1Dropped = 0;
				buffer2Dropped = 0;
				buffer1Serviced = 0;
				buffer2Serviced = 0;
				serviceTimesBuf1.clear();
				serviceTimesBuf2.clear();
				firstTimeToTrack = e.time;
			}
			
			if(e.type.equals("arrival"))	{
				if(pktCount <= NUMPKTS) {
					double nextPacketTime = getExponential(LAMDA);
					events.add(new Event(currTime + nextPacketTime, "arrival", pktCount, -1));
					pktCount++;
				}
				
				double chance = getBinomial();
				if(chance <= PHI) {
					if(buffer1Count < BUFFER) {
						buffer1Count++;
						double timeToWait = getExponential(SRVTIME1);
						events.add(new Event(lastDepart + timeToWait, "departure", e.pktNum, 1));
						lastDepart += timeToWait;
						serviceTimesBuf1.add(timeToWait);
					}
					else {
						buffer1Dropped++;
					}
				}
				else {
					if(buffer2Count < BUFFER) {
						buffer2Count++;
						double timeToWait = getExponential(SRVTIME2);
						events.add(new Event(lastDepart + timeToWait, "departure", e.pktNum, 2));
						lastDepart += timeToWait;
						serviceTimesBuf2.add(timeToWait);
					}
					else {
						buffer2Dropped++;
					}
					
				}
			}
			else if(e.type.equals("departure")) {
				if(e.lineNum == 1) {
					buffer1Count--;
					buffer1Serviced++;
				}
				else if(e.lineNum == 2) {
					buffer2Count--;
					buffer2Serviced++;
				}
				else {
					System.out.println("Should never get here!");
				}
			}
			else {
				System.out.println("Should never get here!");
			}
			events.remove(e);
		}
		
		
		System.out.println("Blocking probability, buffer 1: " + buffer1Dropped/(double)buffer1Serviced);
		System.out.println("Blocking probability, buffer 2: " + buffer2Dropped/(double)buffer2Serviced);
		System.out.println("Blocking probability, system: " + (buffer1Dropped+buffer2Dropped)/(double)NUMPKTS);
		System.out.println("=========================================================");
		System.out.println("Average delay, buffer 1: " + serviceTimesBuf1.stream().mapToDouble(Double::doubleValue).sum()/buffer1Serviced);
		System.out.println("Average delay, buffer 2: " + serviceTimesBuf2.stream().mapToDouble(Double::doubleValue).sum()/buffer2Serviced);
		System.out.println("Average delay, system: " + (serviceTimesBuf1.stream().mapToDouble(Double::doubleValue).sum() + serviceTimesBuf2.stream().mapToDouble(Double::doubleValue).sum()) / (buffer1Serviced + buffer2Serviced));
		System.out.println("=========================================================");
		System.out.println("Throughput, buffer1: " + buffer1Serviced / (currTime - firstTimeToTrack));
		System.out.println("Throughput, buffer2: " + buffer2Serviced / (currTime - firstTimeToTrack));
		System.out.println("Throughput, system: " + (buffer1Serviced + buffer2Serviced) / (currTime - firstTimeToTrack));
		System.out.println("========================================================");
		System.out.println("Dropped on 1:" + buffer1Dropped);
		System.out.println("Dropped on 2:" + buffer2Dropped);
		System.out.println("Buffer 1 Serviced: " + buffer1Serviced);
		System.out.println("Buffer 2 Serviced: " + buffer2Serviced);
		//System.out.println(currTime);
		
		//System.out.println("Service Times::::::");
		//System.out.println(serviceTimes.stream().mapToDouble(Double::doubleValue).sum() / 150000);
		//System.out.println("Arrival Times::::::");
		//System.out.println(arrivalTimes.stream().mapToDouble(Double::doubleValue).sum() / 150000);
	}
	
	public Event findNextEvent() {
		Event ret = events.get(0);
		for(Event e : events) {
			if(e.time < ret.time) {
				ret = e;
			}
		}
		return ret;
	}
	
	public double getPoisson(double lamda) {
		int r = 0;
	    double a = Math.random();
	    double p = Math.exp(-lamda);

	    while (a > p) {
	        r++;
	        a = a - p;
	        p = p * lamda / r;
	    }
	    
	    return r;
	}
	
	public double getBinomial() {
		return Math.random();
	}
	
	public double getExponential(double mu) {
		return (Math.log(Math.random()) / -mu); 
	}
	
	public Driver() {
		currTime = 0.0;
		lastDepart = 0.0;
		buffer1Dropped = 0;
		buffer2Dropped = 0;
		events = new ArrayList<Event>();
		pktCount = 1;
		buffer1Count = 0;
		buffer2Count = 0;
		buffer1Serviced = 0;
		buffer2Serviced = 0;
		serviceTimesBuf1 = new ArrayList<Double>();
		serviceTimesBuf2 = new ArrayList<Double>();
		
		
		//serviceTimes = new ArrayList<Double>();
		//arrivalTimes = new ArrayList<Double>();
		
		run();
	}
	
	public static void main(String[] args) {
		new Driver();
	}

}
