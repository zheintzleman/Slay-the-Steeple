package app;

public class Player extends Entity {
  public Player(String name, int hp, int maxHP, String[] img, Combat combat){
    super("Ironclad", hp, maxHP, img, combat);
  }
  
  @Override
  public void die(){
    getCombat().endCombat();
  }

  @Override
  /**Subtracts the entity's hp the specified amount. If hp is 0 or less, it dies.
   * Calls OnPlayerHurt Event.
  */
  public void subtractHP(int dmg){
    super.subtractHP(dmg);
    if(dmg > 0){
      getCombat().getEventManager().OnPlayerHurt(dmg);
    }
  }
}
