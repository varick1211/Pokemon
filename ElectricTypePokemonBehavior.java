import java.util.Random;

public class ElectricTypePokemonBehavior {
    private Random random;

    public ElectricTypePokemonBehavior() {
        this.random = new Random();
    }

    // Electric Pokemon damage against the 4 types
    public int calculateDamage(Pokemon attacker, Pokemon target) {
        int baseDamage = attacker.getAttack() - target.getDefense() / 2;
        baseDamage = Math.max(1, baseDamage);

        String targetType = target.getType();

        if (targetType.equals("Water")) {
            baseDamage = baseDamage * 5; // Electric beats Water
        } else if (targetType.equals("Grass")) {
            baseDamage = baseDamage / 2; // Grass resists Electric
        } else if (targetType.equals("Electric")) {
            baseDamage = baseDamage / 2; // Electric resists Electric
        }
        // Fire takes normal damage

        return baseDamage;
    }

    // Electric Pokemon have a chance to paralyze the opponent
    public boolean tryToParalyze(Pokemon target) {
        if (random.nextInt(100) < 20) { // 20% chance
            target.setStatus("paralyzed"); // Using setStatus method to set status
            return true;
        }
        return false;
    }

    // Get an electric attack name based on level
    public String getElectricAttack(int level) {
        if (level < 10) {
            return "Thunder Shock";
        } else if (level < 20) {
            return "Thunderbolt";
        } else {
            return "Thunder";
        }
    }

    // Electric Pokemon special attack with type effectiveness
    public int electricAttack(Pokemon attacker, Pokemon target) {
        if (attacker.fainted() || !attacker.getStatus().equals("normal")) {
            return 0;
        }

        int damage = calculateDamage(attacker, target);
        String attackName = getElectricAttack(attacker.getLevel());

        System.out.println(attacker.getName() + " used " + attackName + "!");

        // Try to paralyze the target
        if (tryToParalyze(target)) {
            System.out.println(target.getName() + " was paralyzed!");
        }

        target.takeDamage(damage);
        return damage;
    }
}