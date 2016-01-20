package org.nag.objects;

public class Dog extends Entity{
    private String name;
    private int age;
    private String breed;
    @Ignore
    private Boolean barking;

    public Dog() {
        barking = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Boolean getBarking() {
        return barking;
    }

    public void setBarking(boolean barking) {
        this.barking = barking;
    }
}
