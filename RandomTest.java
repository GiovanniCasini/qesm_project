import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RandomTest {
    public static void main(String[] args) {
        Random random = new Random();
        int numRuns = 10000;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter("csvs/random_externalities_test.csv"))){
            writer.write("NumberOfClients,AverageSatisfactionProbability,AverageFailureProbability,AverageRevenue\n");
        
            int maxClients = 100;
            for (int nClients = 1; nClients <= maxClients; nClients += 1) {
                System.out.println("Number of Clients: " + nClients);

                float totalSatisfactionProbability = 0;
                float totalFailureProbability = 0;
                float totalRevenue = 0;

                for (int run = 0; run < numRuns; run++) {
                    List<Client> clients = createClients(nClients, random);
                    List<CloudProvider> cloudProviders = createCloudProviders(clients, random);

                    assignClientsRandomlyWithExternalities(clients, cloudProviders);

                    totalSatisfactionProbability += overallSatisfactionProbability(clients);
                    totalFailureProbability += overallFailureProbability(clients);
                    totalRevenue += totalRevenue(clients);
                }

                float avgSatisfactionProbability = totalSatisfactionProbability / numRuns;
                float avgFailureProbability = totalFailureProbability / numRuns;
                float avgRevenue = totalRevenue / numRuns;

                writer.write(nClients + "," + avgSatisfactionProbability + "," + avgFailureProbability + "," + avgRevenue + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void assignClientsRandomlyWithExternalities(List<Client> clients, List<CloudProvider> cloudProviders) {
        Random random = new Random();
    
        List<EdgeNode> availableNodes = new ArrayList<>();
        for (CloudProvider provider : cloudProviders) {
            availableNodes.addAll(provider.getEdgeNodes());
        }
    
        for (Client client : clients) {
            if (availableNodes.isEmpty()) {
                //System.out.println("Non ci sono nodi disponibili");
                break;
            }
    
            EdgeNode selectedNode = availableNodes.get(random.nextInt(availableNodes.size()));
            client.setAssignedEdgeNode(selectedNode);
            selectedNode.assignClient(client);
            float nodeFirstExecProb = selectedNode.getFirstExecProb();
            client.setSatisfProb(((float) nodeFirstExecProb - (selectedNode.getAssignedClients().size() * (nodeFirstExecProb / (5 * selectedNode.getCapacity()))))
                * (calculateReachabilityProbability(Math.abs(client.getPosition() - client.getAssignedEdgeNode().getDistance()))));
            //System.out.println("Client_" + client.getId() + " -> EdgeNode_" + selectedNode.getId());
            
            if (selectedNode.getAssignedClients().size() >= selectedNode.getCapacity()) {
                availableNodes.remove(selectedNode);
            }
        }
    }    

    public static void assignClientsRandomly(List<Client> clients, List<CloudProvider> cloudProviders) {
        Random random = new Random();
    
        List<EdgeNode> availableNodes = new ArrayList<>();
        for (CloudProvider provider : cloudProviders) {
            availableNodes.addAll(provider.getEdgeNodes());
        }
    
        for (Client client : clients) {
            if (availableNodes.isEmpty()) {
                //System.out.println("Non ci sono nodi disponibili");
                break;
            }
    
            EdgeNode selectedNode = availableNodes.get(random.nextInt(availableNodes.size()));
            client.setAssignedEdgeNode(selectedNode);
            selectedNode.assignClient(client);
            //System.out.println("Client_" + client.getId() + " -> EdgeNode_" + selectedNode.getId());
            
            if (selectedNode.getAssignedClients().size() >= selectedNode.getCapacity()) {
                availableNodes.remove(selectedNode);
            }
        }
    }    
    
    private static List<Client> createClients(int numClients, Random random){
        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < numClients; i++) {
            float sla = Math.round((0.1f + random.nextFloat() * 0.9f) * 100f) / 100f; // SLA casuale tra 0.1 e 1
            int pos = random.nextInt(1000); // POS casuale tra 0 e 100
            clients.add(new Client(i + 1, sla, pos));
        }

        return clients;
    }

    private static List<CloudProvider> createCloudProviders(List<Client> clients, Random random) {
        List<CloudProvider> cloudProviders = new ArrayList<>();
        List<Integer> numNodesPerCloudProvider = new ArrayList<>(Arrays.asList(2, 4, 8));
        List<Integer> distancePerCloudProvider = new ArrayList<>(
            Arrays.asList(750+random.nextInt(250), 500+random.nextInt(500), random.nextInt(1000)));
        List<Integer> capacityPerCloudProvider = new ArrayList<>(Arrays.asList(4, 2, 1));
        List<Float> satisfactionProbabilities = new ArrayList<>(Arrays.asList(1f, 0.9f, 0.8f));

        for (int i = 0; i < numNodesPerCloudProvider.size(); i++) {
            CloudProvider cloudProvider = new CloudProvider(i + 1);
            for (int j = 0; j < numNodesPerCloudProvider.get(i); j++) {
                EdgeNode edgeNode = new EdgeNode((i + 1) + "_" + (j + 1), distancePerCloudProvider.get(i), 
                capacityPerCloudProvider.get(i), satisfactionProbabilities.get(i), cloudProvider); 
                cloudProvider.addEdgeNode(edgeNode);
            }
            cloudProviders.add(cloudProvider);
        }

        return cloudProviders;
    }

    private static float overallSatisfactionProbability(List<Client> clients){
        float count = 0;
        for (Client client : clients) {
            if (client.getAssignedEdgeNode() != null){
                count += client.getSatisfProb();
            }
        }
        return count / clients.size();
    }

    private static float overallFailureProbability(List<Client> clients){
        float count = 0;
        for (Client client : clients) {
            if (client.getAssignedEdgeNode() != null){
                count += client.getSatisfProb();
            }
        }
        return 1 - (count / clients.size());
    }

    private static float calculateReachabilityProbability(float totalDistance) {
        float minDistance = 0;
        float maxDistance = 1000;
        float maxProbability = 1f; 
        float minProbability = 0.1f; 

        if (totalDistance < minDistance) {
            return maxProbability; // Se la distanza è inferiore a minDistance, ritorna maxProbability
        } else if (totalDistance > maxDistance) {
            return minProbability; // Se la distanza è superiore a maxDistance, ritorna minProbability
        } else {
            // Interpolazione lineare tra minDistance e maxDistance
            return maxProbability - (maxProbability - minProbability) * (totalDistance - minDistance) / (maxDistance - minDistance);
        }
    }

    private static float totalRevenue(List<Client> clients){
        float count = 0;
        for (Client client : clients) {
            if (client.getAssignedEdgeNode() != null){
                count += client.getPayment();
            }
        }
        return count;
    }
}