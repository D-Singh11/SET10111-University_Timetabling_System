package set10111.simulation;

import jade.core.AID;
import timetabling_ontology.elements.TimeSlot;

public class Offer {
	private AID offeredBy;
	private TimeSlot slot;
	
	public Offer(AID offeredBy, TimeSlot slot) {
		super();
		this.offeredBy = offeredBy;
		this.slot = slot;
	}
	
	public AID getOfferedByAID() {
		return offeredBy;
	}
	
	public TimeSlot getSlot() {
		return slot;
	}
	
}
