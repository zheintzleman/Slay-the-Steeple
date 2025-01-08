package app;

/** An extension of Entity used to represent the player (e.g. The Ironclad entity)
 * 
 * @see Entity
 * @see Combat Combat.player
 */
public class Player extends Entity {
  public Player(String name, String[] img, EntityHealth healthObj){
    super("Ironclad", img, healthObj);
  }
  
  @Override
  public void die(){
    Combat.c.endCombat();
  }

  @Override
  public void endTurn(Player player){
    if(!player.hasStatus("Barricade")){
      setBlock(0);
    }
    updateDecreasingStatuses();
    subtractStatusStrength("Strength", getStatusStrength("Strength Down"));
    setStatusStrength("Strength Down", 0);
    subtractStatusStrength("Dexterity", getStatusStrength("Dexterity Down"));
    setStatusStrength("Dexterity Down", 0);
  }
  @Override
  public void setSplitIntent(){
    throw new UnsupportedOperationException("Calling setSplitIntent on Player");
  }
  @Override
  public void giveBlock(Entity receiver, int blockPreCalculations){
    throw new UnsupportedOperationException("Calling giveBlock from Player.");
  }
}
