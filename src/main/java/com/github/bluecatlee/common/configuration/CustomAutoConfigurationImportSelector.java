package com.github.bluecatlee.common.configuration;

import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomAutoConfigurationImportSelector extends AutoConfigurationImportSelector {

    @Override
    protected Set<String> getExclusions(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        Set<String> exclusions = super.getExclusions(metadata, attributes);
        // Exclude Activiti via profile
        // if (!Arrays.asList(getEnvironment().getActiveProfiles()).contains("activiti")) {
            List<String> autoConfigClassNames = SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, getBeanClassLoader());
            exclusions.addAll(autoConfigClassNames.stream().filter(ac -> ac.startsWith("org.activiti")).collect(Collectors.toSet()));
        // }
        return exclusions;
    }

}
