import java.util.*;

public class FireTypePokemonBehavior {
    private Random random;

    public FireTypePokemonBehavior() {
        this.random = new Random();
    }

    // Fire Pokemon damage against the 4 types
    public int calculateDamage(Pokemon attacker, Pokemon target) {
        int baseDamage = attacker.getAttack() - target.getDefense() / 2;
        baseDamage = Math.max(1, baseDamage);

        String targetType = target.getType();

        if (targetType.equals("Grass")) {
            baseDamage = baseDamage * 5; // Fire beats Grass
        } else if (targetType.equals("Water")) {
            baseDamage = baseDamage / 2; // Water resists Fire
        } else if (targetType.equals("Fire")) {
            baseDamage = baseDamage / 2; // Fire resists Fire
        }
        // Electric takes normal damage

        return baseDamage;
    }

    // Fire Pokemon have a chance to burn the opponent
    public boolean tryToBurn(Pokemon target) {
        if (random.nextInt(100) < 20) { // 20% chance
            target.setStatus("burned"); // Using setStatus method to set status
            return true;
        }
        return false;
    }

    // Get a fire attack name based on level
    public String getFireAttack(int level) {
        if (level < 10) {
            return "Ember";
        } else if (level < 20) {
            return "Flamethrower";
        } else {
            return "Fire Blast";
        }
    }

    // Fire Pokemon special attack with type effectiveness
    public int fireAttack(Pokemon attacker, Pokemon target) {
        if (attacker.fainted() || !attacker.getStatus().equals("normal")) {
            return 0;
        }

        int damage = calculateDamage(attacker, target);
        String attackName = getFireAttack(attacker.getLevel());

        System.out.println(attacker.getName() + " used " + attackName + "!");

        // Try to burn the target
        if (tryToBurn(target)) {
            System.out.println(target.getName() + " was burned!");
        }

        target.takeDamage(damage);
        return damage;
    }
}