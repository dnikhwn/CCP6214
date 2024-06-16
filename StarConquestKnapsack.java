import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StarConquestKnapsack {

  // Method to perform the knapsack algorithm
  public static int calculateMaxProfit(int maxCapacity, int[] weights, int[] values, List<String> starNames, PrintWriter outputWriter) {

    // Check for invalid inputs
    if (weights == null || values == null || weights.length != values.length || maxCapacity < 0)
      throw new IllegalArgumentException("Invalid input parameters");

    final int numStars = weights.length;

    // DP table to store the maximum profit at each capacity
    int[][] dpTable = new int[numStars + 1][maxCapacity + 1];

    // Fill the DP table
    for (int i = 1; i <= numStars; i++) {
      int currentWeight = weights[i - 1];
      int currentValue = values[i - 1];

      for (int cap = 1; cap <= maxCapacity; cap++) {
        // Option 1: Exclude the current star
        dpTable[i][cap] = dpTable[i - 1][cap];

        // Option 2: Take the current star (if it fits)
        if (cap >= currentWeight) {
          dpTable[i][cap] = Math.max(dpTable[i][cap], dpTable[i - 1][cap - currentWeight] + currentValue);
        }
      }
    }

    // Print the DP table once after processing all items
    printDpTable(dpTable, numStars, maxCapacity, outputWriter);

    int remainingCapacity = maxCapacity;
    List<Integer> selectedStars = new ArrayList<>();
    int totalWeight = 0;
    int totalProfit = 0;

    // Backtrack to find the selected stars
    for (int i = numStars; i > 0; i--) {
      if (dpTable[i][remainingCapacity] != dpTable[i - 1][remainingCapacity]) {
        int starIndex = i - 1;
        selectedStars.add(starIndex);
        remainingCapacity -= weights[starIndex];
        totalWeight += weights[starIndex];
        totalProfit += values[starIndex];
      }
    }

    // Print the selected stars with their weights and profits
    outputWriter.println("Selected Stars:");
    outputWriter.println(String.format("%-20s%-10s%-10s", "Star Name", "Weight", "Profit"));
    outputWriter.println(String.format("%-20s%-10s%-10s", "---------", "------", "------"));
    for (int index : selectedStars) {
      outputWriter.println(String.format("%-20s%-10d%-10d", starNames.get(index), weights[index], values[index]));
    }

    // Print the cumulative weight and profit
    outputWriter.println("\nTotal Weight: " + totalWeight);
    outputWriter.println("Total Profit: " + totalProfit);

    // Return the maximum profit
    return dpTable[numStars][maxCapacity];
  }

  // Method to print the DP table
  private static void printDpTable(int[][] dpTable, int numStars, int maxCapacity, PrintWriter outputWriter) {
    outputWriter.println("DP Table:");
    for (int i = 0; i <= numStars; i++) {
      for (int j = 0; j <= maxCapacity; j++) {
        outputWriter.print(dpTable[i][j] + " ");
      }
      outputWriter.println();
    }
    outputWriter.println();
  }

  public static void main(String[] args) {
    String csvFilePath = "Dataset_Stars.csv"; // Path to the CSV file
    String line;
    String delimiter = ",";

    List<Integer> starWeights = new ArrayList<>();
    List<Integer> starValues = new ArrayList<>();
    List<String> starNames = new ArrayList<>();

    // Read data from the CSV file
    try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
      br.readLine(); // Skip header

      while ((line = br.readLine()) != null) {
        String[] starData = line.split(delimiter);
        String starName = starData[0].trim();
        int weight = Integer.parseInt(starData[4].trim());
        int value = Integer.parseInt(starData[5].trim());
        starNames.add(starName);
        starWeights.add(weight);
        starValues.add(value);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    int maxCapacity = 800;
    int[] weights = starWeights.stream().mapToInt(i -> i).toArray();
    int[] values = starValues.stream().mapToInt(i -> i).toArray();

    // Write results to output file
    try (PrintWriter outputWriter = new PrintWriter("knapsack_output.txt")) {
      int maxProfit = calculateMaxProfit(maxCapacity, weights, values, starNames, outputWriter);
      outputWriter.println("Maximum Profit: " + maxProfit);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
