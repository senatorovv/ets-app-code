package com.kumoco.productorder.api;


import com.kumoco.productorder.model.tmf.ProductOrderItem;
import com.kumoco.productorder.model.tmf.TimePeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;

import static com.kumoco.productorder.api.TestConstants.END_DATE_TIME;
import static com.kumoco.productorder.api.TestConstants.LONG_VALUE;
import static com.kumoco.productorder.api.TestConstants.START_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;


public class TMFModelChangesTests {

    @Test
    public void ProductOrderItem_Create_Test(){

    ProductOrderItem productOrderItem = new ProductOrderItem().quantity(LONG_VALUE);
    assertThat(productOrderItem.getQuantity()).isEqualTo(LONG_VALUE);

}

    @Test
    public void TimePeriod_Create_Test(){

        TimePeriod timePeriod = new TimePeriod().endDateTime(END_DATE_TIME).startDateTime(START_DATE_TIME);
        assertThat(timePeriod.getStartDateTime()).isEqualTo(START_DATE_TIME);
        assertThat(timePeriod.getEndDateTime()).isEqualTo(END_DATE_TIME);
    }

}
