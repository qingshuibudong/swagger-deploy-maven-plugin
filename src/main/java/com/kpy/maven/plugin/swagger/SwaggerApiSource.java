package com.kpy.maven.plugin.swagger;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * swagger 的配置对象，具体参考 https://github.com/kongchen/swagger-maven-plugin/tree/swagger-maven-plugin-3.1.7
 * Created by johnny on 2018/6/27.
 */
@Data
public class SwaggerApiSource {

    private boolean springmvc;


    @Parameter(required = true)
    private List<String> locations;


    private List<String> schemes;

    private String host;

    private String basePath;

//    private String descriptionFile;

    private SwaggerInfo info;


//    private Object securityDefinitions;

    private String templatePath;

    private String outputPath;

    private String outputFormats = "json";

    private String swaggerDirectory;

//    private String swaggerFileName;

//    private String swaggerApiReader="com.github.kongchen.swagger.docgen.reader.JaxrsReader";


    //其他参数忽略


}
