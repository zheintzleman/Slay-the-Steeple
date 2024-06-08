package app;

import app.EventManager.Event;

public class StatusEffect extends Effect {
  private Status status;
  private boolean powerIsStatusStr;

  public StatusEffect(String data, Status status){
    super(data);
    this.status = status;
    powerIsStatusStr = false;
    App.ASSERT(!isBanned(whenPlayed()));
  }
  public StatusEffect(String data, Status status, boolean powerIsStatusStr){
    this(data, status);
    this.powerIsStatusStr = powerIsStatusStr;
    App.ASSERT(!isBanned(whenPlayed()));
  }
  public StatusEffect(StatusEffect prev, Status newStatus){
    super(prev);
    status = newStatus;
    powerIsStatusStr = prev.powerIsStatusStr;
    App.ASSERT(!isBanned(whenPlayed()));
  }

  public Status getStatus(){ return status; }
  public void setStatus(Status newStatus) {status = newStatus; }
  
  @Override
  public int getPower(){
    return powerIsStatusStr ? status.getStrength() : super.getPower();
  }
  
  private static boolean isBanned(Event playEvent){
    return EventManager.BANNED_STATUS_EFFECTS.contains(playEvent);
  }
}
