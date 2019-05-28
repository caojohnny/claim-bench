package com.gmail.woodyc40.claimbench;

import org.bukkit.plugin.java.JavaPlugin;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ClaimBench extends JavaPlugin {
    @Override
    public void onEnable() {
        // Stole this from: https://stackoverflow.com/questions/35574688/how-to-run-a-jmh-benchmark-in-maven-using-execjava-instead-of-execexec
        URLClassLoader classLoader = (URLClassLoader) ClaimBench.class.getClassLoader();
        StringBuilder classpath = new StringBuilder();
        for (URL url : classLoader.getURLs())
            classpath.append(url.getPath()).append(File.pathSeparator);
        System.setProperty("java.class.path", classpath.toString());

        Options options = new OptionsBuilder()
                .include(Bench.class.getSimpleName())
                .forks(0)
                .build();

        try {
            new Runner(options).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
}
