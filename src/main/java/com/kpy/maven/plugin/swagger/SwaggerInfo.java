package com.kpy.maven.plugin.swagger;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Created by johnny on 2018/6/27.
 */
@Data
public class SwaggerInfo {

    @Parameter(required = true)
    private String title;

    @Parameter(required = true)
    private String version;

    private String description;
}
