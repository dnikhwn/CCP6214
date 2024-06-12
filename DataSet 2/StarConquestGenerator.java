import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Star {
    String name;
    int x, y, z;
    int weight;
    int profit;
    List<Connection> connections;

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

    // Function to add a connection to another star
    public void addConnection(Star otherStar, int distance) {
        this.connections.add(new Connection(this, otherStar, distance));
    }
}

class Connection {
    Star star1;
    Star star2;
    int distance;

    public Connection(Star star1, Star star2, int distance) {
        this.star1 = star1;
        this.star2 = star2;
        this.distance = distance;
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

        // Output generated data to a CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter("Dataset_Stars.csv"))) {
            writer.write("Star Name,X,Y,Z,Weight,Profit,Connected Stars (Star Name:Distance)\n");
            for (Star star : stars) {
                writer.print(star.name + "," + star.x + "," + star.y + "," + star.z + "," + star.weight + "," + star.profit + ",");
                for (Connection connection : star.connections) {
                    writer.print(connection.star2.name + ":" + connection.distance + "; ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
}
