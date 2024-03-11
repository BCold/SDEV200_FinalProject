public class Note{
  private String title;
  private String body;

  // Constructor
  public Note(String title, String body){
    this.title = title;
    this.body = body;
  }

  // Accessors
  public String getTitle(){
    return this.title;
  }

  public String getBody(){
    return this.body;
  }

  // Mutators
  public void setTitle(String title){
    this.title = title;
  }

  public void setBody(String body){
    this.body = body;
  }

  public String toString(){
    return "Title: " + this.title + "\nBody: " + this.body;
  }
}