
package com.friendfinder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterestTest {

    @Test
    void testDefaultValues() {
        // Given
        Interest interest = new Interest();

        // Then: default state
        assertNull(interest.getId(), "Default id should be null before persistence");
        assertNull(interest.getName(), "Default name should be null before set");
        // Vi tester ikke users, da der ikke er en setter i Interest-klassen
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Interest interest = new Interest();

        // When
        interest.setId(1L);
        interest.setName("Music");

        // Then
        assertEquals(1L, interest.getId(), "Id should match the value set");
        assertEquals("Music", interest.getName(), "Name should match the value set");
    }
}
