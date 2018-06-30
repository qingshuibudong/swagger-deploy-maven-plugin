package com.kpy.maven.plugin.swagger;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Created by johnny on 2018/6/27.
 */
@Mojo(name = "help")
public class MojoHelp extends AbstractMojo {


    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("帮助文档\n" +
                "generate\n" +
                "\t生成文档\n" +
                "\n" +
                "deploy\n" +
                "\t生成文档，并进行部署\n" +
                "\n" +
                "配置参数:\n" +
                "swaggerConfig\t具体参数参考 https://github.com/kongchen/swagger-maven-plugin/tree/swagger-maven-plugin-3.1.7");

    }
}
