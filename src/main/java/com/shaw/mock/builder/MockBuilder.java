package com.shaw.mock.builder;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class MockBuilder {

    protected static final Logger logger = LogManager.getLogger();
	
    @Autowired
    ApplicationContext context;
    
	public MockBuilder() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <Any> Any build(Class<?> clazz)
	{
		logger.debug("building mock object for " + clazz);
    	List<Field> fields = collectFields(clazz).collect(Collectors.toList());
		fields = fields.parallelStream()
				.filter(fi->Modifier.isStatic(fi.getModifiers())==false 
							&& Modifier.isFinal(fi.getModifiers())==false
							&& Modifier.isTransient(fi.getModifiers())==false
							&& fi.getAnnotation(ManyToOne.class)==null  //exclude bi-directional entity relationships
							&& Modifier.isVolatile(fi.getModifiers())==false)
				.filter(f->f.getAnnotation(GeneratedValue.class)==null)
				.collect(Collectors.toList());

		fields.parallelStream().forEach(m->m.setAccessible(true));
		Object obj;
		Constructor<?> constructor = null;
//		finally {
//			context.;
//		}
		
		try {
			constructor = clazz.getDeclaredConstructor();
			obj = constructor.newInstance();
			fields.parallelStream().forEach(f->setMockValue(obj, f));
			logger.debug("completed mock object for " + clazz);
			return (Any) obj;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			logger.debug("unable to instantiate " + clazz + " with " + constructor);
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("other exception for " + clazz);
			e.printStackTrace();
		}
		try {
			logger.debug("attempting to get bean: " + clazz);
			Object b = context.getBean(clazz);
			return (Any) b;
		} catch (Exception e1) {
			logger.debug("no bean found: " + context + " " + e1.toString());
		}
		return null;
	}

    private Stream<Field> collectFields(Class<?> clazz)
    {
		if (clazz.getDeclaredFields().length==0)
			return Stream.empty();
		Stream<Field> s = Stream.of(clazz.getDeclaredFields());
		if (clazz.getSuperclass()!=null)
			return Stream.concat(s, collectFields(clazz.getSuperclass()));
    	return s;
    }

    private void setMockValue(Object obj, Field f) {
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
		else if (f.getType().isAssignableFrom(String.class))
			value = randomAlphabetic(Math.min(strLength,20));
		else if (f.getType().isAssignableFrom(List.class))
			value = buildList(f);
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

	private List<?> buildList(Field f) {
        ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
        Class<?> clazz = (Class<?>) stringListType.getActualTypeArguments()[0];
		logger.debug("building list of " + clazz);
		List<Object> list = new ArrayList<Object>();
		Object o = build(clazz);
		if (o!=null)
		{
			list.add(o);
			list.add(build(clazz));
		}
        return list;
	}

	static LocalDate randomDate() {
		return LocalDate.ofEpochDay(RandomUtils.nextInt(0,10000));
	}

	static LocalDateTime randomDateTime() {
		return LocalDateTime.ofEpochSecond(RandomUtils.nextInt(0,10000), 0, ZoneOffset.UTC);
	}

}
