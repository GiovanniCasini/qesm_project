import java.util.ArrayList;
import java.util.List;

public class MatchingAlgorithm {

    public static void matchClientsToEdgeNodes(List<Client> clients, List<CloudProvider> cloudProviders) {
        while (someClientIsFreeWithNonEmptyList(clients)) {
            for (Client client : clients) {
                if (client.getAssignedEdgeNode() == null && client.hasPreference()) {
                    EdgeNode edgeNode = client.getPreferenceList().get(0);
                    CloudProvider cloudProvider = findCloudProviderForEdgeNode(cloudProviders, edgeNode);

                    // Provisionally assign client to edgeNode
                    client.setAssignedEdgeNode(edgeNode);
                    edgeNode.assignClient(client);

                    // Handle over-subscription
                    if (edgeNode.isOverSubscribed()) {
                        Client worstClient = edgeNode.getWorstAssignedClient();
                        edgeNode.removeClient(worstClient);
                        worstClient.setAssignedEdgeNode(null);
                    }

                    // Handle full edgeNode
                    if (edgeNode.isFull()) {
                        Client worstClient = edgeNode.getWorstAssignedClient();
                        List<Client> successors = getSuccessors(cloudProvider, edgeNode, worstClient);
                        for (Client successor : successors) {
                            successor.removeFromPreferenceList(edgeNode);
                        }
                    }

                    // Handle full cloudProvider
                    /*
                    if (cloudProvider.isFull()) {
                        Client worstClient = cloudProvider.getAssignedClients().get(cloudProvider.getAssignedClients().size() - 1);
                        for (EdgeNode eNode : cloudProvider.getEdgeNodes()) {
                            List<Client> successors = getSuccessors(cloudProvider, eNode, worstClient);
                            for (Client successor : successors) {
                                successor.removeFromPreferenceList(eNode);
                            }
                        }
                    }
                    */
                }
            }
        }
    }

    private static boolean someClientIsFreeWithNonEmptyList(List<Client> clients) {
        for (Client client : clients) {
            if (client.getAssignedEdgeNode() == null && client.hasPreference()) {
                return true;
            }
        }
        return false;
    }

    private static CloudProvider findCloudProviderForEdgeNode(List<CloudProvider> cloudProviders, EdgeNode edgeNode) {
        for (CloudProvider provider : cloudProviders) {
            if (provider.getEdgeNodes().contains(edgeNode)) {
                return provider;
            }
        }
        return null;
    }

    private static List<Client> getSuccessors(CloudProvider cloudProvider, EdgeNode edgeNode, Client worstClient) {
        List<Client> successors = new ArrayList<>();
        List<Client> preferenceList = cloudProvider.getPreferenceList(edgeNode);
        boolean foundWorstClient = false;

        for (Client client : preferenceList) {
            if (foundWorstClient) {
                successors.add(client);
            }
            if (client.equals(worstClient)) {
                foundWorstClient = true;
            }
        }

        return successors;
    }
}
