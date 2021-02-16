package timetabling_ontology.elements;

import jade.content.AgentAction;
import jade.core.AID;

public class AcceptTimetable implements AgentAction {
	private AID receiver;
	private TimeTable timetable;
	
	public AID getSenderAgentAid() {
		return receiver;
	}
	
	public void setSenderAgentAid(AID receiver) {
		this.receiver = receiver;
	}
	
	public TimeTable getTimeTable() {
		return timetable;
	}
	
	public void setTimeTable(TimeTable timetable) {
		this.timetable = timetable;
	}	

}
