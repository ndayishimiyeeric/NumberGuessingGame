import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GameMasterAgent extends Agent {
    
    private int randomNumber;
    private int guessesLeft = 10;
    
    protected void setup() {
        System.out.println("Game Master Agent " + getAID().getName() + " is ready.");
        addBehaviour(new GenerateNumberBehaviour());
        addBehaviour(new FeedbackBehaviour());
    }

    private class GenerateNumberBehaviour extends OneShotBehaviour {
        public void action() {
            randomNumber = (int) (Math.random() * 100) + 1;
            System.out.println("Game Master Agent " + getAID().getName() + " has generated the number: " + randomNumber);

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(getAID("guesser"));
            msg.setContent(Integer.toString(randomNumber));
            send(msg);
        }
    }

    private class FeedbackBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = receive(mt);
            if (msg != null) {
                int guess = Integer.parseInt(msg.getContent());
                String replyContent;
                if (guessesLeft == 0) {
                    replyContent = "guess limit reached";
                    System.out.println(getAID().getName() + "Guesser Agent has Lost! guess limit reached");
                } else if (guess == randomNumber) {
                	guessesLeft--;
                    replyContent = "correct";
                    System.out.println(getAID().getName() + " Guesser Agent has Won with " + guessesLeft + " guesses left!");
                } else if (guess < randomNumber) {
                	guessesLeft--;
                    replyContent = "too low";
                    System.out.println(getAID().getName() + " too high!");
                } else {
                	guessesLeft--;
                    replyContent = "too high";
                    System.out.println(getAID().getName() + " too low!");
                }

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(replyContent);
                send(reply);
            } else {
                block();
            }
        }
    }
}