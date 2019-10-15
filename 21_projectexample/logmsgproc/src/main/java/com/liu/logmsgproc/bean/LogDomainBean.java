package com.liu.logmsgproc.bean;

/**
 * 报文域定义.
 * @author liudongfei
 */
public class LogDomainBean {
    // parse sequence
    private int sequence;
    // domain id
    private int domainId;
    // domain name
    private String domainName;
    // domain data type
    private String dataType;
    // domain length type
    private boolean isFixedLength;
    // if domain is fixed length, it is content length,if not, it is the length of the domain length.
    private int length;
    // is or not need substr
    private boolean shouldSubstr;
    // 实际长度, 以编码转换以后的字符数表示.
    private int actualLength;
    /**
     * 以|为分隔符， 如ascii|hex, 目前只有2个元素A|B， 前面为为ascii或者bcd， 后面为hex或者dec
     * 如POS收单报文中， POSP规范报文配bcd|hex即可， 表示直接从字符转为hex， 但CUPS规范需要配ascii|hex， 表示从ascii形式转化成当前字符的过程中需要按十六进制编码， 如十六进制的30表示字符0
     * 如果是CUPS报文， 那么只要ascii就行了， 不需要十六进制转码.
     */
    private String contentFormat;
    // like contentFormat
    private String headerFormat;
    // domain is or not optional
    private boolean isOptional;
    // the domain of the expected string
    private String expectedString;
    // the domain of the expected string start with
    private boolean isExpectedStringStartWidth;

    public LogDomainBean() {
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getDomainId() {
        return domainId;
    }

    public void setDomainId(int domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isFixedLength() {
        return isFixedLength;
    }

    public void setFixedLength(boolean isFixedLength) {
        this.isFixedLength = isFixedLength;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isShouldSubstr() {
        return shouldSubstr;
    }

    public void setShouldSubstr(boolean shouldSubstr) {
        this.shouldSubstr = shouldSubstr;
    }

    public int getActualLength() {
        return actualLength;
    }

    public void setActualLength(int actualLength) {
        this.actualLength = actualLength;
    }

    public String getContentFormat() {
        return contentFormat;
    }

    public void setContentFormat(String contentFormat) {
        this.contentFormat = contentFormat;
    }

    public String getHeaderFormat() {
        return headerFormat;
    }

    public void setHeaderFormat(String headerFormat) {
        this.headerFormat = headerFormat;
    }


    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

    public String getExpectedString() {
        return expectedString;
    }

    public void setExpectedString(String expectedString) {
        this.expectedString = expectedString;
    }

    public boolean isExpectedStringStartWidth() {
        return isExpectedStringStartWidth;
    }

    public void setExpectedStringStartWidth(boolean isExpectedStringStartWidth) {
        this.isExpectedStringStartWidth = isExpectedStringStartWidth;
    }

    /**
     * constructor.
     * @param sequence sequence
     * @param domainId domainId
     * @param domainName domainName
     * @param dataType dataType
     * @param isFixedLength isFixedLength
     * @param length length
     * @param shouldSubstr shouldSubstr
     * @param actualLength actualLength
     * @param contentFormat contentFormat
     * @param headerFormat headerFormat
     * @param isOptional isOptional
     * @param expectedString expectedString
     * @param isExpectedStringStartWidth isExpectedStringStartWidth
     */
    public LogDomainBean(int sequence, int domainId, String domainName, String dataType, boolean isFixedLength,
                         int length, boolean shouldSubstr, int actualLength, String contentFormat, String headerFormat,
                         boolean isOptional, String expectedString, boolean isExpectedStringStartWidth) {
        super();
        this.sequence = sequence;
        this.domainId = domainId;
        this.domainName = domainName;
        this.dataType = dataType;
        this.isFixedLength = isFixedLength;
        this.length = length;
        this.shouldSubstr = shouldSubstr;
        this.actualLength = actualLength;
        this.contentFormat = contentFormat;
        this.headerFormat = headerFormat;
        this.isOptional = isOptional;
        this.expectedString = expectedString;
        this.isExpectedStringStartWidth = isExpectedStringStartWidth;
    }

    @Override
    public String toString() {
        return "LogDomainBean{"
                + "sequence=" + sequence
                + ", domainId=" + domainId
                + ", domainName='" + domainName + '\''
                + ", dataType='" + dataType + '\''
                + ", isFixedLength=" + isFixedLength
                + ", length=" + length
                + ", shouldSubstr=" + shouldSubstr
                + ", actualLength=" + actualLength
                + ", contentFormat='" + contentFormat + '\''
                + ", headerFormat='" + headerFormat + '\''
                + ", isOptional=" + isOptional
                + ", expectedString='" + expectedString + '\''
                + ", isExpectedStringStartWidth=" + isExpectedStringStartWidth
                + '}';
    }
}
