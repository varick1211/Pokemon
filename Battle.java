import java.util.*;

public class Battle {
    private Pokemon playerPokemon;
    private Pokemon wildPokemon;
    private Player player;
    private Scanner scanner;
    
    public Battle(Player player, Pokemon wildPokemon) {
        this.player = player;
        this.wildPokemon = wildPokemon;
        this.scanner = new Scanner(System.in);
        
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
        
        boolean battleEnded = false;
        boolean playerRanAway = false;
        boolean pokemonCaught = false;
        
        while (!isGameOver() && !battleEnded) {
            showStatus();
            int choice = getPlayerChoice();
            
            if (choice == 1) {
                attack();
            } else if (choice == 2) {
                if (tryToCatch()) {
                    pokemonCaught = true;
                    battleEnded = true;
                    continue; // Skip wild Pokemon attack
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
        
        // Show battle result summary
        showBattleResult(pokemonCaught, playerRanAway);
        
        // Reset all Pokemon health after battle
        resetAllPokemonHealth();
        System.out.println("Battle ended!");
    }
    
    private void showBattleResult(boolean pokemonCaught, boolean playerRanAway) {
        System.out.println("\n=== BATTLE SUMMARY ===");
        
        if (pokemonCaught) {
            System.out.println("Result: VICTORY (Pokemon Caught)");
        } else if (playerRanAway) {
            System.out.println("Result: ESCAPED (No Record)");
        } else if (playerPokemon.fainted()) {
            System.out.println("Result: DEFEAT");
        } else if (wildPokemon.fainted()) {
            System.out.println("Result: VICTORY (Wild Pokemon Defeated)");
        }
        
        // Show updated battle record
        System.out.println("Battle Record: " + player.getWins() + " Wins, " + 
                          player.getLosses() + " Losses (" + 
                          String.format("%.1f", player.getWinRate()) + "% Win Rate)");
        System.out.println("Total Battles: " + player.getTotalBattles());
    }
    
    private void showStatus() {
        System.out.println("\n--- Battle Status ---");
        System.out.printf("Your %s: HP %d/%d (Level %d)\n", 
                         playerPokemon.getName(), 
                         playerPokemon.getHP(), 
                         playerPokemon.getMaxHP(),
                         playerPokemon.getLevel());
        System.out.printf("Wild %s: HP %d/%d (Level %d)\n", 
                         wildPokemon.getName(), 
                         wildPokemon.getHP(), 
                         wildPokemon.getMaxHP(),
                         wildPokemon.getLevel());
        
        // Show current battle record in status
        System.out.printf("Your Record: %d-%d (%.1f%% Win Rate)\n", 
                         player.getWins(), player.getLosses(), player.getWinRate());
    }
    
    private int getPlayerChoice() {
        System.out.println("\nWhat will you do?");
        System.out.println("1. Attack");
        System.out.println("2. Throw PokeBall (Balls: " + player.getPokeBallCount("PokeBall") + ")");
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
    
    private void attack() {
        if (playerPokemon.fainted()) {
            System.out.println(playerPokemon.getName() + " is too weak to attack!");
            return;
        }
        
        int damage = playerPokemon.performAttack(wildPokemon);
        System.out.printf("%s attacks for %d damage!\n", 
                         playerPokemon.getName(), damage);
        
        if (wildPokemon.fainted()) {
            System.out.printf("Wild %s fainted!\n", wildPokemon.getName());
        }
    }
    
    private boolean tryToCatch() {
        // Check if player has PokeBalls
        if (!player.hasPokeBall("PokeBall")) {
            System.out.println("You don't have any PokeBalls!");
            return false;
        }
        
        // Show catch prediction to help player decide
        showCatchPrediction();
        
        // Use a PokeBall
        player.usePokeBall("PokeBall");
        System.out.println("You threw a PokeBall at " + wildPokemon.getName() + "!");
        
        // Calculate enhanced catch chance
        double catchChance = calculateCatchChance();
        
        // Animated catching sequence
        return performCatchAttempt(catchChance);
    }
    
    private void showCatchPrediction() {
        double hpPercent = (double) wildPokemon.getHP() / wildPokemon.getMaxHP();
        String difficulty;
        
        if (hpPercent > 0.8) {
            difficulty = "Very Hard";
        } else if (hpPercent > 0.6) {
            difficulty = "Hard";
        } else if (hpPercent > 0.4) {
            difficulty = "Medium";
        } else if (hpPercent > 0.2) {
            difficulty = "Easy";
        } else {
            difficulty = "Very Easy";
        }
        
        System.out.println("Catch Difficulty: " + difficulty);
        
        // Show type bonus info
        if (hasTypeAdvantage()) {
            System.out.println("Type advantage bonus active!");
        }
        
        // Show level difference warning
        if (wildPokemon.getLevel() > playerPokemon.getLevel() + 5) {
            System.out.println("Warning: This Pokemon is much higher level!");
        }
    }
    
    private double calculateCatchChance() {
        double baseCatchChance = 0.25; // 25% base chance
        
        // HP-based bonus (lower HP = easier to catch)
        double hpPercent = (double) wildPokemon.getHP() / wildPokemon.getMaxHP();
        double hpBonus = (1.0 - hpPercent) * 0.6; // Up to 60% bonus when at 1 HP
        
        // Level difference penalty/bonus
        int levelDiff = wildPokemon.getLevel() - playerPokemon.getLevel();
        double levelModifier = 0;
        if (levelDiff > 0) {
            // Harder to catch higher level Pokemon
            levelModifier = -Math.min(levelDiff * 0.04, 0.4); // Max 40% penalty
        } else if (levelDiff < 0) {
            // Easier to catch lower level Pokemon
            levelModifier = Math.min(Math.abs(levelDiff) * 0.02, 0.2); // Max 20% bonus
        }
        
        // Type advantage bonus (if your Pokemon has type advantage)
        double typeBonus = hasTypeAdvantage() ? 0.15 : 0.0; // 15% bonus
        
        // Status condition bonus (if wild Pokemon is at very low HP)
        double statusBonus = (hpPercent < 0.1) ? 0.2 : 0.0; // 20% bonus when critically low
        
        // Calculate final catch rate
        double finalCatchChance = baseCatchChance + hpBonus + levelModifier + typeBonus + statusBonus;
        
        // Ensure catch rate is between 5% and 95%
        return Math.max(0.05, Math.min(0.95, finalCatchChance));
    }
    
    private boolean hasTypeAdvantage() {
        String playerType = playerPokemon.getType();
        String wildType = wildPokemon.getType();
        
        // Simple type advantage system
        return (playerType.equals("Water") && wildType.equals("Fire")) ||
               (playerType.equals("Fire") && wildType.equals("Grass")) ||
               (playerType.equals("Grass") && wildType.equals("Water")) ||
               (playerType.equals("Electric") && wildType.equals("Water"));
    }
    
    private boolean performCatchAttempt(double catchChance) {
        // Multi-stage catching animation
        System.out.println("The PokeBall flies through the air...");
        
        try {
            Thread.sleep(500); // Brief pause for drama
        } catch (InterruptedException e) {
            // Continue without pause if interrupted
        }
        
        System.out.println(wildPokemon.getName() + " is absorbed into the PokeBall!");
        System.out.print("PokeBall is shaking");
        
        // Shaking animation
        int shakes = 0;
        int maxShakes = 3;
        
        for (int i = 0; i < maxShakes; i++) {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                // Continue without pause if interrupted
            }
            
            System.out.print(".");
            
            // Each shake has a chance to succeed based on catch rate
            double shakeChance = Math.pow(catchChance, 1.0 / maxShakes);
            if (Math.random() < shakeChance) {
                shakes++;
            } else {
                break; // Pokemon breaks free
            }
        }
        
        System.out.println(); // New line after shaking
        
        if (shakes >= maxShakes) {
            // Successfully caught!
            System.out.println("*Click*");
            System.out.printf("Gotcha! %s was caught!\n", wildPokemon.getName());
            
            // Show catch statistics
            showCatchStats();
            
            if (player.addPokemon(wildPokemon)) {
                int scoreBonus = wildPokemon.getLevel() * 25; // Increased bonus
                player.addScore(scoreBonus);
                System.out.println("Added to your collection! Bonus: " + scoreBonus + " points!");
                
                // Check if this is a new species
                if (isNewSpecies()) {
                    int discoveryBonus = 100;
                    player.addScore(discoveryBonus);
                    System.out.println("New species discovered! Discovery bonus: " + discoveryBonus + " points!");
                }
                
                // Reset all Pokemon health when a new Pokemon is caught
                resetAllPokemonHealth();
            } else {
                System.out.println("Your Pokemon collection is full! " + wildPokemon.getName() + " was released.");
            }
            return true;
        } else {
            // Pokemon broke free
            System.out.printf("%s broke free", wildPokemon.getName());
            
            if (shakes == 0) {
                System.out.println(" immediately!");
            } else if (shakes == 1) {
                System.out.println(" after one shake!");
            } else {
                System.out.println(" after " + shakes + " shakes! So close!");
            }
            
            // Pokemon becomes slightly more agitated (harder to catch next time)
            if (Math.random() < 0.3) {
                System.out.println(wildPokemon.getName() + " looks more alert now!");
            }
            
            return false;
        }
    }
    
    private void showCatchStats() {
        System.out.println("--- Catch Data ---");
        System.out.printf("Species: %s | Level: %d | Type: %s\n", 
                         wildPokemon.getName(), wildPokemon.getLevel(), wildPokemon.getType());
        System.out.printf("HP when caught: %d/%d\n", wildPokemon.getHP(), wildPokemon.getMaxHP());
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
        
        int damage = wildPokemon.performAttack(playerPokemon);
        System.out.printf("Wild %s attacks for %d damage!\n", 
                         wildPokemon.getName(), damage);
        
        if (playerPokemon.fainted()) {
            System.out.printf("%s fainted!\n", playerPokemon.getName());
        }
    }
    
    private boolean isGameOver() {
        return playerPokemon.fainted() || wildPokemon.fainted();
    }
    
    // Reset health for all Pokemon in player's collection
    private void resetAllPokemonHealth() {
        for (int i = 0; i < player.getCollectionSize(); i++) {
            Pokemon pokemon = player.getPokemon(i);
            pokemon.fullHeal();
        }
        System.out.println("All your Pokemon have been fully healed!");
    }
}