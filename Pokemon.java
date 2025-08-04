

public class Pokemon {
	private String name;
	private String type;
	private int level;
	private int HP;
	private int maxHP;
	private int attack;
	private int defense;
	private int speed;
	private boolean shiny;
	
	private boolean fainted;
	private String status;



	public Pokemon(String name, String type, int level) {
		this.name = name;
		this.type = type;
		this.level = level;
		this.shiny = Math.random()<0.10;
		this.fainted = false;
		this.status = "normal";
		
		
		calculateStats();
	}
	
	private void calculateStats() {
        int baseHP = 50 + (level * 2);
        int baseAttack = 40 + (int)(level * 1.5);
        int baseDefense = 35 + (int)(level * 1.3);
        int baseSpeed = 30 + (int)(level * 1.2);
        
        // Apply type modifiers
        switch (type.toLowerCase()) {
            case "Fire":
                baseAttack += 5;
                baseSpeed += 3;
                break;
            case "Water":
                baseHP += 8;
                baseDefense += 5;
                break;
            case "Grass":
                baseHP += 5;
                baseDefense += 8;
                break;
            case "Electric":
                baseSpeed += 8;
                baseAttack += 3;
                break;
        }
        this.maxHP = (int) baseHP;
        this.HP = this.maxHP;
        this.attack = (int) baseAttack;
        this.defense = (int) baseDefense;
        this.speed = (int) baseSpeed;
        
	}
	
	public int performAttack(Pokemon target) {
		if (fainted||!status.equals("normal")) {
			return 0;
		}
	    
	        
	      int damage = Math.max(1, this.attack - target.getDefense() / 2);
	      damage += (int) (Math.random() * 10) - 5;
	        
	        target.takeDamage(damage);
	        return damage;
	    }
	    
	    public void takeDamage(int damage) {
	        this.HP = Math.max(0, this.HP - damage);
	        if (this.HP == 0) {
	            this.fainted = true;
	        }
	    }
	    
	    public void heal(int amount) {
	        if (!fainted) {
	            this.HP = Math.min(this.maxHP, this.HP + amount);
	        }
	    }
	    
	    public void fullHeal() {
	        this.HP = this.maxHP;
	        this.fainted = false;
	        this.status = "normal";
	    }
	    
	    public boolean canBeCaught() {
	        double catchRate = 1.0 - ((double) HP / maxHP) * 0.8;
	        return Math.random() < catchRate;
	    }

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public int getLevel() {
			return level;
		}

		public int getHP() {
			return HP;
		}

		public int getMaxHP() {
			return maxHP;
		}

		public int getAttack() {
			return attack;
		}

		public int getDefense() {
			return defense;
		}

		public int getSpeed() {
			return speed;
		}

		public boolean shiny() {
			return shiny;
		}

		public boolean fainted() {
			return fainted;
		}

		public String getStatus() {
			return status;
		}

		public void setShiny(String status) {
			this.status = status;
		}

		public void setFainted(boolean fainted) {
			this.fainted = fainted;
		}
		public String getDetailedInfo() {
	        return String.format("""
	        	=====Pokemon Stats=====
	            %s
	            Type: %s | Level: %d | %s
	            --------------------------
	            HP: %d/%d
	            Attack: %d | Defense: %d | Speed: %d
	            --------------------------
	            Status: %s
	            ========================
	            """, 
	            toString(), type, level, shiny ? "SHINY" : "Normal",
	            HP, maxHP, attack, defense, speed, status);
		
		
	}
}

