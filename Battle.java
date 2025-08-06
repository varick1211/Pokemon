import java.util.*;

public class Battle {
    private Pokemon playerPokemon;
    private Pokemon wildPokemon;
    private Player player;
    private Scanner scanner;
    private FireTypePokemonBehavior fireTypeBehavior;
    private ElectricTypePokemonBehavior electricTypeBehavior;
    private GrassTypePokemonBehavior grassTypeBehavior;
    private WaterTypePokemonBehavior waterTypeBehavior;
    
    public Battle(Player player, Pokemon wildPokemon) {
        this.player = player;
        this.wildPokemon = wildPokemon;
        this.scanner = new Scanner(System.in);
        this.fireTypeBehavior = new FireTypePokemonBehavior();
        this.electricTypeBehavior = new ElectricTypePokemonBehavior();
        this.grassTypeBehavior = new GrassTypePokemonBehavior();
        this.waterTypeBehavior = new WaterTypePokemonBehavior();
        
        // Select player's Pokemon for battle
        if (player.getCollectionSize() > 0) {
            // Find first non-fainted Pokemon
            Pokemon selectedPokemon = null;
            for (int i = 0; i < player.getCollectionSize(); i++) {
                Pokemon pokemon = player.getPokemon(i);
                if (!pokemon.fainted()) {
                    selectedPokemon = pokemon;
                    break;
                }
            }
            
            if (selectedPokemon != null) {
                this.playerPokemon = selectedPokemon;
                // Don't automatically full heal - keep current HP for strategy
                System.out.println("Go, " + playerPokemon.getName() + "!");
            } else {
                // All Pokemon are fainted, create emergency Pokemon
                this.playerPokemon = new Pokemon("Pikachu", "Electric", 5);
                System.out.println("All your Pokemon are fainted! A wild Pikachu appears to help you!");
            }
        } else {
            // No Pokemon in collection, create emergency Pokemon
            this.playerPokemon = new Pokemon("Pikachu", "Electric", 5);
            System.out.println("A wild Pikachu appears to help you!");
        }
    }
    
    public void startBattle() {
        System.out.println("\n=== BATTLE START ===");
        System.out.println("Wild " + wildPokemon.getName() + " appeared!");
        showBattleStatus();
        
        boolean battleEnded = false;
        boolean playerRanAway = false;
        boolean pokemonCaught = false;
        
        while (!isGameOver() && !battleEnded) {
            // Process status effects at the start of each turn
            processStatusEffects();
            
            int choice = getPlayerChoice();
            
            if (choice == 1) {
                attack();
            } else if (choice == 2) {
                String selectedBall = selectPokeBall();
                if (selectedBall != null) {
                    if (tryToCatch(selectedBall)) {
                        pokemonCaught = true;
                        battleEnded = true;
                        continue; // Skip wild Pokemon attack
                    }
                }
            } else if (choice == 3) {
                System.out.println("You ran away safely!");
                playerRanAway = true;
                battleEnded = true;
                continue; // Skip wild Pokemon attack
            }
            
            // Wild Pokemon attacks back if it's still alive and player didn't run or catch
            if (!wildPokemon.fainted() && !battleEnded) {
                wildPokemonAttack();
            }
            
            // Show HP after each round of attacks
            if (!battleEnded) {
                showBattleStatus();
            }
            
            // Check if player Pokemon fainted after wild Pokemon's attack
            if (playerPokemon.fainted()) {
                battleEnded = true;
            }
        }
        
        // Battle aftermath and win/loss tracking
        if (pokemonCaught) {
            // Catching a Pokemon counts as a win
            System.out.println("\nVictory! You successfully caught " + wildPokemon.getName() + "!");
            player.recordWin();
            
            // Additional catch bonus (beyond the points already given in tryToCatch)
            int catchBonus = wildPokemon.getLevel() * 5;
            player.earnCoins(catchBonus);
            System.out.println("Catch victory bonus: " + catchBonus + " coins!");
            
        } else if (playerRanAway) {
            // Running away doesn't count as win or loss
            System.out.println("\nYou escaped safely. No battle record.");
            
        } else if (playerPokemon.fainted()) {
            // Player's Pokemon fainted = loss
            System.out.println("\n" + playerPokemon.getName() + " fainted! You lost the battle!");
            player.recordLoss();
            
            // Small consolation prize for trying
            int consolationCoins = Math.max(1, wildPokemon.getLevel() / 2);
            player.earnCoins(consolationCoins);
            System.out.println("Better luck next time! Consolation: " + consolationCoins + " coins.");
            
        } else if (wildPokemon.fainted()) {
            // Wild Pokemon fainted = win
            System.out.println("\nYou won the battle!");
            player.recordWin();
            
            // Victory rewards (these were already in the original code)
            int scoreGain = wildPokemon.getLevel() * 10;
            int coinGain = wildPokemon.getLevel() * 2;
            player.addScore(scoreGain);
            player.earnCoins(coinGain);
            System.out.println("Victory rewards: " + scoreGain + " points and " + coinGain + " coins!");
        }
        
        // Clear status effects after battle
        clearStatusEffects();
        
        System.out.println("Battle ended!");
    }

    private void processStatusEffects() {
        // Process player Pokemon status effects
        processStatusEffect(playerPokemon, "Player");
        
        // Process wild Pokemon status effects
        processStatusEffect(wildPokemon, "Wild");
    }
    
    private void processStatusEffect(Pokemon pokemon, String owner) {
        String status = pokemon.getStatus();
        
        if (status.equals("burned")) {
            int burnDamage = pokemon.getMaxHP() / 8; // 1/8 of max HP
            burnDamage = Math.max(1, burnDamage);
            pokemon.takeDamage(burnDamage);
            System.out.println(owner + " " + pokemon.getName() + " is hurt by burn! (" + burnDamage + " damage)");
            
        } else if (status.equals("poisoned")) {
            int poisonDamage = pokemon.getMaxHP() / 8; // 1/8 of max HP
            poisonDamage = Math.max(1, poisonDamage);
            pokemon.takeDamage(poisonDamage);
            System.out.println(owner + " " + pokemon.getName() + " is hurt by poison! (" + poisonDamage + " damage)");
            
        } else if (status.equals("paralyzed")) {
            // Paralyzed Pokemon have a chance to be unable to move
            Random random = new Random();
            if (random.nextInt(100) < 25) { // 25% chance to be fully paralyzed
                System.out.println(owner + " " + pokemon.getName() + " is fully paralyzed and can't move!");
                // This will be handled in the attack methods
            }
        } else if (status.equals("soaked")) {
            System.out.println(owner + " " + pokemon.getName() + " is dripping wet from being soaked!");
            // Soaked Pokemon might take extra damage from Electric attacks
        }
    }
    
    private void clearStatusEffects() {
        // Clear status effects after battle
        if (!playerPokemon.getStatus().equals("normal")) {
            System.out.println(playerPokemon.getName() + "'s status condition cleared!");
            playerPokemon.setStatus("normal");
        }
        
        if (!wildPokemon.getStatus().equals("normal")) {
            System.out.println("Wild " + wildPokemon.getName() + "'s status condition cleared!");
            wildPokemon.setStatus("normal");
        }
    }

    private void showBattleStatus() {
        System.out.println("\n--- BATTLE STATUS ---");
        System.out.println("Your " + playerPokemon.getName() + ": " + 
                          playerPokemon.getHP() + "/" + playerPokemon.getMaxHP() + " HP" +
                          " [Status: " + playerPokemon.getStatus() + "]");
        System.out.println("Wild " + wildPokemon.getName() + ": " + 
                          wildPokemon.getHP() + "/" + wildPokemon.getMaxHP() + " HP" +
                          " [Status: " + wildPokemon.getStatus() + "]");
        System.out.println("--------------------");
    }

    private int getPlayerChoice() {
        System.out.println("\nWhat will you do?");
        System.out.println("1. Attack");
        System.out.println("2. Use PokeBall");
        System.out.println("3. Run Away");
        
        while (true) {
            try {
                System.out.print("Choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                if (choice >= 1 && choice <= 3) {
                    return choice;
                }
                System.out.println("Please choose 1, 2, or 3");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    private String selectPokeBall() {
        System.out.println("\n=== SELECT POKEBALL ===");
        
        // Get all available ball types
        String[] ballTypes = PokeBall.getAllBallTypes();
        List<String> availableBalls = new ArrayList<>();
        
        // Show only balls that player has
        for (String ballType : ballTypes) {
            if (player.hasPokeBall(ballType)) {
                availableBalls.add(ballType);
                PokeBall ball = PokeBall.getPokeBallByName(ballType);
                System.out.println((availableBalls.size()) + ". " + ballType + 
                                 " (x" + player.getPokeBallCount(ballType) + ") - " +
                                 ball.getCatchProbability(wildPokemon));
            }
        }
        
        if (availableBalls.isEmpty()) {
            System.out.println("You don't have any PokeBalls!");
            return null;
        }
        
        System.out.println((availableBalls.size() + 1) + ". Cancel");
        
        while (true) {
            try {
                System.out.print("Select PokeBall: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                if (choice == availableBalls.size() + 1) {
                    return null; // Cancel
                }
                
                if (choice >= 1 && choice <= availableBalls.size()) {
                    return availableBalls.get(choice - 1);
                }
                
                System.out.println("Please choose a valid option");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    private void attack() {
        if (playerPokemon.fainted()) {
            System.out.println(playerPokemon.getName() + " is too weak to attack!");
            return;
        }
        
        // Check if Pokemon is paralyzed and can't move
        if (playerPokemon.getStatus().equals("paralyzed")) {
            Random random = new Random();
            if (random.nextInt(100) < 25) { // 25% chance to be unable to move
                System.out.println(playerPokemon.getName() + " is paralyzed and can't move!");
                return;
            }
        }
        
        int damage = executeTypeSpecificAttack(playerPokemon, wildPokemon);
        
        if (wildPokemon.fainted()) {
            System.out.printf("Wild %s fainted!\n", wildPokemon.getName());
        }
    }
    
    private int executeTypeSpecificAttack(Pokemon attacker, Pokemon target) {
        String attackerType = attacker.getType().toLowerCase();
        
        switch (attackerType) {
            case "fire":
                return fireTypeBehavior.fireAttack(attacker, target);
            case "electric":
                return electricTypeBehavior.electricAttack(attacker, target);
            case "grass":
                return grassTypeBehavior.grassAttack(attacker, target);
            case "water":
                return waterTypeBehavior.waterAttack(attacker, target);
            default:
                // Use regular attack for other types
                int damage = attacker.performAttack(target);
                System.out.printf("%s attacks for %d damage!\n", attacker.getName(), damage);
                return damage;
        }
    }
    
    private boolean tryToCatch(String ballType) {
        // Check if player has the selected PokeBall
        if (!player.hasPokeBall(ballType)) {
            System.out.println("You don't have any " + ballType + "s!");
            return false;
        }
        
        // Get the PokeBall object
        PokeBall pokeBall = PokeBall.getPokeBallByName(ballType);
        
        // Show catch prediction
        System.out.println("Using " + ballType + " - " + pokeBall.getCatchProbability(wildPokemon));
        
        // Use the PokeBall
        player.usePokeBall(ballType);
        System.out.println("You threw a " + ballType + " at " + wildPokemon.getName() + "!");
        
        // Perform catch attempt using PokeBall class
        boolean caughtSuccessfully = performEnhancedCatchAttempt(pokeBall);
        
        if (caughtSuccessfully) {
            System.out.println(pokeBall.getCatchMessage(wildPokemon));
            
            // Show catch statistics
            showCatchStats();
            
            if (player.addPokemon(wildPokemon)) {
                int scoreBonus = wildPokemon.getLevel() * 25;
                player.addScore(scoreBonus);
                System.out.println("Added to your collection! Bonus: " + scoreBonus + " points!");
                
                // Check if this is a new species
                if (isNewSpecies()) {
                    int discoveryBonus = 100;
                    player.addScore(discoveryBonus);
                    System.out.println("New species discovered! Discovery bonus: " + discoveryBonus + " points!");
                }
                
                
            } else {
                System.out.println("Your Pokemon collection is full! " + wildPokemon.getName() + " was released.");
            }
            return true;
        } else {
            System.out.println(pokeBall.getFailMessage(wildPokemon));
            return false;
        }
    }
    
    private boolean performEnhancedCatchAttempt(PokeBall pokeBall) {
        // Multi-stage catching animation
        System.out.println("The " + pokeBall.getName() + " flies through the air...");
        
        try {
            Thread.sleep(500); // Brief pause for drama
        } catch (InterruptedException e) {
            // Continue without pause if interrupted
        }
        
        System.out.println(wildPokemon.getName() + " is absorbed into the " + pokeBall.getName() + "!");
        
        // Special handling for Master Ball
        if (pokeBall.getName().equals("Master Ball")) {
            System.out.println("*Click*");
            System.out.println("Master Ball never fails!");
            return true;
        }
        
        System.out.print(pokeBall.getName() + " is shaking");
        
        // Use PokeBall's attempt catch method
        boolean success = pokeBall.attemptCatch(wildPokemon);
        
        // Shaking animation based on ball type
        int shakes = getShakeCount(pokeBall, success);
        
        for (int i = 0; i < shakes; i++) {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                // Continue without pause if interrupted
            }
            System.out.print(".");
        }
        
        System.out.println(); // New line after shaking
        
        if (success) {
            System.out.println("*Click*");
            System.out.printf("Gotcha! %s was caught!\n", wildPokemon.getName());
        }
        
        return success;
    }
    
    private int getShakeCount(PokeBall pokeBall, boolean success) {
        // Different balls have different shake patterns
        String ballName = pokeBall.getName();
        
        if (success) {
            // Successful catches always shake 3 times
            return 3;
        } else {
            // Failed catches shake fewer times based on ball quality
            switch (ballName) {
                case "Poke Ball":
                    return Math.random() < 0.7 ? 1 : 2;
                case "Great Ball":
                    return Math.random() < 0.5 ? 2 : 3;
                case "Ultra Ball":
                    return Math.random() < 0.3 ? 2 : 3;
                default:
                    return 1;
            }
        }
    }
    
    private void showCatchStats() {
        System.out.println("--- Catch Data ---");
        System.out.printf("Species: %s | Level: %d | Type: %s\n", 
                         wildPokemon.getName(), wildPokemon.getLevel(), wildPokemon.getType());
        System.out.printf("HP when caught: %d/%d | Status: %s\n", 
                         wildPokemon.getHP(), wildPokemon.getMaxHP(), wildPokemon.getStatus());
    }
    
    private boolean isNewSpecies() {
        // Check if player already has this species
        for (int i = 0; i < player.getCollectionSize() - 1; i++) { // -1 because we just added the Pokemon
            Pokemon existing = player.getPokemon(i);
            if (existing.getName().equals(wildPokemon.getName())) {
                return false;
            }
        }
        return true;
    }
    
    private void wildPokemonAttack() {
        if (wildPokemon.fainted()) {
            return; // Can't attack if fainted
        }
        
        // Check if Pokemon is paralyzed and can't move
        if (wildPokemon.getStatus().equals("paralyzed")) {
            Random random = new Random();
            if (random.nextInt(100) < 25) { // 25% chance to be unable to move
                System.out.println("Wild " + wildPokemon.getName() + " is paralyzed and can't move!");
                return;
            }
        }
        
        int damage = executeTypeSpecificAttack(wildPokemon, playerPokemon);
        
        if (playerPokemon.fainted()) {
            System.out.printf("%s fainted!\n", playerPokemon.getName());
        }
    }
    
    private boolean isGameOver() {
        return playerPokemon.fainted() || wildPokemon.fainted();
    }
}