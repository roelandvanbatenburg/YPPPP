package nl.unreadable.YPPPP.model;

public class YPPPPPirate {

	private String name;
	/*
    0 Able
    1 Distinguished
    2 Respected
    3 Master
    4 Renowned
    5 Grand-Master
    6 Legendary
    7 Ultimate
    */
	private int SF, Bilge, Sailing, Rigging, DNav, BNav, Gunning, Carpentry, Rumble, TH, Forage;
	
	public YPPPPPirate(){}
	public YPPPPPirate(String n){name = n;}
	
	public void setSF(int v){SF=v;}
	public void setBilge(int v){Bilge=v;}
	public void setSailing(int v){Sailing=v;}
	public void setRigging(int v){Rigging=v;}
	public void setDNav(int v){DNav=v;}
	public void setBNav(int v){BNav=v;}
	public void setGunning(int v){Gunning=v;}
	public void setCarpentry(int v){Carpentry=v;}
	public void setRumble(int v){Rumble=v;}
	public void setTH(int v){TH=v;}
	public void setForage(int v){Forage=v;}
	
	public void setName(String n){name = n;}
	public String getName(){return name;}
	
	public int getSF(){return SF;}
	public int getBilge(){return Bilge;}
	public int getSailing(){return Sailing;}
	public int getRigging(){return Rigging;}
	public int getDNav(){return DNav;}
	public int getBNav(){return BNav;}
	public int getGunning(){return Gunning;}
	public int getCarpentry(){return Carpentry;}
	public int getRumble(){return Rumble;}
	public int getTH(){return TH;}
	public int getForage(){return Forage;}
}
