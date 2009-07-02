package nl.unreadable.YPPPP.model;

public class YPPPPShip {
	String type;
	double cb_damage; // 1 = small, 1+1/3 = medium, 2 = large
	double ram_damage; // how much damage a ship deals when ramming
	double sf_hp; // damage to max
	double sink_hp; // damage to sink
	double rock_damage; // damage that the ship receives when it hits rocks
	YPPPPSize size; // ship class
	
	double damage; // current damage
	
	public YPPPPShip(YPPPPShip prototype)
	{
		type = prototype.type;
		cb_damage = prototype.cb_damage;
		ram_damage = prototype.ram_damage;
		sf_hp = prototype.sf_hp;
		sink_hp = prototype.sink_hp;
		rock_damage = prototype.rock_damage;
		size = prototype.size;
		damage = prototype.damage;
	}
	public YPPPPShip (String t, double c, double ra, double sf, double si, double ro, YPPPPSize sc)
	{
		type = t;
		cb_damage = c;
		ram_damage = ra;
		sf_hp = sf;
		sink_hp = si;
		rock_damage = ro;
		size = sc;
		damage = 0;
	}
	
	public void changeType(YPPPPShip prototype)
	{
		type = prototype.type;
		cb_damage = prototype.cb_damage;
		ram_damage = prototype.ram_damage;
		sf_hp = prototype.sf_hp;
		sink_hp = prototype.sink_hp;
		rock_damage = prototype.rock_damage;
		size = prototype.size;
	}
	
	public void getShot(double d){damage += d;}
	public void hitRocks(){damage += rock_damage;}
	public void ram(YPPPPShip s){damage += s.ram_damage;}
	public void reset(){damage = 0;}
}
