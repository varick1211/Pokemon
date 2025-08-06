import java.util.*;

public class pokemongenerator {
    private Random random;
    
    private String[] pokemonNames = {
        "Charmander", "Charmeleon", "Charizard", "Vulpix", "Growlithe",
        "Squirtle", "Wartortle", "Blastoise", "Psyduck", "Magikarp",
        "Bulbasaur", "Ivysaur", "Venusaur", "Oddish", "Bellsprout",
        "Pikachu", "Raichu", "Voltorb", "Electrode", "Electabuzz"
    };
    
    private String[] pokemonTypes = {
        "Fire", "Fire", "Fire", "Fire", "Fire",
        "Water", "Water", "Water", "Water", "Water",
        "Grass", "Grass", "Grass", "Grass", "Grass",
        "Electric", "Electric", "Electric", "Electric", "Electric"
    };
    
    public pokemongenerator() {
        this.random = new Random();
    }
    
    public Pokemon createRandomPokemon() {
        int index = random.nextInt(pokemonNames.length);
        String name = pokemonNames[index];
        String type = pokemonTypes[index];
        int level = random.nextInt(20) + 1;
        
        return new Pokemon(name, type, level);
    }
    
    public Pokemon createPokemonOfType(String desiredType) {
        for (int i = 0; i < pokemonTypes.length; i++) {
            if (pokemonTypes[i].equals(desiredType)) {
                String name = pokemonNames[i];
                int level = random.nextInt(20) + 1;
                return new Pokemon(name, desiredType, level);
            }
        }
        
        return createRandomPokemon();
    }
    
    public Pokemon createPokemonInRange(int minLevel, int maxLevel) {
        int index = random.nextInt(pokemonNames.length);
        String name = pokemonNames[index];
        String type = pokemonTypes[index];
        int level = random.nextInt(maxLevel - minLevel + 1) + minLevel;
        
        return new Pokemon(name, type, level);
    }
}