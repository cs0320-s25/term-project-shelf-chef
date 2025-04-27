package CreatedObjects;

public class Star {

  private String starID;
  private String properName;
  private String X;
  private String Y;
  private String Z;

  public Star(String starID, String properName, String X, String Y, String Z) {
    this.starID = starID;
    this.properName = properName;
    this.X = X;
    this.Y = Y;
    this.Z = Z;
  }

  public String getStarId() {
    return starID;
  }

  public String getProperName() {
    return properName;
  }

  public String getX() {
    return X;
  }

  public String getY() {
    return Y;
  }

  public String getZ() {
    return Z;
  }
}
