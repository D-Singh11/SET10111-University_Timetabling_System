package set10111.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

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
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import set10111.timetabling_ontology.TimetablingOntology;
import timetabling_ontology.elements.AcceptTimetable;
import timetabling_ontology.elements.AgreeSwap;
import timetabling_ontology.elements.Swap;
import timetabling_ontology.elements.UpdateTimetable;
import timetabling_ontology.elements.TimeSlot;
import timetabling_ontology.elements.TimeTable;

// was previously named as BuyerAgent because similar to buyer
public class StudentAgent extends Agent {

	// use contract net protocal or CFP to ask other students if they want to swap
	// the slot included in the request. if they do they should send details of
	// their slot back to the requesting agent
	// Run the utility function before sending CFP back to see what effect can
	// swapping have on the agent who is ready to swap.

	// Use following for the utility calculation
//				if assiginedtutorial time is in the  "would like to have"  add 10 points
//				if assiginedtutorial time is in the "outside work or caring commitments"   subtract 10 points
//				if assiginedtutorial time is in the "prefer not to have"   subtract 2 points

//			you can choose on the how many points to subtract or add for each agent

	// Or use the advertisement agent to see which aganet wants to swap the slots
	// and instead of sending requets directky to students to ask for slots tp swap
	// send them to the advertisement agent.
	// and when a slot to swap is found, contact the student agent directly and
	// perform the swap.

	// One of the reason why advertisement is not suitable because you will have to
	// involve an extra agent for advertisements which will also need to be updated
	// whenever a swap is completed. What i mena is
	// is that one every complete swap Timetabling agent or students linked to swap
	// will have to tell advertiser to remove or update their listings on the board

	// Use either CFP or Advertisement technique and discuss in report why you
	// choose one and why did not choose other

	private ArrayList<AID> students = new ArrayList<>();
	private AID timeTablingAgent;
	private ArrayList<String> booksToBuy = new ArrayList<>();
	private HashMap<String, TimeSlot> timeSlotsTOSwap = new HashMap<String, TimeSlot>();
	private HashMap<String, ArrayList<Offer>> currentOffers = new HashMap<>();
	private AID tickerAgent;
	private int numQueriesSent;
	ArrayList<String> moduleNames = new ArrayList<String>();
	public HashMap<String, String> myPreferences = new HashMap<String, String>();
	public HashMap<String, TimeSlot> assignedTimeSlots = new HashMap<String, TimeSlot>();
	public HashMap<String, ContentElementList> activeProposals = new HashMap<String, ContentElementList>();
	private int numSwapRequestsSent = 0;

	private Codec codec = new SLCodec();
	private Ontology ontology = TimetablingOntology.getInstance();

	@Override
	protected void setup() {

		// add this agent to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("student");
		sd.setName(getLocalName() + "-student-agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// add books to buy
		// get the title of book from passed arguments to agent
		Object[] args = getArguments();
		HashMap<String, String> myreferences= ((Customm) args[0]).preferences;
		myPreferences = myreferences;

		SequentialBehaviour dailyActivity = new SequentialBehaviour();
		// sub-behaviours will execute in the order they are added
		dailyActivity.addSubBehaviour(new FindSudents(this));
		dailyActivity.addSubBehaviour(new PreferenceGenerator(this));
		dailyActivity.addSubBehaviour(new GetTimetableBehaviour());
		// send Enquiries will only be called if above behaviour is complete. But we may
		// need it to update the timetables if slots are swapped
		// that means the send Enquiries will never be called OR make some different
		// kind of behaviour
		// add it to the setup()
		dailyActivity.addSubBehaviour(new SendEnquiries(this));
		dailyActivity.addSubBehaviour(new CollectOffers(this));
		addBehaviour(dailyActivity);
		addBehaviour(new QueryBehaviour());

		addBehaviour(new ProposalResponseServer());
	}

	@Override
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	public class PreferenceGenerator extends OneShotBehaviour {

		public PreferenceGenerator(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			try {

				// only commented for testing
				
//				ArrayList<String> prefCodes = new ArrayList<String>();
//				prefCodes.add("outside work or caring commitments");
//				prefCodes.add("prefer not to attend");
//				prefCodes.add("like to have");
//
//				// add new dummy module
////				moduleNames.add("Module1");
////				moduleNames.add("Module2");
////				moduleNames.add("Module3");
//
//				// commented below for because we do not need to have preferences separatly for
//				// ech module. they are for common for all modules
////				for (String moduleName : moduleNames) {
////
////					Module module = new Module();
//
//				for (int i = 0; i < 5; i++) { // add preferences for 5 days
//
//					int day = i + 1;
//
//					// assign time slots for each day for hours between 9-17
//					for (int j = 9; j < 18; j++) {
//
//						int random = (int) Math.round((0 + 2 * Math.random())); // generate random number between 0
//																				// and 2
//						int startTime = j;
//						int endTime = j + 1;
//
//						String preferenceCode = prefCodes.get(random); // get random preference
//
//						TimeSlot slot = new TimeSlot();
//						slot.setModuleName("");
//						slot.setGroupId(9); // should be optional because enot needed for preferences
//						slot.setDate(day);
//						slot.setStartTime(startTime);
//						slot.setEndTime(endTime);
//						slot.setStatus(preferenceCode);
//
//						String slotID = String.valueOf(day).concat(String.valueOf(startTime))
//								.concat(String.valueOf(endTime));
//
//						myPreferences.put(slotID, slot.getStatus());
//
//						System.out.println(getLocalName() + "   " + " -------  " + slot.getModuleName() + " "
//								+ slot.getGroupId() + " " + i + " " + slot.getStartTime() + " " + slot.getEndTime()
//								+ "  " + preferenceCode);
//					}
//
////						preferences.moduleList.put(moduleName, module);
//
//				}

				int total = utilityFunction(assignedTimeSlots, myPreferences);

				System.out.println("Total utility  : " + total);
//				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public class FindSudents extends OneShotBehaviour {

		public FindSudents(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			DFAgentDescription sellerTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("student");
			sellerTemplate.addServices(sd);
			try {
				students.clear();
				DFAgentDescription[] agentsType1 = DFService.search(myAgent, sellerTemplate);
				for (int i = 0; i < agentsType1.length; i++) {
					// check if the aid of the student is not the AID of this student. If it is then
					// do not add to other student agents list
					if (!agentsType1[i].getName().getLocalName().contentEquals(myAgent.getAID().getLocalName())) {
						students.add(agentsType1[i].getName()); // this is the AID
					}
				}
				System.out.println("Total students : " + students.size());
			} catch (FIPAException e) {
				e.printStackTrace();
			}

		}

	}
	
	private class GetTimetableBehaviour extends Behaviour {

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			if (timeSlotsTOSwap.size() != 0) {
				return true;
			}
			return false;
		}

		@Override
		public void action() {
			// This behaviour should only respond to REQUEST messages
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM); // chnage to request
			ACLMessage msg = receive(mt);
			if (msg != null) {
				try {
					ACLMessage reply = msg.createReply();
					ContentElement ce = null;
//					System.out.println(msg.getContent()); // print out the message content in SL

					// Let JADE convert from String to Java objects
					// Output will be a ContentElement
					ce = getContentManager().extractContent(msg);
					if (ce instanceof Action) {
						Concept action = ((Action) ce).getAction();
						if (action instanceof AcceptTimetable) {
							AcceptTimetable order = (AcceptTimetable) action;
							TimeTable it = order.getTimeTable();
							timeTablingAgent = order.getSenderAgentAid(); // get the aid of timetabling agent which will
																			// be used whenever a timeslot is needed to
																			// swap. It will be then used to to ask
																			// Timetabling agent to perform swapping
//							System.out.println(timeTablingAgent);
							// Extract the CD name and print it to demonstrate use of the ontology
							if (it instanceof TimeTable) {
								TimeTable tm = (TimeTable) it;

								// store time table
								Random random = new Random();
								for (int i = 0; i < tm.getTutorialAssignment().size(); i++) {

									TimeSlot slot = tm.getTutorialAssignment().get(i);
									String slotID = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime()))
											.concat(String.valueOf(slot.getEndTime()));
									assignedTimeSlots.put(slotID, slot); // adding slot to tutorial times arraylist
																	// assignedTimeSlots

								}

								// calculating utility of the assigned time table using preferences
								int total = utilityFunction(assignedTimeSlots, myPreferences);

								System.out.println("Total utility  : " + total);

								System.out.println("Total time slots to swap are : " + timeSlotsTOSwap.size());
								
								if (timeSlotsTOSwap.size() < 1) {
									myAgent.doDelete();
								}
							}
						}

					}
				}

				catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}

			} else {
				block();
			}
		}
	}


	// shift these two functions in helper class
	public int utilityFunction(HashMap<String, TimeSlot> assignedTimeSlots,
			HashMap<String, String> studentPreferences) {
		int totalFitness = 0;
		for (String slotID : assignedTimeSlots.keySet()) {
//			String slotID = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime()))
//					.concat(String.valueOf(slot.getEndTime()));

			String studentPreference = studentPreferences.get(slotID);
			
			TimeSlot slot = assignedTimeSlots.get(slotID);

			int chnageInFitness = this.calculateFitnessChange(studentPreference);
			
			if (chnageInFitness == 8 || chnageInFitness == -10) {
				timeSlotsTOSwap.put(slotID, slot);
			}
			totalFitness = totalFitness + chnageInFitness;
		}
		return totalFitness;
	}

	public int calculateFitnessChange(String studentPreference) {
		// total points should be 30 if all modules time slots match students
		// preferemces.
		// If time slot does not match preference than substract 10 points
		// if amtch then add 10 points
		// if timeslot timetable is in "prefer not to attend" then only give 8 points
		// which is 2 points less than if it was "liketo have"
		int fitness = 0;
//		
//		studentPreference = myPreferences.get(slotID);

		if (studentPreference == "like to have") {

			fitness += 10; // add 10 pints to fitness if student-preference time matches with slot time
		} else if (studentPreference == "prefer not to attend") {

			fitness = fitness + 8; // add 8 points to fitness if student-preference at the time of slot's time was
									// "prefer not to attend"
		} else if (studentPreference == "outside work or caring commitments") {

			fitness = fitness - 10; // subtract 10 points from fitness if student-preference at the time of slot's
									// time was "outside work or caring commitments"
		}
		return fitness;
	}
	
	private TimeSlot slotWithWorstFitness(HashMap<String, TimeSlot> slots, String moduleName) {
		String worstfitKey=null;
		int oldFitness=10;
		TimeSlot slot = new TimeSlot();
		for (String id : slots.keySet()) {
			slot = slots.get(id);
			String pref = myPreferences.get(id);
			int fitness = calculateFitnessChange(pref);
			if (fitness <= oldFitness && slot.getModuleName().contains(moduleName)) { //&& slot.getModuleName() == cfpSlot.getModuleName() && slot.getGroupId() != cfpSlot.getGroupId()
				worstfitKey = id;
			}
			oldFitness = fitness;
		}
		return slots.get(worstfitKey);
	}
	

	public class SendEnquiries extends OneShotBehaviour {

		public SendEnquiries(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// send out a call for proposals for each book
			numQueriesSent = 0;
			System.out.println(myAgent.getLocalName());
			for (String string : assignedTimeSlots.keySet()) {
				System.out.println(string);
			}
			int numQueriesSentForEachSlot = 0;
			for (String slotID : timeSlotsTOSwap.keySet()) {

				// Prepare the Query-IF message
				ACLMessage enquiry = new ACLMessage(ACLMessage.CFP); // change to CFP
				enquiry.setLanguage(codec.getName());
				enquiry.setOntology(ontology.getName());

				for (AID student : students) {
					// Prepare the content using predicate.
					TimeSlot tutorialSlot = timeSlotsTOSwap.get(slotID);

					enquiry.setConversationId(slotID + myAgent.getName());
					Swap swapSlot = new Swap(); // rename the OwnsTimeSlot
					swapSlot.setItem(tutorialSlot);
					swapSlot.setOwner(student);

					enquiry.addReceiver(student); // student is the AID of the student agent to whom the enquiry is sent

					// IMPORTANT: According to FIPA, we need to create a wrapper Action object
					// with the action and the AID of the agent
					// we are requesting to perform the action
					// you will get an exception if you try to send the sell action directly
					// not inside the wrapper!!!
					Action request = new Action();
					request.setAction(swapSlot);
					request.setActor(student); // the agent that you request to perform the action

					try {
						// Let JADE convert from Java objects to string
						getContentManager().fillContent(enquiry, request);
						myAgent.send(enquiry); // send enquiry for current tutorial in loop to all other students
						numQueriesSent++; // this stores total queries sent for all timeslots to swap
						numQueriesSentForEachSlot++; // this stores total queries sent for for current timeslot in loop
					} catch (CodecException ce) {
						ce.printStackTrace();
					} catch (OntologyException oe) {
						oe.printStackTrace();
					}
				}
				System.out.println("Total enquiries sent : " + numQueriesSentForEachSlot);
				numQueriesSentForEachSlot = 0;
			}

		}
	}
	
	
	private class QueryBehaviour extends CyclicBehaviour {
		@Override
		public void action() {
			// This behaviour should only respond to QUERY_IF messages
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = receive(mt);
			if (msg != null) {
				try {
					ACLMessage reply = msg.createReply();
					ContentElement ce = null;
//					System.out.println(msg.getContent()); //print out the message content in SL

					// Let JADE convert from String to Java objects
					// Output will be a ContentElement
					ce = getContentManager().extractContent(msg);

					// check if content is an Action
					if (ce instanceof Action) {
						Action swapAction = ((Action) ce);
						Concept action = swapAction.getAction();

						// check if action is of type OwnsTimeSlot
						if (action instanceof Swap) {
							Swap owns = (Swap) action;
							TimeSlot it = owns.getItem();

							// Extract the TimeSlot and build its id and then and print it to ensure message
							// was received successfully
							TimeSlot slot = (TimeSlot) it;
							String slotID = String.valueOf(slot.getDate()).concat(String.valueOf(slot.getStartTime()))
									.concat(String.valueOf(slot.getEndTime()));

							System.out.println("The timeslot id is " + slotID);
							// check if student has this timeslot in the timeSlotsToswap list or check if
							// student wants to swap the time slot
							// only swap if group ids of both time slots are not same because no point
							// swapping with student from same group
							// todo issue - - not seen scenario so far where students have tutorial with
							// same time but different group ids.May be randomly generated timetable always
							// skip that
							// todo solution - - may be start passing preferences and timetable in main
							// which will allow manual creation of desired swapping scenarios // this checks
							// if student agent has not already agrred to swap this slot with other agent
													
//							check if timeslot is not already in assignedSlots, if it is then no point swaping it because will make not improvement to the fitness
							if (!timeSlotsTOSwap.containsKey(slotID)
									&& !currentOffers.containsKey(slotID) && timeSlotsTOSwap.size() > 0) {
								
								// to=doadd this slotid to nottoswap list so that it is blocked from swapping because already negotiating with one student

								// send confirm message
								
								// check if the timeslot in the CFP from other student benefit your local fitness Or is it also not in the "prefer not to attend" and "Other caring time"
								
								//get the timeslot with worst fitness from timeSlotsToswap
								TimeSlot myWorstSlot = slotWithWorstFitness(timeSlotsTOSwap, slot.getModuleName());
								
								// check if the time slots belong to same module
//								if(myWorstSlot.getModuleName().contains(slot.getModuleName())) {
								if(myWorstSlot != null) {
									String worstSlotId = String.valueOf(myWorstSlot.getDate()).concat(String.valueOf(myWorstSlot.getStartTime()))
											.concat(String.valueOf(myWorstSlot.getEndTime()));
									String pref = myPreferences.get(worstSlotId);
									
									// store the fitness of the time slot which this student has on the timeSlot to swap list
									int fitnessOfMySlot= myWorstSlot == null ? 20 :calculateFitnessChange(pref);
									
									pref = myPreferences.get(slotID);
									// fitness of the timeslot which other student agent is asking proposal for
									int fitnessofCFPslot = calculateFitnessChange(pref);
									
									// check if CFP timeslot will benefit this student agent
									if (fitnessofCFPslot > fitnessOfMySlot) {
										
										reply.setPerformative(ACLMessage.PROPOSE);

										// craete content element list which will have orignal action requested by other
										// student and offer of this student
										ContentElementList cml = new ContentElementList();
										cml.add(swapAction);

										// craete response offer
										AgreeSwap offer = new AgreeSwap();
										// add the timeslot this student want to swap for the timeslot which came in CFP
										offer.setItem(myWorstSlot);
										offer.setResponse(true);

										// add offer to the content list a swell
										cml.add(offer);

										// Let JADE convert from Java objects to string
										getContentManager().fillContent(reply, cml);
//										System.out.println("-----" + cml);
										myAgent.send(reply); // send reply/offer to other students
										
										// add on the list consersation id to keep record of the proposal (cml which contain myworst and CFPslot) which has been sent to another student agent
										activeProposals.put(msg.getConversationId(), cml);
//										System.out.println("I have the time slot and want to swap!");
									}
									else {
//										System.out.println("Your slot not increasing my fitness");
										reply.setPerformative(ACLMessage.REFUSE);
										send(reply);
									}
								}
								else {
//									System.out.println("Module mismatch");
									reply.setPerformative(ACLMessage.REFUSE);
									send(reply);
								}							

							} else {
//								System.out.println("I dont want to swap time slot");

								reply.setPerformative(ACLMessage.REFUSE);
								send(reply);
								if (timeSlotsTOSwap.size() < 1) {
									myAgent.doDelete();
								}
							}
						}
					}

				}

				catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}

			} else { // if no que in the message que block the behaviour
				block();
			}
		}

	}

	public class CollectOffers extends Behaviour {
		private int numRepliesReceived = 0;
		boolean received = false;
		private int step = 0;
		private ACLMessage replyProposal;

		public CollectOffers(Agent a) {
			super(a);
			currentOffers.clear();
		}

		@Override
		public void action() {
			switch (step) {
			case 0:
				for (String slotID : timeSlotsTOSwap.keySet()) {
					MessageTemplate mt = MessageTemplate.MatchConversationId(slotID + myAgent.getName());
					ACLMessage msg = myAgent.receive(mt);
					if (msg != null) {
						received = true;
						numRepliesReceived++;
						replyProposal = msg.createReply();
						if (msg.getPerformative() == ACLMessage.PROPOSE) {
//							System.out.println("Propose received");
							ContentElement cel = null;
							try {
								cel = getContentManager().extractContent(msg);

								ContentElementList cl = ((ContentElementList) cel);
								ContentElement ceFromCml = cl.get(1);   // usong one because offer/AgreeSwap is stored at index 1
//								System.out.println(ceFromCml);

								if (ceFromCml instanceof AgreeSwap) {
									AgreeSwap resp = (AgreeSwap) ceFromCml;
									TimeSlot propSlot = resp.getItem();
//									System.out.println("The offer received " + resp);
									
									String propSlotId = String.valueOf(propSlot.getDate()).concat(String.valueOf(propSlot.getStartTime()))
											.concat(String.valueOf(propSlot.getEndTime()));
									
									// check propSlot is already not in my assignedTimetable
									if(!timeSlotsTOSwap.containsKey(propSlotId)) {
										
										// calculate utility to see if it inacreses utility points or not
										//get fitness of the timeslot which was sent to other student as CFP
										TimeSlot mySlot = timeSlotsTOSwap.get(slotID);
										String pref = myPreferences.get(slotID);
										// store the fitness of the time slot which this student sent to other student as CFPhas on the timeSlot to swap list
										int fitnessOfMySlot= calculateFitnessChange(pref);
										// fitness of the timeslot which other student agent has replied back with
										
										pref = myPreferences.get(propSlotId);
										int fitnessofPropslot = calculateFitnessChange(pref);
										
										// check if propSlot timeslot will benefit this student agent
										//
										if (fitnessofPropslot >= fitnessOfMySlot) {
											// add offer to the current offers hashmap list when offer for that slot id is
											// recived first time
											if (!currentOffers.containsKey(slotID)) {
												ArrayList<Offer> offers = new ArrayList<>();
												offers.add(new Offer(msg.getSender(), propSlot));
												currentOffers.put(slotID, offers);
											}
											// otherwise add subsequent offers to the existing current offers list
											else {
												ArrayList<Offer> offers = currentOffers.get(slotID);
												offers.add(new Offer(msg.getSender(), propSlot));
											}
										}
										else {
											replyProposal.setPerformative(ACLMessage.REJECT_PROPOSAL);
											myAgent.send(replyProposal);
										}
									}else {
										replyProposal.setPerformative(ACLMessage.REJECT_PROPOSAL);
										myAgent.send(replyProposal);
									}								
								}

							} catch (CodecException ce) {
								ce.printStackTrace();
							} catch (OntologyException oe) {
								oe.printStackTrace();
							}
						} else if (msg.getPerformative() == ACLMessage.REFUSE) { // & numRepliesReceived ==
																							// numQueriesSent
							// if we have received all the replies that means no other student want to swap
							// the slot.
							// So we should not send anymore CFP's for this slot and it should be removed
							// from the slotsToSwap array
							System.out.println("Refuse received");
						}
					}
				}
				
				if (numQueriesSent == numRepliesReceived) {
					step =1;
					break;
				}

			case 1:			// remove slots which didnot get any offers		
				Set<String> ids = timeSlotsTOSwap.keySet();
				ArrayList<String> notToSwap = new ArrayList<String>(); 
				boolean sendrequst = false;
				if (numQueriesSent == numRepliesReceived && currentOffers.size() > 0) {
					for (String id : currentOffers.keySet()) {
						for (String slotToSwapId : ids) {
							if (currentOffers.containsKey(slotToSwapId) ) {
								// if received offer for slotToSwapId then send request to Timerabling agent to perform swap
//								addBehaviour(new SendSwapAction(myAgent)); // not needed here moved to line 511
								sendrequst = true;

							} else {
								// otherwise that means we received no offers for slotToSwapId in // timeSlotsToSwap hashmap
								// so remove it from the list because all other students have confirmed that hey do not want to swap
								boolean removed = true;
								notToSwap.add(slotToSwapId);

//								if (removed) {
//									System.out.println("Time slot is removed from the swapping list. You have to attend it now. "+ removed);
//								}
							}
						}
//						System.out.println("Total offer for " + id + " - " + currentOffers.get(id).size());

					}
					
					// remove timeslots which not required to swap anymore
					for (String id : notToSwap) {
						timeSlotsTOSwap.remove(id);
					}
					
					// call one shot behaviour to send swap requets
					if(sendrequst) {
							// remove offers related to mySlotId because no longer need to swap that time slot and send Reject proposal to agents whose offer was not accepted
						 TimeSlot otherSlot = new TimeSlot();
						for (String id : currentOffers.keySet()) {
							Offer bestOffer=null;
							int BestIndex=0;
							int oldFitness=0;
							String pref = myPreferences.get(id);
							oldFitness = calculateFitnessChange(pref);
							int i =0;
							for (Offer otherOffer : currentOffers.get(id)) {
								otherSlot = otherOffer.getSlot();
								String OfferId = String.valueOf(otherSlot.getDate()).concat(String.valueOf(otherSlot.getStartTime()))
											.concat(String.valueOf(otherSlot.getEndTime()));
								 pref = myPreferences.get(OfferId);
								int fitness = calculateFitnessChange(pref);
								if (fitness > oldFitness ) { //&& slot.getModuleName() == cfpSlot.getModuleName() && slot.getGroupId() != cfpSlot.getGroupId()
									oldFitness = fitness;
									BestIndex = i;
								}
								i++;						
							}					
							bestOffer = currentOffers.get(id).remove(BestIndex);
							
							// send accept proposal
							AID otherStudent = bestOffer.getOfferedByAID();
							ACLMessage propReply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
							propReply.setLanguage(codec.getName());
							propReply.setOntology(ontology.getName());
							propReply.setConversationId(id + myAgent.getName());
							propReply.addReceiver(otherStudent);
							myAgent.send(propReply);
							
							// if theer are anymore offers then send them reject message
							if (currentOffers.get(id).size() > 0) {
								// reject the oher offers
								for(i=0; i< currentOffers.size(); i++) {
									Offer rejectOffer = currentOffers.get(id).remove(i);				
									// Prepare the Reject_proposal message
									otherStudent = rejectOffer.getOfferedByAID();
									propReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
									propReply.setLanguage(codec.getName());
									propReply.setOntology(ontology.getName());
									propReply.setConversationId(id + myAgent.getName());
									propReply.addReceiver(otherStudent);
									myAgent.send(propReply);
								}
							}
							// add back the best offer to the list so that this agent can make request to timetabling agent for it
							currentOffers.get(id).add(bestOffer);			
						}
						addBehaviour(new SendSwapAction(myAgent));
						step = 2;	
					}
					// it is set to 2 because we have sent all the swap requests or have removed the slots which have not recived any offers from timeSlotsToSwap
					received = false;
				}
				
				if (numQueriesSent == numRepliesReceived && (currentOffers.size() == 0 && activeProposals.size() ==0)) {   // means no offers received for any time slots then display final fitness
					int total = utilityFunction(assignedTimeSlots, myPreferences);
					System.out.println("Final utility  of  - " + myAgent.getLocalName() + " : "+ total);
					// delete agent because all other agents refused to swap any of its slots
					myAgent.doDelete();
				}
				break;
			case 2:
				for (String mySlotId : currentOffers.keySet()) {
					// build template to receive result from TA agent for each swapping request using the unique conv id
					String convId = mySlotId + myAgent.getName() + currentOffers.get(mySlotId).get(0).getOfferedByAID();
					AID otherStudent = currentOffers.get(mySlotId).get(0).getOfferedByAID();
					MessageTemplate mt = MessageTemplate.MatchConversationId(convId);
		
					// checks if TA has replied for that id
					ACLMessage msg = myAgent.receive(mt);
					if (msg != null) {
						received = true;
						if(msg.getPerformative() == ACLMessage.INFORM) {
							try {	
								ContentElement ce = null;
//								System.out.println(msg.getContent()); //print out the message content in SL
								// Let JADE convert from String to Java objects // Output will be a ContentElement
								ce = getContentManager().extractContent(msg);

								// check if content is an Action
								if (ce instanceof Result) {
									Result result = ((Result) ce);
									Concept action = result.getAction();			
									
									if(action instanceof Action) {
										Concept act = ((Action) action).getAction();		
										// check if action is of type OwnsTimeSlot
										if (act instanceof UpdateTimetable) {
											UpdateTimetable owns = (UpdateTimetable) act;
											TimeSlot propSlot = owns.getTimeSlot();
										
											currentOffers.get(mySlotId).remove(0);			// removed the first agent whose offer was accepted and who has already been set acceptproposal		
										// update local timetable // remove old slot
											assignedTimeSlots.remove(mySlotId);
											
											// create slot id for prop slot which has now become my new slot id there we should also have new slotid instead of old
											String swappedSlotId = String.valueOf(propSlot.getDate()).concat(String.valueOf(propSlot.getStartTime()))
													.concat(String.valueOf(propSlot.getEndTime()));
											
											assignedTimeSlots.put(swappedSlotId, propSlot);
											// remove the slot from timeSlots to swap because already swapped it
											timeSlotsTOSwap.remove(mySlotId);
											
											if(timeSlotsTOSwap.size() == 0 && assignedTimeSlots.size() > 0) {    // means all swapped are done
												int total = utilityFunction(assignedTimeSlots, myPreferences);
												
												System.out.println("Final utility  of  - " + myAgent.getLocalName() + " : "+ total);
												
												// can delete this agent too because no more time slots to swap
												System.out.println(myAgent.getLocalName());
												for (String string : assignedTimeSlots.keySet()) {
													System.out.println(string);
												}
												myAgent.doDelete();
												
											}																
										}
									}						

									}
							} catch (CodecException ce) {
								ce.printStackTrace();
							} catch (OntologyException oe) {
								oe.printStackTrace();
							}	
						}
						else if(msg.getPerformative() == ACLMessage.FAILURE){	
							// send reply to other student agent that you reject his proposal
							ACLMessage proposeReply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
							proposeReply.setLanguage(codec.getName());
							proposeReply.setOntology(ontology.getName());
							proposeReply.setConversationId(mySlotId + myAgent.getName());
							System.out.println("Error swapping slot. Try again");
							proposeReply.addReceiver(otherStudent);					
							myAgent.send(proposeReply); // sending reply to other student not the timetabling agent
						}		
						step = 3; // case finished so assign value 3 to end behaviour
					}
					
				}
				break;
			}
			if (!received) {
				block();
			}
		}

		@Override
		public boolean done() {
			return numRepliesReceived == numQueriesSent && step ==3;
		}

		@Override
		public int onEnd() {
			// print the offers
//			for (String slotId : timeSlotsTOSwap.keySet()) {
//				if (currentOffers.containsKey(slotId)) {
//					ArrayList<Offer> offers = currentOffers.get(slotId);
//					for (Offer o : offers) {
//						System.out.println(slotId + "," + o.getSeller().getLocalName() + "," + o.getPrice());
//					}
//				} else {
//					System.out.println("No offers for " + slotId);
//				}
//			}
			return 0;
		}

	}

	
	
	
	public class SendSwapAction extends OneShotBehaviour {

		public SendSwapAction(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// send out a call for proposals for each book
			numSwapRequestsSent = 0;
			
			for (String slotID : currentOffers.keySet()) {

				// Prepare the REQUEST message
				ACLMessage swapRequest = new ACLMessage(ACLMessage.REQUEST);
				swapRequest.setLanguage(codec.getName());
				swapRequest.setOntology(ontology.getName());

				// get the first first offer from the offerlist. all other have already 
				//been rejected in case 1 of CollectOffers behaviour
				Offer offer = currentOffers.get(slotID).get(0);  // Offer class move to ontology predicate
				
				// set the id of the request
				swapRequest.setConversationId(slotID + myAgent.getName() + offer.getOfferedByAID());
				
				UpdateTimetable swapSlot = new UpdateTimetable();
				swapSlot.setStudentRequested(myAgent.getAID());
				swapSlot.setStudentOffered(offer.getOfferedByAID());
				TimeSlot s = new TimeSlot();
				s = offer.getSlot();
				swapSlot.setTimeSlot(s);
				swapRequest.addReceiver(timeTablingAgent); // timeTablingAgent is the AID of the timeTablingAgent agent
															// to whom the swap request is sent

				// IMPORTANT: According to FIPA, we need to create a wrapper Action object
				// with the action and the AID of the agent
				// we are requesting to perform the action
				// you will get an exception if you try to send the sell action directly
				// not inside the wrapper!!!
				Action request = new Action();
				request.setAction(swapSlot);
				request.setActor(timeTablingAgent); // the agent that you request to perform the action

				try {
					// Let JADE convert from Java objects to string
					getContentManager().fillContent(swapRequest, request);
					
					myAgent.send(swapRequest); // send request
//					System.out.println(swapRequest);
					numSwapRequestsSent++; // this stores total queries sent for all timeslots to swap
				} catch (CodecException ce) {
					ce.printStackTrace();
				} catch (OntologyException oe) {
					oe.printStackTrace();
				}

			}
			System.out.println("Total swap requests sent to timetabling agent : " + numSwapRequestsSent);

		}
	}
	
	
	
	
	private class ProposalResponseServer extends CyclicBehaviour {
		
		@Override
		public void action() {
			// mesage template using OR to check if we have either type of message in the message queue
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
					MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
			ACLMessage msg = receive(mt);
			if (msg != null) {
				
				String convId = msg.getConversationId();
//				try {
				// deals with both rejects from other student if that sudent rejects the proposal does not want to swap or is rejecting because that student recieved Failure from ta agent for swap request
					if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL ) {
						// remove the proposal from active proposal list because we have reecived reply related to that proposal
						activeProposals.remove(convId);
//						System.out.println("Propose rejected by cfp student. Removed propose from blocking list");
					}
					else if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
						// remove the proposal from active proposal list because we have received reply related to that proposal
						// also remove that slot from timeSlotsToSwap list because Accept_proposal means we have successfully swapped it with other student
						System.out.println("Proposal accepted by cfp student.");
						activeProposals.remove(convId);					
					}
			} else { // if no message in the message que block the behaviour
				block();
			}
		}

	}

}