import java.util.*;

public class Game {
    private Player player;
    private Scanner scanner;
    private boolean gameRunning;
    private Random random;
    private pokemongenerator pokemonGen;
    private FireTypePokemonBehavior fireBehavior;
    
    public Game() {
        this.scanner = new Scanner(System.in);
        this.gameRunning = true;
        this.random = new Random();
        this.pokemonGen = new pokemongenerator();
        this.fireBehavior = new FireTypePokemonBehavior();
    }
    
    public void startGame() {
        showWelcome();
        setupPlayer();
        
        while (gameRunning) {
            showMainMenu();
            int choice = getChoice(1, 6); // Changed from 5 to 6 for new option
            handleChoice(choice);
        }
        
        // Close scanner to prevent resource leak
        scanner.close();
        System.out.println("Thanks for playing!");
    }
    
    private void showWelcome() {
        System.out.println("\n=== POKEMON GAME ===");
        System.out.println("Catch Pokemon and build your collection!");
    }
    
    private void setupPlayer() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = "Trainer";
        }
        
        this.player = new Player(name);
        System.out.println("Welcome, " + name + "!");
        
        giveStarterPokemon();
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
        pressEnter();
    }
    
    private void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("Score: " + player.getScore() + " | Coins: " + player.getCoins());
        System.out.println("Pokemon: " + player.getCollectionSize());
        System.out.println();
        System.out.println("1. Explore (Find Pokemon)");
        System.out.println("2. View Pokemon");
        System.out.println("3. Buy PokeBalls");
        System.out.println("4. Pokemon Battle Training");
        System.out.println("5. View Player Statistics"); // NEW OPTION
        System.out.println("6. Quit Game"); // Changed from 5 to 6
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
                battleTraining();
                break;
            case 5:
                viewPlayerStats(); // NEW METHOD
                break;
            case 6: // Changed from 5 to 6
                gameRunning = false;
                break;
            default:
                System.out.println("Invalid choice!");
                break;
        }
    }
    
    // NEW METHOD: Display detailed player statistics
    private void viewPlayerStats() {
        System.out.println("\n" + player.getDetailedStats());
        
        // Additional detailed breakdown
        if (player.getCollectionSize() > 0) {
            System.out.println("\n=== POKEMON TYPE BREAKDOWN ===");
            Map<String, Integer> typeCount = player.getPokemonByType();
            for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
                System.out.println(entry.getKey() + " type: " + entry.getValue());
            }
            
            System.out.println("\n=== POKEBALL INVENTORY ===");
            HashMap<String, Integer> pokeballs = player.getAllPokeBalls();
            for (Map.Entry<String, Integer> entry : pokeballs.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
        
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
            
            // Additional healing message after returning from battle
            System.out.println("You returned to the Pokemon Center and your Pokemon are ready for the next adventure!");
        } else {
            System.out.println("No Pokemon found, but you found some coins!");
            int coins = random.nextInt(15) + 5;
            player.earnCoins(coins);
            System.out.println("Found " + coins + " coins!");
        }
        
        pressEnter();
    }
    
    private void battleTraining() {
        System.out.println("\n=== BATTLE TRAINING ===");
        
        if (player.getCollectionSize() < 2) {
            System.out.println("You need at least 2 Pokemon to train!");
            pressEnter();
            return;
        }
        
        // Show all Pokemon with their current status
        System.out.println("Choose your first Pokemon:");
        showPokemonList();
        
        int choice1 = getChoice(1, player.getCollectionSize());
        Pokemon pokemon1 = player.getPokemon(choice1 - 1);
        
        // Check if Pokemon is fainted
        if (pokemon1.fainted()) {
            System.out.println(pokemon1.getName() + " is fainted and cannot battle!");
            pressEnter();
            return;
        }
        
        System.out.println("Choose your second Pokemon:");
        showPokemonListExcluding(choice1 - 1);
        
        int choice2 = getChoice(1, player.getCollectionSize());
        while (choice2 == choice1) {
            System.out.println("Choose a different Pokemon!");
            choice2 = getChoice(1, player.getCollectionSize());
        }
        Pokemon pokemon2 = player.getPokemon(choice2 - 1);
        
        // Check if second Pokemon is fainted
        if (pokemon2.fainted()) {
            System.out.println(pokemon2.getName() + " is fainted and cannot battle!");
            pressEnter();
            return;
        }
        
        // Training battle
        System.out.println("\n" + pokemon1.getName() + " vs " + pokemon2.getName() + "!");
        
        // Use fire behavior if applicable, otherwise use normal attack
        int damage1 = 0;
        if (pokemon1.getType().equals("Fire")) {
            damage1 = fireBehavior.fireAttack(pokemon1, pokemon2);
            System.out.println(pokemon1.getName() + " used Fire Attack and dealt " + damage1 + " damage!");
        } else {
            damage1 = pokemon1.performAttack(pokemon2);
            System.out.println(pokemon1.getName() + " attacked and dealt " + damage1 + " damage!");
        }
        
        // Second Pokemon counter-attacks if not fainted
        if (!pokemon2.fainted()) {
            int damage2 = 0;
            if (pokemon2.getType().equals("Fire")) {
                damage2 = fireBehavior.fireAttack(pokemon2, pokemon1);
                System.out.println(pokemon2.getName() + " used Fire Attack and dealt " + damage2 + " damage!");
            } else {
                damage2 = pokemon2.performAttack(pokemon1);
                System.out.println(pokemon2.getName() + " attacked and dealt " + damage2 + " damage!");
            }
        }
        
        // Both Pokemon gain experience (heal a bit) - training shouldn't be too harsh
        pokemon1.heal(Math.max(5, damage1 / 2));
        pokemon2.heal(Math.max(5, damage1 / 2));
        
        // Full heal both Pokemon after training
        pokemon1.fullHeal();
        pokemon2.fullHeal();
        System.out.println("Both Pokemon gained experience from training and are fully healed!");
        
        pressEnter();
    }
    
    private void showPokemonList() {
        for (int i = 0; i < player.getCollectionSize(); i++) {
            Pokemon pokemon = player.getPokemon(i);
            String status = pokemon.fainted() ? " (FAINTED)" : "";
            System.out.println((i + 1) + ". " + pokemon.getName() + 
                             " (HP: " + pokemon.getHP() + "/" + pokemon.getMaxHP() + 
                             ", Type: " + pokemon.getType() + ")" + status);
        }
    }
    
    private void showPokemonListExcluding(int excludeIndex) {
        for (int i = 0; i < player.getCollectionSize(); i++) {
            if (i != excludeIndex) {
                Pokemon pokemon = player.getPokemon(i);
                String status = pokemon.fainted() ? " (FAINTED)" : "";
                System.out.println((i + 1) + ". " + pokemon.getName() + 
                                 " (HP: " + pokemon.getHP() + "/" + pokemon.getMaxHP() + 
                                 ", Type: " + pokemon.getType() + ")" + status);
            }
        }
    }
    
    // These methods were referenced but not used - keeping for potential future use
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
        showPokemonList();
        
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
        System.out.println("Current PokeBalls: " + player.getPokeBallCount("PokeBall"));
        System.out.println();
        System.out.println("PokeBalls cost 10 coins each");
        
        int maxCanBuy = player.getCoins() / 10;
        if (maxCanBuy == 0) {
            System.out.println("You don't have enough coins!");
            pressEnter();
            return;
        }
        
        System.out.println("How many do you want to buy?");
        System.out.println("(You can afford " + maxCanBuy + ")");
        System.out.print("Enter amount (0 to cancel): ");
        
        int amount = getChoice(0, maxCanBuy);
        
        if (amount > 0) {
            if (player.buyPokeBalls("PokeBall", amount)) {
                System.out.println("Bought " + amount + " PokeBalls for " + (amount * 10) + " coins!");
            } else {
                System.out.println("Purchase failed!");
            }
        } else if (amount == 0) {
            System.out.println("Purchase cancelled.");
        }
        
        pressEnter();
    }
    
    private int getChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("Choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    private void pressEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}