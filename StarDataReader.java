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
import java.util.PriorityQueue;
import java.util.Random;
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

public class StarDataReader {
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
                List<Connection> connections = new ArrayList<>();
                // Parse the connected stars and their distances
                System.out.println("Name: " + name + ", X: " + x + ", Y: " + y + ", Z: " + z + ", Weight: " + weight
                        + ", Profit: " + profit);
                for (String connectedStar : connectedStars) {
                    String[] parts = connectedStar.split(":");
                    String connectedStarName = parts[0].trim();
                    String distanceStr = parts[1].trim().replaceAll("[^0-9]", ""); // Remove non-numeric characters
                    int distance = Integer.parseInt(distanceStr);
                    System.out.println("Connected Star: " + connectedStarName + ", Distance: " + distance);
                    connections.add(new Connection(null, new Star(connectedStarName, 0, 0, 0, 0, 0), distance));
                }
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

        // Run Dijkstra's Algorithm
        List<Map<Star, Integer>> dijkstraResult = new ArrayList<>();
        for (Star star : stars) {
            Map<Star, Integer> distances = dijkstra(star);
            dijkstraResult.add(distances);
        }
        System.out.println("Dijkstra's Result:");
        for (int i = 0; i < dijkstraResult.size(); i++) {
            Map<Star, Integer> distances = dijkstraResult.get(i);
            System.out.println("From Star " + (char) ('A' + i) + ":");
            for (Map.Entry<Star, Integer> entry : distances.entrySet()) {
                System.out.println("  to " + entry.getKey().name + ", Distance: " + entry.getValue());
            }
        }
        printAndSaveDijkstraResult("Dijkstra_Result.txt", dijkstraResult);

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

    // Function to connect stars with routes
    public static void connectStars(List<Star> stars, Random random) {
        for (Star star : stars) {
            int connections = 0;
            while (connections < 3) {
                Star otherStar = stars.get(random.nextInt(stars.size()));
                if (otherStar != star && !areConnected(star, otherStar)) {
                    int distance = star.calculateDistance(otherStar);
                    star.addConnection(otherStar, distance);
                    otherStar.addConnection(star, distance); // Ensure bidirectional connection
                    connections++;
                }
            }
        }
    }

    // Function to check if two stars are already connected
    public static boolean areConnected(Star star1, Star star2) {
        for (Connection connection : star1.connections) {
            if (connection.star2 == star2) {
                return true;
            }
        }
        return false;
    }

    // Dijkstra's Algorithm to find the shortest path from a source star
    public static Map<Star, Integer> dijkstra(Star source) {
        PriorityQueue<StarDistance> queue = new PriorityQueue<>(Comparator.comparingInt(sd -> sd.distance));
        Map<Star, Integer> distances = new HashMap<>();
        Set<Star> visited = new HashSet<>();

        // Initialize distances and add the source to the queue
        distances.put(source, 0);
        queue.add(new StarDistance(source, 0));

        while (!queue.isEmpty()) {
            StarDistance currentSD = queue.poll();
            Star current = currentSD.star;
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // Process each neighbor of the current star
            for (Connection connection : current.connections) {
                Star neighbor = connection.star2;
                int newDistance = distances.get(current) + connection.distance;
                Integer currentDistance = distances.get(neighbor);
                if (currentDistance == null || newDistance < currentDistance) {
                    distances.put(neighbor, newDistance);
                    queue.add(new StarDistance(neighbor, newDistance));
                }
            }
        }
        return distances;
    }

    static class StarDistance {
        Star star;
        int distance;

        StarDistance(Star star, int distance) {
            this.star = star;
            this.distance = distance;
        }
    }

    // Kruskal's Algorithm to find the Minimum Spanning Tree (MST)
    public static List<Connection> kruskal(List<Star> stars) {
        List<Connection> result = new ArrayList<>();
        List<Connection> connections = new ArrayList<>();
        Set<Star> visited = new HashSet<>();
        int totalCost = 0;

        // Collect all connections
        for (Star star : stars) {
            visited.add(star);
            for (Connection connection : star.connections) {
                if (!visited.contains(connection.star2)) {
                    connections.add(connection);
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
                totalCost += connection.distance;
                disjointSet.union(star1, star2);
            }
        }

        System.out.println("Following are the edges of the constructed MST:");
        for (Connection connection : result) {
            System.out.println(connection.star1.name + " -- "
                    + connection.star2.name + " == "
                    + connection.distance);
        }
        System.out.println("Total cost of MST: " + totalCost);

        return result;
    }

    // Function to print and save Kruskal's results
    public static void printAndSaveKruskalResult(String fileName, List<Connection> result, int totalCost) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Connection connection : result) {
                writer.println(
                        connection.star1.name + " -> " + connection.star2.name + ", Distance: " + connection.distance);
            }
            writer.println("Total cost of MST: " + totalCost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to print and save Dijkstra's results
    public static void printAndSaveDijkstraResult(String fileName, List<Map<Star, Integer>> result) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (int i = 0; i < result.size(); i++) {
                Map<Star, Integer> distances = result.get(i);
                writer.println("From Star " + (char) ('A' + i) + ":");
                for (Map.Entry<Star, Integer> entry : distances.entrySet()) {
                    writer.println("  to " + entry.getKey().name + ", Distance: " + entry.getValue());
                }
                writer.println();
            }
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