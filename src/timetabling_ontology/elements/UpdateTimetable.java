package timetabling_ontology.elements;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class UpdateTimetable implements AgentAction {
	private AID studentRequested;
	private AID studentOffered;
	private TimeSlot timeslot;
	
	public AID getStudentRequested() {
		return studentRequested;
	}
	
	public void setStudentRequested(AID studentRequested) {
		this.studentRequested = studentRequested;
	}
	@Slot (mandatory = true)
	public AID getStudentOffered() {
		return studentOffered;
	}
	@Slot (mandatory = true)
	public void setStudentOffered(AID studentOffered) {
		this.studentOffered = studentOffered;
	}
	@Slot (mandatory = true)
	public TimeSlot getTimeSlot() {
		return timeslot;
	}
	
	public void setTimeSlot(TimeSlot timeslot) {
		this.timeslot = timeslot;
	}	
	
}