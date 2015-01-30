package co.insecurity.springref.event;

public class DeletedEvent {
  protected boolean entityFound = true;
  protected boolean successful = true;

  public boolean isEntityFound() {
    return entityFound;
  }
  
  public boolean isSuccessful() {
	  return successful;
  }
}