package com.kpy.maven.plugin.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.twdata.maven.mojoexecutor.MojoExecutor.Element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

/**
 *
 * @author johnny
 * @date 2018/6/27
 */
public class ElementUtils {

    public static final String ANY_NAME = "anyTag";

    /**
     * 转换参数
     *
     * @param config
     * @return
     */
    public static Element[] parseConfiguration(Map<String, Object> config) {
        List<Element> ret = new ArrayList<>(config.size());
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            //对于集合中的元素可能是：基本类型、对象、对象列表
            if (entry.getValue() instanceof Map) {
                //对于对象，递归处理
                Element[] elements = parseConfiguration((Map<String, Object>) entry.getValue());
                ret.add(new Element(name(entry.getKey()), elements));
            } else if (entry.getValue() instanceof List) {
                //对于列表中的元素，可能是：基本类型、对象
                //值是列表，那么每一个元素一定是一个对象（xml表示法，数组中不可能是简单数据类型）

                List<Element> elements = new ArrayList<Element>();
                for (Object item : (List) entry.getValue()) {
                    if (item instanceof Map) {
                        Element[] eleList = parseConfiguration((Map<String, Object>) item);
                        //对于对象的对象名，json没法表示，故伪造一个。其只是组织的形式，不具有实际意义
                        elements.add(new Element(name(ANY_NAME), eleList));
                    } else {
                        elements.add(new Element(name(ANY_NAME), item.toString()));
                    }
                }
                ret.add(new Element(name(entry.getKey()), elements.toArray(new Element[elements.size()])));
            } else {
                //最基本单元
                ret.add(new Element(name(entry.getKey()), entry.getValue().toString()));
            }
        }
        return ret.toArray(new Element[ret.size()]);
    }


    public final static Element[] parseConfiguration(String collectionKey, List configs) {
        ObjectMapper om = new ObjectMapper();

        Map<String, Object> c = new HashMap(1);

        try {
            String json = om.writeValueAsString(configs);
            c.put(collectionKey, om.readValue(json, List.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parseConfiguration(c);

    }


}
