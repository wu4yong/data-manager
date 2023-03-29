package com.qili.datamanager;


import com.qili.datamanager.utils.HBaseApiUtils;
import com.qili.datamanager.vo.HbaseTableInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wuyong
 * @Description: Hbase Java Api Operation Test
 * @See https://hbase.apache.org/book.html#_examples Example 30
 * @DateTime: 2023/2/4 19:00
 **/
@SpringBootTest
class DataManagerApplicationTests {

    // zk 主机
    public static String ZK_QUORUM = "docker-hbase";
    // zk znode
    public static String ZK_ZNODE = "/hbase";
    // zk 端口
    public static String ZK_PORT = "2181";

    // hbase 配置类
    public static Configuration configuration;
    // hbase 连接类
    public static Connection connection;
    // hbase 表管理类
    public static Admin admin;
    // hbase 表名
    private static final String TABLE_NAME = "student";
    // hbase 列族
    private static final String[] COLUMN_FAMILY_NAMES = {"course"};

    /**
     * 初始化连接 hbase-site.xml
     */
    @Before("")
    public void init() throws IOException {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", ZK_QUORUM);
        configuration.set("zookeeper.znode.parent", ZK_ZNODE);
        configuration.set("hbase.zookeeper.property.clientPort", ZK_PORT);
        connection = ConnectionFactory.createConnection(configuration);
        admin = connection.getAdmin();

    }

    /**
     * 关闭连接
     */
    @After("")
    public void close() {
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

    /**
     * 检查表是否存在
     * @throws IOException
     */
    @Test
    public void CheckTableExist() throws IOException {
        init();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        if (admin.tableExists(tableName)) {
            System.out.println(TABLE_NAME + " is exist");
        } else {
            System.out.println("table is not exist");
        }
        close();
    }

    /**
     * 列出数据库中所有的表
     * @throws IOException
     */
    @Test
    public void listTables() throws IOException {
        init();
        for (TableName table : admin.listTableNames()) {
            System.out.println(table);
        }
        close();
    }

    /**
     * 测试创建表结构
     */
    @Test
    public void createSchemaTables() throws IOException {
        init();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        if (admin.tableExists(tableName)) {
            System.out.println(tableName + " is exist");
            // 失效删除表
            // admin.disableTable(tableName);
            // admin.deleteTable(tableName);
        } else {
            System.out.println("create table " + tableName);

            /**
             * 构建表描述
             * 2.0 HTableDescriptor + HColumnDescriptor
             * 3.0 开始使用TableDescriptorBuilder + ColumnFamilyDescriptorBuilder，
             */
            // 创建集合用于存放ColumnFamilyDescriptor对象
            List<ColumnFamilyDescriptor> colFamilyList = new ArrayList<ColumnFamilyDescriptor>();

            // 将每个familyName对应的ColumnFamilyDescriptor对象添加到colFamilyList集合中保存
            TableDescriptorBuilder tableDesBuilder = TableDescriptorBuilder.newBuilder(tableName);
            for (String familyName : COLUMN_FAMILY_NAMES) {
                ColumnFamilyDescriptor colFamilyDes = ColumnFamilyDescriptorBuilder.newBuilder(familyName.getBytes()).build();
                colFamilyList.add(colFamilyDes);
            }

            // 构建TableDescriptor对象，以保存tableName与familyNames
            TableDescriptor tableDes = tableDesBuilder.setColumnFamilies(colFamilyList).build();

            // 有了表描述器便可以创建表了
            admin.createTable(tableDes);
            System.out.println("=============== create " + tableName + " table success ===============");
        }
        close();
    }

    /**
     * 测试删除表
     */
    @Test
    public void deleteTable() throws IOException {
        init();
        TableName tableName = TableName.valueOf("test");
        if (admin.tableExists(tableName)) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            System.out.println("delete " + TABLE_NAME + " successful!");
        }
        close();
    }

    /**
     * 向表中添加一个列族
     * @throws IOException
     */
    @Test
    public void addColumnFamily() throws IOException {
        String colFamily = "address";
        init();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        if (admin.tableExists(tableName)) {
            TableDescriptor tableDes = TableDescriptorBuilder.newBuilder(tableName).build();
            ColumnFamilyDescriptor colFamilyDes = ColumnFamilyDescriptorBuilder.newBuilder(colFamily.getBytes()).build();
            admin.addColumnFamily(tableName, colFamilyDes);
            System.out.println("add " + colFamily + " successful!");
        }
        close();
    }

    /**
     * 从表中移除一个列族
     * @throws IOException
     */
    @Test
    public void removeColumnFamily() throws IOException {
        String colFamily = "address";
        init();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        if (admin.tableExists(tableName)) {
            TableDescriptor tableDes = TableDescriptorBuilder.newBuilder(tableName).build();
            admin.deleteColumnFamily(tableName, colFamily.getBytes());
            System.out.println("remove " + colFamily + " successful!");
        }
        close();
    }

    /**
     * 描述表的详细信息
     * @throws IOException
     */
    @Test
    public void describeTable() throws IOException {
        init();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        if (admin.tableExists(tableName)) {
            ColumnFamilyDescriptor[] colFamilies = admin.getDescriptor(tableName).getColumnFamilies();
            System.out.println("==============describe  " + TABLE_NAME + " ================");
            for (ColumnFamilyDescriptor colFamily : colFamilies) {
                System.out.println(colFamily.getNameAsString());
                System.out.println(colFamily.getBlocksize());
                System.out.println(colFamily.getConfigurationValue(TABLE_NAME));
                System.out.println(colFamily.getMaxVersions());
                System.out.println(colFamily.getEncryptionType());
                System.out.println(colFamily.getTimeToLive());
                System.out.println(colFamily.getDFSReplication());
                System.out.println();
            }
        }
        close();
    }

    /**
     * 测试插入数据
     */
    @Test
    public void insert() throws Exception {
        init();
        //  通过连接查询table对象
        Connection connection = admin.getConnection();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        Table table = connection.getTable(tableName);
        // 通过bytes类创建字节数组
        byte[] rowid = Bytes.toBytes("r1");
        //  创建put对象
        Put put = new Put(rowid);
        put.addColumn(Bytes.toBytes("course"), Bytes.toBytes("chinese"), Bytes.toBytes("90"));
        put.addColumn(Bytes.toBytes("course"), Bytes.toBytes("english"), Bytes.toBytes("90"));
        table.put(put);
        System.out.println("insert " + TABLE_NAME + ": data successful!");
        close();
    }

    /**
     * 删除数据
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        init();
        //  通过连接查询table对象
        Connection connection = admin.getConnection();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        Table table = connection.getTable(tableName);

        //删除数据
        Delete delete = new Delete(Bytes.toBytes("r3"));
        delete.addColumn(Bytes.toBytes("course"), Bytes.toBytes("english"));
        table.delete(delete);
        System.out.println("delete " + TABLE_NAME + ": data successful!");
        close();
    }

    /**
     * 获取全部数据
     * @throws Exception
     */
    @Test
    public void scan() throws Exception {
        init();
        //  通过连接查询table对象
        Connection connection = admin.getConnection();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        Table table = connection.getTable(tableName);

        Scan scan = new Scan();
        // scan.setStartRow(startRow);
        // scan.setStopRow(stopRow);
        ResultScanner rss = table.getScanner(scan);
        for (Result row : rss) {
            for (Cell cell : row.listCells()) {
                System.out.println("RowKey: " + Bytes.toString(row.getRow()) +
                        " Family: " + Bytes.toString(CellUtil.cloneFamily(cell)) +
                        " Qualifier: " + Bytes.toString(CellUtil.cloneQualifier(cell)) +
                        " Value: " + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
        close();

    }

    /**
     * 获取指定记录数据
     *  多版本命令：
     *      修改表结构：alter 'student',{NAME=>'course',VERSIONS=>3}
     *      获取数据：  get 'student','r3',{COLUMN=>'course:chinese',VERSIONS=>3}
     *
     * @throws Exception
     */
    @Test
    public void getRows() throws Exception {
        init();
        //  通过连接查询table对象
        Connection connection = admin.getConnection();
        TableName tableName = TableName.valueOf(TABLE_NAME);
        Table table = connection.getTable(tableName);
        Get get = new Get(Bytes.toBytes("r1"));

        // 添加要获取的列和列族，减少网络的io，相当于在服务器端做了过滤
        // get.addColumn("course".getBytes(), "chinese".getBytes());
        // get.addColumn("course".getBytes(), "english".getBytes());

        Result result = table.get(get);
        Cell cell1 = result.getColumnLatestCell("course".getBytes(), "chinese".getBytes());
        Cell cell2 = result.getColumnLatestCell("course".getBytes(), "english".getBytes());
        System.out.println(Bytes.toString(CellUtil.cloneValue(cell1)));
        System.out.println(Bytes.toString(CellUtil.cloneValue(cell2)));

        close();
    }

    /**
     * 测试创建表
     * @throws IOException
     */
    @Test
    public  void  testUtil() throws IOException {
        HbaseTableInfo tableInfo = new HbaseTableInfo();
        tableInfo.setTableName("test2");
        HBaseApiUtils.createSchemaTables(tableInfo);
        System.out.println("Table created");
    }

}
