import java.util.ArrayList;
import java.util.List;

public class MatchingExternalities {
    public static void matching(List<Client> clients, List<CloudProvider> cloudProviders){
        while (someClientIsFreeWithNonEmptyList(clients)){
            for (Client client : clients){
                if (client.getAssignedEdgeNode() == null && !client.getPreferenceList().isEmpty()){
                    EdgeNode edgeNode = client.getPreferenceList().get(0);

                    // provisionally assign
                    client.setAssignedEdgeNode(edgeNode);
                    edgeNode.assignClient(client);

                    // edgenode oversubscribed
                    if (edgeNode.getAssignedClients().size() > edgeNode.getCapacity()){
                        Client worstClient = edgeNode.getWorstAssignedClient();
                        edgeNode.removeClient(worstClient);
                        worstClient.setAssignedEdgeNode(null);
                    }

                    // edgenode full
                    if (edgeNode.getAssignedClients().size() == edgeNode.getCapacity()){
                        Client worstClient = edgeNode.getWorstAssignedClient();
                        List<Client> successors = getSuccessors(edgeNode, worstClient);
                        for (Client successor : successors) {
                            successor.removeFromPreferenceList(edgeNode);
                            edgeNode.removeFromPreferenceList(successor);
                        }
                    }
                    edgeNode.updateExecutionProbability();
                    updateAllClientsPreferenceLists(clients);
                }
            }
        }
        /*
        System.out.println("\n--- Assignments ---");
        for (Client client : clients){
            if (client.getAssignedEdgeNode() != null){
                System.out.println("Client_" + client.getId() + " -> EdgeNode_" + client.getAssignedEdgeNode().getId());
            }
            else {
                System.out.println("Client_" + client.getId() + " not assigned.");
            }
        }
            */
    }

    private static boolean someClientIsFreeWithNonEmptyList(List<Client> clients) {
        for (Client client : clients) {
            if (client.getAssignedEdgeNode() == null && !client.getPreferenceList().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static List<Client> getSuccessors(EdgeNode edgeNode, Client worstClient) {
        List<Client> successors = new ArrayList<>();
        List<Client> preferenceList = edgeNode.getPreferenceList();
        boolean foundWorstClient = false;

        for (Client client : preferenceList) {
            if (foundWorstClient) {
                successors.add(client);
            }
            else if (client.equals(worstClient)) {
                foundWorstClient = true;
            }
        }
        return successors;
    }

    private static void updateAllClientsPreferenceLists(List<Client> clients) {
        for (Client client : clients) {
            client.updatePreferenceList();
        }
    }
}