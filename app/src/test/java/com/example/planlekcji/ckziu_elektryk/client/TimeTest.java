package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;

import com.example.planlekcji.ckziu_elektryk.client.utils.Time;

import org.junit.Test;

public class TimeTest {

    @Test
    public void shouldCreateTime() {
        Time time = Time.of("08:45");

        assertEquals(8, time.getHours());
        assertEquals(45, time.getMinutes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateTimeWithIncorrectFormat() {
        Time.of("08.45");
    }

    @Test
    public void shouldCreateTimeWithoutFirstZero() {
        Time time = Time.of("8:45");

        assertEquals(8, time.getHours());
        assertEquals(45, time.getMinutes());
    }

    @Test
    public void shouldCreateTimeWithoutSecondZero() {
        Time time = Time.of("8:05");

        assertEquals(8, time.getHours());
        assertEquals(5, time.getMinutes());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCreateTimeWithLetters() {
        Time.of("8:0s");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCreateTimeWithSymbols() {
        Time.of("8:0$");
    }

    @Test
    public void shouldGetCorrectFormat() {
        Time time = Time.of("8:00");

        assertEquals("08:00", time.toString());
    }

}
