package com.shaw.mock.builder;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MockBuilder<Any> {

    protected static final Logger logger = LogManager.getLogger();

    @SuppressWarnings("unchecked")
	public static <Any>Any build(Class<?> clazz)
	{
    	List<Field> fields = collectFields(clazz).collect(Collectors.toList());
		fields = fields.parallelStream()
				.filter(fi->Modifier.isStatic(fi.getModifiers())==false 
							&& Modifier.isFinal(fi.getModifiers())==false
							&& Modifier.isTransient(fi.getModifiers())==false
							&& Modifier.isVolatile(fi.getModifiers())==false)
				.filter(f->f.getAnnotation(GeneratedValue.class)==null)
				.collect(Collectors.toList());

		fields.parallelStream().forEach(m->m.setAccessible(true));
		Object obj;
		try {
			obj = clazz.getDeclaredConstructor().newInstance();
			fields.parallelStream().forEach(f->setMockValue(obj, f));
			return (Any) obj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    private static Stream<Field> collectFields(Class<?> clazz)
    {
		if (clazz.getDeclaredFields().length==0)
			return Stream.empty();
		Stream<Field> s = Stream.of(clazz.getDeclaredFields());
		if (clazz.getSuperclass()!=null)
			return Stream.concat(s, collectFields(clazz.getSuperclass()));
    	return s;
    }

    private static void setMockValue(Object obj, Field f) {
		f.setAccessible(true);
		int strLength = f.getAnnotation(Column.class)!=null?f.getAnnotation(Column.class).length():20;  //limit length of Strings for data columns
		Object value = null;
		if (f.getType().getSimpleName().equals("String") && f.getName().toLowerCase().endsWith("date"))
			value = randomDate().toString(); //added for OrderHeaderView
		else if (f.getType().getSimpleName().equals("String"))
			value = randomAlphabetic(Math.min(strLength,20));
		else if (f.getType().getSimpleName().equals("int") || f.getType().isAssignableFrom(Integer.class))
			value = RandomUtils.nextInt(0,999999);
		else if (f.getType().getSimpleName().equals("long") || f.getType().isAssignableFrom(Long.class))
			value = RandomUtils.nextLong(0,999999999);
		else if (f.getType().getSimpleName().equals("double") || f.getType().isAssignableFrom(Double.class))
			value = RandomUtils.nextDouble(0,9999999);		
		else if (f.getType().isAssignableFrom(BigDecimal.class))
			value = BigDecimal.valueOf(RandomUtils.nextDouble(0,9999999));		
		else if (f.getType().getSimpleName().equals("boolean") || f.getType().isAssignableFrom(Boolean.class))
			value = true;		
		else if (f.getType().isAssignableFrom(LocalDate.class))
			value = randomDate();
		else if (f.getType().isAssignableFrom(LocalDateTime.class))
			value = randomDateTime();
		else if (f.getType().isAssignableFrom(List.class))
			value = null;
		else
			value = build(f.getType());

		try {
			f.set(obj,value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("set " + f.getName() + " -> " + value);
	}

	static LocalDate randomDate() {
		return LocalDate.ofEpochDay(RandomUtils.nextInt(0,10000));
	}

	static LocalDateTime randomDateTime() {
		return LocalDateTime.ofEpochSecond(RandomUtils.nextInt(0,10000), 0, ZoneOffset.UTC);
	}

}
