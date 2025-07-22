package ru.netology.graphics.image;

import java.util.HashMap;
import java.util.Map;

public class Schema implements TextColorSchema {
    private final char[] numToSymb = {
            '#', '$', '@', '%', '*', '+', '-', '\''
    };

    @Override
    public char convert(int color) {
        return numToSymb[(int) Math.floor((double) color / 255 * 8)];
    }
}

