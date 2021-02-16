package timetabling_ontology.elements;

import java.util.List;

import jade.content.Concept;
import jade.content.onto.annotations.AggregateSlot;

public class TimeTable implements Concept {
	private String name;
	private List<TimeSlot> tutorialAssignment;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	@AggregateSlot(cardMin = 3, cardMax=3)			// must have 3 slots for each student 1 for each module
//	@AggregateSlot(cardMin = 1)
	public List<TimeSlot> getTutorialAssignment() {
		return tutorialAssignment;
	}
	
	public void setTutorialAssignment(List<TimeSlot> tutorialAssignment) {
		this.tutorialAssignment = tutorialAssignment;
	}
}
