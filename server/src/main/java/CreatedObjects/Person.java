package CreatedObjects;

public class Person {

  private String name;
  private String age;
  private String city;

  public Person(String name, String age, String city) {
    this.name = name;
    this.age = age;
    this.city = city;
  }

  public String getName() {
    return name;
  }

  public String getAge() {
    return age;
  }

  public String getCity() {
    return city;
  }
}
