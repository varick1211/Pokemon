import java.util.*;


public class ScoreManager {
    private ArrayList<PlayerScore> scores;
    private final int MAX_SCORES = 5; 
    
    public ScoreManager() {
        this.scores = new ArrayList<>();
    }
    
   
    
    public void addScore(String playerName, int score) {
        PlayerScore newScore = new PlayerScore(playerName, score);
        scores.add(newScore);
        
        // Sort scores (highest first)
        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        // Keep only top scores
        if (scores.size() > MAX_SCORES) {
            scores = new ArrayList<>(scores.subList(0, MAX_SCORES));
        }
    }
    
   
    
    public void displayHighScores() {
        System.out.println("\n=== HIGH SCORES ===");
        
        if (scores.isEmpty()) {
            System.out.println("No scores yet!");
        } else {
            for (int i = 0; i < scores.size(); i++) {
                PlayerScore score = scores.get(i);
                System.out.printf("%d. %s - %d points\n", 
                                i + 1, score.getPlayerName(), score.getScore());
            }
        }
    }
    
    
    
    public boolean isHighScore(int score) {
        if (scores.size() < MAX_SCORES) {
            return true; 
        }
        
        
        return score > scores.get(scores.size() - 1).getScore();
    }
    

    
    public int getHighScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.get(0).getScore();
    }
    
  
    
    public int getScoreCount() {
        return scores.size();
    }
}