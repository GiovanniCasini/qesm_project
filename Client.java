import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private String id;
    private List<EdgeNode> preferenceList;
    private EdgeNode assignedEdgeNode;
    private float deadline;
    private float cost;

    public Client(String id, List<EdgeNode> preferenceList, float deadline) {
        this.id = id;
        this.preferenceList = preferenceList;
        this.assignedEdgeNode = null;
        this.deadline = deadline;
        this.cost = 1 / deadline;
    }

    public String getId() {
        return id;
    }

    public float getDeadline() {
        return deadline;
    }

    public float getCost() {
        return cost;
    }

    public List<EdgeNode> getPreferenceList() {
        return preferenceList;
    }

    public EdgeNode getAssignedEdgeNode() {
        return assignedEdgeNode;
    }

    public void setAssignedEdgeNode(EdgeNode assignedEdgeNode) {
        this.assignedEdgeNode = assignedEdgeNode;
    }

    public void removeFromPreferenceList(EdgeNode edgeNode) {
        preferenceList.remove(edgeNode);
    }

    public boolean hasPreference() {
        return !preferenceList.isEmpty();
    }

    public void evaluatePreferences(List<CloudProvider> cloudProviders, float weightDistance, float weightProbability) {
        preferenceList = new ArrayList<>();
        Map<EdgeNode, Float> nodePreference = new HashMap<>();

        // Find the max and min distances for normalization
        float maxDistance = Float.MIN_VALUE;
        float minDistance = Float.MAX_VALUE;
        for (CloudProvider provider : cloudProviders) {
            for (EdgeNode edgeNode : provider.getEdgeNodes()) {
                float distance = edgeNode.getDistance();
                if (distance > maxDistance) maxDistance = distance;
                if (distance < minDistance) minDistance = distance;
            }
        }

    
        for (CloudProvider provider : cloudProviders) {
            for (EdgeNode edgeNode : provider.getEdgeNodes()) {
                float distance = edgeNode.getDistance();
                float probability = edgeNode.getCDF();

                // Normalize distance to a scale between 0 and 1
                float normalizedDistance = (distance - minDistance) / (maxDistance - minDistance);
                // Invert normalized distance to make it comparable to probability
                normalizedDistance = 1 - normalizedDistance;

                // Calculate preference score
                float preferenceScore = weightDistance * normalizedDistance + weightProbability * probability;
                nodePreference.put(edgeNode, preferenceScore);
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
    }
}
