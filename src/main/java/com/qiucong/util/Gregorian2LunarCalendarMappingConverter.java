package com.qiucong.util;

import com.google.common.collect.Lists;
import com.qiucong.model.Gregorian2LunarCalendarMapping;

import java.util.List;

public class Gregorian2LunarCalendarMappingConverter {

    public List<Gregorian2LunarCalendarMapping> getGregorian2LunarMapping(String mapping) {


        List<String> allLines = getAllLines(mapping);
        return null;
    }

    public List<String> getAllLines(String mapping) {
        /*mapping = mapping.replaceAll(" Lunardate ", " ")
                .replaceAll("Gregorian-Lunar Calendar Conversion Table of ", "");*/

        List<String> allLines = Lists.newArrayList();
        for (MappingLineLabel lineLable : MappingLineLabel.values()) {
            String[] lines = mapping.split(lineLable.name());
            if (lines.length > 1) {
                if (!lines[0].endsWith("Solarterms"))
                    allLines.add(lines[0]);
                mapping = lines[1];
            }
        }
        allLines.add(mapping);
        return allLines;
    }

    private enum MappingLineLabel {
        Gregoriandate,
        Jan,
        Feb,
        Mar,
        Apr,
        May,
        Jun,
        Jul,
        Aug,
        Sep,
        Oct,
        Nov,
        Dec,
        Remarks
    }
}
