package cn.itcourage.smartmq.adapter.core.spi;

import java.lang.annotation.*;

/**
 * SPI装载器注解
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SPI {

    // Default SPI name
    String value() default "";

}
