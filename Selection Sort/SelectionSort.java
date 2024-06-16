import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SelectionSort {

    // Method to perform selection sort
    public static void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            // Swap the found minimum element with the first element
            int temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }

    public static void main(String[] args) {
        // File paths for input and output
        String inputFilename = "C:/Users/zzyan/Desktop/CCP6214-1/Dataset_100.txt";
        String outputFilename = "C:/Users/zzyan/Desktop/CCP6214-1/Selection Sort/Selection_Sorted_Dataset_100.txt";

        try {
            // Load the dataset from the input file
            List<String> lines = Files.readAllLines(Paths.get(inputFilename));
            int[] dataset = lines.stream().mapToInt(Integer::parseInt).toArray();

            // Start the timer to measure the sorting duration
            long startTime = System.currentTimeMillis();

            // Perform selection sort on the dataset
            selectionSort(dataset);

            // Stop the timer and calculate the elapsed time
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            // Display the sorted dataset and the time taken for sorting
            System.out.println("Sorted Dataset: " + Arrays.toString(dataset));
            System.out.println("Sorting Time: " + elapsedTime + " milliseconds");

            // Save the sorted dataset to the output file
            List<String> sortedLines = Arrays.stream(dataset)
                                             .mapToObj(String::valueOf)
                                             .collect(Collectors.toList());
            Files.write(Paths.get(outputFilename), sortedLines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
