package com.hcmut.irms.menu_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class Customization {
    private String name;

    @JsonProperty("isRequired")
    private boolean required;

    private List<Option> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public static class Option {
        private String name;
        private BigDecimal extraPrice;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getExtraPrice() {
            return extraPrice;
        }

        public void setExtraPrice(BigDecimal extraPrice) {
            this.extraPrice = extraPrice;
        }
    }
}
