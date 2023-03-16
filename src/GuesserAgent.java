import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GuesserAgent extends Agent {
    private boolean limitReached = false;
    private MessageTemplate informMessageTemplate;

    protected void setup() {
        System.out.println("Guesser Agent " + getAID().getName() + " is ready.");

        informMessageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        addBehaviour(new ReceiveMessageBehaviour(informMessageTemplate) {
            public void action(ACLMessage msg) {
                addBehaviour(new GuessBehaviour());
            }
        });
    }

    private class GuessBehaviour extends Behaviour {
        private boolean guessed = false;
        private int min = 1;
        private int max = 100;
        private int guess;

        public void action() {
            if (!guessed) {
                // Guess a number if not guessed yet
                guess = (int) (Math.random() * (max - min + 1)) + min;
                System.out.println("Guesser Agent " + getAID().getName() + " has guessed the number:" + guess);

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(getAID("game-master"));
                msg.setContent(Integer.toString(guess));
                send(msg);

                guessed = true;
            } else {
                // Receive the reply from GameMasterAgent
                ACLMessage reply = receive(informMessageTemplate);
                if (reply != null) {
                    if (reply.getContent().equals("correct")) {
                        doDelete();
                        return;
                    } else if (reply.getContent().equals("too high")) {
                        max = guess - 1;
                    } else if (reply.getContent().equals("too low")) {
                        min = guess + 1;
                    } else if (reply.getContent().equals("guess limit reached")) {
                		limitReached = true;
                		doDelete();
                	}

                    // Reset the guessed flag
                    guessed = false;
                } else {
                    block();
                }
            }
        }

        public boolean done() {
            // End the behavior if all guesses are used up
            return  limitReached;
        }
    }

    private class ReceiveMessageBehaviour extends Behaviour {
        private final MessageTemplate mt;
        private boolean done = false;

        public ReceiveMessageBehaviour(MessageTemplate mt) {
            this.mt = mt;
        }

        public void action() {
            ACLMessage msg = receive(mt);
            if (msg != null) {
                action(msg);
                done = true;
            } else {
                // block until the message arrive
                block();
            }
        }

        public void action(ACLMessage msg) {
            // To be implemented by subclasses
        }

        public boolean done() {
            return done;
        }
    }
}
