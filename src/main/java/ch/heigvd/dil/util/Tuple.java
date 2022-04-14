package ch.heigvd.dil.util;

import java.util.Map;

/**
 * Contient toutes les données nécessaires pour la construction d'un site statique
 */
public class Tuple {
    private final Map<String, Object> yamlData;
    private final String htmlData;

    public Tuple(Map<String, Object> yamlData, String htmlData) {
        this.yamlData = yamlData;
        this.htmlData = htmlData;
    }

    public Map<String, Object> getYamlData() {
        return yamlData;
    }

    public String getHtmlData() {
        return htmlData;
    }
}
