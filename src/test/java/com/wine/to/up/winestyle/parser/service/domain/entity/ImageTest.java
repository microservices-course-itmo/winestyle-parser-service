package com.wine.to.up.winestyle.parser.service.domain.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    static Image image;

    @BeforeAll
    static void setUp() {
        image = new Image();
    }

    @Test
    void getData() {
        byte[] data = image.getData();
        assertNull(data);
    }
}