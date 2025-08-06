import java.util.*;

public class Game {
    private Player player;
    private Scanner scanner;
    private boolean gameRunning;
    private Random random;
    private pokemongenerator pokemonGen;
    private FireTypePokemonBehavior fireBehavior;
    private ElectricTypePokemonBehavior electricBehavior;
    private GrassTypePokemonBehavior grassBehavior;
    private WaterTypePokemonBehavior waterBehavior;
    private DataBase database;
    private BattleTraining battleTraining;
    private PokemonStorage pokemonStorage; // Added Pokemon storage

    public Game() {
        this.scanner = new Scanner(System.in);
        this.gameRunning = true;
        this.random = new Random();
        this.pokemonGen = new pokemongenerator();
        this.fireBehavior = new FireTypePokemonBehavior();
        this.electricBehavior = new ElectricTypePokemonBehavior();
        this.grassBehavior = new GrassTypePokemonBehavior();
        this.waterBehavior = new WaterTypePokemonBehavior();
        this.database = new DataBase();
        this.battleTraining = new BattleTraining();
        this.pokemonStorage = new PokemonStorage(); // Initialize Pokemon storage
    }

    public void startGame() {
        showWelcome();
        setupPlayer();

        while (gameRunning) {
            showMainMenu();
            int choice = getChoice(1, 9); // Updated to 9 options
            handleChoice(choice);
        }

        // Save both player data and Pokemon collection before exiting
        if (player != null) {
            player.saveToDatabase();
            savePokemonCollection();
        }

        System.out.println("Thanks for playing!");
    }

    private void showWelcome() {
        System.out.println("\n=== POKEMON GAME ===");
        System.out.println("Catch Pokemon and build your collection!");
    }

    private void setupPlayer() {
        System.out.println("\n=== PLAYER SETUP ===");
        System.out.println("1. Create New Player");
        System.out.println("2. Load Existing Player");

        int choice = getChoice(1, 2);

        if (choice == 1) {
            createNewPlayer();
        } else {
            loadExistingPlayer();
        }
    }

    private void createNewPlayer() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = "Trainer";
        }

        this.player = new Player(name);
        System.out.println("Welcome, " + name + "!");
        System.out.println("Your Player ID is: " + player.getPlayerID());
        System.out.println("Remember this ID to load your progress later!");

        // Save initial player data
        player.saveToDatabase();

        giveStarterPokemon();
    }

    private void loadExistingPlayer() {
        System.out.print("Enter your Player ID: ");
        try {
            int playerID = scanner.nextInt();
            scanner.nextLine(); // consume newline

            // Load database from file
            database.loadDB();

            // Get the actual player data from database
            PlayerRecord playerData = database.getPlayerData(playerID);

            if (playerData != null) {
                // Create player with the actual saved name
                this.player = new Player(playerData.getPlayerName());
                player.setPlayerID(playerID);

                // Load the actual progress from database
                if (player.loadFromDatabase(playerID)) {
                    System.out.println("Player loaded successfully!");
                    System.out.println("Welcome back, " + playerData.getPlayerName() + "!");

                    // Display loaded progress
                    System.out.println("Progress loaded:");
                    System.out.println("- Score: " + playerData.getPlayerScores());
                    System.out.println("- Gaole Medals: " + playerData.getPlayerGaoleMedal());
                    System.out.println("- Coins: " + player.getCoins());
                    
                    // Load Pokemon collection
                    loadPokemonCollection();
                    System.out.println("- Pokemon Collection: " + player.getCollectionSize());
                } else {
                    System.out.println("Error loading player data. Creating new player instead.");
                    createNewPlayer();
                    return;
                }
            } else {
                System.out.println("Player ID not found. Creating new player instead.");
                createNewPlayer();
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid Player ID. Creating new player instead.");
            scanner.nextLine(); // clear invalid input
            createNewPlayer();
            return;
        }
    }

    private void giveStarterPokemon() {
        System.out.println("\nChoose your starter Pokemon:");
        System.out.println("1. Charmander (Fire)");
        System.out.println("2. Squirtle (Water)");
        System.out.println("3. Bulbasaur (Grass)");

        int choice = getChoice(1, 3);
        Pokemon starter;

        if (choice == 1) {
            starter = new Pokemon("Charmander", "Fire", 5);
        } else if (choice == 2) {
            starter = new Pokemon("Squirtle", "Water", 5);
        } else {
            starter = new Pokemon("Bulbasaur", "Grass", 5);
        }

        player.addPokemon(starter);
        System.out.println("You got " + starter.getName() + "!");

        // Show starter Pokemon details
        System.out.println(starter.getDetailedInfo());
        
        // Save Pokemon collection after getting starter
        savePokemonCollection();
        
        pressEnter();
    }

    private void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println(player.toString());
        System.out.println();
        System.out.println("1. Explore (Find Pokemon)");
        System.out.println("2. View Pokemon");
        System.out.println("3. Buy PokeBalls");
        System.out.println("4. Pokemon Battle Training");
        System.out.println("5. Use Miracle Item");
        System.out.println("6. View Top Scores");
        System.out.println("7. View Top Players");
        System.out.println("8. Save Pokemon Collection"); // New option
        System.out.println("9. Quit Game");
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1:
                explore();
                break;
            case 2:
                viewPokemon();
                break;
            case 3:
                buyPokeBalls();
                break;
            case 4:
                // Pass all type behaviors to battle training
                battleTraining.startTraining(player, scanner, fireBehavior, electricBehavior, grassBehavior, waterBehavior);
                break;
            case 5:
                useMiracleItem();
                break;
            case 6:
                viewTopScores();
                break;
            case 7:
                viewTopPlayers();
                break;
            case 8:
                savePokemonCollection(); // New save option
                pressEnter();
                break;
            case 9:
                gameRunning = false;
                break;
        }
    }

    // New method to save Pokemon collection
    private void savePokemonCollection() {
        if (player != null && player.getCollectionSize() > 0) {
            List<Pokemon> pokemonList = new ArrayList<>();
            for (int i = 0; i < player.getCollectionSize(); i++) {
                pokemonList.add(player.getPokemon(i));
            }
            pokemonStorage.savePokemonCollection(player.getPlayerID(), pokemonList);
        }
    }

    // New method to load Pokemon collection
    private void loadPokemonCollection() {
        if (player != null && pokemonStorage.hasStoredCollection(player.getPlayerID())) {
            List<Pokemon> loadedPokemon = pokemonStorage.loadPokemonCollection(player.getPlayerID());
            for (Pokemon pokemon : loadedPokemon) {
                player.addPokemon(pokemon);
            }
        }
    }

    // Method to get the appropriate behavior for a Pokemon type
    public Object getTypeBehavior(String type) {
        switch (type.toLowerCase()) {
            case "fire":
                return fireBehavior;
            case "electric":
                return electricBehavior;
            case "grass":
                return grassBehavior;
            case "water":
                return waterBehavior;
            default:
                return null;
        }
    }

    // Method to execute type-specific attack
    public int executeTypeAttack(Pokemon attacker, Pokemon target) {
        String attackerType = attacker.getType().toLowerCase();
        
        switch (attackerType) {
            case "fire":
                return fireBehavior.fireAttack(attacker, target);
            case "electric":
                return electricBehavior.electricAttack(attacker, target);
            case "grass":
                return grassBehavior.grassAttack(attacker, target);
            case "water":
                return waterBehavior.waterAttack(attacker, target);
            default:
                // For other types, use basic attack
                int damage = attacker.getAttack() - target.getDefense() / 2;
                damage = Math.max(1, damage);
                System.out.println(attacker.getName() + " used a basic attack!");
                target.takeDamage(damage);
                return damage;
        }
    }

    private void useMiracleItem() {
        System.out.println("\n=== MIRACLE ITEM SHOP ===");
        System.out.println("Current Gaole Medals: " + player.getGaoleMedals());
        System.out.println();
        System.out.println("Miracle Item - Cost: 160 Gaole Medals");
        System.out.println("Effect: Fully restores all Pokemon HP and gives bonus items!");

        if (player.getGaoleMedals() < 160) {
            System.out.println("You don't have enough Gaole Medals!");
            pressEnter();
            return;
        }

        System.out.println("Do you want to use a Miracle Item? (y/n): ");
        String input = scanner.nextLine().toLowerCase();

        if (input.equals("y") || input.equals("yes")) {
            if (player.checkAndUseMiracleItem()) {
                System.out.println("Miracle Item used successfully!");

                // Heal all Pokemon - using fullHeal() instead of heal()
                int healedCount = 0;
                for (int i = 0; i < player.getCollectionSize(); i++) {
                    Pokemon pokemon = player.getPokemon(i);
                    if (pokemon != null) {
                        // Use fullHeal() which handles both fainted and non-fainted Pokemon
                        pokemon.fullHeal();
                        healedCount++;
                    }
                }

                // Give bonus items
                player.earnCoins(5);
                System.out.println("All " + healedCount + " Pokemon fully healed and revived!");
                System.out.println("Bonus: Received 5 coins!");
                
                // Auto-save Pokemon collection after healing
                savePokemonCollection();
            }
        }

        pressEnter();
    }

    private void viewTopScores() {
        System.out.println("\n=== TOP SCORES ===");
        player.displayTopScores();
        pressEnter();
    }

    private void viewTopPlayers() {
        System.out.println("\n=== TOP PLAYERS ===");
        player.displayTop5Players();
        pressEnter();
    }

    private void explore() {
        System.out.println("\nExploring...");

        if (random.nextDouble() < 0.7) {
            Pokemon wildPokemon = pokemonGen.createRandomPokemon();
            System.out.println("You found a wild " + wildPokemon.getName() + "!");
            System.out.println(wildPokemon.getDetailedInfo());

            Battle battle = new Battle(player, wildPokemon);
            battle.startBattle();
            
            // Auto-save Pokemon collection after battle (in case Pokemon was caught)
            savePokemonCollection();
        } else {
            System.out.println("No Pokemon found, but you found some coins!");
            int coins = random.nextInt(15) + 5;
            player.earnCoins(coins);
            System.out.println("Found " + coins + " coins!");

            // Small chance to find Gaole Medals too
            if (random.nextDouble() < 0.3) {
                int medals = random.nextInt(100) + 1;
                player.updateDatabaseMedals(medals);
                System.out.println("You also found " + medals + " Gaole Medals!");
            }
        }

        pressEnter();
    }

    private Pokemon createPokemonOfType(String type) {
        return pokemonGen.createPokemonOfType(type);
    }

    private Pokemon createPokemonInRange(int minLevel, int maxLevel) {
        return pokemonGen.createPokemonInRange(minLevel, maxLevel);
    }

    private void viewPokemon() {
        System.out.println("\n=== YOUR POKEMON ===");

        if (player.getCollectionSize() == 0) {
            System.out.println("You don't have any Pokemon yet!");
            pressEnter();
            return;
        }

        // Show list first
        for (int i = 0; i < player.getCollectionSize(); i++) {
            Pokemon pokemon = player.getPokemon(i);
            System.out.println((i + 1) + ". " + pokemon.getName() +
                             " (Type: " + pokemon.getType() +
                             ", Level: " + pokemon.getLevel() +
                             ", HP: " + pokemon.getHP() + "/" + pokemon.getMaxHP() + 
                             ", Status: " + pokemon.getStatus() + ")");
        }

        System.out.println("\nEnter Pokemon number for details (0 to go back): ");
        int choice = getChoice(0, player.getCollectionSize());

        if (choice > 0) {
            Pokemon selectedPokemon = player.getPokemon(choice - 1);
            System.out.println(selectedPokemon.getDetailedInfo());
        }

        pressEnter();
    }

    private void buyPokeBalls() {
        System.out.println("\n=== POKEBALL SHOP ===");
        System.out.println("Your coins: " + player.getCoins());
        System.out.println();

        // Show current inventory
        System.out.println("Current Inventory:");
        String[] ballTypes = PokeBall.getAllBallTypes();
        for (String ballType : ballTypes) {
            System.out.println(ballType + ": " + player.getPokeBallCount(ballType));
        }
        System.out.println();

        // Show shop options
        System.out.println("Available PokeBalls:");
        PokeBall pokeBall = PokeBall.createPokeBall();
        PokeBall greatBall = PokeBall.createGreatBall();
        PokeBall ultraBall = PokeBall.createUltraBall();
        PokeBall masterBall = PokeBall.createMasterBall();

        System.out.println("1. " + pokeBall.toString());
        System.out.println("2. " + greatBall.toString());
        System.out.println("3. " + ultraBall.toString());
        System.out.println("4. " + masterBall.toString());
        System.out.println("5. Exit Shop");

        int choice = getChoice(1, 5);

        if (choice == 5) {
            return; // Exit shop
        }

        PokeBall selectedBall;
        String ballName;

        switch (choice) {
            case 1:
                selectedBall = pokeBall;
                ballName = "Poke Ball";
                break;
            case 2:
                selectedBall = greatBall;
                ballName = "Great Ball";
                break;
            case 3:
                selectedBall = ultraBall;
                ballName = "Ultra Ball";
                break;
            case 4:
                selectedBall = masterBall;
                ballName = "Master Ball";
                break;
            default:
                return;
        }

        int cost = selectedBall.getCost();
        int maxCanBuy = player.getCoins() / cost;

        if (maxCanBuy == 0) {
            System.out.println("You don't have enough coins for " + ballName + "!");
            pressEnter();
            return;
        }

        System.out.println("How many " + ballName + "s do you want to buy?");
        System.out.println("(You can afford " + maxCanBuy + ")");
        System.out.print("Enter amount (0 to cancel): ");

        int amount = getChoice(0, maxCanBuy);

        if (amount > 0) {
            if (player.buyPokeBalls(ballName, amount)) {
                System.out.println("Bought " + amount + " " + ballName + "(s) for " + (amount * cost) + " coins!");
            } else {
                System.out.println("Purchase failed!");
            }
        }

        pressEnter();
    }

    private int getChoice(int min, int max) {
        return GameUtils.getChoice(min, max, scanner);
    }

    private void pressEnter() {
        GameUtils.pressEnter(scanner);
    }
}