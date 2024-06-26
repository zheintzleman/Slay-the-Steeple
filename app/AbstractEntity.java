package app;

/** A bare bones implementation of Entity, which throws errors for any method unimplemented in
 * Entity.java. Using this setup instead of just making Entity non-abstract to force myself to
 * explicitly notice whenever I "instantiate an Entity object", and don't accidentally make an
 * Enemy an Entity instead.
 * Currently used for the entity copy system.
 * 
 * @see Entity
 * @see Entity Entity.createCopy()
 */
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
