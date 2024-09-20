import java.util.ArrayList;
import java.util.List;

public class EdgeNode {
    private String id;
    private int capacity;
    private float CDF;
    private float distance;
    private List<Client> assignedClients;

    public EdgeNode(String id, int capacity, float CDF, float distance) {
        this.id = id;
        this.capacity = capacity;
        this.assignedClients = new ArrayList<>();
        this.CDF = CDF;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public float getDistance() {
        return distance;
    }

    public float getCDF() {
        return CDF;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Client> getAssignedClients() {
        return assignedClients;
    }

    public boolean isFull() { // the number of clients assigned equals the capacity
        return assignedClients.size() == capacity;
    }

    public boolean isOverSubscribed() { // the number of clients currently assigned exceeds the capacity
        return assignedClients.size() > capacity;
    }

    public void assignClient(Client client) {
        assignedClients.add(client);
    }

    public void removeClient(Client client) {
        assignedClients.remove(client);
    }

    public Client getWorstAssignedClient() {
        return assignedClients.get(assignedClients.size() - 1); // Assuming the last one is the worst
    }
}
