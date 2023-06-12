package com.kumoco.productorder.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Log4j2
public class ProfileConfig {

    final BuildProperties buildProperties;

    public ProfileConfig(final BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Profile(value = "dev")
    @Bean
    public void devConfig() {

        log.info("Successfully loaded the local dev environment.");
        buildInfo();
    }

    @Profile(value = "sit")
    @Bean
    public void sitConfig() {

        log.info("Successfully loaded the SIT environment.");
        buildInfo();
    }

    @Profile(value = "e2e")
    @Bean
    public void e2eConfig() {

        log.info("Successfully loaded the E2E environment.");
        buildInfo();
    }

    @Profile(value = "uat")
    @Bean
    public void uatConfig() {

        log.info("Successfully loaded the UAT environment.");
        buildInfo();
    }

    @Profile(value = "prod")
    @Bean
    public void productionConfig() {

        log.info("Successfully loaded the PRODUCTION environment.");
        buildInfo();
    }

    @Profile(value = "dev-sit")
    @Bean
    public void devSitConfig() {

        log.info("Successfully loaded the DEV-SIT environment.");
        buildInfo();
    }

    private void buildInfo() {

        log.info("Build Info: " + buildProperties.getArtifact());
        log.info("Build Version: " + buildProperties.getVersion());
        log.info("Build Date/Time: " + buildProperties.getTime());
    }
}
