package jeql.api.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ManDoc
{
  String name() default "";
  String description() default "";
  boolean isRequired() default false;
  boolean isMultiple() default false;
}
