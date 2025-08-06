import java.util.*;
import java.io.*;

public class DataBase {
    private ArrayList<PlayerRecord> players = new ArrayList<PlayerRecord>();
    private ArrayList<Integer> topScores = new ArrayList<Integer>();
    
    // Load & store all data from & to player file
    public void loadDB() {
        players.clear(); // Clear existing data before loading
        try (Scanner pinput = new Scanner(new File("resources/player.txt"))) {
            while (pinput.hasNext()) {
                PlayerRecord ply = new PlayerRecord();
                ply.setPlayerID(pinput.nextInt());
                ply.setPlayerName(pinput.next());
                ply.setPlayerScores(pinput.nextInt());
                ply.setPlayerGaoleMedal(pinput.nextInt());

                players.add(ply);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: Player data file not found. Creating new file.");
            createPlayerFile();
        } catch (NoSuchElementException elementException) {
            System.err.println("Error: Player file improperly formed. " + elementException.getMessage());
        } catch (IllegalStateException stateException) {
            System.err.println("Error reading from player file. " + stateException.getMessage());
        }
    }

    public void storeDB() {
        try {
            // Create resources directory if it doesn't exist
            File resourcesDir = new File("resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }
            
            try (Formatter poutput = new Formatter(new File("resources/player.txt"))) {
                for (PlayerRecord player : players) {
                    poutput.format("%d %s %d %d\n", 
                        player.getPlayerID(), 
                        player.getPlayerName(), 
                        player.getPlayerScores(), 
                        player.getPlayerGaoleMedal());
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: Could not write to player data file. " + fileNotFoundException.getMessage());
        } catch (SecurityException securityException) {
            System.err.println("Error: Security permissions denied for writing to player file. " + securityException.getMessage());
        }
    }

    // Load top scores from file
    public void loadTopScores() {
        topScores.clear(); // Clear existing data before loading
        try (Scanner tsinput = new Scanner(new File("resources/top_scores.txt"))) {
            while (tsinput.hasNextInt()) {
                topScores.add(tsinput.nextInt());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: Top scores file not found. Creating new file.");
            createTopScoresFile();
        } catch (NoSuchElementException elementException) {
            System.err.println("Error: Top scores file improperly formed. " + elementException.getMessage());
        } catch (IllegalStateException stateException) {
            System.err.println("Error reading from top scores file. " + stateException.getMessage());
        }
    }

    // Store top scores to file
    public void storeTopScores() {
        try {
            // Create resources directory if it doesn't exist
            File resourcesDir = new File("resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }
            
            try (Formatter tsoutput = new Formatter(new File("resources/top_scores.txt"))) {
                // Sort scores in descending order and keep only top 5
                Collections.sort(topScores, Collections.reverseOrder());
                for (int i = 0; i < Math.min(5, topScores.size()); i++) {
                    tsoutput.format("%d\n", topScores.get(i));
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Error: Could not write to top scores file. " + fileNotFoundException.getMessage());
        } catch (SecurityException securityException) {
            System.err.println("Error: Security permissions denied for writing to top scores file. " + securityException.getMessage());
        }
    }

    // Add a new score to the top scores list
    public void addTopScore(int newScore) {
        topScores.add(newScore);
        // Sort and keep only top 5
        Collections.sort(topScores, Collections.reverseOrder());
        if (topScores.size() > 5) {
            topScores = new ArrayList<>(topScores.subList(0, 5));
        }
    }

    // Display top scores
    public void displayTopScores() {
        Collections.sort(topScores, Collections.reverseOrder());
        System.out.println("=== TOP 5 SCORES ===");
        if (topScores.isEmpty()) {
            System.out.println("No scores recorded yet!");
            return;
        }
        
        for (int i = 0; i < Math.min(5, topScores.size()); i++) {
            System.out.println((i + 1) + ". " + topScores.get(i) + " points");
        }
    }

    // Add or update player in database
    public void addOrUpdatePlayer(int playerID, String playerName, int score, int gaoleMedals) {
        PlayerRecord existingPlayer = findPlayer(playerID);
        
        if (existingPlayer != null) {
            // Update existing player
            existingPlayer.setPlayerName(playerName);
            existingPlayer.setPlayerScores(score);
            existingPlayer.setPlayerGaoleMedal(gaoleMedals);
        } else {
            // Add new player
            PlayerRecord newPlayer = new PlayerRecord();
            newPlayer.setPlayerID(playerID);
            newPlayer.setPlayerName(playerName);
            newPlayer.setPlayerScores(score);
            newPlayer.setPlayerGaoleMedal(gaoleMedals);
            players.add(newPlayer);
        }
    }

    // Find player by ID
    public PlayerRecord findPlayer(int playerID) {
        for (PlayerRecord player : players) {
            if (player.getPlayerID() == playerID) {
                return player;
            }
        }
        return null;
    }

    // Check if player exists
    public boolean playerExists(int playerID) {
        return findPlayer(playerID) != null;
    }

    // Get player data
    public PlayerRecord getPlayerData(int playerID) {
        return findPlayer(playerID);
    }

    public void addScores(int playerID, int scoresToAdd) {
        PlayerRecord playerToUpdate = findPlayer(playerID);

        if (playerToUpdate != null) {
            int currentScores = playerToUpdate.getPlayerScores();
            int newScores = currentScores + scoresToAdd;
            playerToUpdate.setPlayerScores(newScores);

            if (scoresToAdd > 0) {
                System.out.println("Scores added successfully. New total: " + newScores);
            }
        } else {
            System.out.println("Player with ID " + playerID + " not found!");
        }
    }
    
    public void displayTop5scores() {
        // Sort the players based on scores in descending order
        Collections.sort(players, (player1, player2) -> 
            Integer.compare(player2.getPlayerScores(), player1.getPlayerScores()));

        System.out.println("=== TOP 5 PLAYERS ===");
        if (players.isEmpty()) {
            System.out.println("No players found!");
            return;
        }

        // Display the top 5 players
        for (int i = 0; i < Math.min(5, players.size()); i++) {
            PlayerRecord currentPlayer = players.get(i);
            System.out.println((i + 1) + ". " + currentPlayer.getPlayerName() + 
                             " (ID: " + currentPlayer.getPlayerID() + 
                             ") - Score: " + currentPlayer.getPlayerScores() + 
                             " - Medals: " + currentPlayer.getPlayerGaoleMedal());
        }
    }
    
    public void displayPlayerInfo(int targetPlayerID) {
        PlayerRecord player = findPlayer(targetPlayerID);
        
        if (player != null) {
            System.out.printf("=== PLAYER INFO ===\n");
            System.out.printf("Player ID: %d\n", player.getPlayerID());
            System.out.printf("Name: %s\n", player.getPlayerName());
            System.out.printf("Score: %d\n", player.getPlayerScores());
            System.out.printf("Gaole Medals: %d\n", player.getPlayerGaoleMedal());
        } else {
            System.out.println("Player with ID " + targetPlayerID + " not found.");
        }
    }
    
    public void addGaoleMedals(int targetPlayerID, int medalsToAdd) {
        PlayerRecord playerToUpdate = findPlayer(targetPlayerID);

        if (playerToUpdate != null) {
            int currentMedals = playerToUpdate.getPlayerGaoleMedal();
            int newMedals = currentMedals + medalsToAdd;
            playerToUpdate.setPlayerGaoleMedal(newMedals);

            if (medalsToAdd > 0) {
                System.out.printf("You obtained %d Gaole Medals!\n", medalsToAdd);
                System.out.println("Total Gaole Medals: " + newMedals);
            }
        } else {
            System.out.println("Player with ID " + targetPlayerID + " not found!");
        }
    }

    public boolean checkMiracleItem(int targetPlayerID) {
        PlayerRecord playerToUpdate = findPlayer(targetPlayerID);

        if (playerToUpdate != null) {
            int currentMedals = playerToUpdate.getPlayerGaoleMedal();
            
            if (currentMedals >= 160) {
                int newMedals = currentMedals - 160;
                playerToUpdate.setPlayerGaoleMedal(newMedals);
                System.out.println("160 Gaole Medals converted to Miracle Item!");
                System.out.println("Remaining Gaole Medals: " + newMedals);
                return true;
            } else {
                System.out.println("Not enough Gaole Medals! Need 160, have " + currentMedals);
                return false;
            }
        } else {
            System.out.println("Player with ID " + targetPlayerID + " not found!");
            return false;
        }
    }

    // Helper method to create player file if it doesn't exist
    private void createPlayerFile() {
        try {
            File resourcesDir = new File("resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }
            
            File playerFile = new File("resources/player.txt");
            if (!playerFile.exists()) {
                playerFile.createNewFile();
                System.out.println("Created new player.txt file.");
            }
        } catch (IOException e) {
            System.err.println("Error creating player file: " + e.getMessage());
        }
    }

    // Helper method to create top scores file if it doesn't exist
    private void createTopScoresFile() {
        try {
            File resourcesDir = new File("resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }
            
            File topScoresFile = new File("resources/top_scores.txt");
            if (!topScoresFile.exists()) {
                topScoresFile.createNewFile();
                System.out.println("Created new top_scores.txt file.");
            }
        } catch (IOException e) {
            System.err.println("Error creating top scores file: " + e.getMessage());
        }
    }

    // Utility method to get all players (for debugging or admin functions)
    public ArrayList<PlayerRecord> getAllPlayers() {
        return new ArrayList<>(players);
    }

    // Clear all data (for testing purposes)
    public void clearAllData() {
        players.clear();
        topScores.clear();
        System.out.println("All data cleared from memory.");
    }

    // Get total number of players
    public int getPlayerCount() {
        return players.size();
    }

    // Get player rank by score
    public int getPlayerRank(int playerID) {
        Collections.sort(players, (p1, p2) -> 
            Integer.compare(p2.getPlayerScores(), p1.getPlayerScores()));
        
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerID() == playerID) {
                return i + 1; // Rank starts from 1
            }
        }
        return -1; // Player not found
    }
}