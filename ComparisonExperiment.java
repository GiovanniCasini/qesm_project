import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ComparisonExperiment {
    public static void main(String[] args) {
        Random random = new Random();
        String csvFile = "results.csv"; // Nome del file CSV
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("NumberOfClients,AverageSuccessGreedyDistance,AverageSuccessGreedyCDF,AverageCostGreedyDistance,AverageCostGreedyCDF\n");
            
            // Define the range for the number of clients
            int minClients = 5; // Minimum number of clients
            int maxClients = 50; // Maximum number of clients
            int step = 1; // Step size for incrementing clients
            
            // Loop through different numbers of clients
            for (int nClients = minClients; nClients <= maxClients; nClients += step) {
                System.out.println("Number of Clients: " + nClients);
                
                // Create Clients
                List<Client> clients = createClients(nClients, random);
                
                // Create CloudProviders
                List<CloudProvider> cloudProviders = createCloudProviders(clients, random);
                
                // Run greedy algorithm for distance
                System.out.println("Running greedy algorithm for distance...");
                float[] avgsGreedyDistance = runExperiment(clients, cloudProviders, 0.8f, 0.2f);
                
                // Reset clients and CPs for the next experiment
                clients = createClients(nClients, random);
                cloudProviders = createCloudProviders(clients, random);
                
                // Run greedy algorithm for CDF
                System.out.println("\nRunning greedy algorithm for CDF...");
                float[] avgsGreedyCDF = runExperiment(clients, cloudProviders, 0.2f, 0.8f);
                
                // Write results to CSV
                writer.write(nClients + "," + avgsGreedyDistance[0] + "," + avgsGreedyCDF[0] + "," + avgsGreedyDistance[1] + "," + avgsGreedyCDF[1] + "\n");
                
                System.out.println("----------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Client> createClients(int nClients, Random random) {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < nClients; i++) {
            Client client = new Client("C" + i, null, random.nextFloat(10));
            clients.add(client);
        }
        Collections.sort(clients, new Comparator<Client>() {
            @Override
            public int compare(Client c1, Client c2) {
                return Float.compare(c2.getCost(), c1.getCost());
            }
        });
        return clients;
    }

    private static List<CloudProvider> createCloudProviders(List<Client> clients, Random random) {
        List<CloudProvider> cloudProviders = new ArrayList<>();
        List<Integer> nNodesPerCloudProvider = Arrays.asList(1, 5, 10);
        List<Integer> capacityPerCloudProvider = Arrays.asList(5, 2, 1);
        List<Float> cdfPerCloudProvider = Arrays.asList(0.8f, 0.5f, 0.1f);
        List<Float> distancePerCloudProvider = Arrays.asList(150f, 100f, 50f);

        for (int i = 0; i < nNodesPerCloudProvider.size(); i++) {
            List<EdgeNode> edgeNodes = new ArrayList<>();
            for (int j = 0; j < nNodesPerCloudProvider.get(i); j++) {               
                EdgeNode edgeNode = new EdgeNode("E" + i + "_" + j, capacityPerCloudProvider.get(i), 
                    cdfPerCloudProvider.get(i), distancePerCloudProvider.get(i) + random.nextFloat(5f));
                edgeNodes.add(edgeNode);
            }
            Map<EdgeNode, List<Client>> prefList = new HashMap<>();
            for (EdgeNode e : edgeNodes) {
                prefList.put(e, new ArrayList<>(clients));
            }
            CloudProvider cloudProvider = new CloudProvider("CP" + i, edgeNodes, prefList);
            cloudProviders.add(cloudProvider);
        }
        return cloudProviders;
    }

    private static float[] runExperiment(List<Client> clients, List<CloudProvider> cloudProviders, float weightDistance, float weightCDF) {
        // Set preferences for Clients
        for (Client client : clients) {
            client.evaluatePreferences(cloudProviders, weightDistance, weightCDF);
        }

        // Run the matching algorithm
        MatchingAlgorithm.matchClientsToEdgeNodes(clients, cloudProviders);

        // Calculate metrics
        float avgSuccess = calculateAverageSuccess(clients);
        System.out.println("Average Success: " + avgSuccess);
        float avgCost = calculateAverageCost(clients);
        System.out.println("Average Cost: " + avgCost);
        return new float[]{avgSuccess, avgCost};
    }

    // Calculate the average success probability
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

    // Calculate the average cost (average distance)
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
