package com.qili.datamanager.utils;

import com.google.common.collect.Lists;
import com.qili.datamanager.vo.HbaseTableInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: wuyong
 * @Description: HbaseApi接口工具类
 * @DateTime: 2023/3/28 15:16
 **/
public class HBaseApiUtils {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // zk 主机
    public static String zkQuorum = "docker-hbase";
    // zk znode
    public static String zkZnode = "/hbase";
    // zk 端口
    public static String zkPort = "2181";

    // hbase 连接类
    public static Connection connection;
    // hbase 表管理类
    public static Admin admin;

    private static final List<String> COLUMN_FAMILY_NAMES = new ArrayList();

    /**
     * 初始获取Admin
     * @param zkQuorum
     * @param zkZnode
     * @param zkPort
     * @return
     */
    private static Admin init(String zkQuorum, String zkZnode, String zkPort) throws IOException {
        // hbase 配置类
        Configuration configuration = HBaseConfiguration.create();
        // zk 主机
        configuration.set("hbase.zookeeper.quorum", zkQuorum);
        // zk 节点
        configuration.set("zookeeper.znode.parent", zkZnode);
        // zk 端口
        configuration.set("hbase.zookeeper.property.clientPort", zkPort);
        connection = ConnectionFactory.createConnection(configuration);
        return admin = connection.getAdmin();

    }

    /**
     * 列出数据库中所有的表
     * @throws IOException
     */
    public static List<HbaseTableInfo> listTables() throws IOException {

        Admin admin = init(zkQuorum, zkZnode, zkPort);

        List<HbaseTableInfo> tableInfoList = Lists.newArrayList();
        for (TableName table : admin.listTableNames()) {
            ColumnFamilyDescriptor[] columnFamilies = admin.getDescriptor(TableName.valueOf(table.getName())).getColumnFamilies();
            for (ColumnFamilyDescriptor colFamily : columnFamilies) {
                System.out.println("-------");
            }

            HbaseTableInfo tableInfo = new HbaseTableInfo();
            tableInfo.setClusterName("华为服务器01");
            tableInfo.setSaveTime(new Date());
            tableInfo.setBusinessDepart("研发部");
            tableInfo.setManager("admin");
            tableInfo.setSampleData("暂无样本数据");
            tableInfo.setCreateDate(new Date());

            // 命名空间
            String namespace = Bytes.toString(table.getNamespace());
            // 表名
            // 列族
            byte[] qualifier = table.getQualifier();
            String columnFamilyName = Bytes.toString(qualifier);

            String tName = Bytes.toString(table.getName());
            tableInfo.setNamespace(namespace);
            tableInfo.setTableName(tName);
            tableInfo.setTableName(columnFamilyName);

            tableInfoList.add(tableInfo);

        }
        close();

        return tableInfoList;
    }

    /**
     * 创建表结构
     * @throws IOException
     */
    public static boolean createSchemaTables(HbaseTableInfo hbaseTableInfo) throws IOException {
        Admin admin = init(zkQuorum, zkZnode, zkPort);

        //校验表结构是否存在
        checkTableExist(hbaseTableInfo.getTableName());

        // 转换对象为HBASE
        TableName tableName = TableName.valueOf(hbaseTableInfo.getTableName());
        // 创建集合用于存放ColumnFamilyDescriptor对象
        List<ColumnFamilyDescriptor> colFamilyList = new ArrayList<ColumnFamilyDescriptor>();

        // 将每个familyName对应的ColumnFamilyDescriptor对象添加到colFamilyList集合中保存
        TableDescriptorBuilder tableDesBuilder = TableDescriptorBuilder.newBuilder(tableName);
        if (hbaseTableInfo.getColumnFamilyName() != null) {
            COLUMN_FAMILY_NAMES.add(hbaseTableInfo.getColumnFamilyName());
        } else {
            // 默认给一个列族名称
            String familyName = "cf";
            COLUMN_FAMILY_NAMES.add(familyName);
        }
        for (String familyName : COLUMN_FAMILY_NAMES) {
            ColumnFamilyDescriptor colFamilyDes = ColumnFamilyDescriptorBuilder.newBuilder(familyName.getBytes()).build();
            colFamilyList.add(colFamilyDes);
        }

        // 构建TableDescriptor对象，以保存tableName与familyNames
        TableDescriptor tableDes = tableDesBuilder.setColumnFamilies(colFamilyList).build();

        // 有了表描述器便可以创建表了
        admin.createTable(tableDes);
        System.out.println("=============== create " + tableName + " table success ===============");

        // 关闭连接
        close();
        return true;
    }


    /**
     * 检查表是否存在
     * @param tableName
     * @throws IOException
     */
    public static void checkTableExist(String tableName) throws IOException {
        Admin admin = init(zkQuorum, zkZnode, zkPort);
        TableName tName = TableName.valueOf(tableName);
        Assert.isTrue(!admin.tableExists(tName), "当前:" + tableName + "已存在,请勿重复添加!");
    }


    /**
     * 关闭连接
     */
    public static void close() {
        try {
            if (admin != null) {
                admin.close();
            }
            if (connection != null) {
                connection.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
