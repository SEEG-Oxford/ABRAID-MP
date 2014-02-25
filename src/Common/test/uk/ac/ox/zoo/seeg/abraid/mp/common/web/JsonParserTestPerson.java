package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

/**
 * A test data structure into which to parse JSON. This didn't work as an inner class of JsonParserTest.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JsonParserTestPerson {
    private String name;
    private int age;

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
}
