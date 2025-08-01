import java.util.*;



public class Player{
	private String name;
	private int score;
	private int Battles;
	private int win;
	private int lost;
	private ArrayList<Pokemon> pokemoncollection;
	private HashMap<String,Integer> inventory;
	private int coins;
	
	
	
	public Player(String name) {
		this.name = name;
		this.score = 0;
		this.Battles = 0;
		this.win = 0;
		this.lost = 0;
		this.pokemoncollection = new ArrayList<>();
		this.inventory = new HashMap<>();
		this.coins = 100;
		
		initializepokeballs();
	}
	
	private void initializepokeballs() {
		inventory.put("Pokeball", 10);
		inventory.put("Greatball", 5);
		inventory.put("Ultraball", 3);
		inventory.put("Masterball", 1);
	}
		
		
		 public boolean addPokemon(Pokemon pokemon) {
		        if (pokemoncollection.size() < 50) { 
		            pokemoncollection.add(pokemon);
		            return true;
		        }
		        return false;
		    }
		    
		    public boolean removePokemon(Pokemon pokemon) {
		        return pokemoncollection.remove(pokemon);
		    }
		    
		    public Pokemon getPokemon(int index) {
		        if (index >= 0 && index < pokemoncollection.size()) {
		            return pokemoncollection.get(index);
		        }
		        return null;
		    }
		    
		    public ArrayList<Pokemon> getPokemonCollection() {
		        return new ArrayList<>(pokemoncollection);
		    }
		    
		    public int getCollectionSize() {
		        return pokemoncollection.size();
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
		    
		    public void addPokeBall(String ballType, int amount) {
		        int current = inventory.getOrDefault(ballType, 0);
		        inventory.put(ballType, current + amount);
		    }
		    
		    public int getPokeBallCount(String ballType) {
		        return inventory.getOrDefault(ballType, 0);
		    }
		    
		    public HashMap<String, Integer> getAllPokeBalls() {
		        return new HashMap<>(inventory);
		    }
		    
		    
		    public void recordWin() {
		        win++;
		        Battles++;
		        addScore(100); 
		    }
		    
		    public void recordLoss() {
		        lost++;
		        Battles++;
		        addScore(25);
		    }
		    
		    public void addScore(int points) {
		        score += points;
		        
		        
		        if (score % 500 == 0 && points > 0) {
		            coins += 50;
		        }
		    }
		    
		    
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
		            addPokeBall(ballType, quantity);
		            return true;
		        }
		        return false;
		    }
		    
		    private int getPokeBallCost(String ballType) {
		        switch (ballType) {
		            case "PokeBall": return 10;
		            case "GreatBall": return 25;
		            case "UltraBall": return 50;
		            case "MasterBall": return 200;
		            default: return 10;
		        }
		    }
		    
		    
		    public double getWinRate() {
		        return Battles > 0 ? (double) win / Battles * 100 : 0;
		    }
		    
		    public int getTotalPokemonCaught() {
		        return pokemoncollection.size();
		    }
		    
		    public int getShinyPokemonCount() {
		        return (int) pokemoncollection.stream().filter(Pokemon::shiny).count();
		    }
		    
		    public Map<String, Integer> getPokemonByType() {
		        Map<String, Integer> typeCount = new HashMap<>();
		        for (Pokemon pokemon : pokemoncollection) {
		            typeCount.put(pokemon.getType(), 
		                         typeCount.getOrDefault(pokemon.getType(), 0) + 1);
		        }
		        return typeCount;
		    }
		    
		    
		    public String getName() { return name; }
		    public int getScore() { return score; }
		    public int getTotalBattles() { return Battles; }
		    public int getWins() { return win; }
		    public int getLosses() { return lost; }
		    public int getCoins() { return coins; }
		    
		    
		    public void setName(String name) { this.name = name; }
		    public void setScore(int score) { this.score = score; }
		    public void setCoins(int coins) { this.coins = coins; }
		    
		    
		    public String toString() {
		        return String.format("Player: %s | Score: %d | Coins: %d | Pokemon: %d | W/L: %d/%d", 
		                           name, score, coins, pokemoncollection.size(), win, lost);
		    }
		    
		    public String getDetailedStats() {
		        return String.format("""
		            === PLAYER STATS ===
		            Name: %s
		            Score: %d points
		            Coins: %d
		            
		            Battle Record:
		            Total Battles: %d
		            Wins: %d | Losses: %d
		            Win Rate: %.1f%%
		            
		            Pokemon Collection:
		            Total Pokemon: %d/50
		            Shiny Pokemon: %d
		            
		            PokeBall Inventory:
		            PokeBalls: %d
		            GreatBalls: %d
		            UltraBalls: %d
		            MasterBalls: %d
		            """, 
		            name, score, coins, Battles, win, lost, getWinRate(),
		            pokemoncollection.size(), getShinyPokemonCount(),
		            getPokeBallCount("PokeBall"), getPokeBallCount("GreatBall"),
		            getPokeBallCount("UltraBall"), getPokeBallCount("MasterBall"));
		    }
		}