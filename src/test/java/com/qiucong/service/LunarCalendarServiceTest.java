package com.qiucong.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;

import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LunarCalendarServiceTest {

    @InjectMocks
    LunarCalendarService lunarCalendarService;

    @Before
    public void setUp() {
    }

    @Test
    public void should_return_lunar_date_by_gregorian_date() {


        LocalDate lunarDate = lunarCalendarService.getLunarDate(of(2017, 12, 30));

        assertThat(lunarDate).isEqualTo(of(2017, 11, 13));

    }

}