package app;

public class AbstractEntity extends Entity {

  public AbstractEntity(Entity e){
    super(e);
  }
  
  @Override
  public void endTurn(Player player) {
    throw new RuntimeException("Calling endTurn function on abstract entity.");
  }
  @Override
  public void setSplitIntent() {
    throw new RuntimeException("Calling setSplitIntent function on abstract entity.");
  }
}
