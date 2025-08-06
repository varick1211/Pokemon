import java.io.*;
import java.util.*;

public class PokemonStorage {
    private static final String POKEMON_FILE = "resources/pokemon_collections.txt";

    // Constructor - creates file if it doesn't exist
    public PokemonStorage() {
        createPokemonFile();
    }

    /**
     * Save player's Pokemon collection to file
     */
    public boolean savePokemonCollection(int playerID, List<Pokemon> pokemonList) {
        try {
            // First, load all existing data
            Map<Integer, List<String>> allCollections = loadAllCollections();
            
            // Update this player's collection
            List<String> pokemonData = new ArrayList<>();
            for (Pokemon pokemon : pokemonList) {
                if (pokemon != null) {
                    pokemonData.add(formatPokemonData(pokemon));
                }
            }
            allCollections.put(playerID, pokemonData);
            
            // Write all data back to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(POKEMON_FILE))) {
                for (Map.Entry<Integer, List<String>> entry : allCollections.entrySet()) {
                    int id = entry.getKey();
                    List<String> collection = entry.getValue();
                    
                    writer.println("PLAYER_" + id + "_START");
                    for (String pokemonLine : collection) {
                        writer.println(pokemonLine);
                    }
                    writer.println("PLAYER_" + id + "_END");
                }
            }
            
            System.out.println("Pokemon collection saved! (" + pokemonList.size() + " Pokemon)");
            return true;
            
        } catch (IOException e) {
            System.err.println("Error saving Pokemon collection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load player's Pokemon collection from file
     */
    public List<Pokemon> loadPokemonCollection(int playerID) {
        List<Pokemon> pokemonList = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(POKEMON_FILE))) {
            String line;
            boolean inPlayerSection = false;
            String startMarker = "PLAYER_" + playerID + "_START";
            String endMarker = "PLAYER_" + playerID + "_END";
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.equals(startMarker)) {
                    inPlayerSection = true;
                    continue;
                }
                
                if (line.equals(endMarker)) {
                    break;
                }
                
                if (inPlayerSection && !line.isEmpty()) {
                    Pokemon pokemon = parsePokemonData(line);
                    if (pokemon != null) {
                        pokemonList.add(pokemon);
                    }
                }
            }
            
            if (!pokemonList.isEmpty()) {
                System.out.println("Loaded " + pokemonList.size() + " Pokemon from collection!");
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("No Pokemon collection file found.");
        } catch (IOException e) {
            System.err.println("Error loading Pokemon collection: " + e.getMessage());
        }
        
        return pokemonList;
    }

    /**
     * Load all collections from file (for internal use)
     */
    private Map<Integer, List<String>> loadAllCollections() {
        Map<Integer, List<String>> allCollections = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(POKEMON_FILE))) {
            String line;
            int currentPlayerID = -1;
            List<String> currentCollection = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.startsWith("PLAYER_") && line.endsWith("_START")) {
                    // Extract player ID
                    String idStr = line.substring(7, line.length() - 6); // Remove "PLAYER_" and "_START"
                    try {
                        currentPlayerID = Integer.parseInt(idStr);
                        currentCollection = new ArrayList<>();
                    } catch (NumberFormatException e) {
                        currentPlayerID = -1;
                        currentCollection = null;
                    }
                } else if (line.startsWith("PLAYER_") && line.endsWith("_END")) {
                    // End of player section
                    if (currentPlayerID != -1 && currentCollection != null) {
                        allCollections.put(currentPlayerID, currentCollection);
                    }
                    currentPlayerID = -1;
                    currentCollection = null;
                } else if (currentCollection != null && !line.isEmpty()) {
                    // Pokemon data line
                    currentCollection.add(line);
                }
            }
            
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, return empty map
        } catch (IOException e) {
            System.err.println("Error reading Pokemon collections: " + e.getMessage());
        }
        
        return allCollections;
    }

    /**
     * Format Pokemon data for storage
     */
    private String formatPokemonData(Pokemon pokemon) {
        return String.join("|",
            pokemon.getName(),
            pokemon.getType(),
            String.valueOf(pokemon.getLevel()),
            String.valueOf(pokemon.getHP()),
            String.valueOf(pokemon.getMaxHP()),
            String.valueOf(pokemon.getAttack()),
            String.valueOf(pokemon.getDefense()),
            String.valueOf(pokemon.getSpeed()),
            String.valueOf(pokemon.shiny()),
            String.valueOf(pokemon.fainted()),
            pokemon.getStatus()
        );
    }

    /**
     * Parse Pokemon data from storage format
     */
    private Pokemon parsePokemonData(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length != 11) {
                return null;
            }
            
            String name = parts[0];
            String type = parts[1];
            int level = Integer.parseInt(parts[2]);
            int hp = Integer.parseInt(parts[3]);
            int maxHP = Integer.parseInt(parts[4]);
            int attack = Integer.parseInt(parts[5]);
            int defense = Integer.parseInt(parts[6]);
            int speed = Integer.parseInt(parts[7]);
            boolean shiny = Boolean.parseBoolean(parts[8]);
            boolean fainted = Boolean.parseBoolean(parts[9]);
            String status = parts[10];
            
            // Create Pokemon and restore its saved state
            Pokemon pokemon = new Pokemon(name, type, level);
            restorePokemonStats(pokemon, hp, maxHP, attack, defense, speed, shiny, fainted, status);
            
            return pokemon;
            
        } catch (Exception e) {
            System.err.println("Error parsing Pokemon data: " + data);
            return null;
        }
    }

    /**
     * Restore Pokemon stats using reflection
     */
    private void restorePokemonStats(Pokemon pokemon, int hp, int maxHP, int attack, int defense, int speed, boolean shiny, boolean fainted, String status) {
        try {
            // Use reflection to set private fields
            setField(pokemon, "HP", hp);
            setField(pokemon, "maxHP", maxHP);
            setField(pokemon, "attack", attack);
            setField(pokemon, "defense", defense);
            setField(pokemon, "speed", speed);
            setField(pokemon, "shiny", shiny);
            setField(pokemon, "fainted", fainted);
            setField(pokemon, "status", status);
            
        } catch (Exception e) {
            System.err.println("Warning: Could not restore all Pokemon stats: " + e.getMessage());
        }
    }

    /**
     * Helper method to set private fields using reflection
     */
    private void setField(Pokemon pokemon, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = Pokemon.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(pokemon, value);
    }

    /**
     * Check if player has saved Pokemon collection
     */
    public boolean hasStoredCollection(int playerID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(POKEMON_FILE))) {
            String line;
            String startMarker = "PLAYER_" + playerID + "_START";
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(startMarker)) {
                    return true;
                }
            }
            
        } catch (IOException e) {
            // File doesn't exist or can't be read
        }
        
        return false;
    }

    /**
     * Create Pokemon collection file if it doesn't exist
     */
    private void createPokemonFile() {
        try {
            File resourcesDir = new File("resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }
            
            File pokemonFile = new File(POKEMON_FILE);
            if (!pokemonFile.exists()) {
                pokemonFile.createNewFile();
                System.out.println("Created Pokemon collection file: " + POKEMON_FILE);
            }
        } catch (IOException e) {
            System.err.println("Error creating Pokemon collection file: " + e.getMessage());
        }
    }

    /**
     * Get collection count for a player
     */
    public int getCollectionSize(int playerID) {
        List<Pokemon> collection = loadPokemonCollection(playerID);
        return collection.size();
    }
}