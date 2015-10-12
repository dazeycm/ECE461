import java.util.ArrayList;


public class Driver {
	final double PHI = .5;
	final int SRVTIME1 = 5;
	final int SRVTIME2 = 5;
	final int BUFFER = 5;
	final int LAMDA = 8;
	final int NUMPKTS = 10000;
	final int NUMTOSKIP = 5000;
	
	
	double currTime;
	ArrayList<Event> events;
	int buffer1Dropped;
	int buffer2Dropped;
	int pktCount;
	int buffer1Count;
	int buffer2Count;
	
	public void run() {
		events.add(new Event(currTime, "arrival", pktCount, -1));
		pktCount++;
		
		/**
		double sum = 0;
		for(int i = 0; i < 50000; i++) {
			double rand = getExponential(SRVTIME1);
			System.out.println(sum);
			sum+=rand;
			System.out.println(sum/50000.0);
		}
		**/
		
		while(pktCount < NUMPKTS) {
			Event e = findNextEvent();
			currTime = e.time;
			
			if(e.pktNum > NUMTOSKIP) {
				
			}
			
			if(e.type.equals("arrival"))	{
				events.add(new Event(currTime + getExponential(LAMDA), "arrival", pktCount, -1));
				pktCount++;
				
				if(getBinomial() < PHI) {
					if(buffer1Count < BUFFER) {
						buffer1Count++;
						System.out.println("num on line2:" + buffer1Count);
						events.add(new Event(currTime + getExponential(SRVTIME1), "departure", e.pktNum, 1));
						//System.out.println("Packet arrived on line1");
					}
					else {
						//System.out.println("Packet dropped on line1");
						buffer1Dropped++;
					}
				}
				else {
					if(buffer2Count < BUFFER) {
						buffer2Count++;
						System.out.println("num on line2:" + buffer2Count);
						events.add(new Event(currTime + getExponential(SRVTIME2), "departure", e.pktNum, 2));
						System.out.println("Packet arrived on line2");
					}
					else {
						System.out.println("Packet dropped on line2");
						buffer2Dropped++;
					}
				}
			}
			else if(e.type.equals("departure")) {
				if(e.lineNum == 1) {
					buffer1Count--;
					//System.out.println("Packet departed on line1");
				}
				else if(e.lineNum == 2) {
					buffer2Count--;
					System.out.println("Packet departed on line2");
				}
			}
			events.remove(e);
		}
		System.out.println("Dropped on 1:" + buffer1Dropped);
		System.out.println("Dropped on 2:" + buffer2Dropped);
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
		return -(Math.log(Math.random()) / mu); 
	}
	
	public Driver() {
		currTime = 0.0;
		buffer1Dropped = 0;
		buffer2Dropped = 0;
		events = new ArrayList<Event>();
		pktCount = 1;
		buffer1Count = 0;
		buffer2Count = 0;
		
		run();
	}
	
	public static void main(String[] args) {
		new Driver();
	}

}
