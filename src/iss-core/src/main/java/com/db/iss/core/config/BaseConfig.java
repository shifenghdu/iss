package com.db.iss.core.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

@Service("s_config")
public class BaseConfig {

	private Document document;

	public static final String CONFIG_ARGS = "iss.config";

	public static final String SYSPATH = "iss.path";

	public BaseConfig() {
		SAXReader reader = new SAXReader();
		try {
			try {
				document = reader.read(new File(System.getProperty(SYSPATH)
						+ "/etc/" + System.getProperty(CONFIG_ARGS)));
			} catch (Exception e) {
				document = reader.read(getClass().getClassLoader()
						.getResourceAsStream("config.xml"));
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public Document getDocument() {
		return document;
	}

	public Element getPluginConfig(String name) {
		@SuppressWarnings("unchecked")
		List<Element> list = document.getRootElement().element("plugins")
				.elements("plugin");
		for (Element plugin : list) {
			if (plugin.attributeValue("name").toString().equals(name)) {
				return plugin;
			}
		}
		return null;
	}

	public Map<String, String> getNeighborAddr() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("node", "192.168.2.107:1234");
		return map;
	}

}
