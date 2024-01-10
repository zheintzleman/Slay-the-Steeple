package app;

public class EventManager {
  private Run run;
  private Combat combat;

  public EventManager(Run r){
    run = r;
    combat = null;
  }
  public EventManager(Combat c){
    run = c.getRun();
    combat = c;
  }
  public Combat getCombat(){ return combat; }
  public void setCombat(Combat c){ combat = c; }

  public void OnTurnEnd(){
    // Relics.turnStart(...)((...));
    // Statuses.turnStart(...)((...));
  }
}
