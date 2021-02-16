package timetabling_ontology.elements;

import jade.content.AgentAction;
import jade.content.Predicate;
import jade.core.AID;

public class AgreeSwap implements Predicate {
		private TimeSlot timeSlot;
		private boolean response;
		
		public boolean getResponse() {
			return response;
		}
		
		public void setResponse(boolean response) {
			this.response = response;
		}
		
		public TimeSlot getItem() {
			return timeSlot;
		}
		
		public void setItem(TimeSlot slot) {
			this.timeSlot = slot;
		}

}
