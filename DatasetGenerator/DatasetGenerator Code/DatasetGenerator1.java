import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class DatasetGenerator {

    public static void main(String[] args) throws Exception {
        int leaderID = 1201103464;
        int[] dataSetSizes = {100, 1000, 10000, 100000, 500000, 1000000};

        for (int dataSetSize : dataSetSizes) {
            generateDataSet(dataSetSize, leaderID);
        }
    }

    public static void generateDataSet(int dataSetSize, int seed) throws Exception {
        Random random = new Random(seed);
        File file = new File("DataSet_" + dataSetSize + ".txt");
        FileWriter writer = new FileWriter(file);

        for (int i = 0; i < dataSetSize; i++) {
            int number = random.nextInt(6) + 100000; // Generate number within the specified range (100000 - 106999)
            writer.write(number + " ");
        }

        writer.close();
    }
}