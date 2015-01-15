package com.shadow.resource;

/**
 * @author nevermore on 2015/1/15
 */
public class ResourceConfiguration {
    private String resourceLocation;
    private String fileSuffix;
    private String titleTag;
    private String ignoreTag;
    private String endTag;

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public String getTitleTag() {
        return titleTag;
    }

    public void setTitleTag(String titleTag) {
        this.titleTag = titleTag;
    }

    public void setIgnoreTag(String ignoreTag) {
        this.ignoreTag = ignoreTag;
    }

    public String getIgnoreTag() {
        return ignoreTag;
    }

    public void setEndTag(String endTag) {
        this.endTag = endTag;
    }

    public String getEndTag() {
        return endTag;
    }
}
