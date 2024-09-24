import java.util.*;

public class Main {
    public static void main(String[] args) {
        long seed = 12345L;
        Random random = new Random(seed);

        // Create Clients
        int nClients = 15;
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
        List<Integer> nNodesPerCloudProvider = new ArrayList<>(
            Arrays.asList(random.nextInt(5), random.nextInt(10), random.nextInt(20)));
        List<Integer> capacityPerCloudProvider = new ArrayList<>(Arrays.asList(5, 2, 1));
        List<Float> cdfPerCloudProvider = new ArrayList<>(
            Arrays.asList(random.nextFloat(1f), random.nextFloat(0.8f), random.nextFloat(0.5f)));
        List<Float> distancePerCloudProvider = new ArrayList<>(
            Arrays.asList(random.nextFloat(200f), random.nextFloat(150f), random.nextFloat(100f)));
        
        // Create EdgeNodes for each CloudProvider
        for (int i = 0; i < nNodesPerCloudProvider.size(); i++) {
            List<EdgeNode> edgeNodes = new ArrayList<>();
            for (int j = 0; j < nNodesPerCloudProvider.get(i); j++) {               
                EdgeNode edgeNode = new EdgeNode("E" + i + "_" + j, capacityPerCloudProvider.get(i), 
                    cdfPerCloudProvider.get(i), distancePerCloudProvider.get(i) + random.nextFloat(20f));
                edgeNodes.add(edgeNode);
            }
            // Create Preference Lists for EdgeNodes based on Client Cost (already sorted)
            Map<EdgeNode, List<Client>> prefList = new HashMap<>();
            for (EdgeNode e : edgeNodes) {
                prefList.put(e, new ArrayList<>(clients));
            }
            CloudProvider cloudProvider = new CloudProvider("CP" + i, edgeNodes, prefList);
            cloudProviders.add(cloudProvider);
        }
        
        // Print CloudProviders
        for (CloudProvider cp : cloudProviders) {
            System.out.print(cp.getId() + " ");
            for (EdgeNode e : cp.getEdgeNodes()) {
                System.out.print("[" + e.getId() + " " + e.getCapacity() + " " + e.getCDF() + " " + e.getDistance() +"] ");
            }
            System.out.println();
        }

        // Set preferences for Clients
        int mode = 0; // 0 -> matching, 1 -> random
        if (mode == 0) {
            for (Client client : clients) {
                client.evaluatePreferences(cloudProviders, 0.8f, 0.2f);
            }
        }
        else {
            // Set preferences for Clients randomly
            for (Client client : clients) {
                client.evaluatePreferencesRandomly(cloudProviders);
            }
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

        // Calcolo delle metriche
        float avgSuccess = calculateAverageSuccess(clients);
        float avgCost = calculateAverageCost(clients);
        System.out.println("Average Success: " + avgSuccess);
        System.out.println("Average Cost: " + avgCost);
        System.out.println();
    }

    // Calcola la probabilità di successo media
    public static float calculateAverageSuccess(List<Client> clients) {
        float totalSuccess = 0;
        for (Client client : clients) {
            EdgeNode assignedNode = client.getAssignedEdgeNode();
            if (assignedNode != null) {
                totalSuccess += assignedNode.getCDF();
            }
        }
        return totalSuccess / clients.size();
    }

    // Calcola il costo medio (distanza media)
    public static float calculateAverageCost(List<Client> clients) {
        float totalCost = 0;
        for (Client client : clients) {
            EdgeNode assignedNode = client.getAssignedEdgeNode();
            if (assignedNode != null) {
                totalCost += assignedNode.getDistance();
            }
        }
        return totalCost / clients.size();
    }
}
