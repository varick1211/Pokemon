import java.util.*;

public class Player {
    private int playerID;
    private String name;
    private int score;
    private int wins;
    private int losses;
    private ArrayList<Pokemon> pokemonCollection;
    private HashMap<String, Integer> inventory;
    private int coins;
    private int gaoleMedals;
    private DataBase database;

    // Constructor for new player
    public Player(String name) {
        this.playerID = generatePlayerID();
        this.name = name;
        this.score = 0;
        this.wins = 0;
        this.losses = 0;
        this.pokemonCollection = new ArrayList<>();
        this.inventory = new HashMap<>();
        this.coins = 100;
        this.gaoleMedals = 0;
        this.database = new DataBase();
        
        initializePokeBalls();
    }
    
    // Constructor for loading existing player
    public Player(int playerID, String name, int score, int gaoleMedals) {
        this.playerID = playerID;
        this.name = name;
        this.score = score;
        this.wins = 0;
        this.losses = 0;
        this.pokemonCollection = new ArrayList<>();
        this.inventory = new HashMap<>();
        this.coins = 100;
        this.gaoleMedals = gaoleMedals;
        this.database = new DataBase();
        
        initializePokeBalls();
    }

    private int generatePlayerID() {
        return (int)(Math.random() * 10000) + 1000; // Generate random 4-digit ID
    }

    private void initializePokeBalls() {
        inventory.put("Poke Ball", 10);
        inventory.put("Great Ball", 5);
        inventory.put("Ultra Ball", 2);
        inventory.put("Master Ball", 1);
    }

    // Database operations
    public void saveToDatabase() {
        try {
            database.loadDB(); // Load existing data first
            database.addOrUpdatePlayer(playerID, name, score, gaoleMedals);
            database.storeDB(); // Save all data
            
            // Add current score to top scores
            database.loadTopScores();
            database.addTopScore(score);
            database.storeTopScores();
            
            System.out.println("Player data saved successfully!");
        } catch (Exception e) {
            System.err.println("Error saving player data: " + e.getMessage());
        }
    }
    
    public boolean loadFromDatabase(int targetPlayerID) {
        try {
            database.loadDB();
            PlayerRecord playerData = database.getPlayerData(targetPlayerID);
            
            if (playerData != null) {
                this.playerID = playerData.getPlayerID();
                this.name = playerData.getPlayerName();
                this.score = playerData.getPlayerScores();
                this.gaoleMedals = playerData.getPlayerGaoleMedal();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error loading player data: " + e.getMessage());
            return false;
        }
    }
    
    public void updateDatabaseScore(int newScore) {
        try {
            database.loadDB();
            int scoreDifference = newScore - this.score;
            if (scoreDifference > 0) {
                database.addScores(playerID, scoreDifference);
                database.storeDB();
                
                // Update top scores
                database.loadTopScores();
                database.addTopScore(newScore);
                database.storeTopScores();
            }
            this.score = newScore;
        } catch (Exception e) {
            System.err.println("Error updating score in database: " + e.getMessage());
        }
    }
    
    public void updateDatabaseMedals(int medalsToAdd) {
        try {
            database.loadDB();
            database.addGaoleMedals(playerID, medalsToAdd);
            database.storeDB();
            this.gaoleMedals += medalsToAdd;
        } catch (Exception e) {
            System.err.println("Error updating medals in database: " + e.getMessage());
        }
    }
    
    public boolean checkAndUseMiracleItem() {
        try {
            database.loadDB();
            boolean hasItem = database.checkMiracleItem(playerID);
            database.storeDB();
            if (hasItem) {
                this.gaoleMedals -= 160;
            }
            return hasItem;
        } catch (Exception e) {
            System.err.println("Error checking miracle item: " + e.getMessage());
            return false;
        }
    }
    
    public void displayTopScores() {
        database.loadTopScores();
        database.displayTopScores();
    }
    
    public void displayTop5Players() {
        database.loadDB();
        database.displayTop5scores();
    }

    // Pokemon management
    public boolean addPokemon(Pokemon pokemon) {
        if (pokemonCollection.size() < 30) {
            pokemonCollection.add(pokemon);
            return true;
        }
        return false;
    }

    public Pokemon getPokemon(int index) {
        if (index >= 0 && index < pokemonCollection.size()) {
            return pokemonCollection.get(index);
        }
        return null;
    }

    public int getCollectionSize() {
        return pokemonCollection.size();
    }

    // PokeBall management
    public boolean hasPokeBall(String ballType) {
        return inventory.getOrDefault(ballType, 0) > 0;
    }

    public boolean usePokeBall(String ballType) {
        int count = inventory.getOrDefault(ballType, 0);
        if (count > 0) {
            inventory.put(ballType, count - 1);
            return true;
        }
        return false;
    }

    public int getPokeBallCount(String ballType) {
        return inventory.getOrDefault(ballType, 0);
    }

    // Battle results
    public void recordWin() {
        wins++;
        int oldScore = score;
        addScore(100);
        
        // Award Gaole Medals for wins
        int medalsEarned = 10;
        updateDatabaseMedals(medalsEarned);
        
        // Update database with new score
        updateDatabaseScore(score);
    }

    public void recordLoss() {
        losses++;
        int oldScore = score;
        addScore(25);
        
        // Award fewer medals for losses
        int medalsEarned = 3;
        updateDatabaseMedals(medalsEarned);
        
        // Update database with new score
        updateDatabaseScore(score);
    }

    public void addScore(int points) {
        score += points;

        // Bonus coins every 500 points
        if (score % 500 == 0 && points > 0) {
            coins += 50;
        }
    }

    // Money management
    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public void earnCoins(int amount) {
        coins += amount;
    }

    public boolean buyPokeBalls(String ballType, int quantity) {
        int cost = getPokeBallCost(ballType) * quantity;
        if (spendCoins(cost)) {
            int current = inventory.getOrDefault(ballType, 0);
            inventory.put(ballType, current + quantity);
            return true;
        }
        return false;
    }

    private int getPokeBallCost(String ballType) {
        switch (ballType) {
            case "Poke Ball": return 10;
            case "Great Ball": return 25;
            case "Ultra Ball": return 50;
            case "Master Ball": return 200;
            default: return 10;
        }
    }

    // Getters and Setters
    public int getPlayerID() { return playerID; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getCoins() { return coins; }
    public int getGaoleMedals() { return gaoleMedals; }
    
    public void setPlayerID(int playerID) { this.playerID = playerID; }
    public void setScore(int score) { this.score = score; }
    public void setGaoleMedals(int gaoleMedals) { this.gaoleMedals = gaoleMedals; }

    @Override
    public String toString() {
        return String.format("Player: %s (ID: %d) | Score: %d | Coins: %d | Pokemon: %d | Gaole Medals: %d",
                name, playerID, score, coins, pokemonCollection.size(), gaoleMedals);
        
        
        
    }

}