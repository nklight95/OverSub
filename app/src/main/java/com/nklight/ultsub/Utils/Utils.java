package com.nklight.ultsub.Utils;

import java.util.List;

public  class Utils {
    public static String joinToString(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
}
