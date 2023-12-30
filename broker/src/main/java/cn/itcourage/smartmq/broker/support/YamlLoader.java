package cn.itcourage.smartmq.broker.support;

import cn.itcourage.smartmq.broker.config.SmartMQConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class YamlLoader {

    private final static String CONFIG_FILE_NAME = "config.yaml";

    public static SmartMQConfig loadConfig() {
        Yaml yaml = new Yaml();
        InputStream inputStream = SmartMQConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        return yaml.loadAs(inputStream, SmartMQConfig.class);
    }

}
