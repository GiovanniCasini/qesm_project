import java.util.*;

public class GeneralTest {
    public static void main(String[] args) {
        Random random = new Random();
        
        int numClients = 40;
        List<Client> clients = createClients(numClients, random);
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

        //Matching.matching(clients, cloudProviders);

        Matching.matching(clients, cloudProviders);
        
        float satisfactionProbability = overallSatisfactionProbability(clients);
        float failureProbability = overallFailureProbability(clients);
        float revenue = totalRevenue(clients);
        System.out.println("SatisfProb: " + satisfactionProbability + " FailProb: " + failureProbability + " Revenue: " + revenue);

        System.out.println("\n--- Assignments ---");
        for (Client client : clients){
            if (client.getAssignedEdgeNode() != null){
                System.out.println("Client_" + client.getId() + " -> EdgeNode_" + client.getAssignedEdgeNode().getId());
            }
            else {
                System.out.println("Client_" + client.getId() + " not assigned.");
            }
        }


        verifyStableMatching(clients, cloudProviders);
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
        List<Integer> numNodesPerCloudProvider = new ArrayList<>(Arrays.asList(1, 2, 4));
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

    private static void verifyStableMatching(List<Client> clients, List<CloudProvider> cloudProviders){
        int blockingPairs = 0;
        for (Client client : clients){
            for (CloudProvider cp : cloudProviders){
                for (EdgeNode edgeNode : cp.getEdgeNodes()){
                    EdgeNode currentNode = client.getAssignedEdgeNode();
                    if (edgeNode.equals(currentNode)) continue;
                    // Verifica se il client preferisce il nodo alternativo rispetto a quello corrente
                    if (client.getFirstPreferenceList().indexOf(edgeNode) < client.getFirstPreferenceList().indexOf(currentNode)){
                        Client worstClient = edgeNode.getWorstAssignedClient();
                        if (client.getPayment() > worstClient.getPayment()){
                            System.out.println("Coppia bloccante trovata tra Client_" + client.getId() + " e EdgeNode_" + edgeNode.getId());
                            blockingPairs += 1;
                        }
                    }
                }
            }
        }
        if (blockingPairs == 0) System.out.println("\nMatching stabile, nessuna coppia bloccante trovata!");
    }

    
}
