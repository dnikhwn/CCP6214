import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DatasetGenerator1 {

    // Allowed digits 
    // Leader Student ID = 1201103464
    private static final char[] ALLOWED_DIGITS = {'0', '1', '2', '3', '4', '6'};
    private static final char[] NON_ZERO_DIGITS = {'1', '2', '3', '4', '6'};

    public static void main(String[] args) {
        long seed = 1201103464;

        // Define set sizes
        int[] setSizes = {100, 1000, 5000, 10000, 50000, 100000, 500000, 1000000};

        // Create a new Random object with the current system time as the seed
        Random random = new Random(seed);

        // Generate and store data for each set size
        for (int setSize : setSizes) {
            try {
                generateAndWriteData(setSize, random);
            } catch (IOException e) {
                System.err.println("Error writing dataset of size " + setSize + ": " + e.getMessage());
            }
        }

        System.out.println("Datasets generated successfully!");
    }

    private static void generateAndWriteData(int setSize, Random random) throws IOException {
        String fileName = "Dataset_" + setSize + ".txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        int maxDigits = String.valueOf(setSize).length(); // Maximum number of digits

        for (int i = 0; i < setSize; i++) {
            String randomNumber = generateRandomNumber(random, maxDigits);
            writer.write(randomNumber);
            writer.newLine();
        }

        writer.close();
    }

    private static String generateRandomNumber(Random random, int maxDigits) {
        int numDigits = random.nextInt(maxDigits) + 1; // Number of digits in the number
        StringBuilder number = new StringBuilder(numDigits);

        // Ensure the first digit is not zero
        number.append(NON_ZERO_DIGITS[random.nextInt(NON_ZERO_DIGITS.length)]);

        for (int i = 1; i < numDigits; i++) {
            number.append(ALLOWED_DIGITS[random.nextInt(ALLOWED_DIGITS.length)]);
        }

        return number.toString();
    }
}
