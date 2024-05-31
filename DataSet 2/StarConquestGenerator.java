import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Star {
    String name;
    int x, y, z;
    int weight;
    int profit;

    public Star(String name, int x, int y, int z, int weight, int profit) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.weight = weight;
        this.profit = profit;
    }

    // Function to calculate distance between two stars
    public int calculateDistance(Star otherStar) {
        return (int) Math.sqrt(Math.pow(this.x - otherStar.x, 2) +
                         Math.pow(this.y - otherStar.y, 2) +
                         Math.pow(this.z - otherStar.z, 2));
    }
}

public class StarConquestGenerator {
    // Generate data for the star conquest game
    public static void main(String[] args) {
        // Use the sum of group member IDs as the random seed
        long seed = 1201103071 + 1201103665 + 1201103467;
        Random random = new Random(seed);

        // Generate 20 stars
        List<Star> stars = generateStars(random);

        // Connect stars with routes
        connectStars(stars, random);

        // Display generated data
        for (Star star : stars) {
            System.out.println("Star: " + star.name + ", Coordinates: (" + star.x + ", " + star.y + ", " + star.z +
                    "), Weight: " + star.weight + ", Profit: " + star.profit);
        }
    }

    // Function to generate 20 stars
    public static List<Star> generateStars(Random random) {
        List<Star> stars = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String name = "Star " + (char) ('A' + i);
            int x = random.nextInt(100); // Can change range as needed
            int y = random.nextInt(100); // Can change range as needed
            int z = random.nextInt(100); // Can change range as needed
            int weight = random.nextInt(100); // Can change range as needed
            int profit = random.nextInt(100); // Can change range as needed
            stars.add(new Star(name, x, y, z, weight, profit));
        }
        return stars;
    }

    // Function to connect stars with routes
    public static void connectStars(List<Star> stars, Random random) {
        for (Star star : stars) {
            int connections = 0;
            while (connections < 3) {
                Star otherStar = stars.get(random.nextInt(stars.size()));
                if (otherStar != star && !areConnected(star, otherStar)) {
                    int distance = star.calculateDistance(otherStar);
                    // Create route/connection between stars (not implemented in this code)
                    // You can store the connections in a separate data structure or class
                    System.out.println("Connected " + star.name + " to " + otherStar.name + " with distance " + distance);
                    connections++;
                }
            }
        }
    }

    // Function to check if two stars are already connected
    public static boolean areConnected(Star star1, Star star2) {
        // Implement this function based on your data structure for connections/routes
        return false; // Placeholder
    }
}

