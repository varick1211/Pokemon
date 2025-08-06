import java.util.Scanner;
import java.util.Random;

public class BattleTraining {
    private Random random;
    
    public BattleTraining() {
        this.random = new Random();
    }
    
    // Updated method signature to accept all type behaviors
    public void startTraining(Player player, Scanner scanner, 
                            FireTypePokemonBehavior fireBehavior,
                            ElectricTypePokemonBehavior electricBehavior,
                            GrassTypePokemonBehavior grassBehavior,
                            WaterTypePokemonBehavior waterBehavior) {
        
        System.out.println("\n=== BATTLE TRAINING ===");
        System.out.println("Train your Pokemon against each other to improve their skills!");

        if (player.getCollectionSize() < 2) {
            System.out.println("You need at least 2 Pokemon to train!");
            GameUtils.pressEnter(scanner);
            return;
        }

        // Show training options
        System.out.println("Training Options:");
        System.out.println("1. Quick Training (1 round)");
        System.out.println("2. Extended Training (3 rounds)");
        System.out.println("3. Intensive Training (5 rounds)");
        System.out.println("4. Cancel");
        
        int trainingType = GameUtils.getChoice(1, 4, scanner);
        if (trainingType == 4) return;
        
        int rounds = (trainingType == 1) ? 1 : (trainingType == 2) ? 3 : 5;

        // Select first Pokemon
        System.out.println("Choose your first Pokemon:");
        displayPokemonList(player);

        int choice1 = GameUtils.getChoice(1, player.getCollectionSize(), scanner);
        Pokemon pokemon1 = player.getPokemon(choice1 - 1);

        // Select second Pokemon
        System.out.println("Choose your second Pokemon:");
        displayPokemonListExcluding(player, choice1 - 1);

        int choice2 = GameUtils.getChoice(1, player.getCollectionSize(), scanner);
        while (choice2 == choice1) {
            System.out.println("Choose a different Pokemon!");
            choice2 = GameUtils.getChoice(1, player.getCollectionSize(), scanner);
        }
        Pokemon pokemon2 = player.getPokemon(choice2 - 1);

        // Store original HP for restoration later
        int originalHP1 = pokemon1.getHP();
        int originalHP2 = pokemon2.getHP();
        String originalStatus1 = pokemon1.getStatus();
        String originalStatus2 = pokemon2.getStatus();

        // Start training session
        System.out.println("\n=== TRAINING SESSION START ===");
        System.out.println(pokemon1.getName() + " (" + pokemon1.getType() + ") vs " + 
                          pokemon2.getName() + " (" + pokemon2.getType() + ")!");
        System.out.println("Training for " + rounds + " round(s)...\n");

        int totalDamageDealt = 0;
        int totalExperience = 0;

        for (int round = 1; round <= rounds; round++) {
            System.out.println("--- Round " + round + " ---");
            showTrainingStatus(pokemon1, pokemon2);
            
            // Pokemon 1 attacks Pokemon 2
            int damage1to2 = executeTrainingAttack(pokemon1, pokemon2, 
                                                  fireBehavior, electricBehavior, 
                                                  grassBehavior, waterBehavior);
            totalDamageDealt += damage1to2;
            
            // Small pause for dramatic effect
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            
            // Pokemon 2 attacks Pokemon 1 (if still able)
            if (!pokemon2.fainted()) {
                int damage2to1 = executeTrainingAttack(pokemon2, pokemon1, 
                                                      fireBehavior, electricBehavior, 
                                                      grassBehavior, waterBehavior);
                totalDamageDealt += damage2to1;
            }
            
            // Process status effects during training
            processTrainingStatusEffects(pokemon1, pokemon2);
            
            System.out.println("Round " + round + " complete!\n");
            
            // Brief pause between rounds
            if (round < rounds) {
                try { Thread.sleep(1500); } catch (InterruptedException e) {}
            }
        }

        // Training results
        System.out.println("=== TRAINING COMPLETE ===");
        System.out.println("Training Results:");
        System.out.println("- Total damage dealt: " + totalDamageDealt);
        System.out.println("- Rounds completed: " + rounds);
        
        // Calculate experience and rewards
        int baseExperience = rounds * 10;
        int bonusExperience = Math.min(totalDamageDealt / 5, 50); // Bonus based on damage
        totalExperience = baseExperience + bonusExperience;
        
        // Award medals based on training intensity and performance
        int medalsEarned = calculateTrainingMedals(rounds, totalDamageDealt, 
                                                  pokemon1.getLevel() + pokemon2.getLevel());
        
        // Restore Pokemon to pre-training state (this is training, not real battle)
        restorePokemon(pokemon1, originalHP1, originalStatus1);
        restorePokemon(pokemon2, originalHP2, originalStatus2);
        
        // Apply training benefits
        applyTrainingBenefits(pokemon1, pokemon2, totalExperience);
        
        // Award medals
        player.updateDatabaseMedals(medalsEarned);
        
        System.out.println("\nTraining Benefits:");
        System.out.println("- " + pokemon1.getName() + " gained " + totalExperience + " experience!");
        System.out.println("- " + pokemon2.getName() + " gained " + totalExperience + " experience!");
        System.out.println("- Both Pokemon restored to full health!");
        System.out.println("- Earned " + medalsEarned + " Gaole Medals!");
        
        // Chance for additional rewards
        if (random.nextDouble() < 0.3) { // 30% chance
            int bonusCoins = random.nextInt(10) + 5;
            player.earnCoins(bonusCoins);
            System.out.println("- Training bonus: " + bonusCoins + " coins!");
        }

        GameUtils.pressEnter(scanner);
    }
    
    private void displayPokemonList(Player player) {
        for (int i = 0; i < player.getCollectionSize(); i++) {
            Pokemon pokemon = player.getPokemon(i);
            System.out.println((i + 1) + ". " + pokemon.getName() +
                             " (HP: " + pokemon.getHP() + "/" + pokemon.getMaxHP() +
                             ", Type: " + pokemon.getType() + 
                             ", Level: " + pokemon.getLevel() +
                             ", Status: " + pokemon.getStatus() + ")");
        }
    }
    
    private void displayPokemonListExcluding(Player player, int excludeIndex) {
        for (int i = 0; i < player.getCollectionSize(); i++) {
            if (i != excludeIndex) {
                Pokemon pokemon = player.getPokemon(i);
                System.out.println((i + 1) + ". " + pokemon.getName() +
                                 " (HP: " + pokemon.getHP() + "/" + pokemon.getMaxHP() +
                                 ", Type: " + pokemon.getType() + 
                                 ", Level: " + pokemon.getLevel() +
                                 ", Status: " + pokemon.getStatus() + ")");
            }
        }
    }
    
    private void showTrainingStatus(Pokemon pokemon1, Pokemon pokemon2) {
        System.out.println(pokemon1.getName() + ": " + pokemon1.getHP() + "/" + 
                          pokemon1.getMaxHP() + " HP [" + pokemon1.getStatus() + "]");
        System.out.println(pokemon2.getName() + ": " + pokemon2.getHP() + "/" + 
                          pokemon2.getMaxHP() + " HP [" + pokemon2.getStatus() + "]");
        System.out.println();
    }
    
    private int executeTrainingAttack(Pokemon attacker, Pokemon target,
                                    FireTypePokemonBehavior fireBehavior,
                                    ElectricTypePokemonBehavior electricBehavior,
                                    GrassTypePokemonBehavior grassBehavior,
                                    WaterTypePokemonBehavior waterBehavior) {
        
        if (attacker.fainted()) {
            System.out.println(attacker.getName() + " is too weak to continue training!");
            return 0;
        }
        
        // Check paralysis
        if (attacker.getStatus().equals("paralyzed") && random.nextInt(100) < 25) {
            System.out.println(attacker.getName() + " is paralyzed and can't attack!");
            return 0;
        }
        
        String attackerType = attacker.getType().toLowerCase();
        int damage = 0;
        
        switch (attackerType) {
            case "fire":
                damage = fireBehavior.fireAttack(attacker, target);
                break;
            case "electric":
                damage = electricBehavior.electricAttack(attacker, target);
                break;
            case "grass":
                damage = grassBehavior.grassAttack(attacker, target);
                break;
            case "water":
                damage = waterBehavior.waterAttack(attacker, target);
                break;
            default:
                damage = attacker.performAttack(target);
                System.out.println(attacker.getName() + " used a basic attack for " + damage + " damage!");
                break;
        }
        
        return damage;
    }
    
    private void processTrainingStatusEffects(Pokemon pokemon1, Pokemon pokemon2) {
        processStatusEffect(pokemon1);
        processStatusEffect(pokemon2);
    }
    
    private void processStatusEffect(Pokemon pokemon) {
        String status = pokemon.getStatus();
        
        if (status.equals("burned")) {
            int burnDamage = Math.max(1, pokemon.getMaxHP() / 16); // Reduced damage in training
            pokemon.takeDamage(burnDamage);
            System.out.println(pokemon.getName() + " is hurt by burn! (" + burnDamage + " damage)");
            
        } else if (status.equals("poisoned")) {
            int poisonDamage = Math.max(1, pokemon.getMaxHP() / 16); // Reduced damage in training
            pokemon.takeDamage(poisonDamage);
            System.out.println(pokemon.getName() + " is hurt by poison! (" + poisonDamage + " damage)");
        }
    }
    
    private int calculateTrainingMedals(int rounds, int totalDamage, int combinedLevel) {
        int baseMedals = rounds * 50; // Base medals per round
        int damageBonus = Math.min(totalDamage / 10, 100); // Bonus for damage dealt
        int levelBonus = combinedLevel * 2; // Bonus based on Pokemon levels
        
        return baseMedals + damageBonus + levelBonus;
    }
    
    private void restorePokemon(Pokemon pokemon, int originalHP, String originalStatus) {
        // Restore to original state
        pokemon.heal(pokemon.getMaxHP()); // Full heal first
        int currentHP = pokemon.getHP();
        if (currentHP > originalHP) {
            // Take damage to restore original HP
            pokemon.takeDamage(currentHP - originalHP);
        }
        pokemon.setStatus(originalStatus);
    }
    
    private void applyTrainingBenefits(Pokemon pokemon1, Pokemon pokemon2, int experience) {
        // Both Pokemon gain some permanent benefit from training
        // This could be implemented as stat boosts, HP increases, etc.
        // For now, we'll just heal them and give a small temporary boost
        
        pokemon1.heal(pokemon1.getMaxHP());
        pokemon2.heal(pokemon2.getMaxHP());
        
        // Clear any status effects from training
        pokemon1.setStatus("normal");
        pokemon2.setStatus("normal");
        
        // Could add level-up mechanics here based on experience gained
        if (experience > 50) {
            System.out.println("Intensive training session! Both Pokemon feel stronger!");
        } else if (experience > 25) {
            System.out.println("Good training session! Both Pokemon learned something new!");
        } else {
            System.out.println("Basic training completed. Every bit helps!");
        }
    }
}