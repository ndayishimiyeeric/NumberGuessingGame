import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Main {
    public static void main(String[] args) {
        Profile profile = new ProfileImpl("localhost", 8888, null);
        profile.setParameter(Profile.GUI, "true");

        AgentContainer container = jade.core.Runtime.instance().createMainContainer(profile);

        try {
            AgentController guesser = container.createNewAgent("guesser", GuesserAgent.class.getName(), null);
            AgentController gamemaster = container.createNewAgent("game-master", GameMasterAgent.class.getName(), null);

            guesser.start();
            gamemaster.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}