package assign1;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class assign1_3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== UNIQUE WORD SORTER ===");
        System.out.println("Enter words separated by spaces:");
        String input = scanner.nextLine();
        
        if (input.trim().isEmpty()) {
            System.out.println("No words entered!");
            scanner.close();
            return;
        }
        
        String[] words = input.split("\\s+");
        
        // Remove duplicates while preserving order initially
        Set<String> uniqueWords = new LinkedHashSet<>(Arrays.asList(words));
        String[] uniqueArray = uniqueWords.toArray(new String[0]);
        
        // Sort alphabetically (case-insensitive)
        Arrays.sort(uniqueArray, String.CASE_INSENSITIVE_ORDER);
        
        System.out.println("\nUnique words sorted alphabetically:");
        for (int i = 0; i < uniqueArray.length; i++) {
            System.out.printf("%2d. %s\n", (i + 1), uniqueArray[i]);
        }
        
        System.out.println("\nTotal unique words: " + uniqueArray.length);
        
        scanner.close();
    }
}