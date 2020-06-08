package ru.d1g.doceasy.core.ui;

import org.apache.commons.lang3.StringUtils;

public class DemoUtils {
    public static String getViewName(Class clazz) {
        String lowerCase = StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(clazz.getSimpleName()),
                " "
        ).toLowerCase().replace("view", "").replace("crud", "CRUD");

        return lowerCase.substring(0, 1).toUpperCase() + lowerCase.substring(1);
    }
}
