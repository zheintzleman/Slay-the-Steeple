package app;

public class StatusEffect extends Effect {
  private Status status;
  private boolean powerIsStatusStr;

  public StatusEffect(String data, Status status){
    super(data);
    this.status = status;
    powerIsStatusStr = false;
  }
  public StatusEffect(String data, Status status, boolean powerIsStatusStr){
    this(data, status);
    this.powerIsStatusStr = powerIsStatusStr;
  }
  public StatusEffect(StatusEffect prev, Status newStatus){
    super(prev);
    status = newStatus;
    powerIsStatusStr = prev.powerIsStatusStr;
  }

  public Status getStatus(){ return status; }
  public void setStatus(Status newStatus) {status = newStatus; }
  
  @Override
  public int getPower(){
    return powerIsStatusStr ? status.getStrength() : super.getPower();
  }
}
