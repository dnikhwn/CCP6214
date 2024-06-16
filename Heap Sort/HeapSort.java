import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HeapSort {

    // Method to perform heap sort on the array
    public static void heapSort(int[] array) {
        int n = array.length;

        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i);
        }

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;

            // Heapify the reduced heap
            heapify(array, i, 0);
        }
    }

    // To heapify a subtree rooted with node i which is an index in array[]
    private static void heapify(int[] array, int n, int i) {
        int largest = i; // Initialize largest as root
        int left = 2 * i + 1; // left = 2*i + 1
        int right = 2 * i + 2; // right = 2*i + 2

        // If left child is larger than root
        if (left < n && array[left] > array[largest]) {
            largest = left;
        }

        // If right child is larger than largest so far
        if (right < n && array[right] > array[largest]) {
            largest = right;
        }

        // If largest is not root
        if (largest != i) {
            int swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;

            // Recursively heapify the affected sub-tree
            heapify(array, n, largest);
        }
    }

    public static void main(String[] args) {
        // File paths for input and output
        String inputFilePath = "C:/Users/zzyan/Desktop/CCP6214-main/DataSet 1/Dataset_100.txt";
        String outputFilePath = "C:/Users/zzyan/Desktop/CCP6214-main/Heap Sort/Heap_Sorted_Dataset_100.txt";


        try {
            // Load the dataset from the input file
            List<String> lines = Files.readAllLines(Paths.get(inputFilePath));
            int[] data = lines.stream().mapToInt(Integer::parseInt).toArray();

            // Start the timer to measure the sorting duration
            long startTime = System.currentTimeMillis();

            // Perform heap sort on the dataset
            heapSort(data);

            // Stop the timer and calculate the elapsed time
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Display the sorted dataset and the time taken for sorting
            System.out.println("Sorted Data: " + Arrays.toString(data));
            System.out.println("Time taken for sorting: " + duration + " milliseconds");

            // Save the sorted dataset to the output file
            List<String> sortedLines = Arrays.stream(data)
                                             .mapToObj(String::valueOf)
                                             .collect(Collectors.toList());
            Files.write(Paths.get(outputFilePath), sortedLines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
