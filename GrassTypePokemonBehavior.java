import java.util.Random;

public class GrassTypePokemonBehavior {
    private Random random;

    public GrassTypePokemonBehavior() {
        this.random = new Random();
    }

    // Grass Pokemon damage against the 4 types
    public int calculateDamage(Pokemon attacker, Pokemon target) {
        int baseDamage = attacker.getAttack() - target.getDefense() / 2;
        baseDamage = Math.max(1, baseDamage);

        String targetType = target.getType();
        if (targetType.equals("Water")) {
            baseDamage *= 2; // Grass beats Water
        } else if (targetType.equals("Fire") || targetType.equals("Grass")) {
            baseDamage /= 2; // Fire and Grass resist Grass
        }
        // Electric takes normal damage

        return baseDamage;
    }

    // Grass Pokemon have a chance to poison the opponent
    public boolean tryToPoison(Pokemon target) {
        if (random.nextInt(100) < 20) { // 20% chance
            target.setStatus("poisoned");
            return true;
        }
        return false;
    }

    // Get a grass attack name based on level
    public String getGrassAttack(int level) {
        if (level < 10) {
            return "Vine Whip";
        } else if (level < 20) {
            return "Razor Leaf";
        } else {
            return "Solar Beam";
        }
    }

    // Grass Pokemon special attack with type effectiveness
    public int grassAttack(Pokemon attacker, Pokemon target) {
        if (attacker.fainted() || !attacker.getStatus().equals("normal")) {
            return 0;
        }

        int damage = calculateDamage(attacker, target);
        String attackName = getGrassAttack(attacker.getLevel());

        System.out.println(attacker.getName() + " used " + attackName + "!");

        if (tryToPoison(target)) {
            System.out.println(target.getName() + " was poisoned!");
        }

        target.takeDamage(damage);
        return damage;
    }
}