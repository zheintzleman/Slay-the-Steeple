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
  /**Subtracts the entity's hp the specified amount. If hp is 0 or less, it dies.
   * Calls OnPlayerHurt Event.
  */
  public void subtractHP(int dmg){
    super.subtractHP(dmg);
    if(dmg > 0){
      EventManager.er.OnPlayerHurt(dmg);
    }
  }
  @Override
  public void endTurn(Player player){
    setBlock(0);
    updateCopysDecreasingStatuses();
    endTurnCopy.subtractStatusStrength("Strength", getStatusStrength("Strength Down"));
    endTurnCopy.setStatusStrength("Strength Down", 0);
    endTurnCopy.subtractStatusStrength("Dexterity", getStatusStrength("Dexterity Down"));
    endTurnCopy.setStatusStrength("Dexterity Down", 0);
    setStatuses(endTurnCopy.getStatuses());
  }
  @Override
  public void setSplitIntent() {
    throw new RuntimeException("Calling setSplitIntent on Player"); //TODO: Make a more specific Exception type.
  }
}
