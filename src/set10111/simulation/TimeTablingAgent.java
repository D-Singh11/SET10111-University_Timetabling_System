package set10111.simulation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import set10111.simulation.TimeTablingAgent.TickerWaiter.FindStudents;
import set10111.timetabling_ontology.TimetablingOntology;
import timetabling_ontology.elements.AcceptTimetable;
import timetabling_ontology.elements.AgreeSwap;
import timetabling_ontology.elements.Swap;
import timetabling_ontology.elements.UpdateTimetable;
import timetabling_ontology.elements.TimeSlot;
import timetabling_ontology.elements.TimeTable;

//was previously named as SellerAgent because similar to Seller Agent of practical 6
public class TimeTablingAgent extends Agent {
	private HashMap<String, ACLMessage> BothAgreed = new HashMap<>();
	private AID tickerAgent;
	private ArrayList<AID> studentAgents = new ArrayList<>();
	ArrayList<String> moduleNames = new ArrayList<String>();
	ArrayList<Student> studentList1 = new ArrayList<Student>();
	Random random = new Random();
	private HashMap<AID, Student> studentList = new HashMap<>();
	private boolean result = false;
	
	HashMap<String, Student> studentListFromMain;

	private Codec codec = new SLCodec();
	private Ontology ontology = TimetablingOntology.getInstance();

	@Override
	protected void setup() {
		// add this agent to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("seller");
		sd.setName(getLocalName() + "-seller-agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// get the title of book from passed arguments to agent
			Object[] args = getArguments();
			studentListFromMain = ((Customm) args[0]).timetable;
			
			
			
					
		addBehaviour(new TickerWaiter(this));
//		addBehaviour(bnew SwapRequestServer(myAgent));
	}

	public class TickerWaiter extends OneShotBehaviour {

		// behaviour to wait for a new day
		public TickerWaiter(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchContent("new day"),
					MessageTemplate.MatchContent("terminate"));
			ACLMessage msg = myAgent.receive(mt);
//			if (msg != null) {
//				if (tickerAgent == null) {
//					tickerAgent = msg.getSender();
//				}
//				if (msg.getContent().equals("new day")) {
					myAgent.addBehaviour(new FindStudents(myAgent));
					
					myAgent.addBehaviour(new TimeTableGenerator());
					doWait(2000);
					myAgent.addBehaviour(new SendTimeTable(myAgent));

					CyclicBehaviour os = new SwapRequestServer(myAgent);
					myAgent.addBehaviour(os);
//					ArrayList<Behaviour> cyclicBehaviours = new ArrayList<>();
//					cyclicBehaviours.add(os);
//					myAgent.addBehaviour(new End(myAgent));
//				} else {
//					// termination message to end simulation
//					myAgent.doDelete();
//				}
//			} else {
//				block();
//			}
		}

		public class TimeTableGenerator extends OneShotBehaviour {

			@Override
			public void action() {
				
				for (int i = 0; i < studentAgents.size(); i++) {
					AID studentAid = studentAgents.get(i);
					String name = studentAid.getLocalName();
					
					Student student = studentListFromMain.get(name);
					
					studentList.put(studentAid, student);
				}

//				moduleNames.clear();
//
				// add new dummy module
				moduleNames.add("Module1");
				moduleNames.add("Module2");
				moduleNames.add("Module3");

//				// create students list based upon number of student agents in system
//				for (AID sa : studentAgents) {
//					Student student = new Student();
//					student.name = sa.getLocalName();
//					student.moduleList = new HashMap<>();
//
//					// add to student list
//					studentList.put(sa, student);
//
//				}
//
//				// Now assign random time slots and random tutorial groups to each student for
//				// each module
//				for (String moduleName : moduleNames) {
//					System.out.println("");
//					System.out.println(moduleName);
//					System.out.println("---------");
//
//					int totalStudentG1 = 0;
//					int totalStudentG2 = 0;
//
//					// generate 2 time slots for each tutorial
//					ArrayList<TimeSlot> tutTime = new ArrayList<TimeSlot>();
//					for (int i = 0; i < 2; i++) {
//
//						int startTime = random.nextInt(9) + 9;
//						int endTime = startTime + 1;
//						int day = random.nextInt(5) + 1; // set the day of the tutorial
//
//						TimeSlot slot = new TimeSlot();
//						slot.setModuleName(moduleName);
//						slot.setGroupId(0); // set to 0 because will be updated when actual timeslot is assigned
//						slot.setDate(day);
//						slot.setStartTime(startTime);
//						slot.setEndTime(endTime);
//						slot.setStatus("AsiignedByTA");
//						tutTime.add(slot); // adding dummy slot to tutorial times arraylist
//					}
//
//					// assign each student to random timeslot of tutorial
//					for (AID aid : studentAgents) {
//						Student student = studentList.get(aid);
//
//						int groupId = random.nextInt(2); // generate random number between 0 and 1
//
//						TimeSlot slot = tutTime.get(groupId);
//						slot.setGroupId(groupId + 1); // assign the group id to slot. adding one because want tp statr
//														// group ids from 1 instead of 0
//
////						Module module = new Module();
////						module.name = moduleName;
////						module.timeSlot = slot;
//
//						// add this slot to the module list of student
//						studentList.get(aid).moduleList.put(moduleName, slot);
//
//						if (groupId == 0) {
//							totalStudentG1++;
//						} else {
//							totalStudentG2++;
//						}
//
////						System.out.println(student.name);
//						System.out.println(
//								student.name + "   ----   slot : " + slot.getModuleName() + " " + slot.getGroupId()
//										+ " " + slot.getDate() + " " + slot.getStartTime() + " " + slot.getEndTime());
//					}
//
//					System.out.println("Total Students enrolled on module : " + studentAgents.size());
//					System.out.println(" student in Group 1 : " + totalStudentG1);
//					System.out.println(" student in Group 2 : " + totalStudentG2);
//				}
			}

		}

		public class FindStudents extends OneShotBehaviour {

			public FindStudents(Agent a) {
				super(a);
			}

			@Override
			public void action() {
				DFAgentDescription studentTemplate = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("student");
				studentTemplate.addServices(sd);
				try {
					studentAgents.clear();
					DFAgentDescription[] agentsType1 = DFService.search(myAgent, studentTemplate);
					for (int i = 0; i < agentsType1.length; i++) {
						studentAgents.add(agentsType1[i].getName()); // this is the AID
					}
				} catch (FIPAException e) {
					e.printStackTrace();
				}

			}

		}
		
		public class End extends CyclicBehaviour {

			public End(Agent a) {
				super(a);
			}

			@Override
			public void action() {
				addBehaviour(new FindStudents(myAgent));
				if (studentAgents.size() < 1) {
					myAgent.doDelete();
					System.out.println("No more students active so shutting down TA agent");
				}
				else {
//					block();
				}

			}

		}

		public class SendTimeTable extends OneShotBehaviour {

			public SendTimeTable(Agent a) {
				super(a);
			}

			@Override
			public void action() {
				try {

					// Prepare the action request message
//					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//					msg.setLanguage(codec.getName());
//					msg.setOntology(ontology.getName());

					// include following code in for loop if want to send timetable to all student
					// agents

					for (int i = 0; i < studentAgents.size(); i++) {
						
						// Prepare the action request message
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.setLanguage(codec.getName());
						msg.setOntology(ontology.getName());

						AID student = studentAgents.get(i);

						// Prepare the content.
						TimeTable timetable = new TimeTable();
						timetable.setName(student.getLocalName());

						ArrayList<TimeSlot> timeSlots = new ArrayList<TimeSlot>();

						// todo: remove module class and instead store the time slots in the module list
						// and name list to time slots
						TimeSlot slot = studentList.get(student).moduleList.get(moduleNames.get(0));
						timeSlots.add(slot);

						slot = studentList.get(student).moduleList.get(moduleNames.get(1));
						timeSlots.add(slot);
//
						// comment this third module if test case involve only 2 modules but you will also have to change
						// constrints in onltology
						slot = studentList.get(student).moduleList.get(moduleNames.get(2));
						timeSlots.add(slot);

						timetable.setTutorialAssignment(timeSlots);

						AcceptTimetable order = new AcceptTimetable();
						order.setSenderAgentAid(myAgent.getAID()); // setting the aid of the sender which is this agent
						order.setTimeTable(timetable);

						msg.addReceiver(student); // sellerAID is the AID of the Seller agent

						// IMPORTANT: According to FIPA, we need to create a wrapper Action object
						// with the action and the AID of the agent
						// we are requesting to perform the action
						// you will get an exception if you try to send the sell action directly
						// not inside the wrapper!!!
						Action request = new Action();
						request.setAction(order);
						request.setActor(student); // the agent that you request to perform the action

						// Let JADE convert from Java objects to string
						getContentManager().fillContent(msg, request); // send the wrapper object
						send(msg);
					}

				} catch (CodecException ce) {
					ce.printStackTrace();

				} catch (OntologyException oe) {
					oe.printStackTrace();
				}

			}

		}

	}

	public class SwapRequestServer extends CyclicBehaviour {

		public SwapRequestServer(Agent a) {
			super(a);
		}

		@Override
		public void action() {

			try {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					ACLMessage reply = msg.createReply();
					ContentElement ce = null;

					// Let JADE convert from String to Java objects
					// Output will be a ContentElement
					ce = getContentManager().extractContent(msg);

					// check if content is an Action
					if (ce instanceof Action) {
						Action act = ((Action) ce);
						Concept action = act.getAction();

						// check if action is of type OwnsTimeSlot
						if (action instanceof UpdateTimetable) {
							UpdateTimetable swap = (UpdateTimetable) action;
							// Extract the TimeSlot from action
							TimeSlot timeslot = swap.getTimeSlot();
							String moduleName = timeslot.getModuleName();
							AID student1 = swap.getStudentRequested();
							// change following to AID of second student once fillContent() issue is fixed in case1 of student agent's SendSwapRequest behaviour 
							AID student2 = swap.getStudentOffered();
							boolean second=false;
							// call the method to perform swap
//							boolean isSwapped = swapTimeSlots(student1, moduleName, student2);
							
						
							TimeSlot s1_slot = studentList.get(student1).moduleList.get(moduleName);
							TimeSlot s2_slot = studentList.get(student2).moduleList.get(moduleName);
							
							String id1 =String.valueOf(s1_slot.getDate()).concat(String.valueOf(s1_slot.getStartTime())).concat(String.valueOf(s1_slot.getEndTime()));
							String id2 = String.valueOf(s2_slot.getDate()).concat(String.valueOf(s2_slot.getStartTime())).concat(String.valueOf(s2_slot.getEndTime()));
							
							String firstRequest =moduleName.concat(student1.getLocalName()).concat("-"+student2.getLocalName()).concat(id1).concat(id2);
							BothAgreed.put(firstRequest, msg);
							String secondRequest =moduleName.concat(student2.getLocalName()).concat("-"+student1.getLocalName()).concat(id2).concat(id1);
							
							second = BothAgreed.get(secondRequest) == null ? false : true;
								
							
							
							if (studentList.containsKey(student1) && studentList.containsKey(student2) && second == true) {

								// get the time slot of student's specified module and replace it with other
								// student
								 s1_slot = studentList.get(student1).moduleList.remove(moduleName);
								 s2_slot = studentList.get(student2).moduleList.remove(moduleName);

//								swap slots  use uncommented 2 lines or the next syntax where assign each property separately. 
//								if reference type does not cause issues then use following uncommented 2 lines otherwise dont
								studentList.get(student1).moduleList.put(moduleName, s2_slot);
								studentList.get(student2).moduleList.put(moduleName, s1_slot);


//								studentList.get(student2).moduleList.get(moduleName).timeSlot.replace("AsiignedByTA", slot1);

								result = true;
								 id1 =String.valueOf(s1_slot.getDate()).concat(String.valueOf(s1_slot.getStartTime())).concat(String.valueOf(s1_slot.getEndTime()));
								 id2 = String.valueOf(s2_slot.getDate()).concat(String.valueOf(s2_slot.getStartTime())).concat(String.valueOf(s2_slot.getEndTime()));
								
								System.out.println(student1.getLocalName() + " Swapped  " +  id1 + " > " + student2.getLocalName() +" "+ id2 );
							} else {
								result = false;
							}

//							-------------continue from here-------
							// which will allow manual creation of desired swapping scenarios // this checks
							// if student agent has not already agrred to swap this slot with other agent

							// send confirm message

							if (result) {
								reply = BothAgreed.get(firstRequest).createReply();
								reply.setPerformative(ACLMessage.INFORM);

								Result result = new Result();

								result.setAction(act);
								result.setValue(timeslot); // use predicate

								// craete content element list which will have orignal action requested by other
								// student and offer of this student
								ContentElementList cml = new ContentElementList();
								
								// do we need to use content element list now because result itself has the previous action
//								cml.add(act);

								// add offer to the content list a swell
								cml.add(result);

								// Let JADE convert from Java objects to string
								getContentManager().fillContent(reply, cml);
//								System.out.println("--From TA ---" + cml);
								myAgent.send(reply); // send reply/offer to other students

								
								ACLMessage msg1 = BothAgreed.get(secondRequest);
								ACLMessage reply1 = BothAgreed.get(secondRequest).createReply();
								ContentElement ce1 = null;

								// Let JADE convert from String to Java objects
								// Output will be a ContentElement
								ce1 = getContentManager().extractContent(msg1);
								Action act1 = ((Action) ce1);
								Concept action1 = act1.getAction();
								
								UpdateTimetable swap1 = (UpdateTimetable) action1;
								// Extract the TimeSlot from action
								TimeSlot timeslot1 = swap1.getTimeSlot();
								
								reply1.setPerformative(ACLMessage.INFORM);

								Result result1 = new Result();

								result1.setAction(act1);
								result1.setValue(timeslot1); // use predicate

								// craete content element list which will have orignal action requested by other
								// student and offer of this student
								ContentElementList cml1 = new ContentElementList();
								
								// do we need to use content element list now because result itself has the previous action
//								cml.add(act);

								// add offer to the content list a swell
								cml1.add(result1);

								// Let JADE convert from Java objects to string
								getContentManager().fillContent(reply1, cml1);
//								System.out.println("--From TA ---" + cml);
								myAgent.send(reply1); // send reply/offer to other students

							} else {

//								reply.setPerformative(ACLMessage.FAILURE);
//								// should this be failure or refuse
////								reply.setPerformative(ACLMessage.REFUSE);
//								System.out.println("Error swapping timeslots");
//
//								myAgent.send(reply); // send reply/offer to other students
							}

						}
					}
				} else {
					block();
				}

			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}

		}

//Notes for all agents not only for timetabling agent
		// todo- one way to do swap is that both student agents request timetabling
		// agents to swap the agreed time slot
		// In that scenario timetabling agent will wait for reply from both students for
		// the same slotId but with different groups before Timetabling can perform
		// actual swap

//		OR

		// Other way can be only one of the student agent sends the swap request with
		// details of both students and Timetabling agent performs the swap.

		// ---------
		// once the swap is completed Timetabling agent can send the confirmation in the
		// form of Result see workbook. That response can be
		// used by student agents to update their local assignedtimetable varibale

		// Whilst the swap is being carried out by timetabling agent, we should avoid
		// the scenario of same student aganets agreeing swap of same timeslot with
		// other
		// students. To handle we can create an hasmap varriable called
		// beingSwaapedByTimetablingAgent which can store the ids of time slots which
		// are sent to timetabling agent
		// for swap and not negotiate on the timeslots with other student agents if the
		// ids of timeslots is also on the beingSwaapedByTimetablingAgent

		// if swap was success then remove that time slot from both timeSlotsToSwap and
		// beingSwaapedByTimetablingAgent
		// if failed then remove that time slot from beingSwaapedByTimetablingAgent, so
		// that student can start swapping negotiation with other students again it
		// again
//		handle swapping of timeslots
//		public boolean swapTimeSlots(final AID student1, final String moduleName, final AID student2) {
//			try {
//				addBehaviour(new OneShotBehaviour() {
//					@Override
//					public void action() {
//						// check both students are on the timetable list
//						if (studentList.containsKey(student1) && studentList.containsKey(student2)) {
//
//							// get the time slot of student's specified module and replace it with other
//							// student
//							TimeSlot s1_slot = studentList.get(student1).moduleList.get(moduleName).timeSlot;
//							TimeSlot s2_slot = studentList.get(student2).moduleList.get(moduleName).timeSlot;
//
////							swap slots  use uncommented 2 lines or the next syntax where assign each property separately. 
////							if reference type does not cause issues then use following uncommented 2 lines otherwise dont
//							studentList.get(student1).moduleList.get(moduleName).timeSlot = s2_slot;
//							studentList.get(student2).moduleList.get(moduleName).timeSlot = s1_slot;
//
//							// replace slot of student 1 with student 2
//
//							TimeSlot slot2 = new TimeSlot();
//							slot2.setModuleName(s2_slot.getModuleName());
//							slot2.setGroupId(s2_slot.getGroupId());
//							slot2.setDate(s2_slot.getDate());
//							slot2.setStartTime(s2_slot.getStartTime());
//							slot2.setEndTime(s2_slot.getEndTime());
//							slot2.setStatus(s2_slot.getStatus());
//
////							studentList.get(student1).moduleList.get(moduleName).timeSlot.replace("AsiignedByTA", slot2);
//
//							// replace slot of student 2 with student 1
//
//							TimeSlot slot1 = new TimeSlot();
//							slot1.setModuleName(s2_slot.getModuleName());
//							slot1.setGroupId(s2_slot.getGroupId());
//							slot1.setDate(s2_slot.getDate());
//							slot1.setStartTime(s2_slot.getStartTime());
//							slot1.setEndTime(s2_slot.getEndTime());
//							slot1.setStatus(s2_slot.getStatus());
//
////							studentList.get(student2).moduleList.get(moduleName).timeSlot.replace("AsiignedByTA", slot1);
//
//							result = true;
//						} else {
//							result = false;
//						}
//
//					}
//				});
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return result;
//		}
	}

	public class EndDayListener extends CyclicBehaviour {
		

		public EndDayListener(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			
			
		}

	}
}
