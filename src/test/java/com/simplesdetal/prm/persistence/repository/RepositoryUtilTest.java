package com.simplesdetal.prm.persistence.repository;

import com.simplesdetal.prm.persistence.model.CargoType;
import com.simplesdetal.prm.persistence.model.Profissional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryUtilTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RepositoryUtil repositoryUtil;


    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private  CriteriaQuery<Object[]> criteriaQuery;

    @Mock
    private TypedQuery<Object[]> typedQuery;
    @Mock
    private Root<Profissional> root;
    @Mock
    private Path<?> path;

    @Test
    void testFindText() {

        final Class<Profissional> clazz = Profissional.class;
        final List<Object[]> resultMock = new ArrayList<>();

        Object[] cols = {"Joao", null};
        resultMock.add(cols);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Object[].class)).thenReturn(criteriaQuery);
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(criteriaQuery.from(clazz)).thenReturn(root);
        when(typedQuery.getResultList()).thenReturn(resultMock);
        when(root.get("nome")).thenReturn((Path<Object>) path);
        when(root.get("cargo")).thenReturn((Path<Object>) path);
        when(path.as(any())).thenReturn((Path<Object>) path);


        String text = "test";
        Set<String> fields = new HashSet<>();
        fields.add("nome");
        fields.add("cargo");


        List<Profissional> result = repositoryUtil.findText(entityManager, text, fields, clazz);

        assertNotNull(result);
    }

    @Test
    void testFindTextFieldsNull() {

        final Class<Profissional> clazz = Profissional.class;
        final List<Object[]> resultMock = new ArrayList<>();

        Object[] cols = {"Joao", null};
        resultMock.add(cols);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Object[].class)).thenReturn(criteriaQuery);
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(criteriaQuery.from(clazz)).thenReturn(root);
        when(typedQuery.getResultList()).thenReturn(resultMock);
        when(root.get("id")).thenReturn((Path<Object>) path);
        when(root.get("nome")).thenReturn((Path<Object>) path);
        when(root.get("cargo")).thenReturn((Path<Object>) path);
        when(root.get("nascimento")).thenReturn((Path<Object>) path);
        when(root.get("created")).thenReturn((Path<Object>) path);
        when(path.as(any())).thenReturn((Path<Object>) path);


        String text = "test";
        Set<String> fields = null;

        List<Profissional> result = repositoryUtil.findText(entityManager, text, fields, clazz);

        assertNotNull(result);
    }

    @Test
    void testGetFields() {
        final Set<String> result = repositoryUtil.getFields(Profissional.class);
        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    void testIsNonTransientNonStaticField() throws NoSuchFieldException {
        final Field field = Profissional.class.getDeclaredField("nome");
        final boolean result = repositoryUtil.isNonTransientNonStaticField(field);
        assertTrue(result);
    }

    @Test
    void testIsWrapperOrEnumOrDate() {
        final boolean result = repositoryUtil.isWrapperOrEnumOrDate(CargoType.class);
        assertTrue(result);
    }

    @Test
    void testIsWrapperType() {
        final boolean result = repositoryUtil.isWrapperType(String.class);
        assertTrue(result);
    }
}


