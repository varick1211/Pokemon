import java.util.*;

public class Player {
    // Constants
    private static final int MAX_POKEMON_COLLECTION = 50;
    private static final int SCORE_BONUS_THRESHOLD = 500;
    private static final int BONUS_COINS = 50;
    private static final int WIN_SCORE = 100;
    private static final int LOSS_SCORE = 25;
    private static final int POKEBALL_COST = 10;
    
    // Player attributes
    private String name;
    private int score;
    private int totalBattles;
    private int wins;
    private int losses;
    private int coins;
    
    // Collections
    private ArrayList<Pokemon> pokemonCollection;
    private HashMap<String, Integer> inventory;
    
 
     
    public Player(String name) {
        this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : "Trainer";
        this.score = 0;
        this.totalBattles = 0;
        this.wins = 0;
        this.losses = 0;
        this.coins = 100; // Starting coins
        this.pokemonCollection = new ArrayList<>();
        this.inventory = new HashMap<>();
        
        // Initialize with some basic PokeBalls
        inventory.put("PokeBall", 5);
    }
    

     
    public boolean addPokemon(Pokemon pokemon) {
        if (pokemon == null) {
            return false;
        }
        
        if (pokemonCollection.size() < MAX_POKEMON_COLLECTION) {
            pokemonCollection.add(pokemon);
            return true;
        }
        return false;
    }
    
   
    public boolean removePokemon(Pokemon pokemon) {
        return pokemonCollection.remove(pokemon);
    }
    
    /**
     * Remove a Pokemon by index
     * @param index The index of the Pokemon to remove
     * @return true if successfully removed
     */
    public boolean removePokemon(int index) {
        if (isValidIndex(index)) {
            pokemonCollection.remove(index);
            return true;
        }
        return false;
    }
    
    /**
     * Get a Pokemon by index
     * @param index The index of the Pokemon
     * @return The Pokemon at the specified index, or null if invalid index
     */
    public Pokemon getPokemon(int index) {
        if (isValidIndex(index)) {
            return pokemonCollection.get(index);
        }
        return null;
    }
    
    /**
     * Get a copy of the Pokemon collection
     * @return A new ArrayList containing all Pokemon
     */
    public ArrayList<Pokemon> getPokemonCollection() {
        return new ArrayList<>(pokemonCollection);
    }
    
    /**
     * Get the number of Pokemon in the collection
     * @return The size of the Pokemon collection
     */
    public int getCollectionSize() {
        return pokemonCollection.size();
    }
    
    /**
     * Check if collection is full
     * @return true if collection has reached maximum capacity
     */
    public boolean isCollectionFull() {
        return pokemonCollection.size() >= MAX_POKEMON_COLLECTION;
    }
    
    /**
     * Get the number of available slots in the collection
     * @return Number of remaining slots
     */
    public int getAvailableSlots() {
        return MAX_POKEMON_COLLECTION - pokemonCollection.size();
    }
    
    private boolean isValidIndex(int index) {
        return index >= 0 && index < pokemonCollection.size();
    }
    
    // === POKEBALL MANAGEMENT ===
    
    /**
     * Check if player has PokeBalls of a specific type
     * @param ballType The type of PokeBall
     * @return true if player has at least one of that type
     */
    public boolean hasPokeBall(String ballType) {
        return getPokeBallCount(ballType) > 0;
    }
    
    /**
     * Use a PokeBall (decrements count by 1)
     * @param ballType The type of PokeBall to use
     * @return true if successfully used
     */
    public boolean usePokeBall(String ballType) {
        int count = getPokeBallCount(ballType);
        if (count > 0) {
            inventory.put(ballType, count - 1);
            return true;
        }
        return false;
    }
    
    /**
     * Add PokeBalls to inventory
     * @param ballType The type of PokeBall
     * @param amount The amount to add
     */
    public void addPokeBall(String ballType, int amount) {
        if (amount > 0) {
            int current = getPokeBallCount(ballType);
            inventory.put(ballType, current + amount);
        }
    }
    
    /**
     * Get the count of a specific PokeBall type
     * @param ballType The type of PokeBall
     * @return The count of that PokeBall type
     */
    public int getPokeBallCount(String ballType) {
        return inventory.getOrDefault(ballType, 0);
    }
    
    /**
     * Get a copy of all PokeBalls in inventory
     * @return A HashMap containing all PokeBall types and counts
     */
    public HashMap<String, Integer> getAllPokeBalls() {
        return new HashMap<>(inventory);
    }
    
    /**
     * Buy PokeBalls from the shop
     * @param ballType The type of PokeBall to buy
     * @param amount The amount to buy
     * @return true if purchase was successful
     */
    public boolean buyPokeBalls(String ballType, int amount) {
        if (amount <= 0) {
            return false;
        }
        
        int totalCost = amount * POKEBALL_COST;
        if (spendCoins(totalCost)) {
            addPokeBall(ballType, amount);
            return true;
        }
        return false;
    }
    
    // === BATTLE MANAGEMENT ===
    
    /**
     * Record a battle win
     */
    public void recordWin() {
        wins++;
        totalBattles++;
        addScore(WIN_SCORE);
    }
    
    /**
     * Record a battle loss
     */
    public void recordLoss() {
        losses++;
        totalBattles++;
        addScore(LOSS_SCORE);
    }
    
    /**
     * Add score points and check for coin bonuses
     * @param points The points to add
     */
    public void addScore(int points) {
        if (points > 0) {
            int oldScore = score;
            score += points;
            
            // Award bonus coins for every 500 points milestone
            int oldBonuses = oldScore / SCORE_BONUS_THRESHOLD;
            int newBonuses = score / SCORE_BONUS_THRESHOLD;
            
            if (newBonuses > oldBonuses) {
                int bonusCoins = (newBonuses - oldBonuses) * BONUS_COINS;
                earnCoins(bonusCoins);
            }
        }
    }
    
    /**
     * Calculate win rate as a percentage
     * @return Win rate as a double (0-100)
     */
    public double getWinRate() {
        return totalBattles > 0 ? (double) wins / totalBattles * 100 : 0.0;
    }
    
    // === COIN MANAGEMENT ===
    
    /**
     * Spend coins if player has enough
     * @param amount The amount to spend
     * @return true if transaction was successful
     */
    public boolean spendCoins(int amount) {
        if (amount > 0 && coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * Earn coins
     * @param amount The amount to earn
     */
    public void earnCoins(int amount) {
        if (amount > 0) {
            coins += amount;
        }
    }
    
    /**
     * Check if player can afford a purchase
     * @param cost The cost to check
     * @return true if player has enough coins
     */
    public boolean canAfford(int cost) {
        return coins >= cost;
    }
    
    // === STATISTICS ===
    
    /**
     * Get total number of Pokemon caught
     * @return The size of the Pokemon collection
     */
    public int getTotalPokemonCaught() {
        return pokemonCollection.size();
    }
    
    /**
     * Count shiny Pokemon in collection
     * @return Number of shiny Pokemon
     */
    public int getShinyPokemonCount() {
        return (int) pokemonCollection.stream()
            .filter(Pokemon::shiny)
            .count();
    }
    
    /**
     * Get count of Pokemon by type
     * @return Map with type names as keys and counts as values
     */
    public Map<String, Integer> getPokemonByType() {
        Map<String, Integer> typeCount = new HashMap<>();
        for (Pokemon pokemon : pokemonCollection) {
            String type = pokemon.getType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }
        return typeCount;
    }
    
    /**
     * Get the strongest Pokemon in collection
     * @return The Pokemon with highest level, or null if no Pokemon
     */
    public Pokemon getStrongestPokemon() {
        return pokemonCollection.stream()
            .max(Comparator.comparingInt(Pokemon::getLevel))
            .orElse(null);
    }
    
    /**
     * Get count of fainted Pokemon
     * @return Number of fainted Pokemon
     */
    public int getFaintedPokemonCount() {
        return (int) pokemonCollection.stream()
            .filter(Pokemon::fainted)
            .count();
    }
    
    // === GETTERS ===
    
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getTotalBattles() { return totalBattles; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getCoins() { return coins; }
    
    // === SETTERS ===
    
    public void setName(String name) { 
        this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : this.name;
    }
    
    public void setScore(int score) { 
        this.score = Math.max(0, score);
    }
    
    public void setCoins(int coins) { 
        this.coins = Math.max(0, coins);
    }
    
   
    public String getDetailedStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== PLAYER STATISTICS ===\n");
        stats.append("Name: ").append(name).append("\n");
        stats.append("Score: ").append(score).append("\n");
        stats.append("Coins: ").append(coins).append("\n");
        stats.append("Pokemon Collection: ").append(pokemonCollection.size())
             .append("/").append(MAX_POKEMON_COLLECTION).append("\n");
        stats.append("Battles: ").append(totalBattles).append(" (W: ")
             .append(wins).append(", L: ").append(losses).append(")\n");
        stats.append("Win Rate: ").append(String.format("%.1f", getWinRate())).append("%\n");
        stats.append("Shiny Pokemon: ").append(getShinyPokemonCount()).append("\n");
        stats.append("Fainted Pokemon: ").append(getFaintedPokemonCount()).append("\n");
        
        Pokemon strongest = getStrongestPokemon();
        if (strongest != null) {
            stats.append("Strongest Pokemon: ").append(strongest.getName())
                 .append(" (Level ").append(strongest.getLevel()).append(")\n");
        }
        
        stats.append("PokeBalls: ").append(getPokeBallCount("PokeBall"));
        
        return stats.toString();
    }
    
    @Override
    public String toString() {
        return String.format("Player: %s | Score: %d | Coins: %d | Pokemon: %d/%d | W/L: %d/%d (%.1f%%)", 
                           name, score, coins, pokemonCollection.size(), MAX_POKEMON_COLLECTION, 
                           wins, losses, getWinRate());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return Objects.equals(name, player.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}