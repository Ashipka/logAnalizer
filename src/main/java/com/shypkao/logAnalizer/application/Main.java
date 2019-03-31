package com.shypkao.logAnalizer.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String JOB_IMPORT_DATA_FROM_FILE_TO_DB = "importDataFromFileToDB";
    //---- Command line properties
    public static final String FILE_PATH_NAME = "filePath";
    public static final String CHUNK_SIZE_NAME = "chunkSize";
    public static final String ALERT_EVENT_INTERVAL_NAME = "alertEventInterval";


    public static void main(String[] args) throws Exception {
        runBatchJob(args);
    }

    public static void runBatchJob(String[] args) throws Exception {
        SimpleCommandLinePropertySource commandLinePropertySource = getCommandLineProperties(args);
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        context.getEnvironment().getPropertySources().addFirst(commandLinePropertySource);
        JobLauncher jobLauncher = context.getBean("jobLauncher", JobLauncher.class);
        Properties properties = new Properties();
        JobParameters jobParameters = new DefaultJobParametersConverter().getJobParameters(properties);
        Job job = context.getBean(JOB_IMPORT_DATA_FROM_FILE_TO_DB, Job.class);
        jobLauncher.run(job, jobParameters);
    }

    private static SimpleCommandLinePropertySource getCommandLineProperties(String[] args) throws Exception {
        SimpleCommandLinePropertySource commandLinePropertySource = new SimpleCommandLinePropertySource(args);
        logCommandLineProperties(commandLinePropertySource);
        validateCommandLineProperties(commandLinePropertySource);
        return commandLinePropertySource;
    }

    private static void validateCommandLineProperties(SimpleCommandLinePropertySource commandLinePropertySource) throws Exception {
        if (notValidCommandLineParamNames(commandLinePropertySource)){
            throw new Exception("Parameters are not recognized, use --" + FILE_PATH_NAME + "=value --" +
                    CHUNK_SIZE_NAME+"[Optional]=value --"+ALERT_EVENT_INTERVAL_NAME+"[Optional]=value");
        }
        if (!commandLinePropertySource.containsProperty(FILE_PATH_NAME)) {
            throw new Exception("Need at least json file path parameter, should be --" + FILE_PATH_NAME + "=value");
        }
    }

    private static boolean notValidCommandLineParamNames(SimpleCommandLinePropertySource commandLinePropertySource) {
        return Arrays.stream(commandLinePropertySource.getPropertyNames())
                .anyMatch(paramName -> !isValidParamName(paramName));
    }

    private static boolean isValidParamName(String paramName) {
        return paramName.equals(FILE_PATH_NAME) ||paramName.equals(CHUNK_SIZE_NAME) ||paramName.equals(ALERT_EVENT_INTERVAL_NAME);
    }

    private static void logCommandLineProperties(SimpleCommandLinePropertySource commandLinePropertySource) {
        Arrays.stream(commandLinePropertySource.getPropertyNames())
                .forEach(s ->
                        log.debug(String.format("Application param:%s => %s%n", s, commandLinePropertySource.getProperty(s))));
    }
}
