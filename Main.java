import java.util.*;

public class Main {
    public static void main(String[] args) {
        Random random = new Random();

        // Create Clients
        int nClients = 10;
        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < nClients; i++) {
            Client client = new Client("C" + i, null, random.nextFloat(10));
            clients.add(client);
        }

        for (Client client : clients) {
            System.out.print("[" + client.getId() + " " + client.getCost() + "]");
        }
        System.out.println();

        // Sort Clients by cost
        Collections.sort(clients, new Comparator<Client>() {
            @Override
            public int compare(Client c1, Client c2) {
                return Float.compare(c2.getCost(), c1.getCost());
            }
        });

        for (Client client : clients) {
            System.out.print("[" + client.getId() + " " + client.getCost() + "]");
        }
        System.out.println();


        // Create CloudProviders
        List<CloudProvider> cloudProviders = new ArrayList<>();
        List<Integer> nNodesPerCloudProvider = new ArrayList<>(Arrays.asList(1, 2, 4));
        List<Integer> capacityPerCloudProvider = new ArrayList<>(Arrays.asList(5, 2, 1));
        List<Float> cdfPerCloudProvider = new ArrayList<>(Arrays.asList(0.9f, 0.5f, 0.2f));
        List<Float> distancePerCloudProvider = new ArrayList<>(Arrays.asList(100f, 50f, 20f));
        
        for (int i = 0; i < nNodesPerCloudProvider.size(); i++) {
            List<EdgeNode> edgeNodes = new ArrayList<>();
            for (int j = 0; j < nNodesPerCloudProvider.get(i); j++) {
                EdgeNode edgeNode = new EdgeNode("E" + i + "_" + j, capacityPerCloudProvider.get(i), 
                    cdfPerCloudProvider.get(i), distancePerCloudProvider.get(i));
                edgeNodes.add(edgeNode);
            }
            Map<EdgeNode, List<Client>> prefList = new HashMap<>();
            for (EdgeNode e : edgeNodes) {
                prefList.put(e, new ArrayList<>(clients));
            }
            CloudProvider cloudProvider = new CloudProvider("CP" + i, edgeNodes, prefList);
            cloudProviders.add(cloudProvider);
        }

        for (CloudProvider cp : cloudProviders) {
            System.out.print(cp.getId() + " ");
            for (EdgeNode e : cp.getEdgeNodes()) {
                System.out.print("[" + e.getId() + " " + e.getCapacity() + " " + e.getCDF() + " " + e.getDistance() +"] ");
            }
            System.out.println();
        }

        // Set preferences for Clients
        for (Client client : clients) {
            client.evaluatePreferences(cloudProviders, 0.5f, 0.5f);
        }
        for (Client client : clients) {
            System.out.print(client.getId() + " ");
            for (EdgeNode e : client.getPreferenceList()) {
                System.out.print("[" + e.getId() + "]");
            } 
            System.out.println();
        }
        System.out.println();

        // Run the algorithm
        MatchingAlgorithm.matchClientsToEdgeNodes(clients, cloudProviders);

        // Print the results
        for (Client client : clients) {
            System.out.println("Client " + client.getId() + " is assigned to EdgeNode " + 
                    (client.getAssignedEdgeNode() != null ? client.getAssignedEdgeNode().getId() : "none"));
        }
    }
}
