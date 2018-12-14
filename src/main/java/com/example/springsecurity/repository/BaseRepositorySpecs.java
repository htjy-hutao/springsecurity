package com.example.springsecurity.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ReflectionUtils;

public class BaseRepositorySpecs {

	public static final String EMPTY = "";

	public static <T> Specification<T> byAuto(final EntityManager entityManager, final T example, final Boolean flag) {
		return new Specification<T>() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				Class<T> clazz = (Class<T>) example.getClass();
				EntityType<T> entity = entityManager.getMetamodel().entity(clazz);
				for (Attribute<T, ?> attr : entity.getDeclaredAttributes()) {
					Object attrValue = getValue(example, attr);
					if (attrValue != null) {
						if (attr.getJavaType() == String.class) {
							String value = String.valueOf(attrValue).trim();
							if (!EMPTY.equals(value)) {
								predicates.add(cb.like(root.get(attribute(entity, attr.getName(), String.class)), pattern(value)));
							}
						} else {
							predicates.add(cb.equal(root.get(attribute(entity, attr.getName(), attrValue.getClass())), attrValue));
						}
					}
				}
				if (flag) {
					return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[predicates.size()]));
				} else {
					return predicates.isEmpty() ? cb.disjunction() : cb.or(predicates.toArray(new Predicate[predicates.size()]));
				}
			}
		};
	}

	private static <T> Object getValue(T example, Attribute<T, ?> attr) {
		return ReflectionUtils.getField((Field) attr.getJavaMember(), example);
	}

	private static <E, T> SingularAttribute<T, E> attribute(EntityType<T> entity, String fieldName, Class<E> fieldClass) {
		return entity.getDeclaredSingularAttribute(fieldName, fieldClass);
	}

	private static String pattern(String str) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("%").append(str).append("%");
		return buffer.toString();
	}

}
