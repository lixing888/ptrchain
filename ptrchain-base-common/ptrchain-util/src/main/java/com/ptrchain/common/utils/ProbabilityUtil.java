package com.ptrchain.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class ProbabilityUtil {
    public static BigDecimal sameBirthDay(BigDecimal total,BigDecimal same){
        BigDecimal result = BigDecimal.ONE;
        for (int i = 0;i<total.subtract(same).intValue();i++){
            result = result.multiply(BigDecimal.valueOf((364-i)).divide(BigDecimal.valueOf(365l),20,RoundingMode.HALF_UP));
        }
        BigDecimal a = total.divide(same,20,RoundingMode.HALF_UP);
        result = result.multiply(a).multiply(total.subtract(BigDecimal.ONE)).divide(BigDecimal.valueOf(365l),20,RoundingMode.HALF_UP);

        return result;
    }

    public static void main(String args[]) {
        long start = new Date().getTime();
        System.out.println(ProbabilityUtil.sameBirthDay(new BigDecimal("11"),new BigDecimal("2")));
        long end = new Date().getTime();
        System.out.println(end-start);
    }
}
