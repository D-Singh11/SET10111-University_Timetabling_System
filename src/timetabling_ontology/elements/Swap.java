package timetabling_ontology.elements;

import javax.swing.Action;

import jade.content.AgentAction;
import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class Swap implements AgentAction {
	private AID owner;
//	private AID studentOffered;
	private TimeSlot timeSlot;
	@Slot (mandatory = true)
	public AID getOwner() {
		return owner;
	}
	
	public void setOwner(AID owner) {
		this.owner = owner;
	}
	@Slot (mandatory = true)
	public TimeSlot getItem() {
		return timeSlot;
	}
	
	public void setItem(TimeSlot slot) {
		this.timeSlot = slot;
	}
	
}
