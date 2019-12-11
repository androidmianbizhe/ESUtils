package com.example.es.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ESIndexSettingAndMapping {

    private Setting setting;

    private Mappings mappings;

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public Mappings getMappings() {
        return mappings;
    }

    public void setMappings(Mappings mappings) {
        this.mappings = mappings;
    }

    @Data
    @AllArgsConstructor
    public static class Setting {
        Integer number_of_shards;
        Integer number_of_replicas;

        public Integer getNumber_of_shards() {
            return number_of_shards;
        }

        public void setNumber_of_shards(Integer number_of_shards) {
            this.number_of_shards = number_of_shards;
        }

        public Integer getNumber_of_replicas() {
            return number_of_replicas;
        }

        public void setNumber_of_replicas(Integer number_of_replicas) {
            this.number_of_replicas = number_of_replicas;
        }
    }

    @Data
    public static class Mappings {

        private String documentName;

        private List<Property> properties;

        public String getDocumentName() {
            return documentName;
        }

        public void setDocumentName(String documentName) {
            this.documentName = documentName;
        }

        public List<Property> getProperties() {
            return properties;
        }

        public void setProperties(List<Property> properties) {
            this.properties = properties;
        }

        @Data
        public static class Property {
            private String properName;
            private String properType;
            private String analyzer;
            private String format;
            private String store;
            private String index;

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }

            public String getProperName() {
                return properName;
            }

            public void setProperName(String properName) {
                this.properName = properName;
            }

            public String getProperType() {
                return properType;
            }

            public void setProperType(String properType) {
                this.properType = properType;
            }

            public String getAnalyzer() {
                return analyzer;
            }

            public void setAnalyzer(String analyzer) {
                this.analyzer = analyzer;
            }

            public String getFormat() {
                return format;
            }

            public void setFormat(String format) {
                this.format = format;
            }

            public String getStore() {
                return store;
            }

            public void setStore(String store) {
                this.store = store;
            }
        }
    }
}
