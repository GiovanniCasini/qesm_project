import java.util.ArrayList;
import java.util.List;

public class EdgeNode {
    private String id;
    private float distance;
    private int capacity;
    private List<Client> assignedClients;
    private float executionProbability; 
    private float firstExecProb; 
    private CloudProvider cloudProvider;
    private List<Client> preferenceList;
    private List<Client> firstPreferenceList;

    public EdgeNode(String id, float distance, int capacity, float executionProbability, CloudProvider cloudProvider) {
        this.id = id;
        this.distance = distance;
        this.capacity = capacity;
        this.assignedClients = new ArrayList<>();
        this.executionProbability = executionProbability; 
        this.firstExecProb = executionProbability; 
        this.cloudProvider = cloudProvider;
        this.preferenceList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public float getDistance() {
        return distance;
    }

    public int getCapacity() {
        return capacity;
    }

    public float getExecutionProbability() {
        return executionProbability; 
    }

    public void assignClient(Client client) {
        assignedClients.add(client);
    }

    public List<Client> getAssignedClients() {
        return assignedClients;
    }

    public void setPreferenceList(List<Client> preferenceList) {
        this.preferenceList = new ArrayList<>(preferenceList);
        this.firstPreferenceList = new ArrayList<>(preferenceList);
    }

    public List<Client> getPreferenceList() {
        return preferenceList;
    }

    public void removeFromPreferenceList(Client client) {
        preferenceList.remove(client);
    }

    public List<Client> getFirstPreferenceList() {
        return firstPreferenceList; 
    }

    public CloudProvider getCloudProvider() {
        return cloudProvider;
    }

    public Client getWorstAssignedClient() {
        Client worstClient = null;
        float worstPayment = Float.MAX_VALUE;;
        for (Client client : assignedClients){
            if (client.getPayment() < worstPayment){
                worstClient = client;
                worstPayment = client.getPayment();
            }
        }
        return worstClient;
    }

    public void removeClient(Client client) {
        assignedClients.remove(client);
    }

    public void updateExecutionProbability() {
        this.executionProbability = (float) firstExecProb - (assignedClients.size() * (firstExecProb / (5 * capacity)));
    }

    public float getFirstExecProb(){
        return firstExecProb;
    }

    @Override
    public String toString() {
        return "EdgeNode_" + id + ", Distance: " + distance + ", Capacity: " + capacity + 
               ", Execution Probability: " + executionProbability + "\n";
    }
}
