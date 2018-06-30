package com.kpy.maven.plugin.swagger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * api文档生成
 * Created by johnny on 2018/6/27.
 */
@Mojo(name = "generate")
public class MojoDocGenerator extends AbstractMojo {

    public static final String UTF_8 = "utf-8";
    public static final String STR_IN_TEMPLATE_FOR_REPLACE = ">\\s*markdown";
    public static final String STR_1_IN_MARKDOWN_FOR_REPLACE = ">\\s*security";
    public static final String STR_2_IN_MARKDOWN_FOR_REPLACE = ">\\s*operation";
    public static final String TEMP_TEMPLATE_BASE_DIR = "swagger-deploy";
    //    public static final String KEY_TEMPLATE_PATH = "templatePath";
//    public static final String KEY_API_SOURCES = "apiSources";
    @Component
    protected BuildPluginManager pluginManager;
    @Component
    private MavenProject mavenProject;
    @Component
    private MavenSession mavenSession;
    /**
     * swagger的配置
     */
    @Parameter(required = true)
    private List<SwaggerApiSource> apiSources;

    /**
     * 模板的临时输出目录，默认为项目的target目录
     */
    @Parameter(property = "project.build.directory")
    private String tempTemplateBaseDir;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("==========begin generate============");
        generateDoc();
        getLog().info("==========end generate============");
    }

    protected String getOutputDocument() {
        if (CollectionUtils.isNotEmpty(apiSources)) {
            SwaggerApiSource apiSource = apiSources.get(0);
            return apiSource.getOutputPath();
        }
        return null;
    }

    /**
     * 生成文档
     */
    protected void generateDoc() {

        configDocTemplate();

        Element[] config = ElementUtils.parseConfiguration("apiSources", apiSources);

        try {
            executeMojo(
                    plugin(
                            groupId("com.github.kongchen"),
                            artifactId("swagger-maven-plugin"),
                            version("3.1.7")
                    ),
                    goal("generate"),
                    configuration(
                            config
                    ),
                    executionEnvironment(
                            mavenProject,
                            mavenSession,
                            pluginManager
                    )
            );
        } catch (MojoExecutionException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 配置文档模板
     */
    protected void configDocTemplate() {
        for (SwaggerApiSource apiSource : apiSources) {
            if (StringUtils.isBlank(apiSource.getTemplatePath())) {
                genTemplateFile();
                String fn = tempTemplateBaseDir + "/" + TEMP_TEMPLATE_BASE_DIR + "/strapdown.html.hbs";
                getLog().info("config with temp document template file: " + fn);
                apiSource.setTemplatePath(fn);
            } else {
                getLog().info("document template file: " + apiSource.getTemplatePath());
            }
        }
    }

    /**
     * 生成临时模板
     *
     * @return
     */
    protected void genTemplateFile() {
        String tmpDir = tempTemplateBaseDir + "/" + TEMP_TEMPLATE_BASE_DIR;
        //copy files from jar file
        String basePath = "/templates/";
        File templateFile = copyFile(basePath + "strapdown.html.hbs", tmpDir);
        File markdownFile = copyFile(basePath + "markdown.hbs", tmpDir);
        File operationFile = copyFile(basePath + "operation.hbs", tmpDir);
        File securityFile = copyFile(basePath + "security.hbs", tmpDir);

        //replace template
        /*File file = new File(templateFile);
        try {
            String content = FileUtils.readFileToString(file, UTF_8);
            content = content.replaceAll(STR_IN_TEMPLATE_FOR_REPLACE, ">" + FilenameUtils.getBaseName(markdownFile));
            FileUtils.writeStringToFile(file, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //replace markdown
        File mdFile = new File(markdownFile);
        try {
            String mdContent = FileUtils.readFileToString(mdFile, UTF_8);
            mdContent = mdContent.replaceAll(STR_1_IN_MARKDOWN_FOR_REPLACE, ">" + FilenameUtils.getBaseName(securityFile));
            mdContent = mdContent.replaceAll(STR_2_IN_MARKDOWN_FOR_REPLACE, ">" + FilenameUtils.getBaseName(operationFile));
            FileUtils.writeStringToFile(mdFile, mdContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/


    }

    /**
     * 从jar中拷贝到临时文件
     *
     * @param classpathFile
     * @return
     */
    protected File copyFile(String classpathFile, String destDir) {
        InputStream resourceAsStream = getClass().getResourceAsStream(classpathFile);
        String name = FilenameUtils.getName(classpathFile);
        File destFile = new File(destDir, name);
        try {
            FileUtils.copyInputStreamToFile(resourceAsStream, destFile);
            return destFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
