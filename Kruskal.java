import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Star {
    String name;
    int x, y, z;
    int weight;
    int profit;
    List<Connection> connections;

    // Constructor to initialize a Star object
    public Star(String name, int x, int y, int z, int weight, int profit) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.weight = weight;
        this.profit = profit;
        this.connections = new ArrayList<>();
    }

    // Function to calculate distance between two stars
    public int calculateDistance(Star otherStar) {
        return (int) Math.sqrt(Math.pow(this.x - otherStar.x, 2) +
                Math.pow(this.y - otherStar.y, 2) +
                Math.pow(this.z - otherStar.z, 2));
    }

    // Function to add a connection to another star with a specified distance
    public void addConnection(Star otherStar, int distance) {
        if (distance <= 0) { // If distance is not provided or invalid, calculate it
            distance = this.calculateDistance(otherStar);
        }
        this.connections.add(new Connection(this, otherStar, distance));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Star ").append(name).append(": (X=").append(x).append(", Y=").append(y).append(", Z=").append(z)
                .append(", Weight=").append(weight).append(", Profit=").append(profit).append(")\n");
        sb.append("Connected Stars:\n");
        for (Connection connection : connections) {
            sb.append("  ").append(connection.star2.name).append(", Distance: ").append(connection.distance)
                    .append("\n");
        }
        return sb.toString();
    }
}

class Connection {
    Star star1;
    Star star2;
    int distance;

    // Constructor to initialize a Connection object
    public Connection(Star star1, Star star2, int distance) {
        this.star1 = star1;
        this.star2 = star2;
        this.distance = distance;
    }
}

public class Kruskal {
    public static void main(String[] args) {
        String csvFile = "Dataset_Stars.csv"; // Path to the CSV file containing star data
        String line;
        String cvsSplitBy = ",";
        List<Star> stars = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                String name = data[0].trim();
                int x = Integer.parseInt(data[1].trim());
                int y = Integer.parseInt(data[2].trim());
                int z = Integer.parseInt(data[3].trim());
                int weight = Integer.parseInt(data[4].trim());
                int profit = Integer.parseInt(data[5].trim());
                String[] connectedStars = data[6].trim().split("; ");
                // Parse the connected stars and their distances
                System.out.println("Name: " + name + ", X: " + x + ", Y: " + y + ", Z: " + z + ", Weight: " + weight
                        + ", Profit: " + profit);
                Star star = new Star(name, x, y, z, weight, profit);
                for (String connectedStar : connectedStars) {
                    String[] parts = connectedStar.split(":");
                    String connectedStarName = parts[0].trim();
                    String distanceStr = parts[1].trim().replaceAll("[^0-9]", ""); // Remove non-numeric characters
                    int distance = Integer.parseInt(distanceStr);
                    System.out.println("Connected Star: " + connectedStarName + ", Distance: " + distance);
                    star.addConnection(new Star(connectedStarName, 0, 0, 0, 0, 0), distance);
                }
                stars.add(star);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Run Kruskal's Algorithm
        List<Connection> kruskalResult = kruskal(stars);
        // Calculate total cost
        int totalCost = 0;
        for (Connection connection : kruskalResult) {
            totalCost += connection.distance;
        }

        // Print and save Kruskal's result
        printAndSaveKruskalResult("Kruskal_Result.txt", kruskalResult, totalCost);

    }

    public static List<Connection> kruskal(List<Star> stars) {
        List<Connection> result = new ArrayList<>();
        List<Connection> connections = new ArrayList<>();
        Set<String> addedEdges = new HashSet<>();

        // Collect all connections
        for (Star star : stars) {
            for (Connection connection : star.connections) {
                String edge1 = star.name + " -- " + connection.star2.name;
                String edge2 = connection.star2.name + " -- " + star.name;
                if (!addedEdges.contains(edge1) && !addedEdges.contains(edge2)) {
                    connections.add(connection);
                    addedEdges.add(edge1);
                    addedEdges.add(edge2);
                }
            }
        }

        // Sort connections by distance
        connections.sort(Comparator.comparingInt(c -> c.distance));

        DisjointSet disjointSet = new DisjointSet(stars);

        // Process connections in order
        for (Connection connection : connections) {
            Star star1 = connection.star1;
            Star star2 = connection.star2;
            if (disjointSet.find(star1) != disjointSet.find(star2)) {
                result.add(connection);
                disjointSet.union(star1, star2);
            }
        }

        int totalCost = result.stream().mapToInt(c -> c.distance).sum();

        System.out.println("Following are the edges of the constructed MST:");
        for (Connection connection : result) {
            System.out.println(connection.star1.name + " -- " + connection.star2.name + " == " + connection.distance);
        }
        System.out.println("Total cost of MST: " + totalCost);

        return result;
    }

    public static void printAndSaveKruskalResult(String fileName, List<Connection> result, int totalCost) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Connection connection : result) {
                writer.println(connection.star1.name + " -> " + connection.star2.name + ", Distance: " + connection.distance);
            }
            writer.println("Total cost of MST: " + totalCost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class DisjointSet {
        Map<Star, Star> parent;

        public DisjointSet(List<Star> stars) {
            parent = new HashMap<>();
            for (Star star : stars) {
                parent.put(star, star);
            }
        }

        public Star find(Star star) {
            if (parent.get(star) == star) {
                return star;
            }
            Star root = find(parent.get(star));
            parent.put(star, root);
            return root;
        }

        public void union(Star star1, Star star2) {
            Star parent1 = find(star1);
            Star parent2 = find(star2);
            parent.put(parent1, parent2);
        }
    }
}