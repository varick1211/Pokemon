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
        
       
        if (player.getCollectionSize() > 0) {
            this.playerPokemon = player.getPokemon(0);
            this.playerPokemon.fullHeal();
        } else {
            this.playerPokemon = new Pokemon("Pikachu", "Electric", 5);
            System.out.println("A wild Pikachu appears to help you!");
        }
    }
    
    public void startBattle() {
        System.out.println("\n=== BATTLE START ===");
        System.out.println("Wild " + wildPokemon.getName() + " appeared!");
        System.out.println("Go, " + playerPokemon.getName() + "!");
        
        while (!isGameOver()) {
            showStatus();
            int choice = getPlayerChoice();
            
            if (choice == 1) {
                attack();
            } else if (choice == 2) {
                if (tryToCatch()) {
                    return;
                }
            } else if (choice == 3) {
                System.out.println("You ran away!");
                return;
            }
            
            
            if (!wildPokemon.fainted()) {
                wildPokemonAttack();
            }
        }
        
        
        if (playerPokemon.fainted()) {
            System.out.println("You lost the battle!");
        } else if (wildPokemon.fainted()) {
            System.out.println("You won the battle!");
            player.addScore(wildPokemon.getLevel() * 10);
        }
    }
    
    private void showStatus() {
        System.out.println("\n--- Battle Status ---");
        System.out.printf("Your %s: HP %d/%d\n", 
                         playerPokemon.getName(), 
                         playerPokemon.getHP(), 
                         playerPokemon.getMaxHP());
        System.out.printf("Wild %s: HP %d/%d\n", 
                         wildPokemon.getName(), 
                         wildPokemon.getHP(), 
                         wildPokemon.getMaxHP());
    }
    
    private int getPlayerChoice() {
        System.out.println("\nWhat will you do?");
        System.out.println("1. Attack");
        System.out.println("2. Throw PokeBall");
        System.out.println("3. Run Away");
        
        while (true) {
            try {
                System.out.print("Choice: ");
                int choice = scanner.nextInt();
                if (choice >= 1 && choice <= 3) {
                    return choice;
                }
                System.out.println("Please choose 1, 2, or 3");
            } catch (Exception e) {
                System.out.println("Please enter a number");
                scanner.nextLine();
            }
        }
    }
    
    private void attack() {
        int damage = playerPokemon.performAttack(wildPokemon);
        System.out.printf("%s attacks for %d damage!\n", 
                         playerPokemon.getName(), damage);
        
        if (wildPokemon.fainted()) {
            System.out.printf("Wild %s fainted!\n", wildPokemon.getName());
        }
    }
    
    private boolean tryToCatch() {
        
        if (!player.hasPokeBall("PokeBall")) {
            System.out.println("You don't have any PokeBalls!");
            return false;
        }
        
        player.usePokeBall("PokeBall");
        System.out.println("You threw a PokeBall!");
        
        
        double catchChance = 0.3; 
        double hpPercent = (double) wildPokemon.getHP() / wildPokemon.getMaxHP();
        catchChance += (1.0 - hpPercent) * 0.4; 
        
        if (Math.random() < catchChance) {
            System.out.printf("Gotcha! %s was caught!\n", wildPokemon.getName());
            
            if (player.addPokemon(wildPokemon)) {
                player.addScore(wildPokemon.getLevel() * 20);
                System.out.println("Added to your collection!");
            } else {
                System.out.println("Your Pokemon box is full!");
            }
            return true;
        } else {
            System.out.printf("%s broke free!\n", wildPokemon.getName());
            return false;
        }
    }
    
    private void wildPokemonAttack() {
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
}
	
	


