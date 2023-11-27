package com.simplesdetal.prm.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;

public class RepositoryUtil {

    private RepositoryUtil() {}

    static <T> List<T>  findText(final EntityManager entityManager, final String text, final Set<String> fields, final Class<T> clazz) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        final Root<T> root = criteriaQuery.from(clazz);

        final String textSearch = "%" + text.toUpperCase() + "%";

        final Set<String> allFields = RepositoryUtil.getFields(clazz);
        final List<String> validFields = (fields == null)
                ? allFields.stream().toList()
                : allFields.stream()
                .filter(fields::contains)
                .toList();


        final List<Predicate> conditions = new ArrayList<>();
        validFields.forEach(field ->
                conditions.add(
                        criteriaBuilder.like(criteriaBuilder.upper(root.get(field).as(String.class)), textSearch)
                )
        );
        final Predicate finalCondition = criteriaBuilder.or(conditions.toArray(new Predicate[0]));
        criteriaQuery.where(finalCondition);


        List<Selection<?>> selections = new ArrayList<>();
        for (String field : validFields) {
            selections.add(root.get(field));
        }

        criteriaQuery.multiselect(selections);

        final List<Object[]> result = entityManager.createQuery(criteriaQuery).getResultList();

        final List<T> list = new ArrayList<>();
        for (final Object[]  r: result) {
            final T obj = BeanUtils.instantiateClass(clazz);
            list.add(obj);
            for (int i = 0; i < r.length; i++) {
                final Object value = r[i];
                final String f = validFields.get(i);
                final String mn = "set" + f.substring(0, 1).toUpperCase() + f.substring(1);
                if (Objects.isNull(value)) {
                    continue;
                }
                final Method m = getClassMethod(clazz, mn, value);
                if (Objects.nonNull(m)) {
                    ReflectionUtils.makeAccessible(m);
                    ReflectionUtils.invokeMethod(m, obj, value);
                }
            }
        }

        return list;
    }

    static <T> Method getClassMethod(final Class<T> clazz, final String mn, final Object value) {
        final Method m = ReflectionUtils.findMethod(clazz, mn, value.getClass());
        if (Objects.isNull(m) && value instanceof java.sql.Date || value instanceof java.sql.Timestamp) {
           return ReflectionUtils.findMethod(clazz, mn, Date.class);
        }

        return m;
    }

    static Set<String> getFields(final Class<?> clazz) {

           return Arrays.stream(clazz.getDeclaredFields())
                .filter(RepositoryUtil::isNonTransientNonStaticField)
                .map(Field::getName)
                .collect(HashSet::new, Set::add, Set::addAll);
    }

    static boolean isNonTransientNonStaticField(final Field field) {
        return     !isStatic(field.getModifiers())
                && !isTransient(field.getModifiers())
                && Objects.isNull(field.getAnnotation(jakarta.persistence.Transient.class))
                && isWrapperOrEnumOrDate(field.getType());
    }


    static boolean isWrapperOrEnumOrDate(Class<?> clazz) {
        return isWrapperType(clazz) ||
                clazz.isEnum() ||
                clazz.equals(Date.class);
    }
    static boolean isWrapperType(Class<?> clazz) {
        return clazz.equals(Boolean.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(String.class);
    }
}
