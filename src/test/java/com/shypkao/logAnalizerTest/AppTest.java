package com.shypkao.logAnalizerTest;

import com.shypkao.logAnalizer.application.Main;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class AppTest {
    @Test
    public void runBatchJobTest() throws Exception {
        Resource resource = new ClassPathResource("jsonTest.json");
        String filePath = resource.getFile().getAbsolutePath();;
        String[] args = new String[]{"--filePath="+filePath,"--chunkSize=100"};
        Main.runBatchJob(args);
    }
}
