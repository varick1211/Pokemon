import java.util.*;

public class PlayerRecord {
    private int playerID;
    private String playerName;
    private int playerScores;
    private int playerGaoleMedal;
    private ArrayList<Pokemon> pokemonCollection;
    private HashMap<String, Integer> pokeBallInventory;
    private int coins;

    // Default constructor
    public PlayerRecord() {
        this.playerID = 0;
        this.playerName = "";
        this.playerScores = 0;
        this.playerGaoleMedal = 0;
        this.pokemonCollection = new ArrayList<>();
        this.pokeBallInventory = new HashMap<>();
        this.coins = 100; // Default starting coins
        initializePokeBallInventory();
    }

    // Constructor with basic parameters
    public PlayerRecord(int playerID, String playerName, int playerScores, int playerGaoleMedal) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.playerScores = playerScores;
        this.playerGaoleMedal = playerGaoleMedal;
        this.pokemonCollection = new ArrayList<>();
        this.pokeBallInventory = new HashMap<>();
        this.coins = 100; // Default starting coins
        initializePokeBallInventory();
    }

    // Complete constructor
    public PlayerRecord(int playerID, String playerName, int playerScores, int playerGaoleMedal, 
                       ArrayList<Pokemon> pokemonCollection, HashMap<String, Integer> pokeBallInventory, int coins) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.playerScores = playerScores;
        this.playerGaoleMedal = playerGaoleMedal;
        this.pokemonCollection = pokemonCollection != null ? new ArrayList<>(pokemonCollection) : new ArrayList<>();
        this.pokeBallInventory = pokeBallInventory != null ? new HashMap<>(pokeBallInventory) : new HashMap<>();
        this.coins = coins;
        if (pokeBallInventory == null) {
            initializePokeBallInventory();
        }
    }

    // Initialize default PokeBall inventory
    private void initializePokeBallInventory() {
        pokeBallInventory.put("Poke Ball", 5);
        pokeBallInventory.put("Great Ball", 0);
        pokeBallInventory.put("Ultra Ball", 0);
        pokeBallInventory.put("Master Ball", 0);
    }

    // Basic Getters
    public int getPlayerID() {
        return playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerScores() {
        return playerScores;
    }

    public int getPlayerGaoleMedal() {
        return playerGaoleMedal;
    }

    public int getCoins() {
        return coins;
    }

    // Pokemon Collection Getters
    public ArrayList<Pokemon> getPokemonCollection() {
        return new ArrayList<>(pokemonCollection); // Return copy to prevent external modification
    }

    public int getPokemonCollectionSize() {
        return pokemonCollection.size();
    }

    public Pokemon getPokemon(int index) {
        if (index >= 0 && index < pokemonCollection.size()) {
            return pokemonCollection.get(index);
        }
        return null;
    }

    // PokeBall Inventory Getters
    public HashMap<String, Integer> getPokeBallInventory() {
        return new HashMap<>(pokeBallInventory); // Return copy to prevent external modification
    }

    public int getPokeBallCount(String ballType) {
        return pokeBallInventory.getOrDefault(ballType, 0);
    }

    public int getTotalPokeBalls() {
        return pokeBallInventory.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Basic Setters
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerScores(int playerScores) {
        this.playerScores = playerScores;
    }

    public void setPlayerGaoleMedal(int playerGaoleMedal) {
        this.playerGaoleMedal = playerGaoleMedal;
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins); // Ensure coins can't be negative
    }

    // Pokemon Collection Setters
    public void setPokemonCollection(ArrayList<Pokemon> pokemonCollection) {
        this.pokemonCollection = pokemonCollection != null ? new ArrayList<>(pokemonCollection) : new ArrayList<>();
    }

    public void setPokeBallInventory(HashMap<String, Integer> pokeBallInventory) {
        this.pokeBallInventory = pokeBallInventory != null ? new HashMap<>(pokeBallInventory) : new HashMap<>();
        if (pokeBallInventory == null) {
            initializePokeBallInventory();
        }
    }

    // Pokemon Collection Management
    public boolean addPokemon(Pokemon pokemon) {
        if (pokemon != null) {
            pokemonCollection.add(pokemon);
            return true;
        }
        return false;
    }

    public boolean removePokemon(int index) {
        if (index >= 0 && index < pokemonCollection.size()) {
            pokemonCollection.remove(index);
            return true;
        }
        return false;
    }

    public boolean removePokemon(Pokemon pokemon) {
        return pokemonCollection.remove(pokemon);
    }

    public void clearPokemonCollection() {
        pokemonCollection.clear();
    }

    // PokeBall Inventory Management
    public boolean addPokeBalls(String ballType, int quantity) {
        if (ballType != null && quantity > 0) {
            int currentCount = pokeBallInventory.getOrDefault(ballType, 0);
            pokeBallInventory.put(ballType, currentCount + quantity);
            return true;
        }
        return false;
    }

    public boolean usePokeBall(String ballType) {
        int currentCount = pokeBallInventory.getOrDefault(ballType, 0);
        if (currentCount > 0) {
            pokeBallInventory.put(ballType, currentCount - 1);
            return true;
        }
        return false;
    }

    public boolean hasPokeBall(String ballType) {
        return pokeBallInventory.getOrDefault(ballType, 0) > 0;
    }

    public boolean hasAnyPokeBalls() {
        return getTotalPokeBalls() > 0;
    }

    // Coins Management
    public void addCoins(int amount) {
        if (amount > 0) {
            this.coins += amount;
        }
    }

    public boolean spendCoins(int amount) {
        if (amount > 0 && this.coins >= amount) {
            this.coins -= amount;
            return true;
        }
        return false;
    }

    public boolean canAfford(int cost) {
        return this.coins >= cost;
    }

    // Score and Medal Management
    public void addScore(int points) {
        if (points > 0) {
            this.playerScores += points;
        }
    }

    public void addGaoleMedals(int medals) {
        if (medals > 0) {
            this.playerGaoleMedal += medals;
        }
    }

    public boolean spendGaoleMedals(int amount) {
        if (amount > 0 && this.playerGaoleMedal >= amount) {
            this.playerGaoleMedal -= amount;
            return true;
        }
        return false;
    }

    // Pokemon Type Analysis
    public Map<String, Integer> getPokemonTypeCount() {
        Map<String, Integer> typeCount = new HashMap<>();
        for (Pokemon pokemon : pokemonCollection) {
            String type = pokemon.getType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }
        return typeCount;
    }

    public List<Pokemon> getPokemonByType(String type) {
        List<Pokemon> result = new ArrayList<>();
        for (Pokemon pokemon : pokemonCollection) {
            if (pokemon.getType().equalsIgnoreCase(type)) {
                result.add(pokemon);
            }
        }
        return result;
    }

    public int getAveragePokemonLevel() {
        if (pokemonCollection.isEmpty()) {
            return 0;
        }
        int totalLevel = 0;
        for (Pokemon pokemon : pokemonCollection) {
            totalLevel += pokemon.getLevel();
        }
        return totalLevel / pokemonCollection.size();
    }

    public Pokemon getHighestLevelPokemon() {
        if (pokemonCollection.isEmpty()) {
            return null;
        }
        Pokemon highest = pokemonCollection.get(0);
        for (Pokemon pokemon : pokemonCollection) {
            if (pokemon.getLevel() > highest.getLevel()) {
                highest = pokemon;
            }
        }
        return highest;
    }

    // Utility methods
    @Override
    public String toString() {
        return String.format("PlayerRecord{ID=%d, Name='%s', Score=%d, Medals=%d, Pokemon=%d, Coins=%d}",
                playerID, playerName, playerScores, playerGaoleMedal, pokemonCollection.size(), coins);
    }

    public String getDetailedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== PLAYER DETAILS ===\n");
        info.append("ID: ").append(playerID).append("\n");
        info.append("Name: ").append(playerName).append("\n");
        info.append("Score: ").append(playerScores).append("\n");
        info.append("Gaole Medals: ").append(playerGaoleMedal).append("\n");
        info.append("Coins: ").append(coins).append("\n");
        info.append("Pokemon Collection: ").append(pokemonCollection.size()).append(" Pokemon\n");
        
        if (!pokemonCollection.isEmpty()) {
            info.append("Average Pokemon Level: ").append(getAveragePokemonLevel()).append("\n");
            Pokemon highest = getHighestLevelPokemon();
            if (highest != null) {
                info.append("Strongest Pokemon: ").append(highest.getName())
                    .append(" (Level ").append(highest.getLevel()).append(")\n");
            }
            
            Map<String, Integer> typeCount = getPokemonTypeCount();
            info.append("Pokemon Types: ");
            typeCount.forEach((type, count) -> 
                info.append(type).append(":").append(count).append(" "));
            info.append("\n");
        }
        
        info.append("PokeBall Inventory: ");
        pokeBallInventory.forEach((type, count) -> 
            info.append(type).append(":").append(count).append(" "));
        
        return info.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PlayerRecord that = (PlayerRecord) obj;
        return playerID == that.playerID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(playerID);
    }

    // Create a deep copy of this player record
    public PlayerRecord copy() {
        // Create deep copies of collections
        ArrayList<Pokemon> copiedPokemon = new ArrayList<>();
        for (Pokemon pokemon : pokemonCollection) {
            // Assuming Pokemon class has a copy constructor or clone method
            copiedPokemon.add(new Pokemon(pokemon.getName(), pokemon.getType(), pokemon.getLevel()));
        }
        
        HashMap<String, Integer> copiedInventory = new HashMap<>(pokeBallInventory);
        
        return new PlayerRecord(this.playerID, this.playerName, this.playerScores, 
                               this.playerGaoleMedal, copiedPokemon, copiedInventory, this.coins);
    }

    // Validation methods
    public boolean isValid() {
        return playerID > 0 && 
               playerName != null && !playerName.trim().isEmpty() &&
               playerScores >= 0 && 
               playerGaoleMedal >= 0 &&
               coins >= 0 &&
               pokemonCollection != null &&
               pokeBallInventory != null;
    }

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        
        if (playerID <= 0) errors.add("Player ID must be positive");
        if (playerName == null || playerName.trim().isEmpty()) errors.add("Player name cannot be empty");
        if (playerScores < 0) errors.add("Player scores cannot be negative");
        if (playerGaoleMedal < 0) errors.add("Gaole medals cannot be negative");
        if (coins < 0) errors.add("Coins cannot be negative");
        if (pokemonCollection == null) errors.add("Pokemon collection cannot be null");
        if (pokeBallInventory == null) errors.add("PokeBall inventory cannot be null");
        
        return errors;
    }

    // Statistics methods
    public int getTotalHealthyPokemon() {
        int count = 0;
        for (Pokemon pokemon : pokemonCollection) {
            if (pokemon.getHP() > 0) {
                count++;
            }
        }
        return count;
    }

    public double getCollectionHealthPercentage() {
        if (pokemonCollection.isEmpty()) {
            return 0.0;
        }
        
        int totalCurrentHP = 0;
        int totalMaxHP = 0;
        
        for (Pokemon pokemon : pokemonCollection) {
            totalCurrentHP += pokemon.getHP();
            totalMaxHP += pokemon.getMaxHP();
        }
        
        return totalMaxHP > 0 ? (double) totalCurrentHP / totalMaxHP * 100 : 0.0;
    }
}