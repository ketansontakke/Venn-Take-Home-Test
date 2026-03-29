package com.example.demo;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.example.demo.service.LoadServiceTest;
import com.example.demo.repository.LoadRepositoryTest;
import com.example.demo.processor.FileProcessorTest;
import com.example.demo.util.LoadUtilsTest;

@Suite
@SelectClasses({
        LoadServiceTest.class,
        LoadRepositoryTest.class,
        FileProcessorTest.class,
        LoadUtilsTest.class
})
public class AllTestsSuite {
}
