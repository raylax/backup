package com.jiaomatech.blackbook.common.process;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.ClassMetadata;

import java.util.Set;

public class AutoBeanScanner extends ClassPathBeanDefinitionScanner {

    private BeanDefinitionRegistry registry;

    public AutoBeanScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        super.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            if (!classMetadata.isInterface()) {
                return false;
            }
            String className = classMetadata.getClassName();
            try {
                Class<?> clazz = Class.forName(className);
                return clazz.isAnnotationPresent(AutoBean.class);
            } catch (ClassNotFoundException e) {
                return false;
            }
        });
        this.registry = registry;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : holders) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            Class<?> clazz = getBeanClass(definition);
            definition.getConstructorArgumentValues().addGenericArgumentValue(clazz);
            definition.setBeanClass(AutoBeanFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(clazz.getSimpleName(), definition);
        }
        return holders;
    }

    private Class<?> getBeanClass(BeanDefinition definition) {
        try {
            return Class.forName(definition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
