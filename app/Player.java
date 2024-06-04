package app;

public class Player extends Entity {
  public Player(String name, int hp, int maxHP, String[] img){
    super("Ironclad", hp, img, maxHP);
  }
  public Player(Player p){
    super(p);
  }
  
  @Override
  public void die(){
    Combat.c.endCombat();
  }

  @Override
  public void endTurn(Player player){
    setBlock(0);
    updateCopysDecreasingStatuses();
    subtractStatusStrength("Strength", getStatusStrength("Strength Down"));
    setStatusStrength("Strength Down", 0);
    subtractStatusStrength("Dexterity", getStatusStrength("Dexterity Down"));
    setStatusStrength("Dexterity Down", 0);
  }
  @Override
  public void setSplitIntent() {
    throw new RuntimeException("Calling setSplitIntent on Player"); //TODO: Make a more specific Exception type.
  }
}
