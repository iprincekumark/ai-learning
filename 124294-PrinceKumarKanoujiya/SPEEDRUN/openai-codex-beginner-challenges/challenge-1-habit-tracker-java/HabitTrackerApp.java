import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HabitTrackerApp {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Path DATA_FILE = Path.of("habits.json");
    private static final Map<String, Habit> habits = new LinkedHashMap<>();

    static class Habit {
        String name;
        String frequency;
        Set<LocalDate> completions = new HashSet<>();

        Habit(String name, String frequency) { this.name = name; this.frequency = frequency; }
    }

    public static void main(String[] args) {
        loadHabits();
        while (true) {
            printMenu();
            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1" -> addHabit();
                case "2" -> logCompletion();
                case "3" -> showHabitSummary();
                case "4" -> listHabits();
                case "5" -> printWeeklyReport();
                case "6" -> { saveHabits(); System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid option. Please choose 1-6.");
            }
        }
    }

    static void loadHabits() {
        habits.clear();
        if (!Files.exists(DATA_FILE)) return;
        try {
            String content = Files.readString(DATA_FILE, StandardCharsets.UTF_8);
            Pattern p = Pattern.compile("\\{\\\"name\\\":\\\"(.*?)\\\",\\\"frequency\\\":\\\"(.*?)\\\",\\\"completions\\\":\\[(.*?)\\]}");
            Matcher m = p.matcher(content);
            while (m.find()) {
                Habit h = new Habit(m.group(1), m.group(2));
                String dates = m.group(3).trim();
                if (!dates.isEmpty()) {
                    for (String d : dates.split(",")) {
                        h.completions.add(LocalDate.parse(d.replace("\"", "").trim()));
                    }
                }
                habits.put(h.name.toLowerCase(), h);
            }
        } catch (Exception ex) {
            System.out.println("Could not load habits.json. Starting with empty data.");
        }
    }

    static void saveHabits() {
        StringBuilder sb = new StringBuilder("[\n");
        int i = 0;
        for (Habit h : habits.values()) {
            if (i++ > 0) sb.append(",\n");
            sb.append("  {\"name\":\"").append(h.name).append("\",\"frequency\":\"").append(h.frequency).append("\",\"completions\":[");
            List<LocalDate> sorted = new ArrayList<>(h.completions); Collections.sort(sorted);
            for (int j=0;j<sorted.size();j++) { if (j>0) sb.append(","); sb.append("\"").append(sorted.get(j)).append("\""); }
            sb.append("]}");
        }
        sb.append("\n]\n");
        try { Files.writeString(DATA_FILE, sb.toString(), StandardCharsets.UTF_8); }
        catch (IOException e) { System.out.println("Failed to save habits: " + e.getMessage()); }
    }

    static void addHabit() {
        System.out.print("Enter habit name: ");
        String name = SCANNER.nextLine().trim();
        if (name.isBlank()) { System.out.println("Habit name cannot be blank."); return; }
        if (habits.containsKey(name.toLowerCase())) { System.out.println("Habit already exists."); return; }
        System.out.print("Target frequency (daily/weekly): ");
        String freq = SCANNER.nextLine().trim().toLowerCase();
        if (!freq.equals("daily") && !freq.equals("weekly")) { System.out.println("Frequency must be daily or weekly."); return; }
        habits.put(name.toLowerCase(), new Habit(name, freq)); saveHabits(); System.out.println("Habit added.");
    }

    static void logCompletion() {
        System.out.print("Enter habit name: ");
        Habit h = habits.get(SCANNER.nextLine().trim().toLowerCase());
        if (h == null) { System.out.println("Habit not found."); return; }
        System.out.print("Enter completion date (YYYY-MM-DD): ");
        try {
            LocalDate date = LocalDate.parse(SCANNER.nextLine().trim());
            if (h.completions.add(date)) { saveHabits(); System.out.println("Completion logged."); }
            else System.out.println("Completion already logged for that date.");
        } catch (DateTimeParseException e) { System.out.println("Invalid date format. Use YYYY-MM-DD."); }
    }

    static void showHabitSummary() {
        System.out.print("Enter habit name: ");
        Habit h = habits.get(SCANNER.nextLine().trim().toLowerCase());
        if (h == null) { System.out.println("Habit not found."); return; }
        System.out.printf("Name: %s%nFrequency: %s%nTotal completions: %d%nCurrent streak: %d%n", h.name, h.frequency, h.completions.size(), calculateCurrentStreak(h));
    }

    static void listHabits() {
        if (habits.isEmpty()) { System.out.println("No habits found."); return; }
        for (Habit h : habits.values()) {
            System.out.printf("- %s (%s), completions=%d, streak=%d%n", h.name, h.frequency, h.completions.size(), calculateCurrentStreak(h));
        }
    }

    static int calculateCurrentStreak(Habit habit) {
        if (habit.completions.isEmpty()) return 0;
        int streak = 0; LocalDate cursor = LocalDate.now();
        if (!habit.completions.contains(cursor)) cursor = cursor.minusDays(1);
        while (habit.completions.contains(cursor)) { streak++; cursor = cursor.minusDays(1); }
        return habit.frequency.equals("weekly") ? (int)Math.ceil(streak / 7.0) : streak;
    }

    static void printWeeklyReport() {
        LocalDate today = LocalDate.now(); LocalDate weekStart = today.minusDays(6);
        System.out.println("Weekly Report (last 7 days):");
        for (Habit h : habits.values()) {
            long count = h.completions.stream().filter(d -> !d.isBefore(weekStart) && !d.isAfter(today)).count();
            boolean met = h.frequency.equals("daily") ? count >= 7 : count >= 1;
            System.out.printf("- %s: %d completions, target met: %s%n", h.name, count, met ? "YES" : "NO");
        }
    }

    static void printMenu() {
        System.out.println("\n=== Habit Tracker ===");
        System.out.println("1. Add Habit");
        System.out.println("2. Log Completion");
        System.out.println("3. Show Habit Summary");
        System.out.println("4. List Habits");
        System.out.println("5. Weekly Report");
        System.out.println("6. Save and Exit");
        System.out.print("Choose an option: ");
    }
}
