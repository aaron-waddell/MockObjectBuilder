package com.shaw.mock.builder;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

//	@SuppressWarnings("unchecked")
//	private <Any> Any buildJAXB(Type type)
//	{
//		Class clazz = type.;
//		logger.debug("building mock object for " + clazz );
//		Object j = getMockValue(clazz, type, null, 0);
//		return (Any) j;
//	}
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
		value = getMockValue(f.getType(), f.getGenericType(), f.getName(), strLength); 
		if (value==null)
			value = build(f.getType());

		try {
			f.set(obj,value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("set " + f.getName() + " -> " + value);
	}

    @SuppressWarnings("unchecked")
	public <Any> Any getMockValue(Class<?> clazz, Type type, String fieldName, int strLength) {
		Object value = null;
		if (clazz.getSimpleName().equals("String") && fieldName.toLowerCase().endsWith("date"))
			value = randomDate().toString(); //added for OrderHeaderView
		else if (clazz.isAssignableFrom(List.class))
			value = buildList(type);
		else if (clazz.isAssignableFrom(JAXBElement.class))
			value = buildJAXBElement(type);
		else if (clazz.getSimpleName().equals("String"))
			value = randomAlphabetic(Math.min(strLength,20));
		else if (clazz.getSimpleName().equals("int") || clazz.isAssignableFrom(Integer.class))
			value = RandomUtils.nextInt(0,999999);
		else if (clazz.getSimpleName().equals("short") || clazz.isAssignableFrom(Short.class))
			value = Integer.valueOf(RandomUtils.nextInt(0,999999)).shortValue();
		else if (clazz.getSimpleName().equals("long") || clazz.isAssignableFrom(Long.class))
			value = RandomUtils.nextLong(0,999999999);
		else if (clazz.getSimpleName().equals("double") || clazz.isAssignableFrom(Double.class))
			value = RandomUtils.nextDouble(0,9999999);		
		else if (clazz.getSimpleName().equals("float") || clazz.isAssignableFrom(Float.class))
			value = RandomUtils.nextFloat(0,9999999);		
		else if (clazz.isAssignableFrom(BigDecimal.class))
			value = BigDecimal.valueOf(RandomUtils.nextDouble(0,9999999));		
		else if (clazz.getSimpleName().equals("boolean") || clazz.isAssignableFrom(Boolean.class))
			value = true;		
		else if (clazz.isAssignableFrom(LocalDate.class))
			value = randomDate();
		else if (clazz.isAssignableFrom(LocalDateTime.class))
			value = randomDateTime();
		else if (clazz.isAssignableFrom(LocalTime.class))
			value = randomTime();
		else if (clazz.isAssignableFrom(Timestamp.class))
			value = randomTimestamp();
		else if (clazz.isAssignableFrom(XMLGregorianCalendar.class))
			value = randomXMLCalendar();
		else if (clazz.isAssignableFrom(String.class))
			value = randomAlphabetic(Math.min(strLength,20));
		return (Any) value;
	}

	private List<?> buildList(Type type) {
//        ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
//    	Type type = f.getGenericType();
       	Class<?> clazz = null;
		Type aType = type;
		Type rawType = type;
		if(type instanceof ParameterizedType){
    		ParameterizedType pType = (ParameterizedType)type;
    		aType = pType.getActualTypeArguments()[0];
    		rawType = pType.getRawType();
    	}
		if(aType instanceof ParameterizedType){
    		ParameterizedType pType = (ParameterizedType)aType;
    		rawType = pType.getRawType();
    		aType = pType.getActualTypeArguments()[0];
    	}
		clazz = (Class<?>)rawType;
			
//		if(type instanceof ParameterizedType){
//    		ParameterizedType ptype = (ParameterizedType)type;
//    		type = ptype.getActualTypeArguments()[0];
// 			clazz = (Class<?>)type;
//		}
		logger.debug("building list of " + clazz);
		List<Object> list = new ArrayList<Object>();
		Object o = null;
		if (clazz.isAssignableFrom(JAXBElement.class))
		{
			o = buildJAXBElement(aType);
			list.add(o);
			list.add(buildJAXBElement(aType));
		}
		else
		{
			o = build((Class<?>)aType);
			list.add(o);
			list.add(build((Class<?>)aType));
		}
        return list;
	}

	@SuppressWarnings("unchecked")
	private JAXBElement<Object> buildJAXBElement(Type type) {
		Type atype = type;
		if(type instanceof ParameterizedType){
    		ParameterizedType ptype = (ParameterizedType)type;
    		atype = ptype.getActualTypeArguments()[0];
    	}
		Class<Object> clazz = (Class<Object>)atype;
		logger.info("building JAXB of " + clazz);
		Object j = build(clazz);
		QName qname = new QName("qname");
		JAXBElement<Object> element = new JAXBElement<Object>(qname , clazz, j);
		return element;
	}

	static LocalDate randomDate() {
		return LocalDate.ofEpochDay(RandomUtils.nextInt(0,10000));
	}

	static LocalDateTime randomDateTime() {
		return LocalDateTime.ofEpochSecond(RandomUtils.nextInt(0,10000), 0, ZoneOffset.UTC);
	}

	static LocalTime randomTime() {
		return LocalTime.ofNanoOfDay(RandomUtils.nextInt(0,1000000));
	}

	static Timestamp randomTimestamp() {
		// TODO Auto-generated method stub
		return new Timestamp(RandomUtils.nextLong(0, 1000000));
	}

	static XMLGregorianCalendar randomXMLCalendar() {
		// TODO Auto-generated method stub
		XMLGregorianCalendar c = new XMLGregorianCalendarImpl();
		c.setYear(RandomUtils.nextInt(0, 1000000));
		return c;
	}

	static Calendar randomCalendar() {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(RandomUtils.nextLong(0, 1000000));
		return c;
	}

}
