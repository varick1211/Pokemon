import java.util.*;



public class Game {
    private Player player;
    private Scanner scanner;
    private boolean gameRunning;
    private Random random;
    
    public Game() {
        this.scanner = new Scanner(System.in);
        this.gameRunning = true;
        this.random = new Random();
    }
    
    public void startGame() {
        showWelcome();
        setupPlayer();
        
        while (gameRunning) {
            showMainMenu();
            int choice = getChoice(1, 4);
            handleChoice(choice);
        }
        
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
    }
    
    private void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("Score: " + player.getScore() + " | Coins: " + player.getCoins());
        System.out.println("Pokemon: " + player.getCollectionSize());
        System.out.println();
        System.out.println("1. Explore (Find Pokemon)");
        System.out.println("2. View Pokemon");
        System.out.println("3. Buy PokeBalls");
        System.out.println("4. Quit Game");
    }
    
    private void handleChoice(int choice) {
        if (choice == 1) {
            explore();
        } else if (choice == 2) {
            viewPokemon();
        } else if (choice == 3) {
            buyPokeBalls();
        } else if (choice == 4) {
            gameRunning = false;
        }
    }
    
    private void explore() {
        System.out.println("\nExploring...");
        
        
        if (random.nextDouble() < 0.7) {
            Pokemon wildPokemon = createRandomPokemon();
            System.out.println("You found a wild " + wildPokemon.getName() + "!");
            
            Battle battle = new Battle(player, wildPokemon);
            battle.startBattle();
        } else {
            System.out.println("No Pokemon found, but you found some coins!");
            int coins = random.nextInt(15) + 5; // 5-19 coins
            player.earnCoins(coins);
            System.out.println("Found " + coins + " coins!");
        }
        
        pressEnter();
    }
    
    private Pokemon createRandomPokemon() {
        String[] names = {"Pikachu", "Eevee", "Geodude", "Zubat", "Rattata", 
                         "Pidgey", "Caterpie", "Weedle", "Magikarp", "Psyduck"};
        String[] types = {"Electric", "Normal", "Rock", "Flying", "Normal", 
                         "Flying", "Bug", "Bug", "Water", "Water"};
        
        int index = random.nextInt(names.length);
        int level = random.nextInt(10) + 1; 
        
        return new Pokemon(names[index], types[index], level);
    }
    
    private void viewPokemon() {
        System.out.println("\n=== YOUR POKEMON ===");
        
        if (player.getCollectionSize() == 0) {
            System.out.println("You don't have any Pokemon yet!");
        } else {
            for (int i = 0; i < player.getCollectionSize(); i++) {
                System.out.println((i + 1) + ". " + player.getPokemon(i));
            }
        }
        
        pressEnter();
    }
    
    private void buyPokeBalls() {
        System.out.println("\n=== POKEBALL SHOP ===");
        System.out.println("Your coins: " + player.getCoins());
        System.out.println("Current PokeBalls: " + player.getPokeBallCount("PokeBall"));
        System.out.println();
        System.out.println("PokeBalls cost 10 coins each");
        System.out.println("How many do you want to buy?");
        System.out.println("(You can afford " + (player.getCoins() / 10) + ")");
        
        int maxCanBuy = player.getCoins() / 10;
        if (maxCanBuy == 0) {
            System.out.println("You don't have enough coins!");
            pressEnter();
            return;
        }
        
        System.out.print("Enter amount (0 to cancel): ");
        int amount = getChoice(0, maxCanBuy);
        
        if (amount > 0) {
            if (player.buyPokeBalls("PokeBall", amount)) {
                System.out.println("Bought " + amount + " PokeBalls for " + (amount * 10) + " coins!");
            } else {
                System.out.println("Purchase failed!");
            }
        }
        
        pressEnter();
    }
    
    private int getChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("Choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); 
                
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (Exception e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine();
            }
        }
    }
    
    private void pressEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}