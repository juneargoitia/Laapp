package org.project.travelscrapper.travelscrapper.core;

public class DestinationInfo {
        private final String code;
        private final String type;

        public DestinationInfo(String code, String type) {
            this.code = code;
            this.type = type;
        }

        public String getCode() {
            return code;
        }

        public String getType() {
            return type;
        }
}
