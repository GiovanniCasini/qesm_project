import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private int id;
    private float sla;
    private int position;
    private float payment;
    private EdgeNode assignedEdgeNode;
    private float reachabilityProbability;
    private List<EdgeNode> preferenceList;
    private List<EdgeNode> firstPreferenceList;
    private float satisfProb;
    
    public Client(int id, float sla, int position) {
        this.id = id;
        this.sla = sla;
        this.payment = Math.round((1.0f / sla) * 100f) / 100f; // Costo disposto a pagare inversamente proporzionale allo SLA
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public float getSla() {
        return sla;
    }

    public int getPosition() {
        return position;
    }

    public float getPayment() {
        return payment;
    }

    public void setAssignedEdgeNode(EdgeNode assignedEdgeNode) {
        this.assignedEdgeNode = assignedEdgeNode;
        if (assignedEdgeNode != null){
           this.reachabilityProbability = calculateReachabilityProbability(Math.abs(position - assignedEdgeNode.getDistance())); 
           this.satisfProb = assignedEdgeNode.getExecutionProbability() * reachabilityProbability;
        }
    }

    public EdgeNode getAssignedEdgeNode() {
        return assignedEdgeNode;
    }

    public float getReachabilityProbability() {
        if (assignedEdgeNode != null){
            return reachabilityProbability; 
        }
        else {
            return 0;
        }
    }

    public float getSatisfProb() {
        if (assignedEdgeNode != null){
            return satisfProb; 
        }
        else {
            return 0;
        }
    }

    public void setSatisfProb(float satisfProb){
        this.satisfProb = satisfProb;
    }

    public List<EdgeNode> getPreferenceList() {
        return preferenceList; 
    }

    public void removeFromPreferenceList(EdgeNode edgeNode) {
        preferenceList.remove(edgeNode);
    }

    public List<EdgeNode> getFirstPreferenceList() {
        return firstPreferenceList; 
    }

    public float calculateReachabilityProbability(float totalDistance) {
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

    // Create Preference Lists for Clients towards EdgeNodes
    public void createClientPreferenceList(List<CloudProvider> cloudProviders) {
        preferenceList = new ArrayList<>();
        Map<EdgeNode, Float> nodePreference = new HashMap<>();
    
        for (CloudProvider provider : cloudProviders) {
            for (EdgeNode edgeNode : provider.getEdgeNodes()) {
                float nodeDistance = edgeNode.getDistance();
                float nodeExecutionProbability = edgeNode.getExecutionProbability();

                // Calculate satisfaction probability
                float satisfactionProbability = 
                nodeExecutionProbability * calculateReachabilityProbability(Math.abs(position - nodeDistance));

                nodePreference.put(edgeNode, satisfactionProbability);
            }
        }
        
        // Create a list of entries from the nodePreference map
        List<Map.Entry<EdgeNode, Float>> entries = new ArrayList<>(nodePreference.entrySet());
        
        // Sort the entries based on the preference scores (Float values) in descending order
        Collections.sort(entries, new Comparator<Map.Entry<EdgeNode, Float>>() {
            @Override
            public int compare(Map.Entry<EdgeNode, Float> entry1, Map.Entry<EdgeNode, Float> entry2) {
                // Sort in descending order of preference score
                return Float.compare(entry2.getValue(), entry1.getValue());
            }
        });
        
        // Populate the preferenceList with sorted EdgeNode entries
        for (Map.Entry<EdgeNode, Float> entry : entries) {
            preferenceList.add(entry.getKey());
        }
        firstPreferenceList = new ArrayList<>(preferenceList);
        // Print the preference list
        /*
        System.out.println("Client_" + id + " Preference List:");
        for (EdgeNode node : preferenceList) {
            System.out.println("EdgeNode: " + node.getId() + ", Preference Score: " + 
            node.executionProbability() + " * " + calculateReachabilityProbability(Math.abs(position - node.getDistance()))
            + " = " + nodePreference.get(node));
        }
            */
    }

    public void updatePreferenceList() {
        if (preferenceList == null || preferenceList.isEmpty()) {
            return; // Se la preferenceList è vuota o null, non c'è nulla da aggiornare
        }
    
        Map<EdgeNode, Float> nodePreference = new HashMap<>();
    
        // Calcola la nuova satisfaction probability per ogni EdgeNode nella preferenceList
        for (EdgeNode edgeNode : preferenceList) {
            float nodeDistance = edgeNode.getDistance();
            float nodeExecutionProbability = edgeNode.getExecutionProbability();
    
            // Calcola la nuova satisfaction probability
            float satisfactionProbability = nodeExecutionProbability * calculateReachabilityProbability(Math.abs(position - nodeDistance));
    
            nodePreference.put(edgeNode, satisfactionProbability);
        }
    
        // Ordina la preferenceList in base alle nuove satisfaction probabilities
        Collections.sort(preferenceList, new Comparator<EdgeNode>() {
            @Override
            public int compare(EdgeNode node1, EdgeNode node2) {
                float satisfactionProbability1 = nodePreference.get(node1);
                float satisfactionProbability2 = nodePreference.get(node2);
    
                // Ordina in modo decrescente
                return Float.compare(satisfactionProbability2, satisfactionProbability1);
            }
        });
    }
    
    public void recalculatePreferenceList() {  
        Map<EdgeNode, Float> nodePreference = new HashMap<>();
    
        // Calcola la nuova satisfaction probability per ogni EdgeNode nella preferenceList
        for (EdgeNode edgeNode : firstPreferenceList) {
            float nodeDistance = edgeNode.getDistance();
            float nodeExecutionProbability = edgeNode.getExecutionProbability();
    
            // Calcola la nuova satisfaction probability
            float satisfactionProbability = nodeExecutionProbability * calculateReachabilityProbability(Math.abs(position - nodeDistance));
    
            nodePreference.put(edgeNode, satisfactionProbability);
        }
    
        // Ordina la preferenceList in base alle nuove satisfaction probabilities
        Collections.sort(firstPreferenceList, new Comparator<EdgeNode>() {
            @Override
            public int compare(EdgeNode node1, EdgeNode node2) {
                float satisfactionProbability1 = nodePreference.get(node1);
                float satisfactionProbability2 = nodePreference.get(node2);
    
                // Ordina in modo decrescente
                return Float.compare(satisfactionProbability2, satisfactionProbability1);
            }
        });
    }
    
    @Override
    public String toString() {
        return "Client_" + id + ", SLA: " + sla + ", Payment: " + payment + ", Position: " + position;
    }
}
