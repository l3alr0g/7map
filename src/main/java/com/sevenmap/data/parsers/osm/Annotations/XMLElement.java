package com.sevenmap.data.parsers.osm.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XMLElement {
  String tag() default "";

  Class<?> keyType() default Long.class;

  Class<?> valueType() default String.class;
}
