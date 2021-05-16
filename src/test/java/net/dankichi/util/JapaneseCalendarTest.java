package net.dankichi.util;

import static org.junit.Assert.*;
import org.junit.Test;

import javax.annotation.processing.SupportedSourceVersion;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class JapaneseCalendarTest {

    @Test
    public void 令和改元() {
        JapaneseCalendar cal = new JapaneseCalendar();
        cal.set(2019, Calendar.APRIL, 30);
        assertEquals("平成", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.HEISEI, cal.get(JapaneseCalendar.GENGO));
        assertEquals(31, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.APRIL, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(30, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));

        cal.add(JapaneseCalendar.DAY_OF_MONTH_JP, 1);
        assertEquals("令和", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.REIWA, cal.get(JapaneseCalendar.GENGO));
        assertEquals(1, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.MAY, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(1, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));
    }

    @Test
    public void 平成改元() {
        JapaneseCalendar cal = new JapaneseCalendar();
        cal.set(1989, Calendar.JANUARY, 7);
        assertEquals("昭和", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.SHOUWA, cal.get(JapaneseCalendar.GENGO));
        assertEquals(64, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.JANUARY, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(7, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));

        cal.add(JapaneseCalendar.DAY_OF_MONTH_JP, 1);
        assertEquals("平成", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.HEISEI, cal.get(JapaneseCalendar.GENGO));
        assertEquals(1, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.JANUARY, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(8, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));
    }

    @Test
    public void 昭和改元() {
        JapaneseCalendar cal = new JapaneseCalendar();
        cal.set(1926, Calendar.DECEMBER, 24);
        assertEquals("大正", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.TAISHOU, cal.get(JapaneseCalendar.GENGO));
        assertEquals(15, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.DECEMBER, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(24, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));

        cal.add(JapaneseCalendar.DAY_OF_MONTH_JP, 1);
        assertEquals("昭和", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.SHOUWA, cal.get(JapaneseCalendar.GENGO));
        assertEquals(1, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.DECEMBER, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(25, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));
    }

    @Test
    public void 大正改元() {
        JapaneseCalendar cal = new JapaneseCalendar();
        cal.set(1912, Calendar.JULY, 29);
        assertEquals("明治", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.MEIJI, cal.get(JapaneseCalendar.GENGO));
        assertEquals(45, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.JULY, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(29, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));

        cal.add(JapaneseCalendar.DAY_OF_MONTH_JP, 1);
        assertEquals("大正", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.TAISHOU, cal.get(JapaneseCalendar.GENGO));
        assertEquals(1, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.JULY, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(30, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));
    }

    @Test
    public void グレゴリアン改暦() {
        JapaneseCalendar cal = new JapaneseCalendar();
        cal.set(1872, Calendar.DECEMBER, 31);
        assertEquals("明治", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.MEIJI, cal.get(JapaneseCalendar.GENGO));
        assertEquals(5, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.DECEMBER, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(2, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));

        cal.add(JapaneseCalendar.DAY_OF_MONTH_JP, 1);
        assertEquals("明治", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.MEIJI, cal.get(JapaneseCalendar.GENGO));
        assertEquals(6, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(Calendar.JANUARY, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(1, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));
    }

    @Test
    public void 推古年以前はエラー() {
        JapaneseCalendar cal = new JapaneseCalendar();
        cal.setJapanese(JapaneseCalendar.SUIKO, 1, 1, 1);
        assertEquals("（推古）", cal.getString(JapaneseCalendar.GENGO));
        assertEquals(JapaneseCalendar.SUIKO, cal.get(JapaneseCalendar.GENGO));
        assertEquals(1, cal.get(JapaneseCalendar.YEAR_OF_GENGO));
        assertEquals(0, cal.get(JapaneseCalendar.MONTH_JP));
        assertEquals(1, cal.get(JapaneseCalendar.DAY_OF_MONTH_JP));

        try {
            cal.add(JapaneseCalendar.DAY_OF_MONTH_JP, -1);
            cal.getString(JapaneseCalendar.GENGO);
            fail();
        } catch (UnsupportedOperationException e) {
        }


    }




}
