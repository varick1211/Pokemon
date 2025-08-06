import java.util.*;

public class PokeBall {
    private String name;
    private double catchRate;
    private int cost;
    private String description;
    private Random random;
    
    public PokeBall(String name, double catchRate, int cost, String description) {
        this.name = name;
        this.catchRate = catchRate;
        this.cost = cost;
        this.description = description;
        this.random = new Random();
    }
    
    /**
     * Attempt to catch a Pokemon with this PokeBall
     * @param wildPokemon The Pokemon to catch
     * @return true if catch was successful
     */
    public boolean attemptCatch(Pokemon wildPokemon) {
        // Base catch rate calculation
        double healthFactor = 1.0 - ((double) wildPokemon.getHP() / wildPokemon.getMaxHP());
        double levelFactor = 1.0 / (1.0 + wildPokemon.getLevel() * 0.05);
        
        // Calculate final catch chance
        double finalCatchRate = this.catchRate * (1.0 + healthFactor) * levelFactor;
        
        // Special case for Master Ball
        if (name.equals("Master Ball")) {
            return true; // Master Ball never fails
        }
        
        // Random catch attempt
        double roll = random.nextDouble();
        return roll < finalCatchRate;
    }
    
    /**
     * Get catch success message
     */
    public String getCatchMessage(Pokemon pokemon) {
        return "You caught " + pokemon.getName() + " with a " + this.name + "!";
    }
    
    /**
     * Get catch failure message
     */
    public String getFailMessage(Pokemon pokemon) {
        switch (name) {
            case "Poke Ball":
                return pokemon.getName() + " broke free from the Poke Ball!";
            case "Great Ball":
                return pokemon.getName() + " escaped from the Great Ball!";
            case "Ultra Ball":
                return pokemon.getName() + " burst out of the Ultra Ball!";
            case "Master Ball":
                return "Impossible! Master Ball cannot fail!";
            default:
                return pokemon.getName() + " got away!";
        }
    }
    
    /**
     * Create specific PokeBall types
     */
    public static PokeBall createPokeBall() {
        return new PokeBall("Poke Ball", 0.3, 10, "Standard PokeBall with basic catch rate");
    }
    
    public static PokeBall createGreatBall() {
        return new PokeBall("Great Ball", 0.5, 25, "Better catch rate than Poke Ball");
    }
    
    public static PokeBall createUltraBall() {
        return new PokeBall("Ultra Ball", 0.7, 50, "High catch rate for tough Pokemon");
    }
    
    public static PokeBall createMasterBall() {
        return new PokeBall("Master Ball", 1.0, 200, "Never fails to catch any Pokemon");
    }
    
    /**
     * Get PokeBall by name
     */
    public static PokeBall getPokeBallByName(String ballName) {
        switch (ballName) {
            case "Poke Ball":
                return createPokeBall();
            case "Great Ball":
                return createGreatBall();
            case "Ultra Ball":
                return createUltraBall();
            case "Master Ball":
                return createMasterBall();
            default:
                return createPokeBall();
        }
    }
    
    /**
     * Get all available PokeBall types
     */
    public static String[] getAllBallTypes() {
        return new String[]{"Poke Ball", "Great Ball", "Ultra Ball", "Master Ball"};
    }
    
    /**
     * Show catch probability for this ball against a Pokemon
     */
    public String getCatchProbability(Pokemon pokemon) {
        if (name.equals("Master Ball")) {
            return "100% - Master Ball never fails!";
        }
        
        double healthFactor = 1.0 - ((double) pokemon.getHP() / pokemon.getMaxHP());
        double levelFactor = 1.0 / (1.0 + pokemon.getLevel() * 0.05);
        double finalCatchRate = this.catchRate * (1.0 + healthFactor) * levelFactor;
        
        int percentage = (int) (Math.min(finalCatchRate, 1.0) * 100);
        return percentage + "% catch chance";
    }
    
    // Getters
    public String getName() { return name; }
    public double getCatchRate() { return catchRate; }
    public int getCost() { return cost; }
    public String getDescription() { return description; }
    
    @Override
    public String toString() {
        return name + " (Catch Rate: " + (int)(catchRate * 100) + "%, Cost: " + cost + " coins)";
    }
}