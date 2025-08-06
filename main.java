
public class main {
    
   
    public static void main(String[] args) {
        // Display welcome message
        displayWelcomeMessage();
        
        try {
            // Initialize the main game controller
            Game pokemonGame = new Game();
            
            // Start the main game loop
            pokemonGame.startGame();
            
        } catch (Exception e) {
            // Handle any unexpected errors during game initialization or execution
            System.err.println("An unexpected error occurred while starting the game:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\nThe game will now exit. Please try running it again.");
            System.out.println("If the problem persists, check your system requirements.");
        } finally {
            // Cleanup and exit message
            displayExitMessage();
        }
    }
    
    /**
     * Displays the welcome message and game introduction
     */
    private static void displayWelcomeMessage() {
        System.out.println("========================================");
        System.out.println("        WELCOME TO POKEMON GA-OLE!     ");
        System.out.println("========================================");
        System.out.println();
        System.out.println("🔴 Catch 'em all in this exciting");
        System.out.println("   turn-based Pokemon adventure!");
        System.out.println();
        System.out.println("Features:");
        System.out.println("• Battle wild Pokemon");
        System.out.println("• Catch Pokemon with different PokeBalls");
        System.out.println("• Build your Pokemon collection");
        System.out.println("• Compete for high scores");
        System.out.println("• Type effectiveness battles");
        System.out.println();
        System.out.println("Get ready to become the ultimate");
        System.out.println("Pokemon trainer!");
        System.out.println("========================================");
        System.out.println();
        
        // Brief pause for dramatic effect
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Continue if interrupted
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Displays the exit message when the game ends
     */
    private static void displayExitMessage() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("    THANKS FOR PLAYING POKEMON GA-OLE!");
        System.out.println("========================================");
        System.out.println("Hope you enjoyed your Pokemon adventure!");
        System.out.println("Come back soon to catch more Pokemon!");
        System.out.println();
        System.out.println("========================================");
      
    }
    
   
}