package github.saukiya.sxlevel.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlayerCommand {
    //指令名
    String cmd();

    //指令参数
    String arg() default "";

    //指令执行者
    CommandType[] type() default CommandType.ALL;
}
