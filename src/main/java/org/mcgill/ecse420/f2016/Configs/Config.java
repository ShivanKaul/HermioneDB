package org.mcgill.ecse420.f2016.Configs;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;

public class Config {
    public final Environment environment;
    public final DatabaseConfig dbConfig;

    public Config(String envLocation, EnvironmentConfig envConfig, DatabaseConfig dbConfig) throws DatabaseException {
        this.environment = new Environment(new File(envLocation), envConfig);
        this.dbConfig = dbConfig;
        dbConfig.setDeferredWrite(true);
    }
}
