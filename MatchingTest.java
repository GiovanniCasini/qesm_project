import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MatchingTest {
    public static void main(String[] args) {
        Random random = new Random();
        int numRuns = 5000;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter("csvs/matching_externalities_test.csv"))){
            writer.write("NumberOfClients,AverageSatisfactionProbability,AverageFailureProbability,AverageRevenue\n");
        
            int maxClients = 100;
            for (int nClients = 1; nClients <= maxClients; nClients += 1) {
                System.out.println("Number of Clients: " + nClients);

                float totalSatisfactionProbability = 0;
                float totalFailureProbability = 0;
                float totalRevenue = 0;

                for (int run = 0; run < numRuns; run++) {
                    List<Client> clients = createClients(nClients, random);
                    List<Client> sortedClients = sortClients(clients);
                    List<CloudProvider> cloudProviders = createCloudProviders(clients, random);

                    // Calcola preference list per i clients
                    for (Client client : clients){
                        client.createClientPreferenceList(cloudProviders);
                    }
                    // Calcola preference list per gli edgenodes
                    for (CloudProvider cp : cloudProviders){
                        for (EdgeNode e : cp.getEdgeNodes()){
                            e.setPreferenceList(sortedClients);
                        }
                    }

                    MatchingExternalities.matching(clients, cloudProviders);

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


    private static List<Client> createClients(int numClients, Random random){
        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < numClients; i++) {
            float sla = Math.round((0.1f + random.nextFloat() * 0.9f) * 100f) / 100f; // SLA casuale tra 0.1 e 1
            int pos = random.nextInt(1000); // POS casuale tra 0 e 1000
            clients.add(new Client(i + 1, sla, pos));
        }
        /*
        System.out.println("\n--- Client Generati ---");
        for (Client client : clients) {
            System.out.println(client);
        }
            */
        return clients;
    }

    private static List<Client> sortClients(List<Client> clients){
        List<Client> sortedClients = new ArrayList<>(clients);
        Collections.sort(sortedClients, new Comparator<Client>() {
            @Override
            public int compare(Client c1, Client c2) {
                return Float.compare(c2.getPayment(), c1.getPayment());
            }
        });
        /*
        System.out.println("\n--- Sorted Clients ---");
        for (Client client : sortedClients) {
            System.out.println(client);
        }
            */
        return sortedClients;
    }

    private static List<CloudProvider> createCloudProviders(List<Client> clients, Random random) {
        List<CloudProvider> cloudProviders = new ArrayList<>();
        List<Integer> numNodesPerCloudProvider = new ArrayList<>(Arrays.asList(2, 4, 8));
        List<Integer> distancePerCloudProvider = new ArrayList<>(
            Arrays.asList(750+random.nextInt(250), 500+random.nextInt(500), random.nextInt(1000)));
        List<Integer> capacityPerCloudProvider = new ArrayList<>(Arrays.asList(4, 2, 1));
        List<Float> executionProbabilities = new ArrayList<>(Arrays.asList(1f, 0.9f, 0.8f));

        for (int i = 0; i < numNodesPerCloudProvider.size(); i++) {
            CloudProvider cloudProvider = new CloudProvider(i + 1);
            for (int j = 0; j < numNodesPerCloudProvider.get(i); j++) {
                EdgeNode edgeNode = new EdgeNode((i + 1) + "_" + (j + 1), distancePerCloudProvider.get(i), 
                capacityPerCloudProvider.get(i), executionProbabilities.get(i), cloudProvider); 
                cloudProvider.addEdgeNode(edgeNode);
            }
            cloudProviders.add(cloudProvider);
        }
        /*
        System.out.println("\n--- Cloud Provider e Nodi Edge Generati ---");
        for (CloudProvider provider : cloudProviders) {
            System.out.println(provider);
        }
             */
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
