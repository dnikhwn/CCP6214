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
import java.util.Set;

class D_Star {
    String name;
    int x, y, z;
    int weight;
    int profit;
    Map<String, Integer> connectedStars; // Stores connected star names and their distances

    // Constructor to initialize a D_Star object
    public D_Star(String name, int x, int y, int z, int weight, int profit) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.weight = weight;
        this.profit = profit;
        this.connectedStars = new HashMap<>();
    }

    // Function to parse connections from a CSV-formatted string
    public void parseConnections(String connectionsStr) {
        String[] connectedStars = connectionsStr.split("; ");
        for (String connectedStar : connectedStars) {
            String[] parts = connectedStar.split(":");
            String connectedStarName = parts[0].trim();
            String distanceStr = parts[1].trim().replaceAll("[^0-9]", ""); // Remove non-numeric characters
            int distance = Integer.parseInt(distanceStr);
            this.connectedStars.put(connectedStarName, distance);
        }
    }

    // Function to calculate distance between two stars
    public int calculateDistance(D_Star otherStar) {
        return (int) Math.sqrt(Math.pow(this.x - otherStar.x, 2) +
                Math.pow(this.y - otherStar.y, 2) +
                Math.pow(this.z - otherStar.z, 2));
    }

    // Function to add a connection to another star with a specified distance
    public void addConnection(String otherStarName, int distance) {
        this.connectedStars.put(otherStarName, distance);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("D_Star ").append(name).append(": (X=").append(x).append(", Y=").append(y).append(", Z=").append(z)
                .append(", Weight=").append(weight).append(", Profit=").append(profit).append(")\n");
        sb.append("Connected Stars:\n");
        for (Map.Entry<String, Integer> entry : connectedStars.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(", Distance: ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}


public class Dijkstra {
    public static void main(String[] args) {
        String csvFile = "Dataset_Stars.csv"; // Path to the CSV file containing star data
        List<D_Star> stars = readStarsFromCSV(csvFile);

        // Find D_Star A
        D_Star starA = null;
        for (D_Star star : stars) {
            if (star.name.equals("Star A")) {
                starA = star;
                break;
            }
        }

        if (starA != null) {
            // Run Dijkstra's Algorithm for D_Star A
            Map<D_Star, DijkstraResult> distances = dijkstra(starA, stars);

            // Print and save Dijkstra's results for D_Star A only
            printAndSaveDijkstraResult("Dijkstra_Result.txt", starA, distances);
        } else {
            System.out.println("Star A not found in the dataset.");
        }
    }

    // Function to read stars from CSV
    public static List<D_Star> readStarsFromCSV(String csvFile) {
        List<D_Star> stars = new ArrayList<>();
        String line;
        String cvsSplitBy = ",";

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
                String connectedStarsStr = data[6].trim();

                D_Star star = new D_Star(name, x, y, z, weight, profit);
                star.parseConnections(connectedStarsStr);

                stars.add(star);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stars;
    }

    // Dijkstra's Algorithm to find the shortest path from a source star
    public static Map<D_Star, DijkstraResult> dijkstra(D_Star source, List<D_Star> stars) {
        PriorityQueue<StarDistance> queue = new PriorityQueue<>(Comparator.comparingInt(sd -> sd.distance));
        Map<D_Star, Integer> distances = new HashMap<>();
        Map<D_Star, List<D_Star>> shortestPaths = new HashMap<>();
        Set<D_Star> visited = new HashSet<>();

        // Initialize distances and add the source to the queue
        distances.put(source, 0);
        shortestPaths.put(source, new ArrayList<>());
        queue.add(new StarDistance(source, 0));

        while (!queue.isEmpty()) {
            StarDistance currentSD = queue.poll();
            D_Star current = currentSD.star;
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            // Process each neighbor of the current star
            for (Map.Entry<String, Integer> entry : current.connectedStars.entrySet()) {
                String connectedStarName = entry.getKey();
                int distance = entry.getValue();

                // Find the connected star object
                D_Star neighbor = findStarByName(stars, connectedStarName);
                if (neighbor == null) {
                    continue; // Skip if the neighbor star is not found
                }

                int newDistance = distances.get(current) + distance;

                // Update distance if shorter path is found
                Integer currentDistance = distances.get(neighbor);
                if (currentDistance == null || newDistance < currentDistance) {
                    distances.put(neighbor, newDistance);

                    // Update shortest path
                    List<D_Star> currentShortestPath = new ArrayList<>(shortestPaths.get(current));
                    currentShortestPath.add(current);
                    shortestPaths.put(neighbor, currentShortestPath);

                    queue.add(new StarDistance(neighbor, newDistance));
                }
            }
        }

        // Convert distances and shortestPaths to DijkstraResult format
        Map<D_Star, DijkstraResult> results = new HashMap<>();
        for (Map.Entry<D_Star, Integer> entry : distances.entrySet()) {
            D_Star star = entry.getKey();
            int distance = entry.getValue();
            List<D_Star> path = shortestPaths.get(star);
            results.put(star, new DijkstraResult(distance, path));
        }
        return results;
    }

    // Function to find a star by its name
    public static D_Star findStarByName(List<D_Star> stars, String name) {
        for (D_Star star : stars) {
            if (star.name.equals(name)) {
                return star;
            }
        }
        return null;
    }

    // Function to print and save Dijkstra's results for D_Star A only
    public static void printAndSaveDijkstraResult(String fileName, D_Star starA, Map<D_Star, DijkstraResult> result) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Map.Entry<D_Star, DijkstraResult> entry : result.entrySet()) {
                D_Star destination = entry.getKey();
                DijkstraResult dijkstraResult = entry.getValue();

                writer.println("Shortest Distance from " + starA.name + " to " + destination.name + ": "
                        + dijkstraResult.distance);
                writer.print("Shortest Path from " + starA.name + " to " + destination.name + ": ");

                List<D_Star> path = dijkstraResult.path;
                if (path.isEmpty()) {
                    writer.println("None");
                } else {
                    for (int i = 0; i < path.size(); i++) {
                        writer.print(path.get(i).name);
                        if (i < path.size() - 1) {
                            writer.print(" -> ");
                        }
                    }
                    writer.println(" -> " + destination.name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class StarDistance {
        D_Star star;
        int distance;

        StarDistance(D_Star star, int distance) {
            this.star = star;
            this.distance = distance;
        }
    }

    static class DijkstraResult {
        int distance;
        List<D_Star> path;

        DijkstraResult(int distance, List<D_Star> path) {
            this.distance = distance;
            this.path = path;
        }
    }
}