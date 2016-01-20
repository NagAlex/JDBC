package org.nag;

import org.nag.objects.Cat;
import org.nag.objects.Dog;
import org.nag.objects.User;
import org.nag.storage.DatabaseStorage;
import org.nag.storage.Storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
        Connection connection = createConnection("User", "password", "nag");

        Storage storage = new DatabaseStorage(connection);
        List<Cat> cats = storage.list(Cat.class);
        for (Cat cat : cats) {
            storage.delete(cat);
        }
        cats = storage.list(Cat.class);
        if (!cats.isEmpty()) throw new Exception("Cats should not be in database!");

        for(int i = 1; i <= 20; i++) {
            Cat cat = new Cat();
            cat.setName("cat" + i);
            cat.setAge(i);
            storage.save(cat);

 /*           User user = new User();
            user.setAdmin(false);
            user.setAge(32 + i);
            user.setName("Alexander");
            user.setBalance(77.28 + i*7);
            storage.save(user);
  */      }
        cats = storage.list(Cat.class);
        if (cats.size() != 20) throw new Exception("Number of cats in storage should be 20!");

        User user = new User();
        user.setAdmin(true);
        user.setAge(32);
        user.setName("Alexander");
        user.setBalance(77.28);

        storage.save(user);

        User user1 = storage.get(User.class, user.getId());
        if (!user1.getName().equals(user.getName())) throw new Exception("Users should be equals!");

        user.setAdmin(false);
        storage.save(user);

        User user2 = storage.get(User.class, user.getId());
        if (!user.getAdmin().equals(user2.getAdmin())) throw new Exception("Users should be updated!");

        storage.delete(user1);

        User user3 = storage.get(User.class, user.getId());
        if (user3 != null) throw new Exception("User should be deleted!");

        Dog dog = new Dog();
        dog.setName("Puppy");
        dog.setAge(1);
        dog.setBreed("Taxa");
        storage.save(dog);

        try {
            Thread.sleep(10000);
        }catch(InterruptedException e) {}

        Dog dog1 = storage.get(Dog.class, 1);
        dog1.setName("Wolf");
        dog1.setAge(3);
        storage.save(dog1);

        try {
            Thread.sleep(10000);
        }catch(InterruptedException e) {}

        List<Dog> dogs = storage.list(Dog.class);
        for(Dog d: dogs) {
            System.out.println("Dog " + d.getId() + ": " + d.getName() + ", age: " + d.getAge() +
                               ", breed: " + d.getBreed());
        }

        Dog dog2 = storage.get(Dog.class, dogs.size());
        dog2.setBreed("Labrodor");
        storage.save(dog2);

        connection.close();
    }

    private static Connection createConnection(String login, String password, String dbName) throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, login, password);
    }
}
