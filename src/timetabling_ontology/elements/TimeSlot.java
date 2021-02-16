package timetabling_ontology.elements;

import java.util.Date;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class TimeSlot implements Concept {
	private String moduleName;
	private int groupId;					// to store the id of group of students to which this student belongs to. Because no point changing slots with same group students
	
	private int day;
	private int startTime;
	private int endTime;
//	public boolean status;
	private String status;
	
	
	@Slot (mandatory = true)
	public String getModuleName() {
		return this.moduleName;
	}
	
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	
	@Slot (mandatory = true)   // this can be optional bcause groupid is not used by student when created preferences. Only needed for timetabling agent to assign each student to a specific group
	public int getGroupId() {
		return this.groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	
	@Slot (mandatory = true)
	public int getDate() {
		return this.day;
	}
	
	public void setDate(int day) {
		this.day = day;
	}
	
	
	@Slot (mandatory = true)
	public int getStartTime() {
		return this.startTime;
	}
	
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	
	
	
	@Slot (mandatory = true)
	public int getEndTime() {
		return this.endTime;
	}
	
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	
	
	
	@Slot (mandatory = true)
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

}
