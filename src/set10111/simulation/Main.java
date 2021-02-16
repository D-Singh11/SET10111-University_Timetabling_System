package set10111.simulation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jade.content.Concept;
import jade.content.onto.annotations.AggregateSlot;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import timetabling_ontology.elements.TimeSlot;


public class Main {
	
	
	private static ArrayList<String> studentAgents = new ArrayList<>();
	static ArrayList<String> moduleNames = new ArrayList<String>();
	private  static HashMap<String, Student> studentList = new HashMap<String, Student>();
//	static HashMap<String, TimeSlot> myPreferences = new HashMap<String, TimeSlot>();
	static ArrayList<HashMap<String, String>> allPrefs = new ArrayList<HashMap<String,String>>();
	static Random random = new Random();
	
	
//	Note : Only owner of the time slot to be swapped should tell timetabling agent to swap the slots?
	// what should be included in the Request from the student who want other agent to swap the slot when other student confirm that he is the owner of enquired slot in the previous call
	// I mean request before sending final swap action.
	
	
//	once state updated by timetabling agent should timetabling re-send the timetable to concerned students or should student update their timetable locally themselves. It is not prefeences but a copy of 
	// student's timetable stored locally in student agent class which is sent by the timetabling agent when system starts
	
	// ask should we have preferences separately for each module or just once
	
	
	//You should pass preferences to student agent here in main function
	// Also can generate time table for tutorials here in main and then pass it to timetabling agent

	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
//		ArrayList<String> moduleNames = new ArrayList<String>();
//		HashMap<String, Student> studentList = new HashMap<String, Student>();
		Random random = new Random();
		try{		
			ContainerController myContainer = myRuntime.createMainContainer(myProfile);	
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();		
//*********************************** Run with testing 
			// change the number when testing diffrent test cases to set to the number of agents invplved in test case.
			// It is only used for test cases, in normal scenario it uncomment run without test code
			int numStudents = 2;
			for (int i = 0; i <numStudents; i++) {
				studentAgents.add(""+i);
				HashMap<String, String> pref= PreferenceGenerator2();
				
				allPrefs.add(pref);
			}
			
			
			// for 2 modules change the module assignment to 2 modules in TA agent.
//			TestNoSwapAlreadySatisfied();
//			Test2Modules2Swap();
			
			// 2-agents
//			Test3Modules3Swap();
//			Test3ModulesButSwap2();
			Test3ModulesButSwap1();
//			Test3ModulesNoSwapNeeded();
					
			// all deal with 3 agents
//			_3Modules_3Agents_only_2SwapSlots();
			
			
			
//			create specified number of student agents without hardcoding every single student agent
			AgentController studentAgent;
			for(int i=0; i<numStudents; i++) {
				int studentIdentifier = i+1;
				
				Customm myPref = new Customm();
				myPref.preferences = allPrefs.get(i);
				
				Customm[] pass = {myPref};
				String studentName = "Student" + studentIdentifier;
				studentAgent = myContainer.createNewAgent("Student" + studentIdentifier, StudentAgent.class.getCanonicalName(), pass);
				studentAgent.start();
				
//				studentAgents.set(i, studentName);
			}
			
//			generateTimetable();
			Customm nn = new Customm();
			nn.timetable = studentList;
			Customm[] c = {nn};
			System.out.println(c);
			
			
			AgentController timetablingAgent = myContainer.createNewAgent("timetablingAgent1", TimeTablingAgent.class.getCanonicalName(), c);
			timetablingAgent.start();
			
			
//*********************************** Run with testing 	end		
					
//***************************************************
//			Run without testing
			
//			
//			AgentController timetablingAgent = myContainer.createNewAgent("timetablingAgent1", TimeTablingAgent.class.getCanonicalName(), null);
//			timetablingAgent.start();
//			
//			
//			AgentController tickerAgent = myContainer.createNewAgent("ticker", BuyerSellerTicker.class.getCanonicalName(),
//					null);
//			tickerAgent.start();
//			
//			
//			AgentController studentAgent;
//			int numStudents = 2;
//			for(int i=0; i<numStudents; i++) {
//				int studentIdentifier = i+1;
//				studentAgent = myContainer.createNewAgent("Student" + studentIdentifier, StudentAgentNew.class.getCanonicalName(), null);
//				studentAgent.start();
//			}
			
			
			//****** Moved to timetabling agent ************
			
//			// assign random time slots and random tutorial groups to each student for each module
//			for (String name : moduleNames) {
//				for (Student student : studentList) {
//					int groupId = random.nextInt(2);			// generate random number between 0 and 1
//					Date day = new Date(2);
//					
//					int startTime = random.nextInt(9) + 9;
//					int endTime = startTime + 1;
//					
//					TimeSlot slot = new TimeSlot(name, groupId, day, startTime, endTime, "AsiignedByTA");
//					
//					Module module = new Module(name, slot);
//					
//					// add this slot to the module list of student
//					student.moduleList.add(module);
//					
//					System.out.println(student.name);
//					System.out.println(" slot : "+ slot.getModuleName() + " " + slot.getGroupId()  + " " +  slot.getDate() + " " + slot.getStartTime()+ " " + slot.getEndTime());
//					
//				}
//			}
			
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}


	}
	
	private static void timetableForTesting() {
		HashMap<String, Student> studentList = new HashMap<>();
		
		//create student1
		Student student = new Student();
		String sa = studentAgents.get(0);
		student.name = sa;
		student.moduleList = new HashMap<String,TimeSlot>();
		
		
		String moduleName = "Module1";
		
		
		int startTime = random.nextInt(9) + 9;
		int endTime = startTime + 1;
		int day = random.nextInt(5) + 1; // set the day of the tutorial
		TimeSlot slot = new TimeSlot(); slot.setModuleName(moduleName); slot.setGroupId(0); // set to 0 because will be updated when actual timeslot is assigned
		slot.setDate(day); slot.setStartTime(startTime); slot.setEndTime(endTime); slot.setStatus("AsiignedByTA");
		
		student.moduleList.put(moduleName, slot);
		
		// add to student list
		studentList.put(sa, student);
		
		
		
		
		//Student 2
		student = new Student();
		sa = studentAgents.get(1);
		student.name = sa;
		student.moduleList = new HashMap<String,TimeSlot>();
		
		
		moduleName = "Module1";
		
		
		startTime = random.nextInt(9) + 9;
		endTime = startTime + 1;
		day = random.nextInt(5) + 1; // set the day of the tutorial
		slot = new TimeSlot(); slot.setModuleName(moduleName); slot.setGroupId(0); // set to 0 because will be updated when actual timeslot is assigned
		slot.setDate(day); slot.setStartTime(startTime); slot.setEndTime(endTime); slot.setStatus("AsiignedByTA");
		
		student.moduleList.put(moduleName, slot);
		
		// add to student list
		studentList.put(sa, student);
	}
	
	private static Customm generateTimetable() {
		studentList = new HashMap<String, Student>();

		moduleNames.clear();

//		// add new dummy module
		moduleNames.add("Module1");
		moduleNames.add("Module2");
		moduleNames.add("Module3");

		// create students list based upon number of student agents in system
		for (String sa : studentAgents) {
			Student student = new Student();
			student.name = sa;
			student.moduleList = new HashMap<String,TimeSlot>();

			// add to student list
			studentList.put(sa, student);

		}

		// Now assign random time slots and random tutorial groups to each student for
		// each module
//		for (String moduleName : moduleNames) {
//			System.out.println("");
//			System.out.println(moduleName);
//			System.out.println("---------");
//
//			int totalStudentG1 = 0;
//			int totalStudentG2 = 0;

			// generate 2 time slots for each tutorial
//			ArrayList<TimeSlot> tutTime = new ArrayList<TimeSlot>();
//			for (int i = 0; i < 2; i++) {
//
//				int startTime = random.nextInt(9) + 9;
//				int endTime = startTime + 1;
//				int day = random.nextInt(5) + 1; // set the day of the tutorial
//
//				TimeSlot slot = new TimeSlot();
//				slot.setModuleName(moduleName);
//				slot.setGroupId(0); // set to 0 because will be updated when actual timeslot is assigned
//				slot.setDate(day);
//				slot.setStartTime(startTime);
//				slot.setEndTime(endTime);
//				slot.setStatus("AsiignedByTA");
//				tutTime.add(slot); // adding dummy slot to tutorial times arraylist
//			}
//
//			// assign each student to random timeslot of tutorial
//			for (String aid : studentAgents) {
//				Student student = studentList.get(aid);
//
//				int groupId = random.nextInt(2); // generate random number between 0 and 1
//
//				TimeSlot slot = tutTime.get(groupId);
//				slot.setGroupId(groupId + 1); // assign the group id to slot. adding one because want tp statr
//												// group ids from 1 instead of 0
//
//
//				// add this slot to the module list of student
//				studentList.get(aid).moduleList.put(moduleName, slot);
//
//				if (groupId == 0) {
//					totalStudentG1++;
//				} else {
//					totalStudentG2++;
//				}
//
////				System.out.println(student.name);
////				System.out.println(
////						student.name + "   ----   slot : " + slot.getModuleName() + " " + slot.getGroupId()
////								+ " " + slot.getDate() + " " + slot.getStartTime() + " " + slot.getEndTime());
//			}
			
	

//			System.out.println("Total Students enrolled on module : " + studentAgents.size());
//			System.out.println(" student in Group 1 : " + totalStudentG1);
//			System.out.println(" student in Group 2 : " + totalStudentG2);
//		}
		
		
		Customm testTimetable = new Customm();
		testTimetable.timetable = studentList;
		return testTimetable;
	}
	
	
	public static HashMap<String, String> PreferenceGenerator(){
		 
		HashMap<String, String> myPreferences = new HashMap<String, String>();
		try {

			ArrayList<String> prefCodes = new ArrayList<String>();
			prefCodes.add("outside work or caring commitments");
			prefCodes.add("prefer not to attend");
			prefCodes.add("like to have");


			for (int i = 0; i < 5; i++) { // add preferences for 5 days

				int day = i + 1;

				// assign time slots for each day for hours between 9-17
				for (int j = 9; j < 18; j++) {

					int random = (int) Math.round((0 + 2 * Math.random())); // generate random number between 0
																			// and 2
					int startTime = j;
					int endTime = j + 1;

					String preferenceCode = prefCodes.get(random); // get random preference

					TimeSlot slot = new TimeSlot();
					slot.setModuleName("");
					slot.setGroupId(9); // should be optional because enot needed for preferences
					slot.setDate(day);
					slot.setStartTime(startTime);
					slot.setEndTime(endTime);
					slot.setStatus(preferenceCode);

					String slotID = String.valueOf(day).concat(String.valueOf(startTime))
							.concat(String.valueOf(endTime));

					myPreferences.put(slotID, preferenceCode);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return myPreferences;
	}
	
	
	
	private static void Test1(HashMap<String, Student> studentList, HashMap<String, String> pref1, HashMap<String, String> pref2) {
		
		//test 1 
//		student1
		TimeSlot slot= studentList.get(studentAgents.get(0)).moduleList.get(moduleNames.get(0));
		String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
		
		
		//student 2
		TimeSlot slot2= studentList.get(studentAgents.get(1)).moduleList.get(moduleNames.get(0));
		String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
		
		
		// make clash with preferences by setting the preferences to unable to attend
		String s1 = pref1.get(slotId1);
		String s2 = pref2.get(slotId1);
		pref1.replace(slotId1, "outside work or caring commitments");
		pref2.replace(slotId1, "like to have");
		String s3 = pref1.get(slotId1);
		String s4 = pref2.get(slotId1);
		
		// make clash with preferences  by setting the preferences to unable to attend
		// make clash with preferences  by setting the preferences to unable to attend
//				pref2.get(slotId2).setStatus("outside work or caring commitments");
//				pref1.get(slotId2).setStatus("like to have");
				pref2.replace(slotId2, "outside work or caring commitments");
				pref1.replace(slotId2, "like to have");
				
		// now set the id of other agent to different tutorial so that they can swap
		slot.setGroupId(1);
		slot2.setGroupId(2);
	}
	
	
	public static HashMap<String, String> PreferenceGenerator2(){
		 
		HashMap<String, String> myPreferences = new HashMap<String, String>();
		try {

			ArrayList<String> prefCodes = new ArrayList<String>();
			prefCodes.add("outside work or caring commitments");
			prefCodes.add("prefer not to attend");
			prefCodes.add("like to have");


			for (int i = 0; i < 5; i++) { // add preferences for 5 days

				int day = i + 1;

				// assign time slots for each day for hours between 9-17
				for (int j = 9; j < 18; j++) {

					int random = (int) Math.round((0 + 2 * Math.random())); // generate random number between 0
																			// and 2
					int startTime = j;
					int endTime = j + 1;

					String preferenceCode = prefCodes.get(2); // get random preference


					String slotID = String.valueOf(day).concat(String.valueOf(startTime))
							.concat(String.valueOf(endTime));

					myPreferences.put(slotID, preferenceCode);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return myPreferences;
	}
	
	
	private static void Test2() {
		
		//test 1 
////		student1
//		TimeSlot slot= studentList.get(studentAgents.get(0)).moduleList.get(moduleNames.get(0));
//		String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//		
//		
//		//student 2
//		TimeSlot slot2= studentList.get(studentAgents.get(1)).moduleList.get(moduleNames.get(0));
//		String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
		
		
//		// make clash with preferences by setting the preferences to unable to attend
//		String s1 = pref1.get(slotId1);
//		String s2 = pref2.get(slotId1);
//		pref1.replace(slotId1, "outside work or caring commitments");
//		String s3 = pref1.get(slotId1);
//		String s4 = pref2.get(slotId1);
		
		// make clash with preferences  by setting the preferences to unable to attend
		// make clash with preferences  by setting the preferences to unable to attend
//				pref2.get(slotId2).setStatus("outside work or caring commitments");
//				pref1.get(slotId2).setStatus("like to have");
//				pref2.replace(slotId2, "outside work or caring commitments");
				
//		// now set the id of other agent to different tutorial so that they can swap
//		slot.setGroupId(1);
//		slot2.setGroupId(2);
		
		studentList = new HashMap<String, Student>();
		Student student = new Student();
		student.name = "Student1";
		student.moduleList = new HashMap<String,TimeSlot>();

		// add to student list
		studentList.put("Student1", student);
		
		
		TimeSlot slot = new TimeSlot();
		slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
		String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
		allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
		
		studentList.get("Student1").moduleList.put("Module1", slot);
		
		
		
		
		Student student2 = new Student();
		student2.name = "Student2";
		student2.moduleList = new HashMap<String,TimeSlot>();

		// add to student list
		studentList.put("Student2", student2);
		
		TimeSlot slot2 = new TimeSlot();
		slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
		String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
		allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
		
		studentList.get("Student2").moduleList.put("Module1", slot2);
	}

	
private static void TestNoSwapAlreadySatisfied() {
	
	studentList = new HashMap<String, Student>();
	
	//Student 1
		Student student = new Student();
		student.name = "Student1";
		student.moduleList = new HashMap<String,TimeSlot>();
		// add to student list
		studentList.put("Student1", student);
		
	//Student 2
		Student student2 = new Student();
		student2.name = "Student2";
		student2.moduleList = new HashMap<String,TimeSlot>();
		// add to student list
		studentList.put("Student2", student2);
		
		
		
		
		
		//add slot
		TimeSlot slot = new TimeSlot();
		slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
		String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//		allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
		
		studentList.get(student.name).moduleList.put("Module1", slot);
		
		
		
		
		
		TimeSlot slot2 = new TimeSlot();
		slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
		String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//		allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
		
		studentList.get(student2.name).moduleList.put("Module1", slot2);
	}
	


private static void Test2Swap1With3Agents() {
	
	studentList = new HashMap<String, Student>();
	Student student = new Student();
	student.name = "Student1";
	student.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student1", student);
	
	
	TimeSlot slot = new TimeSlot();
	slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module1", slot);
	
	
	
	
	Student student2 = new Student();
	student2.name = "Student2";
	student2.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student2", student2);
	
	TimeSlot slot2 = new TimeSlot();
	slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module1", slot2);
	
	
	Student student3 = new Student();
	student3.name = "Student3";
	student3.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student3", student3);
	
	
	TimeSlot slot3 = new TimeSlot();
	slot3.setModuleName("Module1");slot3.setGroupId(2);slot3.setDate(3);slot3.setStartTime(16);slot3.setEndTime(17);slot3.setStatus("Assigned By Ta");
	String slotId3 = String.valueOf(slot3.getDate()).concat(String.valueOf(slot3.getStartTime())).concat(String.valueOf(slot3.getEndTime()));
	allPrefs.get(2).replace(slotId3, "outside work or caring commitments");
	
	studentList.get("Student3").moduleList.put("Module1", slot3);
}



private static void Test2Modules2Swap() {
	
	
	studentList = new HashMap<String, Student>();
	Student student = new Student();
	student.name = "Student1";
	student.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student1", student);
	
	
	TimeSlot slot = new TimeSlot();
	slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module1", slot);
	
	
	slot = new TimeSlot();
	slot.setModuleName("Module2");slot.setGroupId(2);slot.setDate(5);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module2", slot);
	
	
	
	
	Student student2 = new Student();
	student2.name = "Student2";
	student2.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student2", student2);
	
	TimeSlot slot2 = new TimeSlot();
	slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module1", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module2");slot2.setGroupId(1);slot2.setDate(4);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module2", slot2);
}

private static void Test3Modules3Swap() {
	
	
	studentList = new HashMap<String, Student>();
	Student student = new Student();
	student.name = "Student1";
	student.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student1", student);
	
	
	TimeSlot slot = new TimeSlot();
	slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module1", slot);
	
	
	slot = new TimeSlot();
	slot.setModuleName("Module2");slot.setGroupId(2);slot.setDate(5);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module2", slot);
	
	slot = new TimeSlot();
	slot.setModuleName("Module3");slot.setGroupId(2);slot.setDate(2);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module3", slot);
	
	
	
	
	Student student2 = new Student();
	student2.name = "Student2";
	student2.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student2", student2);
	
	TimeSlot slot2 = new TimeSlot();
	slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module1", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module2");slot2.setGroupId(1);slot2.setDate(4);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module2", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module3");slot2.setGroupId(1);slot2.setDate(3);slot2.setStartTime(15);slot2.setEndTime(16);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module3", slot2);
}


private static void Test3ModulesButSwap2() {
	
	
	studentList = new HashMap<String, Student>();
	Student student = new Student();
	student.name = "Student1";
	student.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student1", student);
	
	
	TimeSlot slot = new TimeSlot();
	slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module1", slot);
	
	
	slot = new TimeSlot();
	slot.setModuleName("Module2");slot.setGroupId(2);slot.setDate(5);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module2", slot);
	
	slot = new TimeSlot();
	slot.setModuleName("Module3");slot.setGroupId(2);slot.setDate(2);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module3", slot);
	
	
	
	
	Student student2 = new Student();
	student2.name = "Student2";
	student2.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student2", student2);
	
	TimeSlot slot2 = new TimeSlot();
	slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module1", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module2");slot2.setGroupId(1);slot2.setDate(4);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module2", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module3");slot2.setGroupId(1);slot2.setDate(3);slot2.setStartTime(15);slot2.setEndTime(16);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module3", slot2);
}


private static void Test3ModulesButSwap1() {
	
	
	studentList = new HashMap<String, Student>();
	Student student = new Student();
	student.name = "Student1";
	student.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student1", student);
	
	
	TimeSlot slot = new TimeSlot();
	slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module1", slot);
	
	
	slot = new TimeSlot();
	slot.setModuleName("Module2");slot.setGroupId(2);slot.setDate(5);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module2", slot);
	
	slot = new TimeSlot();
	slot.setModuleName("Module3");slot.setGroupId(2);slot.setDate(2);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module3", slot);
	
	
	
	
	Student student2 = new Student();
	student2.name = "Student2";
	student2.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student2", student2);
	
	TimeSlot slot2 = new TimeSlot();
	slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module1", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module2");slot2.setGroupId(1);slot2.setDate(4);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module2", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module3");slot2.setGroupId(1);slot2.setDate(3);slot2.setStartTime(15);slot2.setEndTime(16);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module3", slot2);
}

private static void Test3ModulesNoSwapNeeded() {
	
	
	studentList = new HashMap<String, Student>();
	Student student = new Student();
	student.name = "Student1";
	student.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student1", student);
	
	
	TimeSlot slot = new TimeSlot();
	slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module1", slot);
	
	
	slot = new TimeSlot();
	slot.setModuleName("Module2");slot.setGroupId(2);slot.setDate(5);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module2", slot);
	
	slot = new TimeSlot();
	slot.setModuleName("Module3");slot.setGroupId(2);slot.setDate(2);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module3", slot);
	
	
	
	
	Student student2 = new Student();
	student2.name = "Student2";
	student2.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student2", student2);
	
	TimeSlot slot2 = new TimeSlot();
	slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module1", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module2");slot2.setGroupId(1);slot2.setDate(4);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module2", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module3");slot2.setGroupId(1);slot2.setDate(3);slot2.setStartTime(15);slot2.setEndTime(16);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module3", slot2);
}

private static void _3Modules_3Agents_only_2SwapSlots() {
	studentList = new HashMap<String, Student>();
	Student student = new Student();
	student.name = "Student1";
	student.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student1", student);
	
	
	TimeSlot slot = new TimeSlot();
	slot.setModuleName("Module1");slot.setGroupId(2);slot.setDate(1);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	String slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module1", slot);
	
	
	slot = new TimeSlot();
	slot.setModuleName("Module2");slot.setGroupId(2);slot.setDate(5);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module2", slot);
	
	slot = new TimeSlot();
	slot.setModuleName("Module3");slot.setGroupId(2);slot.setDate(2);slot.setStartTime(12);slot.setEndTime(13);slot.setStatus("Assigned By Ta");
	slotId1 = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime())).concat(String.valueOf(slot.getEndTime()));
//	allPrefs.get(0).replace(slotId1, "outside work or caring commitments");
	
	studentList.get("Student1").moduleList.put("Module3", slot);
	
	
	
	
	Student student2 = new Student();
	student2.name = "Student2";
	student2.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student2", student2);
	
	TimeSlot slot2 = new TimeSlot();
	slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	String slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module1", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module2");slot2.setGroupId(2);slot2.setDate(4);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module2", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module3");slot2.setGroupId(1);slot2.setDate(3);slot2.setStartTime(15);slot2.setEndTime(16);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student2").moduleList.put("Module3", slot2);
	
	
	
	Student student3 = new Student();
	student3.name = "Student2";
	student3.moduleList = new HashMap<String,TimeSlot>();

	// add to student list
	studentList.put("Student3", student3);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module1");slot2.setGroupId(1);slot2.setDate(1);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(1).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student3").moduleList.put("Module1", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module2");slot2.setGroupId(1);slot2.setDate(4);slot2.setStartTime(9);slot2.setEndTime(10);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
//	allPrefs.get(2).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student3").moduleList.put("Module2", slot2);
	
	slot2 = new TimeSlot();
	slot2.setModuleName("Module3");slot2.setGroupId(2);slot2.setDate(5);slot2.setStartTime(15);slot2.setEndTime(16);slot2.setStatus("Assigned By Ta");
	slotId2 = String.valueOf(slot2.getDate()).concat(String.valueOf(slot2.getStartTime())).concat(String.valueOf(slot2.getEndTime()));
	allPrefs.get(2).replace(slotId2, "outside work or caring commitments");
	
	studentList.get("Student3").moduleList.put("Module3", slot2);
}
}




