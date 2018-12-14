package com.example.springsecurity.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

	private final EntityManager entityManager;

	public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.entityManager = em;
	}

	@Override
	public Page<T> findWithConditionAnd(T example, Pageable pageable) {
		return findAll(BaseRepositorySpecs.byAuto(entityManager, example, true), pageable);
	}

	@Override
	public Page<T> findWithConditionOr(T example, Pageable pageable) {
		return findAll(BaseRepositorySpecs.byAuto(entityManager, example, false), pageable);
	}

}
