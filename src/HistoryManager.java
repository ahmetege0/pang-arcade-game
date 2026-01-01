
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryManager {
    private static final String HISTORY_FOLDER = "histories/";

    static {
        File dir = new File(HISTORY_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void appendToHistory(String username, int level, int score) {
        String filename = HISTORY_FOLDER + username + "_history.txt";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String entry = timestamp + ",Level:" + (level + 1) + ",Score:" + score;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(entry);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readHistory(String username) {
        String filename = HISTORY_FOLDER + username + "_history.txt";
        List<String> lines = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) {
            return lines;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static Map<String, Integer> getAllMaxScores() {
        Map<String, Integer> userMax = new HashMap<>();
        File dir = new File(HISTORY_FOLDER);
        File[] files = dir.listFiles((d, name) -> name.endsWith("_history.txt"));
        if (files == null) {
            return userMax;
        }
        for (File f : files) {
            String filename = f.getName(); // örn: "ali_history.txt"
            String username = filename.substring(0, filename.indexOf("_history.txt"));
            int maxScore = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    for (String p : parts) {
                        if (p.startsWith("Score:")) {
                            int sc = Integer.parseInt(p.substring("Score:".length()));
                            if (sc > maxScore) {
                                maxScore = sc;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            userMax.put(username, maxScore);
        }
        return userMax;
    }

    public static List<String> getTopMaxScores(int topN) {
        Map<String, Integer> allMax = getAllMaxScores();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(allMax.entrySet());
 
        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<String> result = new ArrayList<>();
        int count = Math.min(topN, entryList.size());
        for (int i = 0; i < count; i++) {
            Map.Entry<String, Integer> e = entryList.get(i);
            result.add((i+1) + ". " + e.getKey() + " - " + e.getValue());
        }
        return result;
    }
}