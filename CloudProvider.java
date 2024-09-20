import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class CloudProvider {
    private String id;
    private List<EdgeNode> edgeNodes;
    private Map<EdgeNode, List<Client>> preferenceList;
    //private int capacity;

    public CloudProvider(String id, List<EdgeNode> edgeNodes, Map<EdgeNode, List<Client>> preferenceList /*, int capacity*/) {
        this.id = id;
        this.edgeNodes = edgeNodes;
        this.preferenceList = preferenceList;
        //this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public List<EdgeNode> getEdgeNodes() {
        return edgeNodes;
    }

    public List<Client> getPreferenceList(EdgeNode edgeNode) {
        return preferenceList.get(edgeNode);
    }
    /* 
    public boolean isFull() {
        return getAssignedClients().size() == capacity;
    }

    public boolean isOverSubscribed() {
        return getAssignedClients().size() > capacity;
    }
    */
    public List<Client> getAssignedClients() {
        List<Client> allAssignedClients = new ArrayList<>();
        for (EdgeNode edgeNode : edgeNodes) {
            allAssignedClients.addAll(edgeNode.getAssignedClients());
        }
        return allAssignedClients;
    }

    public void removeClientFromPreferenceLists(Client client) {
        for (List<Client> clients : preferenceList.values()) {
            clients.remove(client);
        }
    }
}
