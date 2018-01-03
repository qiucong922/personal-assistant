package com.qiucong.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Gregorian2LunarCalendarMapping {
    private LocalDate gregorianDate;
    private LocalDate LunarDate;
}
