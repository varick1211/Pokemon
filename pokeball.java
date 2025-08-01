


public class pokeball {
    private String type;
    private double baseCatchRate;
    private int cost;
    private String description;
    
    // PokeBall type constants
    public static final String POKEBALL = "PokeBall";
    public static final String GREATBALL = "GreatBall";
    public static final String ULTRABALL = "UltraBall";
    public static final String MASTERBALL = "MasterBall";
    
    // Constructor
    public pokeball(String type) {
        this.type = type;
        initializeBallStats();
    }
    
    private void initializeBallStats() {
        switch (type) {
            case POKEBALL:
                this.baseCatchRate = 0.3;
                this.cost = 10;
                this.description = "A basic ball for catching Pokemon";
                break;
            case GREATBALL:
                this.baseCatchRate = 0.5;
                this.cost = 25;
                this.description = "Better catch rate than a regular PokeBall";
                break;
            case ULTRABALL:
                this.baseCatchRate = 0.7;
                this.cost = 50;
                this.description = "High-performance ball with excellent catch rate";
                break;
            case MASTERBALL:
                this.baseCatchRate = 1.0;
                this.cost = 200;
                this.description = "The ultimate ball that never fails";
                break;
            default:
                this.baseCatchRate = 0.3;
                this.cost = 10;
                this.description = "Unknown ball type";
                break;
        }
    }
    
    
    public boolean attemptCatch(Pokemon pokemon) {
        if (type.equals(MASTERBALL)) {
            return true; 
        }
        
        
        double catchChance = calculateCatchChance(pokemon);
        double randomValue = Math.random();
        

        return performCatchSequence(catchChance, randomValue);
    }
    
    private double calculateCatchChance(Pokemon pokemon) {
        double hpFactor = 1.0 - ((double) pokemon.getHP() / pokemon.getMaxHP());
        double levelFactor = Math.max(0.1, 1.0 - (pokemon.getLevel() / 100.0));
        double statusFactor = pokemon.getStatus().equals("normal") ? 1.0 : 1.5;
        
      
        double shinyFactor = pokemon.shiny() ? 0.8 : 1.0;
     
        double typeFactor = getTypeCatchModifier(pokemon.getType());
        
     
        double finalChance = baseCatchRate * (1.0 + hpFactor) * levelFactor * statusFactor * shinyFactor * typeFactor;
        
        
        return Math.min(0.95, Math.max(0.05, finalChance));
    }
    
    private double getTypeCatchModifier(String type) {
        
        switch (type.toLowerCase()) {
            case "fire":
                return 0.9; 
            case "electric":
                return 0.85; 
            case "water":
                return 1.0; 
            case "grass":
                return 1.1; 
            case "psychic":
                return 0.8; 
            case "fighting":
                return 0.75; 
            default:
                return 1.0;
        }
    }
    
    private boolean performCatchSequence(double catchChance, double randomValue) {
        
        System.out.println("The ball shakes once...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
        }
        
        if (randomValue > catchChance * 0.3) {
            return false; 
        }
        
        System.out.println("The ball shakes twice...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
        }
        
        if (randomValue > catchChance * 0.6) {
            return false; 
        }
        
        System.out.println("The ball shakes three times...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
        }
        
        return randomValue <= catchChance;
    }
    
   
    
    
    public String getEffectivenessRating(Pokemon pokemon) {
        double catchChance = calculateCatchChance(pokemon);
        
        if (catchChance >= 0.8) {
            return "Excellent";
        } else if (catchChance >= 0.6) {
            return "Very Good";
        } else if (catchChance >= 0.4) {
            return "Good";
        } else if (catchChance >= 0.2) {
            return "Fair";
        } else {
            return "Poor";
        }
    }
    
   
    public int getCatchPercentage(Pokemon pokemon) {
        return (int) (calculateCatchChance(pokemon) * 100);
    }
    
    // Static utility methods
    public static pokeball createBall(String type) {
        return new pokeball(type);
    }
    
    public static boolean isValidBallType(String type) {
        return type.equals(POKEBALL) || type.equals(GREATBALL) || 
               type.equals(ULTRABALL) || type.equals(MASTERBALL);
    }
    
    public static String[] getAllBallTypes() {
        return new String[]{POKEBALL, GREATBALL, ULTRABALL, MASTERBALL};
    }
    
    public static int getBallCost(String type) {
        pokeball tempBall = new pokeball(type);
        return tempBall.getCost();
    }
    
    
    public String getType() { return type; }
    public double getBaseCatchRate() { return baseCatchRate; }
    public int getCost() { return cost; }
    public String getDescription() { return description; }
    
    
    public String toString() {
        return String.format("%s (%.0f%% base rate) - %s", 
                           type, baseCatchRate * 100, description);
    }
    
    public String getDetailedInfo() {
        return String.format("""
            %s
            Base Catch Rate: %.0f%%
            Cost: %d coins
            Description: %s
            """, 
            type, baseCatchRate * 100, cost, description);
    }
}
