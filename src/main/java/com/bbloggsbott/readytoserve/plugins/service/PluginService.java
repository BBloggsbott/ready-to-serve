package com.bbloggsbott.readytoserve.plugins.service;

import com.bbloggsbott.readytoserve.application.service.SettingsService;
import com.bbloggsbott.readytoserve.plugins.dto.PluginArgDTO;
import com.bbloggsbott.readytoserve.plugins.dto.PluginDTO;
import com.bbloggsbott.readytoserve.plugins.dto.PluginsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PluginService {

    @Autowired
    private SettingsService settingsService;

    private PluginsDTO plugins;

    private final ObjectMapper mapper = new ObjectMapper();

    private HashMap<String, HashMap<String, Object>> enpointPluginMapping;

    private final String CLASS_KEY = "class";
    private final String METHOD_KEY = "method";
    private final String PLUGIN_DTO_KEY = "dto";

    @PostConstruct
    private void init() throws IOException, NoSuchMethodException, ClassNotFoundException{
        plugins = new PluginsDTO();
        enpointPluginMapping = new HashMap<>();
        loadPluginsConfig(plugins);
        for (PluginDTO plugin: plugins){
            mapEndpointToPlugin(plugin, enpointPluginMapping);
        }
    }

    private void loadPluginsConfig(PluginsDTO plugins) throws IOException {
        String pluginsDir = settingsService.getSettings().getPluginsDirectory();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File configFile = new File(settingsService.getSettings().getPluginsConfigFile());
        plugins.addAll(mapper.readValue(configFile, PluginsDTO.class));
        plugins.forEach(pluginDTO -> {
            pluginDTO.setJarfile(Paths.get(pluginsDir, pluginDTO.getJarfile()).toString());
            String[] methodParts = pluginDTO.getMethod().split("\\.");
            String methodName = methodParts[methodParts.length-1];
            pluginDTO.setMethodName(methodName);
            pluginDTO.setClassName(pluginDTO.getMethod().substring(0, pluginDTO.getMethod().length() - methodName.length() - 1));
            if (pluginDTO.getArgs() == null){
                pluginDTO.setArgs(new ArrayList<>());
            }
        });
    }

    private void mapEndpointToPlugin(PluginDTO plugin, HashMap<String, HashMap<String, Object>> enpointPluginMapping) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException {
        File jarFile = new File(plugin.getJarfile());
        URLClassLoader child = new URLClassLoader(
                new URL[] {jarFile.toURI().toURL()},
                this.getClass().getClassLoader()
        );
        Class classToLoad = Class.forName(plugin.getClassName(), true, child);
        ArrayList<Class> parameterTypes = new ArrayList<>();
        for (PluginArgDTO arg: plugin.getArgs()){
            parameterTypes.add(getClassFromName(arg.getType()));
        }
        Class[] paramsArray = new Class[parameterTypes.size()];
        parameterTypes.toArray(paramsArray);
        Method method = classToLoad.getDeclaredMethod(plugin.getMethodName(), paramsArray);
        HashMap<String, Object> pluginMap = new HashMap<>();
        pluginMap.put(CLASS_KEY, classToLoad);
        pluginMap.put(METHOD_KEY, method);
        pluginMap.put(PLUGIN_DTO_KEY, plugin);
        enpointPluginMapping.put(StringUtils.strip(plugin.getEndpoint(), "/"), pluginMap);
    }

    public Object getResponse(String endpoint, Map<String, Object> requestParams, Map<String, Object> requestBody) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, JsonProcessingException {
        String cleanedEndpoint = StringUtils.strip(endpoint, "/");
        if (enpointPluginMapping.containsKey(cleanedEndpoint)){
            HashMap<String, Object> map = enpointPluginMapping.get(cleanedEndpoint);
            Class loadedClass = (Class) map.get(CLASS_KEY);
            Method method = (Method) map.get(METHOD_KEY);
            PluginDTO plugin = (PluginDTO) map.get(PLUGIN_DTO_KEY);
            ArrayList<Object> params = new ArrayList<>();
            for (PluginArgDTO arg: plugin.getArgs()){
                Class argClass = getClassFromName(arg.getType());
                if (arg.getRequestParam()){
                    if (argClass == String.class){
                        params.add(requestParams.get(arg.getName()).toString());
                    } else{
                        params.add(mapper.readValue(requestParams.get(arg.getName()).toString(), argClass));
                    }
                } else {
                    if (argClass == String.class) {
                        params.add(requestBody.get(arg.getName()).toString());
                    } else {
                        params.add(mapper.readValue(requestBody.get(arg.getName()).toString(), argClass));
                    }
                }
            }
            Object result = method.invoke(loadedClass.getDeclaredConstructor().newInstance(), params.toArray());
            log.info("Responding with {}", result);
            return result;
        }
        log.info("Responding with null");
        return null;
    }

    private Class getClassFromName(String className) throws ClassNotFoundException {
        switch (className){
            case "byte": return byte.class;
            case "short": return short.class;
            case "int": return int.class;
            case "long": return long.class;
            case "float": return float.class;
            case "double": return double.class;
            case "boolean": return boolean.class;
            case "char": return char.class;
        }
        return Class.forName(className);
    }

}
