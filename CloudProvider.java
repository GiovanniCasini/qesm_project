import java.util.ArrayList;
import java.util.List;

public class CloudProvider {
    private int id;
    private List<EdgeNode> edgeNodes;

    public CloudProvider(int id) {
        this.id = id;
        this.edgeNodes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void addEdgeNode(EdgeNode node) {
        edgeNodes.add(node);
    }

    public List<EdgeNode> getEdgeNodes() {
        return edgeNodes;
    }

    @Override
    public String toString() {
        return "CloudProvider_" + id + "\n" + edgeNodes + "\n";
    }
}
