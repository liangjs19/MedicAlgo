package com.mbaxajl3.medicalgo;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class UtilTest {
    @Test
    public void containsIgnoreCase_CorrectSingleString_ReturnsTrue() {
        assertThat(Util.containsIgnoreCase("testing", "est")).isTrue();
    }

    @Test
    public void containsIgnoreCase_IncorrectSingleString_ReturnsFalse() {
        assertThat(Util.containsIgnoreCase("testing", "asd")).isFalse();
    }

    @Test
    public void containsIgnoreCase_CorrectMultiString_ReturnsTrue() {
        String[] strings = {"a", "test"};
        assertThat(Util.containsIgnoreCase("testing", strings)).isTrue();
    }

    @Test
    public void containsIgnoreCase_IncorrectMultiString_ReturnsFalse() {
        String[] strings = {"b", "a"};
        assertThat(Util.containsIgnoreCase("testing", strings)).isFalse();
    }

}
