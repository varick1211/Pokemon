import java.util.Random;

public class WaterTypePokemonBehavior {
    private Random random;

    public WaterTypePokemonBehavior() {
        this.random = new Random();
    }

    // Water Pokemon damage against the 4 types
    public int calculateDamage(Pokemon attacker, Pokemon target) {
        int baseDamage = attacker.getAttack() - target.getDefense() / 2;
        baseDamage = Math.max(1, baseDamage);

        String targetType = target.getType();
        if (targetType.equals("Fire")) {
            baseDamage *= 2; // Water beats Fire
        } else if (targetType.equals("Grass") || targetType.equals("Water")) {
            baseDamage /= 2; // Grass and Water resist Water
        }
        // Electric takes normal damage

        return baseDamage;
    }

    // Water Pokemon have a chance to soak the opponent
    public boolean tryToSoak(Pokemon target) {
        if (random.nextInt(100) < 20) { // 20% chance
            target.setStatus("soaked");
            return true;
        }
        return false;
    }

    // Get a water attack name based on level
    public String getWaterAttack(int level) {
        if (level < 10) {
            return "Water Gun";
        } else if (level < 20) {
            return "Bubble Beam";
        } else {
            return "Hydro Pump";
        }
    }

    // Water Pokemon special attack with type effectiveness
    public int waterAttack(Pokemon attacker, Pokemon target) {
        if (attacker.fainted() || !attacker.getStatus().equals("normal")) {
            return 0;
        }

        int damage = calculateDamage(attacker, target);
        String attackName = getWaterAttack(attacker.getLevel());

        System.out.println(attacker.getName() + " used " + attackName + "!");

        if (tryToSoak(target)) {
            System.out.println(target.getName() + " was soaked!");
        }

        target.takeDamage(damage);
        return damage;
    }
}