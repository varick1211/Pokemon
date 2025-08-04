/**
 * Main entry point for Pokemon Ga-Ole Game
 * Initializes the game and starts the main game loop
 * 
 * Pokemon Ga-Ole is a turn-based Pokemon battle and collection game
 * where players can catch Pokemon, battle wild Pokemon, and compete for high scores.
 * 
 * @author Pokemon Ga-Ole Development Team
 * @version 1.0
 */
public class main {
    
    /**
     * Main method - Entry point of the Pokemon Ga-Ole game
     * Initializes game components and starts the game loop
     * 
     * @param args Command line arguments (not used)
     */
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
        System.out.println("Game developed with ❤️ for Pokemon fans");
        System.out.println("========================================");
    }
    
    /**
     * Displays system information for debugging purposes
     * Can be called with command line argument "--debug" or "--info"
     */
    private static void displaySystemInfo() {
        System.out.println("=== SYSTEM INFORMATION ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Operating System: " + System.getProperty("os.name"));
        System.out.println("Available Memory: " + 
            Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
        System.out.println("==========================");
        System.out.println();
    }
    
    /**
     * Alternative main method entry point with command line argument support
     * Supports debug flags and help information
     * 
     * @param args Command line arguments
     */
    public static void mainWithArgs(String[] args) {
        // Check for command line arguments
        if (args.length > 0) {
            for (String arg : args) {
                switch (arg.toLowerCase()) {
                    case "--help":
                    case "-h":
                        displayHelpInformation();
                        return;
                    case "--debug":
                    case "-d":
                        displaySystemInfo();
                        break;
                    case "--version":
                    case "-v":
                        System.out.println("Pokemon Ga-Ole Version 1.0");
                        return;
                    default:
                        System.out.println("Unknown argument: " + arg);
                        System.out.println("Use --help for available options");
                        return;
                }
            }
        }
        
        // Run the normal main method
        main(args);
    }
    
    /**
     * Displays help information for command line usage
     */
    private static void displayHelpInformation() {
        System.out.println("Pokemon Ga-Ole - Command Line Options");
        System.out.println("=====================================");
        System.out.println();
        System.out.println("Usage: java Main [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --help, -h     Show this help message");
        System.out.println("  --version, -v  Show version information");
        System.out.println("  --debug, -d    Show system information");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java Main              Run the game normally");
        System.out.println("  java Main --debug      Run with system info");
        System.out.println("  java Main --help       Show this help");
        System.out.println();
        System.out.println("For more information, visit the game documentation.");
    }
}