package assign1;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class assign1 {
    
    public static void main(String[] args) {
        // Example numbers - you can modify this array
        double[] numbers = {5, 2, 8, 2, 6, 3, 9, 2, 8, 4};
        
        System.out.println("Numbers: " + Arrays.toString(numbers));
        System.out.printf("Mean: %.2f%n", calculateMean(numbers));
        System.out.printf("Median: %.2f%n", calculateMedian(numbers));
        System.out.println("Mode: " + calculateMode(numbers));
    }
    
    public static double calculateMean(double[] numbers) {
        double sum = 0;
        for (double num : numbers) {
            sum += num;
        }
        return sum / numbers.length;
    }
    
    public static double calculateMedian(double[] numbers) {
        double[] sorted = numbers.clone();
        Arrays.sort(sorted);
        
        int middle = sorted.length / 2;
        
        if (sorted.length % 2 == 0) {
            return (sorted[middle - 1] + sorted[middle]) / 2.0;
        } else {
            return sorted[middle];
        }
    }
    
    public static String calculateMode(double[] numbers) {
        Map<Double, Integer> frequencyMap = new HashMap<>();
        
        for (double num : numbers) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        int maxFrequency = 0;
        for (int frequency : frequencyMap.values()) {
            if (frequency > maxFrequency) {
                maxFrequency = frequency;
            }
        }
        
        StringBuilder modes = new StringBuilder();
        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() == maxFrequency) {
                if (modes.length() > 0) {
                    modes.append(", ");
                }
                modes.append(entry.getKey());
            }
        }
        
        if (maxFrequency == 1 && numbers.length > 1) {
            return "No mode (all values are unique)";
        }
        
        return modes.toString() + " (appears " + maxFrequency + " times)";
    }
}