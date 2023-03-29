package com.qili.datamanager.vo;


import jakarta.validation.constraints.NotBlank;

/**
 * @Author: wuyong
 * @Description: Hbase Java 实体对象
 * @DateTime: 2023/3/38 19:00
 **/
public class HbaseTableInfo {


    /**
     * 集群名称
     */
    private String clusterName;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 表名
     */
    @NotBlank(message = "tableName is required")
    private String tableName;

    /**
     *列族名称
     */
    private String columnFamilyName;

    /**
     * 保存时间
     */
    private String saveTime;

    /**
     * 业务部门
     */
    private String businessDepart;

    /**
     * 管理员
     */
    private String manager;

    /**
     * 样本数据
     */
    private String sampleData;

    /**
     * 创建日期
     */
    private String createDate;


    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnFamilyName() {
        return columnFamilyName;
    }

    public void setColumnFamilyName(String columnFamilyName) {
        this.columnFamilyName = columnFamilyName;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getBusinessDepart() {
        return businessDepart;
    }

    public void setBusinessDepart(String businessDepart) {
        this.businessDepart = businessDepart;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getSampleData() {
        return sampleData;
    }

    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
