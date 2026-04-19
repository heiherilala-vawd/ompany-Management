package com.example.demo.endpoint.rest.mapper;

public class EnumMapper {
  public static <T extends Enum<T>> T mapEnum(Enum<?> source, Class<T> targetClass) {
    if (source == null) {
      return null;
    }
    return Enum.valueOf(targetClass, source.name());
  }
}
