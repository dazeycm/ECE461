
public class Event {
	double time;
	String type;
	int pktNum;
	int lineNum;
	
	public Event(double time, String type, int pktNum, int lineNum) {
		this.time = time;
		this.type = type;
		this.pktNum = pktNum;
		this.lineNum = lineNum; // -1 for scheduling arrival
		
	}
	
	@Override
	public String toString() {
		return "TIME: " + time + "     TYPE: " + type + "     PKTNUM: " + pktNum + "     LINENUM: " + lineNum;
	}
}
