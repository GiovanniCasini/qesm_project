import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AllocationsTest {
    public static void main(String[] args) {
        //long seed = 12345L;
        Random random = new Random();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter("allocations_test.csv"))){
            writer.write("NumberOfClients,AllocatedClients,NotAllocatedClients\n");
        
            int maxClients = 40;
            for (int nClients = 1; nClients <= maxClients; nClients += 1) {
                System.out.println("Number of Clients: " + nClients);

                List<Client> clients = createClients(nClients, random);
                List<CloudProvider> cloudProviders = createCloudProviders(clients, random);

                System.out.println("\n--- Assegnamenti Randomici ---");
                assignClientsRandomly(clients, cloudProviders);

                writer.write(nClients + "," + numAllocatedClients(clients) + "," + numNotAllocatedClients(clients) + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
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
                System.out.println("Non ci sono nodi disponibili");
                break;
            }
    
            EdgeNode selectedNode = availableNodes.get(random.nextInt(availableNodes.size()));
            client.setAssignedEdgeNode(selectedNode);
            selectedNode.assignClient(client);
            System.out.println("Client_" + client.getId() + " -> EdgeNode_" + selectedNode.getId());
            
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

        System.out.println("\n--- Clienti Generati ---");
        for (Client client : clients) {
            System.out.println(client);
        }
        return clients;
    }

    private static List<CloudProvider> createCloudProviders(List<Client> clients, Random random) {
        List<CloudProvider> cloudProviders = new ArrayList<>();
        List<Integer> numNodesPerCloudProvider = new ArrayList<>(Arrays.asList(2, 4, 6));
        List<Integer> distancePerCloudProvider = new ArrayList<>(Arrays.asList(random.nextInt(1000), random.nextInt(1000), random.nextInt(1000)));
        List<Integer> capacityPerCloudProvider = new ArrayList<>(Arrays.asList(3, 2, 1));
        List<Float> satisfactionProbabilities = new ArrayList<>(Arrays.asList(0.9f, 0.8f, 0.7f));

        for (int i = 0; i < numNodesPerCloudProvider.size(); i++) {
            CloudProvider cloudProvider = new CloudProvider(i + 1);
            for (int j = 0; j < numNodesPerCloudProvider.get(i); j++) {
                EdgeNode edgeNode = new EdgeNode((i + 1) + "_" + (j + 1), distancePerCloudProvider.get(i), 
                capacityPerCloudProvider.get(i), satisfactionProbabilities.get(i), cloudProvider); 
                cloudProvider.addEdgeNode(edgeNode);
            }
            cloudProviders.add(cloudProvider);
        }

        System.out.println("\n--- Cloud Provider e Nodi Edge Generati ---");
        for (CloudProvider provider : cloudProviders) {
            System.out.println(provider);
        }
        return cloudProviders;
    }

    private static int numAllocatedClients(List<Client> clients){
        int count = 0;
        for (Client client : clients) {
            if (client.getAssignedEdgeNode() != null){
                count++;
            }
        }
        return count;
    }

    private static int numNotAllocatedClients(List<Client> clients){
        int count = 0;
        for (Client client : clients) {
            if (client.getAssignedEdgeNode() == null){
                count++;
            }
        }
        return count;
    }
}
