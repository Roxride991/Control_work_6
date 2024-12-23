package org.example;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

class Person {
    private String name;
    private LocalDate birthDate;
    private String email;

    public Person(String name, LocalDate birthDate, String email) {
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public boolean isLeapYearBorn() {
        return birthDate.isLeapYear();
    }

    @Override
    public String toString() {
        return "Person{Имя= " + name + ", Дата рождения=" + birthDate + ", Почта= " + email + "}";
    }
}

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        setupLogger();

        try {
            List<Person> persons = createPersonsList();

            List<Map.Entry<String, String>> result = persons.stream()
                    .filter(p -> p.getAge() > 18)
                    .peek(p -> logger.info(" " + p.getName() + ", " + p.getEmail()))
                    .map(p -> Map.entry(p.getName(), p.getEmail()))
                    .collect(Collectors.toList());

            System.out.println("Отфильтрованные данные:(>18)");
            result.forEach(entry -> System.out.println("Имя: " + entry.getKey() + ", Почта: " + entry.getValue()));

            double averageAge = persons.stream()
                    .mapToInt(Person::getAge)
                    .average()
                    .orElse(0);
            logger.info("Средний возраст: " + averageAge);
            System.out.println("Средний возраст: " + averageAge);

            List<Person> leapYearBorn = persons.stream()
                    .filter(Person::isLeapYearBorn)
                    .collect(Collectors.toList());
            logger.info("Люди, родившиеся в високосный год: " + leapYearBorn);
            System.out.println("Люди, родившиеся в високосный год:");
            leapYearBorn.forEach(person -> System.out.println(person.getName() + ", " + person.getEmail()));

            Map<String, List<Person>> groupedByAge = persons.stream()
                    .collect(Collectors.groupingBy(person -> {
                        int age = person.getAge();
                        if (age < 18) return "Ребёнок";
                        else if (age <= 40) return "Молодежь";
                        else if (age <= 65) return "Средний возраст";
                        else return "Пожилой";
                    }));
            logger.info("Группировка людей по возрастным группам: " + groupedByAge);
            System.out.println("Группировка людей по возрастным группам:");
            groupedByAge.forEach((ageGroup, people) -> {
                System.out.println(ageGroup + ":");
                people.forEach(person -> System.out.println("  " + person.getName() + ", " + person.getEmail()));
            });

            Predicate<Person> isAdult = person -> person.getAge() > 18;
            Function<Person, String> personToString = person -> person.getName() + ", " + person.getEmail();
            Consumer<String> printString = System.out::println;


            long startTime = System.currentTimeMillis();
            longOperation();
            long endTime = System.currentTimeMillis();
            logger.info("Обработка: " + (endTime - startTime) + "ms");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred: ", e);
        }
    }

    private static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Logger setup failed: ", e);
        }
    }

    private static List<Person> createPersonsList() {
        return Arrays.asList(
                new Person("Alisa", LocalDate.of(2000, 5, 15), "alisa@example.com"),
                new Person("Vlad", LocalDate.of(2010, 8, 22), "vlad@example.com"),
                new Person("Dima", LocalDate.of(2020, 2, 5), "dima@example.com"),
                new Person("Diana", LocalDate.of(1955, 8, 2), "diana@example.com"),
                new Person("Timur", LocalDate.of(1999, 4, 21), "rxz21@example.com"),
                new Person("Makar", LocalDate.of(1975, 12, 12), "Makar@example.com")
        );
    }

    private static void longOperation() {
        int[] largeArray = new int[100000000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = largeArray.length - i;
        }
        Arrays.sort(largeArray);
    }
}
