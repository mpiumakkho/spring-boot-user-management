package com.mp.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebApiApplication {

    public static String env;
    @Value("${env}")
    public void setEnv(String setEnv) {
        WebApiApplication.env = setEnv;
    }

    public static String site;
    @Value("${site}")
    public void setSite(String site) {
        WebApiApplication.site = site;
    }

    public static String project;
    @Value("${project}")
    public void setProject(String project) {
        WebApiApplication.project = project;
    }

    public static String coreApiServer;
    @Value("${core.api.url}")
    public void setCoreApiServer(String coreApiServer) {
        WebApiApplication.coreApiServer = coreApiServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return (args) -> {
            try {
                System.out.println("------------------------------- " + site + " - " + project + " -------------------------------");
                System.out.printf("%-25s : %s\n", "env", env);
                System.out.printf("%-25s : %s\n", "site", site);
                System.out.printf("%-25s : %s\n", "project", project);
                System.out.printf("%-25s : %s\n", "coreApiServer", coreApiServer);
                System.out.println("Web API Ready! Connecting to Core API...");
            } catch (Exception ex) {
                System.err.println("Error during startup: " + ex.getMessage());
            } finally {
                System.out.println("------------------------------------------------------------------------------------");
            }
        };
    }
}
