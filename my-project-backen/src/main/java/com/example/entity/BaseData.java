package com.example.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public interface BaseData {//实现类似于BeanUtils.copyProperties

    default <V> V asViewObject(Class<V> clazz, Consumer<V> consumer){
        V v = this.asViewObject(clazz);
        consumer.accept(v);
        return v;
    }
    default <V> V asViewObject(Class<V> clazz){
        try {
            Field[] declaredFields = clazz.getDeclaredFields();//获取所有已经生命字段
            Constructor<V> constructor = clazz.getConstructor();//构造器
            V v = constructor.newInstance();//无参构造
            for (Field declaredField : declaredFields) convert(declaredField, v);
            return v;
        }catch (ReflectiveOperationException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    private void convert(Field field, Object vo){
        try {
            Field source = this.getClass().getDeclaredField(field.getName());//获取字段名
            field.setAccessible(true);
            source.setAccessible(true);
            field.set(vo, source.get(this));
        }catch (IllegalAccessException | NoSuchFieldException ignored){}//忽略错误信息
    }
}
