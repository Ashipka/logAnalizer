package com.shypkao.logAnalizer.application.batch;

import com.shypkao.logAnalizer.model.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

import static com.shypkao.logAnalizer.application.Main.*;
import static com.shypkao.logAnalizer.application.batch.SQL.SqlScript.INSERT_LOG_EVENT_ROW_SQL;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private static final String EVENT_LOG_READER = "eventLogReader";
    private static final Integer CHUNK_SIZE = 10;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Environment environment;

    @Bean
    public FlatFileItemReader<LogEvent> reader() {
        FlatFileItemReader<LogEvent> reader = new FlatFileItemReader<>();
        reader.setName(EVENT_LOG_READER);
        reader.setResource( new FileSystemResource(environment.getProperty(FILE_PATH_NAME)));
        reader.setLineMapper(new LogEventJsonLineMapper());
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<LogEvent> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<LogEvent>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(INSERT_LOG_EVENT_ROW_SQL)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importDataFromFileToDB(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get(JOB_IMPORT_DATA_FROM_FILE_TO_DB)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<LogEvent> writer) {
        return stepBuilderFactory.get("step1")
                .<LogEvent, LogEvent>chunk(getChunkSize())
                .reader(reader())
                .writer(writer)
                .build();
    }

    private int getChunkSize() {
        if(!StringUtils.isEmpty(environment.getProperty(CHUNK_SIZE_NAME))){
            return Integer.parseInt(environment.getProperty(CHUNK_SIZE_NAME));
        }
        return CHUNK_SIZE;
    }
}

