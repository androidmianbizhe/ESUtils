package com.example.es.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.List;

@Data
public class ESIndexSettingAndMapping {

    private Setting setting;

    private Mappings mappings;

    @Data
    @AllArgsConstructor
    public static class Setting {
        Integer number_of_shards;
        Integer number_of_replicas;
    }

    @Data
    public static class Mappings {

        private String typeName;

        private List<Property> properties;

        @Data
        public static class Property {
            private String properName;
            private String properType;
            private String analyzer;
            private String format;
        }
    }
}
